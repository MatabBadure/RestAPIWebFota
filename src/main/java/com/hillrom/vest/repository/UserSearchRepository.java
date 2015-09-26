package com.hillrom.vest.repository;

import static com.hillrom.vest.security.AuthoritiesConstants.ACCT_SERVICES;
import static com.hillrom.vest.security.AuthoritiesConstants.ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.ASSOCIATES;
import static com.hillrom.vest.security.AuthoritiesConstants.HILLROM_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;
import static com.hillrom.vest.util.RelationshipLabelConstants.SELF;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.web.rest.dto.PatientUserVO;

@Repository
public class UserSearchRepository {

	private static final String ORDER_BY_CLAUSE_START = " order by ";
	@Inject
	private EntityManager entityManager;

	public Page<HillRomUserVO> findHillRomTeamUsersBy(String queryString,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findHillromTeamUserQuery = "select distinct(user.id),user.first_name as firstName,user.last_name as lastName,user.email,"
				+ " user_authority.authority_name as name,user.is_deleted as isDeleted,user.created_date as createdAt,user.activated as isActivated,user.hillrom_id as hillromId, userExt.mobile_phone as mobilePhone "
				+ " from  USER_EXTENSION userExt join USER user on user.id = userExt.user_id and "
				+ " (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or"
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or"
				+ " lower(user.email) like lower(:queryString) or lower(user.hillrom_id) like lower(:queryString)) "
				+ " join  USER_AUTHORITY user_authority on user_authority.user_id = user.id "
				+ " and  user_authority.authority_name in ('"+ADMIN+"','"+ACCT_SERVICES+"','"+ASSOCIATES+"','"+HILLROM_ADMIN+"')";

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
					String mobilePhone = (String) record[9];
					
					HillRomUserVO hrUserVO = new HillRomUserVO(id, firstName,
							lastName, email, role, isDeleted,createdAtDatetime,isActivated,hillromId, mobilePhone);
					hrUsersList.add(hrUserVO);
				});

		Page<HillRomUserVO> page = new PageImpl<HillRomUserVO>(hrUsersList,
				null, count.intValue());

		return page;
	}

	private void setPaginationParams(Pageable pageable, Query query) {
		
		int firstResult = pageable.getOffset();
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
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or"
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or"
				+ " lower(user.email) like lower(:queryString)) "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = '"+AuthoritiesConstants.HCP+"'"
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
//Patient Search
	public Page<PatientUserVO> findPatientBy(String queryString, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {
				
		String findPatientUserQuery = "select user.id,user.email,user.first_name as firstName,user.last_name as lastName,"
				+ " user.is_deleted as isDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,user.title,user.hillrom_id,user.created_date as createdAt,user.activated as isActivated, patInfo.state as state "
				+ " ,clinic.id as clinic_id, clinic.name as clinicName,pc.compliance_score adherence, pc.last_therapy_session_date as last_date from USER user join USER_AUTHORITY user_authority on user_authority.user_id = user.id "
				+ " and user_authority.authority_name = '"+PATIENT+"'"
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or  "
				+ " lower(user.email) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or"
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or"
				+ " lower(user.hillrom_id) like lower(:queryString)) "
				+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"+SELF+"'"
				+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id  left outer join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id"
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate() "
				+" left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id ";
		
		
		
		
		StringBuilder filterQuery = new StringBuilder();
		
		if(StringUtils.isNotEmpty(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			
			
			if(Objects.nonNull(filterMap.get("isDeleted"))){
				filterQuery.append("select * from (");
				filterQuery.append(findPatientUserQuery);
				
				if("1".equals(filterMap.get("isDeleted")))
					filterQuery.append(") as search_table where isDeleted in (1)");
				else if("0".equals(filterMap.get("isDeleted")))
					filterQuery.append(")  as search_table where isDeleted in (0)");
				else
					filterQuery.append(") as search_table where isDeleted in (0,1)");
			}
			findPatientUserQuery = filterQuery.toString();
		}
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
					queryString);
		System.out.println(findPatientUserQuery);
		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		//setPaginationParams(pageable, query);
		
		List<PatientUserVO> patientUsers = new LinkedList<>();

		List<Object[]> results = query.getResultList();
		Map<Long, PatientUserVO> patientUsersMap = new HashMap<>();

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
					String state = (String) record[14];
					String clinicId = (String) record[15];
					String clinicName = (String) record[16];
					Integer adherence = (Integer) record[17];
					Date lastTransmissionDate = (Date) record[18];
					
					java.util.Date localLastTransmissionDate = null;
					
					if(Objects.nonNull(lastTransmissionDate)){
						localLastTransmissionDate =lastTransmissionDate;
						
					}
					
					
					PatientUserVO patientUserVO = patientUsersMap.get(id);
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					
					Map<String, String> clinicMap = new HashMap<>();
					if (null != clinicId) {
						clinicMap.put("id", clinicId);
						clinicMap.put("name", clinicName);
					}
					if (patientUserVO == null) {
						patientUserVO = new PatientUserVO(id, email, firstName,
								lastName, isDeleted, zipcode, address, city, dobLocalDate,
								gender, title, hillromId,createdAtDatetime,isActivated,state,
								Objects.nonNull(adherence) ? adherence : 0,localLastTransmissionDate);
						if (clinicMap.keySet().size() > 0) {
							patientUserVO.getClinics().add(clinicMap);
						}
						patientUsersMap.put(id, patientUserVO);
					} else {
						patientUsers.remove(patientUserVO);
						if (clinicMap.keySet().size() > 0) {
							patientUserVO.getClinics().add(clinicMap);
						}
					}
					patientUsers.add(patientUserVO);
					}
				});
		int firstResult = pageable.getOffset();
		int maxResults = firstResult + pageable.getPageSize();
		List<PatientUserVO> patientUserSubList = new ArrayList<>();
		if(firstResult < patientUsers.size()){
			maxResults = maxResults > patientUsers.size() ? patientUsers.size() : maxResults ;  
			patientUserSubList = patientUsers.subList(firstResult,maxResults);
		}

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUserSubList, null, count.intValue());

		return page;
	}
	
	public Page<PatientUserVO> findAssociatedPatientToHCPBy(String queryString, Long hcpUserID, String clinicId,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select user.id,user.email,user.first_name as firstName,user.last_name as"
				+ " lastName, user.is_deleted as isDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
				+ "user.title,user.hillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state as state from "
				+ "USER user join USER_AUTHORITY user_authority on user_authority.user_id"
				+ " = user.id and user_authority.authority_name = '"+PATIENT+"'and "
				+ "(lower(user.first_name) like lower(:queryString) or "
				+ "lower(user.last_name) like lower(:queryString) or "
				+ "lower(user.email) like lower(:queryString) or "
				+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ "lower(user.hillrom_id) like lower(:queryString)) "
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"+SELF+"' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id"
				+ ":clinicSearch"
				+ " where upa_hcp.user_id = :hcpUserID ";

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID",
				hcpUserID.toString());
		
		if(!StringUtils.isEmpty(clinicId))
			findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicSearch",
					" join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = '"+clinicId+"'" );
		else
			findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicSearch","");
			
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
					String state = (String) record[14];
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}
					patientUsers.add(new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated,state));
				});

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}
	
	public Page<PatientUserVO> findAssociatedPatientsToClinicBy(String queryString, String clinicID,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select user.id,user.email,user.first_name as"
				+ " firstName,user.last_name as lastName, user.is_deleted as isDeleted,"
				+ "user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
				+ "user.title,user.hillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state , compliance_score, pc.last_therapy_session_date as last_date from USER user"
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id  "
				+ "and user_authority.authority_name = '"+PATIENT+"' and "
				+ "(lower(user.first_name) like lower(:queryString) or  "
				+ "lower(user.last_name) like lower(:queryString) or  "
				+ "lower(user.email) like lower(:queryString) or "
				+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ "lower(user.hillrom_id) like lower(:queryString)) "
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"+SELF+"' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ "join CLINIC_PATIENT_ASSOC patient_clinic on "
				+ "patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = ':clinicId'"
				+ "left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate()";       

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicId",
				clinicID);
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
					String state = (String) record[14];
					Integer adherence = (Integer)record[15];
					Date lastTransmissionDate = (Date) record[16];
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}
					java.util.Date localLastTransmissionDate = null;
					
					if(Objects.nonNull(lastTransmissionDate)){
						localLastTransmissionDate =lastTransmissionDate;
						
					}
					patientUsers.add(new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated,state,
							Objects.nonNull(adherence) ? adherence : 0,localLastTransmissionDate));
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
	
	private Map<String,String> getSearchParams(String filterString){
		
		Map<String,String> filterMap = new HashMap<>();
		
		String[] filters = filterString.split(";");
		for(String filter : filters){
			
			String[] pair = filter.split(":");
			if(pair.length>1)
			if(!StringUtils.isEmpty(pair[1]))
				filterMap.put(pair[0],pair[1]);
		}
		return filterMap;
		
	}
}
