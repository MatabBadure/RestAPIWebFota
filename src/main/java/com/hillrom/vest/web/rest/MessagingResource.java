package com.hillrom.vest.web.rest;

import static com.hillrom.vest.config.Constants.YYYY_MM_DD;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

@Controller
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
	public ResponseEntity<?> getReadUnreadCountByUserId(@PathVariable("fromUserId") Long fromUserId){

	   	 
		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Object> messageList = messagingService.findReadCountByUserId(fromUserId);
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
	@RequestMapping(value="/messagesReceived/{toId}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> getMessagesReceivedForInbox(@PathVariable("toId") String toId,
			@RequestParam(value = "isClinic" , required = true) boolean isClinic,
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
	   	 int i = 0;
	   	 List<Map<String, Object>> responseMapList = new ArrayList< Map<String, Object>>();
		 try{
			
			Page<Object[]> messageList = messagingService.getReceivedMessagesForMailbox(isClinic, toId, mailBoxType, PaginationUtil.generatePageRequest(offset, limit, sortOrder));
			
			if(Objects.nonNull(messageList)){                    
                     for (Object[] object : messageList) {
                    	   Map<String, Object> map = new HashMap<String, Object>();
                    	  
                    	   Long messageTouserAssoc_id = (Long) object[0];
                    	   boolean messageTouserAssoc_isArchived = (boolean) object[1];
                    	   boolean messageTouserAssoc_isRead = (boolean) object[2];
                    	   Long messageTouserAssoc_messages_id  = (Long) object[3];
                    	   org.joda.time.DateTime messageTouserAssoc_messages_messageDatetime  = (org.joda.time.DateTime) object[4];
                    	   String messageTouserAssoc_messages_messageSubject  = (String) object[5];
                    	   Long messageTouserAssoc_messages_messageSizeMBs  = (Long) object[6];
                    	   String messageTouserAssoc_messages_messageType  = (String) object[7];
                    	   String messageTouserAssoc_messages_fromClinic_name  = (String) object[8];
                    	   String messageTouserAssoc_messages_user_lastName = (String) object[9];
                    	   String messageTouserAssoc_messages_user_firstName = (String) object[10];
                            
                           map.put("messageTouserAssoc_id", messageTouserAssoc_id);
                    	   map.put("messageTouserAssoc_isArchived", messageTouserAssoc_isArchived);
                    	   map.put("messageTouserAssoc_isRead", messageTouserAssoc_isRead);
                    	   map.put("messageTouserAssoc_messages_id", messageTouserAssoc_messages_id);
                    	   map.put("messageTouserAssoc_messages_messageDatetime", messageTouserAssoc_messages_messageDatetime);
                    	   map.put("messageTouserAssoc_messages_messageSubject", messageTouserAssoc_messages_messageSubject);
                    	   map.put("messageTouserAssoc_messages_messageSizeMBs", messageTouserAssoc_messages_messageSizeMBs);
                    	   map.put("messageTouserAssoc_messages_messageType", messageTouserAssoc_messages_messageType);
                    	   map.put("messageTouserAssoc_messages_fromClinic_name", messageTouserAssoc_messages_fromClinic_name);
                    	   map.put("messageTouserAssoc_messages_user_lastName", messageTouserAssoc_messages_user_lastName);
                    	   map.put("messageTouserAssoc_messages_user_firstName", messageTouserAssoc_messages_user_firstName);
                    	   
                    	  jsonObject.put("MessageList"+(i++), map);
                    	}
                       responseMapList.add(jsonObject);
                       
                       Map<String, Object> map1 = new HashMap<String, Object>();
                	   map1.put("totalPages", messageList.getTotalPages());
                	   map1.put("totalElements", messageList.getTotalElements());
                	   map1.put("last", messageList.isLast());
                	   map1.put("first", messageList.isFirst());
                	   map1.put("sort", messageList.getSort());
                	   map1.put("numberOfElements", messageList.getNumberOfElements());
                	   map1.put("size", messageList.getSize());
                	   map1.put("number", messageList.getNumber());
                	   responseMapList.add(map1);
                	
                       return new ResponseEntity<>(responseMapList, HttpStatus.OK);
			} 
		}catch(Exception ex){
			ex.printStackTrace();
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
     * GET  /messagesReceivedDetails/{toUserId}/{rootMessageId} -> Get All Received Message Threads for user mailbox.
     */
	@RequestMapping(value="/messagesReceivedDetails/{toUserId}/{rootMessageId}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMessagesReceivedDetailsForInbox(@PathVariable("toUserId") String toUserId,
			@PathVariable("rootMessageId") Long rootMessageId,
			@RequestParam(value = "isClinic" , required = true) boolean isClinic,
            @RequestParam(value = "mailBoxType",required = true) String mailBoxType){
		


		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Object> messageList = messagingService.findByUserIdThreads(isClinic, toUserId, rootMessageId, mailBoxType);
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
	
}
