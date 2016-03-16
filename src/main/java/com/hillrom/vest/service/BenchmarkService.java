package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_TYPE_AVERAGE;

import java.math.BigDecimal;
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

import org.springframework.stereotype.Service;

import com.hillrom.vest.repository.BenchmarkRepository;
import com.hillrom.vest.repository.BenchmarkResultVO;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;

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

	
	private static final String CLINIC_SIZE_RANGE_1_TO_25 = "1-25";
	private static final String CLINIC_SIZE_RANGE_26_TO_50 = "26-50";
	private static final String CLINIC_SIZE_RANGE_51_TO_75 = "51-75";
	private static final String CLINIC_SIZE_RANGE_76_TO_100 = "76-100";
	private static final String CLINIC_SIZE_RANGE_101_TO_150 = "101-150";
	private static final String CLINIC_SIZE_RANGE_151_TO_200 = "151-200";
	private static final String CLINIC_SIZE_RANGE_201_TO_250 = "201-250";
	private static final String CLINIC_SIZE_RANGE_251_TO_300 = "251-300";
	private static final String CLINIC_SIZE_RANGE_301_TO_350 = "301-350";
	private static final String CLINIC_SIZE_RANGE_351_TO_400 = "351-400";
	private static final String CLINIC_SIZE_RANGE_401_AND_ABOVE = "401-above";

	@Inject
	private BenchmarkRepository benchmarkRepository;
	
	public List<BenchMarkDataVO> getBenchmarkDataByAgeGroupOrClinicSize(BenchMarkFilter filter) {
		List<BenchmarkResultVO> benchmarkVOs = new LinkedList<>();
		Map<String, List<BenchmarkResultVO>> groupBenchMarkMap = new HashMap<>();
		SortedMap<String,BenchMarkDataVO> defaultBenchMarkData = new TreeMap<>();
		if(BM_TYPE_AVERAGE.equalsIgnoreCase(filter.getBenchMarkType()) || Objects.isNull(filter.getBenchMarkType())){
			if(AGE_GROUP.equalsIgnoreCase(filter.getxAxisParameter())){
				benchmarkVOs = benchmarkRepository.getAverageBenchmarkByAge(filter.getFrom(), filter.getTo(), filter.getCityCSV(), filter.getStateCSV());
				groupBenchMarkMap = mapBenchMarkByAgeGroup(benchmarkVOs);
				defaultBenchMarkData = prepareDefaultDataByAgeGroupOrClinicSize(filter.getRangeCSV(),null);
			}
			else{
				benchmarkVOs = benchmarkRepository.getAverageBenchmarkByClinicSize(filter.getFrom(), filter.getTo(), filter.getCityCSV(), filter.getStateCSV());
				groupBenchMarkMap = mapBenchMarkByClinicSize(benchmarkVOs);
				defaultBenchMarkData = prepareDefaultDataByAgeGroupOrClinicSize(null,filter.getRangeCSV());
			}
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
		return new LinkedList<>(defaultBenchMarkData.values());
	}

	private BenchMarkDataVO prepareBenchMarkData(String benchMarkParameter,
			BenchMarkStrategy benchMarkStrategy, String rangeLabel,
			List<BenchmarkResultVO> values) {
		BenchMarkDataVO benchMarkDataVO = new BenchMarkDataVO(rangeLabel,values.size());
		switch(benchMarkParameter){
		case BM_PARAM_ADHERENCE_SCORE: setAdherenceScoreBenchMark(benchMarkStrategy,values,benchMarkDataVO);
		break;
		case BM_PARAM_MISSED_THERAPY_DAYS: setMissedTherapyBenchMark(benchMarkStrategy,values,benchMarkDataVO);
		break;
		case BM_PARAM_HMR_DEVIATION: setHMRDeviationBenchMark(benchMarkStrategy, values,benchMarkDataVO);
		break;
		case BM_PARAM_SETTING_DEVIATION: setSettingDeviationBenchMark(benchMarkStrategy,values,benchMarkDataVO);
		break;
		default: setAdherenceScoreBenchMark(benchMarkStrategy,values,benchMarkDataVO);
		}
		return benchMarkDataVO;
	}

	public void setSettingDeviationBenchMark(
			BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeSettingsDeviatedCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setSettingDeviationBenchMark((int)benchMarkValue);
	}

	public void setHMRDeviationBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeNonAdherenceCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.sethMRDeviationBenchMark((int)benchMarkValue);
	}

	public void setMissedTherapyBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeMissedTherapyDaysCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setMissedTherapyDaysBenchMark((int)benchMarkValue);
	}

	public void setAdherenceScoreBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values,BenchMarkDataVO benchMarkDataVO) {
		List<BigDecimal> paramValues = new LinkedList<>();
		double benchMarkValue;
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeComplience());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setAdherenceScoreBenchMark((int)benchMarkValue);
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
				rangeLabels = Arrays.asList(CLINIC_SIZE_RANGE_1_TO_25,CLINIC_SIZE_RANGE_26_TO_50,
					CLINIC_SIZE_RANGE_51_TO_75,CLINIC_SIZE_RANGE_76_TO_100,CLINIC_SIZE_RANGE_101_TO_150,
					CLINIC_SIZE_RANGE_151_TO_200,CLINIC_SIZE_RANGE_201_TO_250,CLINIC_SIZE_RANGE_251_TO_300,
					CLINIC_SIZE_RANGE_301_TO_350,CLINIC_SIZE_RANGE_351_TO_400,CLINIC_SIZE_RANGE_401_AND_ABOVE);
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
				addBenchMarkToMap(AGE_RANGE_0_TO_5,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 10)
				addBenchMarkToMap(AGE_RANGE_6_TO_10,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 15)
				addBenchMarkToMap(AGE_RANGE_11_TO_15,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 20)
				addBenchMarkToMap(AGE_RANGE_16_TO_20,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 25)
				addBenchMarkToMap(AGE_RANGE_21_TO_25,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 30)
				addBenchMarkToMap(AGE_RANGE_26_TO_30,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 35)
				addBenchMarkToMap(AGE_RANGE_31_TO_35,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 40)
				addBenchMarkToMap(AGE_RANGE_36_TO_40,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 45)
				addBenchMarkToMap(AGE_RANGE_41_TO_45,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 50)
				addBenchMarkToMap(AGE_RANGE_46_TO_50,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 55)
				addBenchMarkToMap(AGE_RANGE_51_TO_55,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 60)
				addBenchMarkToMap(AGE_RANGE_56_TO_60,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 65)
				addBenchMarkToMap(AGE_RANGE_61_TO_65,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 70)
				addBenchMarkToMap(AGE_RANGE_66_TO_70,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 75)
				addBenchMarkToMap(AGE_RANGE_71_TO_75,ageRangeBenchmarkVOMap, benchmarkVO);
			else if (benchmarkVO.getAge() <= 80)
				addBenchMarkToMap(AGE_RANGE_76_TO_80,ageRangeBenchmarkVOMap, benchmarkVO);
			else
				addBenchMarkToMap(AGE_RANGE_81_AND_ABOVE,ageRangeBenchmarkVOMap, benchmarkVO);
		}
		return ageRangeBenchmarkVOMap;
	}

	public void addBenchMarkToMap(String key,Map<String, List<BenchmarkResultVO>> rangeBenchMarkVOMap,
			BenchmarkResultVO benchmarkVO) {
		List<BenchmarkResultVO> benchmarkData = rangeBenchMarkVOMap.getOrDefault(key, new LinkedList<>());
		benchmarkData.add(benchmarkVO);
		rangeBenchMarkVOMap.put(key, benchmarkData);
	}

	private Map<String, List<BenchmarkResultVO>> mapBenchMarkByClinicSize(List<BenchmarkResultVO> benchmarkVOs) {
		Map<String, List<BenchmarkResultVO>> clinicSizeBenchMarkMap = new HashMap<>();
		for (BenchmarkResultVO benchmarkVO : benchmarkVOs) {
			if (benchmarkVO.getClinicSize() <= 25)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_1_TO_25,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 50)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_26_TO_50,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 75)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_51_TO_75,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 100)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_76_TO_100,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 150)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_101_TO_150,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 200)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_151_TO_200,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 250)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_201_TO_250,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 300)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_251_TO_300,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 350)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_301_TO_350,clinicSizeBenchMarkMap, benchmarkVO);
			else if (benchmarkVO.getClinicSize() <= 400)
				addBenchMarkToMap(CLINIC_SIZE_RANGE_351_TO_400,clinicSizeBenchMarkMap, benchmarkVO);
			else
				addBenchMarkToMap(CLINIC_SIZE_RANGE_401_AND_ABOVE,clinicSizeBenchMarkMap, benchmarkVO);
		}
		return clinicSizeBenchMarkMap;
	}

}
