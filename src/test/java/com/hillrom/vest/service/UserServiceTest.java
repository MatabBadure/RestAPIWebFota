/*package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.SecurityQuestionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.util.RandomUtil;

*//**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 *//*
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class UserServiceTest {

    private static final String PASSWORD = "johndoe";

	private static final String USERNAME = "john.doe@localhost";

	@Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;
    
    @Inject
    private AuthenticationService authService;

    @Inject
    private SecurityQuestionRepository sqrepository;
    
    @Inject
    private UserSecurityQuestionService userSecurityQuestionService;
    
    private User user ;
    
    private SecurityQuestion question;
    
    
    @Before
    public void setup(){
    	user = userService.createUserInformation(PASSWORD, "John", "Doe", USERNAME, "en-US");
    	
    	question = new SecurityQuestion();
    	question.setQuestion("what is your pet name?");
        
        question = sqrepository.save(question);
       
    }

    @After
    public void destroy(){
    	userRepository.delete(user);
    	sqrepository.delete(question);
    }
    @Test
    public void assertThatUserMustExistToResetPassword() {
        
        Optional<User> maybeUser = userService.requestPasswordReset(USERNAME);
        assertThat(maybeUser.isPresent()).isFalse();

        maybeUser = userService.requestPasswordReset("admin@localhost.com");
        assertThat(maybeUser.isPresent()).isTrue();

        assertThat(maybeUser.get().getEmail()).isEqualTo("admin@localhost.com");
        assertThat(maybeUser.get().getResetDate()).isNotNull();
        assertThat(maybeUser.get().getResetKey()).isNotNull();
        
    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        Optional<User> maybeUser = userService.requestPasswordReset(USERNAME);
        assertThat(maybeUser.isPresent()).isFalse();
    }

    @Test
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {

        DateTime daysAgo = DateTime.now().minusHours(25);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        
        user = userRepository.save(user);
        
        userSecurityQuestionService.saveOrUpdate(user.getId(), question.getId(), "test");
        
        Map<String,String> paramsMap = new HashMap<String,String>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", question.getId().toString());
        paramsMap.put("password", "password");
        
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("ERROR")).isTrue();
        
    }

    @Test
    public void assertThatResetKeyMustBeValid() {
        
        

        DateTime daysAgo = DateTime.now().minusHours(25);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(RandomUtil.generateResetKey());
        
        userRepository.save(user);
        userSecurityQuestionService.saveOrUpdate(user.getId(), question.getId(), "test");
        
        Map<String,String> paramsMap = new HashMap<String,String>();
        paramsMap.put("key", "1234");
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", question.getId().toString());
        paramsMap.put("password", "password");
        
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
    
        assertThat(jsonObject.containsKey("ERROR")).isTrue();
        
    }

    @Test
    public void assertThatUserCanResetPassword() {

        DateTime daysAgo = DateTime.now().minusHours(2);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        
        userRepository.save(user);
        userSecurityQuestionService.saveOrUpdate(user.getId(), question.getId(), "test");
        
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", question.getId().toString());
        paramsMap.put("password", "password");
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
    
        assertThat(jsonObject.containsKey("email")).isTrue();
        
    }

    @Test
    public void testFindNotActivatedUsersByCreationDateBefore() {
        userService.removeNotActivatedUsers();
        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        assertThat(users).isEmpty();
    }
    
    @Test
    public void assertThatUserMustProvideAnswerSecurityQuestion() {
        
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("key", "1234");
        paramsMap.put("questionId", question.getId().toString());
        paramsMap.put("password", "password");
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("ERROR")).isTrue();
        
    }
    
    @Test
    public void assertThatUserMustProvideCorrectAnswerSecurityQuestion() {

        DateTime daysAgo = DateTime.now();
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);
        
        userRepository.save(user);
        userSecurityQuestionService.saveOrUpdate(user.getId(), question.getId(), "test");
        
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", question.getId().toString());
        paramsMap.put("password", "password");
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
    
        assertThat(jsonObject.containsKey("email")).isTrue();
        
    }
    
    @Test
	public void testFailureChangePasswordDueToMissingPassword() {
		user.setActivated(true);
		user.setLastLoggedInAt(DateTime.now());
		userRepository.save(user);
		
		UserLoginToken authToken = authService.authenticate(USERNAME, PASSWORD);
	    JSONObject jsonObject = userService.changePassword(null);
	    assertThat(jsonObject.containsKey("ERROR")).isTrue();
	}
	
	@Test
	public void testSuccessChangePassword() {
		user.setActivated(true);
		user.setLastLoggedInAt(DateTime.now());
		userRepository.save(user);
		UserLoginToken authToken = authService.authenticate(USERNAME, PASSWORD);
	    JSONObject jsonObject = userService.changePassword("admin");
	    assertThat(jsonObject.containsKey("ERROR")).isFalse();
	}
	
	@Test
    public void assertThatUserCanChangeSecurityQuestion() {

        user.setActivated(true);        
		user.setLastLoggedInAt(DateTime.now());
        userRepository.save(user);
        userSecurityQuestionService.saveOrUpdate(user.getId(), question.getId(), "test");
        UserLoginToken authToken = authService.authenticate(USERNAME, PASSWORD);
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("answer", "test123");
        paramsMap.put("questionId", question.getId().toString());
        JSONObject jsonObject = userService.updateSecurityQuestion(user.getId(), paramsMap);
        assertThat(jsonObject);
        
    }

	@Test
    public void assertThatUserCanNotChangeSecurityQuestion() {

        user.setActivated(true);        
		user.setLastLoggedInAt(DateTime.now());
        userRepository.save(user);
        userSecurityQuestionService.saveOrUpdate(user.getId(), question.getId(), "test");
        UserLoginToken authToken = authService.authenticate(USERNAME, PASSWORD);
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("questionId", question.getId().toString());
        JSONObject jsonObject = userService.updateSecurityQuestion(user.getId(), paramsMap);
        assertThat(jsonObject.containsKey("ERROR")).isTrue();
        
    }
}
*/