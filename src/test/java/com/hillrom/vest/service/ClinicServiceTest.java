package com.hillrom.vest.service;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.web.rest.dto.ClinicDTO;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

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
public class ClinicServiceTest {

    @Inject
    private ClinicRepository clinicRepository;

    @Inject
    private ClinicService clinicService;
    
    private Clinic clinic;
    
    private ClinicDTO clinicDTO;
    
    @Before
    public void initTest() {
    	ArrayList<Map<String, String>> childClinics = new ArrayList<Map<String,String>>();
    	for(int i = 0; i<2; i++ ) {
	    	Map<String, String> childClinic = new HashMap<String, String>();
	    	childClinic.put("name", "Fortis-Branch"+i+1);
	    	childClinics.add(childClinic);
    	}
        clinicDTO = new ClinicDTO("Fortis Hospital", "Bannerghatta Road", 560042, "Bangalore", "Karnataka", Long.parseLong("7896541230"), Long.parseLong("9874563210"), null, childClinics, null);

        clinic = clinicService.createClinic(clinicDTO);
    }
    
    @Test
    public void assertThatClinicIsCreated() {
        Clinic newClinic = clinicService.createClinic(clinicDTO);

        assertThat(newClinic.isDeleted()).isFalse();
        assertThat(newClinic.getId()).isNotNull();
        assertThat(newClinic.getName()).isNotNull();

        for(Clinic childClinic : newClinic.getChildClinics()) {
        	clinicRepository.delete(childClinic);
        }
        clinicRepository.delete(newClinic);
    }

    @Test
    public void assertThatClinicIsUpdated() {
    	
    	clinicRepository.saveAndFlush(clinic);
    	
    	ArrayList<Map<String, String>> childClinics = new ArrayList<Map<String,String>>();
    	for(Clinic childClinic : clinic.getChildClinics()) {
	    	Map<String, String> child = new HashMap<String, String>();
	    	child.put("id", childClinic.getId().toString());
	    	child.put("name", childClinic.getName());
	    	childClinics.add(child);
    	}
    	clinicDTO.setChildClinics(childClinics);
    	
        clinic = clinicService.updateClinic(clinic.getId(), clinicDTO);

        assertThat(clinic.isDeleted()).isFalse();
        assertThat(clinic.getId()).isNotNull();
        assertThat(clinic.getName()).isNotNull();
        assertThat(clinic.getChildClinics().size() == clinicDTO.getChildClinics().size()).isTrue();
    }
}
