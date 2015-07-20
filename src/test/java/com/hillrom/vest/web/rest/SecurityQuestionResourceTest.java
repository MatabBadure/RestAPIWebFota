package com.hillrom.vest.web.rest;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.repository.SecurityQuestionRepository;

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
 * Test class for the SecurityQuestionResource REST controller.
 *
 * @see SecurityQuestionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SecurityQuestionResourceTest {

    private static final String DEFAULT_QUESTION = "SAMPLE_TEXT";
    private static final String UPDATED_QUESTION = "UPDATED_TEXT";
    private static final String DEFAULT_ANSWER = "SAMPLE_TEXT";

    @Inject
    private SecurityQuestionRepository securityQuestionRepository;

    private MockMvc restSecurityQuestionMockMvc;

    private SecurityQuestion securityQuestion;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SecurityQuestionResource securityQuestionResource = new SecurityQuestionResource();
        ReflectionTestUtils.setField(securityQuestionResource, "securityQuestionRepository", securityQuestionRepository);
        this.restSecurityQuestionMockMvc = MockMvcBuilders.standaloneSetup(securityQuestionResource).build();
    }

    @Before
    public void initTest() {
        securityQuestion = new SecurityQuestion();
        securityQuestion.setQuestion(DEFAULT_QUESTION);
    }

    @Test
    @Transactional
    public void createSecurityQuestion() throws Exception {
        int databaseSizeBeforeCreate = securityQuestionRepository.findAll().size();

        // Create the SecurityQuestion
        restSecurityQuestionMockMvc.perform(post("/api/securityQuestions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securityQuestion)))
                .andExpect(status().isCreated());

        // Validate the SecurityQuestion in the database
        List<SecurityQuestion> securityQuestions = securityQuestionRepository.findAll();
        assertThat(securityQuestions).hasSize(databaseSizeBeforeCreate + 1);
        SecurityQuestion testSecurityQuestion = securityQuestions.get(securityQuestions.size() - 1);
        assertThat(testSecurityQuestion.getQuestion()).isEqualTo(DEFAULT_QUESTION);
    }

    @Test
    @Transactional
    public void getAllSecurityQuestions() throws Exception {
        // Initialize the database
        securityQuestionRepository.saveAndFlush(securityQuestion);

        // Get all the securityQuestions
        restSecurityQuestionMockMvc.perform(get("/api/securityQuestions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(securityQuestion.getId().intValue())))
                .andExpect(jsonPath("$.[*].question").value(hasItem(DEFAULT_QUESTION.toString())))
                .andExpect(jsonPath("$.[*].answer").value(hasItem(DEFAULT_ANSWER.toString())));
    }

    @Test
    @Transactional
    public void getSecurityQuestion() throws Exception {
        // Initialize the database
        securityQuestionRepository.saveAndFlush(securityQuestion);

        // Get the securityQuestion
        restSecurityQuestionMockMvc.perform(get("/api/securityQuestions/{id}", securityQuestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(securityQuestion.getId().intValue()))
            .andExpect(jsonPath("$.question").value(DEFAULT_QUESTION.toString()))
            .andExpect(jsonPath("$.answer").value(DEFAULT_ANSWER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSecurityQuestion() throws Exception {
        // Get the securityQuestion
        restSecurityQuestionMockMvc.perform(get("/api/securityQuestions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSecurityQuestion() throws Exception {
        // Initialize the database
        securityQuestionRepository.saveAndFlush(securityQuestion);

		int databaseSizeBeforeUpdate = securityQuestionRepository.findAll().size();

        // Update the securityQuestion
        securityQuestion.setQuestion(UPDATED_QUESTION);
        restSecurityQuestionMockMvc.perform(put("/api/securityQuestions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(securityQuestion)))
                .andExpect(status().isOk());

        // Validate the SecurityQuestion in the database
        List<SecurityQuestion> securityQuestions = securityQuestionRepository.findAll();
        assertThat(securityQuestions).hasSize(databaseSizeBeforeUpdate);
        SecurityQuestion testSecurityQuestion = securityQuestions.get(securityQuestions.size() - 1);
        assertThat(testSecurityQuestion.getQuestion()).isEqualTo(UPDATED_QUESTION);
    }

    @Test
    @Transactional
    public void deleteSecurityQuestion() throws Exception {
        // Initialize the database
        securityQuestionRepository.saveAndFlush(securityQuestion);

		int databaseSizeBeforeDelete = securityQuestionRepository.findAll().size();

        // Get the securityQuestion
        restSecurityQuestionMockMvc.perform(delete("/api/securityQuestions/{id}", securityQuestion.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SecurityQuestion> securityQuestions = securityQuestionRepository.findAll();
        assertThat(securityQuestions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
