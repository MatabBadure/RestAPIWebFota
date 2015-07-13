package com.hillrom.vest.config;

import javax.inject.Inject;

import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.hillrom.vest.repository.UserLoginTokenRepository;
import com.hillrom.vest.security.xauth.TokenProvider;
import com.hillrom.vest.service.PatientInfoService;
import com.hillrom.vest.service.UserService;

/**
* Configures x-auth-token security.
*/
@Configuration
public class XAuthConfiguration implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;
    @Inject
    private UserService userService;
    @Inject
    private PatientInfoService patientInfoService;
    @Inject
    private UserLoginTokenRepository userLoginTokenRepository;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "authentication.xauth.");
    }

    @Bean
    public TokenProvider tokenProvider(){
        String secret = propertyResolver.getProperty("secret", String.class, "mySecretXAuthSecret");
        int validityInSeconds = propertyResolver.getProperty("tokenValidityInSeconds", Integer.class, 3600);
        return new TokenProvider(secret, validityInSeconds,userService,userLoginTokenRepository);
    }
}
