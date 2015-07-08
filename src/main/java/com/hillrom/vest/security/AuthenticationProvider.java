package com.hillrom.vest.security;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.hillrom.vest.domain.Authority;
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

    public AuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,PatientInfoService patientInfoService,UserService userService) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.patientInfoService = patientInfoService;
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
    		
    		/*If the User already have a record in User table and 
    		tries to login with JDE_ID/HillromId second time, we should throw an Excpetion*/ 
    		if(patientInfo.getWebLoginCreated()){
    			throw new BadCredentialsException("Invalid Username, please use your registered email");
    		}
    		
    		String defaultPassword = generateDefaultPassword(patientInfo);
    		
    		matchWithDefaultPassword(token.getCredentials().toString(), defaultPassword);
    		
    		//If email doesn't exist, send response to register with email and password
    		if(null == patientInfo.getEmail()){
    			
    			User newUser = userService.createUserFromPatientInfo(patientInfo,passwordEncoder.encode(defaultPassword));
    			JSONObject jsonObject = prepareJSONForPatientUser(login,defaultPassword,newUser.getId(),newUser.getAuthorities());
    			throw new EmailNotPresentForPatientException("Please Register with Email and Password to Login",jsonObject);
    		}
    		
    		Optional<User> userFromDatabase = userService.findOneByEmail(patientInfo.getEmail().toLowerCase());
    		
    		if(userFromDatabase.isPresent()){
    			User existingPatientuser = userFromDatabase.get();
    			/* User exists and it is the first time login,
    			 * else block is not required since WebLogginCreated will be true for subsequent logins which has been checked in prior condition*/ 
    			if(null == existingPatientuser.getLastLoggedInAt()){
    				JSONObject jsonObject = prepareJSONForPatientUser(login,defaultPassword,existingPatientuser.getId(),existingPatientuser.getAuthorities());
        	        throw new FirstLoginException("First Time Login, please reset your password",jsonObject);
    			}
    		}else{
    			User newUser = userService.createUserFromPatientInfo(patientInfo,passwordEncoder.encode(defaultPassword));
    			JSONObject jsonObject = prepareJSONForPatientUser(login,defaultPassword,newUser.getId(),newUser.getAuthorities());
    			throw new FirstLoginException("Please Register with Email and Password to Login",jsonObject);
    		}
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
	
	private void matchWithDefaultPassword(String password, String tokenPassword){
		if(!password.equals(tokenPassword)){
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
		// default password will have the first 4 letters from last name, if length of last name <= 4, use complete string
		int endIndex = patientUser.getLastName().length() > 5 ? 5 : patientUser.getLastName().length() ; 
		defaultPassword.append(patientUser.getLastName().substring(0, endIndex));
		defaultPassword.append(patientUser.getDob().toString("MMddyyyy"));
		return defaultPassword.toString();
	}

	private JSONObject prepareJSONForPatientUser(String username,String password,Long userId,Set<Authority> authorities){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", username);
		jsonObject.put("password", password);
		jsonObject.put("userId", userId);
		jsonObject.put("authorities", authorities);
		return jsonObject;
	}
	
	@Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken
                .class.equals(authentication);
    }
}