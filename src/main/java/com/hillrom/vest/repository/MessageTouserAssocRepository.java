package com.hillrom.vest.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.ClinicPatientAssocPK;
import com.hillrom.vest.domain.MessageTouserAssoc;
import com.hillrom.vest.domain.Messages;
import com.hillrom.vest.web.rest.dto.MessageToUserAssoDTO;
import org.joda.time.DateTime;
import org.hibernate.QueryException;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;



public interface MessageTouserAssocRepository extends
		JpaRepository<MessageTouserAssoc, Long> {

	// To get the count of archived & unarchived messages
	@Query("SELECT messageTouserAssoc.isArchived,count(*) from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 group by messageTouserAssoc.isArchived")
    List<Object> findArchivedCountByUserId(Long userId);
	
	// To get the count of read & unread messages
	@Query("SELECT messageTouserAssoc.isRead,count(*) from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.isArchived = false group by messageTouserAssoc.isRead")
    List<Object> findReadCountByUserId(Long userId);
		
	// To get the count of read & unread messages
	@Query("SELECT messageTouserAssoc.isRead,count(*) from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.toClinic.id = ?2 and messageTouserAssoc.isArchived = false group by messageTouserAssoc.isRead")
    List<Object> findReadCountByUserIdAndClinicId(Long userId, String fromClinicId);
	
	
	@Query("from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.messages.id = ?2 order by messageTouserAssoc.messages.id desc")
    MessageTouserAssoc findByUserIdAndMessageId(Long userId, Long messageId);
	
	@Query("from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.messages.id = ?2 and messageTouserAssoc.toClinic.id = ?3 order by messageTouserAssoc.messages.id desc")
    MessageTouserAssoc findByUserIdAndMessageIdAndClinicId(Long userId, Long messageId, String clinicId);

	
	// To get the list of messages from clinic to patients
	@Query("Select messageTouserAssoc.id, messageTouserAssoc.isArchived, messageTouserAssoc.isRead, messageTouserAssoc.messages.id, messageTouserAssoc.messages.messageDatetime, "
				+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, messageTouserAssoc.messages.messageType,"
				+ "messageTouserAssoc.messages.fromClinic.name, messageTouserAssoc.messages.fromClinic.id, messageTouserAssoc.messages.user.lastName, messageTouserAssoc.messages.user.firstName,"
				+ "messageTouserAssoc.messages.toMessageId, messageTouserAssoc.messages.rootMessageId "
				+ "from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.isArchived = ?2")
	Page<Object> findByUserId(Long userId, boolean isArchived, Pageable pageable);
		
	// To get the list of messages from patient to clinic
	@Query("Select messageTouserAssoc.id, messageTouserAssoc.isArchived, messageTouserAssoc.isRead, messageTouserAssoc.messages.id, messageTouserAssoc.messages.messageDatetime, "
				+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, messageTouserAssoc.messages.messageType,"
				+ "messageTouserAssoc.messages.user.lastName, messageTouserAssoc.messages.user.firstName, messageTouserAssoc.messages.user.id,"
				+ "messageTouserAssoc.messages.toMessageId, messageTouserAssoc.messages.rootMessageId "
				+ "from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.toClinic.id = ?2 and messageTouserAssoc.isArchived = ?3")
	Page<Object> findByClinicId(Long userId, String clinicId, boolean isArchived, Pageable pageable);
	
	// To get the list of messages sent to clinic from patients
	/*@Query("Select messageTouserAssoc.id, messageTouserAssoc.messages.messageDatetime, messageTouserAssoc.messages.id, "
				+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, "
				+ "messageTouserAssoc.messages.messageType, messageTouserAssoc.toClinic.name, messageTouserAssoc.toClinic.id "
				+ "from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.messages.user.id = ?1 "
				+ "group by messageTouserAssoc.messages.id,messageTouserAssoc.toClinic.id having messageTouserAssoc.toClinic.id is not null")
	Page<Object> findByUserIdSent(Long userId, Pageable pageable);*/
	
	//http://qaserver.hillromvest.com/api/messagesSent/219?isClinic=1&clinicId=HR2016000012&page=1&per_page=10&sort_by=messages.messageDatetime&asc=false
	// To get the list of messages sent to clinic from patients
	@Query("Select messageTouserAssoc.id, messageTouserAssoc.messages.messageDatetime, messageTouserAssoc.messages.id, "
				+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, "
				+ "messageTouserAssoc.messages.messageType, group_concat(messageTouserAssoc.toClinic.name), group_concat(messageTouserAssoc.toClinic.id) "
				+ "from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.messages.user.id = ?1 "
				+ "group by messageTouserAssoc.messages.id,messageTouserAssoc.toClinic.id having messageTouserAssoc.toClinic.id is not null")
	Page<Object> findByUserIdSent(Long userId, Pageable pageable);
		

	
	//http://qaserver.hillromvest.com/api/messagesSent/292?isClinic=0&page=1&per_page=10&sort_by=messages.messageDatetime&asc=false
	// To get the list of messages sent to patient from clinic as CA/HCP
	@Query("Select messageTouserAssoc.id, messageTouserAssoc.messages.messageDatetime, messageTouserAssoc.messages.id, "
				+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, "
				+ "messageTouserAssoc.messages.messageType, group_concat(messageTouserAssoc.user.lastName),group_concat(messageTouserAssoc.user.firstName), group_concat(messageTouserAssoc.user.id) "
				+ "from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.messages.user.id = ?1 and messageTouserAssoc.messages.fromClinic.id = ?2"
				+ " group by messageTouserAssoc.messages.id")
	Page<Object> findByClinicIdSent(Long userId, String clinicId, Pageable pageable);


}


