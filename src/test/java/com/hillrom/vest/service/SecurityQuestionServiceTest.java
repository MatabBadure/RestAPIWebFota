package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.repository.SecurityQuestionRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class SecurityQuestionServiceTest {
	
	private static final String SECURITY_QUESTION = "What is your favorite programming language?";
	
	@Inject
	private SecurityQuestionRepository questionRepository;
	
	@Test
	public void createSecQuestionSuccessfully(){
		SecurityQuestion question = new SecurityQuestion();
		question.setQuestion(SECURITY_QUESTION);
		questionRepository.save(question);
		assertThat(questionRepository.save(question));
		questionRepository.delete(question);
	}
	
	@Test(expected = DataIntegrityViolationException.class)
	public void failToCreateSecurityQuestion(){
		SecurityQuestion question = new SecurityQuestion();
		questionRepository.save(question);
	}
	
	@Test
	public void findSecurityQuestionSuccessfully(){
		SecurityQuestion question = new SecurityQuestion();
		question.setQuestion(SECURITY_QUESTION);
		question = questionRepository.save(question);
		assertThat(questionRepository.findOne(question.getId()));
		questionRepository.delete(question);
	}
	
	@Test
	public void updateSecurityQuestionFailure(){
		SecurityQuestion question = new SecurityQuestion();
		question.setQuestion(SECURITY_QUESTION);
		question = questionRepository.save(question);
		assertThat(questionRepository.findOne(question.getId()));
		question.setQuestion(null);
		questionRepository.save(question);
		questionRepository.delete(question);
	}
	
	@Test
	public void updateSecurityQuestionSuccess(){
		SecurityQuestion question = new SecurityQuestion();
		question.setQuestion(SECURITY_QUESTION);
		question = questionRepository.save(question);
		assertThat(questionRepository.findOne(question.getId()));
		question.setQuestion(SECURITY_QUESTION);
		questionRepository.save(question);
		questionRepository.delete(question);
	}

}
