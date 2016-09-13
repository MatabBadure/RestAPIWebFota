package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {

	@Query("from Note note where note.patient.id = ?1 and note.createdOn = ?2")
	Optional<Note> findOneByPatientIdAndCreatedOn(String patientId,LocalDate date);

	@Query("from Note note where  note.patientUser.id = ?1  and note.createdOn = ?2")
	Optional<Note> findOneByPatientUserIdAndCreatedOn(Long userId,LocalDate date);
	
	@Query("from Note note where note.patientUser.id = ?1  and note.patient.id = ?1 and note.createdOn = ?2")
	Optional<Note> findOneByPatientUserIdAndPatientIdAndCreatedOn(Long userId,String patientId,LocalDate date);
	
	Page<Note> findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnDesc(Long userId,LocalDate from,LocalDate to,Boolean isDeleted,Pageable pageable);
	
	List<Note> findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnAsc(Long userId,LocalDate from,LocalDate to,Boolean isDeleted);
}
