package com.hillrom.vest.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.service.util.RandomUtil;

public class AuthenticationProvider extends DaoAuthenticationProvider {

	private final Logger log = LoggerFactory.getLogger(AuthenticationProvider.class);

    private PasswordEncoder passwordEncoder;
    
    @Inject
    private UserService userService;

    public AuthenticationProvider(PasswordEncoder passwordEncoder,UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
    
	@Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token =
                (UsernamePasswordAuthenticationToken) authentication;
        String login = token.getName().toLowerCase();
        log.debug("login : "+login);
        
        User user = getUserByLogin(login);
        
        String password = user.getPassword();
        String tokenPassword = (String) token.getCredentials();
        matchPasswords(password, tokenPassword);
        
        processFirstTimeLogin(user);
        
        UserDetails userDetails = buildUserDetails(user);
        
        return new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(),
        		userDetails.getAuthorities());
    }

	/**
	 * Returns User from DB if User is Not ( Active | Deleted )
	 * @param login
	 * @return User
	 */
	private User getUserByLogin(String login) {
		Optional<User> userFromDatabase = userService.findOneByEmail(login);
        User user = null;
        if(userFromDatabase.isPresent()){
        	user = userFromDatabase.get();
        }else{
        	throw new UsernameNotFoundException("User does not exists");
        }
        
        // User is not activated or Deleted
        if (!user.getActivated() || user.isDeleted()) {
            throw new UserNotActivatedException("User " + login + " was not activated");
        }
		return user;
	}

	/**
	 * Handles password verification 
	 * @param password
	 * @param tokenPassword
	 */
	private void matchPasswords(String password, String tokenPassword) {
		if (!passwordEncoder.matches(tokenPassword, password)) {
		    throw new BadCredentialsException("Invalid username/password");
		}
	}
	
	/**
	 * Throws appropriate exceptions if the User loggedin for the first time
	 * @param user
	 */
	private void processFirstTimeLogin(User user) {
		if(null == user.getLastLoggedInAt()){
        	if(!RandomUtil.isValidEmail(user.getEmail()))
        		throw new EmailNotPresentForPatientException("Please Register with Email and Password to Login",prepareJSONForPatientUser(user.getEmail().toLowerCase(),user.getPassword()));
        	else
        		throw new FirstLoginException("First Time Login, please reset your password",prepareJSONForPatientUser(user.getEmail().toLowerCase(),user.getPassword()));
        }
	}
	
	/**
	 *  prepares the JSONObject to be passed with Exception to create auth token
	 * @param username
	 * @param encodedPassword
	 * @return JSONObject
	 */
	private JSONObject prepareJSONForPatientUser(String username,String encodedPassword){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", username);
		jsonObject.put("password", encodedPassword);
		return jsonObject;
	}

	/**
	 *  build UserDetails Object with granted authorities
	 * @param user
	 * @return UserDetails
	 */
	private UserDetails buildUserDetails(User user){
		List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return  new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(),
                grantedAuthorities);
	}
	
	@Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken
                .class.equals(authentication);
    }
}
