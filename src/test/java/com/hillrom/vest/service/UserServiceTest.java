package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.util.RandomUtil;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class UserServiceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Inject
    private UserSecurityQuestionService userSecurityQuestionService;
    
    @Test
    public void assertThatUserMustExistToResetPassword() {
        
        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();

        maybeUser = userService.requestPasswordReset("admin@localhost");
        assertThat(maybeUser.isPresent()).isTrue();

        assertThat(maybeUser.get().getEmail()).isEqualTo("admin@localhost");
        assertThat(maybeUser.get().getResetDate()).isNotNull();
        assertThat(maybeUser.get().getResetKey()).isNotNull();
        
    }

    @Test
    public void assertThatOnlyActivatedUserCanRequestPasswordReset() {
        User user = userService.createUserInformation("johndoe", "John", "Doe", "john.doe@localhost", "en-US");
        Optional<User> maybeUser = userService.requestPasswordReset("john.doe@localhost");
        assertThat(maybeUser.isPresent()).isFalse();
        userRepository.delete(user);
    }

    @Test
    public void assertThatResetKeyMustNotBeOlderThan24Hours() {
        
        User user = userService.createUserInformation("johndoe", "John", "Doe", "john.doe@localhost", "en-US");

        DateTime daysAgo = DateTime.now().minusHours(25);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);

        user = userRepository.save(user);

        userSecurityQuestionService.save(user.getId(), 2L, "test");
        
        Map<String,String> paramsMap = new HashMap<String,String>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", "2");
        paramsMap.put("passsword", "password");
        
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("ERROR")).isTrue();

        userRepository.delete(user);
        
    }

    @Test
    public void assertThatResetKeyMustBeValid() {
        
        User user = userService.createUserInformation("johndoe", "John", "Doe", "john.doe@localhost", "en-US");

        DateTime daysAgo = DateTime.now().minusHours(25);
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey("1234");

        userRepository.save(user);
        userSecurityQuestionService.save(user.getId(), 2L, "test");
        
        Map<String,String> paramsMap = new HashMap<String,String>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", "2");
        paramsMap.put("passsword", "password");
        
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("ERROR")).isTrue();


        userRepository.delete(user);
        
    }

    @Test
    public void assertThatUserCanResetPassword() {
        
        User user = userService.createUserInformation("johndoe", "John", "Doe", "john.doe@localhost", "en-US");

        String oldPassword = user.getPassword();

        DateTime daysAgo = DateTime.now().minusHours(2);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);

        userRepository.save(user);
        userSecurityQuestionService.save(user.getId(), 2L, "test");
        
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", "2");
        paramsMap.put("passsword", "password");
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("email")).isTrue();
        
        userRepository.delete(user);
        
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
        paramsMap.put("questionId", "2");
        paramsMap.put("passsword", "password");
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("email")).isTrue();
        
    }
    
    @Test
    public void assertThatUserMustProvideCorrectAnswerSecurityQuestion() {
        
    	User user = userService.createUserInformation("johndoe", "John", "Doe", "john.doe@localhost", "en-US");

        String oldPassword = user.getPassword();

        DateTime daysAgo = DateTime.now().minusHours(2);
        String resetKey = RandomUtil.generateResetKey();
        user.setActivated(true);
        user.setResetDate(daysAgo);
        user.setResetKey(resetKey);

        userRepository.save(user);
        userSecurityQuestionService.save(user.getId(), 2L, "test");
        
        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("key", user.getResetKey());
        paramsMap.put("answer", "test");
        paramsMap.put("questionId", "2");
        paramsMap.put("passsword", "password");
        JSONObject jsonObject = userService.completePasswordReset(paramsMap);
        assertThat(jsonObject.containsKey("ERROR")).isTrue();
        
        userRepository.delete(user);
        
    }
}
