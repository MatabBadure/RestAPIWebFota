package com.hillrom.vest.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class RestExceptionTranslationFilter extends ExceptionTranslationFilter {

	private static final String PASSWORD_RESET = "PASSWORD_RESET";
	private static final String EMAIL_PASSWORD_RESET = "EMAIL_PASSWORD_RESET";
	private static final String EMAIL_IS_NOT_REGISTERED = "Email is not registered, please provide email and password to continue";
	private static final String FIRST_LOGIN = "User Logged in first time, please reset password to continue";
	private static final String ERROR = "Error";
	private static final String APP_CODE = "APP_CODE";
	private static final String APPLICATION_JSON = "application/json";

	private JSONObject jsonObject= null;
	
	public RestExceptionTranslationFilter(
			AuthenticationEntryPoint authenticationEntryPoint) {
		super(authenticationEntryPoint);
	}

	@Override
	protected void sendStartAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			AuthenticationException reason) throws ServletException,
			IOException {
		
		response.setContentType(APPLICATION_JSON);
        response.setStatus(401);
        PrintWriter out = response.getWriter();
        
		if(reason instanceof FirstLoginException){
			FirstLoginException firstLoginException = (FirstLoginException) reason;
			
			jsonObject = firstLoginException.getJsonObject();
			jsonObject.put(ERROR, FIRST_LOGIN);
			jsonObject.put(APP_CODE, PASSWORD_RESET);
            
			response.setStatus(100);    
		}else if(reason instanceof EmailNotPresentForPatientException){
			EmailNotPresentForPatientException firstLoginException = (EmailNotPresentForPatientException) reason;
			
			jsonObject = firstLoginException.getJsonObject();
			jsonObject.put(ERROR, EMAIL_IS_NOT_REGISTERED);
			jsonObject.put(APP_CODE, EMAIL_PASSWORD_RESET);
         
            response.setStatus(100);
		}else{
			jsonObject = new JSONObject();
			jsonObject.put(ERROR,reason.getMessage());
		}
		
		out.print(jsonObject);
        out.flush();
        out.close();
	}

	
}
