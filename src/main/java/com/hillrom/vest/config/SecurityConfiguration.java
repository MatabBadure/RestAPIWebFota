package com.hillrom.vest.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.Http401UnauthorizedEntryPoint;
import com.hillrom.vest.security.RestExceptionTranslationFilter;
import com.hillrom.vest.security.xauth.XAuthTokenConfigurer;
import com.hillrom.vest.service.UserLoginTokenService;
import com.hillrom.vest.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Inject
    private Http401UnauthorizedEntryPoint authenticationEntryPoint;

    @Inject
    private UserDetailsService userDetailsService;

    @Inject
    private UserLoginTokenService authTokenService;
    

    @Inject
    private UserService userService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AuthenticationProvider getAuthenticationProvider(){
    	return new com.hillrom.vest.security.AuthenticationProvider(passwordEncoder(),userService);
    }
    
    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(getAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers("/scripts/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/assets/**")
            .antMatchers("/test/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)
        .and()
            .csrf()
            .disable()
            .headers()
            .frameOptions()
            .disable()
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/account/reset_password/init").permitAll()
            .antMatchers("/api/account/reset_password/finish").permitAll()
            .antMatchers("/api/recaptcha").permitAll()
            .antMatchers("/api/securityQuestions").permitAll()
            .antMatchers("/api/account/update_passwordsecurityquestion").permitAll()
            .antMatchers("/api/receiveData").permitAll()
            .antMatchers("/api/patient/{id}/vestdevicedata").permitAll()
            .antMatchers("/api/vestdevicedata").permitAll()
            .antMatchers("/api/validateActivationKey").permitAll()
            .antMatchers("/api/validateResetKey").permitAll()
            .antMatchers("/api/users/{id}/exportVestDeviceData").authenticated()
            .antMatchers("/api/users/{id}/exportVestDeviceDataCSV").authenticated()
            .antMatchers("/api/users/{id}/exportTherapyData").authenticated()
            .antMatchers("/api/users/{id}/exportTherapyDataCSV").authenticated()
            .antMatchers("/api/users/{id}/therapyData").authenticated()
            .antMatchers("/api/logs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/**").authenticated()
            .antMatchers("/api/account/**").authenticated()
            .antMatchers("/api/notes/**").authenticated()
            .antMatchers("/api/users/{id}/notes/**").authenticated()
            .antMatchers("/api/patients/{id}/notes/**").authenticated()
            .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/health/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/configprops/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api-docs/**").hasAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/clinics/**").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES)
            .antMatchers("/api/hillromteamuser/**").hasAnyAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/protected/**").authenticated()
            .antMatchers("/api/user/{id}/changeSecurityQuestion").authenticated()
            .antMatchers("/api/user/**").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES)
            .antMatchers("/api/patient/**").hasAnyAuthority(AuthoritiesConstants.ADMIN)
            .antMatchers("/api/cityStateZipValuesByCity").authenticated()
            .antMatchers("/api/cityStateZipValuesBystate").authenticated()
            .antMatchers("/api/cityStateZipValuesByZipCode").authenticated()
            .antMatchers("/api/survey/**").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT)
            .antMatchers("/api/validateCredentials").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES)
            .antMatchers("/api/loginAnalytics").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES,AuthoritiesConstants.ASSOCIATES)
        .and()
            .apply(securityConfigurerAdapter());

    }

    private XAuthTokenConfigurer securityConfigurerAdapter() {
      return new XAuthTokenConfigurer(userDetailsService, authTokenService);
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
    
    @Bean
    public ExceptionTranslationFilter exceptionTranslationFilter() {
	    RestExceptionTranslationFilter exceptionTranslationFilter = new  RestExceptionTranslationFilter(authenticationEntryPoint);
	    exceptionTranslationFilter.afterPropertiesSet();
	    return exceptionTranslationFilter;
    }
}
