package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.MessageTouserAssoc;
import com.hillrom.vest.domain.Messages;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.MessageTouserAssocRepository;
import com.hillrom.vest.repository.MessagingRepository;
import com.hillrom.vest.repository.NoteRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.MessageDTO;

@Service
@Transactional
public class MessagingService {

	private final Logger log = LoggerFactory.getLogger(MessagingService.class);
	
	@Inject
	private MessagingRepository messagingRepository;
	
	@Inject
	private MessageTouserAssocRepository messageTouserAssocRepository;
	
	

	
	public Messages saveOrUpdateMessageData(MessageDTO messageDTO) throws HillromException{

		Messages newMessage = new Messages();
		
		newMessage.setFromUserId(messageDTO.getFromUserId());
		newMessage.setMessageSubject(messageDTO.getMessageSubject());
		newMessage.setMessageDatetime(new DateTime());
		newMessage.setMessageSizeMBs(messageDTO.getMessageSizeMbs());
		newMessage.setMessageType(messageDTO.getMessageType());
		newMessage.setToMessageId(messageDTO.getToMessageId());
		newMessage.setRootMessageId(messageDTO.getRootMessageId());
		newMessage.setMessageText(messageDTO.getMessageText());
		messagingRepository.save(newMessage);
        log.debug("Created New Message: {}", newMessage);
        return newMessage;
	}


	public Messages updateRootAndToMessageId(Messages newMessage) throws HillromException{

		//Messages newMessage = new Messages();
		
		newMessage.setFromUserId(newMessage.getFromUserId());
		newMessage.setMessageSubject(newMessage.getMessageSubject());
		newMessage.setMessageDatetime(new DateTime());
		newMessage.setMessageSizeMBs(newMessage.getMessageSizeMBs());
		newMessage.setMessageType(newMessage.getMessageType());
		newMessage.setToMessageId(newMessage.getId());
		newMessage.setRootMessageId(newMessage.getId());
		newMessage.setMessageText(newMessage.getMessageText());
		 messagingRepository.save(newMessage);
        log.debug("updated RootMsgId and To MsgId", newMessage);
        return newMessage;
	}
	
	public List<MessageTouserAssoc> saveOrUpdateMessageTousersData(List<Long> toUserIds,Long newMessageId) throws HillromException{
		List<MessageTouserAssoc> listMessageTouserAssoc = new ArrayList<MessageTouserAssoc>();
		for(Long userId : toUserIds){
			MessageTouserAssoc newMessageTouserAssoc = new MessageTouserAssoc();
			newMessageTouserAssoc.setToMessageId(newMessageId);
			newMessageTouserAssoc.setToUserId(userId);
			messageTouserAssocRepository.save(newMessageTouserAssoc);
			listMessageTouserAssoc.add(newMessageTouserAssoc);
		}
		return listMessageTouserAssoc;
	}
	
	public List<Messages> getSentMessagesForMailbox(Long fromUserId) throws HillromException{
		List<Messages> messageList = null;
		messageList = messagingRepository.findByFromUserId(fromUserId);
		return messageList;
	}
	
	public List<Messages> getReceivedMessagesForMailbox(Long toUserId) throws HillromException{
		List<MessageTouserAssoc> messageTouserAssocList = new ArrayList<MessageTouserAssoc>();;
		List<Messages> associatedMessagesList = new ArrayList<Messages>();;
		messageTouserAssocList = messageTouserAssocRepository.findByToUserId(toUserId);
		for(MessageTouserAssoc messageTouserAssoc : messageTouserAssocList){
			associatedMessagesList.add(messagingRepository.findById(messageTouserAssoc.getToMessageId()));
		}
		return associatedMessagesList;
	}
	
	
	

}
