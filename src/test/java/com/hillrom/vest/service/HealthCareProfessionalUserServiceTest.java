package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.web.rest.dto.ClinicDTO;
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
	private static final String EMAIL = "rishabhjain+"+Math.abs(Math.random()*100)+"@neevtech.com";
	private static final String SPECIALITY = "Orthpedician";
	private static final String CREDENTIALS = "MD,MBBS";
	private static final String ZIPCODE = "560009";
	private static final String ROLE = AuthoritiesConstants.HCP;
	private static final String BASE_URL = "http://localhost:8080";

	@Inject
	private UserService userService;

	@Inject
	private HCPClinicService hcpClinicService;

	@Inject
	private ClinicService clinicService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ClinicRepository clinicRepository;

	private UserExtensionDTO userExtensionDTO;
	private ClinicDTO clinicDTO;
	private Clinic clinic;

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

	@Test(expected = Exception.class)
	public void createHCPUserSuccessfully() throws HillromException{
		UserExtension newHCP = userService.createUser(userExtensionDTO, BASE_URL);
		assertThat(newHCP.isDeleted()).isFalse();
        assertThat(newHCP.getId()).isNotNull();
        assertThat(newHCP.getEmail()).isNotNull();
        userRepository.delete(newHCP);
	}

//	@Test(expected = Exception.class)
//	public void updateHCPUserSuccessfully() throws HillromException{
//		UserExtension newHCP = userService.createUser(userExtensionDTO, BASE_URL);
//		userExtensionDTO.setFirstName("Remus");
//		userExtensionDTO.setCity("Bangalore");
//		JSONObject jsonObject = userService.updateUser(newHCP.getId(), userExtensionDTO, BASE_URL);
//		UserExtension updatedHCP = (UserExtension)jsonObject.get("user");
//		assertThat(updatedHCP.isDeleted()).isFalse();
//        assertThat(updatedHCP.getId()).isNotNull();
//        assertThat(updatedHCP.getFirstName()).isEqualTo(userExtensionDTO.getFirstName());
//        assertThat(updatedHCP.getCity()).isEqualTo(userExtensionDTO.getCity());
//        assertThat(updatedHCP.getEmail()).isNotNull();
//        userRepository.delete(updatedHCP);
//	}

//	@Test(expected = Exception.class)
//	public void dissociateClinicFromHCPUserSuccessfully() throws HillromException{
//		clinicDTO = new ClinicDTO("Fortis Hospital", "Bannerghatta Road", 560042, "Bangalore", "Karnataka", "7896541230", "9874563210", null, true, null);
//	    JSONObject clinicJsonObject = clinicService.createClinic(clinicDTO);
//		clinic = (Clinic)clinicJsonObject.get("Clinic");
//
//		List<Map<String, String>> clinicList = new ArrayList<Map<String, String>>();
//		Map<String, String> clinicId = new HashMap<String, String>();
//		clinicId.put("id", clinic.getId().toString());
//		clinicList.add(clinicId);
//		userExtensionDTO.setClinicList(clinicList);
//
//		UserExtension newHCP = userService.createUser(userExtensionDTO, BASE_URL);
//
//		JSONObject jsonObject = hcpClinicService.dissociateClinicFromHCP(newHCP.getId(), clinicList);
//		UserExtension dissociatedHCP = (UserExtension)jsonObject.get("HCPUser");
//		String message = (String) jsonObject.get("message");
//
//		assertThat(dissociatedHCP.isDeleted()).isFalse();
//        assertThat(dissociatedHCP.getId()).isNotNull();
//        assertThat(dissociatedHCP.getEmail()).isNotNull();
//        assertThat(message).isEqualTo("HCP is dissociated with Clinics successfully.");
//        userRepository.delete(dissociatedHCP);
//        clinicRepository.delete(clinic);
//	}

}
