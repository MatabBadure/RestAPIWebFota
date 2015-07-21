package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.inject.Inject;

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
public class PatientUserServiceTest {
	
	private static final String HILLROM_ID = "HR000026";
	private static final String TITLE = "Mr";
	private static final String FIRST_NAME = "Peter";
	private static final String MIDDLE_NAME = "John";
	private static final String LAST_NAME = "Parker";
	private static final String GENDER = "male";
	private static final String LANG_KEY = "en";
	private static final String ZIPCODE = "560009";
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
    }
	
	@Test
	public void createPatientUserSuccessfully(){
		UserExtension newPatientUser = userService.createPatientUser(userExtensionDTO);
		assertThat(newPatientUser.isDeleted()).isFalse();
        assertThat(newPatientUser.getId()).isNotNull();
        assertThat(newPatientUser.getEmail()).isNotNull();
        userRepository.delete(newPatientUser);
	}
	
	@Test
	public void updatePatientUserSuccessfully(){
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
}
