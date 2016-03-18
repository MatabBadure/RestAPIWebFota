package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.BM_TYPE_AVERAGE;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_SELF;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_CLINIC;
import static com.hillrom.vest.service.util.BenchMarkUtil.mapBenchMarkByAgeGroup;
import static com.hillrom.vest.service.util.BenchMarkUtil.mapBenchMarkByClinicSize;
import static com.hillrom.vest.service.util.BenchMarkUtil.prepareBenchMarkData;
import static com.hillrom.vest.service.util.BenchMarkUtil.prepareDefaultDataByAgeGroupOrClinicSize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.BenchmarkRepository;
import com.hillrom.vest.repository.BenchmarkResultVO;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;

@Service
@Transactional
public class BenchmarkService {
	
	@Inject
	private BenchmarkRepository benchmarkRepository;
	
	public SortedMap<String,BenchMarkDataVO> getBenchmarkDataForAdminParameterView(BenchMarkFilter filter) {
		List<BenchmarkResultVO> benchmarkVOs = new LinkedList<>();
		Map<String, List<BenchmarkResultVO>> groupBenchMarkMap = new HashMap<>();
		SortedMap<String,BenchMarkDataVO> defaultBenchMarkData = new TreeMap<>();
		if(BM_TYPE_AVERAGE.equalsIgnoreCase(filter.getBenchMarkType()) || Objects.isNull(filter.getBenchMarkType())){
			if(AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())){
				benchmarkVOs = benchmarkRepository.getAverageBenchmarkByAge(filter.getFrom(), filter.getTo(), filter.getCityCSV(), filter.getStateCSV());
				groupBenchMarkMap = mapBenchMarkByAgeGroup(benchmarkVOs);
			}
			else{
				benchmarkVOs = benchmarkRepository.getAverageBenchmarkByClinicSize(filter.getFrom(), filter.getTo(), filter.getCityCSV(), filter.getStateCSV());
				groupBenchMarkMap = mapBenchMarkByClinicSize(benchmarkVOs);
			}
			defaultBenchMarkData = prepareDefaultDataByAgeGroupOrClinicSize(filter);
		}
		BenchMarkStrategy benchMarkStrategy = BenchMarkStrategyFactory.getBenchMarkStrategy(filter.getBenchMarkType());
		for(String ageRangeLabel : defaultBenchMarkData.keySet()){
			List<BenchmarkResultVO> values = groupBenchMarkMap.get(ageRangeLabel);
			if(Objects.nonNull(values)){
				BenchMarkDataVO benchMarkDataVO = prepareBenchMarkData(
						filter.getBenchMarkParameter(), benchMarkStrategy, ageRangeLabel,
						values);
				defaultBenchMarkData.put(ageRangeLabel, benchMarkDataVO);
			}
		}
		return defaultBenchMarkData;
	}
	
	@SuppressWarnings("unused")
	public Map<String,SortedMap<String,BenchMarkDataVO>> getBenchmarkDataForClinicByAgeGroup(BenchMarkFilter filter) throws HillromException {
		
			List<BenchmarkResultVO> benchmarkVOs = new LinkedList<>();
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForClinicMap = new HashMap<>();
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForPatientMap = new HashMap<>();
			SortedMap<String,BenchMarkDataVO> defaultBenchMarkDataForClinic = new TreeMap<>();
			SortedMap<String,BenchMarkDataVO> defaultBenchMarkDataForUser = new TreeMap<>();
			if(BM_TYPE_AVERAGE.equalsIgnoreCase(filter.getBenchMarkType()) || Objects.isNull(filter.getBenchMarkType())){
				benchmarkVOs = benchmarkRepository.getAverageBenchmarkForClinicByAgeGroup(filter.getFrom(),
						filter.getTo(), filter.getCityCSV(), filter.getStateCSV(),filter.getClinicId());
				Map<Long,List<BenchmarkResultVO>> userBenchmarkData = benchmarkVOs.stream().collect(Collectors.groupingBy(BenchmarkResultVO :: getUserId));
				groupBenchMarkForClinicMap = mapBenchMarkByAgeGroup(benchmarkVOs);
				if(Objects.nonNull(userBenchmarkData.get(filter.getUserId())))
					groupBenchMarkForPatientMap = mapBenchMarkByAgeGroup(userBenchmarkData.get(filter.getUserId()));
				else
					throw new HillromException(ExceptionConstants.HR_808);
				defaultBenchMarkDataForClinic = prepareDefaultDataByAgeGroupOrClinicSize(filter);
				defaultBenchMarkDataForUser = prepareDefaultDataByAgeGroupOrClinicSize(filter);
			}
			BenchMarkStrategy benchMarkStrategy = BenchMarkStrategyFactory.getBenchMarkStrategy(filter.getBenchMarkType());
			
			for(String ageRangeLabel : defaultBenchMarkDataForClinic.keySet()){
				List<BenchmarkResultVO> clinicLevelValues = groupBenchMarkForClinicMap.get(ageRangeLabel);
				List<BenchmarkResultVO> userLevelValues = groupBenchMarkForPatientMap.get(ageRangeLabel);
				//Clinic 
				if(Objects.nonNull(clinicLevelValues)){
					BenchMarkDataVO benchMarkDataVO = prepareBenchMarkData(
							filter.getBenchMarkParameter(), benchMarkStrategy, ageRangeLabel,
							clinicLevelValues);
					defaultBenchMarkDataForClinic.put(ageRangeLabel, benchMarkDataVO);
				}
				//User
				if(Objects.nonNull(userLevelValues)){
					BenchMarkDataVO benchMarkDataVO = prepareBenchMarkData(
							filter.getBenchMarkParameter(), benchMarkStrategy, ageRangeLabel,
							userLevelValues);
					defaultBenchMarkDataForUser.put(ageRangeLabel, benchMarkDataVO);
				}
			}
			Map<String,SortedMap<String,BenchMarkDataVO>> defaultBenchMarkData = new HashMap<>();
			defaultBenchMarkData.put(BENCHMARK_DATA_CLINIC, defaultBenchMarkDataForClinic);
			defaultBenchMarkData.put(BENCHMARK_DATA_SELF, defaultBenchMarkDataForUser);
			return defaultBenchMarkData;
		}
	
	}
