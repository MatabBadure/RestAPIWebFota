package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.repository.PatientInfoRepository;

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
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PatientInfoResource REST controller.
 *
 * @see PatientInfoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PatientInfoResourceTest {

    private static final String DEFAULT_MRN = "SAMPLE_TEXT";
    private static final String UPDATED_MRN = "UPDATED_TEXT";
    private static final String DEFAULT_HILLROM_ID = "SAMPLE_TEXT";
    private static final String UPDATED_HILLROM_ID = "UPDATED_TEXT";
    private static final String DEFAULT_HUB_ID = "SAMPLE_TEXT";
    private static final String UPDATED_HUB_ID = "UPDATED_TEXT";
    private static final String DEFAULT_SERIAL_NUMBER = "SAMPLE_TEXT";
    private static final String UPDATED_SERIAL_NUMBER = "UPDATED_TEXT";
    private static final String DEFAULT_BLUETOOTH_ID = "SAMPLE_TEXT";
    private static final String UPDATED_BLUETOOTH_ID = "UPDATED_TEXT";
    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";
    private static final String DEFAULT_FIRST_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_FIRST_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_MIDDLE_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_MIDDLE_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_LAST_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_LAST_NAME = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_DOB = new LocalDate(0L);
    private static final LocalDate UPDATED_DOB = new LocalDate();
    private static final String DEFAULT_EMAIL = "SAMPLE_TEXT";
    private static final String UPDATED_EMAIL = "UPDATED_TEXT";

    private static final Boolean DEFAULT_WEB_LOGIN_CREATED = false;
    private static final Boolean UPDATED_WEB_LOGIN_CREATED = true;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    @Inject
    private PatientInfoRepository patientInfoRepository;

    private MockMvc restPatientInfoMockMvc;

    private PatientInfo patientInfo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PatientInfoResource patientInfoResource = new PatientInfoResource();
        ReflectionTestUtils.setField(patientInfoResource, "patientInfoRepository", patientInfoRepository);
        this.restPatientInfoMockMvc = MockMvcBuilders.standaloneSetup(patientInfoResource).build();
    }

    @Before
    public void initTest() {
        patientInfo = new PatientInfo();
        patientInfo.setMrn(DEFAULT_MRN);
        patientInfo.setHillromId(DEFAULT_HILLROM_ID);
        patientInfo.setHubId(DEFAULT_HUB_ID);
        patientInfo.setSerialNumber(DEFAULT_SERIAL_NUMBER);
        patientInfo.setBluetoothId(DEFAULT_BLUETOOTH_ID);
        patientInfo.setTitle(DEFAULT_TITLE);
        patientInfo.setFirstName(DEFAULT_FIRST_NAME);
        patientInfo.setMiddleName(DEFAULT_MIDDLE_NAME);
        patientInfo.setLastName(DEFAULT_LAST_NAME);
        patientInfo.setDob(DEFAULT_DOB);
        patientInfo.setEmail(DEFAULT_EMAIL);
        patientInfo.setWebLoginCreated(DEFAULT_WEB_LOGIN_CREATED);
        patientInfo.setIsDeleted(DEFAULT_IS_DELETED);
    }

    @Test
    @Transactional
    public void createPatientInfo() throws Exception {
        int databaseSizeBeforeCreate = patientInfoRepository.findAll().size();

        // Create the PatientInfo
        restPatientInfoMockMvc.perform(post("/api/patientInfos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
                .andExpect(status().isCreated());

        // Validate the PatientInfo in the database
        List<PatientInfo> patientInfos = patientInfoRepository.findAll();
        assertThat(patientInfos).hasSize(databaseSizeBeforeCreate + 1);
        PatientInfo testPatientInfo = patientInfos.get(patientInfos.size() - 1);
        assertThat(testPatientInfo.getMrn()).isEqualTo(DEFAULT_MRN);
        assertThat(testPatientInfo.getHillromId()).isEqualTo(DEFAULT_HILLROM_ID);
        assertThat(testPatientInfo.getHubId()).isEqualTo(DEFAULT_HUB_ID);
        assertThat(testPatientInfo.getSerialNumber()).isEqualTo(DEFAULT_SERIAL_NUMBER);
        assertThat(testPatientInfo.getBluetoothId()).isEqualTo(DEFAULT_BLUETOOTH_ID);
        assertThat(testPatientInfo.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPatientInfo.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testPatientInfo.getMiddleName()).isEqualTo(DEFAULT_MIDDLE_NAME);
        assertThat(testPatientInfo.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testPatientInfo.getDob()).isEqualTo(DEFAULT_DOB);
        assertThat(testPatientInfo.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testPatientInfo.getWebLoginCreated()).isEqualTo(DEFAULT_WEB_LOGIN_CREATED);
        assertThat(testPatientInfo.getIsDeleted()).isEqualTo(DEFAULT_IS_DELETED);
    }

    @Test
    @Transactional
    public void getAllPatientInfos() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);

        // Get all the patientInfos
        restPatientInfoMockMvc.perform(get("/api/patientInfos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(patientInfo.getId().intValue())))
                .andExpect(jsonPath("$.[*].mrn").value(hasItem(DEFAULT_MRN.toString())))
                .andExpect(jsonPath("$.[*].hillromId").value(hasItem(DEFAULT_HILLROM_ID.toString())))
                .andExpect(jsonPath("$.[*].hubId").value(hasItem(DEFAULT_HUB_ID.toString())))
                .andExpect(jsonPath("$.[*].serialNumber").value(hasItem(DEFAULT_SERIAL_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].bluetoothId").value(hasItem(DEFAULT_BLUETOOTH_ID.toString())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
                .andExpect(jsonPath("$.[*].middleName").value(hasItem(DEFAULT_MIDDLE_NAME.toString())))
                .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
                .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].webLoginCreated").value(hasItem(DEFAULT_WEB_LOGIN_CREATED.booleanValue())))
                .andExpect(jsonPath("$.[*].isDeleted").value(hasItem(DEFAULT_IS_DELETED.booleanValue())));
    }

    @Test
    @Transactional
    public void getPatientInfo() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);

        // Get the patientInfo
        restPatientInfoMockMvc.perform(get("/api/patientInfos/{id}", patientInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(patientInfo.getId().intValue()))
            .andExpect(jsonPath("$.mrn").value(DEFAULT_MRN.toString()))
            .andExpect(jsonPath("$.hillromId").value(DEFAULT_HILLROM_ID.toString()))
            .andExpect(jsonPath("$.hubId").value(DEFAULT_HUB_ID.toString()))
            .andExpect(jsonPath("$.serialNumber").value(DEFAULT_SERIAL_NUMBER.toString()))
            .andExpect(jsonPath("$.bluetoothId").value(DEFAULT_BLUETOOTH_ID.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
            .andExpect(jsonPath("$.middleName").value(DEFAULT_MIDDLE_NAME.toString()))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.webLoginCreated").value(DEFAULT_WEB_LOGIN_CREATED.booleanValue()))
            .andExpect(jsonPath("$.isDeleted").value(DEFAULT_IS_DELETED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingPatientInfo() throws Exception {
        // Get the patientInfo
        restPatientInfoMockMvc.perform(get("/api/patientInfos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePatientInfo() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);

		int databaseSizeBeforeUpdate = patientInfoRepository.findAll().size();

        // Update the patientInfo
        patientInfo.setMrn(UPDATED_MRN);
        patientInfo.setHillromId(UPDATED_HILLROM_ID);
        patientInfo.setHubId(UPDATED_HUB_ID);
        patientInfo.setSerialNumber(UPDATED_SERIAL_NUMBER);
        patientInfo.setBluetoothId(UPDATED_BLUETOOTH_ID);
        patientInfo.setTitle(UPDATED_TITLE);
        patientInfo.setFirstName(UPDATED_FIRST_NAME);
        patientInfo.setMiddleName(UPDATED_MIDDLE_NAME);
        patientInfo.setLastName(UPDATED_LAST_NAME);
        patientInfo.setDob(UPDATED_DOB);
        patientInfo.setEmail(UPDATED_EMAIL);
        patientInfo.setWebLoginCreated(UPDATED_WEB_LOGIN_CREATED);
        patientInfo.setIsDeleted(UPDATED_IS_DELETED);
        restPatientInfoMockMvc.perform(put("/api/patientInfos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
                .andExpect(status().isOk());

        // Validate the PatientInfo in the database
        List<PatientInfo> patientInfos = patientInfoRepository.findAll();
        assertThat(patientInfos).hasSize(databaseSizeBeforeUpdate);
        PatientInfo testPatientInfo = patientInfos.get(patientInfos.size() - 1);
        assertThat(testPatientInfo.getMrn()).isEqualTo(UPDATED_MRN);
        assertThat(testPatientInfo.getHillromId()).isEqualTo(UPDATED_HILLROM_ID);
        assertThat(testPatientInfo.getHubId()).isEqualTo(UPDATED_HUB_ID);
        assertThat(testPatientInfo.getSerialNumber()).isEqualTo(UPDATED_SERIAL_NUMBER);
        assertThat(testPatientInfo.getBluetoothId()).isEqualTo(UPDATED_BLUETOOTH_ID);
        assertThat(testPatientInfo.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPatientInfo.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testPatientInfo.getMiddleName()).isEqualTo(UPDATED_MIDDLE_NAME);
        assertThat(testPatientInfo.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testPatientInfo.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testPatientInfo.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testPatientInfo.getWebLoginCreated()).isEqualTo(UPDATED_WEB_LOGIN_CREATED);
        assertThat(testPatientInfo.getIsDeleted()).isEqualTo(UPDATED_IS_DELETED);
    }

    @Test
    @Transactional
    public void deletePatientInfo() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);

		int databaseSizeBeforeDelete = patientInfoRepository.findAll().size();

        // Get the patientInfo
        restPatientInfoMockMvc.perform(delete("/api/patientInfos/{id}", patientInfo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<PatientInfo> patientInfos = patientInfoRepository.findAll();
        assertThat(patientInfos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
