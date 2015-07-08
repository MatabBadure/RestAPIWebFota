package com.hillrom.vest.web.rest;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.security.xauth.Token;
import com.hillrom.vest.security.xauth.TokenProvider;

@RestController
@RequestMapping("/api")
public class UserXAuthTokenController {

    @Inject
    private TokenProvider tokenProvider;

    @Inject
    private AuthenticationManager authenticationManager;

    @Inject
    private UserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate",
            method = RequestMethod.POST)
    @Timed
    public UserLoginToken authorize(@RequestBody(required=true) Map<String,String> credentialsMap) {
    	
    	String username = credentialsMap.get("username");
    	String password = credentialsMap.get("password"); 
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
}
