package com.hillrom.vest.service.monarch;


import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientCompliance;
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
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.OnCredentialsChangeEvent;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.MailService;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.service.PatientComplianceService;
import com.hillrom.vest.service.PatientInfoService;
import com.hillrom.vest.service.PatientNoEventService;
import com.hillrom.vest.service.PatientVestDeviceService;
import com.hillrom.vest.service.UserSecurityQuestionService;
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

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserMonarchService {

	private final Logger log = LoggerFactory.getLogger(UserMonarchService.class);

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
    
    @Inject
	private PatientComplianceService complianceService;
    
    @Inject
    private PatientVestDeviceService patientVestDeviceService;
    
    @Inject
    private EntityUserRepository entityUserRepository;
    
    @Inject
	private NoteService noteService;





	public User getUserObjFromPatientInfo(PatientInfo patientInfo) {
		User patientUser = null;
		for(UserPatientAssoc patientAssoc : patientInfo.getUserPatientAssoc()){
			if(RelationshipLabelConstants.SELF.equals(patientAssoc.getRelationshipLabel())){
				patientUser = patientAssoc.getUser();
			}
		}
		return patientUser;
	}

	

	

	
}

