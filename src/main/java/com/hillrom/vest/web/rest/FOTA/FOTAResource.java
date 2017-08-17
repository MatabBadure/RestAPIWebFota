package com.hillrom.vest.web.rest.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.hillrom.vest.config.FOTA.FOTAConstants;
import com.hillrom.vest.domain.FOTA.FOTAInfo;
import com.hillrom.vest.service.FOTA.FOTAService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.FOTA.dto.FOTAInfoDto;

@RestController
@RequestMapping("/api")
public class FOTAResource {
	private final Logger log = LoggerFactory.getLogger(FOTAResource.class);
	
	@Inject
	private FOTAService fotaService;

	/**
	 * POST /processHexa to byte array
	 */
	@RequestMapping(value = "/processHexaToByte", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> processHexaToByteData(
			@RequestParam(value = "chunckSize", required = false) Integer chunkSize) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = fotaService.processHexaToByteData(HEXAFILEPATH, chunkSize);

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
			jsonObject.put("Error Message", ex.getMessage());
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}
	}

	
	@RequestMapping(value = "/checkUpdate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkUpdate(
			@RequestBody(required = true) String rawMessage) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = fotaService.checkUpdate(rawMessage);
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
			jsonObject.put("Error Message", ex.getMessage());
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = "/FOTA", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> FOTA(
			@RequestBody(required = true) String rawMessage) {
		String FOTAencoded = " ";
		try {
			FOTAencoded = fotaService.FOTAUpdate(rawMessage);
			return new ResponseEntity<>(FOTAencoded, HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
		}
	}
	
	/**
     * POST  /Announcement -> Create New Announcements
     */
	@RequestMapping(value="/FOTA/create", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createFOTA(@Valid @RequestBody(required=true) FOTAInfoDto fotaInfoDto){

		JSONObject jsonObject = new JSONObject();
		
		try{
			
			FOTAInfo fotaInfo = fotaService.savFotaInfoData(fotaInfoDto);
			 jsonObject.put("fotaInfo", fotaInfo);
			 if(Objects.nonNull(fotaInfo)){
				jsonObject.put("statusMsg", "fotaInfo created successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
				
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	
	/**
	   * POST /uploadFile -> receive and locally save a file.
	   * 
	   * @param uploadfile The uploaded file as Multipart file parameter in the 
	   * HTTP request. The RequestParam name must be the same of the attribute 
	   * "name" in the input tag with type file.
	   * 
	   * @return An http OK status in case of success, an http 4xx status in case 
	   * of errors.
	   * 
	   * While calling from pastman pass x-auth-token and name = uploadfile . Body should be form-data , uploadfile and ChooseFile
	   */
	  @RequestMapping(value = "/FOTA/uploadFile", method = RequestMethod.POST)
	  @ResponseBody
	  public ResponseEntity<?> FOTAUploadFile(
	      @RequestParam("uploadfile") MultipartFile uploadfile) {
	    
		  String filename = null;
		  String directory = null;
		  String filepath = null;
		  
		  JSONObject jsonObject = new JSONObject();
		  
	    try {
	    		// Get the filename and build the local file path
	    		filename = uploadfile.getOriginalFilename();
	    		directory = FOTAConstants.FOTA_FILE_PATH;
	    		File filePathDir = createUniqueDirectory(new File(directory),"File");
	    		filepath = Paths.get(filePathDir.toString(), filename).toString();
	    		// Save the file locally
	    		/*BufferedOutputStream stream =
	    				new BufferedOutputStream(new FileOutputStream(new File(filePathDir)));*/
	    		
	    		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
	    		stream.write(uploadfile.getBytes());
	    		stream.close();
	    		jsonObject.put("filepath", filepath);
	    		 return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
	    		 
	    }
	    catch (FileNotFoundException ex) {
	    	jsonObject.put("ERROR","The system cannot find the path/Directory specified");
      	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	    }
	    catch (Exception ex) {
	    	jsonObject.put("ERROR", ex.getMessage());
	    	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	    }
	  } 
	  
	  
	  @RequestMapping(value = "/FOTA/getSoftVersion/{partNoV}/{isOldFileV}", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	  @ResponseBody
	  public ResponseEntity<?> getExistingFOTA(@PathVariable("partNoV") String partNo, @PathVariable("isOldFileV") boolean isOldFile) {
		  JSONObject jsonObject = new JSONObject();
		  
	    try {
	    		// Get the filename and build the local file path
	    		 String softVer = fotaService.getFotaInforByPartNumber(partNo,isOldFile);
	    		 jsonObject.put("existingVersion", softVer);
	    		 return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
	    }
	    catch (Exception ex) {
	    	jsonObject.put("ERROR", ex.getMessage());
	    	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	    }
	  } 
	  
	  @RequestMapping(value = "/FOTA/softDeleteFOTA/{partNoD}/{isOldFileD}", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	  @ResponseBody
	  public ResponseEntity<?> softDeleteFOTA(@PathVariable("partNoD") String partNoD, @PathVariable("isOldFileD") boolean isOldFileD) {
		  JSONObject jsonObject = new JSONObject();
		  
	    try {
	    		// Get the filename and build the local file path
	    		 FOTAInfo fotaInfo = fotaService.softDeleteFOTA(partNoD,isOldFileD);
	    		 jsonObject.put("fotaInfo", fotaInfo);
	    		 if(Objects.nonNull(fotaInfo)){
	 				jsonObject.put("FOTASoftDelete", "Success");
	 				 return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
	 			}
	    }
	    catch (Exception ex) {
	    	jsonObject.put("ERROR", ex.getMessage());
	    	return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	    }
	    jsonObject.put("FOTASoftDelete", "no record");
	    return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
	  } 
	  
	  public  synchronized File createUniqueDirectory(File rootDir, String seed) throws IOException {
	      int index = seed.lastIndexOf('.');
	      if (index > 0) {
	          seed = seed.substring(0, index);
	      }
	      File result = null;
	      int count = 0;
	      while (result == null) {
	          String name = seed + "." + count;
	          File file = new File(rootDir, name);
	          if (!file.exists()) {
	              file.mkdirs();
	              result = file;
	          }
	          count++;
	      }
	      return result;
	  }
	
}
