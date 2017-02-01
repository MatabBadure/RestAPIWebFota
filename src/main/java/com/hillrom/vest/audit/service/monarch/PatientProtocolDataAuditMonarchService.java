package com.hillrom.vest.audit.service.monarch;

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
import com.hillrom.vest.audit.repository.monarch.PatientProtocolDataAuditMonarchRepository;
import com.hillrom.vest.audit.service.AuditableService;
import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.PatientProtocolDataMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.ProtocolConstantsRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.repository.monarch.ProtocolConstantsMonarchRepository;
import com.hillrom.vest.service.PatientProtocolService;
import com.hillrom.vest.web.rest.dto.ProtocolDataVO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.dto.monarch.ProtocolRevisionMonarchVO;
import com.hillrom.vest.web.rest.util.ProtocolDataVOBuilder;
import com.hillrom.vest.web.rest.util.monarch.ProtocolDataMonarchVOBuilder;

@Component("patientProtocolDataAuditService")
public class PatientProtocolDataAuditMonarchService extends AuditableService<PatientProtocolDataMonarch> {

	@Inject
	@Qualifier("patientProtocolDataAuditRepository")
	PatientProtocolDataAuditMonarchRepository protocolDataAuditMonarchRepository;
	
	
	@Inject
	private ProtocolConstantsMonarchRepository protocolConstantsMonarchRepository;
	

	
	public List<PatientProtocolDataMonarch> findProtocolRevisionsByUserIdAndDateRange(Long userId,DateTime datetime){
		return protocolDataAuditMonarchRepository.findProtocolRevisionsByUserIdAndDateRange(userId);
	}
	
	public SortedMap<DateTime,ProtocolRevisionMonarchVO> findProtocolRevisionsByUserIdTillDate(Long userId,DateTime dateTime) throws HillromException{
		SortedMap<DateTime,ProtocolRevisionMonarchVO> protocolRevMap = new TreeMap<>();
		List<PatientProtocolDataMonarch> revisionsList = findProtocolRevisionsByUserIdAndDateRange(userId,dateTime);
		ProtocolConstantsMonarch defaultProtocol = protocolConstantsMonarchRepository.findOne(1L);
		prepareCurrentProtocolRevisions(protocolRevMap, revisionsList,defaultProtocol);
		return protocolRevMap;
	}

	private void prepareCurrentProtocolRevisions(
			SortedMap<DateTime, ProtocolRevisionMonarchVO> protocolRevMap,
			List<PatientProtocolDataMonarch> protocolsList,ProtocolConstantsMonarch defaultProtocol) {
		SortedMap<DateTime, List<PatientProtocolDataMonarch>> protocolsMap = new TreeMap<>(protocolsList.stream().collect(Collectors.groupingBy(PatientProtocolDataMonarch::getLastModifiedDate)));
		for(DateTime lastModifiedDate : protocolsMap.keySet()){
			ProtocolRevisionMonarchVO revision = protocolRevMap.get(lastModifiedDate);
			List<PatientProtocolDataMonarch> protocolData = protocolsMap.get(lastModifiedDate);
			boolean isDeleted = protocolData.get(protocolData.size()-1).isDeleted();
			DateTime revisionCreatedDate = protocolData.get(protocolData.size()-1).getLastModifiedDate(); 
			if(Objects.isNull(revision)){
				revision = new ProtocolRevisionMonarchVO(revisionCreatedDate,null);
			}
			for(PatientProtocolDataMonarch protocol : protocolData){
				revision.addProtocol(ProtocolDataMonarchVOBuilder.convertProtocolDataMonarchToVO(protocol));
			}
			updateLatestRevision(protocolRevMap, lastModifiedDate);
			if(isDeleted){
				ProtocolRevisionMonarchVO nextRevision = new ProtocolRevisionMonarchVO(revisionCreatedDate, null);
				nextRevision.addProtocol(ProtocolDataMonarchVOBuilder.convertProtocolConstantsMonarchToVO(defaultProtocol));
				protocolRevMap.put(revisionCreatedDate.plusSeconds(1),nextRevision);
			}
			protocolRevMap.put(revision.getFrom(),revision);
		}
	}
	
	private void updateLatestRevision(
			SortedMap<DateTime, ProtocolRevisionMonarchVO> protocolRevMap,
			DateTime lastModifiedDate) {
		SortedMap<DateTime,ProtocolRevisionMonarchVO> previousRevisionMap = protocolRevMap.headMap(lastModifiedDate);
		if(Objects.nonNull(previousRevisionMap) && previousRevisionMap.size() > 0){
			DateTime lastKey = previousRevisionMap.lastKey();
			ProtocolRevisionMonarchVO prevRevision = previousRevisionMap.get(lastKey);
			prevRevision.setTo(lastModifiedDate);
			protocolRevMap.put(lastKey, prevRevision);
		}
	}
	
}
