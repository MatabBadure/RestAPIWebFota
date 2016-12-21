package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.Set;

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

import com.hillrom.vest.domain.EntityUserAssoc;
import com.hillrom.vest.domain.MessageTouserAssoc;
import com.hillrom.vest.domain.Messages;
import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserExtension;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ClinicRepository;
import com.hillrom.vest.repository.EntityUserRepository;
import com.hillrom.vest.repository.MessageTouserAssocRepository;
import com.hillrom.vest.repository.MessagingRepository;
import com.hillrom.vest.repository.NoteRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.security.AuthoritiesConstants;
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
	
	@Inject
    private EntityUserRepository entityUserRepository;
	
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
	
	public String saveOrUpdateMessageTousersData(MessageDTO messageDto) throws HillromException{
		
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
		
		String statusMsg = "Message Sent successfully#0";
		// For Clinic Id
		if(toClinicIds.size() > 0){
			
			//Integer clinicSize = toClinicIds.size();
			List<String> clinicFailList =  new ArrayList<>();
			
			for(String clinicId : toClinicIds){
				
				// To get the list of HCP users for the clinic
				List<String> idList = new ArrayList<>();
		        idList.add(clinicId);
				Set<UserExtension> hcpUserList = clinicService.getHCPUsers(idList);
				
				// Check for HCP users available for the Clinic and add it in the list
				if (Objects.nonNull(hcpUserList)) {
					for(UserExtension hcpUser : hcpUserList){
						MessageTouserAssoc newMessageTouserAssoc = new MessageTouserAssoc();
						newMessageTouserAssoc.setMessages(messagingRepository.findById(newMessageId));
						newMessageTouserAssoc.setUser(hcpUser);
						newMessageTouserAssoc.setIsArchived(messageDto.isArchived());
						newMessageTouserAssoc.setIsRead(messageDto.isRead());
						newMessageTouserAssoc.setToClinic(clinicRepository.getOne(clinicId));
						messageTouserAssocRepository.save(newMessageTouserAssoc);
						
						// Send email notification to HCP user if the user opted - Flag 3 for HCP
						sendMessageNotifiationToUser(hcpUser.getId(), messageSubject, 3);
					}
				}				
				
				// To get the list of CA users for the clinic
				List<EntityUserAssoc> clinicUserList  = entityUserRepository.findByClinicIdAndUserRole(clinicId, AuthoritiesConstants.CLINIC_ADMIN);
				
				// Check for CA users available for the Clinic and add it in the list
				if(Objects.nonNull(clinicUserList)){
					for(EntityUserAssoc clinicUserAssoc : clinicUserList){
						MessageTouserAssoc newMessageTouserAssoc = new MessageTouserAssoc();
						newMessageTouserAssoc.setMessages(messagingRepository.findById(newMessageId));
						newMessageTouserAssoc.setUser(clinicUserAssoc.getUser());					
						newMessageTouserAssoc.setIsArchived(messageDto.isArchived());
						newMessageTouserAssoc.setIsRead(messageDto.isRead());
						newMessageTouserAssoc.setToClinic(clinicRepository.getOne(clinicId));					
						messageTouserAssocRepository.save(newMessageTouserAssoc);
						
						// Send email notification to CA user if the user opted - Flag 2 for CA
						sendMessageNotifiationToUser(clinicUserAssoc.getUser().getId(), messageSubject, 2);
					}
				}

				if((Objects.isNull(hcpUserList) && Objects.isNull(clinicUserList)) || 
						((Objects.nonNull(hcpUserList) && hcpUserList.size() == 0) && (Objects.nonNull(clinicUserList) && clinicUserList.size()==0))){
					clinicFailList.add(clinicRepository.getOne(clinicId).getName());
				}
			}
			
			// get the list of clinic's, where message not sent
			String clinicsString = StringUtils.join(clinicFailList,", ");
			
			// Checks for all the messages sent to clinics failed
			if(toClinicIds.size() > 0 && toClinicIds.size() == clinicFailList.size()){
				statusMsg = "Unable to send message to clinic(s) : "+clinicsString+"#1";
			}else if(toClinicIds.size() > 0 && clinicFailList.size() != 0 && clinicFailList.size() < toClinicIds.size()){
				// Checks for atleast one clinic failed to send message
				statusMsg = "Message not sent to all clinic(s), unable to send message to clinic(s) : "+clinicsString+"#1";
			}
		}else if(toUserIds.size() > 0){
			// For List of Patients
			for(Long userId : toUserIds){
				MessageTouserAssoc newMessageTouserAssoc = new MessageTouserAssoc();
				newMessageTouserAssoc.setMessages(messagingRepository.findById(newMessageId));
				newMessageTouserAssoc.setUser(userRepository.findOne(userId));
				newMessageTouserAssoc.setIsArchived(messageDto.isArchived());
				newMessageTouserAssoc.setIsRead(messageDto.isRead());
				messageTouserAssocRepository.save(newMessageTouserAssoc);
				
				// Send email notification to Patient user if the user opted - Flag 1 for Patient
				sendMessageNotifiationToUser(userId, messageSubject, 1);
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
		
		return statusMsg;
	}
	
	public void sendMessageNotifiationToUser(Long userId, String messageSubject, int patOrCaOrHcp) throws HillromException{	
		User user = userService.getUser(userId);
		
		if(user.isMessageNotification())
			mailService.sendMessageNotificationToUser(user, messageSubject, patOrCaOrHcp);
	}	

	public List<Object> findArchivedCountByUserId(Long fromUserId) throws HillromException{
		List<Object> messageList = null;
		messageList = messageTouserAssocRepository.findArchivedCountByUserId(fromUserId);
		return messageList;
	}
	
	public List<Object> findReadCountByUserId(Long fromUserId, boolean isClinic, String clinicId) throws HillromException{
		List<Object> messageList = null;
		messageList = (isClinic && !clinicId.isEmpty()) ? messageTouserAssocRepository.findReadCountByUserIdAndClinicId(fromUserId,clinicId) : messageTouserAssocRepository.findReadCountByUserId(fromUserId);
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
	
	public Page<Object> getReceivedMessagesForMailbox(boolean isClinic, String clinicId, Long toUserId, String mailBoxType, Pageable pageable) throws HillromException{
		
		boolean isArchived = Boolean.TRUE;
		if(Objects.nonNull(mailBoxType) && mailBoxType.equalsIgnoreCase("Inbox")){
			isArchived = Boolean.FALSE;
		}
		
		Page<Object> messageTouserAssocList = null;
		
		// Check for the clinic flag to differentiate between whether the clinic id is passed or patient id is passed
		if(isClinic && !clinicId.isEmpty()){
			messageTouserAssocList = messageTouserAssocRepository.findByClinicId(toUserId, clinicId, isArchived, pageable);
		}else{
			messageTouserAssocList = messageTouserAssocRepository.findByUserId(toUserId, isArchived, pageable);
		}
		
		return messageTouserAssocList;
	}
	
	public List<MessageTouserAssoc> setMessagesArchivedUnarchived(List<MessageToUserAssoDTO> messageToUserArchivedList) throws HillromException{
		List<MessageTouserAssoc> returnMessageTouserAssocList = new ArrayList<MessageTouserAssoc>();
		
		 for(MessageToUserAssoDTO msgToUsrAsscList : messageToUserArchivedList)
		 {
			MessageTouserAssoc messageTouserAssoc = messageTouserAssocRepository.findById(msgToUsrAsscList.getId());
			
			messageTouserAssoc.setIsArchived(msgToUsrAsscList.isArchived()?Boolean.TRUE:Boolean.FALSE);
			returnMessageTouserAssocList.add(messageTouserAssoc);
		 }
			
		return returnMessageTouserAssocList;
	}

	public List<MessageTouserAssoc> setMessagesReadUnread(List<MessageToUserAssoDTO> messageToUserReadUnreadList) throws HillromException{
		List<MessageTouserAssoc> returnMessageTouserAssocList = new ArrayList<MessageTouserAssoc>();
		
		 for(MessageToUserAssoDTO msgToUsrAsscList : messageToUserReadUnreadList)
		 {
			 
			 MessageTouserAssoc messageTouserAssoc = messageTouserAssocRepository.findById(msgToUsrAsscList.getId());
			 
			messageTouserAssoc.setIsRead(msgToUsrAsscList.isRead()?Boolean.TRUE:Boolean.FALSE);
			returnMessageTouserAssocList.add(messageTouserAssoc);
		 }
		return returnMessageTouserAssocList;
	}
	
	public List<Object> findByUserIdThreads(Long messageId, Long rootMessageId, Long userId, String clinicId) throws HillromException{
		List<Object> threadMessagesList = new ArrayList<Object>();
		threadMessagesList = messageTouserAssocRepository.returnThreadMessages(messageId, rootMessageId, userId, clinicId);
		return threadMessagesList;
	}
	
	public Messages findByMessageId(Long messageId) throws HillromException{
		Messages receivedMessage  = messagingRepository.findById(messageId);
		return receivedMessage;
	}	

}
