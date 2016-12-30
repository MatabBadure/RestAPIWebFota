package com.hillrom.vest.web.rest;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
public class AnnouncementsResource {

	@Inject
	private AnnouncementsService announcementsService;
	
		
	/**
     * POST  /Announcement -> Create New Announcements
     */
	@RequestMapping(value="/announcement/create", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAnnouncement(@Valid @RequestBody(required=true) AnnouncementsDTO announcementsDTO){

		JSONObject jsonObject = new JSONObject();
		
		try{
			 Announcements announcements = announcementsService.savAnnouncementData(announcementsDTO);
			 jsonObject.put("Announcement", announcements);
			 if(Objects.nonNull(announcements)){
				jsonObject.put("statusMsg", "Announcement created successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
				
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	/**
     * GET  /Announcement -> get All Announcements
     */
	@RequestMapping(value="/announcements/getAll", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAnnouncements(@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending,
            @RequestParam(value = "userType", required = false) String userType,
            @RequestParam(value = "userTypeId", required = false) Long userId,
            @RequestParam(value = "patientId", required = false) String patientId){

	   	 Map<String,Boolean> sortOrder = new HashMap<>();
	   	 if(sortBy != null  && !sortBy.equals("")) {
	   		 isAscending =  (isAscending != null)?  isAscending : true;
	   		 sortOrder.put(sortBy, isAscending);
	   	 }
	   	 
		JSONObject jsonObject = new JSONObject();
		
		try{
			Page<Announcements> announcementsList = null;
			if(userType.equalsIgnoreCase(AuthoritiesConstants.ADMIN)){
				announcementsList = announcementsService.findAllAnnouncements(PaginationUtil.generatePageRequest(offset, limit, sortOrder));
			}
			if(userType.equalsIgnoreCase(AuthoritiesConstants.CLINIC_ADMIN) || userType.equalsIgnoreCase(AuthoritiesConstants.HCP)){
				announcementsList = announcementsService.findVisibleAnnouncementsById(userType,userId,null,PaginationUtil.generatePageRequest(offset, limit, sortOrder),sortOrder);	
			}
			if(userType.equalsIgnoreCase(AuthoritiesConstants.PATIENT)){
				announcementsList = announcementsService.findVisibleAnnouncementsById(userType,null,patientId,PaginationUtil.generatePageRequest(offset, limit, sortOrder),sortOrder);	
			}
			
			 jsonObject.put("Announcement_List", announcementsList);
			 if(Objects.nonNull(announcementsList)){
				jsonObject.put("announcementMsg", "All Announcements retrieved successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	
	/**
     * GET  /announcement -> get announcement based on id
     */
	@RequestMapping(value="/announcement/{id}/details", method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getAnnouncementDetails(@PathVariable("id") Long id){

		JSONObject jsonObject = new JSONObject();
		
		try{
			  Announcements announcement = announcementsService.findAnnouncementById(id);
			 jsonObject.put("Announcement", announcement);
			 if(Objects.nonNull(announcement)){
				jsonObject.put("announcementMsg", "Announcement with id : " + id + " retrieved successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	


	
	/**
     * POST  /announcement -> update announcement based on id
     */
	@RequestMapping(value="/announcement/update", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateAnnouncementDetails(@Valid @RequestBody(required=true) AnnouncementsDTO announcementsDTO){

		JSONObject jsonObject = new JSONObject();
		
		try{
			  Announcements announcement = announcementsService.updateAnnouncementById(announcementsDTO);
			 jsonObject.put("Announcement", announcement);
			 if(Objects.nonNull(announcement)){
				jsonObject.put("announcementMsg", "Announcement updated successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	/**
     * POST  /announcement -> delete announcement based on id
     */
	@RequestMapping(value="/announcement/{id}/delete", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteAnnouncementDetails(@PathVariable("id") Long id){

		JSONObject jsonObject = new JSONObject();
		
		try{
			  Announcements announcement = announcementsService.deleteAnnouncementById(id);
			 jsonObject.put("Announcement", announcement);
			 if(Objects.nonNull(announcement)){
				jsonObject.put("announcementMsg", "Announcement deleted successfully");
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
	  @RequestMapping(value = "/announcement/uploadFile", method = RequestMethod.POST)
	  @ResponseBody
	  public ResponseEntity<?> AnnouncementUploadFile(
	      @RequestParam("uploadfile") MultipartFile uploadfile) {
	    
		  String filename = null;
		  String directory = null;
		  String filepath = null;
		  
		  JSONObject jsonObject = new JSONObject();
		  
	    try {
	    		// Get the filename and build the local file path
	    		filename = uploadfile.getOriginalFilename();
	    		directory = Constants.ANNOUNCEMENT_FILE_PATH;
	    		filepath = Paths.get(directory, filename).toString();
	      
	    		// Save the file locally
	    		BufferedOutputStream stream =
	    				new BufferedOutputStream(new FileOutputStream(new File(filepath)));
	    		stream.write(uploadfile.getBytes());
	    		stream.close();
	    		
	    		 jsonObject.put("filepath", filepath);
	    		 
	    		 return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
	    		 
	    }
	    catch (Exception ex) {
	    	jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	  
	  } 
	  
	  /**
	   * 
	   * @param fileName
	   * @param response
	   */
	  @RequestMapping(value = "/announcement/files/{file_name}", method = RequestMethod.GET)
	  public ResponseEntity<?> AnnouncementGetFile(
	      @PathVariable("file_name") String fileName, 
	      HttpServletResponse response) {
		  
		  		JSONObject jsonObject = new JSONObject();
	      try {
	    	  // get your file as InputStream
	    	  
	    	  File initialFile = new File(Constants.ANNOUNCEMENT_FILE_PATH + fileName + ".pdf");
	    	  InputStream is = new FileInputStream(initialFile);  
	    	  response.addHeader("Content-disposition", "inline;filename="+Constants.ANNOUNCEMENT_FILE_PATH + fileName);
	    	  response.setContentType("application/pdf");
	        // copy it to response's OutputStream
	        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	        response.flushBuffer();
	        jsonObject.put("response", response);
	        return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
	        
	      } catch (IOException ex) {
	    		jsonObject.put("ERROR", ex.getMessage());
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	      }

	  }
	  
	
	
}