package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_CLINIC;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_SELF;
import static com.hillrom.vest.config.Constants.BM_TYPE_AVERAGE;
import static com.hillrom.vest.config.Constants.BOTH;
import static com.hillrom.vest.config.Constants.KEY_BENCH_MARK_DATA;
import static com.hillrom.vest.config.Constants.KEY_MY_CLINIC;
import static com.hillrom.vest.config.Constants.KEY_OTHER_CLINIC;
import static com.hillrom.vest.config.Constants.KEY_RANGE_LABELS;
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
	
	@Qualifier("benchMarkHCPorClinicAdminGraphService")
	@Inject
	private GraphService benchMarkHCPorClinicAdminGraphService;
	
	public Graph getBenchMarkGraphForAdminParameterView(BenchMarkFilter filter) throws Exception{
		Map<String,BenchMarkDataVO> benchMarkData = getBenchmarkDataForAdminParameterView(filter);
		List<String> rangeLabels =  BenchMarkUtil.getRangeLabels(filter);
		Map<String,Object> benchMarkDataMap = new HashMap<>(2);
		benchMarkDataMap.put(KEY_BENCH_MARK_DATA, benchMarkData);
		benchMarkDataMap.put(KEY_RANGE_LABELS, rangeLabels);
		Graph benchMarkGraph = benchMarkGraphService.populateGraphData(benchMarkDataMap, filter);
		return benchMarkGraph;

	}
	
	public Graph getBenchMarkGraphForPatientView(BenchMarkFilter filter) throws Exception{
		Map<String, Map<String, BenchMarkDataVO>> benchMarkData = getBenchMarkDataForPatientView(filter);
		List<String> rangeLabels = BenchMarkUtil.getRangeLabels(filter);
		Map<String, Object> benchMarkDataMap = new HashMap<>(2);
		benchMarkDataMap.put(KEY_BENCH_MARK_DATA, benchMarkData);
		benchMarkDataMap.put(KEY_RANGE_LABELS, rangeLabels);
		Graph benchMarkGraph = benchmarkPatientGraphService.populateGraphData(benchMarkDataMap, filter);
		return benchMarkGraph;
	}
	
	public Map<String,BenchMarkDataVO> getBenchmarkDataForAdminParameterView(BenchMarkFilter filter) {
		List<BenchmarkResultVO> benchmarkVOs = new LinkedList<>();
		Map<String, List<BenchmarkResultVO>> groupBenchMarkMap = new HashMap<>();
		Map<String,BenchMarkDataVO> defaultBenchMarkData = new LinkedHashMap<>();
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
	
	public Map<String,Map<String,BenchMarkDataVO>> getBenchMarkDataForPatientView(BenchMarkFilter filter) throws HillromException {
		
			List<BenchmarkResultVO> benchmarkVOs = new LinkedList<>();
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForClinicMap = new HashMap<>();
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForPatientMap = new HashMap<>();
			Map<String,BenchMarkDataVO> defaultBenchMarkDataForClinic = new LinkedHashMap<>();
			Map<String,BenchMarkDataVO> defaultBenchMarkDataForUser = new LinkedHashMap<>();
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
			Map<String,Map<String,BenchMarkDataVO>> defaultBenchMarkData = new HashMap<>();
			defaultBenchMarkData.put(BENCHMARK_DATA_CLINIC, defaultBenchMarkDataForClinic);
			defaultBenchMarkData.put(BENCHMARK_DATA_SELF, defaultBenchMarkDataForUser);
			return defaultBenchMarkData;
		}

	private void updateDefaultBenchMarkDataWithActualForPatientView(BenchMarkFilter filter,
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForClinicMap,
			Map<String, List<BenchmarkResultVO>> groupBenchMarkForPatientMap,
			Map<String, BenchMarkDataVO> defaultBenchMarkDataForClinic,
			Map<String, BenchMarkDataVO> defaultBenchMarkDataForUser,
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
	
	public Graph getClinicAndDiseaseStatsGraph(Filter filter) throws Exception{
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

	public Graph getClinicLevelBenchMarkGraphForHCPOrClinicAdmin(BenchMarkFilter filter) throws Exception{
		Map<String,Map<String,BenchMarkDataVO>> benchMarkData = getClinicLevelBenchMarkDataForHCPOrClinicAdmin(filter);
		return benchMarkHCPorClinicAdminGraphService.populateGraphData(benchMarkData, filter);
	}
	
	public Map<String,Map<String,BenchMarkDataVO>> getClinicLevelBenchMarkDataForHCPOrClinicAdmin(BenchMarkFilter filter){
		List<BenchmarkResultVO> myClinicBenchMark =  benchmarkRepository.getAverageBenchMarkByAgeGroupForClinicAndAdminOrHCP(filter);
		List<BenchmarkResultVO> otherClinicsBenchMark =  benchmarkRepository.getAverageBenchMarkByAgeGroupForRestOfClinics(filter);
		Map<String,BenchMarkDataVO> defaultMyClinicData = BenchMarkUtil.prepareDefaultDataByAgeGroupOrClinicSize(filter);
		Map<String,BenchMarkDataVO> defaultOtherClinicsData = BenchMarkUtil.prepareDefaultDataByAgeGroupOrClinicSize(filter);
		Map<String, List<BenchmarkResultVO>> myClinicBenchMarkMap = myClinicBenchMark.stream().collect(Collectors.groupingBy(BenchmarkResultVO :: getAgeRangeLabel));
		Map<String, List<BenchmarkResultVO>> otherClinicsBenchMarkMap = otherClinicsBenchMark.stream().collect(Collectors.groupingBy(BenchmarkResultVO :: getAgeRangeLabel));
		BenchMarkStrategy benchMarkStrategy = BenchMarkStrategyFactory.getBenchMarkStrategy(filter.getBenchMarkType());
		updateDefaultBenchMarkDataWithActualForClinicAdminAndHCPView(filter,myClinicBenchMarkMap,otherClinicsBenchMarkMap,defaultMyClinicData,defaultOtherClinicsData,benchMarkStrategy);
		Map<String,Map<String,BenchMarkDataVO>> actualBenchMarkData = new LinkedHashMap<>();
		actualBenchMarkData.put(KEY_MY_CLINIC, defaultMyClinicData);
		actualBenchMarkData.put(KEY_OTHER_CLINIC, defaultOtherClinicsData);
		return actualBenchMarkData;
	}
	
	private void updateDefaultBenchMarkDataWithActualForClinicAdminAndHCPView(BenchMarkFilter filter,
			Map<String, List<BenchmarkResultVO>> myClinicBenchMarkMap,
			Map<String, List<BenchmarkResultVO>> otherClinicsBenchMarkMap,
			Map<String, BenchMarkDataVO> defaultMyClinicData,
			Map<String, BenchMarkDataVO> defaultBenchMarkDataForOtherClinic,
			BenchMarkStrategy benchMarkStrategy) {
		for(String ageRangeLabel : defaultMyClinicData.keySet()){
			List<BenchmarkResultVO> myClinicValues = myClinicBenchMarkMap.get(ageRangeLabel);
			List<BenchmarkResultVO> otherClinicsValues = otherClinicsBenchMarkMap.get(ageRangeLabel);
			if(Objects.nonNull(myClinicValues)){
				BenchMarkDataVO benchMarkDataVO = prepareBenchMarkData(
						filter.getBenchMarkParameter(), benchMarkStrategy, ageRangeLabel,
						myClinicValues);
				defaultMyClinicData.put(ageRangeLabel, benchMarkDataVO);
			}
			if(Objects.nonNull(otherClinicsValues)){
				BenchMarkDataVO benchMarkDataVO = prepareBenchMarkData(
						filter.getBenchMarkParameter(), benchMarkStrategy, ageRangeLabel,
						otherClinicsValues);
				defaultBenchMarkDataForOtherClinic.put(ageRangeLabel, benchMarkDataVO);
			}
		}
	}
}
