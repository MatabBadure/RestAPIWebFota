package com.hillrom.vest.repository;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SurveyReportRepository {
	
	private final Logger log = LoggerFactory.getLogger(SurveyReportRepository.class);
	
	@Inject
	private EntityManager entityManager;
	
	public List<Object> fiveDaysSurveyGridView(LocalDate fromDate, LocalDate toDate){
		
		String fiveDayGridViewQuery = "select id, survey_id, question_id, group_concat(user_id), group_concat(answer_value_1),"		
										   +"ROUND (" 			
										   +"("	
										   +" LENGTH(group_concat(answer_value_1)) - LENGTH( REPLACE ( group_concat(answer_value_1), 'Yes', '') )"			
										   +")/ LENGTH('Yes)"
										   +") AS Yescount,"
										   +"ROUND ("	
										   +"("	
										   +"LENGTH(group_concat(answer_value_1))"		
										   +"- LENGTH( REPLACE ( group_concat(answer_value_1), 'No', '') )"			
										   +") / LENGTH('No')"
										   +") AS Nocount, answer_value_2, answer_value_3,compl_date"			
										   +"from USER_SURVEY_ANSWERS"
										   +"where question_id in (6,7,8,9,10,11,12)  and survey_id = 1	"
										   + "and compl_date between :fromDate and :toDate"
										   +"group by question_id";
		fiveDayGridViewQuery = fiveDayGridViewQuery.replace(":fromDate",fromDate.toString()).replace(":toDate", toDate.toString());
		log.debug("Five Day Grid View Query" + fiveDayGridViewQuery);
		Query query = entityManager.createNativeQuery(fiveDayGridViewQuery);
		return null;
	}

}
