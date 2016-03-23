package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_CLINIC;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_SELF;
import static com.hillrom.vest.config.Constants.BM_TYPE_AVERAGE;
import static com.hillrom.vest.config.Constants.KEY_BENCH_MARK_DATA;
import static com.hillrom.vest.config.Constants.KEY_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.BOTH;
import static com.hillrom.vest.service.util.BenchMarkUtil.mapBenchMarkByAgeGroup;
import static com.hillrom.vest.service.util.BenchMarkUtil.mapBenchMarkByClinicSize;
import static com.hillrom.vest.service.util.BenchMarkUtil.prepareBenchMarkData;
import static com.hillrom.vest.service.util.BenchMarkUtil.prepareDefaultDataByAgeGroupOrClinicSize;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.BenchmarkRepository;
import com.hillrom.vest.repository.ClinicAndDiseaseStatisticsRepository;
import com.hillrom.vest.service.util.BenchMarkUtil;
import com.hillrom.vest.util.ExceptionConstants;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;
import com.hillrom.vest.web.rest.dto.ClinicDiseaseStatisticsResultVO;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;

@Service
@Transactional
public class BenchmarkService {
	
	@Inject
	private BenchmarkRepository benchmarkRepository;
	
	@Qualifier("benchMarkGraphService")
	@Inject
	private GraphService benchMarkGraphService;
	
	@Qualifier("benchMarkPatientGraphService")
	@Inject
	private GraphService benchmarkPatientGraphService;
	
	@Inject
	private ClinicAndDiseaseStatisticsRepository statisticsRepository;
	
	@Qualifier("clinicAndStatsGraphService")
	@Inject
	private GraphService clinicAndStatsGraphService;
	
	public Graph getBenchMarkGraphForAdminParameterView(BenchMarkFilter filter) throws HillromException{
		SortedMap<String,BenchMarkDataVO> benchMarkData = getBenchmarkDataForAdminParameterView(filter);
		List<String> rangeLabels =  BenchMarkUtil.getRangeLabels(filter);
		Map<String,Object> benchMarkDataMap = new HashMap<>(2);
		benchMarkDataMap.put(KEY_BENCH_MARK_DATA, benchMarkData);
		benchMarkDataMap.put(KEY_RANGE_LABELS, rangeLabels);
		Graph benchMarkGraph = benchMarkGraphService.populateGraphData(benchMarkDataMap, filter);
		return benchMarkGraph;

	}
	
	public Graph getBenchMarkGraphForPatientView(BenchMarkFilter filter) throws HillromException{
		Map<String, SortedMap<String, BenchMarkDataVO>> benchMarkData = getBenchMarkDataForPatientView(filter);
		List<String> rangeLabels = BenchMarkUtil.getRangeLabels(filter);
		Map<String, Object> benchMarkDataMap = new HashMap<>(2);
		benchMarkDataMap.put(KEY_BENCH_MARK_DATA, benchMarkData);
		benchMarkDataMap.put(KEY_RANGE_LABELS, rangeLabels);
		Graph benchMarkGraph = benchmarkPatientGraphService.populateGraphData(benchMarkDataMap, filter);
		return benchMarkGraph;
	}
	
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
	
	public Map<String,SortedMap<String,BenchMarkDataVO>> getBenchMarkDataForPatientView(BenchMarkFilter filter) throws HillromException {
		
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
			
			updateDefaultBenchMarkDataWithActualForPatientView(filter,
					groupBenchMarkForClinicMap, groupBenchMarkForPatientMap,
					defaultBenchMarkDataForClinic, defaultBenchMarkDataForUser,
					benchMarkStrategy);
			Map<String,SortedMap<String,BenchMarkDataVO>> defaultBenchMarkData = new HashMap<>();
			defaultBenchMarkData.put(BENCHMARK_DATA_CLINIC, defaultBenchMarkDataForClinic);
			defaultBenchMarkData.put(BENCHMARK_DATA_SELF, defaultBenchMarkDataForUser);
			return defaultBenchMarkData;
		}

	private void updateDefaultBenchMarkDataWithActualForPatientView(BenchMarkFilter filter,
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForClinicMap,
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForPatientMap,
			SortedMap<String, BenchMarkDataVO> defaultBenchMarkDataForClinic,
			SortedMap<String, BenchMarkDataVO> defaultBenchMarkDataForUser,
			BenchMarkStrategy benchMarkStrategy) {
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
	}
	
	public Graph getClinicAndDiseaseStatsGraph(Filter filter) throws HillromException{
		List<ClinicDiseaseStatisticsResultVO> statsResultsVO = new LinkedList<>(); 
		if(!filter.isIgnoreXAxis()){
			statsResultsVO = statisticsRepository.getClinicDiseaseStatsByAgeGroupOrClinicSize(filter);
		}else{
			statsResultsVO = statisticsRepository.getClinicDiseaseStatsByState(filter);
		}
		Map<String,List<ClinicDiseaseStatisticsResultVO>> statsMap = getClinicAndDiseaseStats(statsResultsVO,filter);
		return clinicAndStatsGraphService.populateGraphData(statsMap, filter);
	}
	
	public Map<String, List<ClinicDiseaseStatisticsResultVO>> getClinicAndDiseaseStats(List<ClinicDiseaseStatisticsResultVO> actualStats,Filter filter){
		Map<String, List<ClinicDiseaseStatisticsResultVO>> defaultStatsMap = new LinkedHashMap<>();
		if(!BOTH.equalsIgnoreCase(filter.getxAxisParameter()) )
			defaultStatsMap = BenchMarkUtil.getDefaultDataForClinicAndDiseaseStats(filter);
		else
			defaultStatsMap = BenchMarkUtil.getDefaultDataForClinicAndDiseaseStatsByBoth(filter);
		Map<String,List<ClinicDiseaseStatisticsResultVO>> actualStatsMap = BenchMarkUtil.groupStatsByXAxisParam(actualStats, filter);
		// update the default map with actual stats
		for(String label : defaultStatsMap.keySet()){
			if(Objects.nonNull(actualStatsMap.get(label))){
				defaultStatsMap.put(label, actualStatsMap.get(label));
			}
		}
		return defaultStatsMap;
	}
	
}
