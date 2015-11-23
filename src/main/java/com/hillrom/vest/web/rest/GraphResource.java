package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

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

import com.hillrom.vest.domain.SecurityQuestion;
import com.hillrom.vest.repository.SecurityQuestionRepository;

/**
 * REST controller for managing Graph Functions.
 */
@RestController
@RequestMapping("/api")
public class GraphResource {

    private final Logger log = LoggerFactory.getLogger(GraphResource.class);

    @Inject
    private SecurityQuestionRepository securityQuestionRepository;

    /**
     * POST  /securityQuestions -> Create a new securityQuestion.
     */
    @RequestMapping(value = "/graph/pdfDownload",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<JSONObject> downloadPDF(@Valid @RequestBody String base64EncodedString) throws URISyntaxException {
        log.debug("REST request to return base64 Encoded String", base64EncodedString);
        JSONObject jsonObject = new JSONObject();
        try{
	        if (Object.isNull(base64EncodedString)) {
	            return ResponseEntity.badRequest().body(jsonObject);;
	        }else{
	        	jsonObject = base64EncodedString;
	        }
	        return ResponseEntity.ok().body(jsonObject);
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage()); 
			return ResponseEntity.badRequest().body(jsonObject);
		}
    }

}
