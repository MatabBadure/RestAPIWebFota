package com.hillrom.vest.web.rest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.HillromTypeCodeFormat;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.HillromTypeCodeFormatService;
import com.hillrom.vest.util.MessageConstants;


/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class UtilResource {

    private final Logger log = LoggerFactory.getLogger(UtilResource.class);

    @Inject
    private HillromTypeCodeFormatService hillromTypeCodeFormatService;
    
    
    /**
     * GET  /listTypeCode/{codeType} -> get all the speciality from type code.
     */
    @RequestMapping(value = "/codeValues/{codeType}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getListTypeCode(@PathVariable String codeType) throws HillromException {
        log.debug("REST request to get List of Type code : {}", codeType);
        JSONObject jsonObject = new JSONObject();
        List<String> typeCodeList = hillromTypeCodeFormatService.findCodeValuesList(codeType);
        if(typeCodeList.isEmpty()){
        	jsonObject.put("message", MessageConstants.HR_312);
        }else{        
        	jsonObject.put("message", MessageConstants.HR_311);
        	jsonObject.put("typeCode", typeCodeList);
        }
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
    }
    
    /**
     * GET  /patient/diagnosis -> get all the diagnosis from type code or description.
     */
    @RequestMapping(value = "patient/diagnosis",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getDiagnosticTypeCode(@RequestParam String searchString) throws HillromException {
        log.debug("REST request to get List of Patient Diagnosis : {}", searchString);
        JSONObject jsonObject = new JSONObject();
        searchString = "%"+searchString+"%";
        List<HillromTypeCodeFormat> typeCodeList = hillromTypeCodeFormatService.getDiagnosisTypeCode(searchString);
        if(typeCodeList.isEmpty()){
        	jsonObject.put("message", MessageConstants.HR_312);
        }else{        
       	jsonObject.put("message", MessageConstants.HR_311);
        	jsonObject.put("typeCode", typeCodeList);
        }
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
    }
    
    /**
     * GET /user/timezone -> get all the generic time zones list 
     * @return
     * @throws HillromException
     */
    @RequestMapping(value = "/user/timezone",
    		method = RequestMethod.GET,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> genericTimeZonesList() throws HillromException {
    	log.debug("REST request to get timezones : ");
    	JSONObject jsonObject = new JSONObject();
    	Map<String,String> map = new LinkedHashMap<String, String>();
		
    	try {
			map = hillromTypeCodeFormatService.getTimeZones();
			if(map.size() > 0){
				jsonObject.put("message","Time zones fetched succesfully");
				jsonObject.put("timezones",map);
			}
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
}
