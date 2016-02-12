package com.hillrom.vest.web.rest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.CityStateZipMapService;

import net.minidev.json.JSONObject;

/**
 * REST controller for managing the current user's account.
 */

@RestController
@RequestMapping("/api")
public class CityStateZipMapResource {



    private final Logger log = LoggerFactory.getLogger(CityStateZipMapResource.class);

    @Inject
    private CityStateZipMapService cityStateZipMapService;

    @RequestMapping(value = "/cityStateZipMapByState",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> getCityStateZipValuesByByState(@RequestParam(required=true, value = "state") String state) {	
    	try {
			return new ResponseEntity<>(cityStateZipMapService.getStateVOByState(state), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR",e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		} 
    }
    
    @RequestMapping(value = "/allstates",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> getState() {
			return new ResponseEntity<>(cityStateZipMapService.getStates(), HttpStatus.OK);
    }
}