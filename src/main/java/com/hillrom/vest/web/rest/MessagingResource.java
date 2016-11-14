package com.hillrom.vest.web.rest;

import static com.hillrom.vest.config.Constants.YYYY_MM_DD;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.validation.Valid;

import net.minidev.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
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
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.ChargerDataService;
import com.hillrom.vest.service.MessagingService;
import com.hillrom.vest.service.NoteService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.MessageDTO;
import com.hillrom.vest.web.rest.dto.NoteDTO;
import com.hillrom.vest.web.rest.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class MessagingResource {

	@Inject
	private MessagingService messagingService;
	
	/**
     * POST  /message -> Create / Compose new message.
     */
	@RequestMapping(value="/message", method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<JSONObject> createMessage(@Valid @RequestBody(required=true) MessageDTO messageDTO){

		JSONObject jsonObject = new JSONObject();
		
		try{
			//Insert message infm to Messages table 
			Messages newMessage = messagingService.saveOrUpdateMessageData(messageDTO);
			
			// Get Id for the message created and update the same for to_messageId and root_message_id
			Messages updatedNewMessage = messagingService.updateMessageData(newMessage.getId(), newMessage);
			
			List<MessageTouserAssoc> newMessageTouserAssocList = messagingService.saveOrUpdateMessageTousersData(messageDTO.getToUserIds(),newMessage.getId());
			jsonObject.put("Message", updatedNewMessage);
			jsonObject.put("MessageTouserAssocList", newMessageTouserAssocList);
			if(Objects.nonNull(newMessage))
				return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);
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
	public ResponseEntity<?> getMessagesSentForInbox(@PathVariable("fromUserId") Long fromUserId){

		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Messages> messageList = messagingService.getSentMessagesForMailbox(fromUserId);
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
	public ResponseEntity<?> getMessagesReceivedForInbox(@PathVariable("toUserId") Long toUserId){

		JSONObject jsonObject = new JSONObject();
		
		try{
			List<Messages> messageList = messagingService.getReceivedMessagesForMailbox(toUserId);
			if(Objects.nonNull(messageList)){
				return new ResponseEntity<>(messageList, HttpStatus.OK);
			}
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
	}
	
}
