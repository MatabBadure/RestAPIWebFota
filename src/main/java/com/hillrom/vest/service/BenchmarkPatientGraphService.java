package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_RUNRATE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_RUNRATE_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION_LABEL;
import static com.hillrom.vest.config.Constants.KEY_BENCH_MARK_DATA;
import static com.hillrom.vest.config.Constants.KEY_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.KEY_TOTAL_PATIENTS;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;

import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_SELF;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_CLINIC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.stereotype.Component;

import com.hillrom.vest.repository.BenchmarkResultVO;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component("benchMarkPatientGraphService")
public class BenchmarkPatientGraphService extends AbstractGraphService{

	@Override
	public Graph populateGraphData(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForDay(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForWeek(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForMonth(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForYear(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) {
		Map<String,Object> benchMarkDataMap = (Map<String, Object>) data;
		Map<String,SortedMap<String,BenchMarkDataVO>> benchMarkData = (Map<String,SortedMap<String,BenchMarkDataVO>>) benchMarkDataMap.getOrDefault(KEY_BENCH_MARK_DATA, new Object());
		List<String> rangeLabels = (List<String>) benchMarkDataMap.getOrDefault(KEY_RANGE_LABELS,new LinkedList<>());
		BenchMarkFilter benchMarkFilter = (BenchMarkFilter) filter;
		Graph benchMarkGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		Series clinicSeries = GraphUtils.createSeriesObjectWithName("Clinic Level");
		Series selfSeries = GraphUtils.createSeriesObjectWithName("Self");
		benchMarkGraph.getxAxis().getCategories().addAll(rangeLabels);
		
		SortedMap<String,BenchMarkDataVO> groupBenchMarkForClinicMap = benchMarkData.get(BENCHMARK_DATA_CLINIC);
		SortedMap<String,BenchMarkDataVO> groupBenchMarkForPatientMap = benchMarkData.get(BENCHMARK_DATA_SELF);
		int y1;
		int y2;
		for(String label : rangeLabels){
			BenchMarkDataVO clinicbenchMarkVO = groupBenchMarkForClinicMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			BenchMarkDataVO userbenchMarkVO = groupBenchMarkForPatientMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			GraphDataVO clinicLevelGraphData = new GraphDataVO();
			GraphDataVO selfGraphData = new GraphDataVO();
			y1 = 0;
			y2 = 0;
			if(BM_PARAM_ADHERENCE_SCORE.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y1 = clinicbenchMarkVO.getAdherenceScoreBenchMark();
				y2 = 0-userbenchMarkVO.getAdherenceScoreBenchMark();
			}else if(BM_PARAM_HMR_DEVIATION.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y1=  clinicbenchMarkVO.gethMRDeviationBenchMark();
				y2 = 0-userbenchMarkVO.gethMRDeviationBenchMark();
			}else if(BM_PARAM_SETTING_DEVIATION.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y1 = clinicbenchMarkVO.getSettingDeviationBenchMark();
				y2 = 0-userbenchMarkVO.getSettingDeviationBenchMark();
			}else if(BM_PARAM_MISSED_THERAPY_DAYS.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y1 = clinicbenchMarkVO.getMissedTherapyDaysBenchMark();
				y2 = 0-userbenchMarkVO.getMissedTherapyDaysBenchMark();
			}else if(BM_PARAM_HMR_RUNRATE.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y1 = clinicbenchMarkVO.gethMRRunrateBenchMark();
				y2 = 0-userbenchMarkVO.gethMRRunrateBenchMark();
			}
			clinicLevelGraphData.setY(y1);
			clinicLevelGraphData.getToolText().put(KEY_TOTAL_PATIENTS, clinicbenchMarkVO.getPatientCount());
			clinicSeries.getData().add(clinicLevelGraphData);
			
			selfGraphData.setY(y2);
			selfSeries.getData().add(selfGraphData);
		}
		benchMarkGraph.getSeries().add(clinicSeries);
		benchMarkGraph.getSeries().add(selfSeries);
		return benchMarkGraph;
	}

}
