package com.hillrom.optimus.web.rest;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.optimus.domain.OptimusData;
import com.hillrom.optimus.service.OptimusDataService;
import com.hillrom.vest.domain.ChargerData;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.ChargerDataService;
import com.hillrom.vest.service.PatientVestDeviceDataService;
import com.hillrom.vest.service.monarch.PatientVestDeviceDataServiceMonarch;
import com.hillrom.vest.service.util.ParserUtil;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.util.PaginationUtil;


@RestController
@RequestMapping("/api")
public class PatientOptimusDeviceDataResource {
	

	
	@Inject
	private OptimusDataService optimusDataService;
	
	
	private final Logger log = LoggerFactory.getLogger(PatientOptimusDeviceDataResource.class);
	
	

	
	@RequestMapping(value = "/receiveDataOptimus",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public String receiveDataOptimus(@RequestBody(required=true)String rawMessage){



		JSONObject optimusJsonDataPOC = new JSONObject();
		String decoded_string = "";
		try{
			log.error("Base64 Received Data for ingestion in receiveDataOptimus : ",rawMessage);
			
			byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);		
	        String sout = "";
	        for(int i=0;i<decoded.length;i++) {
	        	int val = decoded[i] & 0xFF;
	        	sout = sout + val + " ";
	        }
	        log.debug("Input Byte Array :"+sout);
			decoded_string = new String(decoded);
			log.error("Decoded value is " + decoded_string);



			
			optimusJsonDataPOC =   optimusDataService.saveOrUpdateChargerData(rawMessage,decoded_string);			

			optimusJsonDataPOC.put("RESULT", "OK - ");
			return optimusJsonDataPOC.get("RESULT") + " " + optimusJsonDataPOC.get("ERROR");
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("RESULT", "NOT OK - "+e.getMessage());
			return optimusJsonDataPOC.get("RESULT") + " " + optimusJsonDataPOC.get("ERROR");
		}
	}

	

	
	@RequestMapping(value = "/optimusdevicedata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> findLatestData(){
		try{	
			JSONObject jsonObject = new JSONObject();
			OptimusData optimusData = optimusDataService.findLatestData();
	        byte[] b = java.util.Base64.getDecoder().decode(optimusData.getDeviceData());
	        String sout = "";
	        for(int i=0;i<b.length;i++) {
	        	int val = b[i] & 0xFF;
	        	sout = sout + val + " ";
	        }
	        optimusData.setDeviceData(sout);
			jsonObject.put("device_data", optimusData);
			if(optimusData.getDeviceData().length()>0)
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.CREATED);
			else
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.PARTIAL_CONTENT);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	
    /**
     * GET  /optimusDeviceData/:id 
     * get Optimus device data for the given "id".
     */
    @RequestMapping(value = "/optimusDeviceData/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> findById(@PathVariable Long id) {
        log.debug("REST request to fetch charger device data for : {}", id);
        JSONObject jsonObject = new JSONObject();
        OptimusData optimusData = optimusDataService.findById(id);
        byte[] b = java.util.Base64.getDecoder().decode(optimusData.getDeviceData());
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        optimusData.setDeviceData(sout);
    	jsonObject.put("device_data", optimusData);
        if (Objects.nonNull(optimusData)) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }

    
	@RequestMapping(value = "/optimusdevicedatalist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> findAll() {
		try{	
			JSONObject jsonObject = new JSONObject();
			Page<OptimusData> optimusDataList = optimusDataService.findAll(new PageRequest(0, 10));
			jsonObject.put("device_data", optimusDataList);
			if(Objects.nonNull(optimusDataList))
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.CREATED);
			else
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.PARTIAL_CONTENT);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
}
