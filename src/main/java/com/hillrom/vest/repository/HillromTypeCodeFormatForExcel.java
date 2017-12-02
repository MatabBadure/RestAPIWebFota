package com.hillrom.vest.repository;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class HillromTypeCodeFormatForExcel {
	
	private final Logger log = LoggerFactory
			.getLogger(HillromTypeCodeFormatForExcel.class);
	@Inject
	private EntityManager entityManager;
	
	public List<Object[]> findCodeValuesListForExcel(String type) {
		String queryStr = "select CONV(substring(type_code,3,2),16,10) as dec_type_code,type_code_value from HILLROM_TYPE_CODE_VALUES where type = '"+type+ "'";
		Query jpaQuery = entityManager.createNativeQuery(queryStr);
		List<Object[]> resultList = jpaQuery.getResultList();
		log.debug("User list:" + resultList);
		return resultList;
	}
}
