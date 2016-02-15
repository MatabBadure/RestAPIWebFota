package com.hillrom.vest.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.domain.CityStateZipMap;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.CityStateZipMapRepository;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.CityVO;
import com.hillrom.vest.web.rest.dto.StateVO;

@Service
@Transactional
public class CityStateZipMapService {

	@Inject
	private CityStateZipMapRepository cityStateZipMapRepository;

	public List<String> getStates() {
		return cityStateZipMapRepository.findUniqueStates();
	}

	public StateVO getStateVOByState(String state) throws HillromException {
		if (StringUtils.isEmpty(state))
			throw new HillromException(ExceptionConstants.HR_710);
		List<CityStateZipMap> cityStateZipMaps = cityStateZipMapRepository.findByState(state);
		Map<String, List<CityStateZipMap>> zipsGroupByCity = (Map) cityStateZipMaps.stream()
				.collect(Collectors.groupingBy(CityStateZipMap::getCity));
		StateVO stateVO = new StateVO();
		stateVO.setName(state);
		CityVO cityVO = null;
		for (String city : zipsGroupByCity.keySet()) {
			List<CityStateZipMap> cszList = zipsGroupByCity.get(city);
			cityVO = new CityVO();
			cityVO.setName(city);
			for (CityStateZipMap csz : cszList) {
				cityVO.getZipcodes().add(csz.getZipCode());
			}
			stateVO.getCities().add(cityVO);
		}
		return stateVO;
	}

	public List<CityStateZipMap> getbyZipCode(String zipcode) throws HillromException {
		if (StringUtils.isEmpty(zipcode))
			throw new HillromException(ExceptionConstants.HR_711);
		List<CityStateZipMap> cityStateZipMaps = cityStateZipMapRepository.findByZipCode(zipcode);

		if (cityStateZipMaps.isEmpty())
			throw new HillromException(ExceptionConstants.HR_712);
		else
			return cityStateZipMaps;
	}
}
