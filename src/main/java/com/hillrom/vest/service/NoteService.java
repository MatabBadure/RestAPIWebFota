package com.hillrom.vest.service;

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
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.NoteRepository;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;

@Service
@Transactional
public class NoteService {

	@Inject
	private UserService userService;
	
	@Inject
	private NoteRepository noteRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserPatientRepository userPatientRepository;
	
	@Inject
	private PatientNoEventService patientNoEventService;

	@Inject
	private PatientInfoService patientInfoService;
	
	public Note findOneByUserIdAndDate(Long userId,LocalDate date){
		Optional<Note> note =  noteRepository.findOneByPatientUserIdAndCreatedOn(userId, date);
		if(note.isPresent())
			return note.get();
		return null;
	}
	
	
	public Note findOneByPatientIdAndDate(String patientId,LocalDate date){
		Optional<Note> note =  noteRepository.findOneByPatientIdAndCreatedOn(patientId,date);
		if(note.isPresent())
			return note.get();
		return null;
	}
	
	public Note findOneByUserIdAndPatientID(Long userId, String patientId){
		Optional<Note> note =  noteRepository.findOneByPatientUserIdAndPatientId(userId, patientId);
		if(note.isPresent())
			return note.get();
		return null;
	}
	
	public Note saveOrUpdateNoteByUserId(Long userId,String note,LocalDate date) throws HillromException{
		if(StringUtils.isBlank(note))
			return null;
		
		
		PatientNoEvent patientNoEvent = patientNoEventService.findByPatientUserId(userId);
		
		if(Objects.isNull(patientNoEvent))
			throw new HillromException(ExceptionConstants.HR_585);
		if(Objects.isNull(patientNoEvent.getFirstTransmissionDate()) ||  date.isBefore(patientNoEvent.getFirstTransmissionDate()))
			throw new HillromException(ExceptionConstants.HR_701);
			
		Note existingNote = findOneByUserIdAndDate(userId,date);
		if(Objects.isNull(existingNote)){
			existingNote = new Note();
			User patientUser = userRepository.findOne(userId);
			PatientInfo patient = userService.getPatientInfoObjFromPatientUser(patientUser);
			if(Objects.isNull(patient))
				return null;
			existingNote.setPatient(patient);
			existingNote.setPatientUser(patientUser);
			existingNote.setNote(note);
			existingNote.setCreatedOn(date);
			noteRepository.save(existingNote);
		}else{
			existingNote.setNote(note);
			existingNote.setDeleted(false);
			noteRepository.save(existingNote);
		}
		return existingNote;
	}
	
	public Note saveOrUpdateNoteByPatientId(String patientId,String note,LocalDate date) throws HillromException{
		PatientNoEvent patientNoEvent = patientNoEventService.findByPatientId(patientId);
		if(Objects.nonNull(patientNoEvent))
			throw new HillromException(ExceptionConstants.HR_585);
		if(Objects.isNull(patientNoEvent.getFirstTransmissionDate()) ||  date.isBefore(patientNoEvent.getFirstTransmissionDate()))
			throw new HillromException(ExceptionConstants.HR_701);
		if(StringUtils.isBlank(note))
			return null;
		Note existingNote = findOneByPatientIdAndDate(patientId,date);
		if(Objects.isNull(existingNote)){
			existingNote = new Note();
			List<UserPatientAssoc> associations = userPatientRepository.findOneByPatientId(patientId);
			UserPatientAssoc userPatientAssoc = (UserPatientAssoc) associations.stream().filter(assoc -> 
				RelationshipLabelConstants.SELF.equalsIgnoreCase(assoc.getRelationshipLabel())
			);
			if(Objects.nonNull(userPatientAssoc)){				
				existingNote.setPatient(userPatientAssoc.getPatient());
				existingNote.setPatientUser(userPatientAssoc.getUser());
				existingNote.setNote(note);
				existingNote.setCreatedOn(date);
				noteRepository.save(existingNote);
			}
		}else{
			existingNote.setNote(note);
			existingNote.setDeleted(false);
			noteRepository.save(existingNote);
		}
		return existingNote;
	}
	
	// For updating patients memo notes by HCP/CA when user ID is passed
	public Note saveOrUpdateNoteByUserForPatientId(Long userId, String patientId, String note, LocalDate date) throws HillromException{
		
		if(StringUtils.isBlank(note))
			return null;
				
		Note existingNote = findOneByUserIdAndPatientID(userId, patientId);
		
		// For adding new memo note entered by HCP/CA for the patient with the same date
		if(Objects.isNull(existingNote)){
			existingNote = new Note();
			
			PatientInfo patientInfo = patientInfoService.findOneById(patientId);
	    	existingNote.setPatient(patientInfo);
	    	
	    	User patientUser = userRepository.findOne(userId);
			existingNote.setPatientUser(patientUser);
			existingNote.setNote(note);
			
			existingNote.setCreatedOn(date);
			noteRepository.save(existingNote);
		}else{
			
			// For updating the existing memo note entered by HCP/CA for the patient for the same date
			existingNote.setNote(note);
			existingNote.setDeleted(false);
			noteRepository.save(existingNote);
		}
		
		return existingNote;
	}
	
	// For updating patients memo notes by HCP/CA when HR ID is passed
	public Note saveOrUpdateNoteByUserForPatientId(String userId, String patientId, String note, LocalDate date) throws HillromException{
			
		if(StringUtils.isBlank(note))
			return null;
		
		Optional<User> patientUser = userRepository.findOneByHillromId(userId);
		
		Note existingNote = findOneByUserIdAndPatientID(patientUser.get().getId(), patientId);
		
		// For adding new memo note entered by HCP/CA for the patient with the same date
		if(Objects.isNull(existingNote)){
			existingNote = new Note();
			
			PatientInfo patientInfo = patientInfoService.findOneById(patientId);
	    	existingNote.setPatient(patientInfo);
	    		    	
			existingNote.setPatientUser(patientUser.get());
			existingNote.setNote(note);
			existingNote.setCreatedOn(date);
			noteRepository.save(existingNote);
		}else{
			
			// For updating the existing memo note entered by HCP/CA for the patient for the same date
			existingNote.setNote(note);
			existingNote.setDeleted(false);
			noteRepository.save(existingNote);
		}
		
		return existingNote;
	}
	
	public Note update(Long id,String noteText){
		Note existingNote = noteRepository.findOne(id);
		if(Objects.nonNull(existingNote)){
			existingNote.setNote(noteText);
			noteRepository.save(existingNote);
		}
		return existingNote;	
	}
	
	public void deleteNote(Long id){
		noteRepository.delete(id);
	}
	
	public Page<Note> findByUserIdAndDateRange(Long userId,LocalDate from,LocalDate to,Boolean isDeleted,Pageable pageable){
		Page<Note> notes = noteRepository.findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnDesc(userId,from,to,isDeleted,pageable);
		return notes;
	}
	
	public Map<LocalDate,Note> findByPatientUserIdAndCreatedOnBetweenGroupByCreatedOn(Long patientUserId,LocalDate from,LocalDate to,Boolean isDeleted){
		List<Note> notes = noteRepository.findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnAsc(patientUserId, from, to, false);
		Map<LocalDate,Note> dateNotesMap = new TreeMap<>();
		for(Note note : notes){
			dateNotesMap.put(note.getCreatedOn(), note);
		}
		return dateNotesMap;
	}
}
