package com.hillrom.vest.web.rest.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hillrom.vest.service.FOTA.FOTAService;

@RestController
@RequestMapping("/api")
public class FOTAResource {
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
	
}