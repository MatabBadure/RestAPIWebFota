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
import org.springframework.data.domain.PageImpl;
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
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.MessageTouserAssocRepository;
import com.hillrom.vest.repository.MessagingRepository;
import com.hillrom.vest.repository.NoteRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.MessageDTO;
import com.hillrom.vest.web.rest.dto.MessageToUserAssoDTO;

import net.minidev.json.JSONObject;

@Service
@Transactional
public class MessagingService {

	private final Logger log = LoggerFactory.getLogger(MessagingService.class);
	
	@Inject
	private MessagingRepository messagingRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private MessageTouserAssocRepository messageTouserAssocRepository;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ClinicService clinicService;
	
	@Inject
	private ClinicRepository clinicRepository;
	
	public Messages saveOrUpdateMessageData(MessageDTO messageDTO) throws HillromException{

		Messages newMessage = new Messages();
		
		newMessage.setUser(userRepository.findOne(messageDTO.getFromUserId()));
		
		if(Objects.nonNull(messageDTO.getFromClinicId()))
			newMessage.setFromClinic(clinicRepository.getOne(messageDTO.getFromClinicId()));
		
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
	
	public List<MessageTouserAssoc> saveOrUpdateMessageTousersData(MessageDTO messageDto) throws HillromException{
		
		Long newMessageId = messageDto.getId();
		
		List<Long> toUserIds = new ArrayList<Long>();
		if(Objects.nonNull(messageDto.getToUserIds()))
			toUserIds = messageDto.getToUserIds();
		
		Long rootMessageId = messageDto.getRootMessageId();
		Long toMessageId = messageDto.getToMessageId();
		String messageSubject = messageDto.getMessageSubject();
		
		List<String> toClinicIds = new ArrayList<String>();
		if(Objects.nonNull(messageDto.getToClinicIds()))
			toClinicIds = messageDto.getToClinicIds();
		
		List<MessageTouserAssoc> listMessageTouserAssoc = new ArrayList<MessageTouserAssoc>();
		
		if(toClinicIds.size() > 0){
			
			for(String clinicId : toClinicIds){
				// To get the list of CA & HCP users for the Clinic id
				List<User> caHcpUsers = clinicService.getCaHcpUsersForClinic(clinicId);
				for(User caHcpUser : caHcpUsers){
					MessageTouserAssoc newMessageTouserAssoc = new MessageTouserAssoc();
					newMessageTouserAssoc.setMessages(messagingRepository.findById(newMessageId));
					newMessageTouserAssoc.setUser(caHcpUser);					
					newMessageTouserAssoc.setToClinic(clinicRepository.getOne(clinicId));					
					messageTouserAssocRepository.save(newMessageTouserAssoc);
					listMessageTouserAssoc.add(newMessageTouserAssoc);
					sendMessageNotifiationToUser(caHcpUser.getId(), messageSubject);
				}
			}
			
		}else if(toUserIds.size() > 0){
			for(Long userId : toUserIds){
				MessageTouserAssoc newMessageTouserAssoc = new MessageTouserAssoc();
				newMessageTouserAssoc.setMessages(messagingRepository.findById(newMessageId));
				newMessageTouserAssoc.setUser(userRepository.findOne(userId));
				messageTouserAssocRepository.save(newMessageTouserAssoc);
				listMessageTouserAssoc.add(newMessageTouserAssoc);
				sendMessageNotifiationToUser(userId, messageSubject);
			}
		}
		Messages newMessage =  messagingRepository.findById(newMessageId);
		if(Objects.isNull(rootMessageId)){
			newMessage.setRootMessageId(newMessageId);
		}else{
			newMessage.setRootMessageId(rootMessageId);
			newMessage.setToMessageId(toMessageId);
		}
		messagingRepository.save(newMessage);
		return listMessageTouserAssoc;
	}
	
	public void sendMessageNotifiationToUser(Long userId, String messageSubject) throws HillromException{	
		User user = userService.getUser(userId);
		if(user.isMessageNotification())
			mailService.sendMessageNotificationToUser(user, messageSubject);
	}	

	public List<Object> findArchivedCountByUserId(Long fromUserId) throws HillromException{
		List<Object> messageList = null;
		messageList = messageTouserAssocRepository.findArchivedCountByUserId(fromUserId);
		return messageList;
	}
	
	public List<Object> findReadCountByUserId(Long fromUserId) throws HillromException{
		List<Object> messageList = null;
		messageList = messageTouserAssocRepository.findReadCountByUserId(fromUserId);
		return messageList;
	}
	
	public Page<Object> getSentMessagesForMailbox(boolean isClinic, String clinicId, Long fromUserId, Pageable pageable) throws HillromException{
		
		Page<Object> messageList = null;
		if(isClinic && !clinicId.isEmpty()){
			messageList = messageTouserAssocRepository.findByClinicIdSent(fromUserId, clinicId, pageable);
		}else{
			messageList = messageTouserAssocRepository.findByUserIdSent(fromUserId, pageable);
		}
		return messageList;
	}
	
	public Page<Object> getReceivedMessagesForMailbox(boolean isClinic, String toId, String mailBoxType, Pageable pageable) throws HillromException{
		
		boolean isArchived = Boolean.TRUE;
		if(Objects.nonNull(mailBoxType) && mailBoxType.equalsIgnoreCase("Inbox")){
			isArchived = Boolean.FALSE;
		}
		
		// Check for the clinic flag to differentiate between whether the clinic id is passed or patient id is passed
		Page<Object> messageTouserAssocList  = isClinic ? messageTouserAssocRepository.findByClinicId(toId, isArchived, pageable) : messageTouserAssocRepository.findByUserId(Long.parseLong(toId), isArchived, pageable);
		return messageTouserAssocList;
	}
	
	public List<MessageTouserAssoc> setMessagesArchivedUnarchived(List<MessageToUserAssoDTO> messageToUserArchivedList) throws HillromException{
		List<MessageTouserAssoc> returnMessageTouserAssocList = new ArrayList<MessageTouserAssoc>();
		
		 for(MessageToUserAssoDTO msgToUsrAsscList : messageToUserArchivedList)
		 {
				
			MessageTouserAssoc messageTouserAssoc = messageTouserAssocRepository.findByUserIdAndMessageId(msgToUsrAsscList.getUserId(),msgToUsrAsscList.getMessageId());
			
			messageTouserAssoc.setIsArchived(msgToUsrAsscList.isArchived()?Boolean.TRUE:Boolean.FALSE);
			returnMessageTouserAssocList.add(messageTouserAssoc);
		 }
			
		return returnMessageTouserAssocList;
	}

	public List<MessageTouserAssoc> setMessagesReadUnread(List<MessageToUserAssoDTO> messageToUserReadUnreadList) throws HillromException{
		List<MessageTouserAssoc> returnMessageTouserAssocList = new ArrayList<MessageTouserAssoc>();
		
		 for(MessageToUserAssoDTO msgToUsrAsscList : messageToUserReadUnreadList)
		 {
			MessageTouserAssoc messageTouserAssoc = messageTouserAssocRepository.findByUserIdAndMessageId(msgToUsrAsscList.getUserId(),msgToUsrAsscList.getMessageId());
			
			messageTouserAssoc.setIsRead(msgToUsrAsscList.isRead()?Boolean.TRUE:Boolean.FALSE);
			returnMessageTouserAssocList.add(messageTouserAssoc);
		 }
		return returnMessageTouserAssocList;
	}
	
	public List<Object> findByUserIdThreads(boolean isClinic, String toId,Long rootMessageId,String mailBoxType) throws HillromException{
		boolean isArchived = Boolean.TRUE;
		if(Objects.nonNull(mailBoxType) && mailBoxType.equalsIgnoreCase("Inbox")){
			isArchived = Boolean.FALSE;
		}
		
		// Check for the clinic flag to differentiate between whether the clinic id is passed or patient id is passed
		List<Object> messageTouserAssocList  = isClinic ? messageTouserAssocRepository.findByClinicIdThreads(toId, isArchived,rootMessageId) : messageTouserAssocRepository.findByUserIdThreads(Long.parseLong(toId), isArchived,rootMessageId);
		return messageTouserAssocList;
	}
	
	public Messages findByMessageId(Long messageId) throws HillromException{
		Messages receivedMessage  = messagingRepository.findById(messageId);
		return receivedMessage;
	}	
	

}
