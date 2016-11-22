package com.hillrom.vest.repository;

import java.util.HashMap;
import java.util.List;
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

public interface MessageTouserAssocRepository extends
		JpaRepository<MessageTouserAssoc, Long> {

	//@Query("from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 order by messageTouserAssoc.messages.id desc")
    //Page<MessageTouserAssoc> findByUserId(Long userId,Pageable pageable);
	
	@Query("from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1 and messageTouserAssoc.messages.id = ?2 order by messageTouserAssoc.messages.id desc")
    MessageTouserAssoc findByUserIdAndMessageId(Long userId, Long messageId);
	
	// To get the list of messages from clinic to patients
	@Query("Select messageTouserAssoc.id, messageTouserAssoc.isArchived, messageTouserAssoc.isRead, messageTouserAssoc.messages.messageDatetime, "
			+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, messageTouserAssoc.messages.messageType,"
			+ "messageTouserAssoc.messages.fromClinic.name, messageTouserAssoc.messages.user.lastName, messageTouserAssoc.messages.user.firstName "
			+ "from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.user.id = ?1")
    Page<Object> findByUserId(Long userId,Pageable pageable);
	
	// To get the list of messages from patient to clinic
	@Query("Select messageTouserAssoc.id, messageTouserAssoc.isArchived, messageTouserAssoc.isRead, messageTouserAssoc.messages.messageDatetime, "
			+ "messageTouserAssoc.messages.messageSubject, messageTouserAssoc.messages.messageSizeMBs, messageTouserAssoc.messages.messageType,"
			+ "messageTouserAssoc.messages.user.lastName, messageTouserAssoc.messages.user.firstName from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.toClinic.id = ?1")
    Page<Object> findByClinicId(String clinicId,Pageable pageable);

}
