package com.hillrom.vest.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.service.PatientVestDeviceDataService;
import com.hillrom.vest.service.util.CsvUtil;
import com.hillrom.vest.service.util.RequestUtil;
import com.hillrom.vest.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PatientVestDeviceDataResource {
	
	@Inject
	private PatientVestDeviceDataService deviceDataService;
	
	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	@RequestMapping(value = "/receiveData",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveData(HttpServletRequest request){
		List<PatientVestDeviceData> deviceData = null;
		String reqParams[] = new String[]{"device_model_type","device_data",
        "device_serial_number","device_type","hub_id","air_interface_type",
        "customer_name","cde_version","exporter_version","timezone","sp_receive_time",
        "hub_receive_time","device_address","qcl_json_data","twonet_id","hub_receive_time_offset",
        "cuc_version","customer_id"};
		JSONObject jsonObject = RequestUtil.checkRequiredParamsInQueryString(request.getQueryString(), reqParams);
		if(jsonObject.containsKey("ERROR")){
			return new ResponseEntity(jsonObject,HttpStatus.BAD_REQUEST);
		};
		try{			
			deviceData = deviceDataService.save(request.getQueryString());
		}catch(Exception e){
			JSONObject error = new JSONObject();
			error.put("message", e.getMessage());
			return new ResponseEntity(error,HttpStatus.PARTIAL_CONTENT);
		}
		return new ResponseEntity(deviceData,HttpStatus.CREATED);
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
}
