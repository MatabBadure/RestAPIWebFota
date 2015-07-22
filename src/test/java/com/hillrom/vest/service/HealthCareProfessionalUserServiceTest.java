package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class HealthCareProfessionalUserServiceTest {
	
	private static final String HILLROM_ID = "HR000026";
	private static final String TITLE = "Mr";
	private static final String FIRST_NAME = "Peter";
	private static final String MIDDLE_NAME = "John";
	private static final String LAST_NAME = "Parker";
	private static final String EMAIL = "rishabhjain+"+Math.random()*100+"@neevtech.com";
	private static final String SPECIALITY = "Orthpedician";
	private static final String CREDENTIALS = "MD,MBBS";
	private static final String ZIPCODE = "560009";
	private static final String ROLE = AuthoritiesConstants.HCP;
	private static final String BASE_URL = "http://localhost:8080";
	
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
		userExtensionDTO.setEmail(EMAIL);
		userExtensionDTO.setSpeciality(SPECIALITY);
		userExtensionDTO.setCredentials(CREDENTIALS);;
		userExtensionDTO.setZipcode(Integer.parseInt(ZIPCODE));
		userExtensionDTO.setRole(ROLE);
    }
	
	@Test
	public void createHCPUserSuccessfully(){
		JSONObject jsonObject = userService.createUser(userExtensionDTO, BASE_URL);
		UserExtension newHCP = (UserExtension)jsonObject.get("user");
		assertThat(newHCP.isDeleted()).isFalse();
        assertThat(newHCP.getId()).isNotNull();
        assertThat(newHCP.getEmail()).isNotNull();
        userRepository.delete(newHCP);
	}
	
	@Test
	public void updateHCPUserSuccessfully(){
		UserExtension newPatientUser = userService.createPatientUser(userExtensionDTO);
		userExtensionDTO.setFirstName("Remus");
		userExtensionDTO.setCity("Bangalore");
		JSONObject jsonObject = userService.updateUser(newPatientUser.getId(), userExtensionDTO, BASE_URL);
		UserExtension updatedHCP = (UserExtension)jsonObject.get("user");
		assertThat(updatedHCP.isDeleted()).isFalse();
        assertThat(updatedHCP.getId()).isNotNull();
        assertThat(updatedHCP.getFirstName()).isEqualTo(userExtensionDTO.getFirstName());
        assertThat(updatedHCP.getCity()).isEqualTo(userExtensionDTO.getCity());
        assertThat(updatedHCP.getEmail()).isNotNull();
        userRepository.delete(updatedHCP);
	}
}
