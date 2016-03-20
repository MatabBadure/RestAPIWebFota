package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS_LABEL;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION_LABEL;
import static com.hillrom.vest.config.Constants.KEY_BENCH_MARK_DATA;
import static com.hillrom.vest.config.Constants.KEY_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.KEY_TOTAL_PATIENTS;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.springframework.stereotype.Component;

import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component("benchMarkGraphService")
public class BenchMarkGraphService extends AbstractGraphService {

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
		SortedMap<String,BenchMarkDataVO> benchMarkData = (SortedMap<String, BenchMarkDataVO>) benchMarkDataMap.getOrDefault(KEY_BENCH_MARK_DATA, new Object());
		List<String> rangeLabels = (List<String>) benchMarkDataMap.getOrDefault(KEY_RANGE_LABELS,new LinkedList<>());
		BenchMarkFilter benchMarkFilter = (BenchMarkFilter) filter;
		Graph benchMarkGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		Series series = GraphUtils.createSeriesObjectWithName(getSeriesName(benchMarkFilter.getBenchMarkParameter()));
		benchMarkGraph.getxAxis().getCategories().addAll(rangeLabels);
		for(String label : rangeLabels){
			BenchMarkDataVO benchMarkVO = benchMarkData.getOrDefault(label, new BenchMarkDataVO(label, 0));
			GraphDataVO graphData = new GraphDataVO();
			int y = 0;
			if(BM_PARAM_ADHERENCE_SCORE.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y = benchMarkVO.getAdherenceScoreBenchMark();
			}else if(BM_PARAM_HMR_DEVIATION.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y=  benchMarkVO.gethMRDeviationBenchMark();
			}else if(BM_PARAM_SETTING_DEVIATION.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y = benchMarkVO.getSettingDeviationBenchMark();
			}else if(BM_PARAM_MISSED_THERAPY_DAYS.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				y = benchMarkVO.getMissedTherapyDaysBenchMark();
			}
			graphData.setY(y);
			graphData.getToolText().put(KEY_TOTAL_PATIENTS, benchMarkVO.getPatientCount());
			series.getData().add(graphData);
		}
		benchMarkGraph.getSeries().add(series);
		return benchMarkGraph;
	}

	private String getSeriesName(String name){
		Map<String,String> seriesNameMap = new HashMap<>();
		seriesNameMap.put(BM_PARAM_ADHERENCE_SCORE, BM_PARAM_ADHERENCE_SCORE_LABEL);
		seriesNameMap.put(BM_PARAM_SETTING_DEVIATION, BM_PARAM_SETTING_DEVIATION_LABEL);
		seriesNameMap.put(BM_PARAM_HMR_DEVIATION, BM_PARAM_HMR_DEVIATION_LABEL);
		seriesNameMap.put(BM_PARAM_MISSED_THERAPY_DAYS, BM_PARAM_MISSED_THERAPY_DAYS_LABEL);
		return seriesNameMap.get(name);
	}
}
