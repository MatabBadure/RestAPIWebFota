package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.Messages;

public interface MessagingRepository extends JpaRepository<Messages, Long> {

	@Query("from Messages messages where messages.user.id = ?1 order by messages.messageDatetime desc")
    Page<Messages> findByUserId(Long userId,Pageable pageable);

	@Query("from Messages messages where messages.id = ?1")
    Messages findById(Long id);
		
	//@Query("Select message. from Messages messages where messages.id >= ?1 and messages.rootMessageId = ?2 ")
	
	@Query("Select messages, "			
			+ "CASE WHEN mfC.name IS NULL THEN (CASE WHEN messages.user.firstName IS NULL THEN '' ELSE messages.user.firstName || ' ' END  || CASE WHEN messages.user.lastName IS NULL THEN '' ELSE messages.user.lastName END) ELSE mfC.name END "
			+ "from Messages messages "
			+ "left join messages.fromClinic as mfC "			
			+ "where messages.id >= ?1 and messages.rootMessageId = ?2")	
	List<Object> returnForIdAndRootMessageId(Long messageId, Long rootMessageId);
}