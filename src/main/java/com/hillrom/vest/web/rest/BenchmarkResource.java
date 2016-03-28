package com.hillrom.vest.web.rest;

import static com.hillrom.vest.config.Constants.AGE_GROUP;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.BenchmarkService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.Filter;


@RestController
@RequestMapping("/api")
public class BenchmarkResource {

	@Inject
	private BenchmarkService benchmarkService;
	
	@RequestMapping(value = "/benchmark/parameter",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBenchMarkByAgeGroupOrClinicSize(@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		@RequestParam(value="type",required=true)String benchMarkParameter,
    		@RequestParam(value="benchmarkType",required=true)String benchMarkType,
    		@RequestParam(value="state",required=false)String stateCSV,
    		@RequestParam(value="city",required=false)String cityCSV,
    		@RequestParam(value="xAxisParameter",required=false)String xAxisParameter,
    		@RequestParam(value="range",required=true)String range
    		){
		BenchMarkFilter filter = new BenchMarkFilter(from, to, xAxisParameter, benchMarkType, benchMarkParameter,stateCSV,cityCSV,range);
		try {
			return new ResponseEntity<>(benchmarkService.getBenchMarkGraphForAdminParameterView(filter),HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR",ExceptionConstants.HR_717);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@RequestMapping(value = "/user/patient/{id}/benchmark", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBenchMarkForClinicByAgeGroup(@PathVariable Long id,
			@RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
			@RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
			@RequestParam(value = "benchmarkType", required = true) String benchMarkType,
			@RequestParam(value = "parameterType", required = true) String parameterType,
			@RequestParam(value = "clinicId", required = true) String clinicId){
		BenchMarkFilter filter = new BenchMarkFilter(from, to,"All",AGE_GROUP,benchMarkType, parameterType, id, clinicId);
		try {
			return new ResponseEntity<>(benchmarkService.getBenchMarkGraphForPatientView(filter), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", ExceptionConstants.HR_717);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/benchmark/statistics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBenchMarkForClinicByAgeGroup(
			@RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
			@RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
			@RequestParam(value = "xAxisParameter",required=true)String xAxisParameter,
			@RequestParam(value = "state", required = false) String stateCSV,
			@RequestParam(value = "city", required = false) String cityCSV,
			@RequestParam(value = "ageGroupRange",required=true)String ageGroupRange,
			@RequestParam(value = "clinicSizeRange",required=true)String clinicSizeRange,
			@RequestParam(value = "ignoreXAxis",required=false)boolean ignoreXAxis
			){
		Filter filter = new Filter(from, to,xAxisParameter, stateCSV, cityCSV, ageGroupRange, clinicSizeRange,ignoreXAxis);
		try {
			return new ResponseEntity<>(benchmarkService.getClinicAndDiseaseStatsGraph(filter), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", ExceptionConstants.HR_717);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/user/hcp/{id}/benchmark", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getClinicLevelBenchMarkForHCP(@PathVariable Long id,
			@RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
			@RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
			@RequestParam(value="state",required=false)String stateCSV,
    		@RequestParam(value="city",required=false)String cityCSV,
			@RequestParam(value = "benchmarkType", required = true) String benchMarkType,
			@RequestParam(value = "parameterType", required = true) String parameterType,
			@RequestParam(value = "clinicId", required = true) String clinicId){
		BenchMarkFilter filter = new BenchMarkFilter(from, to,"All",AGE_GROUP,benchMarkType, parameterType, id, clinicId);
		filter.setCityCSV(cityCSV);
		filter.setStateCSV(stateCSV);
		try {
			return new ResponseEntity<>(benchmarkService.getClinicLevelBenchMarkGraphForHCPOrClinicAdmin(filter), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", ExceptionConstants.HR_717);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/user/clinicadmin/{id}/benchmark", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getClinicLevelBenchMarkForClinicAdmin(@PathVariable Long id,
			@RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
			@RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
			@RequestParam(value="state",required=false)String stateCSV,
    		@RequestParam(value="city",required=false)String cityCSV,
			@RequestParam(value = "benchmarkType", required = true) String benchMarkType,
			@RequestParam(value = "parameterType", required = true) String parameterType,
			@RequestParam(value = "clinicId", required = true) String clinicId){
		BenchMarkFilter filter = new BenchMarkFilter(from, to,"All",AGE_GROUP,benchMarkType, parameterType, id, clinicId);
		filter.setCityCSV(cityCSV);
		filter.setStateCSV(stateCSV);
		try {
			return new ResponseEntity<>(benchmarkService.getClinicLevelBenchMarkGraphForHCPOrClinicAdmin(filter), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", ExceptionConstants.HR_717);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
