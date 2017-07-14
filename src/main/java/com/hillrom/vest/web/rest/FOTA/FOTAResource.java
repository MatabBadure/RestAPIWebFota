package com.hillrom.vest.web.rest.FOTA;

import static com.hillrom.vest.config.FOTA.FOTAConstants.HEXAFILEPATH;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
			@RequestParam(value = "chunckSize", required = false) Integer chunckSize) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = fotaService.processHexaToByteData(HEXAFILEPATH, chunckSize);

			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);

		} catch (Exception ex) {
			ex.printStackTrace();
			jsonObject.put("Error Message", ex.getMessage());
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}
	}

}
