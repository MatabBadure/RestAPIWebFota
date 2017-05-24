package com.hillrom.vest.web.rest;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.AnnouncementsService;
import com.hillrom.vest.service.TimsService;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api")
public class TimsResource {

	@Inject
	private TimsService timsService;
	
		
	/**
     * POST  /createpatientprotocolmonarch
     */
	@RequestMapping(value="/createpatientprotocolmonarch", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> test(){

		JSONObject jsonObject = new JSONObject();
		
		try{
			  timsService.createPatientProtocolMonarch("Normal","Insert","HR2015000002","App");
			  jsonObject.put("timsMsg", "createPatientProtocolMonarch stored procedure executed successfully");
			  return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);			
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		
	}
	  
	
	
}
