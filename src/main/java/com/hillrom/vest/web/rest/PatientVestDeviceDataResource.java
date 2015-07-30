package com.hillrom.vest.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.service.PatientVestDeviceDataService;

@RestController
@RequestMapping("/api")
public class PatientVestDeviceDataResource {

	@Inject
	private PatientVestDeviceDataService deviceDataService;
	
	@RequestMapping(value = "/receiveData",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveData(HttpServletRequest request){
		List<PatientVestDeviceData> deviceData =deviceDataService.save(request.getQueryString());
		return new ResponseEntity(deviceData,HttpStatus.CREATED);
	}
}
