package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.BM_TYPE_AVERAGE;
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

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.hillrom.vest.repository.BenchmarkRepository;
import com.hillrom.vest.repository.BenchmarkResultVO;
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

}
