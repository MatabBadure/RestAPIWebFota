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

	public List<BenchmarkResultVO> getAverageBenchmark(LocalDate fromDate, LocalDate toDate, String cityCSV,
			String stateCSV) {

		StringBuffer avgQueryString = new StringBuffer("SELECT pc.id as complainceId,pc.patient_id as patId, "
				+ "pc.user_id as userId,pi.dob as dob," + "TIMESTAMPDIFF(YEAR,pi.dob,CURDATE()) AS age, "
				+ "pi.zipcode,pi.city,pi.state, pc.last_therapy_session_date as lastTherapySessionDate, "
				+ "AVG(pc.compliance_score) as avgCompScore, "
				+ "AVG(pc.global_hmr_non_adherence_count) as avgNonAdherenceCount, "
				+ "AVG(pc.global_settings_deviated_days_count)  as avgSettingsDeviatedCount, "
				+ "AVG(pc.global_missed_therapy_days_count)  as avgMissedTherapyDaysCount "
				+ "FROM PATIENT_COMPLIANCE pc " + "left outer join USER u on u.id = pc.user_id "
				+ "left outer join USER_PATIENT_ASSOC upa on u.id = upa.user_id "
				+ "left outer join PATIENT_INFO pi on pi.id = upa.patient_id "
				+ "left outer join USER_AUTHORITY ua on ua.user_id = pc.user_id " + "and ua.authority_name = '"
				+ PATIENT + "' where pc.date between '" + fromDate.toString() + "'  AND '" + toDate.toString() + "'  ");

		if (StringUtils.isNotEmpty(cityCSV))
			avgQueryString.append("and pi.city in ('" + cityCSV + "') ");
		if (StringUtils.isNotEmpty(stateCSV))
			avgQueryString.append("and pi.state in ('" + stateCSV + "') ");
		avgQueryString.append("group by pc.patient_id;");

		Query avgQuery = entityManager.createNativeQuery(avgQueryString.toString(), "avgBenchmarkResultSetMapping");
		return avgQuery.getResultList();
	}
}
