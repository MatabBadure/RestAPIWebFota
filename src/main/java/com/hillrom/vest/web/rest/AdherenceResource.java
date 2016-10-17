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
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdherenceResetRepository;
import com.hillrom.vest.repository.PredicateBuilder;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.AdherenceResetService;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.AdherenceResetDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.PaginationUtil;
import com.mysema.query.types.expr.BooleanExpression;

import net.minidev.json.JSONObject;

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
    private AdherenceResetService adherenceResetService;

    @Inject
	private AdherenceCalculationService adherenceCalculationService;
	
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
		 
    	
		JSONObject jsonObject = new JSONObject();
		
		LocalDate resetDt = LocalDate.now();
		LocalDate resetStartDt = null;
		try {
			resetStartDt	= StringUtils.isNoneBlank(resetStartDate)? DateUtil.parseStringToLocalDate(resetStartDate, YYYY_MM_DD) : LocalDate.now();
			
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		    	
		try {
			AdherenceReset adherenceReset = adherenceResetService.createAdherenceReset(patientId, Long.parseLong(userId), resetDt, 
																					Integer.parseInt(resetScore), resetStartDt, justification, Long.parseLong(createdById));
	        if (Objects.isNull(adherenceReset)) {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_720);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
				// For recalculating adherence score with the adherence start date
	        	String errMsg = adherenceCalculationService.adherenceResetForPatient(Long.parseLong(userId), patientId, resetStartDt, Integer.parseInt(resetScore));
	        	jsonObject.put("message", errMsg);
	            jsonObject.put("AdherenceReset", adherenceReset);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
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

}
