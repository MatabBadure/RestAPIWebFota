package com.hillrom.vest.service;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_COMPLIANCE_SCORE;
import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_SETTINGS_DEVIATION_COUNT;
import static com.hillrom.vest.config.Constants.MONARCH;
import static com.hillrom.vest.config.Constants.VEST;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
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

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.monarch.service.NoteServiceMonarch;
import com.hillrom.monarch.service.PatientComplianceMonarchService;
import com.hillrom.monarch.service.PatientNoEventMonarchService;
import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.domain.UserPatientAssocPK;
import com.hillrom.vest.domain.UserSecurityQuestion;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdvancedSearchRepository;
import com.hillrom.vest.repository.AuthorityRepository;
import com.hillrom.vest.repository.ClinicPatientRepository;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserExtensionRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.OnCredentialsChangeEvent;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.service.util.RequestUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.CareGiverVO;
import com.hillrom.vest.web.rest.dto.ClinicStatsNotificationVO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.UserDTO;
import com.hillrom.vest.web.rest.dto.UserExtensionDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AdvancedSearchService {

	private final Logger log = LoggerFactory.getLogger(AdvancedSearchService.class);

     
    @Inject
	private AdvancedSearchRepository advancedSearchRepository;
    
     

	
    public Page<ClinicVO> advancedSearchClinics(AdvancedClinicDTO advancedClinicDTO,Integer offset, Integer limit,Map<String, Boolean> sortOrder)  throws HillromException {
    	Page<ClinicVO> page = advancedSearchRepository.advancedSearchClinics(advancedClinicDTO, PaginationUtil.generatePageRequest(offset, limit),sortOrder);
    	return page;
    }
	
     
}

