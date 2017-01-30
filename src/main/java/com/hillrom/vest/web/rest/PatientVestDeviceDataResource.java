package com.hillrom.vest.web.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
	
	private static final String FILENAME_THERAPY = "C:/development/charger/PATIENT_VEST_THERAPY_DATA_MONARCH.csv";
	private static final String FILENAME_DEVICE = "C:/development/charger/PATIENT_VEST_DEVICE_DATA_MONARCH.csv";
	
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
			
			//chargerDataService.getDeviceData(rawMessage);
			
			byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
			
	        String sout = "";
	        for(int i=0;i<decoded.length;i++) {
	        	int val = decoded[i] & 0xFF;
	        	sout = sout + val + " ";
	        }
	        
	        log.debug("Input Byte Array :"+sout);

			String decoded_string = new String(decoded);
			log.error("Decoded value is " + decoded_string);

			
			JSONObject chargerJsonData = new JSONObject();
			chargerJsonData =   chargerDataService.saveOrUpdateChargerData(rawMessage,decoded_string,null,null);
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
	
	
	@RequestMapping(value = "/parseTestDataCsv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> parseTestDataCsv(){

		try{		
					
			
			String testData[] = readcsv();
			BufferedWriter bwTherapy = createTherapyfile(FILENAME_THERAPY);
			BufferedWriter bwDevice = createDevicefile(FILENAME_DEVICE);

			for(int j=0; j<testData.length;j++){
				
				String rawMessage = testData[j];
				
				log.debug("Raw Message :"+rawMessage);
				
				byte[] decoded = java.util.Base64.getDecoder().decode(rawMessage);
				
				//log.debug("Decoded Array :"+decoded);
				
		        String sout = "";
		        for(int i=0;i<decoded.length;i++) {
		        	int val = decoded[i] & 0xFF;
		        	sout = sout + val + " ";
		        }
		        
		        log.debug("Input Byte Array :"+sout);
	
				String decoded_string = new String(decoded);
				//log.error("Decoded value is " + decoded_string);
	
				
				JSONObject chargerJsonData = new JSONObject();
				chargerJsonData =   chargerDataService.saveOrUpdateChargerData(rawMessage,decoded_string,bwTherapy,bwDevice);
				log.debug("Last Record Processed : "+ j );
			}

			bwTherapy.close();
			bwDevice.close();
			
			JSONObject result = new JSONObject();
			//result.put("RESULT", chargerJsonData.get("RESULT") + " - " + chargerJsonData.get("ERROR"));
			return new ResponseEntity<>(result,HttpStatus.CREATED);
		}catch(Exception e){
			e.printStackTrace();
			JSONObject error = new JSONObject();
			error.put("ERROR", e.getMessage());
			return new ResponseEntity<>(error,HttpStatus.PARTIAL_CONTENT);
		}
		
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
	        byte[] b = java.util.Base64.getDecoder().decode(chargerData.getDeviceData());
	        String sout = "";
	        for(int i=0;i<b.length;i++) {
	        	int val = b[i] & 0xFF;
	        	sout = sout + val + " ";
	        }
	        chargerData.setDeviceData(sout);
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
        byte[] b = java.util.Base64.getDecoder().decode(chargerData.getDeviceData());
        String sout = "";
        for(int i=0;i<b.length;i++) {
        	int val = b[i] & 0xFF;
        	sout = sout + val + " ";
        }
        chargerData.setDeviceData(sout);
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
	
	public static String[] readcsv() 
	{


	        String csvFile = "C:/development/charger/charger-test-data-1.csv";
	        String line = "";
	        String cvsSplitBy = ",";
	        String[] Outdata = new String[300];
	        String[] data = null;

	        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
	        	int i=0;
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	               data = line.split(cvsSplitBy);
	               Outdata[i++] = data[2];
	         
	            }
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return Outdata;
	}  
	


	public BufferedWriter createTherapyfile(String fileName) {

		BufferedWriter bw = null;
		int bufferSize = 29 * 1024;
		
		try {
				//bw = new BufferedWriter(new FileWriter(fileName)) ;
				  bw = new BufferedWriter(new FileWriter(fileName),bufferSize);
				
				bw.append("devSN,Wifi,devVer,session_index,start_time,end_time,start_battery_level,end_battery_level,number_of_pods,number_of_events,hmr_seconds\n");

				return bw;

		} catch (IOException e) {

			e.printStackTrace();

		}
		return bw;
		

	}
	
	public BufferedWriter createDevicefile(String fileName) {

		BufferedWriter bw = null;
		int bufferSize = 37 * 1024;
		
		try {
				//bw = new BufferedWriter(new FileWriter(fileName)) ;
				bw = new BufferedWriter(new FileWriter(fileName),bufferSize);
				
				bw.append("event_timestamp,event_code,frequency,intensity,duration\n");

			return bw;

		} catch (IOException e) {

			e.printStackTrace();

		}
		return bw;
		

	}
}
