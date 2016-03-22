package com.hillrom.vest.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.hillrom.vest.repository.ClinicAndDiseaseStatisticsRepository;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;

@Service
public class ClinicDiseaseStatsService {

	@Inject
	private ClinicAndDiseaseStatisticsRepository statisticsRepository;
	
	public void test(BenchMarkFilter filter){
		statisticsRepository.getClinicDiseaseStatsByAgeGroupOrClinicSize(filter);
	}
}
