package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.domain.ChargerData;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.ChargerDataService;
import com.hillrom.vest.service.PatientVestDeviceDataService;
import com.hillrom.vest.service.util.ParserUtil;
import com.hillrom.vest.util.MessageConstants;
import com.hillrom.vest.web.rest.util.PaginationUtil;


@RestController
@RequestMapping("/api")
public class PatientVestDeviceDataResource {
	
	@Inject
	private PatientVestDeviceDataService deviceDataService;
	
	@Inject
	private ChargerDataService chargerDataService;
	
	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataResource.class);
	
    public static final char[] EXTENDED = { 0x00C7, 0x00FC, 0x00E9, 0x00E2,
            0x00E4, 0x00E0, 0x00E5, 0x00E7, 0x00EA, 0x00EB, 0x00E8, 0x00EF,
            0x00EE, 0x00EC, 0x00C4, 0x00C5, 0x00C9, 0x00E6, 0x00C6, 0x00F4,
            0x00F6, 0x00F2, 0x00FB, 0x00F9, 0x00FF, 0x00D6, 0x00DC, 0x00A2,
            0x00A3, 0x00A5, 0x20A7, 0x0192, 0x00E1, 0x00ED, 0x00F3, 0x00FA,
            0x00F1, 0x00D1, 0x00AA, 0x00BA, 0x00BF, 0x2310, 0x00AC, 0x00BD,
            0x00BC, 0x00A1, 0x00AB, 0x00BB, 0x2591, 0x2592, 0x2593, 0x2502,
            0x2524, 0x2561, 0x2562, 0x2556, 0x2555, 0x2563, 0x2551, 0x2557,
            0x255D, 0x255C, 0x255B, 0x2510, 0x2514, 0x2534, 0x252C, 0x251C,
            0x2500, 0x253C, 0x255E, 0x255F, 0x255A, 0x2554, 0x2569, 0x2566,
            0x2560, 0x2550, 0x256C, 0x2567, 0x2568, 0x2564, 0x2565, 0x2559,
            0x2558, 0x2552, 0x2553, 0x256B, 0x256A, 0x2518, 0x250C, 0x2588,
            0x2584, 0x258C, 0x2590, 0x2580, 0x03B1, 0x00DF, 0x0393, 0x03C0,
            0x03A3, 0x03C3, 0x00B5, 0x03C4, 0x03A6, 0x0398, 0x03A9, 0x03B4,
            0x221E, 0x03C6, 0x03B5, 0x2229, 0x2261, 0x00B1, 0x2265, 0x2264,
            0x2320, 0x2321, 0x00F7, 0x2248, 0x00B0, 0x2219, 0x00B7, 0x221A,
            0x207F, 0x00B2, 0x25A0, 0x00A0 };
	
	@RequestMapping(value = "/receiveData",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveData(@RequestBody(required=true)String rawMessage){

		try{


		
			log.error("Received Data for ingestion : ",rawMessage);

			JSONObject jsonObject = new JSONObject();

			
			ExitStatus exitStatus = deviceDataService.saveData(rawMessage.replaceAll("\n", "").replaceAll(" ", ""));
			jsonObject.put("message",exitStatus.getExitCode());
				
			if(ExitStatus.COMPLETED.equals(exitStatus))
				return new ResponseEntity<>(jsonObject,HttpStatus.CREATED);
			else
				return new ResponseEntity<>(jsonObject,HttpStatus.PARTIAL_CONTENT);
			
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	@RequestMapping(value = "/receiveDataCharger",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> receiveDataCharger(@RequestBody(required=true)String rawMessage){

		try{		
			log.error("Base64 Received Data for ingestion in receiveDataCharger : ",rawMessage);		
			
			byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
			int[] decoded_int = ParserUtil.convertToIntArray(decoded);
			
			String decoded_string = new String(decoded);
			log.error("Decoded value is " + decoded_string);
			String sout = "";
			for(int i=0;i<decoded_string.length();i++){
				int c = decoded_string.charAt(i);
				decoded_int[i] = c;
				sout = sout + " " + decoded_int[i];
			}
			
			log.error("Base64 Decoded Message : ",sout);
			
			JSONObject chargerJsonData = new JSONObject();
			chargerJsonData = chargerDataService.saveOrUpdateChargerData(decoded_string);
			JSONObject result = new JSONObject();
			result.put("RESULT", chargerJsonData.get("RESULT") + " - " + chargerJsonData.get("ERROR"));
			return new ResponseEntity<>(result,HttpStatus.CREATED);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	public static final char getAscii(int code) {
        if (code >= 0x80 && code <= 0xFF) {
            return EXTENDED[code - 0x7F];
        }
        return (char) code;
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
	
	@RequestMapping(value = "/chargerdevicedata",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> findLatestData(){
		try{	
			JSONObject jsonObject = new JSONObject();
			ChargerData chargerData = chargerDataService.findLatestData();
			jsonObject.put("device_data", chargerData);
			if(chargerData.getDeviceData().length()>0)
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.CREATED);
			else
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.PARTIAL_CONTENT);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
	
    /**
     * GET  /chargerdevicedata/:id -> get charger device data for the given "id".
     */
    @RequestMapping(value = "/chargerdevicedata/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JSONObject> findById(@PathVariable Long id) {
        log.debug("REST request to fetch charger device data for : {}", id);
        JSONObject jsonObject = new JSONObject();
        ChargerData chargerData = chargerDataService.findById(id);
    	jsonObject.put("device_data", chargerData);
        if (Objects.nonNull(chargerData)) {
        	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
        } else {
            return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
        }
    }

    
	@RequestMapping(value = "/chargerdevicedatalist",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> findAll() {
		try{	
			JSONObject jsonObject = new JSONObject();
			Page<ChargerData> chargerDataList = chargerDataService.findAll(new PageRequest(0, 10));
			jsonObject.put("device_data", chargerDataList);
			if(Objects.nonNull(chargerDataList))
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.CREATED);
			else
				return new ResponseEntity<JSONObject>(jsonObject,HttpStatus.PARTIAL_CONTENT);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
	}
	
}
