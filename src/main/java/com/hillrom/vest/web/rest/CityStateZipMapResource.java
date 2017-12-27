package com.hillrom.vest.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.CityStateZipMapService;
import com.hillrom.vest.web.rest.dto.CountryStateDTO;

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

	@RequestMapping(value = "/cityStateZipMapByState", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getCityStateZipValuesByByState(
			@RequestParam(required = true, value = "state") String state) {
		try {
			return new ResponseEntity<>(cityStateZipMapService.getStateVOByState(state), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR", e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/cityStateZipMapByZip", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getCityStateZipValuesByByZipcode(
			@RequestParam(required = true, value = "zipcode") String zipcode) {
		try {
			return new ResponseEntity<>(cityStateZipMapService.getbyZipCode(zipcode), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR", e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/allstates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getState() {
		return new ResponseEntity<>(cityStateZipMapService.getStates(), HttpStatus.OK);
	}

	@RequestMapping(value = "/availableStates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getAvailableState() {
		return new ResponseEntity<>(cityStateZipMapService.getAvailableStates(), HttpStatus.OK);
	}

	@RequestMapping(value = "/availableCities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getCityByState(@RequestParam(required = true, value = "state") String state) {
		try {
			return new ResponseEntity<>(cityStateZipMapService.getAvailableCities(state), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR", e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		}
	}
	
	// To get the state list along with cities
	@RequestMapping(value = "/cityByCountryAndState", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCityByCountryAndState(@RequestBody CountryStateDTO countryStateDTO){
		try {
			return new ResponseEntity<>(cityStateZipMapService.getAvailableCitiesAdv(countryStateDTO),HttpStatus.OK);
		} catch (Exception e) {
			JSONObject json = new JSONObject();
			json.put("ERROR", e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		}
	}
	
	//To get unique states from country code
	@RequestMapping(value = "/stateByCountryCode", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	public ResponseEntity<?> getStateValuesByCountryCode(@RequestBody CountryStateDTO countryStateDTO){
		try {
			List<String> country = countryStateDTO.getCountry();
			return new ResponseEntity<>(cityStateZipMapService.getStateVOByCountryCode(country), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject json = new JSONObject();
			json.put("ERROR", e.getMessage());
			return new ResponseEntity<>(json, HttpStatus.BAD_REQUEST);
		}
	}
}