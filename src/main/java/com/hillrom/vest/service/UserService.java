package com.hillrom.vest.service;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

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
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.web.rest.dto.HillromTeamUserDTO;
import com.hillrom.vest.web.rest.dto.UserDTO;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;

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
    private UserExtensionRepository userExtensionRepository;

    @Inject
    private AuthorityRepository authorityRepository;
    
    @Inject
    private PatientInfoService patientInfoService;
    
    @Inject
    private UserLoginTokenService authTokenService;
    
    @Inject
    private UserSecurityQuestionService userSecurityQuestionService;

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
    
    public UserExtension createDoctor(UserExtensionDTO userExtensionDTO) {
		UserExtension newUser = new UserExtension();
		newUser.setTitle(userExtensionDTO.getTitle());
		newUser.setFirstName(userExtensionDTO.getFirstName());
		newUser.setMiddleName(userExtensionDTO.getMiddleName());
		newUser.setLastName(userExtensionDTO.getLastName());
		newUser.setEmail(userExtensionDTO.getEmail());
		newUser.setSpeciality(userExtensionDTO.getSpeciality());
		newUser.setCredentials(userExtensionDTO.getCredentials());
		newUser.setAddress(userExtensionDTO.getAddress());
		newUser.setZipcode(userExtensionDTO.getZipcode());
		newUser.setCity(userExtensionDTO.getCity());
		newUser.setState(userExtensionDTO.getState());
		newUser.setPrimaryPhone(userExtensionDTO.getPrimaryPhone());
		newUser.setMobilePhone(userExtensionDTO.getMobilePhone());
		newUser.setFaxNumber(userExtensionDTO.getFaxNumber());
		newUser.setLangKey(null);
		// new user is not active
		newUser.setActivated(false);
		newUser.setDeleted(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		newUser.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
		userExtensionRepository.save(newUser);
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
	
	public JSONObject updateEmailOrPassword(Map<String,String> params){
		
		String email = params.get("email");
    	String password = params.get("password");
    	String questionId = params.get("questionId");
    	String answer = params.get("answer");
    	String authToken = params.get("x-auth-token");
    	
    	JSONObject errorsJsonObject = validateRequest(password, questionId,
				answer);
        
        if( null != errorsJsonObject.get("ERROR"))
        	return errorsJsonObject;
        
        User currentUser = findOneByEmail(SecurityUtils.getCurrentLogin()).get();

        errorsJsonObject = isUserExistsWithEmail(email, currentUser);
        
        if(null != errorsJsonObject.get("ERROR")){
        	return errorsJsonObject;
        }
        
        currentUser.setEmail(email);
        currentUser.setPassword(passwordEncoder.encode(password));
        currentUser.setLastLoggedInAt(DateTime.now());
        
        userRepository.save(currentUser);
        
        // update email in patientInfo, if the User is Patient
        updatePatientEmailIfNotPresent(email);
        
        log.debug("updateEmailOrPassword for User: {}", currentUser);
       
        Long qid = Long.parseLong(questionId);
        userSecurityQuestionService.saveOrUpdate(currentUser.getId(), qid, answer);
		authTokenService.deleteToken(authToken); // Token must be deleted to avoid subsequent request
		return new JSONObject();
	}

	/**
	 * Checks whether User Exists with provided Email or Whether Email is left blank
	 * @param email
	 * @param currentUser
	 * @return
	 */
	private JSONObject isUserExistsWithEmail(String email, User currentUser) {
		JSONObject jsonObject = new JSONObject();
		if(!RandomUtil.isValidEmail(currentUser.getEmail()) && StringUtils.isBlank(email)){
        	jsonObject.put("ERROR", "Required field Email is missing");
        }
        
        // Update Email for the firstTime Login , if not present
        if(StringUtils.isNotBlank(email)){
        	Optional<User> existingUser = findOneByEmail(email);
        	if(existingUser.isPresent()){
            	jsonObject.put("ERROR", "Email Already registered, please choose another email");
        	}
        }
        return jsonObject;
	}

	/**
	 * This updates Email in PatientInfo, if the loggedIn User is Patient
	 * @param email
	 */
	private void updatePatientEmailIfNotPresent(String email) {
		if(null != email){
        	patientInfoService.findOneByHillromId(SecurityUtils.getCurrentLogin()).ifPresent(patient -> {
        		patient.setEmail(email);        		
        		patientInfoService.update(patient);
        	});
        }
	}

	/**
	 * Validate whether all required fields present in the request
	 * @param password
	 * @param questionId
	 * @param answer
	 * @return
	 */
	private JSONObject validateRequest(String password,
			String questionId, String answer) {
		JSONObject jsonObject = new JSONObject();
    	
    	if(StringUtils.isBlank(answer)){
    		jsonObject.put("ERROR", "Required field Answer is missing");
    		return jsonObject;
    	}
    	if(StringUtils.isBlank(questionId)){
    		jsonObject.put("ERROR", "Required field SecurityQuestion is missing");
    		return jsonObject;
    	}
        if (!checkPasswordLength(password)) {
        	jsonObject.put("ERROR", "Incorrect password");
            return jsonObject;
        }
		return jsonObject;
	}
	
	private boolean checkPasswordLength(String password) {
	      return (!StringUtils.isEmpty(password) && password.length() >= UserDTO.PASSWORD_MIN_LENGTH && password.length() <= UserDTO.PASSWORD_MAX_LENGTH);
	}
}

