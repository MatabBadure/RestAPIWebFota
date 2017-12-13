package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.util.QueryConstants;
import com.hillrom.vest.service.HCPClinicService;
import com.hillrom.vest.web.rest.dto.AdvancedClinicDTO;
import com.hillrom.vest.web.rest.dto.AdvancedHcpDTO;
import com.hillrom.vest.web.rest.dto.AdvancedPatientDTO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HcpVO;
import com.hillrom.vest.web.rest.dto.PatientUserVO;

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
	private HillromTypeCodeFormatRepository hillromTypeCodeFormatRepository;

	/*
	 * This method takes the DTO object, pageable and sortOrder paramters
	 * Returns Page<ClinicVO>
	 */
	public Page<ClinicVO> advancedSearchClinics(AdvancedClinicDTO advancedClinicDTO, Pageable pageable,Map<String, Boolean> sortOrder) throws HillromException {
		
		String advancedSearchClinicsQuery = QueryConstants.QUERY_ADVANCED_CLINIC_SEARCH_FOR_ALL_DEVICETYPE_HILLROM_LOGIN;
		String whereClause = " WHERE ";
		StringBuilder filter = new StringBuilder();
		StringBuilder finalQuery = new StringBuilder();
		
		try{

		/*Query formation
		 * filter: used to append the values from AdvancedClinicDTO
		*/
			if(!StringUtils.isBlank(advancedClinicDTO.getClinicName())){
				filter = filter.append("clinic.name like").append(" '%").append(advancedClinicDTO.getClinicName()).append("%' "); 
			}
										    
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getClinicType())) ? (filter.append(" AND ")) : (filter.append(""));
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
											
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getClinicSpecialty())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedClinicDTO.getClinicSpecialty())){
				filter = filter.append("clinic.speciality like").append(" '%").append(advancedClinicDTO.getClinicSpecialty()).append("%' ");
			}
										    
			filter = (filter.length()>0)&&(advancedClinicDTO.getCountry().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
			if(advancedClinicDTO.getCountry().size()>0){
				String csvCountries = String.join("','", advancedClinicDTO.getCountry());  //Forming the comma & single quote separated list
			   	filter = filter.append("city_state_zip_map.country IN ('").append(csvCountries).append("') ");
			}
										    
										    
			filter = (filter.length()>0) &&(advancedClinicDTO.getState().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
			if(advancedClinicDTO.getState().size()>0){
				filter = filter.append("clinic.state IN ('").append(commaSeparatedValues(advancedClinicDTO.getState())).append("') ");
			}
										    
			filter = (filter.length()>0) &&(advancedClinicDTO.getCity().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
			if(advancedClinicDTO.getCity().size()>0){
				filter = filter.append("clinic.city IN ('").append(commaSeparatedValues(advancedClinicDTO.getCity())).append("') ");
			}
										    
										    
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getZipcode())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedClinicDTO.getZipcode())){
				filter = filter.append("clinic.zipcode like ").append("'%").append(advancedClinicDTO.getZipcode()).append("%' ");
			}
										    
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getClinicStatus())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedClinicDTO.getClinicStatus())){
				if(advancedClinicDTO.getClinicStatus().equalsIgnoreCase("All")){
					filter = filter.append("clinic.is_deleted IN(0,1)");
				}
				else if(advancedClinicDTO.getClinicStatus().equalsIgnoreCase("Active"))
					filter = filter.append("clinic.is_deleted =").append(" 0 ");
				else if(advancedClinicDTO.getClinicStatus().equalsIgnoreCase("Inactive"))
					filter = filter.append("clinic.is_deleted =").append(" 1 ");
			}
										    
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedClinicDTO.getAdherenceWindowSelected())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedClinicDTO.getAdherenceWindowSelected())){
				filter = filter.append("clinic.adherence_setting = ").append(advancedClinicDTO.getAdherenceWindowSelected());
			}
										    
										    
		finalQuery = finalQuery.append(advancedSearchClinicsQuery);
		//checking if the filter object is null , if not finalQuery = where+filter 
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
			DateTime createdAt = new DateTime(record[14]);
			String address2 = (String) record[15];
			String speciality = (String) record[16];
			Integer adherenceSetting = (Integer) record[17];
			DateTime adherenceSettingModifiedDte = new DateTime( record[18]);

				    
			ClinicVO advancedSearchClinicVO = new ClinicVO(id, name, address,address2, zipcode, country, city, state,
					phoneNumber, faxNumber, speciality,	clinicAdminId, parent, parentClinicId, hillromId,
					deleted, createdAt, adherenceSetting, adherenceSettingModifiedDte);
			
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
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
	
	// Advanced Patient Search for Admin log in
	public Page<PatientUserVO> advancedSearchPatients(AdvancedPatientDTO advancedPatientDTO, Pageable pageable,Map<String, Boolean> sortOrder) {      
		
		//String BaseQuery = "select * from ("+QueryConstants.QUERY_ADVANCED_PATIENT_SEARCH_FOR_ALL_DEVICETYPE_HILLROM_LOGIN+") kt";
		String BaseQuery = QueryConstants.QUERY_ADVANCED_PATIENT_SEARCH_FOR_ALL_DEVICETYPE_HILLROM_LOGIN_UNIONS;
		String age = "TIMESTAMPDIFF(YEAR,kt.pdob,CURDATE())"; // used to calculate age from dob field
		String whereClause = " WHERE ";
		StringBuilder filter = new StringBuilder();
		StringBuilder finalQuery = new StringBuilder();
		
		try{
			

		/*Query formation
		 * filter: used to append the values from AdvancedPatientDTO
		 */
		if(!StringUtils.isBlank(advancedPatientDTO.getName())){
			filter = filter.append("(kt.pfirstName like ").append("'%").append(advancedPatientDTO.getName()).append("%'"); 
			filter = filter.append(" OR ").append("kt.plastName like ").append("'%").append(advancedPatientDTO.getName()).append("%') ");
		}
	    
		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getHillromId())) ? (filter.append(" AND ")) : (filter.append(""));
		if(!StringUtils.isBlank(advancedPatientDTO.getHillromId())){
	    	filter = filter.append("kt.phillrom_id like ").append("'%").append(advancedPatientDTO.getHillromId()).append("%' ");
	    }
		
		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getEmail())) ? (filter.append(" AND ")) : (filter.append(""));
		if(!StringUtils.isBlank(advancedPatientDTO.getEmail())){
	    	filter = filter.append("kt.pemail like ").append("'%").append(advancedPatientDTO.getEmail()).append("%' ");
	    }
		
		if(!advancedPatientDTO.getGender().equalsIgnoreCase("All")){
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getGender())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedPatientDTO.getGender())){
				if(advancedPatientDTO.getGender().equalsIgnoreCase("Male"))
					filter = filter.append("kt.pgender IN ").append("('Male')");
				else if(advancedPatientDTO.getGender().equalsIgnoreCase("Female"))
					filter = filter.append("kt.pgender IN ").append("('Female')");
				else if(advancedPatientDTO.getGender().equalsIgnoreCase("Other"))
					filter = filter.append("kt.pgender IN ").append("('Other')");
			}
		}
	    
	    filter = (filter.length()>0)&&(advancedPatientDTO.getAge().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
	    if(advancedPatientDTO.getAge().size()>0){
	    	filter = filter.append("(").append(age).append(rangeBuilder(advancedPatientDTO.getAge(),"age")).append(")"); 
	    	// rangeBuilder method used to form the where clause for age 
	    }
	   
	    filter = (filter.length()>0)&&(advancedPatientDTO.getCountry().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
		if(advancedPatientDTO.getCountry().size()>0){
			String csvCountries = String.join("','", advancedPatientDTO.getCountry());  //Forming the comma & single quote separated list
		   	filter =filter.append("kt.pcountry IN ('").append(csvCountries).append("') ");
		}
	    
	    filter = (filter.length()>0) &&(advancedPatientDTO.getState().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
	    if(advancedPatientDTO.getState().size()>0){
	    	filter = filter.append("kt.state IN ('").append(commaSeparatedValues(advancedPatientDTO.getState())).append("') ");
	    }
	    
	    filter = (filter.length()>0) &&(advancedPatientDTO.getCity().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
	    if(advancedPatientDTO.getCity().size()>0){
	    	filter = filter.append("kt.pcity IN ('").append(commaSeparatedValues(advancedPatientDTO.getCity())).append("') ");
	    }
	    
	    
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getZipcode())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getZipcode())){
	    	filter = filter.append("kt.pzipcode like").append(" '%").append(advancedPatientDTO.getZipcode()).append("%' ");
	    }
		
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getClinicLevelStatus())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getClinicLevelStatus())){
	    	if(advancedPatientDTO.getClinicLevelStatus().equalsIgnoreCase("All"))
	    	{
	    		filter = filter.append("kt.isDeleted IN (true,false)");
	    	}
	    	else if(advancedPatientDTO.getClinicLevelStatus().equalsIgnoreCase("Active"))
	    	filter = filter.append("kt.isDeleted IN ").append("(false)");
	    	else if(advancedPatientDTO.getClinicLevelStatus().equalsIgnoreCase("Inactive"))
		    	filter = filter.append("kt.isDeleted IN ").append("(true)");	
	    }
		
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getDiagnosis())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getDiagnosis())){
	    	filter = filter.append("(kt.diag1 like '%").append(advancedPatientDTO.getDiagnosis()).append("%' ").append("OR kt.diag2 like '%")
	    			.append(advancedPatientDTO.getDiagnosis()).append("%' ").append("OR kt.diag3 like '%").append(advancedPatientDTO.getDiagnosis()).append("%' ")
	    			.append("OR kt.diag4 like '%").append(advancedPatientDTO.getDiagnosis()).append("%') ");
	    }
	    
	    filter= (filter.length()>0)&&(advancedPatientDTO.getAdherenceScoreRange().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
	    if(advancedPatientDTO.getAdherenceScoreRange().size()>0){
	    	filter = filter.append("(kt.adherence").append(rangeBuilder(advancedPatientDTO.getAdherenceScoreRange(),"adherence")).append(")");
	    	// rangeBuilder method used to form the where clause for adherence 
	    }

	    if(!advancedPatientDTO.getDeviceType().equalsIgnoreCase("All")){
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getDeviceType())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getDeviceType())){
	    	if(advancedPatientDTO.getDeviceType().equalsIgnoreCase("VEST"))
	    		filter = filter.append("kt.devType IN ('ALL','VEST') ");
	    	else if(advancedPatientDTO.getDeviceType().equalsIgnoreCase("MONARCH"))
		    	filter = filter.append("kt.devType IN ('ALL','MONARCH') ");
	    	}
	    }
	    
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getDeviceStatus())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getDeviceStatus())){
	    	if(advancedPatientDTO.getDeviceStatus().equalsIgnoreCase("All"))
	    	{
	    		filter = filter.append("(kt.deviceActiveInactive IN (false,true) OR kt.deviceActiveInactive IS NULL)");
	    	}
	    	else if(advancedPatientDTO.getDeviceStatus().equalsIgnoreCase("Active"))
	    	filter = filter.append("kt.deviceActiveInactive IN (true)");
	    	else if(advancedPatientDTO.getDeviceStatus().equalsIgnoreCase("Inactive"))
		    	filter = filter.append("kt.deviceActiveInactive IN (false)");	
	    }
	    
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateFrom()) 
				|| !StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateTo())) ? (filter.append(" AND ")) : (filter.append(""));
				if(!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateFrom())||!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateTo())){
					if(!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateFrom())&&!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateTo())){
						filter = filter.append("(date(kt.activeDeviceAddedDate) BETWEEN '").append(dateFormat(advancedPatientDTO.getDeviceActiveDateFrom())).append("'")
								.append(" AND ").append("'").append(dateFormat(advancedPatientDTO.getDeviceActiveDateTo())).append("')");
					}
					else if(!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateFrom())){
						filter = filter.append("date(kt.activeDeviceAddedDate) >= '").append(dateFormat(advancedPatientDTO.getDeviceActiveDateFrom())).append("' ");
					}
					else if(!StringUtils.isBlank(advancedPatientDTO.getDeviceActiveDateTo())){
						filter = filter.append("date(kt.activeDeviceAddedDate) <= '").append(dateFormat(advancedPatientDTO.getDeviceActiveDateTo())).append("' ");
					}
				}
	    
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getSerialNo())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getSerialNo())){
	    	filter = filter.append("kt.deviceSerialNumber like '%").append(advancedPatientDTO.getSerialNo()).append("%' ");
	    }
	    
	    filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getMinHMRRange()) 
	    					|| !StringUtils.isBlank(advancedPatientDTO.getMaxHMRRange())) ? (filter.append(" AND ")) : (filter.append(""));
	    if(!StringUtils.isBlank(advancedPatientDTO.getMinHMRRange())||!StringUtils.isBlank(advancedPatientDTO.getMaxHMRRange())){
	    	if(!StringUtils.isBlank(advancedPatientDTO.getMinHMRRange())&&!StringUtils.isBlank(advancedPatientDTO.getMaxHMRRange())){
	    	filter = filter.append("(kt.hmr BETWEEN '").append(Integer.parseInt(advancedPatientDTO.getMinHMRRange())*60).append("'") //converting hmr mins into seconds
	    			 .append(" AND ").append("'").append(Integer.parseInt(advancedPatientDTO.getMaxHMRRange())*60).append("')");
	    	}
	    	else if(!StringUtils.isBlank(advancedPatientDTO.getMinHMRRange())){
	    		filter = filter.append("kt.hmr >= ").append(Integer.parseInt(advancedPatientDTO.getMinHMRRange())*60);
		    }
		    else if(!StringUtils.isBlank(advancedPatientDTO.getMaxHMRRange())){
		    	filter = filter.append("kt.hmr <= ").append(Integer.parseInt(advancedPatientDTO.getMaxHMRRange())*60);
		    }
	    }
	    
	    if(!advancedPatientDTO.getAdherenceReset().equalsIgnoreCase("All")){
	    		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getAdherenceReset())) ? (filter.append(" AND ")) : (filter.append(""));
	    	if(advancedPatientDTO.getAdherenceReset().equalsIgnoreCase("Yes"))
	    		filter = filter.append("kt.adherenceReset IS NOT NULL");
	    	else if(advancedPatientDTO.getAdherenceReset().equalsIgnoreCase("No"))
		    	filter = filter.append("kt.adherenceReset IS NULL");	
	    }
	    
	    if(!advancedPatientDTO.getNoTransmissionRecorded().equalsIgnoreCase("All")){
    		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getNoTransmissionRecorded())) ? (filter.append(" AND ")) : (filter.append(""));
    		if(advancedPatientDTO.getNoTransmissionRecorded().equalsIgnoreCase("Yes"))
    			filter = filter.append("kt.transmissionRecorded IS NULL");
    		else if(advancedPatientDTO.getNoTransmissionRecorded().equalsIgnoreCase("No"))
    			filter = filter.append("kt.transmissionRecorded IS NOT NULL");	
	    }
	    

	    if(!advancedPatientDTO.getBelowFrequencySetting().equalsIgnoreCase("All")){
    		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getBelowFrequencySetting())) ? (filter.append(" AND ")) : (filter.append(""));
    		if(advancedPatientDTO.getBelowFrequencySetting().equalsIgnoreCase("Yes"))
    			filter = filter.append("kt.isSettingsDeviated = 1");
    		else if(advancedPatientDTO.getBelowFrequencySetting().equalsIgnoreCase("No"))
    			filter = filter.append("(kt.isSettingsDeviated IS NULL OR kt.isSettingsDeviated = 0)");	
	    }
	    
	    if(!advancedPatientDTO.getBelowTherapyMin().equalsIgnoreCase("All")){
    		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getBelowTherapyMin())) ? (filter.append(" AND ")) : (filter.append(""));
    		if(advancedPatientDTO.getBelowTherapyMin().equalsIgnoreCase("Yes"))
    			filter = filter.append("kt.isHMRNonCompliant = 1");
    		else if(advancedPatientDTO.getBelowTherapyMin().equalsIgnoreCase("No"))
    			filter = filter.append("(kt.isHMRNonCompliant IS NULL OR kt.isHMRNonCompliant = 0)");	
	    }
	    
	    if(!advancedPatientDTO.getMissedTherapyDays().equalsIgnoreCase("All")){
    		filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedPatientDTO.getMissedTherapyDays())) ? (filter.append(" AND ")) : (filter.append(""));
    		if(advancedPatientDTO.getMissedTherapyDays().equalsIgnoreCase("Yes"))
    			filter = filter.append("kt.isMissedTherapy = 1");
    		else if(advancedPatientDTO.getMissedTherapyDays().equalsIgnoreCase("No"))
    			filter = filter.append("(kt.isMissedTherapy IS NULL OR kt.isMissedTherapy = 0)");	
	    }
	    
	    finalQuery = finalQuery.append(BaseQuery);
		//checking if the filter object is null , if not finalQuery = where+filter 
		if(filter.length()>0){
			finalQuery.append(whereClause);
			finalQuery.append(filter);
		}

		log.debug("Advanced Patient Search Query : " + finalQuery);

		String countSqlQuery = "select count(patientUsers.patientId) from ( " + finalQuery + "  )  patientUsers";

		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		BigInteger count = (BigInteger) countQuery.getSingleResult();

		
		Query query = getOrderedByQuery(finalQuery, sortOrder);
		setPaginationParams(pageable, query);

		List<Object[]> results = query.getResultList();

		List<PatientUserVO> patientUsers = new LinkedList<>();

		results.stream().forEach((record) -> {
			Long id = ((BigInteger) record[1]).longValue();
			String email = (String) record[2];
			String firstName = (String) record[3];
			String lastName = (String) record[4];
			Boolean isDeleted = (Boolean) record[5];
			String zipcode = (String) record[6];
			String address = (String) record[7];
			String city = (String) record[8];
			Date dob = (Date) record[9];
			String gender = (String) record[10];
			String title = (String) record[11];
			String hillromId = (String) record[12];
			Timestamp createdAt = (Timestamp) record[13];
			Boolean isActivated = (Boolean) record[14];
			DateTime createdAtDatetime = new DateTime(createdAt);
			String state = (String) record[15];
			Integer adherence = (Integer) record[16];
			Date lastTransmissionDate = (Date) record[36];
			String mrnId = (String) record[18];
			String hcpNamesCSV = (String) record[19];
			String clinicNamesCSV = (String) record[20];
			Boolean isExpired = (Boolean) record[21];
			String devType = (String) record[26];

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
					Objects.nonNull(adherence) ? adherence : 0, localLastTransmissionDate,devType);
			// mrnId,hcpNamesCSV,clinicNamesCSV
			patientUserVO.setMrnId(mrnId);
			patientUserVO.setHcpNamesCSV(hcpNamesCSV);
			patientUserVO.setClinicNamesCSV(clinicNamesCSV);
			patientUserVO.setExpired(isExpired);
			patientUsers.add(patientUserVO);
		});
		Page<PatientUserVO> page = new PageImpl<PatientUserVO>(patientUsers, null, count.intValue());

		return page;
	}
	catch(Exception e){
		e.printStackTrace();
	}
	return null;
}
	
	//----------------------------------------------------------------------------------------------------------------------------------------------
	

	public Page<HcpVO> advancedSearchHcps(AdvancedHcpDTO advancedHcpDTO,

			Pageable pageable, Map<String, Boolean> sortOrder) {
		
		String baseQuery = QueryConstants.QUERY_ADVANCED_HCP_SEARCH_FOR_ALL_DEVICETYPE_HILLROM_LOGIN;
		String whereClause = " WHERE ";
		StringBuilder filter = new StringBuilder();
		StringBuilder finalQuery = new StringBuilder();

		
		try{
		

			 
			if(!StringUtils.isBlank(advancedHcpDTO.getName())){
				filter = filter.append("(kt.firstName like ").append("'%").append(advancedHcpDTO.getName()).append("%'"); 
				filter = filter.append(" OR ").append("kt.lastName like ").append("'%").append(advancedHcpDTO.getName()).append("%') ");
			} 
			
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedHcpDTO.getSpecialty())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedHcpDTO.getSpecialty())){
		    	filter = filter.append("kt.speciality like ").append("'%").append(advancedHcpDTO.getSpecialty()).append("%' ");
		    }
			
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedHcpDTO.getCredentials())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedHcpDTO.getCredentials())){
		    	filter = filter.append("kt.credentials like ").append("'%").append(advancedHcpDTO.getCredentials()).append("%' ");
		    }
			
			filter = (filter.length()>0)&&(advancedHcpDTO.getCountry().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
			if(advancedHcpDTO.getCountry().size()>0){
				String csvCountries = String.join("','", advancedHcpDTO.getCountry());  //Forming the comma & single quote separated list
				filter = filter.append("kt.country IN ('").append(csvCountries).append("') ");
			}
			
			filter = (filter.length()>0)&&(advancedHcpDTO.getState().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
			if(advancedHcpDTO.getState().size()>0){
			    	filter = filter.append("kt.hstate IN ('").append(commaSeparatedValues(advancedHcpDTO.getState())).append("') ");
		    }
			
			filter = (filter.length()>0)&&(advancedHcpDTO.getCity().size()>0) ? (filter.append(" AND ")) : (filter.append(""));
			if(advancedHcpDTO.getCity().size()>0){
			    	filter = filter.append("kt.hcity IN ('").append(commaSeparatedValues(advancedHcpDTO.getState())).append("') ");
		    }
			
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedHcpDTO.getZipcode())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedHcpDTO.getZipcode())){
		    	filter = filter.append("kt.zipcode like ").append("'%").append(advancedHcpDTO.getZipcode()).append("%' ");
		    }
			
			filter = (filter.length()>0)&&(!StringUtils.isBlank(advancedHcpDTO.getStatus())) ? (filter.append(" AND ")) : (filter.append(""));
			if(!StringUtils.isBlank(advancedHcpDTO.getStatus())){
				if(advancedHcpDTO.getStatus().equalsIgnoreCase("All")){
					filter = filter.append("kt.isDeleted IN (true,false) ");
				}
				else if(advancedHcpDTO.getStatus().equalsIgnoreCase("Active"))
					filter = filter.append("kt.isDeleted IN ").append("(false) ");
				else if(advancedHcpDTO.getStatus().equalsIgnoreCase("Inactive"))
					filter = filter.append("kt.isDeleted IN ").append("(true) ");
			}
			
			finalQuery = finalQuery.append(baseQuery);
			//checking if the filter object is null , if not finalQuery = where+filter 
			if(filter.length()>0){
				finalQuery.append(whereClause);
				finalQuery.append(filter);
			}
			
			log.debug("Advanced HCP Search Query : " + finalQuery);
			
			String countSqlQuery = "select count(distinct hcpUsers.user_id) from (" + finalQuery + " ) hcpUsers";
			
			Query countQuery = entityManager.createNativeQuery(countSqlQuery);
			BigInteger count = (BigInteger) countQuery.getSingleResult();

			Query query = getOrderedByQuery(finalQuery, sortOrder);

			List<HcpVO> hcpUsers = new ArrayList<>();

			Map<Long, HcpVO> hcpUsersMap = new HashMap<>();
			List<Object[]> results = query.getResultList();

			results.forEach((record) -> {
				Long id = ((BigInteger) record[0]).longValue();
				String email = (String) record[1];
				String firstName = (String) record[2];
				String lastName = (String) record[3];
				Boolean isDeleted = (Boolean) record[4];
				String zipcode = (String) record[5];
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
		catch(Exception e){
			e.printStackTrace();
		}
		return null;

	}

	//----------------------------------------------------------------------------------------------------------------------------------------------

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
	/**
	 * Query builder for age/adherence_score range
	 * @param rangeList
	 * @param specifier
	 * @return
	 */
	public String rangeBuilder(List<String> rangeList, String specifier){
		
		String sqlQuery = " BETWEEN ";

				String list = String.join(" ", rangeList);
				String newlist = list.replaceAll("-","AND");
				String splitStr[]= newlist.split(" ");

				StringBuilder builder = new StringBuilder(sqlQuery);

				for (String s : splitStr )
				{
					s=s.replaceAll("above","200");
					s=s.replaceAll("AND"," AND ");

					if(specifier.equalsIgnoreCase("age")){
						builder = builder.append(s).append(" or TIMESTAMPDIFF(YEAR,kt.pdob,CURDATE()) BETWEEN ");
					}
					else if(specifier.equalsIgnoreCase("adherence"))
						builder = builder.append(s).append(" or kt.adherence BETWEEN ");
				}  

				String newStr = new String(builder);
				int i =newStr.lastIndexOf("or");
				newStr =newStr.substring(0, i).concat("");

				return newStr;
		}
	/**
	 * 
	 * To convert the date format
	 * @param dt
	 * @return
	 * @throws ParseException
	 */
	
	public static String dateFormat(String dt) throws ParseException{
		Date initDate =  new SimpleDateFormat("MM/dd/yyyy").parse(dt);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String parsedDate = formatter.format(initDate);
		return parsedDate.toString();
	}
	
	/*
	 * Replace string values having apostrophe with escape sequence and convert into comma separated values
	 */
	public static String commaSeparatedValues(List<String> list){
		List<String> csvList = new ArrayList<String>();
		String value = "";
		for (String x : list) {
			value = x.replace("'", "''");
			csvList.add(value);
		}
		String csvValues = String.join("','", csvList);  //Forming the comma & single quote separated list
		return csvValues;
	}
}