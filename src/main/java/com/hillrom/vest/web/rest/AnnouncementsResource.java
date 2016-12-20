package com.hillrom.vest.web.rest;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.validation.Valid;
import net.minidev.json.JSONObject;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.service.AnnouncementsService;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
            @RequestParam(value = "asc",required = false) Boolean isAscending){

	   	 Map<String,Boolean> sortOrder = new HashMap<>();
	   	 if(sortBy != null  && !sortBy.equals("")) {
	   		 isAscending =  (isAscending != null)?  isAscending : true;
	   		 sortOrder.put(sortBy, isAscending);
	   	 }
	   	 
		JSONObject jsonObject = new JSONObject();
		
		try{
			 Page<Announcements> announcementsList = announcementsService.findAnnouncementData(PaginationUtil.generatePageRequest(offset, limit, sortOrder));
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
	

}
