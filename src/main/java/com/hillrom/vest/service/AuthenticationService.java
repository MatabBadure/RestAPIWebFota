package com.hillrom.vest.service;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.security.UserNotActivatedException;
import com.hillrom.vest.security.xauth.TokenProvider;
import com.hillrom.vest.util.ExceptionConstants;

@Service
@Transactional
public class AuthenticationService {

	@Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private UserDetailsService userDetailsService;
    
    @Inject
    private UserLoginTokenService authTokenService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private PasswordEncoder passwordEncoder;
    
    public UserLoginToken authenticate(String username,String password){
    	if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
    		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
    		Authentication authentication = this.authenticationManager.authenticate(token);
    		SecurityContextHolder.getContext().setAuthentication(authentication);
    		UserDetails details = this.userDetailsService.loadUserByUsername(username);
    		return tokenProvider.createToken(details);    		
    	}else{
    		throw new BadCredentialsException("Please provide Username and Password");
    	}
    }
    
    public void logout(String authToken){
    	authTokenService.deleteToken(authToken);
		SecurityContextHolder.getContext().setAuthentication(null);
    }

    /**
     * validates password of current logged-in user
     * @param password
     * @return
     * @throws HillromException
     */
    public boolean validatePassword(String password) throws HillromException{
    	boolean isValid = false;
    	String login = SecurityUtils.getCurrentLogin();
    	if(Objects.isNull(password))
    		throw new HillromException(ExceptionConstants.HR_714);
    	
		Optional<User> userFromDatabase = userService.findOneByEmailOrHillromId(login);
        User user = null;
        if(userFromDatabase.isPresent()){
        	user = userFromDatabase.get();
        }else{
        	throw new HillromException(ExceptionConstants.HR_704);
        }
        if(passwordEncoder.matches(password, user.getPassword()))
        	isValid = true;
        
    	return isValid;

    }
    
}
