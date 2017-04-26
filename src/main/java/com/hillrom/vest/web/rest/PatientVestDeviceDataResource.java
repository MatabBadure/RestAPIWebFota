package com.hillrom.vest.web.rest;

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
public class PatientVestDeviceDataResource {
	
	@Inject
	private PatientVestDeviceDataService deviceDataService;
	
	@Inject
	private PatientVestDeviceDataServiceMonarch deviceDataServiceMonarch;
	
	@Inject
	private ChargerDataService chargerDataService;
	
	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataResource.class);
	
	
	@RequestMapping(value = "/receiveData",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveData(@RequestBody(required=true)String rawMessage){

		try{


		
			log.error("Received Data for ingestion : ",rawMessage);

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
	
	@RequestMapping(value = "/receiveDataCharger",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveDataCharger(@RequestBody(required=true)String rawMessage){


		try{
			log.error("Base64 Received Data for ingestion in receiveDataCharger : ",rawMessage);

      JSONObject chargerJsonData = new JSONObject();
			
			ExitStatus exitStatus = deviceDataServiceMonarch.saveData(rawMessage);
			
			if(ExitStatus.COMPLETED.equals(exitStatus)){
				chargerJsonData.put("RESULT", "OK - ");
				return new ResponseEntity<>(chargerJsonData,HttpStatus.CREATED);
			}
			else{
				chargerJsonData.put("RESULT", "NOT OK - UnKnown Error");
				return new ResponseEntity<>(chargerJsonData,HttpStatus.PARTIAL_CONTENT);

			}
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("RESULT", "NOT OK - "+e.getMessage());
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
	
	@RequestMapping(value = "/chargerdevicedata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> findLatestData(){
		try{	
			JSONObject jsonObject = new JSONObject();
			ChargerData chargerData = chargerDataService.findLatestData();
	        byte[] b = java.util.Base64.getDecoder().decode(chargerData.getDeviceData());
	        String sout = "";
	        for(int i=0;i<b.length;i++) {
	        	int val = b[i] & 0xFF;
	        	sout = sout + val + " ";
	        }
	        chargerData.setDeviceData(sout);
			jsonObject.put("device_data", chargerData);
			if(chargerData.getDeviceData().length()>0)
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
     * GET  /chargerdevicedata/:id -> get charger device data for the given "id".
     */
    @RequestMapping(value = "/chargerdevicedata/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> findById(@PathVariable Long id) {
        log.debug("REST request to fetch charger device data for : {}", id);
        JSONObject jsonObject = new JSONObject();
        ChargerData chargerData = chargerDataService.findById(id);
        byte[] b = java.util.Base64.getDecoder().decode(chargerData.getDeviceData());
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        chargerData.setDeviceData(sout);
    	jsonObject.put("device_data", chargerData);
        if (Objects.nonNull(chargerData)) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }

    
	@RequestMapping(value = "/chargerdevicedatalist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> findAll() {
		try{	
			JSONObject jsonObject = new JSONObject();
			Page<ChargerData> chargerDataList = chargerDataService.findAll(new PageRequest(0, 10));
			jsonObject.put("device_data", chargerDataList);
			if(Objects.nonNull(chargerDataList))
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
