package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.UserExtensionRepository;

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


/**
 * Test class for the UserExtensionResource REST controller.
 *
 * @see UserExtensionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class UserExtensionResourceTest {

    private static final String DEFAULT_SPECIALITY = "SAMPLE_TEXT";
    private static final String UPDATED_SPECIALITY = "UPDATED_TEXT";
    private static final String DEFAULT_CREDENTIALS = "SAMPLE_TEXT";
    private static final String UPDATED_CREDENTIALS = "UPDATED_TEXT";

    @Inject
    private UserExtensionRepository userExtensionRepository;

    private MockMvc restUserExtensionMockMvc;

    private UserExtension userExtension;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserExtensionResource userExtensionResource = new UserExtensionResource();
        ReflectionTestUtils.setField(userExtensionResource, "userExtensionRepository", userExtensionRepository);
        this.restUserExtensionMockMvc = MockMvcBuilders.standaloneSetup(userExtensionResource).build();
    }

    @Before
    public void initTest() {
        userExtension = new UserExtension();
        userExtension.setSpeciality(DEFAULT_SPECIALITY);
        userExtension.setCredentials(DEFAULT_CREDENTIALS);
    }

    @Test
    @Transactional
    public void createUserExtension() throws Exception {
        int databaseSizeBeforeCreate = userExtensionRepository.findAll().size();

        // Create the UserExtension
        restUserExtensionMockMvc.perform(post("/api/userExtensions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userExtension)))
                .andExpect(status().isCreated());

        // Validate the UserExtension in the database
        List<UserExtension> userExtensions = userExtensionRepository.findAll();
        assertThat(userExtensions).hasSize(databaseSizeBeforeCreate + 1);
        UserExtension testUserExtension = userExtensions.get(userExtensions.size() - 1);
        assertThat(testUserExtension.getSpeciality()).isEqualTo(DEFAULT_SPECIALITY);
        assertThat(testUserExtension.getCredentials()).isEqualTo(DEFAULT_CREDENTIALS);
    }

    @Test
    @Transactional
    public void getAllUserExtensions() throws Exception {
        // Initialize the database
        userExtensionRepository.saveAndFlush(userExtension);

        // Get all the userExtensions
        restUserExtensionMockMvc.perform(get("/api/userExtensions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(userExtension.getId().intValue())))
                .andExpect(jsonPath("$.[*].speciality").value(hasItem(DEFAULT_SPECIALITY.toString())))
                .andExpect(jsonPath("$.[*].credentials").value(hasItem(DEFAULT_CREDENTIALS.toString())));
    }

    @Test
    @Transactional
    public void getUserExtension() throws Exception {
        // Initialize the database
        userExtensionRepository.saveAndFlush(userExtension);

        // Get the userExtension
        restUserExtensionMockMvc.perform(get("/api/userExtensions/{id}", userExtension.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(userExtension.getId().intValue()))
            .andExpect(jsonPath("$.speciality").value(DEFAULT_SPECIALITY.toString()))
            .andExpect(jsonPath("$.credentials").value(DEFAULT_CREDENTIALS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserExtension() throws Exception {
        // Get the userExtension
        restUserExtensionMockMvc.perform(get("/api/userExtensions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserExtension() throws Exception {
        // Initialize the database
        userExtensionRepository.saveAndFlush(userExtension);

		int databaseSizeBeforeUpdate = userExtensionRepository.findAll().size();

        // Update the userExtension
        userExtension.setSpeciality(UPDATED_SPECIALITY);
        userExtension.setCredentials(UPDATED_CREDENTIALS);
        restUserExtensionMockMvc.perform(put("/api/userExtensions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userExtension)))
                .andExpect(status().isOk());

        // Validate the UserExtension in the database
        List<UserExtension> userExtensions = userExtensionRepository.findAll();
        assertThat(userExtensions).hasSize(databaseSizeBeforeUpdate);
        UserExtension testUserExtension = userExtensions.get(userExtensions.size() - 1);
        assertThat(testUserExtension.getSpeciality()).isEqualTo(UPDATED_SPECIALITY);
        assertThat(testUserExtension.getCredentials()).isEqualTo(UPDATED_CREDENTIALS);
    }

    @Test
    @Transactional
    public void deleteUserExtension() throws Exception {
        // Initialize the database
        userExtensionRepository.saveAndFlush(userExtension);

		int databaseSizeBeforeDelete = userExtensionRepository.findAll().size();

        // Get the userExtension
        restUserExtensionMockMvc.perform(delete("/api/userExtensions/{id}", userExtension.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<UserExtension> userExtensions = userExtensionRepository.findAll();
        assertThat(userExtensions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
