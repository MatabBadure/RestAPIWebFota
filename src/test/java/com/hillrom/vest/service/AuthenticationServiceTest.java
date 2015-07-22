package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.UserNotActivatedException;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class AuthenticationServiceTest {

	private static final String WRONG_PASSWORD = "wrongPassword";

	private static final String PASSWORD = "password";
	
	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
    private AuthenticationService authenticationService;
	
	@Inject
    private UserRepository userRepository;
    
    @Inject
    private AuthorityRepository authorityRepository;
    
    @Test
    public void successfulLogin() {
        User newUser = createUser();
        String username = newUser.getEmail();
        UserLoginToken token = authenticationService.authenticate(username, PASSWORD);
        assertThat(token);
        userRepository.delete(newUser);
    }

    @Test(expected = BadCredentialsException.class)
    public void invalidCredentialsLogin() {
        User newUser = createUser();
        String username = newUser.getEmail();
        authenticationService.authenticate(username, WRONG_PASSWORD);
        userRepository.delete(newUser);
    }
    
    @Test(expected = UserNotActivatedException.class)
    public void deletedUserShouldNotLogin() {
        User newUser = createUser();
        newUser.setDeleted(true);
        userRepository.save(newUser);
        
        String username = newUser.getEmail();
        authenticationService.authenticate(username, PASSWORD);
        userRepository.delete(newUser);
    }
    
	private User createUser() {
		User user = new User();
        user.setActivated(true);
        user.setDeleted(false);
        user.setEmail("noname@123.com");
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setFirstName("fname");
        user.setMiddleName("mname");
        user.setLangKey("en");
        user.setActivationKey(null);
        user.setLastLoggedInAt(DateTime.now());
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
        user.getAuthorities().add(authority);
        
        return userRepository.save(user);
	}
    
    
    
}