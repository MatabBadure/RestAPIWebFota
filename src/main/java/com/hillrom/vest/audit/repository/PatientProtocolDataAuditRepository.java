package com.hillrom.vest.audit.repository;

import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Component;

import com.hillrom.vest.domain.PatientProtocolData;

@Component("patientProtocolDataAuditRepository")
public class PatientProtocolDataAuditRepository extends
		AuditableRepository<PatientProtocolData> {

	public List<PatientProtocolData> findProtocolRevisionsByUserIdAndDateRange(Long userId){
		AuditReader reader = getAuditReader();
		AuditQuery query = getAuditReader().createQuery()
	            .forRevisionsOfEntity(PatientProtocolData.class, false, true)
	            .add(AuditEntity.relatedId("patientUser").eq(userId))
	            .addOrder(AuditEntity.property("lastModifiedDate").asc());
		List<Object[]> results= query.getResultList();
        final List<PatientProtocolData> resultList = convertToEntity(results);
        return resultList;
	}

}
