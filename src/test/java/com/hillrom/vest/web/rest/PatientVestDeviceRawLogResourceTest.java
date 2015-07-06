package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.repository.PatientVestDeviceRawLogRepository;

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
 * Test class for the PatientVestDeviceRawLogResource REST controller.
 *
 * @see PatientVestDeviceRawLogResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PatientVestDeviceRawLogResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_DEVICE_MODEL_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_MODEL_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_DATA = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_DATA = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_SERIAL_NO = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_SERIAL_NO = "UPDATED_TEXT";
    private static final String DEFAULT_DEVICE_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_HUB_ID = "SAMPLE_TEXT";
    private static final String UPDATED_HUB_ID = "UPDATED_TEXT";
    private static final String DEFAULT_AIR_INTERFACE_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_AIR_INTERFACE_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_CUSTOMER_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_CUSTOMER_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_TIME_ZONE = "SAMPLE_TEXT";
    private static final String UPDATED_TIME_ZONE = "UPDATED_TEXT";

    private static final DateTime DEFAULT_SP_RECEIVE_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_SP_RECEIVE_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_SP_RECEIVE_TIME_STR = dateTimeFormatter.print(DEFAULT_SP_RECEIVE_TIME);

    private static final DateTime DEFAULT_HUB_RECEIVE_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_HUB_RECEIVE_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_HUB_RECEIVE_TIME_STR = dateTimeFormatter.print(DEFAULT_HUB_RECEIVE_TIME);
    private static final String DEFAULT_DEVICE_ADDRESS = "SAMPLE_TEXT";
    private static final String UPDATED_DEVICE_ADDRESS = "UPDATED_TEXT";

    private static final Integer DEFAULT_HUB_RECEIVE_TIME_OFFSET = 0;
    private static final Integer UPDATED_HUB_RECEIVE_TIME_OFFSET = 1;
    private static final String DEFAULT_CUC_VERSION = "SAMPLE_TEXT";
    private static final String UPDATED_CUC_VERSION = "UPDATED_TEXT";
    private static final String DEFAULT_CUSTOMER_ID = "SAMPLE_TEXT";
    private static final String UPDATED_CUSTOMER_ID = "UPDATED_TEXT";
    private static final String DEFAULT_RAW_MESSAGE = "SAMPLE_TEXT";
    private static final String UPDATED_RAW_MESSAGE = "UPDATED_TEXT";
    private static final String DEFAULT_RAW_HEXA_DATA = "SAMPLE_TEXT";
    private static final String UPDATED_RAW_HEXA_DATA = "UPDATED_TEXT";

    @Inject
    private PatientVestDeviceRawLogRepository patientVestDeviceRawLogRepository;

    private MockMvc restPatientVestDeviceRawLogMockMvc;

    private PatientVestDeviceRawLog patientVestDeviceRawLog;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PatientVestDeviceRawLogResource patientVestDeviceRawLogResource = new PatientVestDeviceRawLogResource();
        ReflectionTestUtils.setField(patientVestDeviceRawLogResource, "patientVestDeviceRawLogRepository", patientVestDeviceRawLogRepository);
        this.restPatientVestDeviceRawLogMockMvc = MockMvcBuilders.standaloneSetup(patientVestDeviceRawLogResource).build();
    }

    @Before
    public void initTest() {
        patientVestDeviceRawLog = new PatientVestDeviceRawLog();
        patientVestDeviceRawLog.setDeviceModelType(DEFAULT_DEVICE_MODEL_TYPE);
        patientVestDeviceRawLog.setDeviceData(DEFAULT_DEVICE_DATA);
        patientVestDeviceRawLog.setDeviceSerialNo(DEFAULT_DEVICE_SERIAL_NO);
        patientVestDeviceRawLog.setDeviceType(DEFAULT_DEVICE_TYPE);
        patientVestDeviceRawLog.setHubId(DEFAULT_HUB_ID);
        patientVestDeviceRawLog.setAirInterfaceType(DEFAULT_AIR_INTERFACE_TYPE);
        patientVestDeviceRawLog.setCustomerName(DEFAULT_CUSTOMER_NAME);
        patientVestDeviceRawLog.setTimeZone(DEFAULT_TIME_ZONE);
        patientVestDeviceRawLog.setSpReceiveTime(DEFAULT_SP_RECEIVE_TIME);
        patientVestDeviceRawLog.setHubReceiveTime(DEFAULT_HUB_RECEIVE_TIME);
        patientVestDeviceRawLog.setDeviceAddress(DEFAULT_DEVICE_ADDRESS);
        patientVestDeviceRawLog.setHubReceiveTimeOffset(DEFAULT_HUB_RECEIVE_TIME_OFFSET);
        patientVestDeviceRawLog.setCucVersion(DEFAULT_CUC_VERSION);
        patientVestDeviceRawLog.setCustomerId(DEFAULT_CUSTOMER_ID);
        patientVestDeviceRawLog.setRawMessage(DEFAULT_RAW_MESSAGE);
        patientVestDeviceRawLog.setRawHexaData(DEFAULT_RAW_HEXA_DATA);
    }

    @Test
    @Transactional
    public void createPatientVestDeviceRawLog() throws Exception {
        int databaseSizeBeforeCreate = patientVestDeviceRawLogRepository.findAll().size();

        // Create the PatientVestDeviceRawLog
        restPatientVestDeviceRawLogMockMvc.perform(post("/api/patientVestDeviceRawLogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(patientVestDeviceRawLog)))
                .andExpect(status().isCreated());

        // Validate the PatientVestDeviceRawLog in the database
        List<PatientVestDeviceRawLog> patientVestDeviceRawLogs = patientVestDeviceRawLogRepository.findAll();
        assertThat(patientVestDeviceRawLogs).hasSize(databaseSizeBeforeCreate + 1);
        PatientVestDeviceRawLog testPatientVestDeviceRawLog = patientVestDeviceRawLogs.get(patientVestDeviceRawLogs.size() - 1);
        assertThat(testPatientVestDeviceRawLog.getDeviceModelType()).isEqualTo(DEFAULT_DEVICE_MODEL_TYPE);
        assertThat(testPatientVestDeviceRawLog.getDeviceData()).isEqualTo(DEFAULT_DEVICE_DATA);
        assertThat(testPatientVestDeviceRawLog.getDeviceSerialNo()).isEqualTo(DEFAULT_DEVICE_SERIAL_NO);
        assertThat(testPatientVestDeviceRawLog.getDeviceType()).isEqualTo(DEFAULT_DEVICE_TYPE);
        assertThat(testPatientVestDeviceRawLog.getHubId()).isEqualTo(DEFAULT_HUB_ID);
        assertThat(testPatientVestDeviceRawLog.getAirInterfaceType()).isEqualTo(DEFAULT_AIR_INTERFACE_TYPE);
        assertThat(testPatientVestDeviceRawLog.getCustomerName()).isEqualTo(DEFAULT_CUSTOMER_NAME);
        assertThat(testPatientVestDeviceRawLog.getTimeZone()).isEqualTo(DEFAULT_TIME_ZONE);
        assertThat(testPatientVestDeviceRawLog.getSpReceiveTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_SP_RECEIVE_TIME);
        assertThat(testPatientVestDeviceRawLog.getHubReceiveTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_HUB_RECEIVE_TIME);
        assertThat(testPatientVestDeviceRawLog.getDeviceAddress()).isEqualTo(DEFAULT_DEVICE_ADDRESS);
        assertThat(testPatientVestDeviceRawLog.getHubReceiveTimeOffset()).isEqualTo(DEFAULT_HUB_RECEIVE_TIME_OFFSET);
        assertThat(testPatientVestDeviceRawLog.getCucVersion()).isEqualTo(DEFAULT_CUC_VERSION);
        assertThat(testPatientVestDeviceRawLog.getCustomerId()).isEqualTo(DEFAULT_CUSTOMER_ID);
        assertThat(testPatientVestDeviceRawLog.getRawMessage()).isEqualTo(DEFAULT_RAW_MESSAGE);
        assertThat(testPatientVestDeviceRawLog.getRawHexaData()).isEqualTo(DEFAULT_RAW_HEXA_DATA);
    }

    @Test
    @Transactional
    public void getAllPatientVestDeviceRawLogs() throws Exception {
        // Initialize the database
        patientVestDeviceRawLogRepository.saveAndFlush(patientVestDeviceRawLog);

        // Get all the patientVestDeviceRawLogs
        restPatientVestDeviceRawLogMockMvc.perform(get("/api/patientVestDeviceRawLogs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(patientVestDeviceRawLog.getId().intValue())))
                .andExpect(jsonPath("$.[*].deviceModelType").value(hasItem(DEFAULT_DEVICE_MODEL_TYPE.toString())))
                .andExpect(jsonPath("$.[*].deviceData").value(hasItem(DEFAULT_DEVICE_DATA.toString())))
                .andExpect(jsonPath("$.[*].deviceSerialNo").value(hasItem(DEFAULT_DEVICE_SERIAL_NO.toString())))
                .andExpect(jsonPath("$.[*].deviceType").value(hasItem(DEFAULT_DEVICE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].hubId").value(hasItem(DEFAULT_HUB_ID.toString())))
                .andExpect(jsonPath("$.[*].airInterfaceType").value(hasItem(DEFAULT_AIR_INTERFACE_TYPE.toString())))
                .andExpect(jsonPath("$.[*].customerName").value(hasItem(DEFAULT_CUSTOMER_NAME.toString())))
                .andExpect(jsonPath("$.[*].timeZone").value(hasItem(DEFAULT_TIME_ZONE.toString())))
                .andExpect(jsonPath("$.[*].spReceiveTime").value(hasItem(DEFAULT_SP_RECEIVE_TIME_STR)))
                .andExpect(jsonPath("$.[*].hubReceiveTime").value(hasItem(DEFAULT_HUB_RECEIVE_TIME_STR)))
                .andExpect(jsonPath("$.[*].deviceAddress").value(hasItem(DEFAULT_DEVICE_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].hubReceiveTimeOffset").value(hasItem(DEFAULT_HUB_RECEIVE_TIME_OFFSET)))
                .andExpect(jsonPath("$.[*].cucVersion").value(hasItem(DEFAULT_CUC_VERSION.toString())))
                .andExpect(jsonPath("$.[*].customerId").value(hasItem(DEFAULT_CUSTOMER_ID.toString())))
                .andExpect(jsonPath("$.[*].rawMessage").value(hasItem(DEFAULT_RAW_MESSAGE.toString())))
                .andExpect(jsonPath("$.[*].rawHexaData").value(hasItem(DEFAULT_RAW_HEXA_DATA.toString())));
    }

    @Test
    @Transactional
    public void getPatientVestDeviceRawLog() throws Exception {
        // Initialize the database
        patientVestDeviceRawLogRepository.saveAndFlush(patientVestDeviceRawLog);

        // Get the patientVestDeviceRawLog
        restPatientVestDeviceRawLogMockMvc.perform(get("/api/patientVestDeviceRawLogs/{id}", patientVestDeviceRawLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(patientVestDeviceRawLog.getId().intValue()))
            .andExpect(jsonPath("$.deviceModelType").value(DEFAULT_DEVICE_MODEL_TYPE.toString()))
            .andExpect(jsonPath("$.deviceData").value(DEFAULT_DEVICE_DATA.toString()))
            .andExpect(jsonPath("$.deviceSerialNo").value(DEFAULT_DEVICE_SERIAL_NO.toString()))
            .andExpect(jsonPath("$.deviceType").value(DEFAULT_DEVICE_TYPE.toString()))
            .andExpect(jsonPath("$.hubId").value(DEFAULT_HUB_ID.toString()))
            .andExpect(jsonPath("$.airInterfaceType").value(DEFAULT_AIR_INTERFACE_TYPE.toString()))
            .andExpect(jsonPath("$.customerName").value(DEFAULT_CUSTOMER_NAME.toString()))
            .andExpect(jsonPath("$.timeZone").value(DEFAULT_TIME_ZONE.toString()))
            .andExpect(jsonPath("$.spReceiveTime").value(DEFAULT_SP_RECEIVE_TIME_STR))
            .andExpect(jsonPath("$.hubReceiveTime").value(DEFAULT_HUB_RECEIVE_TIME_STR))
            .andExpect(jsonPath("$.deviceAddress").value(DEFAULT_DEVICE_ADDRESS.toString()))
            .andExpect(jsonPath("$.hubReceiveTimeOffset").value(DEFAULT_HUB_RECEIVE_TIME_OFFSET))
            .andExpect(jsonPath("$.cucVersion").value(DEFAULT_CUC_VERSION.toString()))
            .andExpect(jsonPath("$.customerId").value(DEFAULT_CUSTOMER_ID.toString()))
            .andExpect(jsonPath("$.rawMessage").value(DEFAULT_RAW_MESSAGE.toString()))
            .andExpect(jsonPath("$.rawHexaData").value(DEFAULT_RAW_HEXA_DATA.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPatientVestDeviceRawLog() throws Exception {
        // Get the patientVestDeviceRawLog
        restPatientVestDeviceRawLogMockMvc.perform(get("/api/patientVestDeviceRawLogs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePatientVestDeviceRawLog() throws Exception {
        // Initialize the database
        patientVestDeviceRawLogRepository.saveAndFlush(patientVestDeviceRawLog);

		int databaseSizeBeforeUpdate = patientVestDeviceRawLogRepository.findAll().size();

        // Update the patientVestDeviceRawLog
        patientVestDeviceRawLog.setDeviceModelType(UPDATED_DEVICE_MODEL_TYPE);
        patientVestDeviceRawLog.setDeviceData(UPDATED_DEVICE_DATA);
        patientVestDeviceRawLog.setDeviceSerialNo(UPDATED_DEVICE_SERIAL_NO);
        patientVestDeviceRawLog.setDeviceType(UPDATED_DEVICE_TYPE);
        patientVestDeviceRawLog.setHubId(UPDATED_HUB_ID);
        patientVestDeviceRawLog.setAirInterfaceType(UPDATED_AIR_INTERFACE_TYPE);
        patientVestDeviceRawLog.setCustomerName(UPDATED_CUSTOMER_NAME);
        patientVestDeviceRawLog.setTimeZone(UPDATED_TIME_ZONE);
        patientVestDeviceRawLog.setSpReceiveTime(UPDATED_SP_RECEIVE_TIME);
        patientVestDeviceRawLog.setHubReceiveTime(UPDATED_HUB_RECEIVE_TIME);
        patientVestDeviceRawLog.setDeviceAddress(UPDATED_DEVICE_ADDRESS);
        patientVestDeviceRawLog.setHubReceiveTimeOffset(UPDATED_HUB_RECEIVE_TIME_OFFSET);
        patientVestDeviceRawLog.setCucVersion(UPDATED_CUC_VERSION);
        patientVestDeviceRawLog.setCustomerId(UPDATED_CUSTOMER_ID);
        patientVestDeviceRawLog.setRawMessage(UPDATED_RAW_MESSAGE);
        patientVestDeviceRawLog.setRawHexaData(UPDATED_RAW_HEXA_DATA);
        restPatientVestDeviceRawLogMockMvc.perform(put("/api/patientVestDeviceRawLogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(patientVestDeviceRawLog)))
                .andExpect(status().isOk());

        // Validate the PatientVestDeviceRawLog in the database
        List<PatientVestDeviceRawLog> patientVestDeviceRawLogs = patientVestDeviceRawLogRepository.findAll();
        assertThat(patientVestDeviceRawLogs).hasSize(databaseSizeBeforeUpdate);
        PatientVestDeviceRawLog testPatientVestDeviceRawLog = patientVestDeviceRawLogs.get(patientVestDeviceRawLogs.size() - 1);
        assertThat(testPatientVestDeviceRawLog.getDeviceModelType()).isEqualTo(UPDATED_DEVICE_MODEL_TYPE);
        assertThat(testPatientVestDeviceRawLog.getDeviceData()).isEqualTo(UPDATED_DEVICE_DATA);
        assertThat(testPatientVestDeviceRawLog.getDeviceSerialNo()).isEqualTo(UPDATED_DEVICE_SERIAL_NO);
        assertThat(testPatientVestDeviceRawLog.getDeviceType()).isEqualTo(UPDATED_DEVICE_TYPE);
        assertThat(testPatientVestDeviceRawLog.getHubId()).isEqualTo(UPDATED_HUB_ID);
        assertThat(testPatientVestDeviceRawLog.getAirInterfaceType()).isEqualTo(UPDATED_AIR_INTERFACE_TYPE);
        assertThat(testPatientVestDeviceRawLog.getCustomerName()).isEqualTo(UPDATED_CUSTOMER_NAME);
        assertThat(testPatientVestDeviceRawLog.getTimeZone()).isEqualTo(UPDATED_TIME_ZONE);
        assertThat(testPatientVestDeviceRawLog.getSpReceiveTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_SP_RECEIVE_TIME);
        assertThat(testPatientVestDeviceRawLog.getHubReceiveTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_HUB_RECEIVE_TIME);
        assertThat(testPatientVestDeviceRawLog.getDeviceAddress()).isEqualTo(UPDATED_DEVICE_ADDRESS);
        assertThat(testPatientVestDeviceRawLog.getHubReceiveTimeOffset()).isEqualTo(UPDATED_HUB_RECEIVE_TIME_OFFSET);
        assertThat(testPatientVestDeviceRawLog.getCucVersion()).isEqualTo(UPDATED_CUC_VERSION);
        assertThat(testPatientVestDeviceRawLog.getCustomerId()).isEqualTo(UPDATED_CUSTOMER_ID);
        assertThat(testPatientVestDeviceRawLog.getRawMessage()).isEqualTo(UPDATED_RAW_MESSAGE);
        assertThat(testPatientVestDeviceRawLog.getRawHexaData()).isEqualTo(UPDATED_RAW_HEXA_DATA);
    }

    @Test
    @Transactional
    public void deletePatientVestDeviceRawLog() throws Exception {
        // Initialize the database
        patientVestDeviceRawLogRepository.saveAndFlush(patientVestDeviceRawLog);

		int databaseSizeBeforeDelete = patientVestDeviceRawLogRepository.findAll().size();

        // Get the patientVestDeviceRawLog
        restPatientVestDeviceRawLogMockMvc.perform(delete("/api/patientVestDeviceRawLogs/{id}", patientVestDeviceRawLog.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<PatientVestDeviceRawLog> patientVestDeviceRawLogs = patientVestDeviceRawLogRepository.findAll();
        assertThat(patientVestDeviceRawLogs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
