package com.hillrom.vest.service.util;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.AGE_RANGE_0_TO_5;
import static com.hillrom.vest.config.Constants.AGE_RANGE_11_TO_15;
import static com.hillrom.vest.config.Constants.AGE_RANGE_16_TO_20;
import static com.hillrom.vest.config.Constants.AGE_RANGE_21_TO_25;
import static com.hillrom.vest.config.Constants.AGE_RANGE_26_TO_30;
import static com.hillrom.vest.config.Constants.AGE_RANGE_31_TO_35;
import static com.hillrom.vest.config.Constants.AGE_RANGE_36_TO_40;
import static com.hillrom.vest.config.Constants.AGE_RANGE_41_TO_45;
import static com.hillrom.vest.config.Constants.AGE_RANGE_46_TO_50;
import static com.hillrom.vest.config.Constants.AGE_RANGE_51_TO_55;
import static com.hillrom.vest.config.Constants.AGE_RANGE_56_TO_60;
import static com.hillrom.vest.config.Constants.AGE_RANGE_61_TO_65;
import static com.hillrom.vest.config.Constants.AGE_RANGE_66_TO_70;
import static com.hillrom.vest.config.Constants.AGE_RANGE_6_TO_10;
import static com.hillrom.vest.config.Constants.AGE_RANGE_71_TO_75;
import static com.hillrom.vest.config.Constants.AGE_RANGE_76_TO_80;
import static com.hillrom.vest.config.Constants.AGE_RANGE_81_AND_ABOVE;
import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_RUNRATE;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_101_TO_150;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_151_TO_200;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_1_TO_25;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_201_TO_250;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_251_TO_300;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_26_TO_50;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_301_TO_350;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_351_TO_400;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_401_AND_ABOVE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_51_TO_75;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_76_TO_100;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import com.hillrom.vest.service.BenchMarkStrategy;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;

public class BenchMarkUtil {

	
	public static SortedMap<String, BenchMarkDataVO> prepareDefaultDataByAgeGroupOrClinicSize(
			BenchMarkFilter filter) {
		SortedMap<String,BenchMarkDataVO> benchMarkData = new TreeMap<>();
		List<String> rangeLabels = getRangeLabels(filter);
		rangeLabels.forEach(label -> {
			benchMarkData.put(label, new BenchMarkDataVO(label, 0));
		});
		return benchMarkData;
	}
	
	public static BenchMarkDataVO prepareBenchMarkData(String benchMarkParameter,
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
		case BM_PARAM_HMR_RUNRATE: setHMRRunrateBenchMark(benchMarkStrategy,values,benchMarkDataVO);
		break;
		default: setAdherenceScoreBenchMark(benchMarkStrategy,values,benchMarkDataVO);
		}
		return benchMarkDataVO;
	}

	public static void setHMRRunrateBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumulativeHMRRunrate());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.sethMRRunrateBenchMark((int)benchMarkValue);		
	}

	public static void setSettingDeviationBenchMark(
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

	public static void setHMRDeviationBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeNonAdherenceCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.sethMRDeviationBenchMark((int)benchMarkValue);
	}

	public static void setMissedTherapyBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeMissedTherapyDaysCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setMissedTherapyDaysBenchMark((int)benchMarkValue);
	}

	public static void setAdherenceScoreBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values,BenchMarkDataVO benchMarkDataVO) {
		List<BigDecimal> paramValues = new LinkedList<>();
		double benchMarkValue;
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeComplience());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setAdherenceScoreBenchMark((int)benchMarkValue);
	}

	public static List<String> getRangeLabels(BenchMarkFilter filter) {
		List<String> rangeLabels = new LinkedList<>();
		String parameter = filter.getxAxisParameter();
		if(Objects.nonNull(parameter) && AGE_GROUP.equalsIgnoreCase(parameter)){
			if("All".equalsIgnoreCase(filter.getAgeRangeCSV())){
				rangeLabels = Arrays.asList(AGE_RANGE_0_TO_5,AGE_RANGE_6_TO_10,AGE_RANGE_11_TO_15,
						AGE_RANGE_16_TO_20,AGE_RANGE_21_TO_25,AGE_RANGE_26_TO_30,AGE_RANGE_31_TO_35,
						AGE_RANGE_36_TO_40,AGE_RANGE_41_TO_45,AGE_RANGE_46_TO_50,AGE_RANGE_51_TO_55,
						AGE_RANGE_56_TO_60,AGE_RANGE_61_TO_65,AGE_RANGE_66_TO_70,AGE_RANGE_71_TO_75,
						AGE_RANGE_76_TO_80,AGE_RANGE_81_AND_ABOVE);
			}else{
				rangeLabels = Arrays.asList(filter.getAgeRangeCSV().split(","));
			}
		}else if(Objects.nonNull(parameter) && CLINIC_SIZE.equalsIgnoreCase(parameter)){
			if("All".equalsIgnoreCase(filter.getClinicSizeRangeCSV())){
				rangeLabels = Arrays.asList(CLINIC_SIZE_RANGE_1_TO_25,CLINIC_SIZE_RANGE_26_TO_50,
					CLINIC_SIZE_RANGE_51_TO_75,CLINIC_SIZE_RANGE_76_TO_100,CLINIC_SIZE_RANGE_101_TO_150,
					CLINIC_SIZE_RANGE_151_TO_200,CLINIC_SIZE_RANGE_201_TO_250,CLINIC_SIZE_RANGE_251_TO_300,
					CLINIC_SIZE_RANGE_301_TO_350,CLINIC_SIZE_RANGE_351_TO_400,CLINIC_SIZE_RANGE_401_AND_ABOVE);
			}else{
				rangeLabels = Arrays.asList(filter.getClinicSizeRangeCSV().split(","));
			}
		}
		return rangeLabels;
	}
	

	public static Map<String, List<BenchmarkResultVO>> mapBenchMarkByAgeGroup(List<BenchmarkResultVO> benchmarkVOs) {
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

	public static void addBenchMarkToMap(String key,Map<String, List<BenchmarkResultVO>> rangeBenchMarkVOMap,
			BenchmarkResultVO benchmarkVO) {
		List<BenchmarkResultVO> benchmarkData = rangeBenchMarkVOMap.getOrDefault(key, new LinkedList<>());
		benchmarkData.add(benchmarkVO);
		rangeBenchMarkVOMap.put(key, benchmarkData);
	}

	public static Map<String, List<BenchmarkResultVO>> mapBenchMarkByClinicSize(List<BenchmarkResultVO> benchmarkVOs) {
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
