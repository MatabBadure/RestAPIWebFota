package com.hillrom.vest.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.web.rest.dto.HillromTeamUserDTO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;
    
    @Inject
    private PatientInfoService patientInfoService;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
        return Optional.empty();
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       Optional<User> opUser = userRepository.findOneByResetKey(key);
       System.out.println("opUser>>> "+opUser);
       return opUser.filter(user -> {
               DateTime oneDayAgo = DateTime.now().minusHours(24);
               return user.getResetDate().isAfter(oneDayAgo.toInstant().getMillis());
           })
           .map(user -> {
               user.setPassword(passwordEncoder.encode(newPassword));
               user.setResetKey(null);
               user.setResetDate(null);
               userRepository.save(user);
               return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
       return userRepository.findOneByEmail(mail)
           .filter(user -> user.getActivated() == true)
           .map(user -> {
               user.setResetKey(RandomUtil.generateResetKey());
               user.setResetDate(DateTime.now());
               userRepository.save(user);
               return user;
           });
    }

    public User createUserInformation(String password, String firstName, String lastName, String email,
                                      String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is not active
        newUser.setActivated(false);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void updateUserInformation(String firstName, String lastName, String email, String langKey) {
        userRepository.findOneByEmail(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setLangKey(langKey);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByEmail(SecurityUtils.getCurrentLogin()).ifPresent(u-> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User currentUser = userRepository.findOneByEmail(SecurityUtils.getCurrentLogin()).get();
        currentUser.getAuthorities().size(); // eagerly load the association
        return currentUser;
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getEmail());
            userRepository.delete(user);
        }
    }
    
    public User createUser(HillromTeamUserDTO hillromTeamUser) {
		User newUser = new User();
		newUser.setFirstName(hillromTeamUser.getFirstName());
		newUser.setLastName(hillromTeamUser.getLastName());
		newUser.setEmail(hillromTeamUser.getEmail());
		newUser.setLangKey(null);
		// new user is not active
		newUser.setActivated(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		newUser.getAuthorities().add(authorityRepository.findOne(hillromTeamUser.getRole()));
		userRepository.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

    public Optional<User> findOneByEmail(String email) {
		return userRepository.findOneByEmail(email);
	}

	public User createUserFromPatientInfo(PatientInfo patientInfo,String encodedPassword) {

		String username = getUsernameAsEmailOrHillromIdFromPatientInfo(patientInfo);

		// If User exists already , then return the existing user.
		Optional<User> existingUser = userRepository.findOneByEmail(username);
		if(existingUser.isPresent()){
			return existingUser.get();
		}
		
		User newUser = new User();
		newUser.setActivated(true);
		newUser.setDeleted(false);
		
		Authority patientAuthority = authorityRepository.findOne(AuthoritiesConstants.PATIENT);
		newUser.getAuthorities().add(patientAuthority);
		
		newUser.setCreatedDate(new DateTime());
		
		newUser.setEmail(username.toLowerCase());
		newUser.setFirstName(patientInfo.getFirstName());
		newUser.setLastName(patientInfo.getLastName());
		newUser.setPassword(encodedPassword);
		newUser.getPatients().add(patientInfo);
		
		User persistedUser = userRepository.save(newUser);
		newUser.setId(persistedUser.getId());
		// Update WebLoginCreated to be true  and user patient association
		updateWebLoginStatusAndUserPatientAssoc(patientInfo, persistedUser);
		return newUser;
		
	}

	/**
	 * @param patientInfo
	 * @return
	 */
	private String getUsernameAsEmailOrHillromIdFromPatientInfo(
			PatientInfo patientInfo) {
		// Set the email to hillromId if email is blank
		String username = null;
		if(StringUtils.isNotBlank(patientInfo.getEmail())){
			username = patientInfo.getEmail();
		}else{
			username = patientInfo.getHillromId();
		}
		return username;
	}	

	/**
	 * @param patientInfo
	 * @param persistedUser
	 */
	private void updateWebLoginStatusAndUserPatientAssoc(
			PatientInfo patientInfo, User persistedUser) {
		patientInfoService.findOneByHillromId(patientInfo.getHillromId()).map(patientUser ->{
			patientUser.setWebLoginCreated(true);
			patientUser.getUsers().add(persistedUser);
			patientInfoService.update(patientUser);
			return patientUser;
		});
	}
	
	public void updateEmailOrPassword(Map<String,String> params){
		String email = params.get("email");
		userRepository.findOneByEmail(SecurityUtils.getCurrentLogin()).ifPresent(u-> {
			if(null != email)
				u.setEmail(email);
			String password = params.get("password");
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            u.setLastLoggedInAt(DateTime.now());
            userRepository.save(u);
            // update email in patientInfo
            if(null != email){
            	PatientInfo patientInfo = patientInfoService.findOneByHillromId(SecurityUtils.getCurrentLogin()).get();
            	patientInfo.setEmail(email);
            	patientInfoService.update(patientInfo);
            }
            log.debug("updateEmailOrPassword for User: {}", u);
        });
	}
	
}
