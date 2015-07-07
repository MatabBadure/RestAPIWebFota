package com.hillrom.vest.security;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.PatientInfoService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.service.util.RandomUtil;

public class AuthenticationProvider extends DaoAuthenticationProvider {

    private final Logger log = LoggerFactory.getLogger(AuthenticationProvider.class);

    private PasswordEncoder passwordEncoder;

    @Inject
    private UserDetailsService userDetailsService;
    
    @Inject
    private PatientInfoService patientInfoService;
    
    @Inject
    private UserService userService;

    public AuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Autowired
    public void setPatientInfoService(PatientInfoService patientInfoService) {
		this.patientInfoService = patientInfoService;
	}
    
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
    
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token =
                (UsernamePasswordAuthenticationToken) authentication;

        String login = token.getName();
        
        return RandomUtil.isValidEmail(login)? loginWithEmail(token, login) : loginWithHillromId(token, login);
    }

	private Authentication loginWithEmail(
			UsernamePasswordAuthenticationToken token, String login) {
			login = login.toLowerCase();
			UserDetails user = userDetailsService.loadUserByUsername(login);
	        if (user == null) {
	            throw new UsernameNotFoundException("User does not exists");
	        }
	        String password = user.getPassword();
	        String tokenPassword = (String) token.getCredentials();
	        matchPasswords(password, tokenPassword);
	        return new UsernamePasswordAuthenticationToken(user, password,
	            user.getAuthorities());
	}
	
    private Authentication loginWithHillromId(UsernamePasswordAuthenticationToken token,String login){
    	PatientInfo patientInfo = null;
    	if(RandomUtil.isValidHillromId(login)){
    		Optional<PatientInfo> patientInfoFromDatabase = patientInfoService.findOneByHillromId(login);
    		
    		if(patientInfoFromDatabase.isPresent()){
    			patientInfo = patientInfoFromDatabase.get();
    		}else{
    			throw new BadCredentialsException("User "+login+" was not found");
    		}
    		
    		String defaultPassword = generateDefaultPassword(patientInfo);
    		
    		/*If the User already have a record in User table and 
    		tries to login with JDE_ID/HillromId second time, we should throw an Excpetion*/ 
    		if(patientInfo.getWebLoginCreated()){
    			throw new BadCredentialsException("Invalid Username, please use your registered email");
    		}
    		
    		//If email doesn't exist, send response to register with email and password
    		if(null == patientInfo.getEmail()){
    			matchPasswords(token.getCredentials().toString(), defaultPassword);
    			User newUser = userService.createUserFromPatientInfo(patientInfo);
    			JSONObject jsonObject = prepareJSONForPatientUser(login,defaultPassword,newUser.getId());
    			throw new EmailNotPresentForPatientException("Please Register with Email and Password to Login",jsonObject);
    		}
    		
    		Optional<User> userFromDatabase = userService.findOneByEmail(patientInfo.getEmail().toLowerCase());
    		userFromDatabase.map(user -> {
    			
    			/* User exists and it is the first time login,
    			 * else block is not required since WebLogginCreated will be true for subsequent logins which has been checked in prior condition*/ 
    			if(null == user.getLastLoggedInAt()){
    				user.setPassword(defaultPassword);
    			}
    			
    			matchPasswords(token.getCredentials().toString(), defaultPassword);
    	        JSONObject jsonObject = prepareJSONForPatientUser(login,defaultPassword,user.getId());
    	        throw new FirstLoginException("First Time Login, please reset your password",jsonObject);
            }).orElse(null);
    	}else{
    		throw new BadCredentialsException("Invalid username/password");
    	}
    	return null;
    }

	private void matchPasswords(String password, String tokenPassword) {
		if (!passwordEncoder.matches(tokenPassword, password)) {
		    throw new BadCredentialsException("Invalid username/password");
		}
	}
    
	private org.springframework.security.core.userdetails.User buildUserWithAuthorities(
			User user) {
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
		        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
		        .collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(user.getEmail().toLowerCase(),
		        user.getPassword(),
		        grantedAuthorities);
	}
    
    
	private String generateDefaultPassword(PatientInfo patientUser) {
		StringBuilder defaultPassword = new StringBuilder();
		defaultPassword.append(patientUser.getZipcode());
		defaultPassword.append(patientUser.getLastName().substring(0, 5));
		defaultPassword.append(new SimpleDateFormat("MMDDYYYY").format(patientUser.getDob()));
		return defaultPassword.toString();
	}

	private JSONObject prepareJSONForPatientUser(String username,String password,Long userId){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", username);
		jsonObject.put("password", password);
		jsonObject.put("userId", userId);
		return jsonObject;
	}
	
	@Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken
                .class.equals(authentication);
    }
}