package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.AdvancedSearchService;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.AdvancedHcpDTO;
import com.hillrom.vest.web.rest.dto.AdvancedPatientDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HcpVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class AdvancedSearchResource {

private final Logger log = LoggerFactory.getLogger(AdvancedSearchResource.class);

	
  	@Inject
	private AdvancedSearchService advancedSearchService;


    /**
     * POST  /clinics/advanced/search -> Advanced Search for clinics.
     */
   @RequestMapping(value = "/clinics/advanced/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	
  	public ResponseEntity<?> searchPatientAssociatedToHcpInAdmin(@RequestBody AdvancedClinicDTO advancedClinicDTO,
  			@RequestParam(value = "page", required = false) Integer offset,
  			@RequestParam(value = "per_page", required = false) Integer limit,
  			@RequestParam(value = "sort_by", required = false) String sortBy,
  			@RequestParam(value = "asc", required = false) Boolean isAscending)
  			throws URISyntaxException {

	  	 Map<String,Boolean> sortOrder = new HashMap<>();
	  	 if(sortBy != null  && !sortBy.equals("")) {
	  		 isAscending =  (isAscending != null)?  isAscending : true;
	  		 sortOrder.put(sortBy, isAscending);
	  	 }

  		Page<ClinicVO> page;
  		
  		try {
  			page = advancedSearchService.advancedSearchClinics(advancedClinicDTO,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
  			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/clinics/advanced/search", offset, limit);
  			return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  		} catch (HillromException e) {
  			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
  		}
  	}
   
   /**
    * POST  /advancedSearch/patient -> Advanced Search for patients.
    */
@RequestMapping(value = "/advancedSearch/patient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE) 	
 	public ResponseEntity<?> searchPatientsAssociated(@RequestBody AdvancedPatientDTO advancedPatientDTO,
 			@RequestParam(value = "page", required = false) Integer offset,
 			@RequestParam(value = "per_page", required = false) Integer limit,
 			@RequestParam(value = "sort_by", required = false) String sortBy,
 			@RequestParam(value = "asc", required = false) Boolean isAscending)
 			throws URISyntaxException {

	  	 Map<String,Boolean> sortOrder = new HashMap<>();
	  	 if(sortBy != null  && !sortBy.equals("")) {
	  		 isAscending =  (isAscending != null)?  isAscending : true;
	  		 sortOrder.put(sortBy, isAscending);
	  	 }

 		Page<PatientUserVO> page;
 		
 		try {
 			page = advancedSearchService.advancedSearchPatients(advancedPatientDTO,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
 			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/advancedSearch/patient", offset, limit);
 			return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
 		} catch (HillromException e) {
 			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
 		}
 	}




/*@RequestMapping(value = "/advancedSearch/hcp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE) 	

	public ResponseEntity<?> searchHcpsAssociated(@RequestBody AdvancedHcpDTO advancedHcpDTO,
    		//@RequestParam(value = "clinicId", required=false)String associatedToClinicId,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending)
			throws URISyntaxException {

	  	 Map<String,Boolean> sortOrder = new HashMap<>();
	  	 if(sortBy != null  && !sortBy.equals("")) {
	  		 isAscending =  (isAscending != null)?  isAscending : true;
	  		 sortOrder.put(sortBy, isAscending);
	  	 }

		Page<HcpVO> page;
		
		try {
			page = advancedSearchService.advancedSearchHcps(advancedHcpDTO,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/clinics/advanced/search", offset, limit);
			return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
		} catch (HillromException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}

	}*/

}
