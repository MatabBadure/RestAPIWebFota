package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Objects;

import javax.inject.Inject;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

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

import com.hillrom.vest.exceptionhandler.HillromException;


/**
 * REST controller for managing Graph Functions.
 */
@RestController
@RequestMapping("/api")
public class GraphResource {

    private final Logger log = LoggerFactory.getLogger(GraphResource.class);



    /**
     * POST  /securityQuestions -> Post base64 Graph PDF string.
     */
    @RequestMapping(value = "/graph/pdfDownload",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Object> downloadPDF(@RequestBody Map<String, String> base64EncodedString) {
        log.debug("REST request to return base64 Encoded String", base64EncodedString);
        JSONObject jsonObject = new JSONObject();
        try{
	        if (!Objects.nonNull(base64EncodedString)) {
	            return ResponseEntity.badRequest().body(jsonObject);
	        }
	        	
	        return new ResponseEntity<Object>(base64EncodedString,HttpStatus.OK);
	        	        
		} catch (Exception e) {
			jsonObject.put("ERROR",e.getMessage()); 
			return ResponseEntity.badRequest().body(jsonObject);
		}
    }

}
