package com.hillrom.vest.security;

import java.util.Optional;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    private static final int NO_OF_CHARACTERS_TO_BE_EXTRACTED = 4;

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
        log.debug("login : ",login);
        
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
    		String encodedPassword = passwordEncoder.encode(defaultPassword);
    		
    		matchWithDefaultPassword(token.getCredentials().toString(), defaultPassword);
    		
    		//If email doesn't exist, send response to register with email and password
    		if(null == patientInfo.getEmail()){
    			
    			userService.createUserFromPatientInfo(patientInfo,encodedPassword);
    			JSONObject jsonObject = prepareJSONForPatientUser(login,encodedPassword);
    			throw new EmailNotPresentForPatientException("Please Register with Email and Password to Login",jsonObject);
    		}
    		
    		Optional<User> userFromDatabase = userService.findOneByEmail(patientInfo.getEmail().toLowerCase());
    		
    		if(userFromDatabase.isPresent()){
    			User existingPatientuser = userFromDatabase.get();
    			// User exists and it is the first time login 
    			if(null == existingPatientuser.getLastLoggedInAt()){
    				JSONObject jsonObject = prepareJSONForPatientUser(patientInfo.getEmail(),defaultPassword);
        	        throw new FirstLoginException("First Time Login, please reset your password",jsonObject);
    			}
    		}else{
    			userService.createUserFromPatientInfo(patientInfo,encodedPassword);
    			JSONObject jsonObject = prepareJSONForPatientUser(login,encodedPassword);
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
    
	 /**Default Password for PatientUser is zipcode+1st 4 characters in last_name+dob in MMddyyy format
	  *    
	  * @param patientUser
	  * @return default password for the PatientUser
	  */
	private String generateDefaultPassword(PatientInfo patientUser) {
		StringBuilder defaultPassword = new StringBuilder();
		defaultPassword.append(patientUser.getZipcode());
		// default password will have the first 4 letters from last name, if length of last name <= 4, use complete string
		int endIndex = patientUser.getLastName().length() > NO_OF_CHARACTERS_TO_BE_EXTRACTED ? NO_OF_CHARACTERS_TO_BE_EXTRACTED : patientUser.getLastName().length() ; 
		defaultPassword.append(patientUser.getLastName().substring(0, endIndex));
		defaultPassword.append(patientUser.getDob().toString("MMddyyyy"));
		return defaultPassword.toString();
	}

	private JSONObject prepareJSONForPatientUser(String username,String encodedPassword){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", username);
		jsonObject.put("password", encodedPassword);
		return jsonObject;
	}
	
	@Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken
                .class.equals(authentication);
    }
}