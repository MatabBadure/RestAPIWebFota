package com.hillrom.vest.repository;

import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.web.rest.dto.AnnouncementsDTO;

import java.math.BigInteger;


@Repository
public class AnnouncementsPermissionRepository {

	@Inject
	private EntityManager entityManager;
	
	private final Logger log = LoggerFactory.getLogger(AnnouncementsPermissionRepository.class);
	private static final String ORDER_BY_CLAUSE_START = " order by ";
	
	public Page<Announcements> findAnnouncementsByPatientId(Pageable pageable, Map<String, Boolean> sortOrder, String patientId,boolean isDeleted) {

		String findAnnouncementsByPatientQuery = " SELECT ac.id,ac.clinic_type,ac.name, ac.patient_type, ac.pdf_file_path, ac.send_to, ac.subject, ac.created_date, ac.modified_date, ac.start_date, ac.end_date from  ANNOUNCEMENTS ac, PATIENT_INFO pi "
				+ " where (ac.send_to = 'All' or ac.send_to = 'Patient') and (ac.patient_type = 'All' or pi.primary_diagnosis = ac.patient_type or "
				+ " IF(TIMESTAMPDIFF(YEAR, pi.dob, CURDATE()) > 18, 'Adult', 'Peds') = ac.patient_type )  "
				+ " and pi.id = :patientId and ac.is_deleted = :isDeleted and  ac.start_date <= now() and (ac.end_date >= now())";
		

		log.debug("Query before replacement : " + findAnnouncementsByPatientQuery);
		findAnnouncementsByPatientQuery = findAnnouncementsByPatientQuery.replaceAll(":patientId", "'" + patientId + "'");
		findAnnouncementsByPatientQuery = findAnnouncementsByPatientQuery.replaceAll(":isDeleted", String.valueOf(isDeleted));
		log.debug("Query after replacement : " + findAnnouncementsByPatientQuery);
		
		

		String countSqlQuery = "select count(*) from (" + findAnnouncementsByPatientQuery + " ) hcpUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();
		log.debug("Query Count : " + count);

		Query query = getOrderedByQuery(findAnnouncementsByPatientQuery, sortOrder);
		log.debug("Order by Query : " + query);
		
		List<Announcements> announcements = new ArrayList<>();

		List<Object[]> results = query.getResultList();
		log.debug("Query results : " + results);

		results.forEach((record) -> {
			Long id = ((BigInteger) record[0]).longValue();
			String clinicType = (String) record[1];
			String name = (String) record[2];
			String patientType = (String) record[3];
			String pdfFilePath = (String) record[4];
			String sentTo = (String) record[5];
			String subject = (String) record[6];
			DateTime createdAt = new DateTime(record[7]);
			DateTime modifiedAt = new DateTime(record[8]);
			LocalDate startDate = new LocalDate(record[7]);
			LocalDate endDate = new LocalDate(record[8]);
			
			Announcements announcement = new Announcements();
			announcement.setId(id);
			log.debug("id : " + id);
			
			announcement.setClinicType(clinicType);
			log.debug("clinicType : " + clinicType);
			
			announcement.setName(name);
			log.debug("name : " + name);
			
			announcement.setPatientType(patientType);
			log.debug("patientType : " + patientType);
			
			announcement.setPdfFilePath(pdfFilePath);
			log.debug("pdfFilePath : " + pdfFilePath);
			
			announcement.setSendTo(sentTo);
			log.debug("sentTo : " + sentTo);
			
			announcement.setSubject(subject);
			log.debug("subject : " + subject);
			
			announcement.setCreatedDate(createdAt);
			log.debug("createdAt : " + createdAt);
			
			announcement.setModifiedDate(modifiedAt);
			log.debug("modifiedAt : " + modifiedAt);
			
			announcement.setStartDate(startDate);
			log.debug("startDate : " + startDate);
			
			announcement.setEndDate(endDate);
			log.debug("endDate : " + endDate);

			announcements.add(announcement);

		});
		int firstResult = pageable.getOffset();
		int maxResults = firstResult + pageable.getPageSize();
		List<Announcements> announcementsSubList = new ArrayList<>();
		if (firstResult < announcements.size()) {
			maxResults = maxResults > announcements.size() ? announcements.size() : maxResults;
			announcementsSubList = announcements.subList(firstResult, maxResults);
		}
		Page<Announcements> page = new PageImpl<Announcements>(announcementsSubList, pageable, count.intValue());
		return page;
	}
	
	private Query getOrderedByQuery(String queryString, Map<String, Boolean> columnNames) {

		StringBuilder sb = new StringBuilder();
		// Append order by only if there is any sort request.
		if (columnNames.keySet().size() > 0) {
			sb.append(ORDER_BY_CLAUSE_START);
		}

		int limit = columnNames.size();
		int i = 0;
		for (String columnName : columnNames.keySet()) {
			if(!Constants.ADHERENCE.equalsIgnoreCase(columnName))
				sb.append("lower(").append(columnName).append(")");
			else
				sb.append(columnName);

			if (columnNames.get(columnName))
				sb.append(" ASC");
			else
				sb.append(" DESC");

			if (i++ != (limit - 1)) {
				sb.append(", ");
			}
		}
		
		log.debug("Search Query :: "+queryString + sb.toString());
		
		Query jpaQuery = entityManager.createNativeQuery(queryString + sb.toString());
		return jpaQuery;
	}
	

}
