package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.Messages;
import com.hillrom.vest.domain.Note;

public interface MessagingRepository extends JpaRepository<Messages, Long> {


	@Query("from Messages messages where messages.user.id = ?1 order by messages.message_datetime desc")
    Page<Messages> findByUserId(Long userId,Pageable pageable);

	@Query("from Messages messages where messages.id = ?1")
    Messages findById(Long id);
		
	
}
