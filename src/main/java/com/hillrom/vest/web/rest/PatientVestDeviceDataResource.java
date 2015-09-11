package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.minidev.json.JSONObject;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.service.PatientVestDeviceDataService;
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
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveData(HttpServletRequest request){
		List<PatientVestDeviceData> deviceData = null;
		Map<String,String[]> paramsMap = request.getParameterMap();
		int i = 0;
		StringBuilder builder = new StringBuilder();
		for(String key : paramsMap.keySet()){
			String[]  values = paramsMap.get(key);
			String value = values.length > 0 ? values[0] : null;
			builder.append(key).append("=").append(value);
			++i;
			if( i < paramsMap.size()){
				builder.append("&");
			}
		}
		String rawMessage = builder.toString();
		String reqParams[] = new String[]{"device_model_type","device_data",
        "device_serial_number","device_type","hub_id","air_interface_type",
        "customer_name","cde_version","exporter_version","timezone","sp_receive_time",
        "hub_receive_time","device_address","twonet_id","hub_receive_time_offset","cuc_version","customer_id"};
		JSONObject jsonObject = RequestUtil.checkRequiredParamsInQueryString(rawMessage, reqParams);
		if(jsonObject.containsKey("ERROR")){
			return new ResponseEntity(jsonObject,HttpStatus.BAD_REQUEST);
		};
		try{			
			deviceData = deviceDataService.save(rawMessage);
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
