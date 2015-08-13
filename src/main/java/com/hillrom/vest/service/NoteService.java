package com.hillrom.vest.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.Note;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.repository.NoteRepository;
import com.hillrom.vest.repository.UserPatientRepository;
import com.hillrom.vest.repository.UserRepository;
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
	
	public Note saveOrUpdateNoteByUserId(Long userId,String note){
		if(StringUtils.isBlank(note))
			return null;
		Note existingNote = findOneByUserIdAndDate(userId,LocalDate.now());
		if(Objects.isNull(existingNote)){
			existingNote = new Note();
			User patientUser = userRepository.findOne(userId);
			PatientInfo patient = userService.getPatientInfoObjFromPatientUser(patientUser);
			if(Objects.isNull(patient))
				return null;
			existingNote.setPatient(patient);
			existingNote.setPatientUser(patientUser);
			existingNote.setNote(note);
			noteRepository.save(existingNote);
		}else{
			existingNote.setNote(note);
			noteRepository.save(existingNote);
		}
		return existingNote;
	}
	
	public Note saveOrUpdateNoteByPatientId(String patientId,String note){
		if(StringUtils.isBlank(note))
			return null;
		Note existingNote = findOneByPatientIdAndDate(patientId,LocalDate.now());
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
				noteRepository.save(existingNote);
			}
		}else{
			existingNote.setNote(note);
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
}
