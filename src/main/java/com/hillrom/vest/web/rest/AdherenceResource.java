package com.hillrom.vest.web.rest;

import static com.hillrom.vest.config.Constants.YYYY_MM_DD;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.AdherenceReset;
import com.hillrom.vest.domain.AdherenceResetMonarch;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.TherapySessionMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdherenceResetRepository;
import com.hillrom.vest.repository.AdhrenceResetHistoryRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.PredicateBuilder;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.repository.monarch.AdherenceResetHistoryMonarchRepository;
import com.hillrom.vest.repository.monarch.PatientComplianceMonarchRepository;
import com.hillrom.vest.repository.monarch.TherapySessionMonarchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.AdherenceResetService;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.service.PatientNoEventService;
import com.hillrom.vest.service.monarch.AdherenceCalculationServiceMonarch;
import com.hillrom.vest.service.monarch.AdherenceResetServiceMonarch;
import com.hillrom.vest.service.monarch.PatientNoEventMonarchService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.AdherenceResetDTO;
import com.hillrom.vest.web.rest.dto.AdherenceResetHistoryVO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HillRomUserVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.PaginationUtil;
import com.mysema.query.types.expr.BooleanExpression;

import net.minidev.json.JSONObject;

import org.joda.time.DateTime;

/**
 * REST controller for managing Clinic.
 */
@RestController
@RequestMapping("/api")
public class AdherenceResource {

    private final Logger log = LoggerFactory.getLogger(AdherenceResource.class);

    @Inject
    private AdherenceResetRepository adherenceResetRepository;
    
    @Inject
    private AdhrenceResetHistoryRepository adhrenceResetHistoryRepository;
    
    @Inject
    private AdherenceResetHistoryMonarchRepository adhrenceResetHistoryMonarchRepository;
    
    @Inject
    private AdherenceResetService adherenceResetService;

    @Inject
    private AdherenceResetServiceMonarch adherenceResetServiceMonarch;    
    
    @Inject
	private AdherenceCalculationService adherenceCalculationService;
    
    @Inject
	private AdherenceCalculationServiceMonarch adherenceCalculationServiceMonarch;
    
	@Inject
	private PatientComplianceRepository patientComplianceRepository;
	
    @Inject
	private PatientNoEventService noEventService;

    @Inject
	private TherapySessionRepository therapyRepository;
    
	@Inject
	private PatientComplianceMonarchRepository patientComplianceMonarchRepository;
	
    @Inject
	private PatientNoEventMonarchService noEventMonarchService;
    
	@Inject
	private TherapySessionMonarchRepository therapyMonarchRepository;
	
    /**
     * POST  /clinics -> Create a new clinic.
     */
    @RequestMapping(value = "/adherenceReset",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> create(@RequestBody AdherenceResetDTO adherenceResetDTO) {
        log.debug("REST request to save Adherence Reset : {}", adherenceResetDTO);
        
        String patientId = adherenceResetDTO.getPatientId();
        String userId = adherenceResetDTO.getUserId();
		String resetStartDate = adherenceResetDTO.getResetStartDate();
		String resetScore = adherenceResetDTO.getResetScore();
		String justification = adherenceResetDTO.getJustification();
		String createdById = adherenceResetDTO.getCreatedBy();
		String deviceType = adherenceResetDTO.getDeviceType();
		 
    	
		JSONObject jsonObject = new JSONObject();
		//hill-1847
		DateTime resetDt = DateUtil.getCurrentDateAndTime();
		//hill-1847
		LocalDate resetStartDt = null;
		try {
			resetStartDt	= StringUtils.isNoneBlank(resetStartDate)? DateUtil.parseStringToLocalDate(resetStartDate, YYYY_MM_DD) : LocalDate.now();
			
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		    	
		try {
			
					
			// Getting the compliance record for the user on adhrence start date
			PatientCompliance patientCompliance = patientComplianceRepository.findByDateAndPatientUserId(resetStartDt,Long.parseLong(userId));
			
			// Getting the event record for the user 
			PatientNoEvent noEvent = noEventService.findByPatientUserId(Long.parseLong(userId));
			
			
			// Getting the compliance record for the user on adherence start date for monarch
			PatientComplianceMonarch patientComplianceMonarch = patientComplianceMonarchRepository.findByDateAndPatientUserId(resetStartDt,Long.parseLong(userId));
			
			// Getting the event record for the user in monarch 
			PatientNoEventMonarch noEventMonarch = noEventMonarchService.findByPatientUserId(Long.parseLong(userId));
			
			// Check for existing adherence score is 100
			if( (deviceType.equals("VEST") && Objects.nonNull(patientCompliance) && patientCompliance.getScore() == 100)
					|| ((deviceType.equals("MONARCH") || deviceType.equals("ALL")) && Objects.nonNull(patientComplianceMonarch) && patientComplianceMonarch.getScore() == 100)){
				jsonObject.put("ERROR", "Adherence score cannot be reset for existing adherence score of 100");
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
			}
			else if( (deviceType.equals("VEST") && (Objects.isNull(noEvent) || Objects.isNull(noEvent.getFirstTransmissionDate()))) ||
					(deviceType.equals("MONARCH") && (Objects.isNull(noEventMonarch) || Objects.isNull(noEventMonarch.getFirstTransmissionDate()))) ||
					(deviceType.equals("ALL") && ((Objects.isNull(noEvent) && Objects.isNull(noEventMonarch)) || 
							(Objects.nonNull(noEventMonarch) && Objects.isNull(noEventMonarch.getFirstTransmissionDate()))) && 
							Objects.isNull(noEvent.getFirstTransmissionDate())) ){
				
				// Check for the non transmission users
				jsonObject.put("ERROR", "Adherence score cannot be reset for the non transmissions users");
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
			}else{
				TherapySession therapy = therapyRepository.findTop1ByPatientUserIdOrderByDateAsc(Long.parseLong(userId));
				TherapySessionMonarch therapyMonarch = therapyMonarchRepository.findTop1ByPatientUserIdOrderByDateAsc(Long.parseLong(userId));
				
				LocalDate firstTherapyDate = Objects.nonNull(therapy) ? therapy.getDate() : null;
				LocalDate firstTherapyDateMonarch = Objects.nonNull(therapyMonarch) ? therapyMonarch.getDate() : null;
				
				if((Objects.nonNull(firstTherapyDate) && resetStartDt.isBefore(firstTherapyDate)) || 
						(Objects.nonNull(firstTherapyDateMonarch) && resetStartDt.isBefore(firstTherapyDateMonarch))){
					jsonObject.put("ERROR", "Adherence start date should be after first therapy date");
		            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
				}
				
			}
			AdherenceReset adherenceReset = null;
			AdherenceResetMonarch adherenceResetMonarch = null; 
					
			if(deviceType.equals("VEST")){
				adherenceReset = adherenceResetService.createAdherenceReset(patientId, Long.parseLong(userId), resetDt, 
																					Integer.parseInt(resetScore), resetStartDt, justification, Long.parseLong(createdById));
			}else if(deviceType.equals("MONARCH") || deviceType.equals("ALL")){
				adherenceResetMonarch = adherenceResetServiceMonarch.createAdherenceReset(patientId, Long.parseLong(userId), resetDt, 
					Integer.parseInt(resetScore), resetStartDt, justification, Long.parseLong(createdById));
			}
			
			String errMsg = "";
	        if (Objects.nonNull(adherenceReset) && Objects.isNull(adherenceResetMonarch)) {
				// For recalculating adherence score with the adherence start date
	        	errMsg = adherenceCalculationService.adherenceResetForPatient(Long.parseLong(userId), patientId, resetStartDt, Integer.parseInt(resetScore), 1);
	        	//jsonObject.put("message", MessageConstants.HR_313);
	        	jsonObject.put("message", errMsg);
	            jsonObject.put("AdherenceReset", adherenceReset);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        } else if (Objects.isNull(adherenceReset) && Objects.nonNull(adherenceResetMonarch)) {
				// For recalculating adherence score with the adherence start date
	        	
	        	if(deviceType.equals("ALL")){
	        		errMsg = adherenceCalculationServiceMonarch.adherenceCalculationBoth(Long.parseLong(userId), patientId, resetStartDt, noEventMonarch.getFirstTransmissionDate(), Integer.parseInt(resetScore), Long.parseLong(userId), 1);
	        	}else{
	        		errMsg = adherenceCalculationServiceMonarch.adherenceResetForPatient(Long.parseLong(userId), patientId, resetStartDt, Integer.parseInt(resetScore), 1);
	        	}
	        	//jsonObject.put("message", MessageConstants.HR_313);
	        	jsonObject.put("message", errMsg);
	            jsonObject.put("AdherenceReset", adherenceReset);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        } else {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_720);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    
    //Hill-2133
    /**
     * GET  /adherenceResetHistory -> Get Adherence Reset History For Patient
     */
    @RequestMapping(value = "/user/{id}/AdherenceResetHistoryForPatient",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<?> adherenceResetHistoryForPatient(@PathVariable("id") Long userId,
			@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending,
            @RequestParam(value = "deviceType", required = true) String deviceType) throws URISyntaxException  {
        
    	
		Map<String,Boolean> sortOrder = new HashMap<>();
    	if(sortBy != null  && !sortBy.equals("")) {
    		isAscending =  (isAscending != null)?  isAscending : true;
    		sortOrder.put(sortBy, isAscending);
    	}
    	Page<AdherenceResetHistoryVO> page = null;
    	JSONObject jsonObject = new JSONObject();
    	
		try {
			if(deviceType.equals("VEST")){
				page = adhrenceResetHistoryRepository.getAdherenceResetHistoryForPatient(userId,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
				jsonObject.put("Adherence_Reset_History", page);
			}else if(deviceType.equals("MONARCH")){
				page = adhrenceResetHistoryMonarchRepository.getAdherenceResetHistoryForPatient(userId,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
				jsonObject.put("Adherence_Reset_History", page);
			}
			else if(deviceType.equals("ALL")){
				page = adhrenceResetHistoryMonarchRepository.getAdherenceResetHistoryForPatientAll(userId,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
				jsonObject.put("Adherence_Reset_History", page);
			}
			
			 if(Objects.nonNull(page)){
				jsonObject.put("AdherenceResetHistoryMessage", "Adherence Reset History retrieved successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
			}
		} catch (HillromException e) {			
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);        
    }

   
	
	private Map<String,String> getSearchParams(String filterString){
		
		Map<String,String> filterMap = new HashMap<>();
		if(StringUtils.isEmpty(filterString))
			return filterMap;
		
		String[] filters = filterString.split(";");
		for(String filter : filters){
			
			String[] pair = filter.split(":");
			if(pair.length>1)
			if(!StringUtils.isEmpty(pair[1]))
				filterMap.put(pair[0],pair[1]);
		}
		return filterMap;
	}
	
	   /**
     * GET  /adherenceResetList -> get all adherence reset list for patient / user.
     */
    @RequestMapping(value = "/adherenceResetList",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<List<AdherenceReset>> getAdherenceResetList(@RequestParam(value = "user" , required = false) Long userId,
    								@RequestParam(value = "patient" , required = false) String patientId)
        throws HillromException {
    	

		JSONObject jsonObject = new JSONObject();
		
		try{
			List<AdherenceReset> adherenceResetList = adherenceResetService.findOneByPatientUserIdAndCreatedByAndResetDate(userId,patientId);
			if(Objects.nonNull(adherenceResetList)){
				return new ResponseEntity<>(adherenceResetList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
