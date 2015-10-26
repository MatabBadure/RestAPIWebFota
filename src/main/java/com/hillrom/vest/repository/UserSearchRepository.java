package com.hillrom.vest.repository;

import static com.hillrom.vest.security.AuthoritiesConstants.ACCT_SERVICES;
import static com.hillrom.vest.security.AuthoritiesConstants.ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.ASSOCIATES;
import static com.hillrom.vest.security.AuthoritiesConstants.CLINIC_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.HCP;
import static com.hillrom.vest.security.AuthoritiesConstants.HILLROM_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;
import static com.hillrom.vest.util.RelationshipLabelConstants.SELF;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.QClinic;
import com.hillrom.vest.domain.QClinicPatientAssoc;
import com.hillrom.vest.domain.QPatientCompliance;
import com.hillrom.vest.domain.QPatientInfo;
import com.hillrom.vest.domain.QPatientNoEvent;
import com.hillrom.vest.domain.QUser;
import com.hillrom.vest.domain.QUserAuthority;
import com.hillrom.vest.domain.QUserPatientAssoc;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.web.rest.dto.PatientUserVO;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.QueryFlag.Position;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.JPQLTemplates;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAQueryFactory;
import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLSubQuery;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.Ops;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.PredicateOperation;
import com.mysema.query.types.expr.CaseBuilder;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.StringExpression;
import com.mysema.query.types.expr.StringExpressions;
import com.mysql.jdbc.Connection;

@Repository
public class UserSearchRepository {

	private static final String ORDER_BY_CLAUSE_START = " order by ";
	@Inject
	private EntityManager entityManager;

	public Page<HillRomUserVO> findHillRomTeamUsersBy(String queryString,String filter,
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
		
//		QUser user = QUser.user;
//		QAuthority authority = QAuthority.authority;
//		QUserExtension userExtension = QUserExtension.userExtension;
//		
//		JPAQuery query = new JPAQuery(entityManager);
		//List<String> tupleList = query.from(user).join(authority).on(user.id.eq(authority.)).where(user.firstName.eq("Pradeep")).list(user.firstName);
		

		StringBuilder filterQuery = new StringBuilder();
		
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){
		
			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			applyIsDeletedFilter(findHillromTeamUserQuery, filterQuery, filterMap);
			findHillromTeamUserQuery = filterQuery.toString();
		}
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

	public Page<HcpVO> findHCPBy(String queryString, String filter, Pageable pageable,
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

		StringBuilder filterQuery = new StringBuilder();
	
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){
		
			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			
			applyIsDeletedFilter(findHcpQuery, filterQuery, filterMap);
			
			findHcpQuery = filterQuery.toString();
		}
			
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
		
		String query1 ="select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,phillrom_id,createdAt,isActivated, state , adherence,last_date,mrnid,hName,clinicName,isHMRNonCompliant,isSettingsDeviated,isMissedTherapy  from (select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,user.last_name as plastName, user.is_deleted as isDeleted, user.zipcode as pzipcode,patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,user.gender as pgender,user.title as ptitle,  user.hillrom_id as phillrom_id,user.created_date as createdAt,user.activated as isActivated, patInfo.state as state ,  user_clinic.mrn_id as mrnid, clinic.id as pclinicid, GROUP_CONCAT(clinic.name) as clinicName,pc.compliance_score as adherence,  pc.last_therapy_session_date as last_date,pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,"
				+ " pc.missed_therapy_count as isMissedTherapy from USER user join USER_PATIENT_ASSOC  upa on user.id = upa.user_id "
				+ " and upa.relation_label = '"+SELF+"' join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = '"+PATIENT+"' "
				+ " and (lower(user.first_name) like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or "
				+ " lower(user.email) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or ";
		
		String hrIdSearch = " lower(user.hillrom_id) like lower(:queryString) or ";
		
		String query2 =	 " ((lower(IFNULL(patInfo.city,'')) like lower(:queryString)) or "
						+" (lower(IFNULL(patInfo.state,'')) like lower(:queryString))) )";

		// This is applicable only when search is performed by HCP or CLINIC_ADMIN
		String mrnIdSearch = " or (lower(IFNULL(user_clinic.mrn_id,0)) like lower(:queryString) ) ) ";
		
		String query3 = " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate() "
				+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id "
				+ " group by user.id) as associated_patient left outer join (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName, "
				+ " clinic.id as hclinicid from USER huser join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id "
				+ " and user_authorityh.authority_name = '"+HCP+"' "
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
				+ " left outer join PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=curdate() "
				+ " group by clinic.id) as associated_hcp  on associated_patient.pclinicid = associated_hcp.hclinicid ";
		
		String findPatientUserQuery = query1;
		// HCP , CLINIC_ADMIN can search on MRNID not HRID
		if(SecurityUtils.isUserInRole(HCP) || SecurityUtils.isUserInRole(CLINIC_ADMIN))
			findPatientUserQuery = findPatientUserQuery.concat(query2).substring(0, findPatientUserQuery.lastIndexOf(")")).concat(mrnIdSearch);
		else // Admin can search on HRID not MRNID
			findPatientUserQuery += hrIdSearch + query2;
		findPatientUserQuery += query3;
		
		findPatientUserQuery = applyFiltersToQuery(filter,
				findPatientUserQuery);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
					queryString);
		
		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);
		
		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();
		
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
					Integer adherence = (Integer) record[15];
					Date lastTransmissionDate = (Date) record[16];
					String mrnId = (String) record[17];
					String hcpNamesCSV = (String) record[18];
					String clinicNamesCSV = (String) record[19];
					
					java.util.Date localLastTransmissionDate = null;
					
					if(Objects.nonNull(lastTransmissionDate)){
						localLastTransmissionDate =lastTransmissionDate;
						
					}
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}

					PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated,state,
							Objects.nonNull(adherence) ? adherence : 0,localLastTransmissionDate);
					//mrnId,hcpNamesCSV,clinicNamesCSV
					patientUserVO.setMrnId(mrnId);
					patientUserVO.setHcpNamesCSV(hcpNamesCSV);
					patientUserVO.setClinicNamesCSV(clinicNamesCSV);
					patientUsers.add(patientUserVO);
				});
		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}

	private List<PatientUserVO> extractPatientSearchResultsToVO(List<Object[]> results) {
		List<PatientUserVO> patientUsers = new LinkedList<>();
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
					Integer adherence = (Integer) record[15];
					Date lastTransmissionDate = (Date) record[16];
					String mrnId = (String) record[17];
					String hcpNamesCSV = (String) record[18];
					String clinicNamesCSV = (String) record[19];
					
					java.util.Date localLastTransmissionDate = null;
					
					if(Objects.nonNull(lastTransmissionDate)){
						localLastTransmissionDate =lastTransmissionDate;
						
					}
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}

					PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated,state,
							Objects.nonNull(adherence) ? adherence : 0,localLastTransmissionDate);
					//mrnId,hcpNamesCSV,clinicNamesCSV
					patientUserVO.setMrnId(mrnId);
					patientUserVO.setHcpNamesCSV(hcpNamesCSV);
					patientUserVO.setClinicNamesCSV(clinicNamesCSV);
					patientUsers.add(patientUserVO);
				});
		return patientUsers;
	}

	private String applyFiltersToQuery(String filter,String query) {
		
		StringBuilder filterQuery = new StringBuilder();
		
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");

			applyQueryFilters(query, filterQuery, filterMap);
			query = filterQuery.toString();
		}
		return query;
	}

	
/*	public Page<PatientUserVO> findAssociatedPatientToHCPBy(String queryString, Long hcpUserID, String clinicId, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,phillrom_id,createdAt,isActivated, state  , pcompliance_score,last_date,mrnid,hlastName,clinicName,hcp_id,pclinicid,isSettingsDeviated,isHMRNonCompliant,isMissedTherapy from "
				+ " (select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,user.last_name as plastName, user.is_deleted as isDeleted,user.zipcode as pzipcode,patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,user.gender as pgender,user.title as ptitle,user.hillrom_id as phillrom_id,user.created_date as createdAt,user.activated as isActivated, patInfo.state as state  , user_clinic.mrn_id as mrnid, clinic.id as pclinicid, clinic.name as clinicName,pc.compliance_score as pcompliance_score, pc.last_therapy_session_date as last_date, "
				+ " pc.is_settings_deviated as isSettingsDeviated ,pc.is_hmr_compliant as isHMRNonCompliant,pc.missed_therapy_count as isMissedTherapy "
				+ " from USER user left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = '"+PATIENT+"' "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or   "
				+ " lower(user.email) like lower(:queryString) or  "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ " lower(user.hillrom_id) like lower(:queryString) )  join USER_PATIENT_ASSOC  upa on user.id = upa.user_id "
				+ " and upa.relation_label = '"+SELF+"' join PATIENT_INFO patInfo on upa.patient_id = patInfo.id  and "
				+ " ((lower(IFNULL(patInfo.city,'')) like lower(:queryString)) or (lower(IFNULL(patInfo.state,'')) like lower(:queryString))) "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id and "
				+ " lower(IFNULL(user_clinic.mrn_id,0)) like lower(:queryString) "
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate() "
				+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id "
				+ " ) as associated_patient "
				+ " left outer join (select  huser.id as hcp_id, concat( huser.last_name,' ',huser.first_name ) as hlastName "
				+ " , clinic.id as hclinicid from USER huser "
				+ " join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id and user_authorityh.authority_name = '"+HCP+"' "
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
				+ " left outer join PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=curdate()"
				+ " ) as associated_hcp on associated_patient.pclinicid = associated_hcp.hclinicid "
				+ " where lower(IFNULL(hcp_id,0))= :hcpUserID ";
		
		StringBuilder filterQuery = new StringBuilder();
	
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			
			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID",
				hcpUserID.toString());
		
		if(!StringUtils.isEmpty(clinicId)){
			findPatientUserQuery = findPatientUserQuery.concat(" and pclinicid = '"+clinicId+"'");
		}
		
		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);
		
		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = extractPatientSearchResultsToVO(results);

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}
	*/
	
	public Page<PatientUserVO> findAssociatedPatientToHCPBy(String queryString, Long hcpUserID, String clinicId, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName, "
				+ " user.is_deleted as isDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,"
				+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt,"
				+ " user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
				+ " pc.last_therapy_session_date as last_date,"
				+ " (select  GROUP_CONCAT(clinicc.name)"
				+ " from USER userc "
				+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  "
				+ "and user_authorityc.authority_name = 'PATIENT' "
				+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = 'Self' "
				+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id  "
				+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
				+ " where upac.user_id = user.id "
				+ " group by patInfoc.id) as clinicname, (select  GROUP_CONCAT(userh.first_name, userh.last_name) "
				+ " from USER userh "
				+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id  and user_authorityh.authority_name = 'HCP' " 
				+ " join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id and upah.relation_label = 'HCP' "
				+ " left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
				+ " where patInfo.id = patInfoh.id"
				+ " group by patInfoh.id) as hcpname, patient_clinic.mrn_id as mrnid, pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated, pc.missed_therapy_count as isMissedTherapy"
				+ " from USER user"
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id"
				+ " and user_authority.authority_name = '"+PATIENT+"'and (lower(user.first_name) "
				+ " like lower(:queryString) or lower(user.last_name) like lower(:queryString) "
				+ " or lower(user.email) like lower(:queryString) or lower(CONCAT(user.first_name,' ',user.last_name)) "
				+ " like lower(:queryString) or lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) "
				+ " or lower(user.hillrom_id) like lower(:queryString))"
				+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"+SELF+"'  "
				+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id" 
				+" join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id  "
				+" left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate()  "
				+" join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id and lower(IFNULL(patient_clinic.mrn_id,0)) like lower('%%') ";
		QUser user = QUser.user;
		QUser userP = new QUser("userP");
		QUser userH = new QUser("userH");
		QUserAuthority userAuthority= QUserAuthority.userAuthority;
		QUserAuthority userAuthorityP= new QUserAuthority("userAuthorityP");
		QUserAuthority userAuthorityH= new QUserAuthority("userAuthorityH");
		QUserPatientAssoc userPatientAssoc = QUserPatientAssoc.userPatientAssoc;
		QUserPatientAssoc userPatientAssocP = new QUserPatientAssoc("userPatientAssocP");
		QUserPatientAssoc userPatientAssocH = new QUserPatientAssoc("userPatientAssocH");
		QUserPatientAssoc userPatientAssocHCP = new QUserPatientAssoc("userPatientAssocHCP");
		QPatientInfo patientInfo = QPatientInfo.patientInfo;
		QPatientInfo patientInfoP = new QPatientInfo("patientInfoP");
		QPatientInfo patientInfoH = new QPatientInfo("patientInfoH");
		QClinicPatientAssoc clinicPatientAssoc = QClinicPatientAssoc.clinicPatientAssoc;
		QClinicPatientAssoc clinicPatientAssocP = new QClinicPatientAssoc("clinicPatientAssocP");
		QClinic clinic = QClinic.clinic;
		QPatientCompliance patientCompliance = QPatientCompliance.patientCompliance;
		java.util.Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		SQLTemplates dialect = new MySQLTemplates(); // SQL-dialect
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.sql.Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hillromvest_copy_of_dev","root", "root");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Configuration configuration = new Configuration(dialect);
		
		//JPASubQuery clinicNameSubQuery = new JPASubQuery().from(userP)
		SQLSubQuery clinicNameSubQuery =new SQLSubQuery().from(userP)
		.leftJoin(userAuthorityP).on(userAuthorityP.userId.eq(userP.id).and(userAuthorityP.authorityName.eq("PATIENT")))
		.join(userPatientAssocP).on(userP.id.eq(userPatientAssocP.userId).and(userPatientAssocP.relationLabel.eq("Self")))
		.leftJoin(patientInfoP).on(userPatientAssocP.patientId.eq(patientInfoP.id))
		.leftJoin(clinicPatientAssocP).on(clinicPatientAssocP.patientId.eq(patientInfoP.id))
		.leftJoin(clinic).on(clinicPatientAssocP.clinicId.eq(clinic.id).and(clinicPatientAssocP.patientId.eq(patientInfoP.id)))
		.where(userP.id.eq(userPatientAssocP.userId))
		.groupBy(patientInfoP.id);
		
		
		//JPASubQuery userNameSubQuery = new JPASubQuery().from(userH)
				SQLSubQuery userNameSubQuery = new SQLSubQuery().from(userH)
		.leftJoin(userAuthorityH).on(userAuthorityH.userId.eq(userH.id).and(userAuthorityH.authorityName.eq("HCP")))
		.join(userPatientAssocH).on(userH.id.eq(userPatientAssocH.userId).and(userPatientAssocH.relationLabel.eq("HCP")))
		.leftJoin(patientInfoH).on(userPatientAssocH.patientId.eq(patientInfoH.id))
		.where(patientInfo.id.eq(patientInfoH.id))
		.groupBy(patientInfoH.id);
		String queryString1 = queryString.replaceAll("'", "");
		//JPAQuery findPatientUserQuery1 = new JPAQuery(entityManager).from(user)
		SQLQuery findPatientUserQuery1 = new SQLQuery(conn, configuration).from(user).join(userAuthority)
		.on(
			userAuthority.userId.eq(user.id)
			.and(userAuthority.authorityName.eq(PATIENT))
			.or(user.firstName.lower().like(queryString1.toLowerCase())
					.or(user.lastName.lower().like(queryString1.toLowerCase()))
					.or(user.email.lower().like(queryString1.toLowerCase()))
					.or(user.firstName.lower().concat(" ").concat(user.lastName.lower()).like(queryString1.toLowerCase()))
					.or(user.lastName.lower().concat(" ").concat(user.firstName.lower()).like(queryString1.toLowerCase()))
					.or(user.hillromId.lower().like(queryString1.toLowerCase()))
				)
		)
		.join(userPatientAssoc).on(user.id.eq(userPatientAssoc.userId)
				.and(userPatientAssoc.relationLabel.eq(SELF)))
		.join(patientInfo).on(userPatientAssoc.patientId.eq(patientInfo.id))
		.join(userPatientAssocHCP).on(patientInfo.id.eq(userPatientAssocHCP.patientId))
		.leftJoin(patientCompliance).on(user.id.eq(patientCompliance.userId)
				.and(patientCompliance.date.eq(DateExpression.currentDate())))
		.join(clinicPatientAssoc).on(clinicPatientAssoc.patientId.eq(patientInfo.id)
				.and(new CaseBuilder().when(clinicPatientAssoc.mrnId.isNotNull()).then(clinicPatientAssoc.mrnId.toLowerCase())
						.otherwise("0").like("%%")));
		
		BooleanBuilder whereClause = new BooleanBuilder();
		
				String query2 = " where upa_hcp.user_id = :hcpUserID ";
				String query3 =	" group by user.id ";
				if(!StringUtils.isEmpty(clinicId)){
					findPatientUserQuery = findPatientUserQuery + query2 + 
							" or patient_clinic.clinic_id  = '"+clinicId+"' "+
							query3;
					whereClause.and(userPatientAssocHCP.userId.eq(hcpUserID).or(clinicPatientAssoc.clinicId.eq(clinicId)));
				}
				else {
					findPatientUserQuery=findPatientUserQuery+query2+query3;
					whereClause.and(userPatientAssocHCP.userId.eq(hcpUserID));
				}

		StringBuilder filterQuery = new StringBuilder();
		String filterQuery1 = new String();
		BooleanBuilder whereClauseForOuterTable = new BooleanBuilder();
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			
			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);
			
			filterQuery1 = filterQuery.substring(filterQuery.lastIndexOf(")  as search_table")-1, filterQuery.length());

			findPatientUserQuery = filterQuery.toString();
			
			applyQueryFilter1(whereClauseForOuterTable, filterMap, user, patientCompliance);
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID",
				hcpUserID.toString());
		
		findPatientUserQuery1.groupBy(user.id);//.where(whereClause)
		
		OrderSpecifier<Long> orderById = new OrderSpecifier<Long>(null, null, null);
		OrderSpecifier<String> orderByEmail = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByFirstName = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByLastName = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<Boolean> orderByIsDeleted = new OrderSpecifier<Boolean>(null, null, null);
		OrderSpecifier<Integer> orderByZipcode = new OrderSpecifier<Integer>(null, null, null);
		OrderSpecifier<String> orderByAddress = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByCity = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<Date> orderByDOB = new OrderSpecifier<Date>(null, null, null);
		OrderSpecifier<String> orderByGender = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByTitle = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByHillromId = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<Timestamp> orderByCreatedAt = new OrderSpecifier<Timestamp>(null, null, null);
		OrderSpecifier<Boolean> orderByIsActivated = new OrderSpecifier<Boolean>(null, null, null);
		OrderSpecifier<String> orderByState = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<Integer> orderByComplianceScore = new OrderSpecifier<Integer>(null, null, null);
		OrderSpecifier<Date> orderByLastDate = new OrderSpecifier<Date>(null, null, null);
		OrderSpecifier<String> orderByClinicName = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByHCPName = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<String> orderByMRNId = new OrderSpecifier<String>(null, null, null);
		OrderSpecifier<Boolean> orderByIsHMRNonCompliant = new OrderSpecifier<Boolean>(null, null, null);
		OrderSpecifier<Boolean> orderByIsSettingDeviated = new OrderSpecifier<Boolean>(null, null, null);
		OrderSpecifier<Integer> orderByMissedTherapyCount = new OrderSpecifier<Integer>(null, null, null);
		
		
		for (String columnName : sortOrder.keySet()) {
			
			switch (columnName) {
			case "id":
				if (sortOrder.get(columnName))
					orderById=user.id.asc();
				else
					orderById=user.id.desc();
				
				break;
				
			case "email":
				if (sortOrder.get(columnName))
					orderByEmail=user.email.asc();
				else
					orderByEmail=user.email.desc();
				
				break;

			case "firstName":
				if (sortOrder.get(columnName))
					orderByFirstName=user.firstName.asc();
				else
					orderByFirstName=user.firstName.desc();
				
				break;
				
			case "lastName":
				if (sortOrder.get(columnName))
					orderByLastName=user.lastName.asc();
				else
					orderByLastName=user.lastName.desc();
				
				break;

			case "isDeleted":
				if (sortOrder.get(columnName))
					orderByIsDeleted=user.isDeleted.asc();
				else
					orderByIsDeleted=user.isDeleted.desc();
				
				break;

			case "zipcode":
				if (sortOrder.get(columnName))
					orderByZipcode=user.zipcode.asc();
				else
					orderByZipcode=user.zipcode.desc();
				
				break;

			case "address":
				if (sortOrder.get(columnName))
					orderByAddress=patientInfo.address.asc();
				else
					orderByAddress=patientInfo.address.desc();
				
				break;

			case "city":
				if (sortOrder.get(columnName))
					orderByCity=patientInfo.city.asc();
				else
					orderByCity=patientInfo.city.desc();
				
				break;

			case "dob":
				if (sortOrder.get(columnName))
					orderByDOB=user.dob.asc();
				else
					orderByDOB=user.dob.desc();
				
				break;

			case "gender":
				if (sortOrder.get(columnName))
					orderByGender=user.gender.asc();
				else
					orderByGender=user.gender.desc();
				
				break;

			case "title":
				if (sortOrder.get(columnName))
					orderByTitle=user.title.asc();
				else
					orderByTitle=user.title.desc();
				
				break;

			case "hillromId":
				if (sortOrder.get(columnName))
					orderByHillromId=user.hillromId.asc();
				else
					orderByHillromId=user.hillromId.desc();
				
				break;

			case "createdAt":
				if (sortOrder.get(columnName))
					orderByCreatedAt=user.createdDate.asc();
				else
					orderByCreatedAt=user.createdDate.desc();
				
				break;

			case "iaActivated":
				if (sortOrder.get(columnName))
					orderByIsActivated=user.activated.asc();
				else
					orderByIsActivated=user.activated.desc();
				
				break;

			case "state":
				if (sortOrder.get(columnName))
					orderByState=patientInfo.state.asc();
				else
					orderByState=patientInfo.state.desc();
				
				break;

			case "adherence":
				if (sortOrder.get(columnName))
					orderByComplianceScore=patientCompliance.complianceScore.asc();
				else
					orderByComplianceScore=patientCompliance.complianceScore.desc();
				
				break;

			case "last_date":
				if (sortOrder.get(columnName))
					orderByLastDate=patientCompliance.lastTherapySessionDate.asc();
				else
					orderByLastDate=patientCompliance.lastTherapySessionDate.desc();
				
				break;

			case "clinicname":
				if (sortOrder.get(columnName))
					orderByClinicName=clinic.name.asc();
				else
					orderByClinicName=clinic.name.desc();
				
				break;

			case "hcpname":
				if (sortOrder.get(columnName))
					orderByHCPName=userH.firstName.append(userH.lastName).asc();
				else
					orderByHCPName=userH.firstName.append(userH.lastName).desc();
				
				break;

			case "mrnid":
				if (sortOrder.get(columnName))
					orderByMRNId=clinicPatientAssoc.mrnId.asc();
				else
					orderByMRNId=clinicPatientAssoc.mrnId.desc();
				
				break;

			case "isHMRNonCompliant":
				if (sortOrder.get(columnName))
					orderByIsHMRNonCompliant=patientCompliance.isHmrCompliant.asc();
				else
					orderByIsHMRNonCompliant=patientCompliance.isHmrCompliant.desc();
				
				break;

			case "isSettingsDeviated":
				if (sortOrder.get(columnName))
					orderByIsSettingDeviated=patientCompliance.isSettingsDeviated.asc();
				else
					orderByIsSettingDeviated=patientCompliance.isSettingsDeviated.desc();
				
				break;

			case "isMissedTherapy":
				if (sortOrder.get(columnName))
					orderByMissedTherapyCount=patientCompliance.missedTherapyCount.asc();
				else
					orderByMissedTherapyCount=patientCompliance.missedTherapyCount.desc();
				
				break;

			default:
				break;
			}
		}
		
//		findPatientUserQuery1.orderBy(orderById, orderByEmail, orderByFirstName, orderByLastName, orderByIsDeleted, orderByZipcode, orderByAddress, orderByCity, orderByDOB, 
//				orderByGender, orderByTitle, orderByHillromId, orderByCreatedAt, orderByIsActivated, orderByState, orderByComplianceScore, orderByLastDate, 
//				orderByClinicName, orderByHCPName, orderByMRNId, orderByIsHMRNonCompliant, orderByIsSettingDeviated, orderByMissedTherapyCount);
//		
		List<Tuple> tupleList = findPatientUserQuery1.addFlag(Position.START, filterQuery1.length()>0?"select * from (":"").addFlag(Position.AFTER_GROUP_BY, filterQuery1).list(user.id, user.email, user.firstName.as("firstName"), user.lastName.as("lastName"), user.isDeleted.as("isDeleted"), 
				user.zipcode, patientInfo.address, patientInfo.city, user.dob, user.gender, user.title, user.hillromId, user.createdDate.as("createdAt"), 
				user.activated.as("isActivated"), patientInfo.state.as("state"), patientCompliance.complianceScore.as("adherence"), 
				patientCompliance.lastTherapySessionDate.as("last_date"), /*clinicNameSubQuery.addFlag(Position.START, "select GROUP_CONCAT(").addFlag(Position.END," as clinicname").list(clinic.name.as("clinicname")), userNameSubQuery.addFlag(Position.START, " GROUP_CONCAT(hcpname) as hc").list(userH.firstName.append(userH.lastName).as("hcpname")),*/ clinicPatientAssoc.mrnId.as("mrnid"), 
				patientCompliance.isHmrCompliant.as("isHMRNonCompliant"), patientCompliance.isSettingsDeviated.as("isSettingsDeviated"), 
				patientCompliance.missedTherapyCount.as("isMissedTherapy"))
		;
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		
		setPaginationParams(pageable, query);
		
		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();
		
		tupleList.stream().forEach(
				(record) -> {
					Long id = record.get(user.id);
					String email = record.get(user.email);
					String firstName = record.get(user.firstName);
					String lastName = record.get(user.lastName);
					Boolean isDeleted = record.get(user.isDeleted);
					Integer zipcode = record.get(user.zipcode);;
					String address = record.get(patientInfo.address);
					String city = record.get(patientInfo.city);
					Date dob = record.get(user.dob);
					String gender = record.get(user.gender);
					String title = record.get(user.title);
					String hillromId = record.get(user.hillromId);
					Timestamp createdAt = record.get(user.createdDate);
					Boolean isActivated = record.get(user.activated);
					DateTime createdAtDatetime = new DateTime(createdAt);
					String state = record.get(patientInfo.state);
					Integer adherence = record.get(patientCompliance.complianceScore);
					Date lastTransmissionDate = record.get(patientCompliance.lastTherapySessionDate);
					String clinicNamesCSV = "";//record.get("clinicname");
					String hcpNamesCSV = "";//(String) record[18];
					String mrnId = "";//(String) record[19];
					
					java.util.Date localLastTransmissionDate = null;
					
					if(Objects.nonNull(lastTransmissionDate)){
						localLastTransmissionDate =lastTransmissionDate;
						
					}
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}

					PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated,state,
							Objects.nonNull(adherence) ? adherence : 0,localLastTransmissionDate);
					//mrnId,hcpNamesCSV,clinicNamesCSV
					patientUserVO.setMrnId(mrnId);
					patientUserVO.setHcpNamesCSV(hcpNamesCSV);
					patientUserVO.setClinicNamesCSV(clinicNamesCSV);
					patientUsers.add(patientUserVO);
				}); 

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
					Integer adherence = (Integer) record[15];
					Date lastTransmissionDate = (Date) record[16];
					String clinicNamesCSV = (String) record[17];
					String hcpNamesCSV = (String) record[18];
					String mrnId = (String) record[19];
					
					java.util.Date localLastTransmissionDate = null;
					
					if(Objects.nonNull(lastTransmissionDate)){
						localLastTransmissionDate =lastTransmissionDate;
						
					}
					
					java.util.Date dobLocalDate = null;
					if(null !=dob){
						dobLocalDate = new java.util.Date(dob.getTime());
					}

					PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName,
							lastName, isDeleted, zipcode, address, city, dobLocalDate,
							gender, title, hillromId,createdAtDatetime,isActivated,state,
							Objects.nonNull(adherence) ? adherence : 0,localLastTransmissionDate);
					//mrnId,hcpNamesCSV,clinicNamesCSV
					patientUserVO.setMrnId(mrnId);
					patientUserVO.setHcpNamesCSV(hcpNamesCSV);
					patientUserVO.setClinicNamesCSV(clinicNamesCSV);
					patientUsers.add(patientUserVO);
				});

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}
	
	public Page<PatientUserVO> findAssociatedPatientsToClinicBy(String queryString, String clinicID, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select user.id,user.email,user.first_name as"
				+ " firstName,user.last_name as lastName, user.is_deleted as isDeleted,"
				+ "user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
				+ "user.title,user.hillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state , compliance_score, pc.last_therapy_session_date as last_date, "
				+ "pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,pc.missed_therapy_count as isMissedTherapy "
				+ "from USER user"
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
		
		StringBuilder filterQuery = new StringBuilder();
		
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			
			
			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);
			
			findPatientUserQuery = filterQuery.toString();
		}


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
		
		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = extractPatientResultsToVO(results);

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}

	private List<PatientUserVO> extractPatientResultsToVO(List<Object[]> results) {
		List<PatientUserVO> patientUsers = new LinkedList<>();
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
		return patientUsers;
	}
	
	private void applyQueryFilters(String query, StringBuilder filterQuery, Map<String, String> filterMap) {
		applyIsDeletedFilter(query, filterQuery, filterMap);
		
		applyIsHMRNonCompliantFilter(filterQuery, filterMap);
		
		applyIsSettingsDeviatedFilter(filterQuery, filterMap);
		
		applyIsMissedTherapyFilter(filterQuery, filterMap);
		
		applyIsNoEventFilter(filterQuery, filterMap);
	}
	
	private void applyQueryFilter1(BooleanBuilder whereClause, Map<String, String> filterMap, QUser qUser, QPatientCompliance qPatientCompliance) {
		applyIsDeletedFilter(whereClause, filterMap, qUser);
		
		applyIsHMRNonCompliantFilter(whereClause, filterMap, qPatientCompliance);
		
		applyIsSettingsDeviatedFilter(whereClause, filterMap, qPatientCompliance);
		
		applyIsMissedTherapyFilter(whereClause, filterMap, qPatientCompliance);
		
		applyIsNoEventFilter(whereClause, filterMap, qUser);
	}

	private void applyIsNoEventFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if(Objects.nonNull(filterMap.get("isNoEvent")) && "1".equals(filterMap.get("isNoEvent"))){
			
			filterQuery.append("and exists (SELECT PATIENT_NO_EVENT.id FROM PATIENT_NO_EVENT "
					+ "WHERE PATIENT_NO_EVENT.user_id = search_table.id AND "
					+ "PATIENT_NO_EVENT.first_transmission_date is null LIMIT 1)");
		}
	}
	
	private void applyIsNoEventFilter(BooleanBuilder whereClause, Map<String, String> filterMap, QUser qUser) {
		if(Objects.nonNull(filterMap.get("isNoEvent")) && "1".equals(filterMap.get("isNoEvent"))){
					QPatientNoEvent qPatientNoEvent = QPatientNoEvent.patientNoEvent;
					JPASubQuery subquery = new JPASubQuery().from(qPatientNoEvent)
							.where(qPatientNoEvent.userId.eq(qUser.id).and(qPatientNoEvent.firstTransmissionDate.isNull())).limit(1);
					whereClause.and(subquery.exists());
		}
	}

	private void applyIsMissedTherapyFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if(Objects.nonNull(filterMap.get("isMissedTherapy"))){
			
			if("1".equals(filterMap.get("isMissedTherapy")))
				filterQuery.append(" and (isMissedTherapy > 0 && isMissedTherapy %3 = 0) ");
			else if("0".equals(filterMap.get("isMissedTherapy")))
				filterQuery.append(" and (isMissedTherapy %3 <> 0)");
		}
	}

	private void applyIsMissedTherapyFilter(BooleanBuilder whereClause, Map<String, String> filterMap,
			QPatientCompliance qPatientCompliance) {
			if(Objects.nonNull(filterMap.get("isMissedTherapy"))){
				if("1".equals(filterMap.get("isMissedTherapy")))
					whereClause.and(qPatientCompliance.missedTherapyCount.gt(0).and(qPatientCompliance.missedTherapyCount.mod(3).eq(0)));
				else if("0".equals(filterMap.get("isMissedTherapy")))
					whereClause.and(qPatientCompliance.missedTherapyCount.mod(3).gt(0).and(qPatientCompliance.missedTherapyCount.mod(3).lt(0)));
		}
	}
	
	private void applyIsSettingsDeviatedFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if(Objects.nonNull(filterMap.get("isSettingsDeviated"))){
			
			
			if("1".equals(filterMap.get("isSettingsDeviated")))
				filterQuery.append(" and isSettingsDeviated = 1 ");
			else if("0".equals(filterMap.get("isSettingsDeviated")))
				filterQuery.append(" and isSettingsDeviated = 0 ");
		}
	}
	
	private void applyIsSettingsDeviatedFilter(BooleanBuilder whereClause, Map<String, String> filterMap,
			QPatientCompliance qPatientCompliance) {
			if(Objects.nonNull(filterMap.get("isSettingsDeviated"))){

				if("1".equals(filterMap.get("isSettingsDeviated")))
					whereClause.and(qPatientCompliance.isSettingsDeviated.eq(Boolean.TRUE));
				else if("0".equals(filterMap.get("isSettingsDeviated")))
					whereClause.and(qPatientCompliance.isSettingsDeviated.eq(Boolean.FALSE));
		}
	}


	private void applyIsHMRNonCompliantFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if(Objects.nonNull(filterMap.get("isHMRNonCompliant"))){
			
			
			if("1".equals(filterMap.get("isHMRNonCompliant")))
				filterQuery.append(" and isHMRNonCompliant = 0 ");
			else if("0".equals(filterMap.get("isHMRNonCompliant")))
				filterQuery.append(" and isHMRNonCompliant = 1 ");
		}
	}
	
	private void applyIsHMRNonCompliantFilter(BooleanBuilder whereClause, Map<String, String> filterMap,
			QPatientCompliance qPatientCompliance) {
				if(Objects.nonNull(filterMap.get("isHMRNonCompliant"))){
					if("1".equals(filterMap.get("isHMRNonCompliant")))
					whereClause.and(qPatientCompliance.isHmrCompliant.eq(Boolean.FALSE));
					else if("0".equals(filterMap.get("isHMRNonCompliant")))
						whereClause.and(qPatientCompliance.isHmrCompliant.eq(Boolean.TRUE));
				}
	}


	private void applyIsDeletedFilter(String query, StringBuilder filterQuery,
			Map<String, String> filterMap) {
		if(Objects.nonNull(filterMap.get("isDeleted"))){
			filterQuery.append(query);
			
			if("1".equals(filterMap.get("isDeleted")))
				filterQuery.append(") as search_table where isDeleted in (1)");
			else if("0".equals(filterMap.get("isDeleted")))
				filterQuery.append(")  as search_table where isDeleted in (0)");
			else
				filterQuery.append(") as search_table where isDeleted in (0,1)");
		}
		else{
			filterQuery.append(query);
			filterQuery.append(") as search_table where isDeleted in (0,1)");
		}
	}
	
	private void applyIsDeletedFilter(BooleanBuilder whereClause, Map<String, String> filterMap, QUser qUser) {
		if(Objects.nonNull(filterMap.get("isDeleted"))){
			if("1".equals(filterMap.get("isDeleted")))
				whereClause.and(qUser.isDeleted.in(new ArrayList<Boolean>().add(Boolean.TRUE)));
			else if("0".equals(filterMap.get("isDeleted")))
				whereClause.and(qUser.isDeleted.in(new ArrayList<Boolean>().add(Boolean.FALSE)));
			else{
				List<Boolean> booleanList = new ArrayList<Boolean>();
				booleanList.add(Boolean.TRUE);
				booleanList.add(Boolean.FALSE);
				whereClause.and(qUser.isDeleted.in(booleanList));
			}
		}
		else{
			List<Boolean> booleanList = new ArrayList<Boolean>();
			booleanList.add(Boolean.TRUE);
			booleanList.add(Boolean.FALSE);
			whereClause.and(qUser.isDeleted.in(booleanList));
		}
	}

	public Page<PatientUserVO> findAssociatedPatientToClinicAdminBy(String queryString, Long clinicAdminId, String clinicId, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,phillrom_id,createdAt,isActivated, state  , pcompliance_score,last_date,mrnid,hlastName,clinicName,pclinicadmin,pclinicadminname,pclinicid,isSettingsDeviated,isHMRNonCompliant,isMissedTherapy from "
				+ " (select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,user.last_name as plastName, user.is_deleted as isDeleted,user.zipcode as pzipcode,patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,user.gender as pgender,user.title as ptitle,user.hillrom_id as phillrom_id,user.created_date as createdAt,user.activated as isActivated, patInfo.state as state  , user_clinic.mrn_id as mrnid, clinic.id as pclinicid, clinic.name as clinicName,clinic.clinic_admin_id as pclinicadmin,(select CONCAT( ca.last_name,' ',ca.first_name) from USER ca  where ca.id = pclinicadmin )as pclinicadminname , pc.compliance_score as pcompliance_score, pc.last_therapy_session_date as last_date, "
				+ " pc.is_settings_deviated as isSettingsDeviated ,pc.is_hmr_compliant as isHMRNonCompliant,pc.missed_therapy_count as isMissedTherapy "
				+ " from USER user left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = '"+PATIENT+"' "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or   "
				+ " lower(user.email) like lower(:queryString) or  "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ " lower(user.hillrom_id) like lower(:queryString) )  join USER_PATIENT_ASSOC  upa on user.id = upa.user_id "
				+ " and upa.relation_label = '"+SELF+"' join PATIENT_INFO patInfo on upa.patient_id = patInfo.id  and "
				+ " ((lower(IFNULL(patInfo.city,'')) like lower(:queryString)) or (lower(IFNULL(patInfo.state,'')) like lower(:queryString))) "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id and "
				+ " lower(IFNULL(user_clinic.mrn_id,0)) like lower(:queryString) "
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate() "
				+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id "
				+ " ) as associated_patient "
				+ " left outer join (select  huser.id as hcp_id, concat( huser.last_name,' ',huser.first_name ) as hlastName "
				+ " , clinic.id as hclinicid from USER huser "
				+ " join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id and user_authorityh.authority_name = '"+CLINIC_ADMIN+"' "
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
				+ " left outer join PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=curdate()"
				+ " ) as associated_hcp on associated_patient.pclinicid = associated_hcp.hclinicid "
				+ " where lower(IFNULL(pclinicadmin,0))= :clinicAdminId ";
		
		StringBuilder filterQuery = new StringBuilder();
	
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			
			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicAdminId",
				clinicAdminId.toString());
		
		if(!StringUtils.isEmpty(clinicId)){
			findPatientUserQuery = findPatientUserQuery.concat(" and pclinicid = '"+clinicId+"'");
		}
		
		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);
		
		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = extractPatientSearchResultsToVO(results);

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}

	public Page<PatientUserVO> findAssociatedPatientToHCPAndClinicBy(String queryString, Long hcpUserID, String clinicId, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery;
		
		String query1= "select user.id,user.email,user.first_name as firstName,user.last_name as"
				+ " lastName, user.is_deleted as isDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
				+ "user.title,user.hillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence, pc.last_therapy_session_date as last_date, "
				+ "pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,pc.missed_therapy_count as isMissedTherapy "
				+ "from USER user join USER_AUTHORITY user_authority on user_authority.user_id"
				+ " = user.id and user_authority.authority_name = '"+PATIENT+"'and "
				+ "(lower(user.first_name) like lower(:queryString) or "
				+ "lower(user.last_name) like lower(:queryString) or "
				+ "lower(user.email) like lower(:queryString) or "
				+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ "lower(user.hillrom_id) like lower(:queryString)) "
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"+SELF+"' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id "
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate() ";
		String query2 = " where upa_hcp.user_id = :hcpUserID ";
		
		if(!StringUtils.isEmpty(clinicId)){
			findPatientUserQuery = query1+
					" join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = '"+clinicId+"' "+
					query2;
		}
		else 
			findPatientUserQuery=query1+query2;
		
		StringBuilder filterQuery = new StringBuilder();
	
		if(StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)){

			Map<String,String> filterMap = getSearchParams(filter);
			
			filterQuery.append("select * from (");
			
			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString",
				queryString);
		
		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID",
				hcpUserID.toString());
		
		if(!StringUtils.isEmpty(clinicId)){
			findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicSearch",
					" join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = '"+clinicId+"'" );
		}
		String countSqlQuery = "select count(patientUsers.id) from ("
				+ findPatientUserQuery + " ) patientUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);
		
		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = extractPatientSearchResultsToVO(results);

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

	//Patient Search which are not associated with the clinic
	
		public List<PatientUserVO> findPatientNotAssociatedToClinic(String clinicId, String searchString) {

			String findPatientUserQuery = "select * from ( select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,"
					+ "user.last_name as plastName, user.is_deleted as isDeleted,user.zipcode as pzipcode,"
					+ "patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,user.gender as pgender,"
					+ "user.title as ptitle,user.hillrom_id as phillrom_id,user.created_date as createdAt,"
					+ "user.activated as isActivated, patInfo.state as state,pc.compliance_score as pcompliance_score, "
					+ "pc.last_therapy_session_date as last_date , user_clinic.mrn_id as mrnid,"
					+ "clinic.id as pclinicid, GROUP_CONCAT(clinic.name) as clinicName from USER user "
					+"join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = 'PATIENT' and "
					+ "(lower(user.first_name) like lower(:searchString) or  lower(user.last_name) like lower(:searchString) or   "
					+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:searchString) or "
					+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:searchString)  )"  
					+"join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = 'Self' "
					+"join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
					+"left outer join CLINIC_PATIENT_ASSOC user_clinic on "
					+"user_clinic.patient_id = patInfo.id "
					+"left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=curdate()  "
					+"left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id "
					+" where clinic.id <> ':clinicId' or clinic.id IS NULL "
					+"group by user.id ) as tble where patient_id not in ( select user.id as patient_id  from USER user "
					+"join USER_PATIENT_ASSOC  upa on user.id = upa.user_id  and upa.relation_label = 'Self' "
					+"join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
					+"join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id "
					+"left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id "
					+"where clinic.id = ':clinicId')";

			if(StringUtils.isEmpty(searchString))
				findPatientUserQuery = findPatientUserQuery.replaceAll(":searchString","");
			else
				findPatientUserQuery = findPatientUserQuery.replaceAll(":searchString", searchString);
			
			findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicId", clinicId);

			Query patientQuery = entityManager.createNativeQuery(findPatientUserQuery);

			
			List<Object[]> results = patientQuery.getResultList();

			List<PatientUserVO> patientUsers =  extractPatientSearchResultsToVO(results);
			return patientUsers;
		}

	
	
	private Map<String,String> getSearchParams(String filterString){
		
		Map<String,String> filterMap = new HashMap<>();
		
		if(StringUtils.isEmpty(filterString))
		return filterMap;
		
		String[] filters = filterString.split(";");
		for(String filter : filters){
			
			String[] pair = filter.split(":");
			if(pair.length>1)
			if(!StringUtils.isEmpty(pair[1]))
				filterMap.put(pair[0],pair[1]);
		}
		return filterMap;
	}
	
	public List<String> getAllTest(Pageable generatePageRequest) {
		QUser user = QUser.user;
		
		SQLTemplates dialect = new MySQLTemplates(); // SQL-dialect
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.sql.Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hillromvest_copy_of_dev","root", "root");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Configuration configuration = new Configuration(dialect);
		SQLQuery query1 = new SQLQuery(conn, configuration); 
		JPAQuery query = new JPAQuery(entityManager);
		List<String> tupleList = query1.from(user).where(user.firstName.eq("Pradeep")).list(user.firstName);
		
		return tupleList;
	}
}
