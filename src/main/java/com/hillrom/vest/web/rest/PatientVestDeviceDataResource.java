package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.service.PatientVestDeviceDataService;
import com.hillrom.vest.web.rest.util.PaginationUtil;


@RestController
@RequestMapping("/api")
public class PatientVestDeviceDataResource {
	
	@Inject
	private PatientVestDeviceDataService deviceDataService;
	
	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataResource.class);
	
	@RequestMapping(value = "/receiveData",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveData(@RequestBody(required=true)String rawMessage){
		log.debug("Received Data for ingestion : ",rawMessage);
		try{
			JSONObject jsonObject = new JSONObject();
			ExitStatus exitStatus = deviceDataService.saveData(rawMessage.replaceAll("\n", "").replaceAll(" ", ""));
			jsonObject.put("message",exitStatus.getExitCode());
			if(ExitStatus.COMPLETED.equals(exitStatus))
				return new ResponseEntity<>(jsonObject,HttpStatus.CREATED);
			else
				return new ResponseEntity<>(jsonObject,HttpStatus.PARTIAL_CONTENT);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@RequestMapping(value = "/vestdevicedata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PatientVestDeviceData>> getAll(@RequestParam(value="page",required=false)Integer pageNo,
			@RequestParam(value="per_page",required=false)Integer per_page) throws URISyntaxException{
		Page<PatientVestDeviceData> page = deviceDataRepository.findAll(PaginationUtil.generatePageRequest(pageNo, per_page));
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/vestdevicedata", pageNo, per_page);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}
}
