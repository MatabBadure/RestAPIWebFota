package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.repository.BenchmarkRepository;
import com.hillrom.vest.repository.BenchmarkResultVO;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;

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
	
	public List<BenchMarkDataVO> getBenchmarkVOGroupedByAge(String ageRangeCSV, LocalDate fromDate,
			LocalDate toDate, String stateCSV, String cityCSV, String benchMarkType,String benchMarkParameter) {
		List<BenchmarkResultVO> benchmarkVOs = benchmarkRepository.getAverageBenchmark(fromDate, toDate, cityCSV, stateCSV);
		Map<String, List<BenchmarkResultVO>> ageGroupBenchMarkMap = mapBenchMarkByAgeGroup(benchmarkVOs);
		BenchMarkStrategy benchMarkStrategy = BenchMarkStrategyFactory.getBenchMarkStrategy(benchMarkType);
		SortedMap<String,BenchMarkDataVO> defaultBenchMarkData = prepareDefaultDataByAgeGroupOrClinicSize(ageRangeCSV,null);
		for(String ageRangeLabel : defaultBenchMarkData.keySet()){
			List<BenchmarkResultVO> values = ageGroupBenchMarkMap.get(ageRangeLabel);
			if(Objects.nonNull(values)){
				BenchMarkDataVO benchMarkDataVO = prepareBenchMarkData(
						benchMarkParameter, benchMarkStrategy, ageRangeLabel,
						values);
				defaultBenchMarkData.put(ageRangeLabel, benchMarkDataVO);
			}
		}
		return new LinkedList<>(defaultBenchMarkData.values());
	}

	private BenchMarkDataVO prepareBenchMarkData(String benchMarkParameter,
			BenchMarkStrategy benchMarkStrategy, String rangeLabel,
			List<BenchmarkResultVO> values) {
		List<BigDecimal> paramValues = new LinkedList<>();
		BenchMarkDataVO benchMarkDataVO = new BenchMarkDataVO(rangeLabel,values.size());
		double benchMarkValue = 0;
		if(BM_PARAM_ADHERENCE_SCORE.equalsIgnoreCase(benchMarkParameter)){
			values.stream().forEach(benchmarkVO -> {
				paramValues.add(benchmarkVO.getCumilativeComplience());
			});
			benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
			benchMarkDataVO.setAdherenceScoreBenchMark((int)benchMarkValue);
		}else if(BM_PARAM_MISSED_THERAPY_DAYS.equalsIgnoreCase(benchMarkParameter)){
			values.stream().forEach(benchmarkVO -> {
				paramValues.add(benchmarkVO.getCumilativeMissedTherapyDaysCount());
			});
			benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
			benchMarkDataVO.setMissedTherapyDaysBenchMark((int)benchMarkValue);
		}else if(BM_PARAM_HMR_DEVIATION.equalsIgnoreCase(benchMarkParameter)){
			values.stream().forEach(benchmarkVO -> {
				paramValues.add(benchmarkVO.getCumilativeNonAdherenceCount());
			});
			benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
			benchMarkDataVO.sethMRDeviationBenchMark((int)benchMarkValue);
		}else if(BM_PARAM_SETTING_DEVIATION.equalsIgnoreCase(benchMarkParameter)){
			values.stream().forEach(benchmarkVO -> {
				paramValues.add(benchmarkVO.getCumilativeSettingsDeviatedCount());
			});
			benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
			benchMarkDataVO.setSettingDeviationBenchMark((int)benchMarkValue);
		}
		return benchMarkDataVO;
	}

	private SortedMap<String, BenchMarkDataVO> prepareDefaultDataByAgeGroupOrClinicSize(
			String ageRangeCSV, String clinicSizeCSV) {
		SortedMap<String,BenchMarkDataVO> benchMarkData = new TreeMap<>();
		List<String> rangeLabels = new LinkedList<>();  
		if(Objects.nonNull(ageRangeCSV)){
			if(ageRangeCSV.equalsIgnoreCase("All")){
				rangeLabels = Arrays.asList(AGE_RANGE_0_TO_5,AGE_RANGE_6_TO_10,AGE_RANGE_11_TO_15,
						AGE_RANGE_16_TO_20,AGE_RANGE_21_TO_25,AGE_RANGE_26_TO_30,AGE_RANGE_31_TO_35,
						AGE_RANGE_36_TO_40,AGE_RANGE_41_TO_45,AGE_RANGE_46_TO_50,AGE_RANGE_51_TO_55,
						AGE_RANGE_56_TO_60,AGE_RANGE_61_TO_65,AGE_RANGE_66_TO_70,AGE_RANGE_71_TO_75,
						AGE_RANGE_76_TO_80,AGE_RANGE_81_AND_ABOVE);
			}else{
				rangeLabels = Arrays.asList(ageRangeCSV.split(","));
			}
		}else if(Objects.nonNull(clinicSizeCSV)){
			if(clinicSizeCSV.equalsIgnoreCase("All")){
				// to be updated with Clinic Size labels
				rangeLabels = Arrays.asList(AGE_RANGE_0_TO_5,AGE_RANGE_6_TO_10,AGE_RANGE_11_TO_15,
						AGE_RANGE_16_TO_20,AGE_RANGE_21_TO_25,AGE_RANGE_26_TO_30,AGE_RANGE_31_TO_35,
						AGE_RANGE_36_TO_40,AGE_RANGE_41_TO_45,AGE_RANGE_46_TO_50,AGE_RANGE_51_TO_55,
						AGE_RANGE_56_TO_60,AGE_RANGE_61_TO_65,AGE_RANGE_66_TO_70,AGE_RANGE_71_TO_75,
						AGE_RANGE_76_TO_80,AGE_RANGE_81_AND_ABOVE);
			}else{
				rangeLabels = Arrays.asList(clinicSizeCSV.split(","));
			}
		}
		rangeLabels.forEach(label -> {
			benchMarkData.put(label, new BenchMarkDataVO(label, 0));
		});
		return benchMarkData;
	}

	private Map<String, List<BenchmarkResultVO>> mapBenchMarkByAgeGroup(List<BenchmarkResultVO> benchmarkVOs) {
		Map<String, List<BenchmarkResultVO>> ageRangeBenchmarkVOMap = new HashMap<>();
		for (BenchmarkResultVO benchmarkVO : benchmarkVOs) {
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

	private Map<String, List<BenchmarkResultVO>> getAgeRangedMap(String ageRangeCSV) {

		Map<String, List<BenchmarkResultVO>> ageRangeBenchmarkVOMap = new HashMap<>();
		String[] ageRangeArray = ageRangeCSV.split(",");
		for (String ageRange : ageRangeArray)
			ageRangeBenchmarkVOMap.put(ageRange, new ArrayList<BenchmarkResultVO>());
		return ageRangeBenchmarkVOMap;
	}
}
