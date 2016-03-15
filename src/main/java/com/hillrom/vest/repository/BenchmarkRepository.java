package com.hillrom.vest.repository;

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

@Repository
public class BenchmarkRepository {

	private final Logger log = LoggerFactory.getLogger(BenchmarkRepository.class);

	private static final String ORDER_BY_CLAUSE_START = " order by ";

	@Inject
	private EntityManager entityManager;

	public List<BenchmarkResultVO> getAverageBenchmarkByAge(LocalDate fromDate, LocalDate toDate, String cityCSV,
			String stateCSV) {

		StringBuffer avgQueryString = new StringBuffer("SELECT pc.id as complainceId,pc.patient_id as patId, "
				+ "pc.user_id as userId,pi.dob as dob," + "TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) AS age, "
				+ "pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, "
				+ "AVG(pc.compliance_score) as avgCompScore, "
				+ "AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount, "
				+ "AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount, "
				+ "AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount "
				+ "FROM PATIENT_COMPLIANCE pc " + "left outer join USER u on u.id = pc.user_id "
				+ "left outer join USER_PATIENT_ASSOC upa on u.id = upa.user_id "
				+ "left outer join PATIENT_INFO pi on pi.id = upa.patient_id ");
		if (StringUtils.isNotEmpty(cityCSV))
			avgQueryString.append("and pi.city in ('" + cityCSV + "') ");
		if (StringUtils.isNotEmpty(stateCSV))
			avgQueryString.append("and pi.state in ('" + stateCSV + "') ");
		
		avgQueryString.append("left outer join USER_AUTHORITY ua on ua.user_id = pc.user_id " 
				+ "and ua.authority_name = '"+PATIENT+"' where pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");		
		avgQueryString.append("group by pc.patient_id;");

		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(), "avgBenchmarkResultSetMapping");
		return avgQuery.getResultList();
	}
	
	public List<BenchmarkResultVO> getAverageBenchmarkByClinicSize(LocalDate fromDate, LocalDate toDate, String cityCSV,
			String stateCSV) {

		StringBuffer avgQueryString = new StringBuffer("SELECT pc.id as complainceId,pc.patient_id as patId," 
				+"pc.user_id as userId,pi.dob as dob, " 
				+"pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, "
				+"AVG(pc.compliance_score) as avgCompScore,  "
				+"AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount,  "
				+"AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount,  "
				+"AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount,clinic_size_table.clinicsize  "
				+"FROM PATIENT_COMPLIANCE pc   left outer join USER u on u.id = pc.user_id  "
				+"left outer join USER_PATIENT_ASSOC upa on u.id = upa.user_id  "
				+"left outer join PATIENT_INFO pi on pi.id = upa.patient_id ");
		        if(StringUtils.isEmpty(cityCSV)) 
		        	avgQueryString.append("and pi.city in ('" + cityCSV + "')");
		        if (StringUtils.isNotEmpty(stateCSV))
					avgQueryString.append("and pi.state in ('" + stateCSV + "') ");
		        
		        avgQueryString.append("left outer join CLINIC_PATIENT_ASSOC cpa on cpa.patient_id = pi.id "					
				+"left outer join CLINIC cl on cl.id = cpa.clinic_id "
				+"left outer join USER_AUTHORITY ua on ua.user_id = pc.user_id   and ua.authority_name = '"+PATIENT+"' "
				+"left outer join (select clinic_id as clinicid,group_concat(patient_id),count(patient_id) as clinicsize	 "					
				+"from CLINIC_PATIENT_ASSOC group by clinic_id) as clinic_size_table on						 "
				+"clinic_size_table.clinicid = cl.id where pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");
		        avgQueryString.append("group by pc.patient_id;");

		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(), "avgBenchmarkByClinicSizeResultSetMapping");
		return avgQuery.getResultList();
	}
	
	public List<BenchmarkResultVO> getGroupedAverageBenchmarkByAge(LocalDate fromDate, LocalDate toDate, String cityCSV,
			String stateCSV){
		    StringBuffer avgGroupedQueryString = new StringBuffer(
			"SELECT count(distinct(pc.patient_id)) as patientcount, "
			+"if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 0 and 5 , '0-5', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 6 and 10 ,'6-10', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 11 and 15 ,'11-15', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 16 and 20 ,'16-20', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 21 and 25 ,'21-25', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 26 and 30 ,'26-30', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 31 and 35 ,'31-35', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 36 and 40 ,'36-40', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 41 and 45 ,'41-45', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 46 and 50 ,'46-50', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 51 and 55 ,'51-55', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 56 and 60 ,'56-60', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 61 and 65 ,'61-65', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 66 and 70 ,'66-70', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 71 and 75 ,'71-75', "
			+"(if(TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) between 76 and 80 ,'76-80','81-above'))))))))))))))))))))))))))))))) "
			+"AS agerange, AVG(pc.compliance_score) as avgCompScore "
			+"AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount, "
			+"AVG(pc.global_settings_deviated_count)  as avgSettingsDeviatedCount, "
			+"AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount "
			+"FROM PATIENT_COMPLIANCE pc where (pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "') "
			+"left outer join USER u on u.id = pc.user_id "
			+"left outer join USER_PATIENT_ASSOC upa on u.id = upa.user_id "
			+"left outer join PATIENT_INFO pi on pi.id = upa.patient_id");
			if(StringUtils.isEmpty(cityCSV)) 
				avgGroupedQueryString.append(" and pi.city in ('" + cityCSV + "')");
	        if (StringUtils.isNotEmpty(stateCSV))
	        	avgGroupedQueryString.append(" and pi.state in ('" + stateCSV + "') ");
	        avgGroupedQueryString.append("left outer join USER_AUTHORITY ua on ua.user_id = pc.user_id "
	        		+ "and ua.authority_name = 'PATIENT' group by agerange");
	        	
			Query avgQuery = entityManager.createNativeQuery(avgGroupedQueryString.toString(), "avgGroupedBenchmarkByAgeResultSetMapping");
			return avgQuery.getResultList();
	}
}
