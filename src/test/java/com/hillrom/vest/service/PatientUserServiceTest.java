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
public class PatientUserServiceTest {
	
	private static final String PASSWORD = "admin";
	private static final String USERNAME = "admin@localhost.com";
	
	private static final String HILLROM_ID = "HR0000"+Math.abs(Math.random()*100);
	private static final String TITLE = "Mr";
	private static final String FIRST_NAME = "Peter";
	private static final String MIDDLE_NAME = "John";
	private static final String LAST_NAME = "Parker";
	private static final String GENDER = "male";
	private static final String LANG_KEY = "en";
	private static final String ZIPCODE = "560009";
	private static final String DOB = "05/08/1990";
	private static final String ROLE = AuthoritiesConstants.PATIENT;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserRepository userRepository;

	private UserExtensionDTO userExtensionDTO;
	
	@Before
    public void initTest() {
		userExtensionDTO = new UserExtensionDTO();
		userExtensionDTO.setHillromId(HILLROM_ID);
		userExtensionDTO.setTitle(TITLE);
		userExtensionDTO.setFirstName(FIRST_NAME);
		userExtensionDTO.setMiddleName(MIDDLE_NAME);
		userExtensionDTO.setLastName(LAST_NAME);
		userExtensionDTO.setGender(GENDER);
		userExtensionDTO.setLangKey(LANG_KEY);
		userExtensionDTO.setZipcode(Integer.parseInt(ZIPCODE));
		userExtensionDTO.setRole(ROLE);
		userExtensionDTO.setDob(DOB);
		
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ACCT_SERVICES));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, authorities));
        SecurityContextHolder.setContext(securityContext);
    }
	
	@Test(expected = Exception.class)
	public void createPatientUserSuccessfully() throws HillromException{
		UserExtension newPatientUser = userService.createPatientUser(userExtensionDTO);
		assertThat(newPatientUser.isDeleted()).isFalse();
        assertThat(newPatientUser.getId()).isNotNull();
        assertThat(newPatientUser.getEmail()).isNotNull();
        userRepository.delete(newPatientUser);
	}
	
	@Test(expected = Exception.class)
	public void updatePatientUserSuccessfully() throws HillromException{
		UserExtension newPatientUser = userService.createPatientUser(userExtensionDTO);
		userExtensionDTO.setMiddleName("Smith");
		userExtensionDTO.setCity("Bangalore");
		UserExtension updatedPatientUser = userService.updatePatientUser(newPatientUser.getId(), userExtensionDTO);
		assertThat(updatedPatientUser.isDeleted()).isFalse();
        assertThat(updatedPatientUser.getId()).isNotNull();
        assertThat(updatedPatientUser.getMiddleName()).isEqualTo(userExtensionDTO.getMiddleName());
        assertThat(updatedPatientUser.getCity()).isEqualTo(userExtensionDTO.getCity());
        assertThat(updatedPatientUser.getEmail()).isNotNull();
        userRepository.delete(updatedPatientUser);
	}
	
	@Test(expected = Exception.class)
    public void assertThatPatientUserIsDeleted() throws HillromException {
		UserExtension newPatientUser = userService.createPatientUser(userExtensionDTO);
    	
        JSONObject jsonObject = userService.deleteUser(newPatientUser.getId());
        String message = (String) jsonObject.get("message");
        
        assertThat(message).isNotNull();
        assertThat(message).isEqualToIgnoringCase("Patient User deleted successfully.");
    }
}
