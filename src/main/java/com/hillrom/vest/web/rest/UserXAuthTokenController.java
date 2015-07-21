package com.hillrom.vest.web.rest;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.service.AuthenticationService;

@RestController
@RequestMapping("/api")
public class UserXAuthTokenController {

    @Inject
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/authenticate",
            method = RequestMethod.POST)
    @Timed
    public UserLoginToken authorize(@RequestBody(required=true) Map<String,String> credentialsMap) {
    	String username = credentialsMap.get("username");
    	String password = credentialsMap.get("password"); 
    	return authenticationService.authenticate(username, password);
    }

    @RequestMapping(value = "/logout",
            method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestHeader(value="x-auth-token",required=true)String authToken){
    	authenticationService.logout(authToken);
    	return ResponseEntity.ok().build();
    }
}
