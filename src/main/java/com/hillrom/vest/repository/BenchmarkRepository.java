package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;

@Repository
public class BenchmarkRepository {

	//private final Logger log = LoggerFactory.getLogger(BenchmarkRepository.class);

	//private static final String ORDER_BY_CLAUSE_START = " order by ";

	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchmarkByAge(LocalDate fromDate, LocalDate toDate, String cityCSV,
			String stateCSV) {

		StringBuilder avgQueryString = new StringBuilder("SELECT pc.id as complainceId,pc.patient_id as patId, ");
				avgQueryString.append("pc.user_id as userId,pi.dob as dob,TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) AS age, ")
				.append("pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, ")
				.append("AVG(pc.compliance_score) as avgCompScore, ")
				.append("AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount, ")
				.append("AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount, ")
				.append("AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount, ")
				.append("AVG(pc.hmr_run_rate) as avgHMRRunrate ")
				.append("FROM PATIENT_COMPLIANCE pc left outer join USER u on u.id = pc.user_id ")
				.append("join USER_PATIENT_ASSOC upa on u.id = upa.user_id and relation_label = '"+RELATION_LABEL_SELF+"' ")
				.append("join PATIENT_INFO pi on pi.id = upa.patient_id ");
		if (StringUtils.isNotEmpty(cityCSV))
			avgQueryString.append("and pi.city in ('" + cityCSV + "') ");
		if (StringUtils.isNotEmpty(stateCSV))
			avgQueryString.append("and pi.state in ('" + stateCSV + "') ");
		
		avgQueryString.append("left outer join USER_AUTHORITY ua on ua.user_id = pc.user_id ") 
				.append("and ua.authority_name = '"+PATIENT+"' where pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");		
		avgQueryString.append("group by pc.patient_id;");

		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(), "avgBenchmarkResultSetMapping");
		return avgQuery.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchmarkByClinicSize(LocalDate fromDate, LocalDate toDate, String cityCSV,
			String stateCSV) {

		StringBuilder avgQueryString = new StringBuilder("SELECT pc.id as complainceId,pc.patient_id as patId,"); 
				avgQueryString.append("pc.user_id as userId,pi.dob as dob, ") 
				.append("pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, ")
				.append("AVG(pc.compliance_score) as avgCompScore,  ")
				.append("AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount,  ")
				.append("AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount,  ")
				.append("AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount,")
				.append("AVG(pc.hmr_run_rate) as avgHMRRunrate, clinic_size_table.clinicsize ")
				.append("FROM PATIENT_COMPLIANCE pc   left outer join USER u on u.id = pc.user_id  ")
				.append("join USER_PATIENT_ASSOC upa on u.id = upa.user_id  and relation_label = '"+RELATION_LABEL_SELF+"' ")
				.append("join PATIENT_INFO pi on pi.id = upa.patient_id ");
		        if(StringUtils.isNotEmpty(cityCSV)) 
		        	avgQueryString.append("and pi.city in ('" + cityCSV + "')");
		        if (StringUtils.isNotEmpty(stateCSV))
					avgQueryString.append("and pi.state in ('" + stateCSV + "') ");
		        
		        avgQueryString.append("join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id ")					
				.append("join CLINIC cl on cl.id = cpa.clinic_id ")
				.append("join USER_AUTHORITY ua on ua.user_id = pc.user_id   and ua.authority_name = '"+PATIENT+"' ")
				.append("left outer join (select clinic_id as clinicid, count(patient_id) as clinicsize	 ")					
				.append("from CLINIC_PATIENT_ASSOC group by clinic_id) as clinic_size_table on ")
				.append("clinic_size_table.clinicid = cl.id where pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");
		        avgQueryString.append("group by pc.patient_id;");
		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(),"avgBenchmarkByClinicSizeResultSetMapping");
		return avgQuery.getResultList();
	}

	// query for Average Benchmark for a Clinic by Age Group
	@SuppressWarnings("unchecked")
	public List<BenchmarkResultVO> getAverageBenchmarkForClinicByAgeGroup(LocalDate fromDate, LocalDate toDate,
			String cityCSV, String stateCSV, String clinicId) {
		StringBuilder avgQueryString = new StringBuilder();
			avgQueryString.append("SELECT pc.id as complainceId,pc.patient_id as patId, ");
			avgQueryString.append("pc.user_id as userId,pi.dob as dob, ");
			avgQueryString.append("pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, ");
			avgQueryString.append("AVG(pc.compliance_score) as avgCompScore, ");
			avgQueryString.append("AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount, ");
			avgQueryString.append("AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount, ");
			avgQueryString.append("AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount, ");
			avgQueryString.append("AVG(pc.hmr_run_rate) as avgHMRRunrate, cl.name as clinicName ");
			avgQueryString.append("FROM PATIENT_COMPLIANCE pc ");
			avgQueryString.append("join USER u on u.id = pc.user_id ");
			avgQueryString.append("join USER_PATIENT_ASSOC upa on u.id = upa.user_id and relation_label = '"+RELATION_LABEL_SELF+"' ");
			avgQueryString.append("join PATIENT_INFO pi on pi.id = upa.patient_id ");
			if (StringUtils.isNotEmpty(cityCSV))
				avgQueryString.append("and pi.city in ('" + cityCSV + "')");
			if (StringUtils.isNotEmpty(stateCSV))
				avgQueryString.append("and pi.state in ('" + stateCSV + "') ");
			avgQueryString.append("join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id ");
			avgQueryString.append("join CLINIC cl on cl.id = cpa.clinic_id and cl.id = '" + clinicId + "' ");
			avgQueryString.append(
					"join USER_AUTHORITY ua on ua.user_id = pc.user_id  and ua.authority_name = '" + PATIENT + "' ");
			avgQueryString.append("where pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");
			avgQueryString.append("group by pc.patient_id;");
			System.out.println(avgQueryString);
		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(),
				"avgBenchmarkForClinicByAgeGroupResultSetMapping");
		return avgQuery.getResultList();
	}
}
