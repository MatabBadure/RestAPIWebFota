package com.hillrom.vest.audit.service;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
		ProtocolConstants defaultProtocol = protocolConstantsRepository.findOne(1L);
		prepareCurrentProtocolRevisions(protocolRevMap, revisionsList,defaultProtocol);
		return protocolRevMap;
	}

	private void prepareCurrentProtocolRevisions(
			SortedMap<DateTime, ProtocolRevisionVO> protocolRevMap,
			List<PatientProtocolData> protocolsList,ProtocolConstants defaultProtocol) {
		SortedMap<DateTime, List<PatientProtocolData>> protocolsMap = new TreeMap<>(protocolsList.stream().collect(Collectors.groupingBy(PatientProtocolData::getLastModifiedDate)));
		for(DateTime lastModifiedDate : protocolsMap.keySet()){
			ProtocolRevisionVO revision = protocolRevMap.get(lastModifiedDate);
			List<PatientProtocolData> protocolData = protocolsMap.get(lastModifiedDate);
			boolean isDeleted = protocolData.get(protocolData.size()-1).isDeleted();
			DateTime revisionCreatedDate = protocolData.get(protocolData.size()-1).getLastModifiedDate(); 
			if(Objects.isNull(revision)){
				revision = new ProtocolRevisionVO(revisionCreatedDate,null);
			}
			for(PatientProtocolData protocol : protocolData){
				revision.addProtocol(ProtocolDataVOBuilder.convertProtocolDataToVO(protocol));
			}
			updateLatestRevision(protocolRevMap, lastModifiedDate);
			if(isDeleted){
				ProtocolRevisionVO nextRevision = new ProtocolRevisionVO(revisionCreatedDate, null);
				protocolRevMap.put(revisionCreatedDate.plusSeconds(1),nextRevision);
			}
			protocolRevMap.put(revision.getFrom(),revision);
		}
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
