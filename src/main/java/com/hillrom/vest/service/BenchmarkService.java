package com.hillrom.vest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.repository.BenchmarkRepository;
import com.hillrom.vest.repository.BenchmarkVO;

@Service
@Transactional
public class BenchmarkService {
	private static final String AGE_RANGE_0_TO_5 = "0-5";
	private static final String AGE_RANGE_6_TO_10 = "6-10";
	private static final String AGE_RANGE_11_TO_15 = "11-15";
	private static final String AGE_RANGE_16_TO_20 = "16-20";
	private static final String AGE_RANGE_21_TO_25 = "21-25";
	private static final String AGE_RANGE_26_TO_30 = "26-30";
	private static final String AGE_RANGE_31_TO_35 = "31-35";
	private static final String AGE_RANGE_36_TO_40 = "36-40";
	private static final String AGE_RANGE_41_TO_45 = "41-45";
	private static final String AGE_RANGE_46_TO_50 = "46-50";
	private static final String AGE_RANGE_51_TO_55 = "51-55";
	private static final String AGE_RANGE_56_TO_60 = "56-60";
	private static final String AGE_RANGE_61_TO_65 = "61-65";
	private static final String AGE_RANGE_66_TO_70 = "66-70";
	private static final String AGE_RANGE_71_TO_75 = "71-75";
	private static final String AGE_RANGE_76_TO_80 = "76-80";
	private static final String AGE_RANGE_81_AND_ABOVE = "81-above";

	@Inject
	private BenchmarkRepository benchmarkRepository;
	
	public Map<String, List<BenchmarkVO>> getBenchmarkVOGroupedByAge(String ageRangeCSV, LocalDate fromDate,
			LocalDate toDate, String stateCSV, String cityCSV, String benchMarkType) {

		List<BenchmarkVO> benchmarkVOs = benchmarkRepository.getAverageBenchmark(fromDate, toDate, cityCSV, stateCSV);
		Map<String, List<BenchmarkVO>> ageGroupMap = mapBenchMarkByAgeGroup(benchmarkVOs);
		//Declare a Map<String,Map<String,Object>> benchMarkData to be returned from the method
		// Get BenchMarkStrategy from BenchMarkStrategyFactory
		//Iterate ageGroupMap/clinicSizeGroupMap
		// create a Map<String,Object> metaDataForBenchMark
		// metaDataForBenchMark.put("patientsCount",<size of VO List per groupLabel>)
		// * For each groupLabel of ageGroupMap/clinicSizeGroupMap
		// 	benchMarkValue = calculateBenchMark using benchMarkStrategy 
		// 	metaDataForBenchMark.put(<label for benchMarkParameter>,<benchMarkValue>)
		// * finish loop  
		// benchMarkData.put(groupLabel,metaDataForBenchMark)
		return null;
	}

	private Map<String, List<BenchmarkVO>> mapBenchMarkByAgeGroup(List<BenchmarkVO> benchmarkVOs) {
		Map<String, List<BenchmarkVO>> ageRangeBenchmarkVOMap = new HashMap<>();
		for (BenchmarkVO benchmarkVO : benchmarkVOs) {
			if (benchmarkVO.getAge() <= 5)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_0_TO_5).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 10)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_6_TO_10).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 15)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_11_TO_15).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 20)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_16_TO_20).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 25)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_21_TO_25).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 30)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_26_TO_30).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 35)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_31_TO_35).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 40)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_36_TO_40).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 45)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_41_TO_45).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 50)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_46_TO_50).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 55)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_51_TO_55).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 60)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_56_TO_60).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 65)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_61_TO_65).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 70)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_66_TO_70).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 75)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_71_TO_75).add(benchmarkVO);
			else if (benchmarkVO.getAge() <= 80)
				ageRangeBenchmarkVOMap.get(AGE_RANGE_76_TO_80).add(benchmarkVO);
			else
				ageRangeBenchmarkVOMap.get(AGE_RANGE_81_AND_ABOVE).add(benchmarkVO);
		}
		return ageRangeBenchmarkVOMap;
	}

	private Map<String, List<BenchmarkVO>> getAgeRangedMap(String ageRangeCSV) {

		Map<String, List<BenchmarkVO>> ageRangeBenchmarkVOMap = new HashMap<>();
		String[] ageRangeArray = ageRangeCSV.split(",");
		for (String ageRange : ageRangeArray)
			ageRangeBenchmarkVOMap.put(ageRange, new ArrayList<BenchmarkVO>());
		return ageRangeBenchmarkVOMap;
	}
}
