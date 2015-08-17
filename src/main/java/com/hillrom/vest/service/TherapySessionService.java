package com.hillrom.vest.service;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.TherapySessionRepository;

@Service
@Transactional
public class TherapySessionService {

	@Inject
	private TherapySessionRepository therapySessionRepository;
	
	public List<TherapySession> saveOrUpdate(List<TherapySession> therapySessions){
		User patientUser = therapySessions.get(0).getPatientUser();
		List<TherapySession> existingTherapySessions =  therapySessionRepository.findByPatientUserId(patientUser.getId());
		// Removing existing therapySessions from DB
		if(existingTherapySessions.size() > 0){
			Iterator<TherapySession> tpsIterator = therapySessions.iterator();
			while(tpsIterator.hasNext()){
				TherapySession tps = tpsIterator.next();
				if(tps.getDate().isBefore(existingTherapySessions.get(0).getDate())){
					tpsIterator.remove();
				}
			}
		}
		return therapySessionRepository.save(therapySessions);
	}
}
