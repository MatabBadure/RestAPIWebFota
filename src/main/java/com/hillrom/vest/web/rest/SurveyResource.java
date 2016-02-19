package com.hillrom.vest.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

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

import com.hillrom.vest.domain.Survey;
import com.hillrom.vest.domain.UserSurveyAnswer;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.SurveyService;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.SurveyVO;
import com.hillrom.vest.web.rest.dto.UserSurveyAnswerDTO;

import net.minidev.json.JSONObject;

/**
 * REST controller for survey APIs.
 */
@RestController
@RequestMapping("/api")
public class SurveyResource {

	private final Logger log = LoggerFactory.getLogger(SurveyResource.class);

	@Inject
	private SurveyService surveyService;

	/*
	 **
	 * get /survey -> get all. survey
	 */
	@RequestMapping(value = "/survey", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> getAllSurvey() {
		List<Survey> surveys = surveyService.getAllSurveys();
		return new ResponseEntity<List<Survey>>(surveys, HttpStatus.OK);
	}

	/*
	 **
	 * get /survey/{id} -> get Survey by Id.
	 */
	@RequestMapping(value = "/survey/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> getSurveyById(@PathVariable Long id) {
		SurveyVO surveyVO;
		try {
			surveyVO = surveyService.getSurveyById(id);
			return new ResponseEntity<SurveyVO>(surveyVO, HttpStatus.OK);
		} catch (HillromException e) {
			log.debug("Survey with id {} : Not Found", id);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/survey", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> addSurveyAnswers(@RequestBody UserSurveyAnswerDTO userSurveyAnswers) {
		JSONObject jsonObject = new JSONObject();
		try {
			System.out.println(userSurveyAnswers.toString());
			surveyService.createSurveyAnswer(userSurveyAnswers);
			jsonObject.put("message", MessageConstants.HR_306);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/surveyanswer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> getSurveyAnswerById(@PathVariable Long id) {
		UserSurveyAnswer userSurveyAnswer;
		try {
			userSurveyAnswer = surveyService.getSurveyAnswerById(id);
			return new ResponseEntity<UserSurveyAnswer>(userSurveyAnswer, HttpStatus.OK);
		} catch (HillromException e) {
			log.debug("User Survey Answer with id {} : Not Found", id);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.NOT_FOUND);
		}
	}
	/*
	 **
	 * get /duesurvey/{id} -> get Due Survey by User Id.
	 */
	@RequestMapping(value = "/survey/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> getDueSurveyByUserId(@PathVariable Long id) {
		Survey survey;
		try {
			survey = surveyService.getDueSurveyByUserId(id);
			return new ResponseEntity<Survey>(survey, HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.NOT_FOUND);
		}
	}
}
