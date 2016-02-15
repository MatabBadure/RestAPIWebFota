package com.hillrom.vest.audit.service;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hillrom.vest.audit.repository.PatientProtocolDataAuditRepository;
import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.PatientProtocolService;
import com.hillrom.vest.web.rest.dto.ProtocolDataVO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.util.ProtocolDataVOBuilder;

@Component("patientProtocolDataAuditService")
public class PatientProtocolDataAuditService extends AuditableService<PatientProtocolData> {

	@Inject
	@Qualifier("patientProtocolDataAuditRepository")
	PatientProtocolDataAuditRepository protocolDataAuditRepository;
	
	@Inject
	PatientProtocolService protocolService;
	
	@Inject
	private ProtocolConstantsRepository protocolConstantsRepository;
	
	@Inject
	private UserRepository userRepository;

	
	public List<PatientProtocolData> findProtocolRevisionsByUserIdAndDateRange(Long userId,DateTime datetime){
		return protocolDataAuditRepository.findProtocolRevisionsByUserIdAndDateRange(userId);
	}
	
	public SortedMap<DateTime,ProtocolRevisionVO> findProtocolRevisionsByUserIdTillDate(Long userId,DateTime dateTime) throws HillromException{
		SortedMap<DateTime,ProtocolRevisionVO> protocolRevMap = new TreeMap<>();
		List<PatientProtocolData> revisionsList = findProtocolRevisionsByUserIdAndDateRange(userId,dateTime);
		List<PatientProtocolData> existingProtocols = protocolService.getAllProtocolsAssociatedWithPatient(userId);
		ProtocolConstants defaultProtocol = protocolConstantsRepository.findOne(1L);
		addDefaultProtocolOnUserCreation(userId, protocolRevMap,defaultProtocol);
		prepareCurrentProtocolRevisions(protocolRevMap, existingProtocols,defaultProtocol);
		prepareProtocolRevisions(protocolRevMap, revisionsList,defaultProtocol);
		
		/*if(protocolRevMap.size() == 1){
			prepareCurrentProtocolRevisions(protocolRevMap, existingProtocols,defaultProtocol);
		}*/
		return protocolRevMap;
	}

	private void addDefaultProtocolOnUserCreation(Long userId,
			SortedMap<DateTime, ProtocolRevisionVO> protocolRevMap,
			ProtocolConstants defaultProtocol) {
		User user = userRepository.findOne(userId);
		DateTime dt = user.getCreatedDate();
		ProtocolRevisionVO defaultAssignment = createRevision(dt, null);
		defaultAssignment.addProtocol(ProtocolDataVOBuilder.convertProtocolConstantsToVO(defaultProtocol));
		// default protocol to be available on user creation,hence 1 minute is deducted from created date
		protocolRevMap.put(dt.minusMinutes(1), defaultAssignment); 
	}

	private void prepareProtocolRevisions(
			SortedMap<DateTime, ProtocolRevisionVO> protocolRevMap,
			List<PatientProtocolData> revisionsList,ProtocolConstants defaultProtocol) {
		for(PatientProtocolData protocol : revisionsList){
			DateTime lastModifiedDate = protocol.getLastModifiedDate();
			updateLatestRevision(protocolRevMap, lastModifiedDate);
			ProtocolRevisionVO revision = protocolRevMap.get(lastModifiedDate);
			if(Objects.isNull(revision) && !protocol.isDeleted()){
				ProtocolDataVO dataVO = ProtocolDataVOBuilder.convertProtocolDataToVO(protocol);
				SortedMap<DateTime, ProtocolRevisionVO> headMap = protocolRevMap.headMap(lastModifiedDate);
				DateTime activeFrom = protocol.getCreatedDate();
				DateTime latestKey = null;
				if(Objects.nonNull(headMap) && headMap.size() > 0){
					activeFrom = latestKey = headMap.lastKey();
					activeFrom = headMap.get(activeFrom).getTo();
				} 
				revision = createRevision(activeFrom, null);
				if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(protocol.getType())){
					revision.addProtocol(dataVO);
				}else{
					// Handle custom protocol create & update revisions
					ProtocolRevisionVO existingRev = protocolRevMap.get(latestKey);
					if(Objects.nonNull(existingRev)){
						if(existingRev.getProtcols().size() > 0 &&
								Constants.CUSTOM_PROTOCOL.equalsIgnoreCase(existingRev.getProtcols().get(0).getType())){
							for(ProtocolDataVO  vo : existingRev.getProtcols()){
								if(!vo.getId().equalsIgnoreCase(protocol.getId()) ){
									revision.addProtocol(vo);
								}
							}
						}
					}
					revision.addProtocol(dataVO);
				}
				protocolRevMap.put(activeFrom, revision);
			}else{
				if(protocol.isDeleted()){
					updateLatestRevision(protocolRevMap, lastModifiedDate);
					ProtocolDataVO vo = ProtocolDataVOBuilder.convertProtocolConstantsToVO(defaultProtocol);
					ProtocolRevisionVO defaultAssignment = createRevision(lastModifiedDate,null);
					defaultAssignment.addProtocol(vo);
					protocolRevMap.put(lastModifiedDate.plusSeconds(1), defaultAssignment);
				}else{
					List<ProtocolDataVO> protocolRev = revision.getProtcols();
					ProtocolDataVO lastProtocolRev = protocolRev.get(protocolRev.size()-1);
					
					if(Constants.NORMAL_PROTOCOL.equalsIgnoreCase(lastProtocolRev.getType())){
						ProtocolRevisionVO nextRevision = createRevision(protocol.getLastModifiedDate(), null);
						nextRevision.addProtocol(ProtocolDataVOBuilder.convertProtocolDataToVO(protocol));
						protocolRevMap.put(lastModifiedDate.plusSeconds(1), nextRevision);
						revision.setTo(lastModifiedDate);
					}

					ProtocolRevisionVO nextRevision = createNewRevisionForUpdate(protocolRevMap,protocol);
					
					protocolRevMap.put(lastModifiedDate, nextRevision);
				}
			} 
		}
	}

	private ProtocolRevisionVO createRevision(DateTime from,DateTime to) {
		ProtocolRevisionVO newRevision = new ProtocolRevisionVO();
		newRevision.setFrom(from);
		newRevision.setTo(to);
		return newRevision;
	}

	private void prepareCurrentProtocolRevisions(
			SortedMap<DateTime, ProtocolRevisionVO> protocolRevMap,
			List<PatientProtocolData> protocolsList,ProtocolConstants defaultProtocol) {
		for(PatientProtocolData protocol : protocolsList){
			ProtocolDataVO dataVO = ProtocolDataVOBuilder.convertProtocolDataToVO(protocol);
			ProtocolRevisionVO revision = protocolRevMap.get(protocol.getLastModifiedDate());
			DateTime activeTillDate = protocol.isDeleted() ? protocol.getLastModifiedDate() : null; 
			if(Objects.isNull(revision)){
				revision = createRevision(protocol.getCreatedDate(),activeTillDate);
			}
			revision.addProtocol(dataVO);
			if(protocol.isDeleted()){
				updateLatestRevision(protocolRevMap, protocol.getLastModifiedDate());
				ProtocolDataVO defProDataVO = ProtocolDataVOBuilder.convertProtocolConstantsToVO(defaultProtocol);
				ProtocolRevisionVO nextRevision = createRevision(protocol.getLastModifiedDate(), null);
				nextRevision.addProtocol(defProDataVO);
				protocolRevMap.put(protocol.getLastModifiedDate().plusSeconds(1),nextRevision);
			}
			protocolRevMap.put(protocol.getLastModifiedDate(), revision);
		}
	}
	
	private ProtocolRevisionVO createNewRevisionForUpdate(
			SortedMap<DateTime, ProtocolRevisionVO> protocolRevMap,
			PatientProtocolData protocolData) {
		SortedMap<DateTime, ProtocolRevisionVO> recentRevMap = protocolRevMap.headMap(protocolData.getLastModifiedDate().plusMillis(1));
		ProtocolRevisionVO revision = null;
		if(Objects.nonNull(recentRevMap) && recentRevMap.size() > 0){
			revision = recentRevMap.get(recentRevMap.lastKey());
		}
		if(Objects.isNull(revision)){
			revision = createRevision(protocolData.getLastModifiedDate(), null);
		}
		List<ProtocolDataVO> previousProtocol = revision.getProtcols();
			int index = -1;
			for(int i = 0; i < revision.getProtcols().size(); i++){
				if(protocolData.getId().equalsIgnoreCase(previousProtocol.get(i).getId())){
					index = i;
					break;
				}
			}
			if(index > -1){
				revision.getProtcols().set(index, ProtocolDataVOBuilder.convertProtocolDataToVO(protocolData));
			}else{
				revision.addProtocol(ProtocolDataVOBuilder.convertProtocolDataToVO(protocolData));
			}
			return revision;
	}

	private void updateLatestRevision(
			SortedMap<DateTime, ProtocolRevisionVO> protocolRevMap,
			DateTime lastModifiedDate) {
		SortedMap<DateTime,ProtocolRevisionVO> previousRevisionMap = protocolRevMap.headMap(lastModifiedDate);
		if(Objects.nonNull(previousRevisionMap) && previousRevisionMap.size() > 0){
			DateTime lastKey = previousRevisionMap.lastKey();
			ProtocolRevisionVO prevRevision = previousRevisionMap.get(lastKey);
			prevRevision.setTo(lastModifiedDate);
			protocolRevMap.put(lastKey, prevRevision);
		}
	}
	
}
