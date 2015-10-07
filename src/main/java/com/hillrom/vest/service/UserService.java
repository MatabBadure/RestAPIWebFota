package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.domain.UserSecurityQuestion;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.ClinicPatientRepository;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.OnCredentialsChangeEvent;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.service.util.RequestUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.CareGiverVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.UserDTO;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;

import net.minidev.json.JSONObject;

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
    private UserPatientRepository userPatientRepository;

    @Inject
    private PatientInfoRepository patientInfoRepository;

    @Inject
    private PatientInfoService patientInfoService;

    @Inject
    private MailService mailService;

    @Inject
    private UserSecurityQuestionService userSecurityQuestionService;

    @Inject
	private ClinicRepository clinicRepository;

    @Inject
    private ApplicationEventPublisher eventPublisher;
    
    @Inject
    private PatientNoEventService noEventService;
    
    @Inject
	private ClinicPatientRepository clinicPatientRepository;

    public String generateDefaultPassword(User patientUser) {
		StringBuilder defaultPassword = new StringBuilder();
		defaultPassword.append(patientUser.getZipcode());
		// default password will have the first 4 letters from last name, if length of last name <= 4, use complete string
		int endIndex = patientUser.getLastName().length() > Constants.NO_OF_CHARACTERS_TO_BE_EXTRACTED ? Constants.NO_OF_CHARACTERS_TO_BE_EXTRACTED : patientUser.getLastName().length() ;
		defaultPassword.append(patientUser.getLastName().substring(0, endIndex));
		defaultPassword.append(patientUser.getDob().toString(Constants.DATEFORMAT_MMddyyyy));
		return defaultPassword.toString();
	}

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
        return Optional.empty();
    }

    /**
     * Completes the reset password flow
     * @param paramsMap
     * @return
     * @throws HillromException
     */
    public JSONObject completePasswordReset(Map<String,String> paramsMap) throws HillromException {
       log.debug("Reset user password for reset key {}", paramsMap);

       String requiredParams[] = {"password","questionId","answer"};
       JSONObject errorJSON =  RequestUtil.checkRequiredParams(paramsMap,requiredParams);
       if(null != errorJSON.get("ERROR"))
    	   return errorJSON;

       String key = paramsMap.get("key");
       String newPassword = paramsMap.get("password");
       String questionId = paramsMap.get("questionId");
       String answer = paramsMap.get("answer");

       JSONObject jsonObject = new JSONObject();
       if (!checkPasswordConstraints(newPassword)) {
    	   jsonObject.put("message", "Incorrect password");
    	   return jsonObject;
       }

       Optional<User> opUser = userRepository.findOneByResetKey(key);
       if(opUser.isPresent()){
    	   User user = opUser.get();
    	   errorJSON = canProceedPasswordReset(questionId, answer,user);
    	   if(null != errorJSON.get("ERROR")){
    		   return errorJSON;
    	   }
           user.setPassword(passwordEncoder.encode(newPassword));
           user.setResetKey(null);
           user.setResetDate(null);
           userRepository.save(user);
   		   eventPublisher.publishEvent(new OnCredentialsChangeEvent(user.getId()));
           jsonObject.put("email", user.getEmail());
           return jsonObject;
       }else{
    	   throw new HillromException(ExceptionConstants.HR_556);//Invalid Reset Key
       }

    }

    /**
     * Verifies whether Token expired or the security question answer matches
     * @param questionId
     * @param answer
     * @param user
     * @return
     * @throws HillromException
     */
	private JSONObject canProceedPasswordReset(String questionId, String answer,
			 User user) throws HillromException {
		JSONObject jsonObject = new JSONObject();
		DateTime oneDayAgo = DateTime.now().minusHours(24);
           if(user.getResetDate().isBefore(oneDayAgo.toInstant().getMillis())){
        	   throw new HillromException(ExceptionConstants.HR_504);//Reset Key Expired
           }
           if(!verifySecurityQuestion(user,questionId,answer)){
        	   throw new HillromException(ExceptionConstants.HR_505);//Incorrect Security Question or Password
           }
           return jsonObject;
	}

    private boolean verifySecurityQuestion(User user,String questionId,String answer){
    	Optional<UserSecurityQuestion> opUserSecurityQuestion =  userSecurityQuestionService.findOneByUserIdAndQuestionId(user.getId(), Long.parseLong(questionId));
    	if(opUserSecurityQuestion.isPresent()){
    		return answer.equals(opUserSecurityQuestion.get().getAnswer());
    	}
    	return false;
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) && password.length() >= UserDTO.PASSWORD_MIN_LENGTH && password.length() <= UserDTO.PASSWORD_MAX_LENGTH);
    }

    private boolean checkPasswordConstraints(String password) {
    	Pattern pattern = Pattern.compile(UserDTO.PASSWORD_PATTERN);
   	  	Matcher matcher = pattern.matcher(password);
   	  	boolean isValid = matcher.matches();
   	  	log.debug("Password : {}, Valid : {}", password,isValid);
        return isValid;
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
        Authority authority = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
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
    		eventPublisher.publishEvent(new OnCredentialsChangeEvent(u.getId()));
            log.debug("Changed Information for User: {}", u);
        });
    }

    public JSONObject changePassword(String password) throws HillromException {
    	JSONObject jsonObject = new JSONObject();
    	if(!checkPasswordConstraints(password)){
    		throw new HillromException(ExceptionConstants.HR_506);//Incorrect password
    	}else{
    		userRepository.findOneByEmail(SecurityUtils.getCurrentLogin()).ifPresent(u-> {
    			String encryptedPassword = passwordEncoder.encode(password);
    			u.setPassword(encryptedPassword);
    			u.setLastLoggedInAt(DateTime.now());
    			userRepository.save(u);
    			eventPublisher.publishEvent(new OnCredentialsChangeEvent(u.getId()));
    			log.debug("Changed password for User: {}", u);
    		});
    	}
    	return jsonObject;
    }

    public String updatePassword(Long id, Map<String, String> passwordList) throws HillromException {
    	Optional<User> user = userRepository.findOneByEmail(SecurityUtils.getCurrentLogin());
    	if(user.isPresent()){
			String encryptedNewPassword = passwordEncoder.encode(passwordList.get("newPassword"));
			if(passwordEncoder.matches(passwordList.get("password"), user.get().getPassword())) {
				if(!checkPasswordLength(passwordList.get("newPassword"))){
		    		throw new HillromException(ExceptionConstants.HR_598);
		    	}else{
		    		user.get().setPassword(encryptedNewPassword);
	    			userRepository.save(user.get());
	    			eventPublisher.publishEvent(new OnCredentialsChangeEvent(user.get().getId()));
	    			log.debug("Changed password for User: {}", user.get());
	    			return MessageConstants.HR_294;
		    	}
			} else {
				throw new HillromException(ExceptionConstants.HR_597);
			}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
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

    private List<String> rolesAdminCanModerate() {
		List<String> rolesAdminCanModerate = new ArrayList<String>();
    	rolesAdminCanModerate.add(AuthoritiesConstants.ACCT_SERVICES);
    	rolesAdminCanModerate.add(AuthoritiesConstants.ASSOCIATES);
    	rolesAdminCanModerate.add(AuthoritiesConstants.ADMIN);
		return rolesAdminCanModerate;
	}

    public UserExtension createUser(UserExtensionDTO userExtensionDTO, String baseUrl) throws HillromException{
		if(userExtensionDTO.getEmail() != null) {
			Optional<User> existingUser = userRepository.findOneByEmail(userExtensionDTO.getEmail());
			if (existingUser.isPresent()) {
				throw new HillromException(ExceptionConstants.HR_501);
    		}
    	}
		if(userExtensionDTO.getHillromId() != null) {
			Optional<User> existingUser = userRepository.findOneByHillromId(userExtensionDTO.getHillromId());
			if (existingUser.isPresent()) {
				throw new HillromException(ExceptionConstants.HR_522);
    		}
    	}
    	List<String> rolesAdminCanModerate = rolesAdminCanModerate();
    	if(rolesAdminCanModerate.contains(userExtensionDTO.getRole())
    			&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
    		UserExtension user = createHillromTeamUser(userExtensionDTO);
    		if(user.getId() != null) {
    			if(userExtensionDTO.getEmail() != null) {
    				mailService.sendActivationEmail(user, baseUrl);
    			}
                return user;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_511);
    		}
    	} else if (AuthoritiesConstants.PATIENT.equals(userExtensionDTO.getRole())) {
        	UserExtension user = createPatientUser(userExtensionDTO);
    		if(user.getId() != null) {
                return user;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_521);
    		}
        } else if (AuthoritiesConstants.HCP.equals(userExtensionDTO.getRole())) {
        	UserExtension user = createHCPUser(userExtensionDTO);
        	if(user.getId() != null) {
                mailService.sendActivationEmail(user, baseUrl);
                return user;
        	} else {
        		throw new HillromException(ExceptionConstants.HR_531);
    		}
        } else if (AuthoritiesConstants.CLINIC_ADMIN.equals(userExtensionDTO.getRole())) {
        	UserExtension user = createClinicAdminUser(userExtensionDTO);
        	if(user.getId() != null) {
                mailService.sendActivationEmail(user, baseUrl);
                return user;
        	} else {
        		throw new HillromException(ExceptionConstants.HR_537);
    		}
        } else {
        	throw new HillromException(ExceptionConstants.HR_502);
    	}
    }

    public UserExtension createHillromTeamUser(UserExtensionDTO userExtensionDTO) throws HillromException {
    	UserExtension newUser = new UserExtension();
		try {
	    	assignValuesToUserObj(userExtensionDTO, newUser);
			newUser.setActivated(false);
			newUser.setDeleted(false);
			newUser.setActivationKey(RandomUtil.generateActivationKey());
			newUser.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
			userExtensionRepository.save(newUser);
			log.debug("Created Information for User: {}", newUser);
			return newUser;
	    } catch(Exception e){
			throw new HillromException(ExceptionConstants.HR_501, e);
		}
	}

    public UserExtension createPatientUser(UserExtensionDTO userExtensionDTO) throws HillromException {
    	UserExtension newUser = new UserExtension();
    	Optional<PatientInfo> existingPatientInfoFromDB = patientInfoRepository.findOneByHillromId(userExtensionDTO.getHillromId());
    	if(existingPatientInfoFromDB.isPresent())
    		return newUser;
    	else
    		return populatePatientUserInDB(userExtensionDTO);
	}

    private UserExtension  populatePatientUserInDB(UserExtensionDTO userExtensionDTO){
		UserExtension newUser = new UserExtension();
		String patientInfoId = patientInfoRepository.id();
		PatientInfo patientInfo = new PatientInfo();
		assignValuesToPatientInfoObj(userExtensionDTO, patientInfo);

		// Assigns Next Patient HillromId from Stored Procedure
		patientInfo.setId(patientInfoId);
		patientInfo = patientInfoRepository.save(patientInfo);
		log.debug("Created Information for Patient : {}", patientInfo);

		assignValuesToUserObj(userExtensionDTO, newUser);

		newUser.setPassword(passwordEncoder
				.encode(generateDefaultPassword((User) newUser)));
		newUser.setActivated(true);
		newUser.setDeleted(false);
		newUser.setHillromId(userExtensionDTO.getHillromId());

		newUser.getAuthorities().add(
				authorityRepository.findOne(userExtensionDTO.getRole()));
		newUser = userExtensionRepository.save(newUser);
		log.debug("Created Information for Patient User: {}", newUser);

		UserPatientAssoc userPatientAssoc = createUserPatientAssociation(
				newUser, patientInfo);

		patientInfo.getUserPatientAssoc().add(userPatientAssoc);
		patientInfoRepository.save(patientInfo);
		log.debug("Updated Information for Patient User: {}", patientInfo);

		newUser.getUserPatientAssoc().add(userPatientAssoc);
		userExtensionRepository.save(newUser);
		log.debug("Updated Information for Patient User: {}", newUser);
		noEventService.createIfNotExists(new PatientNoEvent(newUser.getCreatedDate().toLocalDate(),null, patientInfo, newUser));
		return newUser;
	}

	public UserPatientAssoc createUserPatientAssociation(UserExtension newUser,
			PatientInfo patientInfo) {
		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, newUser), AuthoritiesConstants.PATIENT, RelationshipLabelConstants.SELF);
		userPatientAssoc = userPatientRepository.save(userPatientAssoc);
		log.debug("Created Information for userPatientAssoc: {}",
				userPatientAssoc);
		return userPatientAssoc;
	}

    public UserExtension createHCPUser(UserExtensionDTO userExtensionDTO) throws HillromException {
    	UserExtension newUser = new UserExtension();
		assignValuesToUserObj(userExtensionDTO, newUser);
		newUser.setActivated(false);
		newUser.setDeleted(false);
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		for(Map<String, String> clinicObj : userExtensionDTO.getClinicList()){
			
			if(Objects.isNull(clinicObj.get("id")))
					throw new  HillromException(ExceptionConstants.HR_544);
			
			Clinic clinic = clinicRepository.getOne(clinicObj.get("id"));
			newUser.getClinics().add(clinic);
		}
		newUser.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
		userExtensionRepository.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}
    
    public UserExtension createClinicAdminUser(UserExtensionDTO userExtensionDTO) throws HillromException {
    	UserExtension newUser = new UserExtension();
		assignValuesToUserObj(userExtensionDTO, newUser);
		newUser.setActivated(false);
		newUser.setDeleted(false);
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		newUser.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
		userExtensionRepository.saveAndFlush(newUser);
		if(newUser.getId() != null) {
			List<Clinic> clinicList = new LinkedList<>();
			for(Map<String, String> clinicObj : userExtensionDTO.getClinicList()){
				Clinic clinic = clinicRepository.getOne(clinicObj.get("id"));
				clinic.setClinicAdminId(newUser.getId());
				clinicList.add(clinic);
			}
			clinicRepository.save(clinicList);
			log.debug("Created Information for User: {}", newUser);
			return newUser;
		} else {
			throw new HillromException(ExceptionConstants.HR_574);
		}
	}
    
    public UserExtension updateUser(Long id, UserExtensionDTO userExtensionDTO, String baseUrl) throws HillromException{
        if(userExtensionDTO.getEmail() != null) {
			Optional<User> existingUser = userRepository.findOneByEmail(userExtensionDTO.getEmail());
			if(existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
				throw new HillromException(ExceptionConstants.HR_501);//e-mail address already in use
			}
    	}
        if(userExtensionDTO.getHillromId() != null) {
			Optional<User> existingUser = userRepository.findOneByHillromId(userExtensionDTO.getHillromId());
			if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
				throw new HillromException(ExceptionConstants.HR_522);
    		}
    	}
        List<String> rolesAdminCanModerate = rolesAdminCanModerate();
        UserExtension existingUser = userExtensionRepository.findOne(id);
        String currentEmail = StringUtils.isNotBlank(existingUser.getEmail()) ? existingUser.getEmail() : null;
        if(rolesAdminCanModerate.contains(userExtensionDTO.getRole())
        		&& SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
        	UserExtension user = updateHillromTeamUser(existingUser, userExtensionDTO);
    		if(user.getId() != null) {
    			if(StringUtils.isNotBlank(userExtensionDTO.getEmail()) && StringUtils.isNotBlank(currentEmail) && !userExtensionDTO.getEmail().equals(currentEmail)) {
    				sendEmailNotification(baseUrl, user);
    			}
                return user;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_517);//Unable to update Hillrom User
    		}
    	} else if (AuthoritiesConstants.PATIENT.equals(userExtensionDTO.getRole())) {
    		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(existingUser);
    		if(Objects.nonNull(patientInfo)){
	    		if(!userExtensionDTO.getClinicMRNId().isEmpty()){
					List<ClinicPatientAssoc> existingClinics = clinicPatientRepository.findByMRNId(userExtensionDTO.getClinicMRNId().get("mrnId"));
					if(!existingClinics.isEmpty()){
						for(ClinicPatientAssoc clinicPatientAssoc : existingClinics) {
							if(clinicPatientAssoc.getClinic().getId().equals(userExtensionDTO.getClinicMRNId().get("clinicId")) 
									&& !clinicPatientAssoc.getPatient().getId().equals(patientInfo.getId())){
								throw new HillromException(ExceptionConstants.HR_599);
							}
						}
					}
	    		}
    		}
           	UserExtension user = updatePatientUser(existingUser, userExtensionDTO);
    		if(user.getId() != null) {
    			if(StringUtils.isNotBlank(userExtensionDTO.getEmail()) && !userExtensionDTO.getEmail().equals(currentEmail)) {
    				sendEmailNotification(baseUrl, user);
    			}
                return user;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_524);//Unable to update Patient.
    		}
        } else if (AuthoritiesConstants.HCP.equals(userExtensionDTO.getRole())) {
           	UserExtension user = updateHCPUser(existingUser, userExtensionDTO);
    		if(user.getId() != null) {
    			if(StringUtils.isNotBlank(userExtensionDTO.getEmail()) && StringUtils.isNotBlank(currentEmail) && !userExtensionDTO.getEmail().equals(currentEmail)) {
    				sendEmailNotification(baseUrl, user);
    			}
                return user;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_531);//Unable to update HealthCare Professional.
    		}
        } else if (AuthoritiesConstants.CLINIC_ADMIN.equals(userExtensionDTO.getRole())) {
           	UserExtension user = updateClinicAdminUser(existingUser, userExtensionDTO);
    		if(user.getId() != null) {
    			if(StringUtils.isNotBlank(userExtensionDTO.getEmail()) && StringUtils.isNotBlank(currentEmail) && !userExtensionDTO.getEmail().equals(currentEmail)) {
    				sendEmailNotification(baseUrl, user);
    			}
                return user;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_575);//Unable to update Clinic Admin.
    		}
        } else {
        	throw new HillromException(ExceptionConstants.HR_555);//Incorrect data
    	}
    }

	private void sendEmailNotification(String baseUrl, UserExtension user) {
		user.setActivationKey(RandomUtil.generateActivationKey());
		user.setActivated(false);
		userRepository.saveAndFlush(user);
		mailService.sendActivationEmail(user, baseUrl);
		eventPublisher.publishEvent(new OnCredentialsChangeEvent(user.getId()));
	}

    public UserExtension updateHillromTeamUser(UserExtension user, UserExtensionDTO userExtensionDTO) {
		assignValuesToUserObj(userExtensionDTO, user);
		// clearing existing roles for the user
		user.getAuthorities().clear();
		user.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
		userExtensionRepository.save(user);
		log.debug("Updated Information for Hillrom User: {}", user);
		return user;
	}

    public UserExtension updatePatientUser(UserExtension user, UserExtensionDTO userExtensionDTO) {
    	patientInfoRepository.findOneByHillromId(userExtensionDTO.getHillromId())
    	.map(patient -> {
    		assignValuesToPatientInfoObj(userExtensionDTO, patient);
    		patientInfoRepository.save(patient);
    		assignValuesToUserObj(userExtensionDTO, user);
			userExtensionRepository.save(user);
			if(!userExtensionDTO.getClinicMRNId().isEmpty()){
				Optional<ClinicPatientAssoc> clinicPatientAssoc = clinicPatientRepository.findOneByClinicIdAndPatientId(
						userExtensionDTO.getClinicMRNId().get("clinicId"), patient.getId());
				if(clinicPatientAssoc.isPresent()){
					clinicPatientAssoc.get().setMrnId(userExtensionDTO.getClinicMRNId().get("mrnId"));
					clinicPatientRepository.saveAndFlush(clinicPatientAssoc.get());
				}
			}
			log.debug("Updated Information for Patient User: {}", user);
    		return user;
    	});
		return user;
	}

    public UserExtension updateHCPUser(UserExtension hcpUser, UserExtensionDTO userExtensionDTO) {
		assignValuesToUserObj(userExtensionDTO, hcpUser);
		userExtensionRepository.save(hcpUser);
		log.debug("Updated Information for HealthCare Proffessional: {}", hcpUser);
		return hcpUser;
	}
    
    public UserExtension updateClinicAdminUser(UserExtension clinicAdminUser, UserExtensionDTO userExtensionDTO) {
		assignValuesToUserObj(userExtensionDTO, clinicAdminUser);
		userExtensionRepository.saveAndFlush(clinicAdminUser);
		log.debug("Updated Information for Clinic Admin User : {}", clinicAdminUser);
		return clinicAdminUser;
	}

	private void assignValuesToPatientInfoObj(UserExtensionDTO userExtensionDTO, PatientInfo patientInfo) {
		patientInfo.setHillromId(userExtensionDTO.getHillromId());
		if(userExtensionDTO.getTitle() != null)
			patientInfo.setTitle(userExtensionDTO.getTitle());
		if(userExtensionDTO.getFirstName() != null)
			patientInfo.setFirstName(userExtensionDTO.getFirstName());
		if(userExtensionDTO.getMiddleName() != null)
			patientInfo.setMiddleName(userExtensionDTO.getMiddleName());
		if(userExtensionDTO.getLastName() != null)
			patientInfo.setLastName(userExtensionDTO.getLastName());
		if(userExtensionDTO.getGender() != null)
			patientInfo.setGender(userExtensionDTO.getGender());
		if(userExtensionDTO.getDob() != null)
			patientInfo.setDob(LocalDate.parse(userExtensionDTO.getDob(), DateTimeFormat.forPattern("MM/dd/yyyy")));
		if(userExtensionDTO.getLangKey() != null)
			patientInfo.setLangKey(userExtensionDTO.getLangKey());
		if(userExtensionDTO.getEmail() != null)
			patientInfo.setEmail(userExtensionDTO.getEmail());
		if(userExtensionDTO.getAddress() != null)
			patientInfo.setAddress(userExtensionDTO.getAddress());
		if(userExtensionDTO.getZipcode() != null)
			patientInfo.setZipcode(userExtensionDTO.getZipcode());
		if(userExtensionDTO.getCity() != null)
			patientInfo.setCity(userExtensionDTO.getCity());
		if(userExtensionDTO.getState() != null)
			patientInfo.setState(userExtensionDTO.getState());
		if(userExtensionDTO.getPrimaryPhone() != null)
			patientInfo.setPrimaryPhone(userExtensionDTO.getPrimaryPhone());
		if(userExtensionDTO.getMobilePhone() != null)
			patientInfo.setMobilePhone(userExtensionDTO.getMobilePhone());
		if(userExtensionDTO.isExpired()==true){
		
			patientInfo.setExpired(userExtensionDTO.isExpired());
			patientInfo.setExpiredDate(new DateTime());
		}
		patientInfo.setWebLoginCreated(true);
	}

	private void assignValuesToUserObj(UserExtensionDTO userExtensionDTO, UserExtension newUser) {
		if(userExtensionDTO.getTitle() != null)
			newUser.setTitle(userExtensionDTO.getTitle());
		if(userExtensionDTO.getFirstName() != null)
			newUser.setFirstName(userExtensionDTO.getFirstName());
		if(userExtensionDTO.getMiddleName() != null)
			newUser.setMiddleName(userExtensionDTO.getMiddleName());
		if(userExtensionDTO.getLastName() != null)
			newUser.setLastName(userExtensionDTO.getLastName());
		if(userExtensionDTO.getEmail() != null)
			newUser.setEmail(userExtensionDTO.getEmail());
		if(userExtensionDTO.getSpeciality() != null)
			newUser.setSpeciality(userExtensionDTO.getSpeciality());
		if(userExtensionDTO.getCredentials() != null)
			newUser.setCredentials(userExtensionDTO.getCredentials());
		if(userExtensionDTO.getAddress() != null)
			newUser.setAddress(userExtensionDTO.getAddress());
		if(userExtensionDTO.getZipcode() != null)
			newUser.setZipcode(userExtensionDTO.getZipcode());
		if(userExtensionDTO.getCity() != null)
			newUser.setCity(userExtensionDTO.getCity());
		if(userExtensionDTO.getState() != null)
			newUser.setState(userExtensionDTO.getState());
		if(userExtensionDTO.getPrimaryPhone() != null)
			newUser.setPrimaryPhone(userExtensionDTO.getPrimaryPhone());
		if(userExtensionDTO.getMobilePhone() != null)
			newUser.setMobilePhone(userExtensionDTO.getMobilePhone());
		if(userExtensionDTO.getFaxNumber() != null)
			newUser.setFaxNumber(userExtensionDTO.getFaxNumber());
		if(userExtensionDTO.getNpiNumber() != null)
			newUser.setNpiNumber(userExtensionDTO.getNpiNumber());
		if(userExtensionDTO.getDob() != null)
			newUser.setDob(LocalDate.parse(userExtensionDTO.getDob(), DateTimeFormat.forPattern("MM/dd/yyyy")));
		if(userExtensionDTO.getGender() != null)
			newUser.setGender(userExtensionDTO.getGender());
		if(userExtensionDTO.getHillromId() != null)
			newUser.setHillromId(userExtensionDTO.getHillromId());
		newUser.setLangKey(userExtensionDTO.getLangKey());
	}

    public Optional<User> findOneByEmail(String email) {
		return userRepository.findOneByEmail(email);
	}

    public Optional<User> findOneByEmailOrHillromId(String login){
    	return userRepository.findOneByEmailOrHillromId(login);
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
		User persistedUser = userRepository.save(newUser);

		UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, newUser), AuthoritiesConstants.PATIENT, RelationshipLabelConstants.SELF);
		userPatientRepository.save(userPatientAssoc);
		newUser.getUserPatientAssoc().add(userPatientAssoc);
		patientInfo.getUserPatientAssoc().add(userPatientAssoc);

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
			patientInfoService.update(patientUser);
			return patientUser;
		});
	}

	public JSONObject updateEmailOrPassword(Map<String,String> params) throws HillromException{

		String email = params.get("email");
    	String password = params.get("password");
    	String questionId = params.get("questionId");
    	String answer = params.get("answer");
    	String termsAndConditionsAccepted = params.get("termsAndConditionsAccepted");

    	JSONObject errorsJsonObject = validateRequest(password, questionId,
				answer,termsAndConditionsAccepted);

        if( null != errorsJsonObject.get("ERROR"))
        	return errorsJsonObject;

        User currentUser = findOneByEmailOrHillromId(SecurityUtils.getCurrentLogin()).get();

        errorsJsonObject = isUserExistsWithEmail(email, currentUser);

        if(null != errorsJsonObject.get("ERROR")){
        	return errorsJsonObject;
        }

        if(null!= email)
        	currentUser.setEmail(email);

        Long qid = Long.parseLong(questionId);
        Optional<UserSecurityQuestion> opUserSecQ = userSecurityQuestionService.saveOrUpdate(currentUser.getId(), qid, answer);

        if(opUserSecQ.isPresent()){

        	currentUser.setPassword(passwordEncoder.encode(password));
        	currentUser.setLastLoggedInAt(DateTime.now());
        	currentUser.setTermsConditionAccepted(true);
        	currentUser.setTermsConditionAcceptedDate(DateTime.now());
        	userRepository.save(currentUser);
    		eventPublisher.publishEvent(new OnCredentialsChangeEvent(currentUser.getId()));
        	// update email in patientInfo, if the User is Patient
        	updatePatientEmailIfNotPresent(email);

        	log.debug("updateEmailOrPassword for User: {}", currentUser);

        }else{
        	throw new HillromException(ExceptionConstants.HR_557);//Invalid Security Question or Answer")
        }
		return new JSONObject();
	}

	/**
	 * Checks whether User Exists with provided Email or Whether Email is left blank
	 * @param email
	 * @param currentUser
	 * @return
	 * @throws HillromException
	 */
	private JSONObject isUserExistsWithEmail(String email, User currentUser) throws HillromException {
		JSONObject jsonObject = new JSONObject();
		if(!RandomUtil.isValidEmail(currentUser.getEmail()) && StringUtils.isBlank(email)){
			throw new HillromException(ExceptionConstants.HR_508);//Required field Email is missing
        }

        // Update Email for the firstTime Login , if not present
        if(StringUtils.isNotBlank(email)){
        	Optional<User> existingUser = findOneByEmail(email);
        	if(existingUser.isPresent()){
            	throw new HillromException(ExceptionConstants.HR_509);//Email Already registered, please choose another email
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
	 * @throws HillromException
	 */
	private JSONObject validateRequest(String password,
		String questionId, String answer,String termsAndConditionsAccepted) throws HillromException {
		JSONObject jsonObject = new JSONObject();
    	if(!StringUtils.isNotBlank(termsAndConditionsAccepted) || "false".equalsIgnoreCase(termsAndConditionsAccepted)){
    		throw new HillromException(ExceptionConstants.HR_510);

    	}
    	if(StringUtils.isBlank(answer)){
    		throw new HillromException(ExceptionConstants.HR_503);//Required field Answer is missing"
    	}
    	if(StringUtils.isBlank(questionId) || !StringUtils.isNumeric(questionId)){
    		throw new HillromException(ExceptionConstants.HR_507);//Required field SecurityQuestion is missing");
    	}
        if (!checkPasswordConstraints(password)) {
        	throw new HillromException(ExceptionConstants.HR_506);//Incorrect password
        }
		return jsonObject;
	}

	public JSONObject deleteUser(Long id) throws HillromException {
    	JSONObject jsonObject = new JSONObject();
    	UserExtension existingUser = userExtensionRepository.findOne(id);
    	List<Authority> authorities  = authorityRepository.findAll();
    	Map<String,Authority> authorityMap = new HashMap<>();
    	authorities.stream().forEach(authority -> {
    		authorityMap.put(authority.getName(), authority);
    	});
		if(Objects.nonNull(existingUser)) {
			if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.ACCT_SERVICES))) {
				if(existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.PATIENT))) {
					deletePatientUser(existingUser);
					jsonObject.put("message", MessageConstants.HR_214);
				} else if((existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.HCP))
							|| existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.CLINIC_ADMIN)))) {
					existingUser.setDeleted(true);
					userExtensionRepository.save(existingUser);
					jsonObject.put("message", MessageConstants.HR_204);
				} else {
					throw new HillromException(ExceptionConstants.HR_513);//Unable to delete User
				}
			} else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))){
				if(existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.PATIENT))) {
					deletePatientUser(existingUser);
					jsonObject.put("message", MessageConstants.HR_214);
				} else if(existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.ADMIN))) {
					if(SecurityUtils.getCurrentLogin().equals(existingUser.getEmail())) {
						throw new HillromException(ExceptionConstants.HR_520);
					}
					existingUser.setDeleted(true);
					userExtensionRepository.save(existingUser);
					jsonObject.put("message", MessageConstants.HR_204);
				} else if((existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.ACCT_SERVICES))
							|| existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.ASSOCIATES))
							|| existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.PATIENT))
							|| existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.HCP))
							|| existingUser.getAuthorities().contains(authorityMap.get(AuthoritiesConstants.CLINIC_ADMIN)))) {
					existingUser.setDeleted(true);
					userExtensionRepository.save(existingUser);
					jsonObject.put("message", MessageConstants.HR_204);
				} else {
					throw new HillromException(ExceptionConstants.HR_513);//Unable to delete User
				}
			} else {
				throw new HillromException(ExceptionConstants.HR_513);//Unable to delete User
			}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);//No such user exist
		}
		return jsonObject;
    }

	private void deletePatientUser(UserExtension existingUser) {
		List<UserPatientAssoc> caregiverAssocList = getListOfCaregiversAssociatedToPatientUser(existingUser);
		List<UserExtension> caregiversToBeDeleted = new LinkedList<>();
		caregiverAssocList.forEach(caregiverAssoc -> {
			List<PatientInfo> patientList = new LinkedList<>();
			caregiverAssoc.getUser().getUserPatientAssoc().forEach(userPatientAssoc -> {
				if(AuthoritiesConstants.CARE_GIVER.equals(userPatientAssoc.getUserRole())){
					patientList.add(caregiverAssoc.getPatient());
				}
			});
			if(patientList.size() == 1){
				caregiversToBeDeleted.add((UserExtension)caregiverAssoc.getUser());
			}
		});
		userExtensionRepository.delete(caregiversToBeDeleted);
		userPatientRepository.delete(caregiverAssocList);
		existingUser.setDeleted(true);
		userExtensionRepository.save(existingUser);
	}

	public JSONObject updatePasswordSecurityQuestion(Map<String,String> params) throws HillromException{
		String requiredParams[] = {"key","password","questionId","answer","termsAndConditionsAccepted"};
		JSONObject errorsJson = RequestUtil.checkRequiredParams(params, requiredParams);
		if(errorsJson.containsKey("ERROR")){
			return errorsJson;
		}

		String password = params.get("password");
		if(!checkPasswordConstraints(password)){
			throw new HillromException(ExceptionConstants.HR_506);//Incorrect Password
		}

		String key = params.get("key");
		Optional<User> existingUser = userRepository.findOneByActivationKey(key);
		User currentUser = null;
		if(existingUser.isPresent()){
			currentUser = existingUser.get();
		}else{
			throw new HillromException(ExceptionConstants.HR_553);//Invalid Activation Key
		}

		Long qid = Long.parseLong(params.get("questionId"));
		String answer = params.get("answer");
		Optional<UserSecurityQuestion> opUserSecQ = userSecurityQuestionService.saveOrUpdate(currentUser.getId(), qid, answer);

		if(opUserSecQ.isPresent()){
			currentUser.setActivationKey(null);
			currentUser.setLastLoggedInAt(DateTime.now());
			currentUser.setLastModifiedDate(DateTime.now());
			currentUser.setPassword(passwordEncoder.encode(params.get("password")));
			currentUser.setTermsConditionAccepted(true);
			currentUser.setTermsConditionAcceptedDate(DateTime.now());
			userRepository.save(currentUser);
		}else{
			throw new HillromException(ExceptionConstants.HR_557);//Invalid Security Question or Answer

		}
		return new JSONObject();
	}

	public UserExtension getHCPUser(Long id) throws HillromException{
		UserExtension hcpUser = userExtensionRepository.findOne(id);
		if(hcpUser.getId() != null) {
		} else {
			throw new HillromException(ExceptionConstants.HR_533);//Unable to fetch HealthCare Professional.");
		}
		return hcpUser;
	 }

	public Optional<PatientUserVO> getPatientUser(Long id){
		UserExtension user = userExtensionRepository.findOne(id);
		if(null == user)
			return Optional.empty();
		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(user);
		List<ClinicPatientAssoc> clinicPatientAssocList = clinicPatientRepository.findOneByPatientId(patientInfo.getId());
		PatientUserVO patientUserVO =  new PatientUserVO(user,patientInfo);
		String mrnId;
		java.util.Iterator<ClinicPatientAssoc> cpaIterator = clinicPatientAssocList.iterator();
		while(cpaIterator.hasNext()){
			ClinicPatientAssoc clinicPatientAssoc  = cpaIterator.next();
			if(Objects.nonNull(clinicPatientAssoc)){
				Map<String,Object> clinicMRNId = new HashMap<>();
				clinicMRNId.put("clinic", clinicPatientAssoc.getClinic());
				clinicMRNId.put("mrnId", clinicPatientAssoc.getMrnId());
				mrnId = clinicPatientAssoc.getMrnId(); 
				patientUserVO.setMrnId(mrnId);
				patientUserVO.setClinicMRNId(clinicMRNId);
			}
		}
		return Optional.of(patientUserVO);
	}

	public User getUser(Long id) throws HillromException{
		User user = userRepository.findOne(id);
		if(Objects.nonNull(user)) {
			return user;
		} else {
			throw new HillromException(ExceptionConstants.HR_512);//No such user exist
		}

	 }

	public UserPatientAssoc createCaregiverUser(Long patientUserId, UserExtensionDTO userExtensionDTO, String baseUrl) throws HillromException {
		UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
		if(patientUser != null) {
			List<UserPatientAssoc> caregiverAssocList = getListOfCaregiversAssociatedToPatientUser(patientUser);
			if(caregiverAssocList.size() >= Constants.MAX_NO_OF_CAREGIVERS) {
				throw new HillromException(ExceptionConstants.HR_563);
			}
		} else {
			throw new HillromException(ExceptionConstants.HR_523);
		}
    	if(userExtensionDTO.getEmail() != null && AuthoritiesConstants.CARE_GIVER.equals(userExtensionDTO.getRole())) {
			Optional<User> existingUser = userRepository.findOneByEmail(userExtensionDTO.getEmail());
			if (existingUser.isPresent()){
				if(existingUser.get().getAuthorities().contains(new Authority(AuthoritiesConstants.CARE_GIVER))
						|| existingUser.get().getAuthorities().contains(new Authority(AuthoritiesConstants.HCP))) {
					UserPatientAssoc caregiverAssoc = associateExistingCaregiverUserWithPatient(patientUser, userExtensionDTO, existingUser);
					if(Objects.nonNull(caregiverAssoc)){
	    				mailService.sendActivationEmail(caregiverAssoc.getUser(), baseUrl);
		                return caregiverAssoc;
					} else {
						throw new HillromException(ExceptionConstants.HR_561);
					}
				} else {
					throw new HillromException(ExceptionConstants.HR_501);
				}
    		} else {
    			UserPatientAssoc caregiverAssoc = createCaregiver(patientUserId, userExtensionDTO);
	    		if(caregiverAssoc.getUser().getId() != null) {
	    			if(userExtensionDTO.getEmail() != null) {
	    				mailService.sendActivationEmail(caregiverAssoc.getUser(), baseUrl);
	    			}
	                return caregiverAssoc;
	    		} else {
	    			throw new HillromException(ExceptionConstants.HR_561);
	    		}
    		}
    	} else {
    		throw new HillromException(ExceptionConstants.HR_554);
    	}
	}

	private UserPatientAssoc associateExistingCaregiverUserWithPatient(UserExtension patientUser, UserExtensionDTO userExtensionDTO, Optional<User> existingUser) {
		User caregiverUser = existingUser.get();
		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		UserPatientAssocPK userPatientAssocPK = new UserPatientAssocPK(patientInfo, caregiverUser);
		if(Objects.nonNull(patientInfo)) {
			UserPatientAssoc userPatientAssoc = userPatientRepository.findOne(userPatientAssocPK);
			if(Objects.nonNull(userPatientAssoc)){
				userPatientAssoc.setUserRole(AuthoritiesConstants.CARE_GIVER);
				userPatientAssoc.setRelationshipLabel(userExtensionDTO.getRelationship());
			}else{
				userPatientAssoc = new UserPatientAssoc(userPatientAssocPK, AuthoritiesConstants.CARE_GIVER, userExtensionDTO.getRelationship());
			}
			userPatientRepository.save(userPatientAssoc);
			caregiverUser.getUserPatientAssoc().add(userPatientAssoc);
			caregiverUser.setDeleted(false);
			userRepository.save(caregiverUser);
			patientInfo.getUserPatientAssoc().add(userPatientAssoc);
			log.debug("Created Information for Caregiver User: {}", caregiverUser);
			return userPatientAssoc;
		} else {
			return null;
		}
	}

	public UserPatientAssoc createCaregiver(Long patientUserId, UserExtensionDTO userExtensionDTO) {
    	UserExtension newUser = new UserExtension();
    	UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
    	if(patientUser != null) {
    		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
    		if(patientInfo != null) {
    			assignValuesToUserObj(userExtensionDTO, newUser);
    			newUser.setActivated(false);
    			newUser.setDeleted(false);
    			newUser.setActivationKey(RandomUtil.generateActivationKey());
        		newUser.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
    			userExtensionRepository.save(newUser);
    			UserPatientAssoc userPatientAssoc = new UserPatientAssoc(new UserPatientAssocPK(patientInfo, newUser), AuthoritiesConstants.CARE_GIVER, userExtensionDTO.getRelationship());
    			userPatientRepository.saveAndFlush(userPatientAssoc);
    			newUser.getUserPatientAssoc().add(userPatientAssoc);
    			patientInfo.getUserPatientAssoc().add(userPatientAssoc);
    			log.debug("Created Information for Caregiver User: {}", newUser);
    			return userPatientAssoc;
    		}
    	}
    	return null;
	}

	public PatientInfo getPatientInfoObjFromPatientUser(User patientUser) {
		PatientInfo patientInfo = null;
		for(UserPatientAssoc patientAssoc : patientUser.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientInfo = patientAssoc.getPatient();
			}
		}
		return patientInfo;
	}

	public User getUserObjFromPatientInfo(PatientInfo patientInfo) {
		User patientUser = null;
		for(UserPatientAssoc patientAssoc : patientInfo.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientUser = patientAssoc.getUser();
			}
		}
		return patientUser;
	}

	public String deleteCaregiverUser(Long patientUserId, Long caregiverId) throws HillromException {
    	UserExtension caregiverUser = userExtensionRepository.findOne(caregiverId);
    	if(caregiverUser.getId() != null) {
    		UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
    		if(Objects.nonNull(patientUser)) {
	    		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
				if(caregiverUser.getUserPatientAssoc().size() == 1) {
					caregiverUser.setDeleted(true);
					userExtensionRepository.save(caregiverUser);
				}
				caregiverUser.getUserPatientAssoc().forEach(caregiverPatientAssoc -> {
					if(Objects.nonNull(patientInfo)
							&& caregiverPatientAssoc.getUserPatientAssocPK().equals(
									new UserPatientAssocPK(patientInfo, caregiverUser))) {
						userPatientRepository.delete(caregiverPatientAssoc);
					}
				});
				return MessageConstants.HR_264;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_523);//No such patient exists
    		}
		} else {
			throw new HillromException(ExceptionConstants.HR_516);//Unable to delete Caregiver User.");
		}
    }

	public List<CareGiverVO> getCaregiversForPatient(Long patientUserId) throws HillromException {
    	List<CareGiverVO> caregiverList = new LinkedList<>();
		UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
		if(Objects.nonNull(patientUser)) {
    		for(UserPatientAssoc upAssoc: getListOfCaregiversAssociatedToPatientUser(patientUser)){
    			caregiverList.add(new CareGiverVO(upAssoc.getUserRole(), upAssoc.getRelationshipLabel(), upAssoc.getUser(),upAssoc.getUser().getId(),upAssoc.getPatient().getId()));
    		}
		} else {
			throw new HillromException(ExceptionConstants.HR_523);
		}
		return caregiverList;
    }

	private List<UserPatientAssoc> getListOfCaregiversAssociatedToPatientUser(UserExtension patientUser) {
		List<UserPatientAssoc> caregiverAssocList = new ArrayList<>();
		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
		patientInfo.getUserPatientAssoc().forEach(userPatientassoc -> {
			if(AuthoritiesConstants.CARE_GIVER.equals(userPatientassoc.getUserRole()) && !userPatientassoc.getUser().isDeleted()) {
				caregiverAssocList.add(userPatientassoc);
			}
		});
		return caregiverAssocList;
	}

	public JSONObject updateSecurityQuestion(Long id, Map<String,String> params) throws HillromException {
		User existingUser = userRepository.findOne(id);
		JSONObject jsonObject = new JSONObject();
		if(Objects.nonNull(existingUser)){
			if(SecurityUtils.getCurrentLogin().equalsIgnoreCase(existingUser.getEmail())){
				jsonObject = RequestUtil.checkRequiredParams(params, new String[]{"questionId","answer"});
				if(jsonObject.containsKey("ERROR")){
					return jsonObject;
				}
				String questionId = params.get("questionId");
				String answer = params.get("answer");
				Long qid = Long.parseLong(questionId);
				userSecurityQuestionService.saveOrUpdate(id, qid, answer);
			}
			else
				throw new HillromException(ExceptionConstants.HR_558);//Forbidden
		}else{
			throw new HillromException(ExceptionConstants.HR_512);//User Doesn't exist
		}
		return jsonObject;
	}

	public UserPatientAssoc updateCaregiverUser(Long patientUserId, Long caregiverUserId, UserExtensionDTO userExtensionDTO, String baseUrl) throws HillromException {
		UserPatientAssoc caregiverAssoc = new UserPatientAssoc();
		if (AuthoritiesConstants.CARE_GIVER.equals(userExtensionDTO.getRole())) {
			caregiverAssoc = updateCaregiver(patientUserId, caregiverUserId, userExtensionDTO);
			if(Objects.nonNull(caregiverAssoc) && Objects.nonNull(caregiverAssoc.getUser().getId())) {
				if(userExtensionDTO.getEmail() != null) {
					mailService.sendActivationEmail(caregiverAssoc.getUser(), baseUrl);
				}
			} else {
				throw new HillromException(ExceptionConstants.HR_562);
			}
		} else {
			throw new HillromException(ExceptionConstants.HR_555);
		}
		return caregiverAssoc;
	}

	public UserPatientAssoc updateCaregiver(Long patientUserId, Long caregiverUserId, UserExtensionDTO userExtensionDTO) {
    	UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
    	UserExtension caregiverUser = userExtensionRepository.findOne(caregiverUserId);
    	if(Objects.nonNull(patientUser) && Objects.nonNull(caregiverUser)) {
    		PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
    		if(patientInfo != null) {
    			assignValuesToUserObj(userExtensionDTO, caregiverUser);
    			caregiverUser.getAuthorities().add(authorityRepository.findOne(userExtensionDTO.getRole()));
    			userExtensionRepository.save(caregiverUser);
    			UserPatientAssoc userPatientAssoc = userPatientRepository.findOne(new UserPatientAssocPK(patientInfo, caregiverUser));;
    			userPatientAssoc.setRelationshipLabel(userExtensionDTO.getRelationship());
    			userPatientAssoc.setUserRole(AuthoritiesConstants.CARE_GIVER);
    			userPatientRepository.save(userPatientAssoc);
    			log.debug("Updated Information for Caregiver User: {}", caregiverUser);
    			return userPatientAssoc;
    		}
    	}
    	return null;
	}

	public UserPatientAssoc getCaregiverUser(Long patientUserId, Long caregiverUserId) throws HillromException {
    	JSONObject jsonObject = new JSONObject();
		UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
		if(Objects.nonNull(patientUser)) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
    		if(patientInfo != null) {
    			List<UserPatientAssoc> caregiverAssocList = getListOfCaregiversAssociatedToPatientUser(patientUser);
    			List<UserPatientAssoc> filteredCaregiverAssocList =  caregiverAssocList.parallelStream()
    					.filter(caregiverAssoc -> caregiverUserId.equals(caregiverAssoc.getUser().getId()))
    					.collect(Collectors.toList());
	    		if(filteredCaregiverAssocList.isEmpty()){
	    			throw new HillromException(ExceptionConstants.HR_564);
	    		} else {
	    			jsonObject.put("message", MessageConstants.HR_263);
	    			return filteredCaregiverAssocList.get(0);
	    		}
    		} else {
    			throw new HillromException(ExceptionConstants.HR_523);
    		}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
    }

	public List<UserExtension> getAllUsersBy(String role) throws HillromException{
		List<UserExtension> users = userExtensionRepository.findByActiveStatus();
		List<UserExtension> filteredUsers = new LinkedList<>();
		if(users.isEmpty()) {
			throw new HillromException(ExceptionConstants.HR_519);
		} else {
			Authority authority = new Authority(role);
			filteredUsers = users.stream().filter(user -> user.getAuthorities().contains(authority)).collect(Collectors.toList());
		    return filteredUsers;
		}
	 }
	
	public User setHRMNotificationSetting(Long id, Map<String, Boolean> paramsMap) throws HillromException {
		User user = userRepository.findOne(id);
		if (user != null) {
			user.setMissedTherapyNotification(paramsMap.get("isMissedTherapyNotification"));
			user.setNonHMRNotification(paramsMap.get("isNonHMRNotification"));
			user.setSettingDeviationNotification(paramsMap.get("isSettingDeviationNotification"));
			userRepository.save(user);
			return user;
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
	}
	
	public List<CareGiverVO> getAssociatedPatientsForCaregiver(Long caregiverId) throws HillromException {
    	List<UserPatientAssoc> patientAssocList = new LinkedList<>();
		UserExtension caregiverUser = userExtensionRepository.findOne(caregiverId);

		List<CareGiverVO> caregiverList = new LinkedList<>();
		if(Objects.nonNull(caregiverUser)) {
    		patientAssocList = userPatientRepository.findByUserIdAndUserRole(caregiverUser.getId(), AuthoritiesConstants.CARE_GIVER);
    		if(patientAssocList != null){
    			
    			for(UserPatientAssoc userPatientAssoc : patientAssocList){
    				List<UserPatientAssoc> patientAssocHRIDList = new LinkedList<>();	
    				CareGiverVO careGiverVO = new CareGiverVO(userPatientAssoc.getUserRole(), userPatientAssoc.getRelationshipLabel(), userPatientAssoc.getUser(),userPatientAssoc.getUser().getId(),userPatientAssoc.getPatient().getId());
    				
    				patientAssocHRIDList = userPatientRepository.findByPatientIdAndUserRole(userPatientAssoc.getPatient().getId(),AuthoritiesConstants.PATIENT);
    				
    				if(patientAssocHRIDList != null){
    	    			for(UserPatientAssoc userPatientAssocHRID : patientAssocHRIDList){
    	    				if(userPatientAssoc.getUser().getId().equals(caregiverId)){
    	    					
    	    					System.out.println("PatientId before : " +userPatientAssoc.getPatient().getId());
    	    					careGiverVO.setUserId(userPatientAssocHRID.getUser().getId());
    	    	   				caregiverList.add(careGiverVO);
    	    					System.out.println("PatientId after : " +careGiverVO.getUser().getId().toString());
    	    				}
    	    			}
    	    		}
    			}
    		}
		} else {
			throw new HillromException(ExceptionConstants.HR_523);
		}
		return caregiverList;
    }

	public PatientUserVO getPatientUserWithMRNId(Long patientUserId, String clinicId) throws HillromException{
		UserExtension patientUser = userExtensionRepository.findOne(patientUserId);
		if(Objects.nonNull(patientUser)) {
			PatientInfo patientInfo = getPatientInfoObjFromPatientUser(patientUser);
    		if(patientInfo != null) {
				Optional<ClinicPatientAssoc> clinicPatientAssoc = clinicPatientRepository.findOneByClinicIdAndPatientId(
						clinicId, patientInfo.getId());
				PatientUserVO patientUserVO = new PatientUserVO(patientUser, patientInfo);
				if(clinicPatientAssoc.isPresent()){
					Map<String,Object> clinicMRNId = new HashMap<>();
					clinicMRNId.put("clinic", clinicPatientAssoc.get().getClinic());
					clinicMRNId.put("mrnId", clinicPatientAssoc.get().getMrnId());
					patientUserVO.setClinicMRNId(clinicMRNId);
				}
				return patientUserVO;
    		} else {
    			throw new HillromException(ExceptionConstants.HR_523);
    		}
		} else {
			throw new HillromException(ExceptionConstants.HR_512);
		}
	}

}

