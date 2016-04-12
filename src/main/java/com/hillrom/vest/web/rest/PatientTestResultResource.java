package com.hillrom.vest.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.PatientTestResult;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.PateintTestResultService;
import com.hillrom.vest.util.MessageConstants;

import net.minidev.json.JSONObject;

/**
 * REST controller for survey APIs.
 */
@RestController
@RequestMapping("/api")
public class PatientTestResultResource {

	private final Logger log = LoggerFactory.getLogger(PatientTestResultResource.class);

	@Inject
	private PateintTestResultService pateintTestResultService;

	/*
	 **
	 * get /testresult -> get all test result
	 */
	@RequestMapping(value = "/testresult", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getAllPatientTestResult() {
		List<PatientTestResult> patientTestResults = pateintTestResultService.getPatientTestResult();
		return new ResponseEntity<List<PatientTestResult>>(patientTestResults, HttpStatus.OK);
	}

	/*
	 **
	 * get /survey/{id} -> get test result.
	 */
	@RequestMapping(value = "/testresult/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> getPatientTestResultById(@PathVariable Long userId) throws HillromException {
		List<PatientTestResult> patientTestResults;
		patientTestResults = pateintTestResultService.getPatientTestResultByUserId(userId);
		return new ResponseEntity<List<PatientTestResult>>(patientTestResults, HttpStatus.OK);
	}
	
	/*
	 **
	 * POST /testresult -> create update test result.
	 */
	@RequestMapping(value = "/testresult/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> addPatientTestResult(@RequestBody PatientTestResult patientTestResult,@PathVariable Long userId, HttpServletRequest request) {
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		JSONObject jsonObject = new JSONObject();
		try {
			pateintTestResultService.createPatientTestResult(patientTestResult,userId,baseUrl);
			jsonObject.put("message", MessageConstants.HR_308);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/testresult/{userId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	
	@RolesAllowed({ AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN })
	public ResponseEntity<?> updatePatientTestResult(@RequestBody PatientTestResult patientTestResult,@PathVariable Long userId, HttpServletRequest request) {
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		JSONObject jsonObject = new JSONObject();
		try {
			pateintTestResultService.updatePatientTestResult(patientTestResult, userId, baseUrl);
			jsonObject.put("message", MessageConstants.HR_309);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}
}
