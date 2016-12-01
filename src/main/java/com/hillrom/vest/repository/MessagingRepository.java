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
		
	@Query("from Messages messages where messages.id >= ?1 and messages.rootMessageId = ?2 ")
	List<Messages> returnForIdAndRootMessageId(Long messageId, Long rootMessageId);	
}