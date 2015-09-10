package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.web.rest.dto.PatientUserVO;

@Repository
public class UserSearchRepository {

	private static final String ORDER_BY_CLAUSE_START = " order by ";
	@Inject
	private EntityManager entityManager;

	public Page<HillRomUserVO> findHillRomTeamUsersBy(String queryString,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findHillromTeamUserQuery = "select distinct(user.id),user.first_name as firstName,user.last_name as lastName,user.email,"
				+ " user_authority.authority_name as name,user.is_deleted as isDeleted,user.created_date as createdAt,user.activated as isActivated,user.hillrom_id as hillromId "
				+ " from  USER_EXTENSION userExt join USER user on user.id = userExt.user_id and "
				+ " (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(user.email) like lower(:queryString) or lower(user.hillrom_id) like lower(:queryString)) "
				+ " join  USER_AUTHORITY user_authority on user_authority.user_id = user.id "
				+ " and  user_authority.authority_name in ('ADMIN','ACCT_SERVICES','ASSOCIATES','HILLROM_ADMIN')";

		findHillromTeamUserQuery = findHillromTeamUserQuery.replaceAll(
				":queryString", queryString);
		String countSqlQuery = "select count(hillromUsers.id) from ("
				+ findHillromTeamUserQuery + ") hillromUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findHillromTeamUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<HillRomUserVO> hrUsersList = new ArrayList<>();
		List<Object[]> results = query.getResultList();
		results.stream().forEach(
				(record) -> {
					Long id = ((BigInteger) record[0]).longValue();
					String firstName = (String) record[1];
					String lastName = (String) record[2];
					String email = (String) record[3];
					String role = (String) record[4];
					Boolean isDeleted = (Boolean) record[5];
					Timestamp createdAt = (Timestamp) record[6];
					Boolean isActivated = (Boolean) record[7];
					String hillromId = (String)record[8];
					DateTime createdAtDatetime = new DateTime(createdAt);
					
					HillRomUserVO hrUserVO = new HillRomUserVO(id, firstName,
							lastName, email, role, isDeleted,createdAtDatetime,isActivated,hillromId);
					hrUsersList.add(hrUserVO);
				});

		Page<HillRomUserVO> page = new PageImpl<HillRomUserVO>(hrUsersList,
				null, count.intValue());

		return page;
	}

	private void setPaginationParams(Pageable pageable, Query query) {
		int firstResult = pageable.getPageNumber() * pageable.getOffset();
		int maxResult = pageable.getPageSize();
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
	}

	public Page<HcpVO> findHCPBy(String queryString, Pageable pageable,
			Map<String, Boolean> sortOrder) {
		
		String findHcpQuery = "select user.id,user.email,user.first_name as firstName,user.last_name as lastName,user.is_deleted as isDeleted,"
				+ " user.zipcode,userExt.address,userExt.city,userExt.credentials,userExt.fax_number,userExt.primary_phone,"
				+ " userExt.mobile_phone,userExt.speciality,userExt.state,clinic.id as clinicId,clinic.name as clinicName,user.created_date as createdAt,user.activated isActivated,userExt.npi_number as npiNumber "
				+ " FROM USER user join USER_EXTENSION userExt on user.id = userExt.user_id "
				+ " and (lower(user.first_name) like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or  lower(user.email) like lower(:queryString)) "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = 'HCP' "
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = user.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = user.id ";

		findHcpQuery = findHcpQuery.replaceAll(":queryString", queryString);

		String countSqlQuery = "select count(distinct hcpUsers.id) from ("
				+ findHcpQuery + " ) hcpUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findHcpQuery, sortOrder);
		
		List<HcpVO> hcpUsers = new ArrayList<>();

		Map<Long, HcpVO> hcpUsersMap = new HashMap<>();
		List<Object[]> results = query.getResultList();

		results.forEach(
				(record) -> {
					Long id = ((BigInteger) record[0]).longValue();
					String email = (String) record[1];
					String firstName = (String) record[2];
					String lastName = (String) record[3];
					Boolean isDeleted = (Boolean) record[4];
					Integer zipcode = (Integer) record[5];
					String address = (String) record[6];
					String city = (String) record[7];
					String credentials = (String) record[8];
					
					String faxNumber =  (String) record[9]; 
					String primaryPhone = (String) record[10];
					String mobilePhone = (String) record[11];
					String speciality = (String) record[12];
					String state = (String) record[13];
					String clinicId = (String) record[14];
					String clinicName = (String) record[15];
					Timestamp createdAt = (Timestamp) record[16];
					DateTime createdAtDatetime = new DateTime(createdAt);
					Boolean isActivated = (Boolean) record[17];
					String npiNumber = (String)record[18];
					HcpVO hcpVO = hcpUsersMap.get(id);

					Map<String, String> clinicMap = new HashMap<>();
					if (null != clinicId) {
						clinicMap.put("id", clinicId);
						clinicMap.put("name", clinicName);
					}
					if (hcpVO == null) {
						hcpVO = new HcpVO(id, firstName, lastName, email,
								isDeleted, zipcode, address, city, credentials,
								faxNumber, primaryPhone, mobilePhone,
								speciality, state,createdAtDatetime,isActivated,npiNumber);
						if (clinicMap.keySet().size() > 0) {
							hcpVO.getClinics().add(clinicMap);
						}
						hcpUsersMap.put(id, hcpVO);
					} else {
						hcpUsers.remove(hcpVO);
						if (clinicMap.keySet().size() > 0) {
							hcpVO.getClinics().add(clinicMap);
						}
					}
					hcpUsers.add(hcpVO);
				});
		int firstResult = pageable.getOffset();
		int maxResults = firstResult + pageable.getPageSize();
		List<HcpVO> hcpUsersSubList = new ArrayList<>();
		if(firstResult < hcpUsers.size()){
			maxResults = maxResults > hcpUsers.size() ? hcpUsers.size() : maxResults ;  
			hcpUsersSubList = hcpUsers.subList(firstResult,maxResults);
		}
		Page<HcpVO> page = new PageImpl<HcpVO>(hcpUsersSubList, null, count.intValue());

		return page;
	}

	public Page<PatientUserVO> findPatientBy(String queryString,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select user.id,user.email,user.first_name as firstName,user.last_name as lastName,"
				+ " user.is_deleted as isDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,user.title,user.hillrom_id,user.created_date as createdAt,user.activated as isActivated "
				+ " from USER user join USER_AUTHORITY user_authority on user_authority.user_id = user.id "
				+ " and user_authority.authority_name = 'PATIENT' and user.activated = 1"
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or  "
				+ " lower(user.email) like lower(:queryString) or "
				+ " lower(user.hillrom_id) like lower(:queryString)) "
				+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = 'SELF' "
				+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id ";

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);

		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);
		
		List<PatientUserVO> patientUsers = new LinkedList<>();

		List<Object[]> results = query.getResultList();

		results.stream().forEach(
				(record) -> {
					Long id = ((BigInteger) record[0]).longValue();
					String email = (String) record[1];
					String firstName = (String) record[2];
					String lastName = (String) record[3];
					Boolean isDeleted = (Boolean) record[4];
					Integer zipcode = (Integer) record[5];
					String address = (String) record[6];
					String city = (String) record[7];
					Date dob = (Date) record[8];
					String gender = (String) record[9];
					String title = (String) record[10];
					String hillromId = (String) record[11];
					Timestamp createdAt = (Timestamp) record[12];
					Boolean isActivated = (Boolean) record[13];
					DateTime createdAtDatetime = new DateTime(createdAt);
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}
					patientUsers.add(new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated));
				});

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}

	private Query getOrderedByQuery(String queryString,
			Map<String, Boolean> columnNames) {

		StringBuilder sb = new StringBuilder();
		// Append order by only if there is any sort request.
		if (columnNames.keySet().size() > 0) {
			sb.append(ORDER_BY_CLAUSE_START);
		}

		int limit = columnNames.size();
		int i = 0;
		for (String columnName : columnNames.keySet()) {
			sb.append("lower(").append(columnName).append(")");

			if (columnNames.get(columnName))
				sb.append(" ASC");
			else
				sb.append(" DESC");

			if (i != (limit - 1)) {
				sb.append(", ");
			}
		}
		Query jpaQuery = entityManager.createNativeQuery(queryString
				+ sb.toString());
		return jpaQuery;
	}

}
