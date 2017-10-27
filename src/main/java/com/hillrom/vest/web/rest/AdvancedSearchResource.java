package com.hillrom.vest.web.rest;

import static com.hillrom.vest.security.AuthoritiesConstants.CLINIC_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.HCP;
import static com.hillrom.vest.config.Constants.VEST;
import static com.hillrom.vest.config.Constants.MONARCH;
//import static com.hillrom.vest.config.Constants.ALL;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.DefaultEvaluationContextProvider;
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

import com.hillrom.monarch.repository.PatientComplianceMonarchRepository;
import com.hillrom.monarch.repository.PatientMonarchDeviceDataRepository;
import com.hillrom.monarch.service.AdherenceCalculationServiceMonarch;
import com.hillrom.monarch.service.PatientComplianceMonarchService;
import com.hillrom.monarch.service.PatientHCPMonarchService;
import com.hillrom.monarch.service.PatientProtocolMonarchService;
import com.hillrom.monarch.service.PatientVestDeviceMonarchService;
import com.hillrom.monarch.service.TherapySessionServiceMonarch;
import com.hillrom.monarch.web.rest.dto.ProtocolMonarchDTO;
import com.hillrom.monarch.web.rest.dto.ProtocolRevisionMonarchVO;
import com.hillrom.monarch.web.rest.dto.TherapyDataMonarchVO;
import com.hillrom.vest.domain.Notification;
import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;

import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientProtocolDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.domain.TherapySession;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.AdvancedSearchRepository;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.NotificationRepository;
import com.hillrom.vest.repository.PatientComplianceRepository;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.TherapySessionRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.UserSearchRepository;
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
import com.hillrom.vest.service.util.CsvUtil;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.PatientComplianceVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.hillrom.vest.web.rest.dto.ProtocolDTO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.dto.StatisticsVO;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

import net.minidev.json.JSONObject;
/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class AdvancedSearchResource {

	private final Logger log = LoggerFactory.getLogger(AdvancedSearchResource.class);

	
  	@Inject
	private AdvancedSearchRepository advancedSearchRepository;


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
  			page = advancedSearchRepository.advancedSearchClinics(advancedClinicDTO,PaginationUtil.generatePageRequest(offset, limit),sortOrder);
  			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/clinics/advanced/search", offset, limit);
  			return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
  		} catch (HillromException e) {
  			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
  		}
  		

  	}

  
}