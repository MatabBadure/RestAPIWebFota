/*package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.repository.ClinicRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


*//**
 * Test class for the ClinicResource REST controller.
 *
 * @see ClinicResource
 *//*
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ClinicResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_ADDRESS = "SAMPLE_TEXT";
    private static final String UPDATED_ADDRESS = "UPDATED_TEXT";

    private static final Integer DEFAULT_ZIPCODE = 0;
    private static final Integer UPDATED_ZIPCODE = 1;
    private static final String DEFAULT_CITY = "SAMPLE_TEXT";
    private static final String UPDATED_CITY = "UPDATED_TEXT";
    private static final String DEFAULT_STATE = "SAMPLE_TEXT";
    private static final String UPDATED_STATE = "UPDATED_TEXT";

    private static final Long DEFAULT_PHONE_NUMBER = 0L;
    private static final Long UPDATED_PHONE_NUMBER = 1L;

    private static final Long DEFAULT_FAX_NUMBER = 0L;
    private static final Long UPDATED_FAX_NUMBER = 1L;
    private static final String DEFAULT_HILLROM_ID = "SAMPLE_TEXT";
    private static final String UPDATED_HILLROM_ID = "UPDATED_TEXT";
    private static final String DEFAULT_PARENT_CLINIC_ID = "SAMPLE_TEXT";
    private static final String UPDATED_PARENT_CLINIC_ID = "UPDATED_TEXT";

    @Inject
    private ClinicRepository clinicRepository;

    private MockMvc restClinicMockMvc;

    private Clinic clinic;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClinicResource clinicResource = new ClinicResource();
        ReflectionTestUtils.setField(clinicResource, "clinicRepository", clinicRepository);
        this.restClinicMockMvc = MockMvcBuilders.standaloneSetup(clinicResource).build();
    }

    @Before
    public void initTest() {
        clinic = new Clinic();
        clinic.setName(DEFAULT_NAME);
        clinic.setAddress(DEFAULT_ADDRESS);
        clinic.setZipcode(DEFAULT_ZIPCODE);
        clinic.setCity(DEFAULT_CITY);
        clinic.setState(DEFAULT_STATE);
        clinic.setPhoneNumber(DEFAULT_PHONE_NUMBER);
        clinic.setFaxNumber(DEFAULT_FAX_NUMBER);
        clinic.setHillromId(DEFAULT_HILLROM_ID);
    }

    @Test
    @Transactional
    public void createClinic() throws Exception {
        int databaseSizeBeforeCreate = clinicRepository.findAll().size();

        // Create the Clinic
        restClinicMockMvc.perform(post("/api/clinics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clinic)))
                .andExpect(status().isCreated());

        // Validate the Clinic in the database
        List<Clinic> clinics = clinicRepository.findAll();
        assertThat(clinics).hasSize(databaseSizeBeforeCreate + 1);
        Clinic testClinic = clinics.get(clinics.size() - 1);
        assertThat(testClinic.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testClinic.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testClinic.getZipcode()).isEqualTo(DEFAULT_ZIPCODE);
        assertThat(testClinic.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testClinic.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testClinic.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testClinic.getFaxNumber()).isEqualTo(DEFAULT_FAX_NUMBER);
        assertThat(testClinic.getHillromId()).isEqualTo(DEFAULT_HILLROM_ID);
    }

    @Test
    @Transactional
    public void getAllClinics() throws Exception {
        // Initialize the database
        clinicRepository.saveAndFlush(clinic);

        // Get all the clinics
        restClinicMockMvc.perform(get("/api/clinics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(clinic.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].zipcode").value(hasItem(DEFAULT_ZIPCODE)))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
                .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.intValue())))
                .andExpect(jsonPath("$.[*].faxNumber").value(hasItem(DEFAULT_FAX_NUMBER.intValue())))
                .andExpect(jsonPath("$.[*].hillromId").value(hasItem(DEFAULT_HILLROM_ID.toString())))
                .andExpect(jsonPath("$.[*].parentClinicId").value(hasItem(DEFAULT_PARENT_CLINIC_ID.toString())));
    }

    @Test
    @Transactional
    public void getClinic() throws Exception {
        // Initialize the database
        clinicRepository.saveAndFlush(clinic);

        // Get the clinic
        restClinicMockMvc.perform(get("/api/clinics/{id}", clinic.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(clinic.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.zipcode").value(DEFAULT_ZIPCODE))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.intValue()))
            .andExpect(jsonPath("$.faxNumber").value(DEFAULT_FAX_NUMBER.intValue()))
            .andExpect(jsonPath("$.hillromId").value(DEFAULT_HILLROM_ID.toString()))
            .andExpect(jsonPath("$.parentClinicId").value(DEFAULT_PARENT_CLINIC_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClinic() throws Exception {
        // Get the clinic
        restClinicMockMvc.perform(get("/api/clinics/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClinic() throws Exception {
        // Initialize the database
        clinicRepository.saveAndFlush(clinic);

		int databaseSizeBeforeUpdate = clinicRepository.findAll().size();

        // Update the clinic
        clinic.setName(UPDATED_NAME);
        clinic.setAddress(UPDATED_ADDRESS);
        clinic.setZipcode(UPDATED_ZIPCODE);
        clinic.setCity(UPDATED_CITY);
        clinic.setState(UPDATED_STATE);
        clinic.setPhoneNumber(UPDATED_PHONE_NUMBER);
        clinic.setFaxNumber(UPDATED_FAX_NUMBER);
        clinic.setHillromId(UPDATED_HILLROM_ID);
        restClinicMockMvc.perform(put("/api/clinics")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(clinic)))
                .andExpect(status().isOk());

        // Validate the Clinic in the database
        List<Clinic> clinics = clinicRepository.findAll();
        assertThat(clinics).hasSize(databaseSizeBeforeUpdate);
        Clinic testClinic = clinics.get(clinics.size() - 1);
        assertThat(testClinic.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testClinic.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testClinic.getZipcode()).isEqualTo(UPDATED_ZIPCODE);
        assertThat(testClinic.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testClinic.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testClinic.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testClinic.getFaxNumber()).isEqualTo(UPDATED_FAX_NUMBER);
        assertThat(testClinic.getHillromId()).isEqualTo(UPDATED_HILLROM_ID);
    }

    @Test
    @Transactional
    public void deleteClinic() throws Exception {
        // Initialize the database
        clinicRepository.saveAndFlush(clinic);

		int databaseSizeBeforeDelete = clinicRepository.findAll().size();

        // Get the clinic
        restClinicMockMvc.perform(delete("/api/clinics/{id}", clinic.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Clinic> clinics = clinicRepository.findAll();
        assertThat(clinics).hasSize(databaseSizeBeforeDelete - 1);
    }
}
*/