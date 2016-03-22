package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.AGE_RANGE_0_TO_5;
import static com.hillrom.vest.config.Constants.AGE_RANGE_11_TO_15;
import static com.hillrom.vest.config.Constants.AGE_RANGE_16_TO_20;
import static com.hillrom.vest.config.Constants.AGE_RANGE_21_TO_25;
import static com.hillrom.vest.config.Constants.AGE_RANGE_26_TO_30;
import static com.hillrom.vest.config.Constants.AGE_RANGE_31_TO_35;
import static com.hillrom.vest.config.Constants.AGE_RANGE_36_TO_40;
import static com.hillrom.vest.config.Constants.AGE_RANGE_41_TO_45;
import static com.hillrom.vest.config.Constants.AGE_RANGE_46_TO_50;
import static com.hillrom.vest.config.Constants.AGE_RANGE_51_TO_55;
import static com.hillrom.vest.config.Constants.AGE_RANGE_56_TO_60;
import static com.hillrom.vest.config.Constants.AGE_RANGE_61_TO_65;
import static com.hillrom.vest.config.Constants.AGE_RANGE_66_TO_70;
import static com.hillrom.vest.config.Constants.AGE_RANGE_6_TO_10;
import static com.hillrom.vest.config.Constants.AGE_RANGE_71_TO_75;
import static com.hillrom.vest.config.Constants.AGE_RANGE_76_TO_80;
import static com.hillrom.vest.config.Constants.AGE_RANGE_81_AND_ABOVE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_101_TO_150;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_151_TO_200;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_1_TO_25;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_201_TO_250;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_251_TO_300;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_26_TO_50;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_301_TO_350;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_351_TO_400;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_401_AND_ABOVE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_51_TO_75;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_76_TO_100;
import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.ClinicDiseaseStatisticsResultVO;

@Repository
public class ClinicAndDiseaseStatisticsRepository {

	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<ClinicDiseaseStatisticsResultVO> getClinicDiseaseStatsByState(
			BenchMarkFilter filter) {
		StringBuilder query = new StringBuilder(
				"SELECT count(distinct(pi.id)) as totalPatients ");
		query.append(",");
		applyPatientRelatedJoins(query,filter);
		applyStateAndCityFilters(query, filter);
		query.append("group by pi.id");
		return (List<ClinicDiseaseStatisticsResultVO>) entityManager
				.createNativeQuery(query.toString(),
						"clinicAndDiseaseStatsByState");
	}

	@SuppressWarnings("unchecked")
	public List<ClinicDiseaseStatisticsResultVO> getClinicDiseaseStatsByAgeGroupOrClinicSize(
			BenchMarkFilter filter) {
		StringBuilder query = new StringBuilder(
				"SELECT count(distinct(pi.id)) as totalPatients, ");
		if (AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())) {
			applyCaseStatementForAgeGroup(filter, query);
		} else if (CLINIC_SIZE.equalsIgnoreCase(filter.getxAxisParameter())) {
			applyCaseStatementForClinicSize(filter, query);
		} else {
			applyCaseStatementForAgeGroup(filter, query);
			query.append(",");
			applyCaseStatementForClinicSize(filter, query);
		}
		query.append(",");
		applyPatientRelatedJoins(query,filter);
		applyStateAndCityFilters(query, filter);

		if (CLINIC_SIZE.equalsIgnoreCase(filter.getxAxisParameter())) {
			applyClinicRelatedJoins(query);
		}

		if (CLINIC_SIZE.equalsIgnoreCase(filter.getxAxisParameter())) {
			query.append(" group by clinicSizeRangeLabel ");
		} else {
			query.append("group by  ageRangeLabel");
		}
		return (List<ClinicDiseaseStatisticsResultVO>) entityManager
				.createNativeQuery(query.toString(),
						"clinicAndDiseaseStatsByAgeorClinicSize");
	}

	private void applyClinicRelatedJoins(StringBuilder query) {
		query.append(
				" join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id =  pi.id join CLINIC cl on cl.id = cpa.clinic_id")
				.append(" join (select clinic_id as clinicid,count(patient_id) as clinicsize from ")
				.append(" CLINIC_PATIENT_ASSOC group by clinic_id) as clinic_size_table")
				.append(" on clinic_size_table. clinicid = cl.id ");
	}

	private void applyStateAndCityFilters(StringBuilder query,
			BenchMarkFilter filter) {
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

	private void applyPatientRelatedJoins(StringBuilder query,BenchMarkFilter filter) {
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

	private void applyCaseStatementForClinicSize(BenchMarkFilter filter,
			StringBuilder query) {
		query.append(" case ");
		String clinicSizeGroupRangeLabel = filter.getClinicSizeRangeCSV();
		String clinicSizeGroupRangeLabels[] = clinicSizeGroupRangeLabel
				.split(",");
		if ("All".equalsIgnoreCase(clinicSizeGroupRangeLabel)) {
			clinicSizeGroupRangeLabels = new String[] {
					CLINIC_SIZE_RANGE_1_TO_25, CLINIC_SIZE_RANGE_26_TO_50,
					CLINIC_SIZE_RANGE_51_TO_75, CLINIC_SIZE_RANGE_76_TO_100,
					CLINIC_SIZE_RANGE_101_TO_150, CLINIC_SIZE_RANGE_151_TO_200,
					CLINIC_SIZE_RANGE_201_TO_250, CLINIC_SIZE_RANGE_251_TO_300,
					CLINIC_SIZE_RANGE_301_TO_350, CLINIC_SIZE_RANGE_351_TO_400,
					CLINIC_SIZE_RANGE_401_AND_ABOVE };
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

	private void applyCaseStatementForAgeGroup(BenchMarkFilter filter,
			StringBuilder query) {
		query.append(" case ");
		String ageGroupRangeLabel = filter.getAgeRangeCSV();
		String ageGroupRangeLabels[] = ageGroupRangeLabel.split(",");
		if ("All".equalsIgnoreCase(ageGroupRangeLabel)) {
			ageGroupRangeLabels = new String[] { AGE_RANGE_0_TO_5,
					AGE_RANGE_6_TO_10, AGE_RANGE_11_TO_15, AGE_RANGE_16_TO_20,
					AGE_RANGE_21_TO_25, AGE_RANGE_26_TO_30, AGE_RANGE_31_TO_35,
					AGE_RANGE_36_TO_40, AGE_RANGE_41_TO_45, AGE_RANGE_46_TO_50,
					AGE_RANGE_51_TO_55, AGE_RANGE_56_TO_60, AGE_RANGE_61_TO_65,
					AGE_RANGE_66_TO_70, AGE_RANGE_71_TO_75, AGE_RANGE_76_TO_80,
					AGE_RANGE_81_AND_ABOVE };
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
