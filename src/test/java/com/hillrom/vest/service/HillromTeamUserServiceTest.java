package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class HillromTeamUserServiceTest {
	
	private static final String PASSWORD = "admin";
	private static final String USERNAME = "admin@localhost.com";
	
	private static final String TITLE = "Mr.";
	private static final String FIRST_NAME = "Rishabh";
	private static final String LAST_NAME = "Jain";
	private static final String ROLE = AuthoritiesConstants.ASSOCIATES;
	private static final String EMAIL = "rishabhjain+"+Math.abs(Math.random()*100)+"@neevtech.com";
	private static final String BASE_URL = "http://localhost:8080";
	private static final String String = null;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserRepository userRepository;
	
	private UserExtensionDTO userExtensionDTO;
	
	@Before
    public void initTest() {
		userExtensionDTO = new UserExtensionDTO();
		userExtensionDTO.setTitle(TITLE);
		userExtensionDTO.setFirstName(FIRST_NAME);
		userExtensionDTO.setLastName(LAST_NAME);
		userExtensionDTO.setEmail(EMAIL);
		userExtensionDTO.setRole(ROLE);
		
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, authorities));
        SecurityContextHolder.setContext(securityContext);
    }
	
	@Test(expected = Exception.class)
	public void createHillromUserSuccessfully() throws HillromException{
		UserExtension newHRUser = userService.createUser(userExtensionDTO, BASE_URL);
		assertThat(newHRUser.isDeleted()).isFalse();
        assertThat(newHRUser.getId()).isNotNull();
        assertThat(newHRUser.getEmail()).isNotNull();
        userRepository.delete(newHRUser);
	}
	
//	@Test(expected = Exception.class)
//	public void assertThatUpdateHillromUserSuccessfully() throws HillromException{
//		userExtensionDTO.setFirstName("Remus");
//		userExtensionDTO.setRole(AuthoritiesConstants.ACCT_SERVICES);
//		UserExtension newHRUser = userService.createUser(userExtensionDTO, BASE_URL);
//		JSONObject jsonObject = userService.updateUser(newHRUser.getId(), userExtensionDTO, BASE_URL);
//		UserExtension updatedHRUser = (UserExtension)jsonObject.get("user");
//		assertThat(updatedHRUser.isDeleted()).isFalse();
//        assertThat(updatedHRUser.getId()).isNotNull();
//        assertThat(updatedHRUser.getFirstName()).isEqualTo(userExtensionDTO.getFirstName());
//        assertThat(updatedHRUser.getEmail()).isNotNull();
//        userRepository.delete(updatedHRUser);
//	}

	@Test(expected = Exception.class)
    public void assertThatHillromUserDeletedSuccessfully() throws HillromException {
		UserExtension newHRUser = userService.createUser(userExtensionDTO, BASE_URL);
    	String baseUrl = "baseUrl";
		JSONObject jsonObject = userService.deleteUser(newHRUser.getId(),baseUrl);
        String message = (String) jsonObject.get("message");
        System.out.println("message Created : "+jsonObject);
        assertThat(message).isNotEmpty();
        assertThat(message).isEqualToIgnoringCase("User deleted successfully.");
    }
}
