package com.hillrom.vest.repository;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

@Component
public class CityStateZipMapCustomRepository {
	
	@Inject
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public Map<String,List<String>> getExistingStateAndCitiesMapInsystem(){
		String query = "SELECT distinct(state), group_concat(distinct city) from PATIENT_INFO group by state having state is NOT NULL";
		List<Object[]> resultSet = entityManager.createNativeQuery(query).getResultList();
		Map<String,List<String>> stateCityMap = new LinkedHashMap<>();
		for (Object[] stateCities : resultSet) {
			stateCityMap.put((String) stateCities[0],
					Objects.nonNull(stateCities[1])
							? new LinkedList<String>(Arrays.asList(((String) stateCities[1]).split(",")))
							: new LinkedList<String>());
		}
		return stateCityMap;
	}
}
