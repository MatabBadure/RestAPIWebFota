package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserSecurityQuestion;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.SecurityQuestionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSecurityQuestionRepository;
import com.hillrom.vest.security.AuthoritiesConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class UserSecurityQuestionServiceTest {
	
	private static final String ANSWER = "JAVA";

	private static final String PASSWORD = "password";
	
	private static final String SECURITY_QUESTION = "What is your favorite programming language?";
	
	private User user ;
	private SecurityQuestion question;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private SecurityQuestionRepository questionRepository;
	
	@Inject
	private UserSecurityQuestionRepository securityQuestionRepository;
	
	@Inject
	private UserSecurityQuestionService securityQuestionService;
	
	@Inject
	private AuthorityRepository authorityRepository;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Before
	public void setup(){
		user = createUser();
		question = createSecurityQuestion();
	}
	
	@After
	public void destroy(){
		userRepository.delete(user);
		questionRepository.delete(question);
	}
	
	@Test
	public void createUserSecurityQuestionSuccessfully(){
		Optional<UserSecurityQuestion> mayBeSaved = securityQuestionService.save(user.getId(), question.getId(), ANSWER);
		assertThat(mayBeSaved.isPresent()).isTrue();
		assertThat(mayBeSaved.get().getAnswer()).isEqualTo(ANSWER);
	}
	
	@Test
	public void failCreateUserSecurityQuestion(){
		Optional<UserSecurityQuestion> mayBeSaved = securityQuestionService.save(user.getId(), null, ANSWER);
		assertThat(mayBeSaved.isPresent()).isFalse();
	}
	
	@Test
	public void updateUserSecurityQuestionSuccessfully(){
		Optional<UserSecurityQuestion> mayBeSaved = securityQuestionService.save(user.getId(), question.getId(), ANSWER);
		mayBeSaved = securityQuestionService.findOneByUserIdAndQuestionId(user.getId(), question.getId());
		mayBeSaved.get().setAnswer(PASSWORD);
		UserSecurityQuestion updated = securityQuestionRepository.save(mayBeSaved.get());
		assertThat(updated).isNotNull();
		assertThat(updated.getAnswer()).isEqualTo(PASSWORD);
	}
	
	@Test
	public void failUpdateUserSecurityQuestion(){
		Optional<UserSecurityQuestion> updated = securityQuestionService.update(user.getId(), question.getId(), null);
		assertThat(updated.isPresent()).isFalse();
	}
	
	private User createUser() {
		User user = new User();
        user.setActivated(true);
        user.setDeleted(false);
        user.setEmail("noname@123.com");
        user.setPassword(passwordEncoder.encode(PASSWORD));
        user.setFirstName("fname");
        user.setMiddleName("mname");
        user.setLangKey("en");
        user.setActivationKey(null);
        user.setLastLoggedInAt(DateTime.now());
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
        user.getAuthorities().add(authority);
        
        return userRepository.save(user);
	}
	
	private SecurityQuestion createSecurityQuestion(){
		SecurityQuestion question = new SecurityQuestion();
		question.setQuestion(SECURITY_QUESTION);
		return questionRepository.save(question);
	}
}
