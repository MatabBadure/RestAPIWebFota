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
	public ResponseEntity<?> createMessage(@Valid @RequestBody(required=true) MessageDTO messageDTO){

		JSONObject jsonObject = new JSONObject();
		
		try{
			
	
			Long fromUserId = messageDTO.getFromUserId();
			String messageSubject = messageDTO.getMessageSubject();
			Long messageSizeInMbs = messageDTO.getMessageSizeMbs();
			String messageType = messageDTO.getMessageType();
			Long toMessageId = messageDTO.getToMessageId();
			Long rootMessageId = messageDTO.getRootMessageId();
			String messageText = messageDTO.getMessageText();
	
			Messages newMessage = messagingService.saveOrUpdateMessageData(messageDTO);
			if(Objects.nonNull(newMessage))
				return new ResponseEntity<>(newMessage, HttpStatus.CREATED);
		}catch(Exception ex){
			jsonObject.put("ERROR", ex.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
	}
	

}
