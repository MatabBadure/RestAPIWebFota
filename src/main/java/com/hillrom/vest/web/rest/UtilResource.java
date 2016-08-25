package com.hillrom.vest.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.HillromTypeCodeFormatRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.util.MessageConstants;


/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class UtilResource {

    private final Logger log = LoggerFactory.getLogger(UtilResource.class);

    @Inject
    private HillromTypeCodeFormatRepository hillromTypeCodeFormatRepository;   
    
    
    /**
     * GET  /listTypeCode/{codeType} -> get all the speciality from type code.
     */
    @RequestMapping(value = "/listTypeCode/{codeType}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> getListTypeCode(@PathVariable String codeType) throws HillromException {
        log.debug("REST request to get List of Type code : {}", codeType);
        JSONObject jsonObject = new JSONObject();
        List<String> typeCodeList = hillromTypeCodeFormatRepository.findTypeCodeListVal(codeType);
        if(typeCodeList.isEmpty()){
        	jsonObject.put("message", MessageConstants.HR_312);
        }else{        
        	jsonObject.put("message", MessageConstants.HR_311);
        	jsonObject.put("typeCode", typeCodeList);
        }
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
    }
}
