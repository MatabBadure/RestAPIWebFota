package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.PATIENT_VEST_DEVICE_RAW_LOGS;
import com.hillrom.vest.repository.PATIENT_VEST_DEVICE_RAW_LOGSRepository;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PATIENT_VEST_DEVICE_RAW_LOGSResource REST controller.
 *
 * @see PATIENT_VEST_DEVICE_RAW_LOGSResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PATIENT_VEST_DEVICE_RAW_LOGSResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final DateTime DEFAULT_HUB_RECEIVE_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_HUB_RECEIVE_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_HUB_RECEIVE_TIME_STR = dateTimeFormatter.print(DEFAULT_HUB_RECEIVE_TIME);
    private static final String DEFAULT_DEVICE_ADDRESS = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_ADDRESS = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_MODEL_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_MODEL_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_DATA = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_DATA = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_SERIAL_NUMBER = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_SERIAL_NUMBER = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_HUB_ID = "SAMPLE_TEXT";
    private static final String UPDATED_HUB_ID = "UPDATED_TEXT";
    private static final String DEFAULT_AIR_INTERFACE_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_AIR_INTERFACE_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_CUSTOMER_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_CUSTOMER_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_TIMEZONE = "SAMPLE_TEXT";
    private static final String UPDATED_TIMEZONE = "UPDATED_TEXT";
    private static final String DEFAULT_SP_RECEIVE_TIME = "SAMPLE_TEXT";
    private static final String UPDATED_SP_RECEIVE_TIME = "UPDATED_TEXT";

    private static final Integer DEFAULT_HUB_RECEIVE_TIME_OFFSET = 0;
    private static final Integer UPDATED_HUB_RECEIVE_TIME_OFFSET = 1;
    private static final String DEFAULT_CUC_VERSION = "SAMPLE_TEXT";
    private static final String UPDATED_CUC_VERSION = "UPDATED_TEXT";
    private static final String DEFAULT_CUSTOMER_ID = "SAMPLE_TEXT";
    private static final String UPDATED_CUSTOMER_ID = "UPDATED_TEXT";
    private static final String DEFAULT_RAW_MESSAGE = "SAMPLE_TEXT";
    private static final String UPDATED_RAW_MESSAGE = "UPDATED_TEXT";

    @Inject
    private PATIENT_VEST_DEVICE_RAW_LOGSRepository pATIENT_VEST_DEVICE_RAW_LOGSRepository;

    private MockMvc restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc;

    private PATIENT_VEST_DEVICE_RAW_LOGS pATIENT_VEST_DEVICE_RAW_LOGS;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PATIENT_VEST_DEVICE_RAW_LOGSResource pATIENT_VEST_DEVICE_RAW_LOGSResource = new PATIENT_VEST_DEVICE_RAW_LOGSResource();
        ReflectionTestUtils.setField(pATIENT_VEST_DEVICE_RAW_LOGSResource, "pATIENT_VEST_DEVICE_RAW_LOGSRepository", pATIENT_VEST_DEVICE_RAW_LOGSRepository);
        this.restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc = MockMvcBuilders.standaloneSetup(pATIENT_VEST_DEVICE_RAW_LOGSResource).build();
    }

    @Before
    public void initTest() {
        pATIENT_VEST_DEVICE_RAW_LOGS = new PATIENT_VEST_DEVICE_RAW_LOGS();
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_receive_time(DEFAULT_HUB_RECEIVE_TIME);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_address(DEFAULT_DEVICE_ADDRESS);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_model_type(DEFAULT_DEVICE_MODEL_TYPE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_data(DEFAULT_DEVICE_DATA);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_serial_number(DEFAULT_DEVICE_SERIAL_NUMBER);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_type(DEFAULT_DEVICE_TYPE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_id(DEFAULT_HUB_ID);
        pATIENT_VEST_DEVICE_RAW_LOGS.setAir_interface_type(DEFAULT_AIR_INTERFACE_TYPE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setCustomer_name(DEFAULT_CUSTOMER_NAME);
        pATIENT_VEST_DEVICE_RAW_LOGS.setTimezone(DEFAULT_TIMEZONE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setSp_receive_time(DEFAULT_SP_RECEIVE_TIME);
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_receive_time_offset(DEFAULT_HUB_RECEIVE_TIME_OFFSET);
        pATIENT_VEST_DEVICE_RAW_LOGS.setCuc_version(DEFAULT_CUC_VERSION);
        pATIENT_VEST_DEVICE_RAW_LOGS.setCustomer_id(DEFAULT_CUSTOMER_ID);
        pATIENT_VEST_DEVICE_RAW_LOGS.setRaw_message(DEFAULT_RAW_MESSAGE);
    }

    @Test
    @Transactional
    public void createPATIENT_VEST_DEVICE_RAW_LOGS() throws Exception {
        int databaseSizeBeforeCreate = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll().size();

        // Create the PATIENT_VEST_DEVICE_RAW_LOGS
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(post("/api/pATIENT_VEST_DEVICE_RAW_LOGSs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pATIENT_VEST_DEVICE_RAW_LOGS)))
                .andExpect(status().isCreated());

        // Validate the PATIENT_VEST_DEVICE_RAW_LOGS in the database
        List<PATIENT_VEST_DEVICE_RAW_LOGS> pATIENT_VEST_DEVICE_RAW_LOGSs = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll();
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSs).hasSize(databaseSizeBeforeCreate + 1);
        PATIENT_VEST_DEVICE_RAW_LOGS testPATIENT_VEST_DEVICE_RAW_LOGS = pATIENT_VEST_DEVICE_RAW_LOGSs.get(pATIENT_VEST_DEVICE_RAW_LOGSs.size() - 1);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getHub_receive_time().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_HUB_RECEIVE_TIME);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_address()).isEqualTo(DEFAULT_DEVICE_ADDRESS);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_model_type()).isEqualTo(DEFAULT_DEVICE_MODEL_TYPE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_data()).isEqualTo(DEFAULT_DEVICE_DATA);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_serial_number()).isEqualTo(DEFAULT_DEVICE_SERIAL_NUMBER);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_type()).isEqualTo(DEFAULT_DEVICE_TYPE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getHub_id()).isEqualTo(DEFAULT_HUB_ID);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getAir_interface_type()).isEqualTo(DEFAULT_AIR_INTERFACE_TYPE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getCustomer_name()).isEqualTo(DEFAULT_CUSTOMER_NAME);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getTimezone()).isEqualTo(DEFAULT_TIMEZONE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getSp_receive_time()).isEqualTo(DEFAULT_SP_RECEIVE_TIME);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getHub_receive_time_offset()).isEqualTo(DEFAULT_HUB_RECEIVE_TIME_OFFSET);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getCuc_version()).isEqualTo(DEFAULT_CUC_VERSION);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getCustomer_id()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getRaw_message()).isEqualTo(DEFAULT_RAW_MESSAGE);
    }

    @Test
    @Transactional
    public void checkHub_receive_timeIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll()).hasSize(0);
        // set the field null
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_receive_time(null);

        // Create the PATIENT_VEST_DEVICE_RAW_LOGS, which fails.
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(post("/api/pATIENT_VEST_DEVICE_RAW_LOGSs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pATIENT_VEST_DEVICE_RAW_LOGS)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<PATIENT_VEST_DEVICE_RAW_LOGS> pATIENT_VEST_DEVICE_RAW_LOGSs = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll();
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSs).hasSize(0);
    }

    @Test
    @Transactional
    public void checkDevice_addressIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll()).hasSize(0);
        // set the field null
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_address(null);

        // Create the PATIENT_VEST_DEVICE_RAW_LOGS, which fails.
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(post("/api/pATIENT_VEST_DEVICE_RAW_LOGSs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pATIENT_VEST_DEVICE_RAW_LOGS)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<PATIENT_VEST_DEVICE_RAW_LOGS> pATIENT_VEST_DEVICE_RAW_LOGSs = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll();
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSs).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllPATIENT_VEST_DEVICE_RAW_LOGSs() throws Exception {
        // Initialize the database
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.saveAndFlush(pATIENT_VEST_DEVICE_RAW_LOGS);

        // Get all the pATIENT_VEST_DEVICE_RAW_LOGSs
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(get("/api/pATIENT_VEST_DEVICE_RAW_LOGSs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(pATIENT_VEST_DEVICE_RAW_LOGS.getId().intValue())))
                .andExpect(jsonPath("$.[*].hub_receive_time").value(hasItem(DEFAULT_HUB_RECEIVE_TIME_STR)))
                .andExpect(jsonPath("$.[*].device_address").value(hasItem(DEFAULT_DEVICE_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].device_model_type").value(hasItem(DEFAULT_DEVICE_MODEL_TYPE.toString())))
                .andExpect(jsonPath("$.[*].device_data").value(hasItem(DEFAULT_DEVICE_DATA.toString())))
                .andExpect(jsonPath("$.[*].device_serial_number").value(hasItem(DEFAULT_DEVICE_SERIAL_NUMBER.toString())))
                .andExpect(jsonPath("$.[*].device_type").value(hasItem(DEFAULT_DEVICE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].hub_id").value(hasItem(DEFAULT_HUB_ID.toString())))
                .andExpect(jsonPath("$.[*].air_interface_type").value(hasItem(DEFAULT_AIR_INTERFACE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].customer_name").value(hasItem(DEFAULT_CUSTOMER_NAME.toString())))
                .andExpect(jsonPath("$.[*].timezone").value(hasItem(DEFAULT_TIMEZONE.toString())))
                .andExpect(jsonPath("$.[*].sp_receive_time").value(hasItem(DEFAULT_SP_RECEIVE_TIME.toString())))
                .andExpect(jsonPath("$.[*].hub_receive_time_offset").value(hasItem(DEFAULT_HUB_RECEIVE_TIME_OFFSET)))
                .andExpect(jsonPath("$.[*].cuc_version").value(hasItem(DEFAULT_CUC_VERSION.toString())))
                .andExpect(jsonPath("$.[*].customer_id").value(hasItem(DEFAULT_CUSTOMER_ID.toString())))
                .andExpect(jsonPath("$.[*].raw_message").value(hasItem(DEFAULT_RAW_MESSAGE.toString())));
    }

    @Test
    @Transactional
    public void getPATIENT_VEST_DEVICE_RAW_LOGS() throws Exception {
        // Initialize the database
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.saveAndFlush(pATIENT_VEST_DEVICE_RAW_LOGS);

        // Get the pATIENT_VEST_DEVICE_RAW_LOGS
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(get("/api/pATIENT_VEST_DEVICE_RAW_LOGSs/{id}", pATIENT_VEST_DEVICE_RAW_LOGS.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(pATIENT_VEST_DEVICE_RAW_LOGS.getId().intValue()))
            .andExpect(jsonPath("$.hub_receive_time").value(DEFAULT_HUB_RECEIVE_TIME_STR))
            .andExpect(jsonPath("$.device_address").value(DEFAULT_DEVICE_ADDRESS.toString()))
            .andExpect(jsonPath("$.device_model_type").value(DEFAULT_DEVICE_MODEL_TYPE.toString()))
            .andExpect(jsonPath("$.device_data").value(DEFAULT_DEVICE_DATA.toString()))
            .andExpect(jsonPath("$.device_serial_number").value(DEFAULT_DEVICE_SERIAL_NUMBER.toString()))
            .andExpect(jsonPath("$.device_type").value(DEFAULT_DEVICE_TYPE.toString()))
            .andExpect(jsonPath("$.hub_id").value(DEFAULT_HUB_ID.toString()))
            .andExpect(jsonPath("$.air_interface_type").value(DEFAULT_AIR_INTERFACE_TYPE.toString()))
            .andExpect(jsonPath("$.customer_name").value(DEFAULT_CUSTOMER_NAME.toString()))
            .andExpect(jsonPath("$.timezone").value(DEFAULT_TIMEZONE.toString()))
            .andExpect(jsonPath("$.sp_receive_time").value(DEFAULT_SP_RECEIVE_TIME.toString()))
            .andExpect(jsonPath("$.hub_receive_time_offset").value(DEFAULT_HUB_RECEIVE_TIME_OFFSET))
            .andExpect(jsonPath("$.cuc_version").value(DEFAULT_CUC_VERSION.toString()))
            .andExpect(jsonPath("$.customer_id").value(DEFAULT_CUSTOMER_ID.toString()))
            .andExpect(jsonPath("$.raw_message").value(DEFAULT_RAW_MESSAGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPATIENT_VEST_DEVICE_RAW_LOGS() throws Exception {
        // Get the pATIENT_VEST_DEVICE_RAW_LOGS
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(get("/api/pATIENT_VEST_DEVICE_RAW_LOGSs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePATIENT_VEST_DEVICE_RAW_LOGS() throws Exception {
        // Initialize the database
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.saveAndFlush(pATIENT_VEST_DEVICE_RAW_LOGS);

		int databaseSizeBeforeUpdate = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll().size();

        // Update the pATIENT_VEST_DEVICE_RAW_LOGS
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_receive_time(UPDATED_HUB_RECEIVE_TIME);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_address(UPDATED_DEVICE_ADDRESS);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_model_type(UPDATED_DEVICE_MODEL_TYPE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_data(UPDATED_DEVICE_DATA);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_serial_number(UPDATED_DEVICE_SERIAL_NUMBER);
        pATIENT_VEST_DEVICE_RAW_LOGS.setDevice_type(UPDATED_DEVICE_TYPE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_id(UPDATED_HUB_ID);
        pATIENT_VEST_DEVICE_RAW_LOGS.setAir_interface_type(UPDATED_AIR_INTERFACE_TYPE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setCustomer_name(UPDATED_CUSTOMER_NAME);
        pATIENT_VEST_DEVICE_RAW_LOGS.setTimezone(UPDATED_TIMEZONE);
        pATIENT_VEST_DEVICE_RAW_LOGS.setSp_receive_time(UPDATED_SP_RECEIVE_TIME);
        pATIENT_VEST_DEVICE_RAW_LOGS.setHub_receive_time_offset(UPDATED_HUB_RECEIVE_TIME_OFFSET);
        pATIENT_VEST_DEVICE_RAW_LOGS.setCuc_version(UPDATED_CUC_VERSION);
        pATIENT_VEST_DEVICE_RAW_LOGS.setCustomer_id(UPDATED_CUSTOMER_ID);
        pATIENT_VEST_DEVICE_RAW_LOGS.setRaw_message(UPDATED_RAW_MESSAGE);
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(put("/api/pATIENT_VEST_DEVICE_RAW_LOGSs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(pATIENT_VEST_DEVICE_RAW_LOGS)))
                .andExpect(status().isOk());

        // Validate the PATIENT_VEST_DEVICE_RAW_LOGS in the database
        List<PATIENT_VEST_DEVICE_RAW_LOGS> pATIENT_VEST_DEVICE_RAW_LOGSs = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll();
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSs).hasSize(databaseSizeBeforeUpdate);
        PATIENT_VEST_DEVICE_RAW_LOGS testPATIENT_VEST_DEVICE_RAW_LOGS = pATIENT_VEST_DEVICE_RAW_LOGSs.get(pATIENT_VEST_DEVICE_RAW_LOGSs.size() - 1);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getHub_receive_time().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_HUB_RECEIVE_TIME);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_address()).isEqualTo(UPDATED_DEVICE_ADDRESS);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_model_type()).isEqualTo(UPDATED_DEVICE_MODEL_TYPE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_data()).isEqualTo(UPDATED_DEVICE_DATA);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_serial_number()).isEqualTo(UPDATED_DEVICE_SERIAL_NUMBER);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getDevice_type()).isEqualTo(UPDATED_DEVICE_TYPE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getHub_id()).isEqualTo(UPDATED_HUB_ID);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getAir_interface_type()).isEqualTo(UPDATED_AIR_INTERFACE_TYPE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getCustomer_name()).isEqualTo(UPDATED_CUSTOMER_NAME);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getTimezone()).isEqualTo(UPDATED_TIMEZONE);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getSp_receive_time()).isEqualTo(UPDATED_SP_RECEIVE_TIME);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getHub_receive_time_offset()).isEqualTo(UPDATED_HUB_RECEIVE_TIME_OFFSET);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getCuc_version()).isEqualTo(UPDATED_CUC_VERSION);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getCustomer_id()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testPATIENT_VEST_DEVICE_RAW_LOGS.getRaw_message()).isEqualTo(UPDATED_RAW_MESSAGE);
    }

    @Test
    @Transactional
    public void deletePATIENT_VEST_DEVICE_RAW_LOGS() throws Exception {
        // Initialize the database
        pATIENT_VEST_DEVICE_RAW_LOGSRepository.saveAndFlush(pATIENT_VEST_DEVICE_RAW_LOGS);

		int databaseSizeBeforeDelete = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll().size();

        // Get the pATIENT_VEST_DEVICE_RAW_LOGS
        restPATIENT_VEST_DEVICE_RAW_LOGSMockMvc.perform(delete("/api/pATIENT_VEST_DEVICE_RAW_LOGSs/{id}", pATIENT_VEST_DEVICE_RAW_LOGS.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<PATIENT_VEST_DEVICE_RAW_LOGS> pATIENT_VEST_DEVICE_RAW_LOGSs = pATIENT_VEST_DEVICE_RAW_LOGSRepository.findAll();
        assertThat(pATIENT_VEST_DEVICE_RAW_LOGSs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
