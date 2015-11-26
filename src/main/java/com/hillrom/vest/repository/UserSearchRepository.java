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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.PatientUserVO;

@Repository
public class UserSearchRepository {

	private static final String ORDER_BY_CLAUSE_START = " order by ";
	@Inject
	private EntityManager entityManager;

	@Inject
	private HCPClinicService hcpClinicService;
	@Inject
	private UserPatientRepository userPatientRepository;

	public Page<HillRomUserVO> findHillRomTeamUsersBy(String queryString, String filter, Pageable pageable,
			Map<String, Boolean> sortOrder) {

		String findHillromTeamUserQuery = "select distinct(user.id),user.first_name as firstName,user.last_name as lastName,user.email,"
				+ " user_authority.authority_name as name,user.is_deleted as isDeleted,user.created_date as createdAt,user.activated as isActivated,user.hillrom_id as hillromId, userExt.mobile_phone as mobilePhone "
				+ " from  USER_EXTENSION userExt left outer join USER user on user.id = userExt.user_id and "
				+ " (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or"
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or"
				+ " lower(user.email) like lower(:queryString) or lower(user.hillrom_id) like lower(:queryString)) "
				+ " left outer join  USER_AUTHORITY user_authority on user_authority.user_id = user.id "
				+ " and  user_authority.authority_name in ('" + ADMIN + "','" + ACCT_SERVICES + "','" +  AuthoritiesConstants.CARE_GIVER + "','" + CLINIC_ADMIN + "','" + HCP + "','" + PATIENT + "','" + ASSOCIATES
				+ "','" + HILLROM_ADMIN + "')";

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");
			applyIsDeletedFilter(findHillromTeamUserQuery, filterQuery, filterMap);
			findHillromTeamUserQuery = filterQuery.toString();
		}
		findHillromTeamUserQuery = findHillromTeamUserQuery.replaceAll(":queryString", queryString);
		String countSqlQuery = "select count(hillromUsers.id) from (" + findHillromTeamUserQuery + ") hillromUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findHillromTeamUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<HillRomUserVO> hrUsersList = new ArrayList<>();
		List<Object[]> results = query.getResultList();
		results.stream().forEach((record) -> {
			Long id = ((BigInteger) record[0]).longValue();
			String firstName = (String) record[1];
			String lastName = (String) record[2];
			String email = (String) record[3];
			String role = (String) record[4];
			Boolean isDeleted = (Boolean) record[5];
			Timestamp createdAt = (Timestamp) record[6];
			Boolean isActivated = (Boolean) record[7];
			String hillromId = (String) record[8];
			DateTime createdAtDatetime = new DateTime(createdAt);
			String mobilePhone = (String) record[9];

			HillRomUserVO hrUserVO = new HillRomUserVO(id, firstName, lastName, email, role, isDeleted,
					createdAtDatetime, isActivated, hillromId, mobilePhone);
			hrUsersList.add(hrUserVO);
		});

		Page<HillRomUserVO> page = new PageImpl<HillRomUserVO>(hrUsersList, null, count.intValue());

		return page;
	}

	private void setPaginationParams(Pageable pageable, Query query) {

		int firstResult = pageable.getOffset();
		int maxResult = pageable.getPageSize();
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
	}

	public Page<HcpVO> findHCPBy(String queryString, String filter, Pageable pageable, Map<String, Boolean> sortOrder, 
			String associatedToClinicId) {

		String findHcpQuery = "select user.id,user.email,user.first_name as firstName,user.last_name as lastName,user.is_deleted as isDeleted,"
				+ " user.zipcode,userExt.address,userExt.city as hcity,userExt.credentials,userExt.fax_number,userExt.primary_phone,"
				+ " userExt.mobile_phone,userExt.speciality,userExt.state as hstate,clinic.id as clinicId,clinic.name as clinicName,user.created_date as createdAt,user.activated isActivated,userExt.npi_number as npiNumber "
				+ " FROM USER user join USER_EXTENSION userExt on user.id = userExt.user_id "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or"
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or"
				+ " lower(user.email) like lower(:queryString)) "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = '"
				+ AuthoritiesConstants.HCP + "'"
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = user.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = user.id ";
		
		if (StringUtils.isNotEmpty(associatedToClinicId))
			findHcpQuery+= " where user_clinic.users_id in (select users_id from CLINIC_USER_ASSOC where clinics_id = '"+ associatedToClinicId +"')";
		
		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyIsDeletedFilter(findHcpQuery, filterQuery, filterMap);

			findHcpQuery = filterQuery.toString();
		}

		findHcpQuery = findHcpQuery.replaceAll(":queryString", queryString);
		
		

		String countSqlQuery = "select count(distinct hcpUsers.id) from (" + findHcpQuery + " ) hcpUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findHcpQuery, sortOrder);

		List<HcpVO> hcpUsers = new ArrayList<>();

		Map<Long, HcpVO> hcpUsersMap = new HashMap<>();
		List<Object[]> results = query.getResultList();

		results.forEach((record) -> {
			Long id = ((BigInteger) record[0]).longValue();
			String email = (String) record[1];
			String firstName = (String) record[2];
			String lastName = (String) record[3];
			Boolean isDeleted = (Boolean) record[4];
			Integer zipcode = (Integer) record[5];
			String address = (String) record[6];
			String city = (String) record[7];
			String credentials = (String) record[8];

			String faxNumber = (String) record[9];
			String primaryPhone = (String) record[10];
			String mobilePhone = (String) record[11];
			String speciality = (String) record[12];
			String state = (String) record[13];
			String clinicId = (String) record[14];
			String clinicName = (String) record[15];
			Timestamp createdAt = (Timestamp) record[16];
			DateTime createdAtDatetime = new DateTime(createdAt);
			Boolean isActivated = (Boolean) record[17];
			String npiNumber = (String) record[18];

			HcpVO hcpVO = hcpUsersMap.get(id);

			Map<String, String> clinicMap = new HashMap<>();
			if (null != clinicId) {
				clinicMap.put("id", clinicId);
				clinicMap.put("name", clinicName);
			}
			if (hcpVO == null) {
				hcpVO = new HcpVO(id, firstName, lastName, email, isDeleted, zipcode, address, city, credentials,
						faxNumber, primaryPhone, mobilePhone, speciality, state, createdAtDatetime, isActivated,
						npiNumber);
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
		if (firstResult < hcpUsers.size()) {
			maxResults = maxResults > hcpUsers.size() ? hcpUsers.size() : maxResults;
			hcpUsersSubList = hcpUsers.subList(firstResult, maxResults);
		}
		Page<HcpVO> page = new PageImpl<HcpVO>(hcpUsersSubList, null, count.intValue());

		return page;
	}

	// Get all HCPs associated with Clinics of a Patient
	public List<HcpVO> findHCPByPatientClinics(String queryString, String filter, Long userId,
			Map<String, Boolean> sortOrder) throws HillromException {

		List<UserPatientAssoc> associations = userPatientRepository.findOneByUserId(userId);
		associations = associations.stream()
				.filter(assoc -> RelationshipLabelConstants.SELF.equalsIgnoreCase(assoc.getRelationshipLabel()))
				.collect(Collectors.toList());
		UserPatientAssoc userPatientAssoc = associations.isEmpty() ? null : associations.get(0);
		if (Objects.isNull(userPatientAssoc))
			throw new HillromException(ExceptionConstants.HR_523);

		String patientId = userPatientAssoc.getPatient().getId();

		String findHcpQuery = "select * from"
				+ " (select user.id,user.email,user.first_name as firstName,user.last_name as lastName,user.is_deleted as isDeleted, "
				+ " user.zipcode,userExt.address,userExt.city as hcity,userExt.credentials,userExt.fax_number,userExt.primary_phone,"
				+ " userExt.mobile_phone,"
				+ " userExt.speciality,userExt.state as hstate,clinic.id as clinicId,clinic.name as clinicName,"
				+ " user.created_date as createdAt,user.activated isActivated,userExt.npi_number as npiNumber  FROM USER user "
				+ " join USER_EXTENSION userExt on user.id = userExt.user_id  and (lower(user.first_name) "
				+ " like lower(:queryString) or lower(user.last_name) like lower(:queryString) or  "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) "
				+ " or lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) "
				+ " or lower(user.email) like lower(:queryString))  "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id "
				+ " and user_authority.authority_name = '" + HCP + "' left outer "
				+ " join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = user.id  "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = user.id ) "
				+ " as t,CLINIC_PATIENT_ASSOC cpasso "
				+ " where cpasso.patient_id = ':patientId' and t.clinicId = cpasso.clinic_id";

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyIsDeletedFilter(findHcpQuery, filterQuery, filterMap);

			findHcpQuery = filterQuery.toString();
		}

		findHcpQuery = findHcpQuery.replaceAll(":queryString", queryString);
		findHcpQuery = findHcpQuery.replaceAll(":patientId", patientId);

		String countSqlQuery = "select count(hcpUsers.id) from (" + findHcpQuery + " ) hcpUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findHcpQuery, sortOrder);

		List<HcpVO> hcpUsers = new ArrayList<>();

		List<Object[]> results = query.getResultList();

		results.forEach((record) -> {
			Long id = ((BigInteger) record[0]).longValue();
			String email = (String) record[1];
			String firstName = (String) record[2];
			String lastName = (String) record[3];
			Boolean isDeleted = (Boolean) record[4];
			Integer zipcode = (Integer) record[5];
			String address = (String) record[6];
			String city = (String) record[7];
			String credentials = (String) record[8];

			String faxNumber = (String) record[9];
			String primaryPhone = (String) record[10];
			String mobilePhone = (String) record[11];
			String speciality = (String) record[12];
			String state = (String) record[13];
			String clinicId = (String) record[14];
			String clinicName = (String) record[15];
			Timestamp createdAt = (Timestamp) record[16];
			DateTime createdAtDatetime = new DateTime(createdAt);
			Boolean isActivated = (Boolean) record[17];
			String npiNumber = (String) record[18];

			Map<String, String> clinicMap = new HashMap<>();
			clinicMap.put("id", clinicId);
			clinicMap.put("name", clinicName);

			HcpVO hcpVO = new HcpVO(id, firstName, lastName, email, isDeleted, zipcode, address, city, credentials,
					faxNumber, primaryPhone, mobilePhone, speciality, state, createdAtDatetime, isActivated, npiNumber);
			hcpVO.getClinics().add(clinicMap);

			hcpUsers.add(hcpVO);
		});
		return hcpUsers;
	}

	// Search HCPs for Clinic Admin and clinic Id, used in Clinic Admin Dash
	// board.
	public Page<HcpVO> findHCPByClinicAdmin(Long clinicAdminId, String paramClinicId, String queryString, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findHcpByClinicAdminQuery = "select user.id,user.email,user.first_name as firstName,user.last_name as lastName,user.is_deleted as isDeleted,"
				+ " user.zipcode,userExt.address,userExt.city as hcity,userExt.credentials,userExt.fax_number,userExt.primary_phone,"
				+ " userExt.mobile_phone,userExt.speciality,userExt.state as hstate,clinic.id as clinicId,clinic.name as clinicName,user.created_date as createdAt,user.activated isActivated,userExt.npi_number as npiNumber "
				+ " FROM USER user join USER_EXTENSION userExt on user.id = userExt.user_id "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or"
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or"
				+ " lower(user.email) like lower(:queryString)) "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = '"
				+ AuthoritiesConstants.HCP + "'"
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = user.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = user.id "
				+ " where clinic.clinic_admin_id = :clinicAdminId ";

		String clinicFilterQuery = " and clinic.id = ':clinicId'";

		findHcpByClinicAdminQuery = findHcpByClinicAdminQuery.replaceAll(":clinicAdminId",
				Long.toString(clinicAdminId));

		if (StringUtils.isNotEmpty(paramClinicId)) {
			findHcpByClinicAdminQuery += clinicFilterQuery;
			findHcpByClinicAdminQuery = findHcpByClinicAdminQuery.replaceAll(":clinicId", paramClinicId);
		}

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyIsDeletedFilter(findHcpByClinicAdminQuery, filterQuery, filterMap);

			findHcpByClinicAdminQuery = filterQuery.toString();
		}

		findHcpByClinicAdminQuery = findHcpByClinicAdminQuery.replaceAll(":queryString", queryString);

		String countSqlQuery = "select count(distinct hcpUsers.id) from (" + findHcpByClinicAdminQuery + " ) hcpUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findHcpByClinicAdminQuery, sortOrder);

		List<HcpVO> hcpUsers = new ArrayList<>();

		Map<Long, HcpVO> hcpUsersMap = new HashMap<>();
		List<Object[]> results = query.getResultList();

		results.forEach((record) -> {
			Long id = ((BigInteger) record[0]).longValue();
			String email = (String) record[1];
			String firstName = (String) record[2];
			String lastName = (String) record[3];
			Boolean isDeleted = (Boolean) record[4];
			Integer zipcode = (Integer) record[5];
			String address = (String) record[6];
			String city = (String) record[7];
			String credentials = (String) record[8];

			String faxNumber = (String) record[9];
			String primaryPhone = (String) record[10];
			String mobilePhone = (String) record[11];
			String speciality = (String) record[12];
			String state = (String) record[13];
			String clinicId = (String) record[14];
			String clinicName = (String) record[15];
			Timestamp createdAt = (Timestamp) record[16];
			DateTime createdAtDatetime = new DateTime(createdAt);
			Boolean isActivated = (Boolean) record[17];
			String npiNumber = (String) record[18];

			HcpVO hcpVO = hcpUsersMap.get(id);

			Map<String, String> clinicMap = new HashMap<>();
			if (null != clinicId) {
				clinicMap.put("id", clinicId);
				clinicMap.put("name", clinicName);
			}
			if (hcpVO == null) {
				hcpVO = new HcpVO(id, firstName, lastName, email, isDeleted, zipcode, address, city, credentials,
						faxNumber, primaryPhone, mobilePhone, speciality, state, createdAtDatetime, isActivated,
						npiNumber);
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
		if (firstResult < hcpUsers.size()) {
			maxResults = maxResults > hcpUsers.size() ? hcpUsers.size() : maxResults;
			hcpUsersSubList = hcpUsers.subList(firstResult, maxResults);
		}
		Page<HcpVO> page = new PageImpl<HcpVO>(hcpUsersSubList, null, count.intValue());

		return page;
	}

	// Patient Search for Admin log in
	public Page<PatientUserVO> findPatientBy(String queryString, String filter, Pageable pageable,
			Map<String, Boolean> sortOrder) {

		String query1 = "select patient_id as id,pemail,pfirstName,plastName, isDeleted,pzipcode,paddress,pcity,pdob,pgender,ptitle,"
				+ "phillrom_id,createdAt,isActivated, state , adherence,last_date,mrnid,hName,clinicName,isExpired,isHMRNonCompliant,isSettingsDeviated,"
				+ "isMissedTherapy  from (select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,user.last_name as plastName,"
				+ " user.is_deleted as isDeleted, user.zipcode as pzipcode,patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,"
				+ "user.gender as pgender,user.title as ptitle,  user.hillrom_id as phillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state as state ,  user_clinic.mrn_id as mrnid, clinic.id as pclinicid, "
				+ "GROUP_CONCAT(clinic.name) as clinicName, user.expired as isExpired, pc.compliance_score as adherence,  "
				+ "pc.last_therapy_session_date as last_date,pc.is_hmr_compliant as isHMRNonCompliant,"
				+ "pc.is_settings_deviated as isSettingsDeviated,"
				+ " pc.missed_therapy_count as isMissedTherapy from USER user join USER_PATIENT_ASSOC  upa on user.id = upa.user_id "
				+ " and upa.relation_label = '" + SELF + "' join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id "
				+ " join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = '"
				+ PATIENT + "' "
				+ " and (lower(user.first_name) like lower(:queryString) or  lower(user.last_name) like lower(:queryString) or "
				+ " lower(user.email) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or ";

		String hrIdSearch = " lower(user.hillrom_id) like lower(:queryString))";

		String query2 = " ((lower(IFNULL(patInfo.city,'')) like lower(:queryString)) or "
				+ " (lower(IFNULL(patInfo.state,'')) like lower(:queryString))) )";

		// This is applicable only when search is performed by HCP or
		// CLINIC_ADMIN
		String mrnIdSearch = " or (lower(IFNULL(user_clinic.mrn_id,0)) like lower(:queryString) ) ) ";

		String query3 = " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1) "
				+ " left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and  user_clinic.patient_id = patInfo.id "
				+ " group by user.id) as associated_patient left outer join (select  GROUP_CONCAT(huser.last_name ,' ',huser.first_name ) as hName, "
				+ " clinic.id as hclinicid from USER huser join USER_AUTHORITY user_authorityh on user_authorityh.user_id = huser.id "
				+ " and user_authorityh.authority_name = '" + HCP + "' "
				+ " left outer join CLINIC_USER_ASSOC user_clinic on user_clinic.users_id = huser.id "
				+ " left outer join CLINIC clinic on user_clinic.clinics_id = clinic.id and user_clinic.users_id = huser.id "
				+ " left outer join PATIENT_COMPLIANCE pc on huser.id = pc.user_id AND pc.date=subdate(curdate(),1) "
				+ " group by clinic.id) as associated_hcp  on associated_patient.pclinicid = associated_hcp.hclinicid ";

		String findPatientUserQuery = query1;
		// HCP , CLINIC_ADMIN can search on MRNID not HRID
		if (SecurityUtils.isUserInRole(HCP) || SecurityUtils.isUserInRole(CLINIC_ADMIN))
			findPatientUserQuery = findPatientUserQuery
					.substring(0, findPatientUserQuery.lastIndexOf(")")).concat(mrnIdSearch);
		else // Admin can search on HRID not MRNID
			findPatientUserQuery += hrIdSearch;
		findPatientUserQuery += query3;

		findPatientUserQuery = applyFiltersToQuery(filter, findPatientUserQuery);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString", queryString);

		String countSqlQuery = "select count(patientUsers.id) from (" + findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		System.out.println("Query "+findPatientUserQuery);
		
		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();

		results.stream().forEach((record) -> {
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
			Boolean isExpired = (Boolean) record[20];

			java.util.Date localLastTransmissionDate = null;

			if (Objects.nonNull(lastTransmissionDate)) {
				localLastTransmissionDate = new java.util.Date(lastTransmissionDate.getTime());

			}

			LocalDate dobLocalDate = null;
			if (Objects.nonNull(dob)) {
				dobLocalDate = new LocalDate(dob.getTime());
			}

			PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName, lastName, isDeleted, zipcode, address,
					city, dobLocalDate, gender, title, hillromId, createdAtDatetime, isActivated, state,
					Objects.nonNull(adherence) ? adherence : 0, localLastTransmissionDate);
			// mrnId,hcpNamesCSV,clinicNamesCSV
			patientUserVO.setMrnId(mrnId);
			patientUserVO.setHcpNamesCSV(hcpNamesCSV);
			patientUserVO.setClinicNamesCSV(clinicNamesCSV);
			patientUserVO.setExpired(isExpired);
			patientUsers.add(patientUserVO);
		});
		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(addLatestAdherenceScore(patientUsers), null, count.intValue());

		return page;
	}

	private List<PatientUserVO> extractPatientSearchResultsToVO(List<Object[]> results) {
		List<PatientUserVO> patientUsers = new LinkedList<>();
		results.stream().forEach((record) -> {
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

			if (Objects.nonNull(lastTransmissionDate)) {
				localLastTransmissionDate = lastTransmissionDate;

			}

			LocalDate dobLocalDate = null;
			if (Objects.nonNull(dob)) {
				dobLocalDate = new LocalDate(dob.getTime());
			}

			PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName, lastName, isDeleted, zipcode, address,
					city, dobLocalDate, gender, title, hillromId, createdAtDatetime, isActivated, state,
					Objects.nonNull(adherence) ? adherence : 0, localLastTransmissionDate);
			// mrnId,hcpNamesCSV,clinicNamesCSV
			patientUserVO.setMrnId(mrnId);
			patientUserVO.setHcpNamesCSV(hcpNamesCSV);
			patientUserVO.setClinicNamesCSV(clinicNamesCSV);
			patientUsers.add(patientUserVO);
		});
		return patientUsers;
	}

	private String applyFiltersToQuery(String filter, String query) {

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFilters(query, filterQuery, filterMap);
			query = filterQuery.toString();
		}
		return query;
	}

	// Pateint Search for HCP DashBoard

	public Page<PatientUserVO> findAssociatedPatientToHCPBy(String queryString, Long hcpUserID, String clinicId,
			String filter, Pageable pageable, Map<String, Boolean> sortOrder) throws HillromException {

		String findPatientUserQuery = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName, "
				+ " user.is_deleted as isUserDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob as patientDoB,"
				+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt,"
				+ " user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
				+ " pc.last_therapy_session_date as last_date," + " (select  GROUP_CONCAT(clinicc.name)"
				+ " from USER userc "
				+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  "
				+ "and user_authorityc.authority_name = 'PATIENT' "
				+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = 'Self' "
				+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id  "
				+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
				+ " where upac.user_id = user.id "
				+ " group by patInfoc.id) as clinicname, (select  GROUP_CONCAT(userh.first_name,' ', userh.last_name) "
				+ " from USER userh "
				+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id  "
				+ "and user_authorityh.authority_name = 'HCP' "
				+ " join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id and upah.relation_label = 'HCP' "
				+ " left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
				+ " where patInfo.id = patInfoh.id"
				+ " group by patInfoh.id) as hcpname, patient_clinic.mrn_id as mrnid, patient_clinic.is_active as isDeleted,"
				+ " user.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,"
				+ " pc.missed_therapy_count as isMissedTherapy " + " from USER user"
				+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "'"
				+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
				+ " join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id  "
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1)  "
				+ " left outer join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id "
				+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id"
				+ " and user_authority.authority_name = '" + PATIENT + "'";

		String searchQuery = " and (lower(user.first_name) "
				+ " like lower(:queryString) or lower(user.last_name) like lower(:queryString) "
				+ " or lower(user.email) like lower(:queryString) or lower(CONCAT(user.first_name,' ',user.last_name)) "
				+ " like lower(:queryString) or lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) "
				+ " or lower(user.hillrom_id) like lower(:queryString) or lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString)) ";

		String query2 = " where upa_hcp.user_id = :hcpUserID ";
		String query3 = " group by user.id ";

		String associatedClinicList = hcpClinicService.getFlattenedAssociatedClinicsIdForHCP(hcpUserID);

		String filterByAssociatedClinicList = StringUtils.isEmpty(associatedClinicList) ? ""
				: " or patient_clinic.clinic_id  in (" + associatedClinicList + " ) ";
		if (StringUtils.isEmpty(clinicId) | "all".equalsIgnoreCase(clinicId)) {
			findPatientUserQuery = findPatientUserQuery + query2 + searchQuery + filterByAssociatedClinicList + query3;
		} else if ("others".equalsIgnoreCase(clinicId)) {
			findPatientUserQuery = findPatientUserQuery + query2 + searchQuery + query3;
		} else
			findPatientUserQuery = findPatientUserQuery + " where patient_clinic.clinic_id  ='" + clinicId + "'"
					+ searchQuery + query3;

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFiltersForHCPandClinicAdmin(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString", queryString);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID", hcpUserID.toString());

		String countSqlQuery = "select count(patientUsers.id) from (" + findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();
		results.stream().forEach((record) -> {
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
			Boolean isActiveInClinic = (Boolean) record[20];
			Boolean isExpired = (Boolean) record[21];

			java.util.Date localLastTransmissionDate = null;

			if (Objects.nonNull(lastTransmissionDate)) {
				localLastTransmissionDate = lastTransmissionDate;
			}

			LocalDate dobLocalDate = null;
			if (Objects.nonNull(dob)) {
				dobLocalDate = new LocalDate(dob.getTime());
			}
			// Clinic wise active inactive is not the for the case og others
			if (Objects.isNull(isActiveInClinic))
				isActiveInClinic = !isDeleted;

			PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName, lastName,
					isActiveInClinic ? false : true, zipcode, address, city, dobLocalDate, gender, title, hillromId,
					createdAtDatetime, isActivated, state, Objects.nonNull(adherence) ? adherence : 0,
					localLastTransmissionDate);
			// mrnId,hcpNamesCSV,clinicNamesCSV
			patientUserVO.setMrnId(mrnId);
			patientUserVO.setHcpNamesCSV(hcpNamesCSV);
			patientUserVO.setClinicNamesCSV(clinicNamesCSV);
			patientUserVO.setExpired(isExpired);
			patientUsers.add(patientUserVO);
		});

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(addLatestAdherenceScore(patientUsers), null, count.intValue());

		return page;
	}

	// Pateint Search for HCP DashBoard in Admin Dashboard

	public Page<PatientUserVO> findAssociatedPatientToHCPInAdmin(String queryString, Long hcpUserID, String clinicId,
			String filter, Pageable pageable, Map<String, Boolean> sortOrder) throws HillromException {

		String findPatientUserQuery = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName, "
				+ " user.is_deleted as isUserDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob as patientDoB,"
				+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt,"
				+ " user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
				+ " pc.last_therapy_session_date as last_date," + " (select  GROUP_CONCAT(clinicc.name)"
				+ " from USER userc "
				+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  "
				+ "and user_authorityc.authority_name = 'PATIENT' "
				+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = 'Self' "
				+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id  "
				+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
				+ " where upac.user_id = user.id "
				+ " group by patInfoc.id) as clinicname, (select  GROUP_CONCAT(userh.first_name,' ', userh.last_name) "
				+ " from USER userh "
				+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id  "
				+ "and user_authorityh.authority_name = 'HCP' "
				+ " join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id and upah.relation_label = 'HCP' "
				+ " left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
				+ " where patInfo.id = patInfoh.id"
				+ " group by patInfoh.id) as hcpname, patient_clinic.mrn_id as mrnid, patient_clinic.is_active as isDeleted,"
				+ " user.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,"
				+ " pc.missed_therapy_count as isMissedTherapy " + " from USER user"
				+ " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "'"
				+ " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
				+ " join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id  "
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1)  "
				+ " left outer join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id "
				+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id"
				+ " and user_authority.authority_name = '" + PATIENT + "'";

		String searchQuery = " and (lower(user.first_name) "
				+ " like lower(:queryString) or lower(user.last_name) like lower(:queryString) "
				+ " or lower(user.email) like lower(:queryString) or lower(CONCAT(user.first_name,' ',user.last_name)) "
				+ " like lower(:queryString) or lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) "
				+ " or lower(user.hillrom_id) like lower(:queryString) or lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString)) ";

		String query2 = " where upa_hcp.user_id = :hcpUserID ";
		String query3 = " group by user.id ";

		String associatedClinicList = hcpClinicService.getFlattenedAssociatedClinicsIdForHCP(hcpUserID);

		String filterByAssociatedClinicList = StringUtils.isEmpty(associatedClinicList) ? ""
				: " and patient_clinic.clinic_id  in (" + associatedClinicList + " ) ";
		if (StringUtils.isEmpty(clinicId) | "all".equalsIgnoreCase(clinicId)) {
			findPatientUserQuery = findPatientUserQuery + query2 + searchQuery + filterByAssociatedClinicList + query3;
		} else if ("others".equalsIgnoreCase(clinicId)) {
			findPatientUserQuery = findPatientUserQuery + query2 + searchQuery + query3;
		} else
			findPatientUserQuery = findPatientUserQuery + query2 + " and patient_clinic.clinic_id  ='" + clinicId + "'"
					+ searchQuery + query3;

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFiltersForHCPandClinicAdmin(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString", queryString);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID", hcpUserID.toString());

		String countSqlQuery = "select count(patientUsers.id) from (" + findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();
		results.stream().forEach((record) -> {
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
			Boolean isActiveInClinic = (Boolean) record[20];
			Boolean isExpired = (Boolean) record[21];

			java.util.Date localLastTransmissionDate = null;

			if (Objects.nonNull(lastTransmissionDate)) {
				localLastTransmissionDate = lastTransmissionDate;
			}

			LocalDate dobLocalDate = null;
			if (Objects.nonNull(dob)){
				dobLocalDate = new LocalDate(dob.getTime());
			}
			// Clinic wise active inactive is not the for the case og others
			if (Objects.isNull(isActiveInClinic))
				isActiveInClinic = !isDeleted;

			PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName, lastName,
					isActiveInClinic ? false : true, zipcode, address, city, dobLocalDate, gender, title, hillromId,
					createdAtDatetime, isActivated, state, Objects.nonNull(adherence) ? adherence : 0,
					localLastTransmissionDate);
			// mrnId,hcpNamesCSV,clinicNamesCSV
			patientUserVO.setMrnId(mrnId);
			patientUserVO.setHcpNamesCSV(hcpNamesCSV);
			patientUserVO.setClinicNamesCSV(clinicNamesCSV);
			patientUserVO.setExpired(isExpired);
			patientUsers.add(patientUserVO);
		});

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(addLatestAdherenceScore(patientUsers), null, count.intValue());

		return page;
	}

	public Page<PatientUserVO> findAssociatedPatientsToClinicBy(String queryString, String clinicID, String filter,
			Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = "select user.id,user.email,user.first_name as"
				+ " firstName,user.last_name as lastName, user.is_deleted as isDeleted,"
				+ "user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
				+ "user.title,user.hillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state , compliance_score, pc.last_therapy_session_date as last_date, user.expired, "
				+ "pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,pc.missed_therapy_count as isMissedTherapy "
				+ "from USER user" + " join USER_AUTHORITY user_authority on user_authority.user_id = user.id  "
				+ "and user_authority.authority_name = '" + PATIENT + "' and "
				+ "(lower(user.first_name) like lower(:queryString) or  "
				+ "lower(user.last_name) like lower(:queryString) or  "
				+ "lower(user.email) like lower(:queryString) or "
				+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ "lower(user.hillrom_id) like lower(:queryString)) "
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ "join CLINIC_PATIENT_ASSOC patient_clinic on "
				+ "patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = ':clinicId'"
				+ "left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1)";

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString", queryString);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicId", clinicID);
		String countSqlQuery = "select count(patientUsers.id) from (" + findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = extractPatientResultsToVO(results);

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(addLatestAdherenceScore(patientUsers), null, count.intValue());

		return page;
	}

	private List<PatientUserVO> extractPatientResultsToVO(List<Object[]> results) {
		List<PatientUserVO> patientUsers = new LinkedList<>();

		results.stream().forEach((record) -> {
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
			Boolean isExpired = (Boolean) record[17];

			LocalDate dobLocalDate = null;
			if (Objects.nonNull(dob)) {
				dobLocalDate = new LocalDate(dob.getTime());
			}
			java.util.Date localLastTransmissionDate = null;

			if (Objects.nonNull(lastTransmissionDate)) {
				localLastTransmissionDate = lastTransmissionDate;

			}

			PatientUserVO patientUser = new PatientUserVO(id, email, firstName, lastName, isDeleted, zipcode, address,
					city, dobLocalDate, gender, title, hillromId, createdAtDatetime, isActivated, state,
					Objects.nonNull(adherence) ? adherence : 0, localLastTransmissionDate);
			patientUser.setExpired(isExpired);

			patientUsers.add(patientUser);
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

	// isDeleted field has different meaning For patients in HCP and ClinicAdmin
	// dashboard
	private void applyQueryFiltersForHCPandClinicAdmin(String query, StringBuilder filterQuery,
			Map<String, String> filterMap) {
		applyIsDeletedFiltersForHCPandClinicAdmin(query, filterQuery, filterMap);

		applyIsHMRNonCompliantFilter(filterQuery, filterMap);

		applyIsSettingsDeviatedFilter(filterQuery, filterMap);

		applyIsMissedTherapyFilter(filterQuery, filterMap);

		applyIsNoEventFilter(filterQuery, filterMap);
	}

	private void applyIsNoEventFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if (Objects.nonNull(filterMap.get("isNoEvent")) && "1".equals(filterMap.get("isNoEvent"))) {

			filterQuery.append("and exists (SELECT PATIENT_NO_EVENT.id FROM PATIENT_NO_EVENT "
					+ "WHERE PATIENT_NO_EVENT.user_id = search_table.id AND "
					+ "PATIENT_NO_EVENT.first_transmission_date is null LIMIT 1)");
		}
	}

	private void applyIsMissedTherapyFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if (Objects.nonNull(filterMap.get("isMissedTherapy"))) {

			if ("1".equals(filterMap.get("isMissedTherapy")))
				filterQuery.append(" and (isMissedTherapy >= 3) ");
			else if ("0".equals(filterMap.get("isMissedTherapy")))
				filterQuery.append(" and ( isMissedTherapy < 3 )");
		}
	}

	private void applyIsSettingsDeviatedFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if (Objects.nonNull(filterMap.get("isSettingsDeviated"))) {

			if ("1".equals(filterMap.get("isSettingsDeviated")))
				filterQuery.append(" and isSettingsDeviated = 1 ");
			else if ("0".equals(filterMap.get("isSettingsDeviated")))
				filterQuery.append(" and isSettingsDeviated = 0 ");
		}
	}

	private void applyIsHMRNonCompliantFilter(StringBuilder filterQuery, Map<String, String> filterMap) {
		if (Objects.nonNull(filterMap.get("isHMRNonCompliant"))) {

			if ("1".equals(filterMap.get("isHMRNonCompliant")))
				filterQuery.append(" and isHMRNonCompliant = 0 ");
			else if ("0".equals(filterMap.get("isHMRNonCompliant")))
				filterQuery.append(" and isHMRNonCompliant = 1 ");
		}
	}

	private void applyIsDeletedFilter(String query, StringBuilder filterQuery, Map<String, String> filterMap) {
		if (Objects.nonNull(filterMap.get("isDeleted"))) {
			filterQuery.append(query);

			if ("1".equals(filterMap.get("isDeleted")))
				filterQuery.append(") as search_table where isDeleted in (1)");
			else if ("0".equals(filterMap.get("isDeleted")))
				filterQuery.append(")  as search_table where isDeleted in (0)");
			else
				filterQuery.append(") as search_table where isDeleted in (0,1)");
		} else {
			filterQuery.append(query);
			filterQuery.append(") as search_table where isDeleted in (0,1)");
		}
	}

	private void applyIsDeletedFiltersForHCPandClinicAdmin(String query, StringBuilder filterQuery,
			Map<String, String> filterMap) {
		if (Objects.nonNull(filterMap.get("isDeleted"))) {
			filterQuery.append(query);

			if ("0".equals(filterMap.get("isDeleted")))
				filterQuery.append(") as search_table where isDeleted in (1)");
			else if ("1".equals(filterMap.get("isDeleted")))
				filterQuery.append(")  as search_table where isDeleted in (0)");
			else
				filterQuery.append(") as search_table where isDeleted in (0,1)");
		} else {
			filterQuery.append(query);
			filterQuery.append(") as search_table where isDeleted in (0,1)");
		}
	}

	public Page<PatientUserVO> findAssociatedPatientToClinicAdminBy(String queryString, Long clinicAdminId,
			String clinicId, String filter, Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery = " select user.id,user.email,user.first_name as firstName,user.last_name as lastName,  user.is_deleted as isUserDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,"
				+ " user.gender,user.title,user.hillrom_id,user.created_date as createdAt, user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence,"
				+ " pc.last_therapy_session_date as last_date, "
				+ " (select  GROUP_CONCAT(clinicc.name) from USER userc  "
				+ " left outer join USER_AUTHORITY user_authorityc on user_authorityc.user_id = userc.id  and user_authorityc.authority_name = '"
				+ PATIENT + "' "
				+ " join USER_PATIENT_ASSOC  upac on userc.id = upac.user_id and upac.relation_label = '" + SELF + "' "
				+ " left outer join PATIENT_INFO patInfoc on upac.patient_id = patInfoc.id  "
				+ " left outer join CLINIC_PATIENT_ASSOC user_clinicc on user_clinicc.patient_id = patInfoc.id   "
				+ " left outer join CLINIC clinicc on user_clinicc.clinic_id = clinicc.id and user_clinicc.patient_id = patInfoc.id "
				+ "  where upac.user_id = user.id  group by patInfoc.id) as clinicname, "
				+ " (select  GROUP_CONCAT(userh.last_name,' ',userh.first_name)  from USER userh  "
				+ " left outer join USER_AUTHORITY user_authorityh on user_authorityh.user_id = userh.id "
				+ " and user_authorityh.authority_name = '" + HCP
				+ "'  join USER_PATIENT_ASSOC  upah on userh.id = upah.user_id " + " and upah.relation_label = '" + HCP
				+ "'  left outer join PATIENT_INFO patInfoh on upah.patient_id = patInfoh.id "
				+ " where patInfo.id = patInfoh.id group by patInfoh.id) as hcpname,patient_clinic.mrn_id as mrnid,"
				+ " patient_clinic.is_active as isDeleted, user.expired as isExpired, pc.is_hmr_compliant as isHMRNonCompliant,"
				+ " pc.is_settings_deviated as isSettingsDeviated, pc.missed_therapy_count as isMissedTherapy from USER user "
				+ " left outer join USER_AUTHORITY user_authority on user_authority.user_id = user.id and user_authority.authority_name = '"
				+ PATIENT + "'" + " join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '"
				+ SELF + "' " + " join PATIENT_INFO patInfo on upa.patient_id = patInfo.id"
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1) "
				+ " join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id "
				+ " join CLINIC clinic on clinic.id = patient_clinic.clinic_id "
				+ " and (lower(user.first_name)  like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString)  or "
				+ " lower(user.email) like lower(:queryString) or "
				+ " lower(CONCAT(user.first_name,' ',user.last_name))  like lower(:queryString) or "
				+ " lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString)  or "
				+ " lower(IFNULL(patient_clinic.mrn_id,0)) like lower(:queryString) or "
				+ " lower(user.hillrom_id) like lower(:queryString)) "

		+ " where lower(IFNULL(clinic.clinic_admin_id,0))= :clinicAdminId ";

		String groupBy = " group by user.id ";
		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString", queryString);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicAdminId", clinicAdminId.toString());

		if (!StringUtils.isEmpty(clinicId)) {
			findPatientUserQuery = findPatientUserQuery.concat(" and clinic.id = '" + clinicId + "'");
		}
		findPatientUserQuery += groupBy;

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFiltersForHCPandClinicAdmin(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		String countSqlQuery = "select count(patientUsers.id) from (" + findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();
		results.stream().forEach((record) -> {
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
			Boolean isActivatedInClinic = (Boolean) record[20];
			Boolean isExpired = (Boolean) record[21];

			java.util.Date localLastTransmissionDate = null;

			if (Objects.nonNull(lastTransmissionDate)) {
				localLastTransmissionDate = lastTransmissionDate;

			}

			LocalDate dobLocalDate = null;
			if (Objects.nonNull(dob)) {
				dobLocalDate = new LocalDate(dob.getTime());
			}

			PatientUserVO patientUserVO = new PatientUserVO(id, email, firstName, lastName,
					isActivatedInClinic ? false : true, zipcode, address, city, dobLocalDate, gender, title, hillromId,
					createdAtDatetime, isActivated, state, Objects.nonNull(adherence) ? adherence : 0,
					localLastTransmissionDate);
			// mrnId,hcpNamesCSV,clinicNamesCSV
			patientUserVO.setMrnId(mrnId);
			patientUserVO.setHcpNamesCSV(hcpNamesCSV);
			patientUserVO.setClinicNamesCSV(clinicNamesCSV);
			patientUserVO.setExpired(isExpired);
			patientUsers.add(patientUserVO);
		});

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(addLatestAdherenceScore(patientUsers), null, count.intValue());

		return page;
	}

	public Page<PatientUserVO> findAssociatedPatientToHCPAndClinicBy(String queryString, Long hcpUserID,
			String clinicId, String filter, Pageable pageable, Map<String, Boolean> sortOrder) {

		String findPatientUserQuery;

		String query1 = "select user.id,user.email,user.first_name as firstName,user.last_name as"
				+ " lastName, user.is_deleted as isDeleted,user.zipcode,patInfo.address,patInfo.city,user.dob,user.gender,"
				+ "user.title,user.hillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state as state, pc.compliance_score adherence, pc.last_therapy_session_date as last_date, "
				+ "pc.is_hmr_compliant as isHMRNonCompliant,pc.is_settings_deviated as isSettingsDeviated,pc.missed_therapy_count as isMissedTherapy "
				+ "from USER user join USER_AUTHORITY user_authority on user_authority.user_id"
				+ " = user.id and user_authority.authority_name = '" + PATIENT + "'and "
				+ "(lower(user.first_name) like lower(:queryString) or "
				+ "lower(user.last_name) like lower(:queryString) or "
				+ "lower(user.email) like lower(:queryString) or "
				+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:queryString) or "
				+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:queryString) or "
				+ "lower(user.hillrom_id) like lower(:queryString)) "
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = '" + SELF + "' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ "join USER_PATIENT_ASSOC upa_hcp on patInfo.id = upa_hcp.patient_id "
				+ " left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1) ";
		String query2 = " where upa_hcp.user_id = :hcpUserID ";

		if (!StringUtils.isEmpty(clinicId)) {
			findPatientUserQuery = query1
					+ " join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = '"
					+ clinicId + "' " + query2;
		} else
			findPatientUserQuery = query1 + query2;

		StringBuilder filterQuery = new StringBuilder();

		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		findPatientUserQuery = findPatientUserQuery.replaceAll(":queryString", queryString);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":hcpUserID", hcpUserID.toString());

		if (!StringUtils.isEmpty(clinicId)) {
			findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicSearch",
					" join CLINIC_PATIENT_ASSOC patient_clinic on patient_clinic.patient_id = patInfo.id and patient_clinic.clinic_id = '"
							+ clinicId + "'");
		}
		String countSqlQuery = "select count(patientUsers.id) from (" + findPatientUserQuery + " ) patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		Query query = getOrderedByQuery(findPatientUserQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = extractPatientSearchResultsToVO(results);

		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(addLatestAdherenceScore(patientUsers), null, count.intValue());

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
			if(!"adherence".equalsIgnoreCase(columnName))
				sb.append("lower(").append(columnName).append(")");
			else
				sb.append(columnName);

			if (columnNames.get(columnName))
				sb.append(" ASC");
			else
				sb.append(" DESC");

			if (i != (limit - 1)) {
				sb.append(", ");
			}
		}
		Query jpaQuery = entityManager.createNativeQuery(queryString + sb.toString());
		return jpaQuery;
	}

	// Patient Search which are not associated with the clinic

	public List<PatientUserVO> findPatientNotAssociatedToClinic(String clinicId, String searchString, String filter) {

		String findPatientUserQuery = "select * from ( select user.id as patient_id,user.email as pemail,user.first_name as pfirstName,"
				+ "user.last_name as plastName, user.is_deleted as isDeleted,user.zipcode as pzipcode,"
				+ "patInfo.address paddress,patInfo.city as pcity,user.dob as pdob,user.gender as pgender,"
				+ "user.title as ptitle,user.hillrom_id as phillrom_id,user.created_date as createdAt,"
				+ "user.activated as isActivated, patInfo.state as state,pc.compliance_score as pcompliance_score, "
				+ "pc.last_therapy_session_date as last_date , user_clinic.mrn_id as mrnid,"
				+ "clinic.id as pclinicid, GROUP_CONCAT(clinic.name) as clinicName from USER user "
				+ "join USER_AUTHORITY user_authority on user_authority.user_id = user.id  and user_authority.authority_name = 'PATIENT' and "
				+ "(lower(user.first_name) like lower(:searchString) or  lower(user.last_name) like lower(:searchString) or   "
				+ "lower(CONCAT(user.first_name,' ',user.last_name)) like lower(:searchString) or "
				+ "lower(CONCAT(user.last_name,' ',user.first_name)) like lower(:searchString)  )"
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id and upa.relation_label = 'Self' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ "left outer join CLINIC_PATIENT_ASSOC user_clinic on " + "user_clinic.patient_id = patInfo.id "
				+ "left outer join PATIENT_COMPLIANCE pc on user.id = pc.user_id AND pc.date=subdate(curdate(),1)  "
				+ "left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id "
				+ " where clinic.id <> ':clinicId' or clinic.id IS NULL "
				+ "group by user.id ) as tble where patient_id not in ( select user.id as patient_id  from USER user "
				+ "join USER_PATIENT_ASSOC  upa on user.id = upa.user_id  and upa.relation_label = 'Self' "
				+ "join PATIENT_INFO patInfo on upa.patient_id = patInfo.id "
				+ "join CLINIC_PATIENT_ASSOC user_clinic on user_clinic.patient_id = patInfo.id "
				+ "left outer join CLINIC clinic on user_clinic.clinic_id = clinic.id and user_clinic.patient_id = patInfo.id "
				+ "where clinic.id = ':clinicId')";

		if (StringUtils.isEmpty(searchString))
			findPatientUserQuery = findPatientUserQuery.replaceAll(":searchString", "");
		else
			findPatientUserQuery = findPatientUserQuery.replaceAll(":searchString", searchString);

		findPatientUserQuery = findPatientUserQuery.replaceAll(":clinicId", clinicId);
		
		StringBuilder filterQuery = new StringBuilder();
		
		if (StringUtils.isNotEmpty(filter) && !"all".equalsIgnoreCase(filter)) {

			Map<String, String> filterMap = getSearchParams(filter);

			filterQuery.append("select * from (");

			applyQueryFilters(findPatientUserQuery, filterQuery, filterMap);

			findPatientUserQuery = filterQuery.toString();
		}

		Query patientQuery = entityManager.createNativeQuery(findPatientUserQuery);

		List<Object[]> results = patientQuery.getResultList();

		List<PatientUserVO> patientUsers = extractPatientSearchResultsToVO(results);
		return patientUsers;
	}

	private Map<String, String> getSearchParams(String filterString) {

		Map<String, String> filterMap = new HashMap<>();

		if (StringUtils.isEmpty(filterString))
			return filterMap;

		String[] filters = filterString.split(";");
		for (String filter : filters) {

			String[] pair = filter.split(":");
			if (pair.length > 1)
				if (!StringUtils.isEmpty(pair[1]))
					filterMap.put(pair[0], pair[1]);
		}
		return filterMap;
	}
	
	
	private List<PatientUserVO> addLatestAdherenceScore(List<PatientUserVO> patientUserVOs){
		
		if(patientUserVOs.isEmpty())
			return patientUserVOs;
		
		String complienceQuery = "select user_id, pc.date, compliance_score from PATIENT_COMPLIANCE pc where pc.user_id in ("+getFlattenedUserIds(patientUserVOs)+") AND pc.date IN (subdate(curdate(),1),curdate()) order by date";
		Query query = entityManager.createNativeQuery(complienceQuery);
		System.out.println("Query :: "+ complienceQuery);
		List<Object[]> results = query.getResultList();
		Map<Long,Integer> userComplianceMap = new HashMap<>();
		results.stream().forEach((record) -> {
			Long id = ((BigInteger) record[0]).longValue();
			Date date = (Date) record[1];
			Integer compliance = (Integer) record[2];
			userComplianceMap.put(id, compliance);
			});
		
			for(PatientUserVO pUser : patientUserVOs){
				Integer adherence = userComplianceMap.get(pUser.getId());
				pUser.setAdherence(Objects.nonNull(adherence) ? adherence : 0);
	    	}
		return patientUserVOs;
	}
	
	
	private String getFlattenedUserIds(List<PatientUserVO> patientUserVO){
		StringBuilder userIdsString = new StringBuilder();
	    if(Objects.isNull(patientUserVO)){
	    	return null;
	    } else {
	    	for(PatientUserVO pUser : patientUserVO){
	    		userIdsString.append(pUser.getId());
	    		userIdsString.append(",");
	    	}
	    	if(userIdsString.indexOf(",") < 0)
	    		return "";
	    	else 
	    		return userIdsString.deleteCharAt(userIdsString.lastIndexOf(",")).toString();
	    }
    }

}
