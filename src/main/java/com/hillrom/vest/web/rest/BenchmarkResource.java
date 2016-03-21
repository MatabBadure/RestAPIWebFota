package com.hillrom.vest.web.rest;

import static com.hillrom.vest.config.Constants.KEY_BENCH_MARK_DATA;
import static com.hillrom.vest.config.Constants.KEY_RANGE_LABELS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.hillrom.vest.service.GraphService;
import com.hillrom.vest.service.util.BenchMarkUtil;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.Graph;


@RestController
@RequestMapping("/api")
public class BenchmarkResource {

	@Inject
	private BenchmarkService benchmarkService;
	
	@Qualifier("benchMarkGraphService")
	@Inject
	private GraphService benchMarkGraphService;
	
	@Qualifier("benchMarkPatientGraphService")
	@Inject
	private GraphService benchmarkPatientGraphService;

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
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	@RequestMapping(value = "/user/patient/{id}/benchmark", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBenchMarkForClinicByAgeGroup(@PathVariable Long id,
			@RequestParam(value = "from", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate from,
			@RequestParam(value = "to", required = true) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate to,
			@RequestParam(value = "benchmarkType", required = true) String benchMarkType,
			@RequestParam(value = "parameterType", required = true) String parameterType,
			@RequestParam(value = "clinicId", required = true) String clinicId,
			@RequestParam(value = "range",required=true)String range,
			@RequestParam(value = "xAxisParameter",required=false)String xAxisParameter){
		BenchMarkFilter filter = new BenchMarkFilter(from, to,range,xAxisParameter, benchMarkType, parameterType, id, clinicId);
		Map<String, SortedMap<String, BenchMarkDataVO>> benchMarkData;
		try {
			return new ResponseEntity<>(benchmarkService.getBenchMarkDataForPatientView(filter), HttpStatus.OK);
		} catch (HillromException e) {
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			JSONObject errorMessage = new JSONObject();
			errorMessage.put("ERROR", e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
