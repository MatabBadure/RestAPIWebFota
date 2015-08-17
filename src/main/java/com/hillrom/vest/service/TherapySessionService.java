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
			TherapySession latestThreapySession = existingTherapySessions.get(0);
			Iterator<TherapySession> tpsIterator = therapySessions.iterator();
			while(tpsIterator.hasNext()){
				TherapySession tps = tpsIterator.next();
				// Remove previous therapy Sessions
				int tpsDayOfYear = tps.getDate().getDayOfYear();
				int latestTpsDayOfYear = latestThreapySession.getDate().getDayOfYear();
				if(tpsDayOfYear < latestTpsDayOfYear){
					tpsIterator.remove();
					//Remove previous therapySessions of the same day.
				} else {
					Integer tpsSessionNo = tps.getSessionNo();
					Integer latestTpsSessionNo = latestThreapySession.getSessionNo();
					if(tpsDayOfYear == latestTpsDayOfYear && tpsSessionNo <= latestTpsSessionNo){
						tpsIterator.remove();
					}
				}
			}
		}
		return therapySessionRepository.save(therapySessions);
	}
}
