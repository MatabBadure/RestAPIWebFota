package com.hillrom.vest.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.security.xauth.Token;
import com.hillrom.vest.security.xauth.TokenProvider;

public class RestExceptionTranslationFilter extends ExceptionTranslationFilter {
	
	@Inject
    private TokenProvider tokenProvider;

	private static final String PASSWORD_RESET = "PASSWORD_RESET";
	private static final String EMAIL_PASSWORD_RESET = "EMAIL_PASSWORD_RESET";
	private static final String EMAIL_IS_NOT_REGISTERED = "Email is not registered, please provide email and password to continue";
	private static final String FIRST_LOGIN = "User Logged in first time, please reset password to continue";
	private static final String ERROR = "Error";
	private static final String APP_CODE = "APP_CODE";
	private static final String APPLICATION_JSON = "application/json";

	private JSONObject jsonObject= null;
	
	private UserDetails userDetails = null;
	
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
			
			userDetails = prepareUserDetailsObjectFromJson(jsonObject);
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);
			
			UserLoginToken token = tokenProvider.createToken(userDetails);
			
			jsonObject.put("token", token.getId());
			
		}else if(reason instanceof EmailNotPresentForPatientException){
			EmailNotPresentForPatientException firstLoginException = (EmailNotPresentForPatientException) reason;
			
			jsonObject = firstLoginException.getJsonObject();
			jsonObject.put(ERROR, EMAIL_IS_NOT_REGISTERED);
			jsonObject.put(APP_CODE, EMAIL_PASSWORD_RESET);
         
			userDetails = prepareUserDetailsObjectFromJson(jsonObject);
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);
			
			UserLoginToken token = tokenProvider.createToken(userDetails);
			
			jsonObject.put("token", token.getId());
            
		}else{
			jsonObject = new JSONObject();
			jsonObject.put(ERROR,reason.getMessage());
		}
		
		// These should not be passed in response
		jsonObject.remove("username");
		jsonObject.remove("password");
		
		out.print(jsonObject);
        out.flush();
        out.close();
        return;
	}

	private UserDetails prepareUserDetailsObjectFromJson(JSONObject jsonObject){
		String username = (String) jsonObject.get("username");
		String password = (String) jsonObject.get("password");
		
		List<GrantedAuthority> grantedAuthorities = new LinkedList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.PATIENT));
		
		return new org.springframework.security.core.userdetails.User(username.toLowerCase(),
				password,
				grantedAuthorities);
	}
}
