package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.UserLoginTokenRepository;

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
 * Test class for the UserLoginTokenResource REST controller.
 *
 * @see UserLoginTokenResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class UserLoginTokenResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final DateTime DEFAULT_CREATED_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_CREATED_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_CREATED_TIME_STR = dateTimeFormatter.print(DEFAULT_CREATED_TIME);

    @Inject
    private UserLoginTokenRepository userLoginTokenRepository;

    private MockMvc restUserLoginTokenMockMvc;

    private UserLoginToken userLoginToken;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserLoginTokenResource userLoginTokenResource = new UserLoginTokenResource();
        ReflectionTestUtils.setField(userLoginTokenResource, "userLoginTokenRepository", userLoginTokenRepository);
        this.restUserLoginTokenMockMvc = MockMvcBuilders.standaloneSetup(userLoginTokenResource).build();
    }

    @Before
    public void initTest() {
        userLoginToken = new UserLoginToken();
        userLoginToken.setCreatedTime(DEFAULT_CREATED_TIME);
    }

    @Test
    @Transactional
    public void createUserLoginToken() throws Exception {
        int databaseSizeBeforeCreate = userLoginTokenRepository.findAll().size();

        // Create the UserLoginToken
        restUserLoginTokenMockMvc.perform(post("/api/userLoginTokens")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userLoginToken)))
                .andExpect(status().isCreated());

        // Validate the UserLoginToken in the database
        List<UserLoginToken> userLoginTokens = userLoginTokenRepository.findAll();
        assertThat(userLoginTokens).hasSize(databaseSizeBeforeCreate + 1);
        UserLoginToken testUserLoginToken = userLoginTokens.get(userLoginTokens.size() - 1);
        assertThat(testUserLoginToken.getCreatedTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_CREATED_TIME);
    }

    @Test
    @Transactional
    public void getAllUserLoginTokens() throws Exception {
        // Initialize the database
        userLoginTokenRepository.saveAndFlush(userLoginToken);

        // Get all the userLoginTokens
        restUserLoginTokenMockMvc.perform(get("/api/userLoginTokens"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(userLoginToken.getId().intValue())))
                .andExpect(jsonPath("$.[*].createdTime").value(hasItem(DEFAULT_CREATED_TIME_STR)));
    }

    @Test
    @Transactional
    public void getUserLoginToken() throws Exception {
        // Initialize the database
        userLoginTokenRepository.saveAndFlush(userLoginToken);

        // Get the userLoginToken
        restUserLoginTokenMockMvc.perform(get("/api/userLoginTokens/{id}", userLoginToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(userLoginToken.getId().intValue()))
            .andExpect(jsonPath("$.createdTime").value(DEFAULT_CREATED_TIME_STR));
    }

    @Test
    @Transactional
    public void getNonExistingUserLoginToken() throws Exception {
        // Get the userLoginToken
        restUserLoginTokenMockMvc.perform(get("/api/userLoginTokens/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserLoginToken() throws Exception {
        // Initialize the database
        userLoginTokenRepository.saveAndFlush(userLoginToken);

		int databaseSizeBeforeUpdate = userLoginTokenRepository.findAll().size();

        // Update the userLoginToken
        userLoginToken.setCreatedTime(UPDATED_CREATED_TIME);
        restUserLoginTokenMockMvc.perform(put("/api/userLoginTokens")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userLoginToken)))
                .andExpect(status().isOk());

        // Validate the UserLoginToken in the database
        List<UserLoginToken> userLoginTokens = userLoginTokenRepository.findAll();
        assertThat(userLoginTokens).hasSize(databaseSizeBeforeUpdate);
        UserLoginToken testUserLoginToken = userLoginTokens.get(userLoginTokens.size() - 1);
        assertThat(testUserLoginToken.getCreatedTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_CREATED_TIME);
    }

    @Test
    @Transactional
    public void deleteUserLoginToken() throws Exception {
        // Initialize the database
        userLoginTokenRepository.saveAndFlush(userLoginToken);

		int databaseSizeBeforeDelete = userLoginTokenRepository.findAll().size();

        // Get the userLoginToken
        restUserLoginTokenMockMvc.perform(delete("/api/userLoginTokens/{id}", userLoginToken.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<UserLoginToken> userLoginTokens = userLoginTokenRepository.findAll();
        assertThat(userLoginTokens).hasSize(databaseSizeBeforeDelete - 1);
    }
}
