package com.hillrom.vest.service.monarch;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.hillrom.vest.domain.NoteMonarch;

import com.hillrom.vest.repository.monarch.NoteMonarchRepository;

@Service
@Transactional
public class NoteServiceMonarch {
	
	@Inject
	private NoteMonarchRepository noteMonarchRepository;
	
	public Map<LocalDate,NoteMonarch> findByPatientUserIdAndCreatedOnBetweenGroupByCreatedOn(Long patientUserId,LocalDate from,LocalDate to,Boolean isDeleted){
		List<NoteMonarch> notes = noteMonarchRepository.findByPatientUserIdAndCreatedOnBetweenAndDeletedOrderByCreatedOnAsc(patientUserId, from, to, false);
		Map<LocalDate,NoteMonarch> dateNotesMap = new TreeMap<>();
		for(NoteMonarch note : notes){
			dateNotesMap.put(note.getCreatedOn(), note);
		}
		return dateNotesMap;
	}

}
