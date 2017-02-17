package com.hillrom.vest.service.monarch;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.NoteMonarch;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.monarch.NoteMonarchRepository;
import com.hillrom.vest.service.UserService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

@Service
@Transactional
public class NoteServiceMonarch {
	
	@Inject
	private NoteMonarchRepository noteMonarchRepository;
	
	@Inject
	private PatientNoEventMonarchService patientNoEventMonarchService;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private UserPatientRepository userPatientRepository;
	
	public Map<LocalDate,NoteMonarch> findByPatientUserIdAndCreatedOnBetweenGroupByCreatedOn(Long patientUserId,LocalDate from,LocalDate to,Boolean isDeleted){
		List<NoteMonarch> notes = noteMonarchRepository.findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnAsc(patientUserId, from, to, false);
		Map<LocalDate,NoteMonarch> dateNotesMap = new TreeMap<>();
		for(NoteMonarch note : notes){
			dateNotesMap.put(note.getCreatedOn(), note);
		}
		return dateNotesMap;
	}
	
	public Page<NoteMonarch> findByUserIdAndDateRange(Long userId,LocalDate from,LocalDate to,Boolean isDeleted,Pageable pageable){
		Page<NoteMonarch> notes = noteMonarchRepository.findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnDesc(userId,from,to,isDeleted,pageable);
		return notes;
	}

	public NoteMonarch findOneByUserIdAndDate(Long userId,LocalDate date){
		Optional<NoteMonarch> note =  noteMonarchRepository.findOneByPatientUserIdAndCreatedOn(userId, date);
		if(note.isPresent())
			return note.get();
		return null;
	}
	
	public NoteMonarch saveOrUpdateNoteByUserId(Long userId,String note,LocalDate date) throws HillromException{
		if(StringUtils.isBlank(note))
			return null;
		
		
		PatientNoEventMonarch patientNoEvent = patientNoEventMonarchService.findByPatientUserId(userId);
		
		if(Objects.isNull(patientNoEvent))
			throw new HillromException(ExceptionConstants.HR_585);
		if(Objects.isNull(patientNoEvent.getFirstTransmissionDate()) ||  date.isBefore(patientNoEvent.getFirstTransmissionDate()))
			throw new HillromException(ExceptionConstants.HR_701);
			
		NoteMonarch existingNote = findOneByUserIdAndDate(userId,date);
		if(Objects.isNull(existingNote)){
			existingNote = new NoteMonarch();
			User patientUser = userRepository.findOne(userId);
			PatientInfo patient = userService.getPatientInfoObjFromPatientUser(patientUser);
			if(Objects.isNull(patient))
				return null;
			existingNote.setPatient(patient);
			existingNote.setPatientUser(patientUser);
			existingNote.setNote(note);
			existingNote.setCreatedOn(date);
			noteMonarchRepository.save(existingNote);
		}else{
			existingNote.setNote(note);
			existingNote.setDeleted(false);
			noteMonarchRepository.save(existingNote);
		}
		return existingNote;
	}
	
	public NoteMonarch findOneByPatientIdAndDate(String patientId,LocalDate date){
		Optional<NoteMonarch> note =  noteMonarchRepository.findOneByPatientIdAndCreatedOn(patientId,date);
		if(note.isPresent())
			return note.get();
		return null;
	}
	
	public NoteMonarch saveOrUpdateNoteByPatientId(String patientId,String note,LocalDate date) throws HillromException{
		PatientNoEventMonarch patientNoEvent = patientNoEventMonarchService.findByPatientId(patientId);
		if(Objects.nonNull(patientNoEvent))
			throw new HillromException(ExceptionConstants.HR_585);
		if(Objects.isNull(patientNoEvent.getFirstTransmissionDate()) ||  date.isBefore(patientNoEvent.getFirstTransmissionDate()))
			throw new HillromException(ExceptionConstants.HR_701);
		if(StringUtils.isBlank(note))
			return null;
		NoteMonarch existingNote = findOneByPatientIdAndDate(patientId,date);
		if(Objects.isNull(existingNote)){
			existingNote = new NoteMonarch();
			List<UserPatientAssoc> associations = userPatientRepository.findOneByPatientId(patientId);
			UserPatientAssoc userPatientAssoc = (UserPatientAssoc) associations.stream().filter(assoc -> 
				RelationshipLabelConstants.SELF.equalsIgnoreCase(assoc.getRelationshipLabel())
			);
			if(Objects.nonNull(userPatientAssoc)){				
				existingNote.setPatient(userPatientAssoc.getPatient());
				existingNote.setPatientUser(userPatientAssoc.getUser());
				existingNote.setNote(note);
				existingNote.setCreatedOn(date);
				noteMonarchRepository.save(existingNote);
			}
		}else{
			existingNote.setNote(note);
			existingNote.setDeleted(false);
			noteMonarchRepository.save(existingNote);
		}
		return existingNote;
	}
	
}
