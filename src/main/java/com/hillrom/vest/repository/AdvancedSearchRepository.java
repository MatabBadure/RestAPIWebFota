package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;

@Repository
public class AdvancedSearchRepository {

	private final Logger log = LoggerFactory
			.getLogger(AdvancedSearchRepository.class);

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
		
		List<String> clinicCity = advancedClinicDTO.getCity();
		List<String> clinicState = advancedClinicDTO.getState();
		String advancedSearchClinicsQuery = "";
		String whereClause = " WHERE ";
		StringBuilder filter = new StringBuilder();
	
		StringBuilder finalQuery = new StringBuilder();
		String value = "";
		
		try{

		advancedSearchClinicsQuery = "SELECT clinic.id as id, " + 
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
										    " LEFT OUTER JOIN CITY_STATE_ZIP_MAP city_state_zip_map on clinic.zipcode =  city_state_zip_map.zip";
		
		
											
											if(!StringUtils.isBlank(advancedClinicDTO.getName())){
												filter = filter.append("clinic.name like").append(" '%").append(advancedClinicDTO.getName()).append("%' ");
											}
										    
											filter= (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getClinicType())) ? (filter.append(" AND ")) : (filter.append(""));
											if(!StringUtils.isBlank(advancedClinicDTO.getClinicType())){
												if(advancedClinicDTO.getClinicType().equalsIgnoreCase("Parent")){
													filter = filter.append("clinic.is_parent =").append(" 1 ");
												}
												else if(advancedClinicDTO.getClinicType().equalsIgnoreCase("Satellite")){
													filter = filter.append("clinic.is_parent =").append(" 0 ");
												}
												else {
													filter = filter.append("clinic.is_parent like").append(" '%%' ");
												}
											}
											
											filter= (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getSpecialty())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!StringUtils.isBlank(advancedClinicDTO.getSpecialty())){
										    	filter = filter.append("clinic.speciality like").append(" '%").append(advancedClinicDTO.getSpecialty()).append("%' ");
										    }
										    
										    filter= (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getCountry())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!StringUtils.isBlank(advancedClinicDTO.getCountry())){
										    	filter = filter.append("city_state_zip_map.country like").append(" '%").append(advancedClinicDTO.getCountry()).append("%' ");
										    }
										    
										    
										    filter= (filter.length()>0) &&((advancedClinicDTO.getState())!=null) ? (filter.append(" AND ")) : (filter.append(""));
										    if(advancedClinicDTO.getState() != null){
										    	String csvStates = String.join(",", advancedClinicDTO.getState());
										    	filter = filter.append("clinic.state IN(").append(csvStates).append(") ");
										    }
										    
										    
										    filter= (filter.length()>0) &&((advancedClinicDTO.getCity())!=null) ? (filter.append(" AND ")) : (filter.append(""));
										    if(advancedClinicDTO.getCity()!=null){
										    	String csvCities = String.join(",", advancedClinicDTO.getCity());
										    	filter = filter.append("clinic.city IN(").append(csvCities).append(") ");
										    }
										    
										    /*filter= (filter.length()>0) &&(!Objects.isNull((advancedClinicDTO.getState())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!StringUtils.isBlank(advancedClinicDTO.getState())){
										    	filter = filter.append("clinic.state like").append(" '%").append(advancedClinicDTO.getState()).append("%' ");
										    }
										    
										    filter= (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getCity())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!StringUtils.isBlank(advancedClinicDTO.getCity())){
										    	filter = filter.append("clinic.city like").append(" '%").append(advancedClinicDTO.getCity()).append("%' ");
										    }*/
										    
										    filter= (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getZipcode())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!Objects.isNull(advancedClinicDTO.getZipcode())){
										    	filter = filter.append("clinic.zipcode like ").append(" '%").append(advancedClinicDTO.getZipcode()).append("%' ");
										    }
										    
										    
										    filter= (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getStatus())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!StringUtils.isBlank(advancedClinicDTO.getStatus())){
										    	if(advancedClinicDTO.getStatus().equalsIgnoreCase("All"))
										    	{
										    		filter = filter.append("clinic.is_deleted like").append(" '%%' ");
										    	}
										    	else if(advancedClinicDTO.getStatus().equalsIgnoreCase("Active"))
										    	filter = filter.append("clinic.is_deleted =").append(" 0 ");
										    	else if(advancedClinicDTO.getStatus().equalsIgnoreCase("Inactive"))
											    	filter = filter.append("clinic.is_deleted =").append(" 1 ");
										    }
										    
										    filter= (filter.length()>0)&&(!Objects.isNull(advancedClinicDTO.getAdherenceWindowSelected())) ? (filter.append(" AND ")) : (filter.append(""));
										    if(!StringUtils.isBlank(advancedClinicDTO.getAdherenceWindowSelected())){
										    	filter = filter.append("clinic.adherence_setting = ").append(advancedClinicDTO.getAdherenceWindowSelected());
										    }
										    
										    
		finalQuery=finalQuery.append(advancedSearchClinicsQuery);							    
		if(filter.length()>0){
			finalQuery.append(whereClause);
			finalQuery.append(filter);
		}

		log.debug("Query : " + finalQuery);
		
		String countSqlQuery = "select count(hillromAdvancedSearchClinics.id) from (" + finalQuery + ") hillromAdvancedSearchClinics";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		
		Query query = getOrderedByQuery(finalQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<ClinicVO> advancedSearchClinicsList = new ArrayList<>();
		List<Object[]> results = query.getResultList();
		results.stream().forEach((record) -> {
			String id = ((String) record[0]);
			String name = (String) record[1];
			String address = (String) record[2];
			String zipcode = (String) record[3];
			String country = (String) record[4];
			String city = (String) record[5];
			String state = (String) record[6];
			String phoneNumber = (String) record[7];
			String faxNumber = (String) record[8];
			String hillromId = (String) record[9];
			String parentClinicId = (String) record[10];
			Boolean deleted = (Boolean) record[11];
			Boolean	parent = (Boolean) record[12];
			String clinicAdminId = Objects.nonNull(record[13])?record[13].toString():null; 
//			Long clinicAdminId= ((BigInteger) record[0]).longValue();
			DateTime createdAt = new DateTime(record[14]);
			String address2 = (String) record[15];
			String speciality = (String) record[16];
			Integer adherenceSetting = (Integer) record[17];
			DateTime adherenceSettingModifiedDte = new DateTime( record[18]);

				    
			ClinicVO advancedSearchClinicVO = new ClinicVO(id,name, address, address2, zipcode, city,
					state, phoneNumber, faxNumber, speciality, clinicAdminId,
					parent, hillromId,deleted,createdAt,adherenceSetting, adherenceSettingModifiedDte);
			
			advancedSearchClinicsList.add(advancedSearchClinicVO); 
			
		});
		
		Page<ClinicVO> page = new PageImpl<ClinicVO>(advancedSearchClinicsList, null, count.intValue());

		return page;
		}
		
		
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	// catch block should come here

	// SELECT cl.name,
	// ux.speciality,ux.credentials,cszm.country,ux.city,ux.state,us.zipcode,us.is_deleted
	// from hillromvest_prod.USER us
	// left outer join hillromvest_prod.USER_EXTENSION ux on us.id = ux.user_id
	// left outer join hillromvest_prod.CLINIC_USER_ASSOC cua on cua.users_id =
	// us.id
	// left outer join hillromvest_prod.CLINIC cl on cl.id = cua.clinics_id
	// left outer join hillromvest_prod.USER_AUTHORITY ua on ua.user_id = us.id
	// left outer join hillromvest_prod.CITY_STATE_ZIP_MAP cszm on us.zipcode =
	// cszm.zip
	// where ua.authority_name = 'HCP' AND
	// cl.name IS null or cl.name like '%%' AND
	// ux.speciality IS null or ux.speciality like '%%' AND
	// ux.credentials IS null or ux.credentials like '%%' AND
	// cszm.country like '%%' AND
	// ux.city like '%%' AND
	// ux.state like '%%' AND
	// us.zipcode like '%%' AND
	// us.is_deleted in (false,true)

	private void setPaginationParams(Pageable pageable, Query query) {

		int firstResult = pageable.getOffset();
		int maxResult = pageable.getPageSize();
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
	}

	private Query getOrderedByQuery(StringBuilder finalQuery,
			Map<String, Boolean> columnNames) {

		StringBuilder sb = new StringBuilder();
		// Append order by only if there is any sort request.
		if (columnNames.keySet().size() > 0) {
			sb.append(ORDER_BY_CLAUSE_START);
		}

		int limit = columnNames.size();
		int i = 0;
		for (String columnName : columnNames.keySet()) {
			if (!Constants.ADHERENCE.equalsIgnoreCase(columnName))
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

		log.debug("Search Query :: " + finalQuery + sb.toString());

		Query jpaQuery = entityManager.createNativeQuery(finalQuery
				+ sb.toString());
		return jpaQuery;
	}
}