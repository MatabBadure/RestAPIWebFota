package com.hillrom.vest.repository;

import static com.hillrom.vest.config.AdherenceScoreConstants.DEFAULT_SETTINGS_DEVIATION_COUNT;
import static com.hillrom.vest.config.AdherenceScoreConstants.ADHERENCE_SETTING_DEFAULT_DAYS;
import static com.hillrom.vest.security.AuthoritiesConstants.CLINIC_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.HCP;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;
import static com.hillrom.vest.util.RelationshipLabelConstants.SELF;

import java.math.BigInteger;
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

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.Authority;
import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserPatientAssoc;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.util.QueryConstants;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.util.RelationshipLabelConstants;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HcpVO;
import com.hillrom.vest.web.rest.dto.HillRomUserVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;

@Repository
public class AdvancedSearchRepository {
	
	private final Logger log = LoggerFactory.getLogger(AdvancedSearchRepository.class);

	private static final String ORDER_BY_CLAUSE_START = " order by ";
	
	@Inject
	private EntityManager entityManager;

	@Inject
	private HCPClinicService hcpClinicService;
	
	@Inject
	private UserPatientRepository userPatientRepository;
	
	@Inject
	private AuthorityRepository authorityRepository;
	

	public Page<ClinicVO> advancedSearchClinics(AdvancedClinicDTO advancedClinicDTO, Pageable pageable,Map<String, Boolean> sortOrder) throws HillromException {

		String advancedSearchClinicsQuery = "SELECT clinic.id as id, " + 
											"clinic.name as name, "+
										    "clinic.address as address, "+
										    "clinic.zipcode as zipcode, "+
										    "city_state_zip_map.country as country, "+
										    "clinic.city as city, "+
										    "clinic.state as state, "+
										    "clinic.phone_number as phone, "+
										    "clinic.fax_number as fax, "+
										    "clinic.hillrom_id as hillromId, "+
										    "clinic.parent_clinic_id as parentClinicId, "+
										    "clinic.is_deleted as deletedFlag, "+
										    "clinic.is_parent as parentFlag, "+
										    "clinic.clinic_admin_id as clinicAdminId, "+
										    "clinic.created_date as createdDate, "+
										    "clinic.address2 as address2, "+
										    "clinic.speciality as speciality, "+
										    "clinic.adherence_setting as adherenceSetting, "+
										    "clinic.adherenceSetting_modified_date as adherenceSettingModDate"+
										    " FROM CLINIC clinic"+
										    " LEFT OUTER JOIN CITY_STATE_ZIP_MAP city_state_zip_map on clinic.zipcode =  city_state_zip_map.zip"+
										    " WHERE clinic.name like %"+ advancedClinicDTO.getName() + "%"+
										    " AND clinic.parentFlag in " + (advancedClinicDTO.getClinicType().equalsIgnoreCase("All")?"(true,false)":advancedClinicDTO.getClinicType().equalsIgnoreCase("Parent")?"true":"false") +
										    " AND clinic.speciality like %"+ advancedClinicDTO.getSpecialty() + "%"+ 
										    " AND clinic.city like %"+ advancedClinicDTO.getCity() + "%"+
										    " AND clinic.state like %"+ advancedClinicDTO.getState() + "%"+
										    " AND clinic.zipcode like %"+ advancedClinicDTO.getZipcode() + "%"+
										    " AND city_state_zip_map.country like %"+ advancedClinicDTO.getCountry() + "%"+
										    " AND clinic.deletedFlag in " + (advancedClinicDTO.getStatus().equalsIgnoreCase("All")?"(true,false)":advancedClinicDTO.getStatus().equalsIgnoreCase("Inactive")?"true":"false") +
										    " AND clinic.adherenceSetting in ("+ advancedClinicDTO.getAdherenceWindowSelected() + ")";                                                                    
		                                                                                                        
				

		log.debug("Query : " + advancedSearchClinicsQuery);
		
		String countSqlQuery = "select count(hillromUsers.id) from (" + advancedSearchClinicsQuery + ") hillromAdvancedSearchClinics";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		
		Query query = getOrderedByQuery(advancedSearchClinicsQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<ClinicVO> advancedSearchClinicsList = new ArrayList<>();
		List<Object[]> results = query.getResultList();
		results.stream().forEach((record) -> {
			String id = ((String) record[0]);
			String name = (String) record[1];
			String address = (String) record[2];
			Integer zipcode = ((Integer) record[3]);
			String country = (String) record[4];
			String city = (String) record[5];
			String state = (String) record[6];
			String phoneNumber = (String) record[7];
			String faxNumber = (String) record[8];
			String hillromId = (String) record[9];
			String parentClinicId = (String) record[10];
			Boolean deleted = (Boolean) record[11];
			Boolean	parent = (Boolean) record[12];
			Long clinicAdminId = ((BigInteger) record[13]).longValue(); 
			DateTime createdAt = new DateTime(record[14]);
			String address2 = (String) record[15];
			String speciality = (String) record[16];
			Integer adherenceSetting = (Integer) record[17];
			DateTime adherenceSettingModifiedDte = new DateTime( record[18]);

				    
			ClinicVO advancedSearchClinicVO = new ClinicVO(id,name,address,address2,zipcode,city,state,country,phoneNumber, faxNumber, speciality, clinicAdminId,
					parent, parentClinicId,hillromId,deleted,createdAt,adherenceSetting, adherenceSettingModifiedDte);
			advancedSearchClinicsList.add(advancedSearchClinicVO);
		});

		Page<ClinicVO> page = new PageImpl<ClinicVO>(advancedSearchClinicsList, null, count.intValue());

		return page;
	}
	 
	
//	SELECT cl.name, ux.speciality,ux.credentials,cszm.country,ux.city,ux.state,us.zipcode,us.is_deleted 
//	from hillromvest_prod.USER us
//	left outer join hillromvest_prod.USER_EXTENSION ux on us.id = ux.user_id
//	left outer join hillromvest_prod.CLINIC_USER_ASSOC cua on cua.users_id = us.id
//	left outer join hillromvest_prod.CLINIC cl on cl.id = cua.clinics_id
//	left outer join hillromvest_prod.USER_AUTHORITY ua on ua.user_id = us.id
//	left outer join hillromvest_prod.CITY_STATE_ZIP_MAP cszm on us.zipcode = cszm.zip
//	where ua.authority_name = 'HCP' AND
//	cl.name IS null or cl.name like '%%' AND
//	ux.speciality IS null or ux.speciality  like '%%' AND
//	ux.credentials IS null or ux.credentials  like '%%' AND
//	cszm.country  like '%%' AND
//	ux.city  like '%%' AND
//	ux.state  like '%%' AND
//	us.zipcode  like '%%' AND
//	us.is_deleted in (false,true)
	
	private void setPaginationParams(Pageable pageable, Query query) {

		int firstResult = pageable.getOffset();
		int maxResult = pageable.getPageSize();
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
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
