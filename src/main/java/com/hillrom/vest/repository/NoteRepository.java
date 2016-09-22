package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hillrom.vest.domain.Note;


public interface NoteRepository extends JpaRepository<Note, Long> {

	@Query("from Note note where note.patient.id = ?1 and note.createdOn = ?2")
	Optional<Note> findOneByPatientIdAndCreatedOn(String patientId,LocalDate date);

	@Query("from Note note where  note.patientUser.id = ?1  and note.createdOn = ?2")
	Optional<Note> findOneByPatientUserIdAndCreatedOn(Long userId,LocalDate date);
	
	// Added to get the Patient memo notes with respect to the HCP/CA user id
	@Query("from Note note where note.patientUser.id = ?1  and note.patient.id = ?2")
	Optional<Note> findOneByPatientUserIdAndPatientId(Long userId,String patientId);
	
	// Added to get the Patient memo notes for admin users entered byHCP/CA
	@Query(nativeQuery=true,value="select * from patient_note where user_id <> :userId  and patient_id = :patientId")
	Optional<Note> returnPatientMemo(@Param("userId")Long userId,@Param("patientId")String patientId);
	
	Page<Note> findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnDesc(Long userId,LocalDate from,LocalDate to,Boolean isDeleted,Pageable pageable);
	
	List<Note> findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnAsc(Long userId,LocalDate from,LocalDate to,Boolean isDeleted);
}
