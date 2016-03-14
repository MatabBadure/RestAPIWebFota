package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.KEY_TOTAL_PATIENTS;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;

import java.util.List;

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
		List<BenchMarkDataVO> benchMarkData = (List<BenchMarkDataVO>) data;
		BenchMarkFilter benchMarkFilter = (BenchMarkFilter) filter;
		Graph benchMarkGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		Series series = GraphUtils.createSeriesObjectWithName(benchMarkFilter.getBenchMarkParameter());
		for(BenchMarkDataVO benchMarkVO : benchMarkData){
			benchMarkGraph.getxAxis().getCategories().add(benchMarkVO.getGroupLabel());
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

}
