package com.hillrom.vest.audit.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.UserPatientAssoc;

@Component("patientProtocolDataAuditRepository")
public class PatientProtocolDataAuditRepository extends
		AuditableRepository<PatientProtocolData> {
	
	
	@Inject
	private EntityManager entityManager;
	
	private final Logger log = LoggerFactory.getLogger(PatientProtocolDataAuditRepository.class);
	
	/**

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
	
	*/



	public List<PatientProtocolData> findProtocolRevisionsByUserIdAndDateRange(Long userId){
		  EntityManager em = getEntityManager();
		  Query userQuery = em.createNativeQuery(" SELECT * from USER_PATIENT_ASSOC where relation_label = 'SELF' and user_id = ?", UserPatientAssoc.class);
		  userQuery.setParameter(1, userId);
		  UserPatientAssoc upa = (UserPatientAssoc) userQuery.getSingleResult();
		  
		  Query query = em.createNativeQuery("SELECT * FROM PATIENT_PROTOCOL_DATA_AUD where USER_ID = ? ").setParameter(1, userId);
		  
		  /*AuditReader reader = getAuditReader();
		  AuditQuery query = getAuditReader().createQuery()
		             .forRevisionsOfEntity(PatientProtocolData.class, false, true)
		             .add(AuditEntity.relatedId("patientUser").eq(userId))
		             .addOrder(AuditEntity.property("lastModifiedDate").asc());*/
		  List<Object[]> results= query.getResultList();
		  List<PatientProtocolData> resultList = new LinkedList<PatientProtocolData>();
		  for(int i =0;i<results.size();i++){
		   Object[] value = results.get(i);
		   PatientProtocolData ppd = new PatientProtocolData();
		   ppd.setPatient(upa.getPatient());
		   ppd.setPatientUser(upa.getUser());
		   ppd.setId(value[0].toString());
		   ppd.setCreatedBy(value[3].toString());
		   ppd.setCreatedDate(new DateTime((java.sql.Timestamp)value[4]));
		   ppd.setLastModifiedBy(value[5].toString());
		   ppd.setLastModifiedDate(new DateTime((java.sql.Timestamp)value[6]));
		   ppd.setDeleted((boolean)value[7]);
		   ppd.setMinFrequency((Integer)value[10]);
		   ppd.setMinMinutesPerTreatment((Integer)value[11]);
		   ppd.setMinPressure((Integer)value[12]);
		   ppd.setProtocolKey(value[13].toString());
		   ppd.setTreatmentLabel(Objects.nonNull(value[14])?value[14].toString():null);
		   ppd.setTreatmentsPerDay((Integer)value[15]);
		   ppd.setType(value[16].toString());
		   resultList.add(ppd);
		  }
		        return resultList;
	}

	
	
	
}
