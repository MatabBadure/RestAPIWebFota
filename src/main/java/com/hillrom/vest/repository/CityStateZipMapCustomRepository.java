package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

import com.hillrom.vest.web.rest.dto.CityNewVo;
import com.hillrom.vest.web.rest.dto.CountryStateDTO;

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
	
	// Returns the list of cities with boolean true 
	public List<CityNewVo> getExistingCities(CountryStateDTO countryStateDTO){
		String csvCountries = String.join("','", countryStateDTO.getCountry());
		String csvStates = String.join("','", countryStateDTO.getState());
		StringBuilder cBuilder = new StringBuilder();
		StringBuilder sBuilder = new StringBuilder();
		
		cBuilder = cBuilder.append("('").append(csvCountries).append("') ");
		sBuilder = sBuilder.append("('").append(csvStates).append("') ");
		
		String query = "SELECT distinct(primary_city),true FROM `hillrom-everest`.city_state_zip_map "+
			 	       "WHERE country IN"+cBuilder+" AND state IN"+sBuilder;
		List<Object[]> resultSet = entityManager.createNativeQuery(query).getResultList();
		/*List<CityNewVo> cityList = new ArrayList<>();
		resultSet.stream().forEach((record) -> {
			String c = ((String) record[0]);
			Boolean ticked = (Boolean) record[1];
			CityNewVo cityNewVO= new CityNewVo(c, ticked);
			cityList.add(cityNewVO);
		});*/
		List<CityNewVo> cityList = new ArrayList<>();
		for(Object[] result : resultSet){
			CityNewVo newObj = new CityNewVo();
			newObj.setName((String) result[0]);
			if(result[1].equals(BigInteger.ONE)){
				newObj.setTicked(true);
			}
			cityList.add(newObj);
		}
		return cityList;
	}
}
	