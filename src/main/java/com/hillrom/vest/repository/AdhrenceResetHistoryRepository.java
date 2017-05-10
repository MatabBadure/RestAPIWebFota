package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.web.rest.dto.AdherenceResetHistoryVO;

@Repository
public class AdhrenceResetHistoryRepository {
	
	private final Logger log = LoggerFactory.getLogger(UserSearchRepository.class);
	private static final String ORDER_BY_CLAUSE_START = " order by ";
	
	@Inject
	private EntityManager entityManager;
	//hill-2133
	
		public Page<AdherenceResetHistoryVO> getAdherenceResetHistoryForPatient(Long userId,Pageable pageable,
				Map<String, Boolean> sortOrder) throws HillromException {

			String adherenceResetHistoryQuery = "SELECT DATE_FORMAT(reset_start_date,'%m/%d/%Y') as c1 , "
					+ " DATE_FORMAT(reset_date,'%m/%d/%Y') as c2 , "
					+ " DATE_FORMAT(reset_date,'%l:%i%p') as c3 , "
					+ " CASE justification "
					+ "	WHEN 'Hospitalization' THEN justification "
					+ "	WHEN 'Device issue' THEN justification "
					+ "	WHEN 'No connectivity' THEN justification "
					+ "	ELSE 'Other'"
					+ "	END  , "
					+ "	CASE justification"
					+ "	WHEN 'Hospitalization' THEN 'NA' "
					+ "	WHEN 'Device issue' THEN 'NA' "
					+ "	WHEN 'No connectivity' THEN 'NA' "
					+ "	ELSE justification "
					+ "	END " 
					+ " FROM ADHERENCE_RESET where user_id = "+userId+" ORDER BY reset_date desc ";
				
			String countSqlQuery = "select count(adherenceResetHistory.c1) from (" + adherenceResetHistoryQuery + ") adherenceResetHistory";

			Query countQuery = entityManager.createNativeQuery(countSqlQuery);
			BigInteger count = (BigInteger) countQuery.getSingleResult();
			//sort by isDeleted to isDeleted and isActive
			/*if(sortOrder.containsKey("isDeleted")){
				sortOrder.put("isActivated", sortOrder.get("isDeleted"));
			}*/
			
			Query query = getOrderedByQuery(adherenceResetHistoryQuery, sortOrder);
			setPaginationParams(pageable, query);

			List<AdherenceResetHistoryVO> historyList = new ArrayList<>();
			List<Object[]> results = query.getResultList();
			results.stream().forEach((record) -> {				
				String resetStartDate = (String) record[0];
				String resetDate = (String) record[1];
				String resetTime = (String) record[2];
				String justification = (String) record[3];
				String comments = (String) record[4];

				AdherenceResetHistoryVO adhRstHistoryVO = new AdherenceResetHistoryVO(resetStartDate, resetDate,
						resetTime, justification, comments);
				historyList.add(adhRstHistoryVO);
			});
			
			int firstResult = pageable.getOffset();
			int maxResults = firstResult + pageable.getPageSize();
			List<AdherenceResetHistoryVO> adhRstHistoryList = new ArrayList<>();
			if (firstResult < historyList.size()) {
				maxResults = maxResults > historyList.size() ? historyList.size() : maxResults;
				adhRstHistoryList = historyList.subList(firstResult, maxResults);
			}		

			Page<AdherenceResetHistoryVO> page = new PageImpl<AdherenceResetHistoryVO>(historyList, pageable, count.intValue());

			return page;
		}

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
				/*if(!Constants.ADHERENCE.equalsIgnoreCase(columnName))
					sb.append("lower(").append(columnName).append(")");
				else*/
					sb.append(columnName);

				if (columnNames.get(columnName))
					sb.append(" ASC");
				else
					sb.append(" DESC");

				if (i++ != (limit - 1)) {
					sb.append(", ");
				}
			}
			
			log.debug(" Query :: "+queryString + sb.toString());
			
			Query jpaQuery = entityManager.createNativeQuery(queryString + sb.toString());
			return jpaQuery;
		}


		

}
