package com.hillrom.vest.security.xauth;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hillrom.vest.service.UserLoginTokenService;

public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private UserDetailsService detailsService;
    
    private UserLoginTokenService authTokenService;

    public XAuthTokenConfigurer(UserDetailsService detailsService, UserLoginTokenService authTokenService) {
        this.detailsService = detailsService;
        this.authTokenService = authTokenService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        XAuthTokenFilter customFilter = new XAuthTokenFilter(detailsService, authTokenService);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
