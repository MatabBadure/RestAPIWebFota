package com.hillrom.vest.security.xauth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.hillrom.vest.service.UserLoginTokenService;

/**
 * Filters incoming requests and installs a Spring Security principal if a
 * header corresponding to a valid user is found.
 */
public class XAuthTokenFilter extends GenericFilterBean {

	private final static String XAUTH_TOKEN_HEADER_NAME = "x-auth-token";

	private UserDetailsService detailsService;

	private UserLoginTokenService authTokenService;

	public XAuthTokenFilter(UserDetailsService detailsService,
			 UserLoginTokenService authTokenService) {
		this.detailsService = detailsService;
		this.authTokenService = authTokenService;
	}

	@Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String authToken = httpServletRequest.getHeader(XAUTH_TOKEN_HEADER_NAME);
            if (StringUtils.hasText(authToken)) {
                String username = authToken != null && authToken.contains("#")? authToken.split("#")[0]: null;
                if(null!= username){
                	UserDetails details = this.detailsService.loadUserByUsername(username);
                	if(authTokenService.validateToken(authToken)){
                		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
                		SecurityContextHolder.getContext().setAuthentication(token);
                	}                	
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
