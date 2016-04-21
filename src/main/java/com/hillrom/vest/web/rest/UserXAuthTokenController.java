package com.hillrom.vest.web.rest;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.AuthenticationService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;

@RestController
@RequestMapping("/api")
public class UserXAuthTokenController {

    @Inject
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/authenticate",
            method = RequestMethod.POST)
    
    public UserLoginToken authorize(HttpServletRequest request,@RequestBody(required=true) Map<String,String> credentialsMap) {
    	String username = credentialsMap.get("username");
    	String password = credentialsMap.get("password"); 
    	return authenticationService.authenticate(username, password,request.getRemoteAddr());
    }

    @RequestMapping(value = "/logout",
            method = RequestMethod.POST)
    public ResponseEntity<?> logout(@RequestHeader(value="x-auth-token",required=true)String authToken){
    	authenticationService.logout(authToken);
    	return ResponseEntity.ok().build();
    }
    
    @RequestMapping(value = "/validateCredentials",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)    
    public ResponseEntity<JSONObject> validatePassword(@RequestBody(required=true) Map<String,String> params){
    	JSONObject jsonObject = new JSONObject();
    	String password = params.get("password");
    	boolean isValid = false;
    	String message = ExceptionConstants.HR_716;
    	HttpStatus statusCode = HttpStatus.UNAUTHORIZED;
    	try {
			isValid = authenticationService.validatePassword(password);
			if(isValid){
				statusCode = HttpStatus.OK;
				message = MessageConstants.HR_306;
			}
		} catch (HillromException e) {				
			message = e.getMessage();
			statusCode = HttpStatus.BAD_REQUEST;
		}
    	jsonObject.put("message", message);
    	jsonObject.put("isValid", isValid);
    	return ResponseEntity.status(statusCode).body(jsonObject);

    }

}
