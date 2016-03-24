package com.hillrom.vest.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.Survey;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.SurveyService;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.FiveDayViewVO;
import com.hillrom.vest.web.rest.dto.SurveyGraph;
import com.hillrom.vest.web.rest.dto.SurveyVO;
import com.hillrom.vest.web.rest.dto.UserSurveyAnswerDTO;

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
	public ResponseEntity<?> addSurveyAnswers(@RequestBody UserSurveyAnswerDTO userSurveyAnswers, HttpServletRequest request) {
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
		JSONObject jsonObject = new JSONObject();
		try {
			surveyService.createSurveyAnswer(userSurveyAnswers,baseUrl);
			jsonObject.put("message", MessageConstants.HR_307);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/surveyanswer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT })
	public ResponseEntity<?> getSurveyAnswerById(@PathVariable Long id) {
		UserSurveyAnswerDTO surveyAnswerDTO;
		try {
			surveyAnswerDTO = surveyService.getSurveyAnswerById(id);
			return new ResponseEntity<UserSurveyAnswerDTO>(surveyAnswerDTO, HttpStatus.OK);
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
			jsonObject.put("MESSAGE", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.ACCEPTED);
		}
	}
	
	@RequestMapping(value = "/survey/gridview/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES })
	public ResponseEntity<?> getSurveyGridViewById(@PathVariable Long id,
			@RequestParam(required = true, value = "fromDate")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate fromDate,
  			@RequestParam(required = true, value = "toDate")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate toDate) {
		try {
			return new ResponseEntity<JSONObject>(surveyService.getGridView(id, fromDate, toDate), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/survey/answerbyquestion/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES })
	public ResponseEntity<?> getSurveyAnswerByQuestionId(@PathVariable Long id,
			@RequestParam(required = true, value = "fromDate")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
  			@RequestParam(required = true, value = "toDate")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to) {
		try {
			return new ResponseEntity< List<FiveDayViewVO>>(surveyService.getSurveyAnswerByQuestionId(id, from, to), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/survey/{id}/graph", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getSurveyGraphById(@PathVariable Long id,
			@RequestParam(required = true, value = "from")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
  			@RequestParam(required = true, value = "to")@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to) {
		try{
			return new ResponseEntity< SurveyGraph>(surveyService.getSurveyGraphById(id, from, to), HttpStatus.OK);
		}catch(HillromException e){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
	}

}
