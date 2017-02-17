package com.hillrom.vest.repository.monarch;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hillrom.vest.domain.NoteMonarch;


public interface NoteMonarchRepository extends JpaRepository<NoteMonarch, Long> {

	/*@Query("from NoteMonarch noteMonarch where noteMonarch.patient.id = ?1 and noteMonarch.createdOn = ?2")
	Optional<NoteMonarch> findOneByPatientIdAndCreatedOn(String patientId,LocalDate date);

	@Query("from NoteMonarch noteMonarch where  noteMonarch.patientUser.id = ?1  and noteMonarch.createdOn = ?2")
	Optional<NoteMonarch> findOneByPatientUserIdAndCreatedOn(Long userId,LocalDate date);
	
	// Added to get the Patient memo notes with respect to the HCP/CA user id
	@Query("from NoteMonarch note where note.patientUser.id = ?1  and note.patient.id = ?2")
	Optional<NoteMonarch> findOneByPatientUserIdAndPatientId(Long userId,String patientId);
	
	// Added to get the Patient memo notes for admin users entered byHCP/CA
	@Query(nativeQuery=true,value="select * from PATIENT_NOTE_MONARCH where user_id <> :userId  and patient_id = :patientId")
	Optional<NoteMonarch> returnPatientMemo(@Param("userId")Long userId,@Param("patientId")String patientId);
	
	Page<NoteMonarch> findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnDesc(Long userId,LocalDate from,LocalDate to,Boolean isDeleted,Pageable pageable);*/
	
	List<NoteMonarch> findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnAsc(Long userId,LocalDate from,LocalDate to,Boolean isDeleted);
}
