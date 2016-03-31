package com.hillrom.vest.service.util;

import static com.hillrom.vest.config.Constants.*;
import static com.hillrom.vest.config.Constants.AGE_RANGE_81_AND_ABOVE;
import static com.hillrom.vest.config.Constants.AGE_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_RUNRATE;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_401_AND_ABOVE;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.RANGE_SEPARATOR;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.hillrom.vest.service.BenchMarkStrategy;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;
import com.hillrom.vest.web.rest.dto.ClinicDiseaseStatisticsResultVO;
import com.hillrom.vest.web.rest.dto.Filter;

public class BenchMarkUtil {

	public static final RangeMap<Integer,String> AGE_RANGE_MAP = getAgeRangeMap();
	public static final RangeMap<Integer,String> CLINIC_SIZE_RANGE_MAP = getClinicSizeRangeMap();
	
	public static Map<String, BenchMarkDataVO> prepareDefaultDataByAgeGroupOrClinicSize(
			BenchMarkFilter filter) {
		Map<String,BenchMarkDataVO> benchMarkData = new LinkedHashMap<>();
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
		switch(benchMarkParameter.toLowerCase()){
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
		benchMarkDataVO.sethMRRunrateBenchMark((int)Math.round(benchMarkValue));		
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
		benchMarkDataVO.setSettingDeviationBenchMark((int)Math.round(benchMarkValue));
	}

	public static void setHMRDeviationBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeNonAdherenceCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.sethMRDeviationBenchMark((int)Math.round(benchMarkValue));
	}

	public static void setMissedTherapyBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values, BenchMarkDataVO benchMarkDataVO) {
		double benchMarkValue;
		List<BigDecimal> paramValues = new LinkedList<>();
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeMissedTherapyDaysCount());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setMissedTherapyDaysBenchMark((int)Math.round(benchMarkValue));
	}

	public static void setAdherenceScoreBenchMark(BenchMarkStrategy benchMarkStrategy,
			List<BenchmarkResultVO> values,BenchMarkDataVO benchMarkDataVO) {
		List<BigDecimal> paramValues = new LinkedList<>();
		double benchMarkValue;
		values.stream().forEach(benchmarkVO -> {
			paramValues.add(benchmarkVO.getCumilativeComplience());
		});
		benchMarkValue = benchMarkStrategy.calculateBenchMark(paramValues);
		benchMarkDataVO.setAdherenceScoreBenchMark((int)Math.round(benchMarkValue));
	}

	public static List<String> getRangeLabels(Filter filter){
		String rangeCSV = filter.getAgeRangeCSV();
		if(Objects.isNull(rangeCSV)){
			rangeCSV = filter.getClinicSizeRangeCSV();
		}
		return getRangeLabels(filter.getxAxisParameter(), rangeCSV);
	}
	
	public static List<String> getRangeLabels(String parameter,String rangeCSV) {
		List<String> rangeLabels = new LinkedList<>();
		if(Objects.nonNull(parameter) && AGE_GROUP.equalsIgnoreCase(parameter)){
			if("All".equalsIgnoreCase(rangeCSV)){
				rangeLabels = Arrays.asList(AGE_RANGE_LABELS);
			}else{
				rangeLabels = Arrays.asList(rangeCSV.split(","));
			}
		}else if(Objects.nonNull(parameter) && CLINIC_SIZE.equalsIgnoreCase(parameter)){
			if("All".equalsIgnoreCase(rangeCSV)){
				rangeLabels = Arrays.asList(CLINIC_SIZE_RANGE_LABELS);
			}else{
				rangeLabels = Arrays.asList(rangeCSV.split(","));
			}
		}
		return rangeLabels;
	}
	

	public static Map<String, List<BenchmarkResultVO>> mapBenchMarkByAgeGroup(List<BenchmarkResultVO> benchmarkVOs) {
		Map<String, List<BenchmarkResultVO>> ageRangeBenchmarkVOMap = new HashMap<>();
		for (BenchmarkResultVO benchmarkVO : benchmarkVOs) {
			String ageRange = AGE_RANGE_MAP.get(benchmarkVO.getAge());
			addBenchMarkToMap(ageRange,ageRangeBenchmarkVOMap, benchmarkVO);
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
			String clinicSizeRange = CLINIC_SIZE_RANGE_MAP.get(benchmarkVO.getClinicSize());
			addBenchMarkToMap(clinicSizeRange,clinicSizeBenchMarkMap, benchmarkVO);
		}
		return clinicSizeBenchMarkMap;
	}
	
	public static RangeMap<Integer,String> createRangeMap() {
		RangeMap<Integer,String> rangeMap = TreeRangeMap.create();
		return  rangeMap;
	}
	
	public static RangeMap<Integer,String> getAgeRangeMap(){
		RangeMap<Integer,String> ageRangeMap = createRangeMap();
		for(String ageRange : AGE_RANGE_LABELS){
			String range[] = ageRange.split(RANGE_SEPARATOR);
			if(!AGE_RANGE_81_AND_ABOVE.equalsIgnoreCase(ageRange)){
				ageRangeMap.put(Range.closed(Integer.parseInt(range[0]), Integer.parseInt(range[1])),ageRange);
			}else{
				ageRangeMap.put(Range.closed(Integer.parseInt(range[0]), Integer.parseInt("100")),ageRange);
			}
		}
		return ageRangeMap;
	}

	public static RangeMap<Integer,String> getClinicSizeRangeMap(){
		RangeMap<Integer,String> clinicSizeRangeMap = createRangeMap();
		for(String ageRange : CLINIC_SIZE_RANGE_LABELS){
			String range[] = ageRange.split(RANGE_SEPARATOR);
			if(!CLINIC_SIZE_RANGE_401_AND_ABOVE.equalsIgnoreCase(ageRange)){
				clinicSizeRangeMap.put(Range.closed(Integer.parseInt(range[0]), Integer.parseInt(range[1])),ageRange);
			}else{
				clinicSizeRangeMap.put(Range.closed(Integer.parseInt(range[0]), Integer.valueOf(Integer.MAX_VALUE)),ageRange);
			}
		}
		return clinicSizeRangeMap;
	}

	public static Map<String, List<ClinicDiseaseStatisticsResultVO>> getDefaultDataForClinicAndDiseaseStats(Filter filter){
		List<String> rangeLabels = getRangeLabelsForStatsByXAxisParameter(filter);
		return prepareDefaultDataForClinicAndDiseaseStats(filter, rangeLabels);
	}

	private static Map<String, List<ClinicDiseaseStatisticsResultVO>> prepareDefaultDataForClinicAndDiseaseStats(
			Filter filter, List<String> rangeLabels) {
		Map<String, List<ClinicDiseaseStatisticsResultVO>> statsMap = new LinkedHashMap<>();
		boolean isMultipleStatesSelected = StringUtils.isEmpty(filter.getCityCSV());
		for(String label : rangeLabels){
			ClinicDiseaseStatisticsResultVO defaultData = null;
			switch(filter.getxAxisParameter()){
			case AGE_GROUP : defaultData = new ClinicDiseaseStatisticsResultVO(0, label, "", "", "");
			break;
			case CLINIC_SIZE : defaultData = new ClinicDiseaseStatisticsResultVO(0, "", label, "", "");
			break;
			default: if(isMultipleStatesSelected){
						defaultData = new ClinicDiseaseStatisticsResultVO(0, "", "", label, "");
					 }else{
						 defaultData = new ClinicDiseaseStatisticsResultVO(0, "", "", "", label);
					 }
			}
			List<ClinicDiseaseStatisticsResultVO> statsPerLabel = new LinkedList<>();
			statsPerLabel.add(defaultData);
			statsMap.put(label, statsPerLabel);
		}
		return statsMap;
	}
	
	public static Map<String, List<ClinicDiseaseStatisticsResultVO>> getDefaultDataForClinicAndDiseaseStatsByBoth(Filter filter){
		Map<String, List<ClinicDiseaseStatisticsResultVO>> statsMap = new LinkedHashMap<>();
		List<String> ageRangeLabels = getRangeLabelsForStatsByXAxisParameter(filter);
		List<String> clinicSizeRangeLabels = getRangeLabels(CLINIC_SIZE,filter.getClinicSizeRangeCSV());
		for(String clinicSizeRange : clinicSizeRangeLabels){
			List<ClinicDiseaseStatisticsResultVO> clinicSizeRangeList = new LinkedList<>();
			for(String ageRange : ageRangeLabels){
				clinicSizeRangeList.add(new ClinicDiseaseStatisticsResultVO(0, ageRange, clinicSizeRange, "", ""));
			}
			statsMap.put(clinicSizeRange, clinicSizeRangeList);
		}
		return statsMap;
	}

	private static List<String> getRangeLabelsForStatsByXAxisParameter(Filter filter) {
		List<String> rangeLabels = new LinkedList<>();
		if(!filter.isIgnoreXAxis()){
			switch(filter.getxAxisParameter()){
			case AGE_GROUP : rangeLabels = getRangeLabels(filter);
			break;
			case CLINIC_SIZE : rangeLabels = getRangeLabels(filter);
			break;
			// For both option, ageGroup labels will be on xAxis
			default: rangeLabels = getRangeLabels(AGE_GROUP,filter.getAgeRangeCSV());
			}
		}else{
			if(StringUtils.isEmpty(filter.getCityCSV()))
				rangeLabels = Arrays.asList(filter.getStateCSV().split(","));
			else
				rangeLabels = Arrays.asList(filter.getCityCSV().split(","));
		}
		return rangeLabels;
	}

	public static Map<String,List<ClinicDiseaseStatisticsResultVO>> groupStatsByXAxisParam(List<ClinicDiseaseStatisticsResultVO> actualStats,Filter filter){
		Map<String,List<ClinicDiseaseStatisticsResultVO>> statsMap = new LinkedHashMap<>();
		if(!filter.isIgnoreXAxis()){
			switch(filter.getxAxisParameter()){
			case AGE_GROUP : statsMap = actualStats.stream().collect(Collectors.groupingBy(ClinicDiseaseStatisticsResultVO :: getAgeGroupLabel));
			break;
			case CLINIC_SIZE : statsMap = actualStats.stream().collect(Collectors.groupingBy(ClinicDiseaseStatisticsResultVO :: getClinicSizeLabel));
			break;
			// This is applicable only for BOTH (Clinic Size and Age group)
			default : statsMap = actualStats.stream().collect(Collectors.groupingBy(ClinicDiseaseStatisticsResultVO :: getClinicSizeLabel));
			}
		}else{
			if(StringUtils.isEmpty(filter.getCityCSV()))
				statsMap = actualStats.stream().collect(Collectors.groupingBy(ClinicDiseaseStatisticsResultVO :: getState));
			else
				statsMap = actualStats.stream().collect(Collectors.groupingBy(ClinicDiseaseStatisticsResultVO :: getCity));
		}
		return statsMap;
	}

	public static int getYAxisValueForBenchMark(String benchMarkParameter,BenchMarkDataVO benchMarkData){
		int yValue = 0;
		switch(benchMarkParameter.toLowerCase()){
		case BM_PARAM_ADHERENCE_SCORE : yValue = benchMarkData.getAdherenceScoreBenchMark();
		break;
		case BM_PARAM_HMR_DEVIATION : yValue = benchMarkData.gethMRDeviationBenchMark();
		break;
		case BM_PARAM_SETTING_DEVIATION :  yValue = benchMarkData.getSettingDeviationBenchMark();
		break;
		case BM_PARAM_MISSED_THERAPY_DAYS : yValue = benchMarkData.getMissedTherapyDaysBenchMark();
		break;
		case BM_PARAM_HMR_RUNRATE :  yValue = benchMarkData.gethMRRunrateBenchMark();
		break;
		}
		return yValue;
	}
	
	public static String getBenchMarkParameterLabel(BenchMarkFilter filter){
		switch(filter.getBenchMarkParameter().toLowerCase()){
		case BM_PARAM_ADHERENCE_SCORE : return BM_PARAM_ADHERENCE_SCORE_LABEL;
		case BM_PARAM_HMR_DEVIATION : return BM_PARAM_HMR_DEVIATION_LABEL;
		case BM_PARAM_SETTING_DEVIATION :  return BM_PARAM_SETTING_DEVIATION_LABEL;
		case BM_PARAM_MISSED_THERAPY_DAYS : return BM_PARAM_MISSED_THERAPY_DAYS_LABEL;
		case BM_PARAM_HMR_RUNRATE :  return BM_PARAM_HMR_RUNRATE_LABEL;
		}
		return "";
	}
	
	public static String getBenchMarkTypeLabel(String benchMarkType){
		switch(benchMarkType){
		case BM_TYPE_AVERAGE : return BM_TYPE_AVERAGE_LABEL;
		case BM_TYPE_MEDIAN : return BM_TYPE_MEDIAN_LABEL;
		case BM_TYPE_PERCENTILE : return BM_TYPE_PERCENTILE_LABEL;
		default: return BM_TYPE_AVERAGE_LABEL;
		}
	}

}
