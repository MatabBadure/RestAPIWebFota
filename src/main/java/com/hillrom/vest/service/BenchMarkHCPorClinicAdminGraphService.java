package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.BM_PARAM_ADHERENCE_SCORE;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_DEVIATION;
import static com.hillrom.vest.config.Constants.BM_PARAM_HMR_RUNRATE;
import static com.hillrom.vest.config.Constants.BM_PARAM_MISSED_THERAPY_DAYS;
import static com.hillrom.vest.config.Constants.BM_PARAM_SETTING_DEVIATION;
import static com.hillrom.vest.config.Constants.KEY_MY_CLINIC;
import static com.hillrom.vest.config.Constants.KEY_OTHER_CLINIC;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component("benchMarkHCPorClinicAdminGraphService")
public class BenchMarkHCPorClinicAdminGraphService extends AbstractGraphService {

	@Override
	public Graph populateGraphDataForDay(Object data, Filter filter) throws Exception{
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForWeek(Object data, Filter filter) throws Exception {
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForMonth(Object data, Filter filter) throws Exception{
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForYear(Object data, Filter filter) throws Exception{
		return populateGraphDataForCustomDateRange(data, filter);
	}

	@Override
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) throws Exception{
		Map<String,Map<String,BenchMarkDataVO>> benchMarkDataMap = (Map<String,Map<String,BenchMarkDataVO>>) data;
		BenchMarkFilter benchMarkFilter = (BenchMarkFilter) filter;
		Graph benchMarkGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		Series clinicSeries = GraphUtils.createSeriesObjectWithName("Clinic Patients Avg.Adherence Score");
		String seriesName = getSecondSeriesName(benchMarkFilter);
		Series selfSeries = GraphUtils.createSeriesObjectWithName(seriesName);
		
		Map<String,BenchMarkDataVO> myClinicBenchMarkMap = benchMarkDataMap.getOrDefault(KEY_MY_CLINIC,new LinkedHashMap<>(1));
		Map<String,BenchMarkDataVO> otherClinicBenchMarkMap = benchMarkDataMap.getOrDefault(KEY_OTHER_CLINIC,new LinkedHashMap<>(1));
		for(String label : myClinicBenchMarkMap.keySet()){
			benchMarkGraph.getxAxis().getCategories().add(label);
			BenchMarkDataVO myClinicBenchMark = myClinicBenchMarkMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			BenchMarkDataVO otherClinicsBenchMark = otherClinicBenchMarkMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			GraphDataVO myClinicGraphData = new GraphDataVO();
			// Since it is kind of mirror image graph, we make the value negative
			myClinicGraphData.setY(0-getYAxisValueForBenchMark(benchMarkFilter.getBenchMarkParameter(), otherClinicsBenchMark));
			selfSeries.getData().add(myClinicGraphData);
			
			GraphDataVO otherClinicsGraphData = new GraphDataVO();
			otherClinicsGraphData.setY(getYAxisValueForBenchMark(benchMarkFilter.getBenchMarkParameter(), myClinicBenchMark));
			clinicSeries.getData().add(otherClinicsGraphData);
		}
		benchMarkGraph.getSeries().add(clinicSeries);
		benchMarkGraph.getSeries().add(selfSeries);
		return benchMarkGraph;
	}

	private String getSecondSeriesName(Filter filter) {
		String seriesName = "Avg Adherence Score";
		if(StringUtils.isNotEmpty(filter.getStateCSV())){
			seriesName = "State "+seriesName;
		}else if(StringUtils.isNotEmpty(filter.getCityCSV())){
			seriesName = "City "+seriesName;
		}
		return seriesName;
	}

	private int getYAxisValueForBenchMark(String benchMarkParameter,BenchMarkDataVO benchMarkData){
		int yValue = 0;
		switch(benchMarkParameter){
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


}
