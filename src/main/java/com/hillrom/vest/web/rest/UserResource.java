package com.hillrom.vest.web.rest;

import static com.hillrom.vest.security.AuthoritiesConstants.CLINIC_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.HCP;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
import com.hillrom.vest.repository.monarch.PatientComplianceMonarchRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.AdherenceCalculationService;
import com.hillrom.vest.service.ExcelOutputService;
import com.hillrom.vest.service.GraphService;
import com.hillrom.vest.service.PatientComplianceService;
import com.hillrom.vest.service.PatientHCPService;
import com.hillrom.vest.service.PatientProtocolService;
import com.hillrom.vest.service.PatientVestDeviceService;
import com.hillrom.vest.service.TherapySessionService;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.service.monarch.PatientHCPMonarchService;
import com.hillrom.vest.service.monarch.TherapySessionServiceMonarch;
import com.hillrom.vest.service.monarch.AdherenceCalculationServiceMonarch;
import com.hillrom.vest.service.monarch.PatientComplianceMonarchService;
import com.hillrom.vest.service.util.CsvUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.PatientComplianceVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.ProtocolDTO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.dto.StatisticsVO;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;
import com.hillrom.vest.web.rest.dto.monarch.ProtocolRevisionMonarchVO;
import com.hillrom.vest.web.rest.dto.monarch.TherapyDataMonarchVO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

import net.minidev.json.JSONObject;
/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

	private final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserSearchRepository userSearchRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private PatientVestDeviceService patientVestDeviceService;

	@Inject
	private PatientProtocolService patientProtocolService;

	@Inject
	private TherapySessionService therapySessionService;
	
	@Inject
	private AdherenceCalculationService adherenceCalculationService;
	
	@Inject
	private PatientComplianceRepository complianceRepository;
	
	@Inject
	private PatientComplianceMonarchRepository complianceMonarchRepository;
	
	@Inject
	private NotificationRepository notificationRepository;

	@Inject
	private TherapySessionRepository therapySessionRepository;

	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	@Inject
    private PatientHCPService patientHCPService;
	
	@Inject
	private PatientComplianceService patientComplianceService;

	@Inject
	private PatientComplianceMonarchService patientComplianceMonarchService;
	
	@Inject
	private ExcelOutputService excelOutputService;
	
	@Qualifier("hmrGraphService")
	@Inject
	private GraphService hmrGraphService;
	
	//hill-1847
	@Qualifier("adherenceTrendGraphService")
	@Inject
	private GraphService adherenceTrendGraphService;
    //hill-1847
	
	@Qualifier("adherenceTrendGraphServiceMonarch")
	@Inject
	private GraphService adherenceTrendGraphServiceMonarch;
    
	
	@Qualifier("complianceGraphService")
	@Inject
	private GraphService complianceGraphService;

	@Qualifier("cumulativeStatsGraphService")
	@Inject
	private GraphService cumulativeStatsGraphService;

	@Qualifier("treatmentStatsGraphService")
	@Inject
	private GraphService treatmentStatsGraphService;
	
	@Inject
	private TherapySessionServiceMonarch therapySessionServiceMonarch;
	
	@Qualifier("hmrGraphServiceMonarch")
	@Inject
	private GraphService hmrGraphServiceMonarch;
	
	@Inject
	private AdherenceCalculationServiceMonarch adherenceCalculationServiceMonarch;
	
	@Qualifier("complianceGraphServiceMonarch")
	@Inject
	private GraphService complianceGraphServiceMonarch;
	
	@Inject
    private PatientHCPMonarchService patientHCPMonarchService;
	
	/**
	 * GET /users -> get all users.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	
	public List<User> getAll() {
		log.debug("REST request to get all Users");
		return userRepository.findAll();
	}

	/**
	 * GET /users/:login -> get the "login" user.
	 */
	@RequestMapping(value = "/users/{email}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	
	ResponseEntity<User> getUser(@PathVariable String email) {
		log.debug("REST request to get User : {}", email);
		return userRepository.findOneByEmail(email)
				.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/user/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed({HCP,CLINIC_ADMIN})
	public ResponseEntity<?> searchHcp(
			@RequestParam(required = true, value = "searchString") String searchString,
			@RequestParam(required = false, value = "filter") String filter,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "sort_by", required = false) String sortBy,
			@RequestParam(value = "asc", required = false) Boolean isAscending,
			@RequestParam(value = "deviceType", required = true) String deviceType)
			throws URISyntaxException {
		if(searchString.endsWith("_")){
 		   searchString = searchString.replace("_", "\\\\_");
		}
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		Map<String, Boolean> sortOrder = new HashMap<>();
		if (StringUtils.isNotBlank(sortBy)) {
			isAscending = (isAscending != null) ? isAscending : true;
			if(sortBy.equalsIgnoreCase("email"))
				sortOrder.put("user." + sortBy, isAscending);
			else	
				sortOrder.put(sortBy, isAscending);
		}
		Page<PatientUserVO> page = userService.patientSearch(
				queryString, filter, sortOrder, deviceType, offset, limit);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
				page, "/user/patient/search", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

	}
	
	//HCP log in. Patient associated to  to HCP
   @RequestMapping(value = "/user/hcp/{id}/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	
	public ResponseEntity<?> searchPatientAssociatedToHcp(@PathVariable Long id,
			@RequestParam(required = true, value = "searchString") String searchString,
			@RequestParam(required = false, value = "clinicId") String clinicId,
			@RequestParam(required = false, value = "filter") String filter,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "sort_by", required = false) String sortBy,
			@RequestParam(value = "deviceType", required = false) String deviceType,
			@RequestParam(value = "asc", required = false) Boolean isAscending)
			throws URISyntaxException {
		if(searchString.endsWith("_")){
 		   searchString = searchString.replace("_", "\\\\_");
		}
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		Map<String, Boolean> sortOrder = new HashMap<>();
		if (StringUtils.isNotBlank(sortBy)) {
			isAscending = (isAscending != null) ? isAscending : true;
			if(sortBy.equalsIgnoreCase("email"))
				sortOrder.put("user." + sortBy, isAscending);
			else	
				sortOrder.put(sortBy, isAscending);
		}
		Page<PatientUserVO> page;
		try {
			page = userService.patientSearchUnderHCPUser(
					queryString, id, clinicId, filter,sortOrder,deviceType, offset, limit);
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/user/hcp/"+id+"/patient/search", offset, limit);
			return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
		} catch (HillromException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
		

	}
   
   @RequestMapping(value = "/user/admin/hcp/{id}/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	
  	public ResponseEntity<?> searchPatientAssociatedToHcpInAdmin(@PathVariable Long id,
  			@RequestParam(required = true, value = "searchString") String searchString,
  			@RequestParam(required = false, value = "clinicId") String clinicId,
  			@RequestParam(required = false, value = "filter") String filter,
  			@RequestParam(value = "page", required = false) Integer offset,
  			@RequestParam(value = "per_page", required = false) Integer limit,
  			@RequestParam(value = "sort_by", required = false) String sortBy,
  			@RequestParam(value = "asc", required = false) Boolean isAscending)
  			throws URISyntaxException {
  		if(searchString.endsWith("_")){
   		   searchString = searchString.replace("_", "\\\\_");
  		}
  		String queryString = new StringBuilder("'%").append(searchString)
  				.append("%'").toString();
  		Map<String, Boolean> sortOrder = new HashMap<>();
  		if (StringUtils.isNotBlank(sortBy)) {
  			isAscending = (isAscending != null) ? isAscending : true;
  			if(sortBy.equalsIgnoreCase("email"))
  				sortOrder.put("user." + sortBy, isAscending);
  			else	
  				sortOrder.put(sortBy, isAscending);
  		}
  		Page<PatientUserVO> page;
  		try {
  			page = userSearchRepository.findAssociatedPatientToHCPInAdmin(
  					queryString, id, clinicId, filter, PaginationUtil.generatePageRequest(offset, limit),
  					sortOrder);
  			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
  					page, "/user/admin/hcp/"+id+"/patient/search", offset, limit);
  			return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  		} catch (HillromException e) {
  			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
  		}
  		

  	}

   //Admin login. Associated Patient to Clinic
   @RequestMapping(value = "/user/clinic/{clinicId}/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
   @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES,AuthoritiesConstants.CLINIC_ADMIN})
	public ResponseEntity<?> searchPatientAssociatedToClinic(@PathVariable String clinicId,
			@RequestParam(required = true, value = "searchString") String searchString,
			@RequestParam(required = false, value = "filter") String filter,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "sort_by", required = false) String sortBy,
			@RequestParam(value = "asc", required = false) Boolean isAscending,
			@RequestParam(value = "deviceType", required = true) String deviceType)
			throws URISyntaxException {
		if(searchString.endsWith("_")){
		   searchString = searchString.replace("_", "\\\\_");
		}
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		Map<String, Boolean> sortOrder = new HashMap<>();
		if (StringUtils.isNotBlank(sortBy)) {
			isAscending = (isAscending != null) ? isAscending : true;
			if(sortBy.equalsIgnoreCase("email"))
				sortOrder.put("user." + sortBy, isAscending);
			else	
				sortOrder.put(sortBy, isAscending);
		}
		Page<PatientUserVO> page = userService.patientSearchByClinic(
				queryString,clinicId, filter,sortOrder, deviceType,offset, limit);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
				page, "/user/clinic/"+clinicId+"/patient/search", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/user/{id}/patient", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getPatientUser(@PathVariable Long id) {
		log.debug("REST request to get PatientUser : {}", id);
		Optional<PatientUserVO> patientUser = userService.getPatientUser(id);
		if(patientUser.isPresent()){
			return new ResponseEntity<>(patientUser.get(),
					HttpStatus.OK);
		}
		else{
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR",ExceptionConstants.HR_523);
			return new ResponseEntity<>(jsonObject,HttpStatus.NOT_FOUND);
		}
	}

	/**
     * PUT  /patient/:id/link -> link vest device with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/linkvestdevice",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> linkVestDeviceWithPatient(@PathVariable Long id, @RequestBody Map<String, Object> deviceData) {
    	log.debug("REST request to link vest device with patient user : {}", id);
        JSONObject jsonObject = new JSONObject();
		try {
			Object responseObj = patientVestDeviceService.linkVestDeviceWithPatient(id, deviceData);
			if (responseObj instanceof User) {
				jsonObject.put("ERROR", ExceptionConstants.HR_572);
				jsonObject.put("user", (User) responseObj);
				return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
			} else {
				jsonObject.put("message", MessageConstants.HR_282);
				jsonObject.put("user", (PatientVestDeviceHistory) responseObj);
				return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
			}
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * GET  /patient/:id/vestdevice -> get linked vest device with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/vestdevice",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES, AuthoritiesConstants.PATIENT})
    public ResponseEntity<JSONObject> getLinkedVestDeviceWithPatient(@PathVariable Long id) {
    	log.debug("REST request to link vest device with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
		try {
			List<PatientVestDeviceHistory> deviceList = patientVestDeviceService.getLinkedVestDeviceWithPatient(id);
			if(deviceList.isEmpty()){
     			jsonObject.put("message",MessageConstants.HR_281); //No device linked with patient.
     		} else {
     			jsonObject.put("message", MessageConstants.HR_282);//Vest devices linked with patient fetched successfully.
     			jsonObject.put("deviceList", deviceList);
     		}
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
    
    /**
     * DELETE  /patient/:id/deactivatevestdevice/:serialNumber -> deactivate vest device with {serialNumber} from patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/deactivatevestdevice/{serialNumber}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> deactivateVestDeviceFromPatient(@PathVariable Long id, @PathVariable String serialNumber) {
    	log.debug("REST request to deactivate vest device with serial number {} from patient user : {}", serialNumber, id);
    	JSONObject jsonObject = new JSONObject();
    	try {
			String message = patientVestDeviceService.deactivateVestDeviceFromPatient(id, serialNumber);
			if (StringUtils.isBlank(message)) {
				jsonObject.put("ERROR", ExceptionConstants.HR_573);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", message);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
		} catch (HillromException e) {
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
		}
    }
	
	@RequestMapping(value="/user/{id}/changeSecurityQuestion",method=RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> updateSecurityQuestion(@PathVariable Long id,@RequestBody(required=true)Map<String,String> params){
		log.debug("REST request to update Security Question and Answer {}",id,params);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = userService.updateSecurityQuestion(id,params);
			jsonObject.put("message", MessageConstants.HR_295);
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		if(jsonObject.containsKey("ERROR")){
			return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.OK);
	}
	
	/**
     * POST  /patient/:id/protocol -> add protocol with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> addProtocolToPatient(@PathVariable Long id, @RequestBody ProtocolDTO protocolDTO) {
    	log.debug("REST request to add protocol with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.addProtocolToPatient(id, protocolDTO);
	    	if (protocolList.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_559);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_241);
	        	jsonObject.put("protocol", protocolList);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * PUT  /patient/:id/protocol -> update protocol with patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> updateProtocolToPatient(@PathVariable Long id, @RequestBody List<PatientProtocolData> ppdList) {
    	log.debug("REST request to update protocol with patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try{
              
    		List<PatientProtocolData> protocolList = patientProtocolService.updateProtocolToPatient(id, ppdList);
	    	if (protocolList.isEmpty()) {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_560);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_242);
	        	jsonObject.put("protocol", protocolList);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * GET  /patient/:id/protocol -> get all protocol for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getAllProtocolsAssociatedWithPatient(@PathVariable Long id) {
    	log.debug("REST request to get protocol for patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.getActiveProtocolsAssociatedWithPatient(id);
    		if (protocolList.isEmpty()) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_243);
	        	jsonObject.put("protocol", protocolList);
	        }
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * GET  /patient/:id/protocol/:protocolId -> get protocol details with {protocolId} for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol/{protocolId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> getProtocolDetails(@PathVariable Long id, @PathVariable String protocolId) {
    	log.debug("REST request to get protocol details with {} for patient user : {}", protocolId, id);
    	JSONObject jsonObject = new JSONObject();
    	try {
    		List<PatientProtocolData> protocolList = patientProtocolService.getProtocolDetails(id, protocolId);
    		if (protocolList.isEmpty()) {
	        	jsonObject.put("ERROR", ExceptionConstants.HR_551);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_243);
	        	jsonObject.put("protocol", protocolList);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    /**
     * DELETE  /patient/:id/protocol -> delete protocol for patient {id}.
     */
    @RequestMapping(value = "/patient/{id}/protocol/{protocolId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.ACCT_SERVICES})
    public ResponseEntity<JSONObject> deleteProtocolForPatient(@PathVariable Long id, @PathVariable String protocolId) {
    	log.debug("REST request to delete protocol for patient user : {}", id);
    	JSONObject jsonObject = new JSONObject();
    	try {
	    	String message = patientProtocolService.deleteProtocolForPatient(id, protocolId);
	        if (Objects.isNull(message)) {
	        	jsonObject.put("message", MessageConstants.HR_245);
	        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	        } else {
	        	jsonObject.put("message", message);
	            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	        }
    	} catch(HillromException hre){
    		jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
    	}
    }
    
    
    @RequestMapping(value = "/users/{id}/therapyData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTherapyByPatientUserIdAndDate(@PathVariable Long id,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		@RequestParam(value="duration",required=true)String duration,
    		@RequestParam(value="deviceType",required=true)String deviceType) {
    		try{
    			if(deviceType.equals("VEST")){
    				List<TherapyDataVO> therapyData = therapySessionService.findByPatientUserIdAndDateRange(id, from, to);
    				if(therapyData.size() > 0){
    					Graph hmrGraph = hmrGraphService.populateGraphData(therapyData, new Filter(from, to, duration, null));
    					return new ResponseEntity<>(hmrGraph,HttpStatus.OK);
    				}
    			}
    			if(deviceType.equals("MONARCH")){
    				List<TherapyDataMonarchVO> therapyData = therapySessionServiceMonarch.findByPatientUserIdAndDateRange(id, from, to);
        			if(therapyData.size() > 0){
        				Graph hmrGraph = hmrGraphServiceMonarch.populateGraphData(therapyData, new Filter(from, to, duration, null));
        				return new ResponseEntity<>(hmrGraph,HttpStatus.OK);
        			}
    			}
    			return new ResponseEntity<>(HttpStatus.OK);
    			
    		}catch(Exception ex){
    			JSONObject jsonObject = new JSONObject();
            	jsonObject.put("ERROR", ExceptionConstants.HR_717);
        		return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }
    
    @RequestMapping(value = "/users/{id}/compliance",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getComplianceScoreByPatientUserIdAndDate(@PathVariable Long id,
    		@RequestParam(value="deviceType",required=true) String deviceType) {
    	
    	if(deviceType.equals("VEST")){
    		PatientCompliance compliance = patientComplianceService.findLatestComplianceByPatientUserId(id);
    		if(Objects.nonNull(compliance)){
        		if(Objects.isNull(compliance.getHmrRunRate())){
        			compliance.setHmrRunRate(0);
        		}
           		return new ResponseEntity<>(compliance,HttpStatus.OK);
        	}
    	}else if (deviceType.equals("MONARCH")){
    		PatientComplianceMonarch compliance = patientComplianceMonarchService.findLatestComplianceByPatientUserId(id);
    		if(Objects.nonNull(compliance)){
        		if(Objects.isNull(compliance.getHmrRunRate())){
        			compliance.setHmrRunRate(0);
        		}
           		return new ResponseEntity<>(compliance,HttpStatus.OK);
        	}
    	}    	
    	
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/users/{id}/notifications",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Notification>> getNotificationsByPatientUserId(@PathVariable Long id,
    		@RequestParam(value="from",required=false)Long from,
    		@RequestParam(value="to",required=false)Long to,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException{
    	
    	LocalDate fromDate = Objects.isNull(from) ? LocalDate.now().minusDays(1) : LocalDate.fromDateFields(new Date(from));
    	LocalDate toDate = Objects.isNull(to) ? LocalDate.now() : LocalDate.fromDateFields(new Date(to));
    	Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
    	Page<Notification> page = notificationRepository.findByPatientUserIdAndDateBetweenAndIsAcknowledged(id, fromDate,toDate,false, pageable);
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users/"+id+"/notifications", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/users/{userId}/notifications/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> acknowledgeNotification(@PathVariable Long userId,
    		@PathVariable Long id,@RequestBody(required=true)Map<String,String> params){
    	JSONObject json = new JSONObject();
    	Optional<Notification> notificationFromDB = Optional.of(notificationRepository.findOne(id));
	    	if(notificationFromDB.isPresent()){
	    		Notification notification = notificationFromDB.get();
	    		if(notification.getPatientUser().getId().equals(userId) 
	    				&& SecurityUtils.getCurrentLogin().equalsIgnoreCase(notification.getPatientUser().getEmail())){
	    			boolean isAcknowledged  = "TRUE".equalsIgnoreCase(params.get("isAcknowledged")) ? true : false;
	    			notification.setAcknowledged(isAcknowledged);
	    			notificationRepository.save(notification);
	    			json.put("notification", notification);
	    			return new ResponseEntity<>(json,HttpStatus.OK);
	    		}else{
	    			json.put("ERROR", ExceptionConstants.HR_403);
	    			return new ResponseEntity<>(json,HttpStatus.FORBIDDEN);
	    		}
	    	}else{
	    		json.put("ERROR", ExceptionConstants.HR_591);
	    		return new ResponseEntity<>(json,HttpStatus.NOT_FOUND);
	    	}    
    	}
    
    /**
     * PUT  /user/:id/notifications -> update HRM notification setting for user  {id}.
     */
    @RequestMapping(value = "/users/{id}/notificationsetting",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.PATIENT, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<JSONObject> updateUserNotification(@PathVariable Long id, @RequestBody Map<String, Boolean> paramsMap) {
    	JSONObject json = new JSONObject();
    	try {
			json.put("user", userService.setUserNotificationSetting(id, paramsMap));
			return new ResponseEntity<>(json,HttpStatus.OK);
		} catch (HillromException e) {
			json.put("ERROR", e.getMessage());
			return new ResponseEntity<>(json,HttpStatus.NOT_FOUND);
		}
    }
    
    @RequestMapping(value = "/users/{id}/missedTherapyCount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> getMissedTherapyCount(@PathVariable Long id){
    	JSONObject json = new JSONObject();
    	json.put("count",patientComplianceService.getMissedTherapyCountByPatientUserId(id));
    	return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/users/{id}/exportTherapyData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TherapySession>> exportTherapyData(@PathVariable Long id,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to){
    	List<TherapySession> therapySessions = therapySessionRepository.findByPatientUserIdAndDateRange(id, from, to);
    	return new ResponseEntity<>(therapySessions,HttpStatus.OK);
    }
    
    @RequestMapping(value = "/users/{id}/exportTherapyDataCSV",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportTherapyDataCSV(@PathVariable Long id,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		HttpServletResponse response) throws UnsupportedEncodingException, IOException{
    	List<TherapySession> therapySessions = therapySessionRepository.findByPatientUserIdAndDateRange(id, from, to);
    	ICsvBeanWriter beanWriter = null;
    	CellProcessor[] processors = CsvUtil.getCellProcessorForTherapySessionData();
    	try {
            beanWriter = new CsvBeanWriter(response.getWriter(),
                    CsvPreference.STANDARD_PREFERENCE);
			String[] header = CsvUtil.getHeaderValuesForTherapySessionCSV();
			String[] headerMapping = CsvUtil.getHeaderMappingForTherapySessionData();
            if(therapySessions.size() > 0 ){
            	beanWriter.writeHeader(header);
                for (TherapySession session : therapySessions) {
                    beanWriter.write(session, headerMapping,processors);
                }
            }else{
            	response.setStatus(204);
            }
        } catch (Exception ex) {
        	response.setStatus(500);
        } finally {
            if (beanWriter != null) {
                try {
                    beanWriter.close();
                } catch (IOException ex) {
                	response.setStatus(500);
                }
            }
        }
    }

    @RequestMapping(value = "/users/{id}/exportVestDeviceData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> exportVestDeviceData(
			@PathVariable Long id,
			@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
			@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to) {
		Long fromTimestamp = from.toDateTimeAtStartOfDay().getMillis();
		Long toTimestamp = to.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).getMillis();
		List<PatientVestDeviceData> vestDeviceData = deviceDataRepository.findByPatientUserIdAndTimestampBetween(id, fromTimestamp, toTimestamp);
		return new ResponseEntity<>(vestDeviceData,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users/{id}/exportVestDeviceDataCSV",
			produces="application/vnd.ms-excel",
            method = RequestMethod.GET)
	public void exportVestDeviceDataCSV(
			@PathVariable Long id,
			@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
			@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
			HttpServletResponse response) {
		
		Long fromTimestamp = from.toDateTimeAtStartOfDay().getMillis();
		Long toTimestamp = to.toDateTimeAtStartOfDay().plusHours(23).plusMinutes(59).plusSeconds(59).getMillis();
		List<PatientVestDeviceData> vestDeviceData = deviceDataRepository.findByPatientUserIdAndTimestampBetween(id, fromTimestamp, toTimestamp);
    	try {
            if(vestDeviceData.size() > 0 ){
            	excelOutputService.createExcelOutputExcel(response, vestDeviceData);
            }else{
            	response.setStatus(204);
            }
        } catch (Exception ex) {
        	response.setStatus(500);
        } 
	}
	
	/**
     * GET  /users/:userId/clinics/:clinicId/statistics -> get the patient statistics for clinic associated with user.
     */
    @RequestMapping(value = "/users/{userId}/clinics/{clinicId}/statistics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<?> getPatientStatisticsForClinicAssociated(@PathVariable Long userId, @PathVariable String clinicId,
    		@RequestParam(value="deviceType",required=true) String deviceType) {
        log.debug("REST request to get patient statistics for clinic {} associated with User : {}", clinicId, userId);
        JSONObject jsonObject = new JSONObject();
        try {
        	Map<String, Object> statitics = null;
        	LocalDate date = LocalDate.now();

        	if(deviceType.equals("VEST")) {
        		statitics = patientHCPService.getTodaysPatientStatisticsForClinicAssociatedWithHCP(clinicId, date);
        	}
        	else if(deviceType.equals("MONARCH")) {
        		statitics = patientHCPMonarchService.getTodaysPatientStatisticsForClinicAssociatedWithHCP(clinicId, date);
        	}
        	else if(deviceType.equals("ALL")) {
        		statitics = patientHCPMonarchService.getTodaysPatientStatisticsForClinicAssociatedWithHCPAll(clinicId, date);
        	}
        	if(statitics.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_584);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_297);
	        	jsonObject.put("statitics", statitics);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /users/:userId/clinics/:clinicId/patients -> get the patient list filter by metric type for clinic associated with user.
     */
    @RequestMapping(value = "/users/{userId}/clinics/{clinicId}/patients",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<?> getPatientsFilterByMetricTypeForClinicAssociated(@PathVariable Long userId, @PathVariable String clinicId,
    		@RequestParam(value = "filterBy",required = false) String filterBy) {
        log.debug("REST request to get patient list filter by metric type for clinic {} associated with User : {}", clinicId, userId);
        JSONObject jsonObject = new JSONObject();
        try {
        	LocalDate date = LocalDate.now();
        	List<PatientComplianceVO> patientUsers = patientHCPService.getPatientListFilterByMetricForClinicAssociated(userId, clinicId, date, filterBy);
	        if (patientUsers.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_585);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_213);
	        	jsonObject.put("patientUsers", patientUsers);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * GET  /users/:userId/clinics/:clinicId/patients/noevents -> get the patient list with no events for clinic associated with user.
     */
    @RequestMapping(value = "/users/{userId}/clinics/{clinicId}/patients/noevents",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<?> getPatientsWithNoEventsForClinicAssociated(@PathVariable Long userId, @PathVariable String clinicId) {
        log.debug("REST request to get patient list with no event for clinic {} associated with User : {}", clinicId, userId);
        JSONObject jsonObject = new JSONObject();
        try {
        	LocalDate date = LocalDate.now();
        	List<PatientUserVO> patientUsers = patientHCPService.getPatientsWithNoEventsForClinicAssociated(userId, clinicId, date);
	        if (patientUsers.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_585);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_213);
	        	jsonObject.put("patientUsers", patientUsers);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * GET  /users/:hcpId/clinics/:clinicId/cumulativeStatistics -> get the patient statistics for clinic associated with hcp user.
     * @throws Exception 
     */
    @RequestMapping(value = "/users/{hcpId}/clinics/{clinicId}/cumulativeStatistics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP})
    public ResponseEntity<?> getPatientsCumulativeStatisticsForClinicAssociatedWithHCP(@PathVariable Long hcpId, @PathVariable String clinicId,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		@RequestParam(value = "deviceType", required = true) String deviceType){
        log.debug("REST request to get patients cumulative statistics for clinic {} associated with HCP : {}", clinicId, hcpId,from,to);
        JSONObject jsonObject = new JSONObject();
        try {
        	Collection<StatisticsVO> statiticsCollection = null;
        	if(deviceType.equals("VEST")){
	        	statiticsCollection = patientHCPService.getCumulativePatientStatisticsForClinicAssociatedWithHCP(hcpId,clinicId,from,to);		      
        	}
        	if(deviceType.equals("MONARCH")){
        		statiticsCollection = patientHCPMonarchService.getCumulativePatientStatisticsForClinicAssociatedWithHCP(hcpId,clinicId,from,to);
        	}
        	if (statiticsCollection.isEmpty()) {
        		return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        	} else {
        		Graph cumulativeStatsGraph = cumulativeStatsGraphService.populateGraphData(statiticsCollection, new Filter(from,to,null,null));
        		return new ResponseEntity<>(cumulativeStatsGraph, HttpStatus.OK);
		    }
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        } catch(Exception ex){
        	jsonObject.put("ERROR", ExceptionConstants.HR_717);
    		return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET  /users/:hcpId/clinics/:clinicId/treatmentStatistics -> get the patient statistics for clinic associated with hcp user.
     * @throws Exception 
     */
    @RequestMapping(value = "/users/{hcpId}/clinics/{clinicId}/treatmentStatistics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP})
    public ResponseEntity<?> getPatientsTreatmentStatisticsForClinicAssociatedWithHCP(@PathVariable Long hcpId, @PathVariable String clinicId,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		@RequestParam(value = "deviceType", required = true) String deviceType) {
        log.debug("REST request to get patients treatement statistics for clinic {} associated with HCP : {}", clinicId, hcpId,from,to);
        JSONObject jsonObject = new JSONObject();
        try {
        	Collection<TreatmentStatisticsVO> statiticsCollection = null;
        	if(deviceType.equals("VEST")){
        	statiticsCollection = patientHCPService.getTreatmentStatisticsForClinicAssociatedWithHCP(hcpId,clinicId,from,to);
        	}
        	if(deviceType.equals("MONARCH")){
        	statiticsCollection = patientHCPService.getTreatmentStatisticsForClinicAssociatedWithHCP(hcpId,clinicId,from,to);
        	}
        	if (statiticsCollection.isEmpty()) {
   	        	return new ResponseEntity<>(HttpStatus.OK);
   	        } else {
   	        	Graph treatmentStatsGraph = treatmentStatsGraphService.populateGraphData(statiticsCollection, new Filter(from,to,null,null));
   		        return new ResponseEntity<>(treatmentStatsGraph, HttpStatus.OK);
   	        }
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        } catch(Exception ex){
        	jsonObject.put("ERROR", ExceptionConstants.HR_717);
    		return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * GET  /patient/:patientUserId/clinic/:clinicId/mrnId -> get the patient user with clinic mrn id.
     */
    @RequestMapping(value = "/patient/{patientUserId}/clinic/{clinicId}/{caUserId}/mrnId", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
	public ResponseEntity<JSONObject> getPatientUserWithMRNId(@PathVariable Long patientUserId,@PathVariable String clinicId,@PathVariable Long caUserId) {
		log.debug("REST request to get patient user with clinic mrn id : {}", patientUserId);
		JSONObject jsonObject = new JSONObject();
        try {
			PatientUserVO patientUser = userService.getPatientUserWithMRNId(patientUserId,clinicId,caUserId);
			if (Objects.isNull(patientUser)) {
	        	jsonObject.put("message", ExceptionConstants.HR_585);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_213);
	        	jsonObject.put("patientUser", patientUser);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
	}
    
    //Patient associated with clinic admin by clinic id
    @RequestMapping(value = "/user/clinicadmin/{id}/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.CLINIC_ADMIN})
	public ResponseEntity<?> searchPatientAssociatedToClinicAdmin(@PathVariable Long id,
			@RequestParam(required = true, value = "searchString") String searchString,
			@RequestParam(required = false, value = "clinicId") String clinicId,
			@RequestParam(required = false, value = "filter") String filter,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "sort_by", required = false) String sortBy,
			@RequestParam(value = "asc", required = false) Boolean isAscending,
			@RequestParam(value = "deviceType", required = false) String deviceType)
			throws URISyntaxException {
		if(searchString.endsWith("_")){
 		   searchString = searchString.replace("_", "\\\\_");
		}
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		Map<String, Boolean> sortOrder = new HashMap<>();
		if (StringUtils.isNotBlank(sortBy)) {
			isAscending = (isAscending != null) ? isAscending : true;
			if(sortBy.equalsIgnoreCase("email"))
				sortOrder.put("user." + sortBy, isAscending);
			else	
				sortOrder.put(sortBy, isAscending);
		}
		
		Page<PatientUserVO> page = userService.associatedPatientSearchInClinicAdmin(id,
				queryString,clinicId, filter,sortOrder, deviceType,offset, limit);
		
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
				page, "/user/clinicadmin/"+id+"/patient/search", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

	}

    @RequestMapping(value = "/user/hcp/{id}/clinic/patient/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
	public ResponseEntity<?> searchPatientAssociatedToHCPAndClinic(@PathVariable Long id,
			@RequestParam(required = true, value = "searchString") String searchString,
			@RequestParam(required = false, value = "clinicId") String clinicId,
			@RequestParam(required = false, value = "filter") String filter,
			@RequestParam(value = "page", required = false) Integer offset,
			@RequestParam(value = "per_page", required = false) Integer limit,
			@RequestParam(value = "sort_by", required = false) String sortBy,
			@RequestParam(value = "asc", required = false) Boolean isAscending,
			@RequestParam(value = "deviceType", required = false) String deviceType)
			throws URISyntaxException {
		if(searchString.endsWith("_")){
 		   searchString = searchString.replace("_", "\\\\_");
		}
		String queryString = new StringBuilder("'%").append(searchString)
				.append("%'").toString();
		Map<String, Boolean> sortOrder = new HashMap<>();
		if (StringUtils.isNotBlank(sortBy)) {
			isAscending = (isAscending != null) ? isAscending : true;
			if(sortBy.equalsIgnoreCase("email"))
				sortOrder.put("user." + sortBy, isAscending);
			else	
				sortOrder.put(sortBy, isAscending);
		}
		Page<PatientUserVO> page = userSearchRepository.findAssociatedPatientToClinicAdminBy(
				queryString, id, clinicId, filter, PaginationUtil.generatePageRequest(offset, limit),
				sortOrder,deviceType);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
				page, "/user/hcp/"+id+"/clinic/patient/search", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

	}

    @RequestMapping(value="/user/{id}/securityQuestion",
    		method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> getSecurityQuestion(@PathVariable Long id){
		log.debug("REST request to get Security Question for user {}",id);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("question", userService.getSecurityQuestion(id));
			jsonObject.put("message", MessageConstants.HR_304);
			return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.OK);
		} catch (HillromException e) {
			jsonObject.put("ERROR",e.getMessage());
			return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.BAD_REQUEST);
		}
	}
    
    @RequestMapping(value="/user/{id}/adherenceTrend",
    		method=RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAdherenceTrendForDuration(@PathVariable Long id,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to){
    	log.debug("REST request to get Adherence Trend for the duration : ", id,from,to);
    	try {

            List<ProtocolRevisionVO> adherenceTrends = patientComplianceService.findAdherenceTrendByUserIdAndDateRange(id,from,to);
            return new ResponseEntity<>(adherenceTrends,HttpStatus.OK);	
		} catch (HillromException e) {
			// TODO: handle exception
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ERROR", e.getMessage());
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		} catch(Exception e){
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

    @RequestMapping(value = "/users/{id}/complianceGraphData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getComplianceGraphData(@PathVariable Long id,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		@RequestParam(value = "deviceType", required = true) String deviceType){
    	try{
    		if(deviceType.equals("VEST")){
    		List<TherapyDataVO> therapyData = therapySessionService.getComplianceGraphData(id, from, to);
    		if(therapyData.size() > 0){
    			ProtocolConstants protocol = adherenceCalculationService.getProtocolByPatientUserId(id);
    			Map<String,Object> therapyAndProtocolData = new HashMap<>();
    			therapyAndProtocolData.put("protocol", protocol);
    			therapyAndProtocolData.put("therapyData", therapyData);
    			Graph complianceGraph = complianceGraphService.populateGraphData(therapyAndProtocolData, new Filter(from,to,null,null));
    			return new ResponseEntity<>(complianceGraph,HttpStatus.OK); 
    		}
    		}
    		if(deviceType.equals("MONARCH")){
        		List<TherapyDataMonarchVO> therapyData = therapySessionServiceMonarch.getComplianceGraphData(id, from, to);
        		if(therapyData.size() > 0){
        			ProtocolConstantsMonarch protocol = adherenceCalculationServiceMonarch.getProtocolByPatientUserId(id);
        			Map<String,Object> therapyAndProtocolData = new HashMap<>();
        			therapyAndProtocolData.put("protocol", protocol);
        			therapyAndProtocolData.put("therapyData", therapyData);
        			Graph complianceGraph = complianceGraphServiceMonarch.populateGraphData(therapyAndProtocolData, new Filter(from,to,null,null));
        			return new ResponseEntity<>(complianceGraph,HttpStatus.OK); 
        		}
        		}
    		return new ResponseEntity<>(HttpStatus.OK);
    	} catch(Exception ex){
    		JSONObject jsonObject = new JSONObject();
        	jsonObject.put("ERROR", ExceptionConstants.HR_717);
    		return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
  //hill-1847
    @RequestMapping(value = "/users/{id}/adherenceTrendGraphData", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAdherenceTrendGraphData(@PathVariable Long id,
    		@RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
    		@RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
    		@RequestParam(value = "duration", required = true) String duration,
    		@RequestParam(value = "deviceType", required = true) String deviceType) {
    		try {
    			
    			List<ProtocolRevisionVO> adherenceTrendData = null;
    			List<ProtocolRevisionMonarchVO> adherenceTrendDataMonarch = null;
    			
    			if(deviceType.equals("VEST")){
    				adherenceTrendData = patientComplianceService.findAdherenceTrendByUserIdAndDateRange(id, from, to);
    			}else if(deviceType.equals("MONARCH")){
    				adherenceTrendDataMonarch = patientComplianceMonarchService.findAdherenceTrendByUserIdAndDateRange(id, from, to);
    			}
    			
    			if (Objects.nonNull(adherenceTrendData) &&  adherenceTrendData.size() > 0) {
    				Graph adherenceTrendGraph = adherenceTrendGraphService.populateGraphData(adherenceTrendData, new Filter(from,to, duration, null));
    				return new ResponseEntity<>(adherenceTrendGraph, HttpStatus.OK);
    			}
    			else if(Objects.nonNull(adherenceTrendDataMonarch) &&  adherenceTrendDataMonarch.size() > 0) {
    				Graph adherenceTrendGraphMonarch = adherenceTrendGraphServiceMonarch.populateGraphData(adherenceTrendDataMonarch, new Filter(from,to, duration, null));
    				return new ResponseEntity<>(adherenceTrendGraphMonarch, HttpStatus.OK);
    			}
    			return new ResponseEntity<>(HttpStatus.OK);
    		} catch (Exception ex) {
    			JSONObject jsonObject = new JSONObject();
    			jsonObject.put("ERROR", ExceptionConstants.HR_717);
    			return new ResponseEntity<>(jsonObject, HttpStatus.INTERNAL_SERVER_ERROR);
    		}
    }
    //hill-1847
    
    
    /**
     * GET  /users/:userId/clinics/:clinicId/statistics -> get the patient statistics for clinic Badge associated with user.
     */
    @RequestMapping(value = "/users/{userId}/clinics/{clinicId}/badgestatistics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    @RolesAllowed({AuthoritiesConstants.ADMIN, AuthoritiesConstants.HCP, AuthoritiesConstants.CLINIC_ADMIN})
    public ResponseEntity<?> getPatientStatisticsForClinicBadgeAssociatedWithUser(@PathVariable Long userId, @PathVariable String clinicId,
    		@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
			@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to) {
        log.debug("REST request to get patient badge statistics for clinic {} associated with User : {}", clinicId, userId);
        JSONObject jsonObject = new JSONObject();
        try {
        	LocalDate date = LocalDate.now();
        	Map<String, Object> statitics = patientHCPService.getTodaysPatientStatisticsForClinicAssociatedWithHCP(clinicId, from, to);
	        if (statitics.isEmpty()) {
	        	jsonObject.put("message", ExceptionConstants.HR_584);
	        } else {
	        	jsonObject.put("message", MessageConstants.HR_297);
	        	jsonObject.put("statitics", statitics);
	        }
	        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
        } catch (HillromException hre){
        	jsonObject.put("ERROR", hre.getMessage());
    		return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }
    
    
}