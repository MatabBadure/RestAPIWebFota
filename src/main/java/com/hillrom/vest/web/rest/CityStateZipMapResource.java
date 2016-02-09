package com.hillrom.vest.web.rest;

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

import com.google.inject.Inject;
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

    /**
     * get  /CityStateZipValuesByCity -> get city state zip values by city the user.
     */
    @RequestMapping(value = "/cityStateZipValuesByCity",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> getCityStateZipValuesByCity(@RequestParam(value = "city") String city, HttpServletRequest request) {
    	
    	try {
			return new ResponseEntity<>(cityStateZipMapService.getByCityName(city), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR",e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		} 
    }
    

    /**
     * get  /CityStateZipValuesByCity -> get city state zipcode values by zipcode the user.
     */
    @RequestMapping(value = "/cityStateZipValuesByZipCode",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> getCityStateZipValuesByZipCode(@RequestParam(value = "zipcode") int zipCode, HttpServletRequest request) {
    	
    	try {
			return new ResponseEntity<>(cityStateZipMapService.getByZipCode(zipCode), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR",e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		} 
    }
    
    /**
     * get  /CityStateZipValuesByCity -> get city state zip values by state the user.
     */
    @RequestMapping(value = "/cityStateZipValuesByState",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<?> getCityStateZipValuesByZipCode(@RequestParam(value = "state") String state, HttpServletRequest request) {
    	
    	try {
    		System.out.println("cityStateZipMapService :: "+cityStateZipMapService);
			return new ResponseEntity<>(cityStateZipMapService.getByState(state), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR",e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		} 
    }
}