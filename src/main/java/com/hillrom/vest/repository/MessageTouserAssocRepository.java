package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.ClinicPatientAssocPK;
import com.hillrom.vest.domain.MessageTouserAssoc;
import com.hillrom.vest.domain.Messages;

public interface MessageTouserAssocRepository extends
		JpaRepository<MessageTouserAssoc, Long> {

	@Query("from MessageTouserAssoc messageTouserAssoc where messageTouserAssoc.toUserId = ?1 order by messageTouserAssoc.toMessageId desc")
    List<MessageTouserAssoc> findByToUserId(Long toUserId);

}
