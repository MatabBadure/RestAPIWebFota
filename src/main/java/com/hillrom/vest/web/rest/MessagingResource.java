package com.hillrom.vest.web.rest;

import static com.hillrom.vest.config.Constants.YYYY_MM_DD;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.hillrom.vest.domain.MessageTouserAssoc;
import com.hillrom.vest.domain.Messages;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.ChargerDataService;
import com.hillrom.vest.service.MessagingService;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.MessageDTO;
import com.hillrom.vest.web.rest.dto.MessageToUserAssoDTO;
import com.hillrom.vest.web.rest.dto.NoteDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;


import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class MessagingResource {

	@Inject
	private MessagingService messagingService;
	
	/**
     * POST  /message -> Create / Compose new message.
     */
	@RequestMapping(value="/message", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createMessage(@Valid @RequestBody(required=true) MessageDTO messageDTO){

		JSONObject jsonObject = new JSONObject();
		
		try{
				
			Messages newMessage = messagingService.saveOrUpdateMessageData(messageDTO);
			messageDTO.setId(newMessage.getId());
			List<MessageTouserAssoc> newMessageTouserAssocList = messagingService.saveOrUpdateMessageTousersData(messageDTO);
			jsonObject.put("Message", newMessage);
			jsonObject.put("MessageTouserAssocList", newMessageTouserAssocList);
			if(Objects.nonNull(newMessage)){
				jsonObject.put("statusMsg", "Message sent successfully");
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	/**
     * GET  /messages/{fromUserId} -> Get All Sent Messages for user mailbox.
     */
	@RequestMapping(value="/messagesSent/{fromUserId}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMessagesSentForInbox(@PathVariable("fromUserId") Long fromUserId,
			@RequestParam(value = "isClinic" , required = true) boolean isClinic,
			@RequestParam(value = "clinicId" , required = false) String clinicId,
			@RequestParam(value = "page" , required = false) Integer offset,
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
			Page<Object> messageList = messagingService.getSentMessagesForMailbox(isClinic, clinicId, fromUserId, PaginationUtil.generatePageRequest(offset, limit, sortOrder));
			if(Objects.nonNull(messageList)){
				return new ResponseEntity<>(messageList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
	/**
     * GET  /messages/{fromUserId}/archivedCount -> Get count of archived messages.
     */
	@RequestMapping(value="/messages/{fromUserId}/archivedCount",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getArchivedCountByUserId(@PathVariable("fromUserId") Long fromUserId){

	   	 
		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Object> messageList = messagingService.findArchivedCountByUserId(fromUserId);
			if(Objects.nonNull(messageList)){
				return new ResponseEntity<>(messageList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
	/**
     * GET  /messages/{fromUserId}/readunredCount -> Get count of read-unread messages.
     */
	@RequestMapping(value="/messages/{fromUserId}/readunredCount",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getReadUnreadCountByUserId(@PathVariable("fromUserId") Long fromUserId,
			@RequestParam(value = "isClinic" , required = true) boolean isClinic,
			@RequestParam(value = "clinicId" , required = false) String clinicId){
	   	 
		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Object> messageList = messagingService.findReadCountByUserId(fromUserId, isClinic, clinicId);
			if(Objects.nonNull(messageList)){
				return new ResponseEntity<>(messageList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}

	/**
     * GET  /messages/{toUserId} -> Get All Received Messages for user mailbox.
     */
	@RequestMapping(value="/messagesReceived/{toUserId}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMessagesReceivedForInbox(@PathVariable("toUserId") Long toUserId,
			@RequestParam(value = "isClinic" , required = true) boolean isClinic,
			@RequestParam(value = "clinicId" , required = false) String clinicId,
			@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "sort_by", required = false) String sortBy,
            @RequestParam(value = "asc",required = false) Boolean isAscending,
            @RequestParam(value = "mailBoxType",required = true) String mailBoxType){
		
	   	 Map<String,Boolean> sortOrder = new HashMap<>();
	   	 if(sortBy != null  && !sortBy.equals("")) {
	   		 isAscending =  (isAscending != null)?  isAscending : true;
	   		 sortOrder.put(sortBy, isAscending);
	   	 }

		JSONObject jsonObject = new JSONObject();
		
		try{
			//Page<JSONObject> messageList = messagingService.getReceivedMessagesForMailbox(toUserId,new PageRequest(offset, limit));
			Page<Object> messageList = messagingService.getReceivedMessagesForMailbox(isClinic, clinicId, toUserId, mailBoxType, PaginationUtil.generatePageRequest(offset, limit, sortOrder));
			if(Objects.nonNull(messageList)){
				return new ResponseEntity<>(messageList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
	/**
     * POST  /messages/archived -> Archive / UnArchive a list of messages in a user mailbox.
     */
	@RequestMapping(value="/messages/archived",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setMessagesArchivedInarchived(@Valid @RequestBody(required=true) List<MessageToUserAssoDTO> messageToUserArchivedList){

		JSONObject jsonObject = new JSONObject();
		
		try{
			List<MessageTouserAssoc> messageTouserAssoc = messagingService.setMessagesArchivedUnarchived(messageToUserArchivedList);
			if(Objects.nonNull(messageTouserAssoc)){
				return new ResponseEntity<>(messageTouserAssoc, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
	/**
     * POST  /messages/readunread -> Set as Read / UnRead a list of messages in a user mailbox.
     */
	
	@RequestMapping(value="/messages/readunread",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setMessagesReadUnread(@Valid @RequestBody(required=true) List<MessageToUserAssoDTO> messageToUserReadunreadList){

		JSONObject jsonObject = new JSONObject();
		
		try{
			List<MessageTouserAssoc> messageTouserAssoc = messagingService.setMessagesReadUnread(messageToUserReadunreadList);
			if(Objects.nonNull(messageTouserAssoc)){
				return new ResponseEntity<>(messageTouserAssoc, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
	/**
     * GET  /messagesReceivedThreads/{messageId}/{rootMessageId} -> Get All Message Threads for user mailbox.
     */
	@RequestMapping(value="/messagesReceivedThreads/{messageId}/{rootMessageId}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMessagesReceivedThreadsForInbox(@PathVariable("messageId") Long messageId,			
			@PathVariable("rootMessageId") Long rootMessageId,
			@RequestParam(value = "userId" , required = true) Long userId,
			@RequestParam(value = "clinicId" , required = true) String clinicId){

		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Object> messageList = messagingService.findByUserIdThreads(messageId, rootMessageId, userId, clinicId);
			if(Objects.nonNull(messageList)){
				return new ResponseEntity<>(messageList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/**
     * GET  /messageDetails/{id} -> Get Received Message Details for user mailbox.
     */
	@RequestMapping(value="/messageDetails/{id}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMessagesDetailsForInbox(@PathVariable("id") Long id){
		
		JSONObject jsonObject = new JSONObject();
		
		try{
			Messages receivedMessage = messagingService.findByMessageId(id);
			if(Objects.nonNull(receivedMessage)){
				return new ResponseEntity<>(receivedMessage, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
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
	  @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	  @ResponseBody
	  public ResponseEntity<?> uploadFile(
	      @RequestParam("uploadfile") MultipartFile uploadfile) {
	    
	    try {
	      // Get the filename and build the local file path
	      String filename = uploadfile.getOriginalFilename();
	      String directory = "/tmp/visiview-files";
	      String filepath = Paths.get(directory, filename).toString();
	      
	      // Save the file locally
	      BufferedOutputStream stream =
	          new BufferedOutputStream(new FileOutputStream(new File(filepath)));
	      stream.write(uploadfile.getBytes());
	      stream.close();
	    }
	    catch (Exception e) {
	      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	    
	    return new ResponseEntity<>(HttpStatus.OK);
	  } // method uploadFile
	  
	  

	  
	  @RequestMapping(value = "/files/{file_name}", method = RequestMethod.GET)
	  public void getFile(
	      @PathVariable("file_name") String fileName, 
	      HttpServletResponse response) {
	      try {
	        // get your file as InputStream
    	    File initialFile = new File("/tmp/visiview-files/" + fileName + ".pdf");
    	    InputStream is = new FileInputStream(initialFile);  
    	    
    		response.addHeader("Content-disposition", "inline;filename=/tmp/visiview-files/" + fileName);
    		response.setContentType("application/pdf");
	        // copy it to response's OutputStream
	        org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
	        response.flushBuffer();
	      } catch (IOException ex) {
	    	  ex.printStackTrace();
	        //log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
	        throw new RuntimeException("IOError writing file to output stream");
	      }

	  }
}
