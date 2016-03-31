package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.AGE_RANGE_81_AND_ABOVE;
import static com.hillrom.vest.config.Constants.AGE_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_401_AND_ABOVE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.CLINIC_ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.HCP;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.security.SecurityUtils;
import com.hillrom.vest.service.AverageBenchMarkStrategy;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;
import com.hillrom.vest.web.rest.dto.Filter;

@Repository
public class BenchmarkRepository {

	private final Logger log = LoggerFactory.getLogger(BenchmarkRepository.class);

	//private static final String ORDER_BY_CLAUSE_START = " order by ";

	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchmarkByAgeForParameterView(BenchMarkFilter filter) {

		StringBuilder avgQueryString = new StringBuilder("SELECT ");
				applyCaseStatementForAgeGroup("All", avgQueryString);
				avgQueryString.append(",");
				// to avoid one more result set mapping
				avgQueryString.append("null as clinicSizeRangeLabel, ");
				addBenchMarkParametersToQuery(avgQueryString);
				avgQueryString.append(",");
				avgQueryString.append(" count(distinct pi.id) as patientCount ");
				avgQueryString.append("FROM PATIENT_COMPLIANCE pc join USER u on u.id = pc.user_id ")
				.append("join USER_PATIENT_ASSOC upa on u.id = upa.user_id and relation_label = '"+RELATION_LABEL_SELF+"' ")
				.append("join PATIENT_INFO pi on pi.id = upa.patient_id ");
				
				applyStateAndCityFilterOnPatients(filter.getCityCSV(), filter.getStateCSV(),
						avgQueryString);
		
		avgQueryString.append(" join USER_AUTHORITY ua on ua.user_id = pc.user_id ") 
				.append("and ua.authority_name = '"+PATIENT+"' where pc.date between '" + filter.getFrom().toString() + "'  AND '" + filter.getTo().toString() + "'  ");		
		avgQueryString.append("group by ageRangeLabel");
		
		log.debug(avgQueryString.toString());
		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(), "avgBenchmarkResultSetMapping");
		return avgQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchmarkByClinicSizeForParameterView(BenchMarkFilter filter) {
		StringBuilder avgQueryString = new StringBuilder();
				avgQueryString.append("SELECT ");
				avgQueryString.append("null as ageRangeLabel, ");
				applyCaseStatementForClinicSize("All", avgQueryString);
				avgQueryString.append(",");
				addBenchMarkParametersToQuery(avgQueryString);
				avgQueryString.append(",");
				avgQueryString.append(" clinic_size_table.clinicsize as patientCount ");
				
				addPatientRelatedJoins(avgQueryString);
		        
				applyStateAndCityFilterOnPatients(filter.getCityCSV(), filter.getStateCSV(),
						avgQueryString);
		        
		        avgQueryString.append("join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id ")					
				.append("join CLINIC cl on cl.id = cpa.clinic_id ");
		        
		        avgQueryString.append("join USER_AUTHORITY ua on ua.user_id = pc.user_id   and ua.authority_name = '"+PATIENT+"' ")
				.append("join (select clinic_id as clinicid, count(patient_id) as clinicsize	 ")					
				.append("from CLINIC_PATIENT_ASSOC group by clinic_id) as clinic_size_table on ")
				.append("clinic_size_table.clinicid = cl.id where pc.date between '" + filter.getFrom().toString() + "'  AND '" + filter.getTo().toString() + "'  ");
		        avgQueryString.append("group by clinicSizeRangeLabel");
		        log.debug(avgQueryString.toString());
		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(),"avgBenchmarkResultSetMapping");
		return avgQuery.getResultList();
	}

	private void applyStateAndCityFilterOnPatients(String cityCSV,
			String stateCSV, StringBuilder avgQueryString) {
		if(StringUtils.isNotEmpty(cityCSV)) 
			avgQueryString.append("and pi.city in (" + getQuotedCsvValues(cityCSV) + ")");
		if (StringUtils.isNotEmpty(stateCSV))
			avgQueryString.append("and pi.state in (" + getQuotedCsvValues(stateCSV) + ") ");
	}

	// query for Average Benchmark for a Clinic by Age Group
	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchmarkForClinicByAgeGroup(LocalDate fromDate, LocalDate toDate,
			String cityCSV, String stateCSV, String clinicId) {
		StringBuilder avgQueryString = new StringBuilder();
			avgQueryString.append("SELECT pc.id as complainceId,pc.patient_id as patId, ");
			avgQueryString.append("pc.user_id as userId,pi.dob as dob, ");
			avgQueryString.append("pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, ");
			
			addBenchMarkParametersToQuery(avgQueryString);
			
			avgQueryString.append(", cl.name as clinicName ");
			
			addPatientRelatedJoins(avgQueryString);
			
			applyStateAndCityFilterOnPatients(cityCSV, stateCSV, avgQueryString);
			avgQueryString.append("join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id ");
			avgQueryString.append("join CLINIC cl on cl.id = cpa.clinic_id and cl.id = '" + clinicId + "' ");
			applyJoinWithUserAuthority(fromDate, toDate, avgQueryString);
			avgQueryString.append("group by pc.patient_id;");
			log.debug(avgQueryString.toString());
		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(),
				"avgBenchmarkForClinicByAgeGroupResultSetMapping");
		return avgQuery.getResultList();
	}

	private String getQuotedCsvValues(String valuesCSV) {
		String[] values = valuesCSV.split(",");
		StringBuilder quotedCSVValues = new StringBuilder(); 
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			quotedCSVValues.append("'").append(value).append("'");
			if (i < values.length - 1) {
				quotedCSVValues.append(",");
			}
		}
		return quotedCSVValues.toString();
	}
	
	private void applyJoinWithUserAuthority(LocalDate fromDate,
			LocalDate toDate, StringBuilder avgQueryString) {
		avgQueryString.append("join USER_AUTHORITY ua on ua.user_id = pc.user_id  and ua.authority_name = '" + PATIENT + "' ");
		avgQueryString.append("and pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");
	}

	private void addPatientRelatedJoins(StringBuilder avgQueryString) {
		avgQueryString.append("FROM PATIENT_COMPLIANCE pc ");
		avgQueryString.append("join USER u on u.id = pc.user_id ");
		avgQueryString.append("join USER_PATIENT_ASSOC upa on u.id = upa.user_id and relation_label = '"+RELATION_LABEL_SELF+"' ");
		avgQueryString.append("join PATIENT_INFO pi on pi.id = upa.patient_id ");
	}

	private void addBenchMarkParametersToQuery(StringBuilder avgQueryString) {
		avgQueryString.append("AVG(pc.compliance_score) as avgCompScore, ");
		avgQueryString.append("AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount, ");
		avgQueryString.append("AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount, ");
		avgQueryString.append("AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount, ");
		avgQueryString.append("AVG(pc.hmr_run_rate) as avgHMRRunrate ");
	}
	
	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchMarkByAgeGroupForClinicAndAdminOrHCP(BenchMarkFilter filter){
		StringBuilder query = new StringBuilder();
		query.append("SELECT cl.name,cl.zipcode,cl.city ,cl.state,");
		applyCaseStatementForAgeGroup("All",query);
		query.append(",");
		addBenchMarkParametersToQuery(query);
		addPatientRelatedJoins(query);
		query.append("join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id ");
		query.append("join CLINIC cl on cl.id = cpa.clinic_id ");
		if(StringUtils.isNotEmpty(filter.getClinicId())){
			query.append(" and cl.id = '" + filter.getClinicId() + "' ");
		}
		applyJoinWithUserAuthority(filter.getFrom(), filter.getTo(), query);
		// Apply Joins For HCP or Clinic Admin
		if(SecurityUtils.isUserInRole(HCP)){
			applyHCPRelatedJoin(filter.getUserId(), filter.getClinicId(), query);
		}else if(SecurityUtils.isUserInRole(CLINIC_ADMIN)){
			applyClinicAdminRelatedJoin(filter.getUserId(), filter.getClinicId(), query);
		}
		query.append("group by ageRangeLabel");
		log.debug(query.toString());
		return entityManager.createNativeQuery(query.toString(), "avgBenchMarkForClinicAdminOrHCPByAgeGroup").getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchMarkByAgeGroupForRestOfClinics(BenchMarkFilter filter){
		StringBuilder query = new StringBuilder();
		query.append("SELECT cl.name,cl.zipcode,cl.city ,cl.state,");
		applyCaseStatementForAgeGroup("All",query);
		query.append(",");
		addBenchMarkParametersToQuery(query);
		addPatientRelatedJoins(query);
		query.append("join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id ");
		query.append("join CLINIC cl on cl.id = cpa.clinic_id ");
		if(StringUtils.isNotEmpty(filter.getStateCSV()) && !"All".equalsIgnoreCase(filter.getStateCSV())){
			query.append(" and cl.state in (" + getQuotedCsvValues(filter.getStateCSV()) + ")");
		}
		if(StringUtils.isNotEmpty(filter.getCityCSV()) && !"All".equalsIgnoreCase(filter.getCityCSV())){
			query.append(" and cl.city in (" + getQuotedCsvValues(filter.getCityCSV()) + ")");
		}
		applyJoinWithUserAuthority(filter.getFrom(), filter.getTo(), query);
		query.append("group by ageRangeLabel");
		log.debug(query.toString());
		return entityManager.createNativeQuery(query.toString(), "avgBenchMarkForClinicAdminOrHCPByAgeGroup").getResultList();
	}

	private void applyClinicAdminRelatedJoin(Long userId, String clinicId,
			StringBuilder query) {
		query.append(" join ENTITY_USER_ASSOC eua on  eua.user_id = ").append(userId);
		query.append(" and eua.entity_id = ").append("'").append(clinicId).append("'");
		query.append(" join USER_AUTHORITY cua on cua.user_id = eua.user_id and cua.authority_name = ")
		.append("'").append(CLINIC_ADMIN).append("'");
	}

	private void applyHCPRelatedJoin(Long userId, String clinicId,
			StringBuilder query) {
		query.append(" join CLINIC_USER_ASSOC cua on  cua.users_id = ").append(userId);
		query.append(" and cua.clinics_id = ").append("'").append(clinicId).append("'");
		query.append(" join USER_AUTHORITY hua on hua.user_id = cua.users_id and hua.authority_name = ")
		.append("'").append(HCP).append("'");
	}

	private void applyCaseStatementForAgeGroup(String ageGroupRangeLabel,
			StringBuilder query) {
		query.append(" case ");
		String ageGroupRangeLabels[] = ageGroupRangeLabel.split(",");
		if ("All".equalsIgnoreCase(ageGroupRangeLabel)) {
			ageGroupRangeLabels = AGE_RANGE_LABELS;
		}
		for (String rangeLabel : ageGroupRangeLabels) {
			String ranges[] = rangeLabel.split("-");
			if (!AGE_RANGE_81_AND_ABOVE.equalsIgnoreCase(rangeLabel)) {
				query.append(
						" when TIMESTAMPDIFF(YEAR,IFNULL(pi.dob,CURDATE()),CURDATE()) between ")
						.append(ranges[0]).append(" and ").append(ranges[1]);
			} else {
				query.append(" when  TIMESTAMPDIFF(YEAR,IFNULL(pi.dob,CURDATE()),CURDATE()) >= ")
						.append(ranges[0]);
			}
			query.append(" then ").append("'").append(rangeLabel).append("'");
		}
		query.append(" end as ageRangeLabel ");
	}
	
	private void applyCaseStatementForClinicSize(String clinicSizeGroupRangeLabel,
			StringBuilder query) {
		query.append(" case ");
		String clinicSizeGroupRangeLabels[] = clinicSizeGroupRangeLabel
				.split(",");
		if ("All".equalsIgnoreCase(clinicSizeGroupRangeLabel)) {
			clinicSizeGroupRangeLabels = CLINIC_SIZE_RANGE_LABELS;
		}
		for (String rangeLabel : clinicSizeGroupRangeLabels) {
			String ranges[] = rangeLabel.split("-");
			if (!CLINIC_SIZE_RANGE_401_AND_ABOVE.equalsIgnoreCase(rangeLabel)) {
				query.append(" when clinic_size_table.clinicsize between ")
						.append(ranges[0]).append(" and ").append(ranges[1]);
			} else {
				query.append(" when clinic_size_table.clinicsize >= ").append(
						ranges[0]);
			}
			query.append(" then ").append("'").append(rangeLabel).append("'");
		}
		query.append(" end as clinicSizeRangeLabel");
	}

}
