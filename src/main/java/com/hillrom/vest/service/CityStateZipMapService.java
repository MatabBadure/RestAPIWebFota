package com.hillrom.vest.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import com.hillrom.vest.domain.CityStateZipMap;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.CityStateZipMapRepository;
import com.hillrom.vest.util.ExceptionConstants;

import bsh.StringUtil;

@Service
@Transactional
public class CityStateZipMapService {

	@Inject
	private CityStateZipMapRepository cityStateZipMapRepository;
	
	public  List<CityStateZipMap> getByCityName(String city) throws HillromException{
		if(StringUtils.isEmpty(city))
			throw new HillromException(ExceptionConstants.HR_709);
		
		List<CityStateZipMap> cityStateZipMaps = cityStateZipMapRepository.findAllByCity(city); 
		if(Objects.nonNull(cityStateZipMaps) & !cityStateZipMaps.isEmpty())
			return cityStateZipMaps;
		else throw new HillromException(ExceptionConstants.HR_709);
	}
	
	public List<CityStateZipMap> getByZipCode(String zip) throws HillromException{
		
		Integer zipCode = Integer.parseInt(zip);
		
		if(Objects.isNull(zipCode))
			throw new HillromException(ExceptionConstants.HR_711);
			
		List<CityStateZipMap> cityStateZipMaps = cityStateZipMapRepository.findAllByZipCode(zipCode); 
		if(Objects.nonNull(cityStateZipMaps) & !cityStateZipMaps.isEmpty())
			return cityStateZipMaps;
		else throw new HillromException(ExceptionConstants.HR_711);
	}
	
	public List<CityStateZipMap> getByState(String state) throws HillromException{
		if(StringUtils.isEmpty(state))
			throw new HillromException(ExceptionConstants.HR_710);
		
		List<CityStateZipMap> cityStateZipMaps = cityStateZipMapRepository.findAllByState(state); 
		if(Objects.nonNull(cityStateZipMaps) & !cityStateZipMaps.isEmpty())
			return cityStateZipMaps;
		else throw new HillromException(ExceptionConstants.HR_710);
	}
}
