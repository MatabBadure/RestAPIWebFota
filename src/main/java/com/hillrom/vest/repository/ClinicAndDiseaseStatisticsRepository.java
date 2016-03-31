package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.AGE_RANGE_81_AND_ABOVE;
import static com.hillrom.vest.config.Constants.AGE_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_401_AND_ABOVE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.web.rest.dto.ClinicDiseaseStatisticsResultVO;
import com.hillrom.vest.web.rest.dto.Filter;

@Repository
public class ClinicAndDiseaseStatisticsRepository {

	@Inject
	private EntityManager entityManager;
	
	private final Logger log = LoggerFactory.getLogger(ClinicAndDiseaseStatisticsRepository.class);

	@SuppressWarnings("unchecked")
	public List<ClinicDiseaseStatisticsResultVO> getClinicDiseaseStatsByState(
			Filter filter) {
		StringBuilder query = new StringBuilder(
				"SELECT count(distinct(pi.id)) as totalPatients ");
		query.append(",");
		applyPatientRelatedJoins(query,filter);
		applyStateAndCityFilters(query, filter);
		
 		if(StringUtils.isNotEmpty(filter.getStateCSV()) && filter.getStateCSV().split(",").length <= 1){
			query.append("group by pi.city");
		}else{
			query.append("group by pi.state");
		}
		query.append(" having count(distinct pi.id) > 0");
		log.debug(query.toString());
		return (List<ClinicDiseaseStatisticsResultVO>) entityManager
				.createNativeQuery(query.toString(),
						"clinicAndDiseaseStatsByState").getResultList();
	}

	
	@SuppressWarnings("unchecked")
	public List<ClinicDiseaseStatisticsResultVO> getClinicDiseaseStatsByAgeGroupOrClinicSize(
			Filter filter) {
		StringBuilder query = new StringBuilder(
				"SELECT count(distinct(pi.id)) as totalPatients, ");
		if (AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())) {
			applyCaseStatementForAgeGroup("All", query);
		} else if (CLINIC_SIZE.equalsIgnoreCase(filter.getxAxisParameter())) {
			applyCaseStatementForClinicSize("All", query);
		} else {
			applyCaseStatementForAgeGroup("All", query);
			query.append(",");
			applyCaseStatementForClinicSize("All", query);
		}
		query.append(",");
		// Hack to avoid duplicate resultset mapping, need to provide dummy column
		if(AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())){
			query.append(" null as clinicSizeRangeLabel, ");
		}else if(CLINIC_SIZE.equalsIgnoreCase(filter.getxAxisParameter())){
			query.append(" null as ageRangeLabel, ");
		}
		
		applyPatientRelatedJoins(query,filter);
		applyStateAndCityFilters(query, filter);

		// Apply clinicRelated Joins if xAxisParameter is clinicSize or both
		if (!AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())) {
			applyClinicRelatedJoins(query);
		}

		if (CLINIC_SIZE.equalsIgnoreCase(filter.getxAxisParameter())) {
			query.append(" group by clinicSizeRangeLabel ");
		} else if(AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())) {
			query.append("group by  ageRangeLabel");
		} else {
			query.append(" group by ageRangeLabel,clinicSizeRangeLabel ");
		}
		
		log.debug(query.toString());
		return (List<ClinicDiseaseStatisticsResultVO>) entityManager
				.createNativeQuery(query.toString(),
						"clinicAndDiseaseStatsByAgeorClinicSize").getResultList();
	}

	private void applyClinicRelatedJoins(StringBuilder query) {
		query.append(
				" join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id =  pi.id join CLINIC cl on cl.id = cpa.clinic_id")
				.append(" join (select clinic_id as clinicid,count(patient_id) as clinicsize from ")
				.append(" CLINIC_PATIENT_ASSOC group by clinic_id) as clinic_size_table")
				.append(" on clinic_size_table. clinicid = cl.id ");
	}

	private void applyStateAndCityFilters(StringBuilder query,
			Filter filter) {
		if (StringUtils.isNotEmpty(filter.getStateCSV())) {
			query.append(" and pi.state in (");
			String[] states = filter.getStateCSV().split(",");
			addCSVValuesInQuery(query, states);
			query.append(")");
		}
		if (StringUtils.isNotEmpty(filter.getCityCSV())) {
			query.append(" and pi.city in (");
			String[] cities = filter.getCityCSV().split(",");
			addCSVValuesInQuery(query, cities);
			query.append(")");
		}
	}

	private void addCSVValuesInQuery(StringBuilder query, String[] values) {
		for (int i = 0; i < values.length; i++) {
			String state = values[i];
			query.append("'").append(state).append("'");
			if (i < values.length - 1) {
				query.append(",");
			}
		}
	}

	private void applyPatientRelatedJoins(StringBuilder query,Filter filter) {
		query.append(" pi.state,pi.city ")
				.append(" FROM PATIENT_INFO pi ")
				.append(" join USER_PATIENT_ASSOC upa on pi.id = upa.patient_id ")
				.append(" join USER u on u.id = upa.user_id and upa.relation_label = '"
						+ RELATION_LABEL_SELF + "'")
				.append(" join USER_AUTHORITY ua on ua.user_id = upa.user_id and ua.authority_name = '"
						+ PATIENT + "'")
				.append(" and DATE(u.created_date) between ")
				.append("'")
				.append(filter.getFrom().toString())
				.append("'").append(" and ").append("'").append(filter.getTo().toString()).append("'");
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
}
