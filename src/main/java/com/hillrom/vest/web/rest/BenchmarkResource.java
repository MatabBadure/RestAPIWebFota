package com.hillrom.vest.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.service.BenchmarkService;
import com.hillrom.vest.service.GraphService;
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

	@RequestMapping(value = "/benchmark/parameter",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBenchMarkByAgeGroupOrClinicSize(
			@RequestParam(value="from",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate from,
    		@RequestParam(value="to",required=true)@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate to,
    		@RequestParam(value="type",required=true)String benchMarkParameter,
    		@RequestParam(value="benchmarkType",required=true)String benchMarkType,
    		@RequestParam(value="state",required=false)String stateCSV,
    		@RequestParam(value="city",required=false)String cityCSV,
    		@RequestParam(value="xAxisParameter",required=false)String xAxisParameter,
    		@RequestParam(value="range",required=true)String range
    		){
		BenchMarkFilter filter = new BenchMarkFilter(from, to, xAxisParameter, benchMarkType, benchMarkParameter,stateCSV,cityCSV,range);
		List<BenchMarkDataVO> benchMarkData = benchmarkService.getBenchmarkDataByAgeGroupOrClinicSize(filter);
		Graph benchMarkGraph = benchMarkGraphService.populateGraphData(benchMarkData, filter);
		return new ResponseEntity<>(benchMarkGraph,HttpStatus.OK);
	}
}
