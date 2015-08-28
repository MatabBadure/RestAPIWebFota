package com.hillrom.vest.web.rest;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.hillrom.vest.domain.Note;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class NoteResource {

	@Inject
	private NoteService noteService;
	
	@RequestMapping(value="/notes", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@RequestBody(required=true) Map<String,String> paramsMap){
		if(!SecurityUtils.isUserInRole(AuthoritiesConstants.PATIENT)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		JSONObject jsonObject = new JSONObject();
		String noteText = paramsMap.get("noteText");
		String userId = paramsMap.get("userId");
		String patientId = paramsMap.get("patientId");
		String timestamp = paramsMap.get("date");
		LocalDate date = StringUtils.isNumeric(timestamp)? LocalDate.fromDateFields(new Date(Long.parseLong(timestamp))) : LocalDate.now();
		Note note = null;
	
		if(Objects.isNull(noteText)){
			jsonObject.put("ERROR", "Required Param missing [noteText]");
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		if(Objects.nonNull(userId)){
			note = noteService.saveOrUpdateNoteByUserId(Long.parseLong(userId), noteText,date);
		}else if(Objects.nonNull(patientId)){
			note = noteService.saveOrUpdateNoteByPatientId(patientId, noteText,date);
		}else{
			jsonObject.put("ERROR", "Required Param missing [noteText,patientId/userId]");
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		if(Objects.nonNull(note)){
			return new ResponseEntity<>(note, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/users/{userId}/notes",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findByPatientId(@PathVariable("userId") Long userId,
			@RequestParam(value="date",required=true) Long timestamp){
		Note note = noteService.findOneByUserIdAndDate(userId, LocalDate.fromDateFields(new Date(timestamp)));
		if(Objects.nonNull(note)){
			return new ResponseEntity<>(note, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value="/patients/{patientId}/notes",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findByUserId(@PathVariable("patientId") String patientId,
			@RequestParam(value="date",required=true) Long timestamp){
		Note note = noteService.findOneByPatientIdAndDate(patientId,LocalDate.fromDateFields(new Date(timestamp)));
		if(Objects.nonNull(note)){
			return new ResponseEntity<>(note, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value="/notes/{id}", method=RequestMethod.PUT,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@PathVariable Long id,@RequestBody(required=true) Map<String,String> paramsMap){
		if(!SecurityUtils.isUserInRole(AuthoritiesConstants.PATIENT)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		JSONObject jsonObject = new JSONObject();
		String noteText = paramsMap.get("noteText");
		if(Objects.isNull(noteText)){
			jsonObject.put("ERROR", "Required Param missing [noteText]");
			return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
		}
		Note note = noteService.update(id, noteText);
		if(Objects.nonNull(note)){
			return new ResponseEntity<>(note, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value="/notes/{id}", method=RequestMethod.DELETE,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable Long id){
		if(!SecurityUtils.isUserInRole(AuthoritiesConstants.PATIENT)){
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		noteService.deleteNote(id);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/notes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Note>> getNotificationsByPatientUserId(@RequestParam(value="from",required=true)Long fromTimestamp,
    		@RequestParam(value="to",required=true)Long toTimestamp,
    		@RequestParam(value="userId",required=true)Long userId,
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException{
    	
    	Pageable pageable = PaginationUtil.generatePageRequest(offset, limit);
    	Page<Note> page = noteService.findByUserIdAndDateRange(userId, LocalDate.fromDateFields(new Date(fromTimestamp)), LocalDate.fromDateFields(new Date(toTimestamp)), pageable);
    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/notes", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
