package com.hillrom.vest.security;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.UserService;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) {
        log.debug("Authenticating {}", email);
        String lowercaseEmail = email.toLowerCase();
        Optional<User> userFromDatabase =  userRepository.findOneByEmailOrHillromId(lowercaseEmail);
        return userFromDatabase.map(user -> {
            if (!user.getActivated()) {
                throw new UserNotActivatedException("User " + lowercaseEmail + " was not activated");
            }
            List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                    .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                    .collect(Collectors.toList());
            String password = user.getPassword();
            // handle NULL password for user created from Data Transmission / JDE script
            if(Objects.isNull(password)){
            	try{
            		password = userService.generateDefaultPassword(user);
            	}catch(Exception e){// Exception indicates that mandatory fields are not present
            		throw new UsernameNotFoundException("User " + lowercaseEmail + " was not found in the database");
            	}
            }
            return new org.springframework.security.core.userdetails.User(lowercaseEmail,
                    password,
                    grantedAuthorities);
        }).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseEmail + " was not found in the database"));
    }
}
