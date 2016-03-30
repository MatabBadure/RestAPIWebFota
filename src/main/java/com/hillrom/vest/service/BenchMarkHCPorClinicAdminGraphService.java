package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.KEY_MY_CLINIC;
import static com.hillrom.vest.config.Constants.KEY_OTHER_CLINIC;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;
import static com.hillrom.vest.service.util.BenchMarkUtil.*;

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
	@SuppressWarnings("unchecked")
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) throws Exception{
		Map<String,Map<String,BenchMarkDataVO>> benchMarkDataMap = (Map<String,Map<String,BenchMarkDataVO>>) data;
		BenchMarkFilter benchMarkFilter = (BenchMarkFilter) filter;
		Graph benchMarkGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		String myClinicSeriesName = getFirstSeriesName(benchMarkFilter);
		Series myClinicSeries = GraphUtils.createSeriesObjectWithName(myClinicSeriesName);
		String seriesName = getSecondSeriesName(benchMarkFilter);
		Series otherClinicSeries = GraphUtils.createSeriesObjectWithName(seriesName);
		
		Map<String,BenchMarkDataVO> myClinicBenchMarkMap = benchMarkDataMap.getOrDefault(KEY_MY_CLINIC,new LinkedHashMap<>(1));
		Map<String,BenchMarkDataVO> otherClinicBenchMarkMap = benchMarkDataMap.getOrDefault(KEY_OTHER_CLINIC,new LinkedHashMap<>(1));
		for(String label : myClinicBenchMarkMap.keySet()){
			benchMarkGraph.getxAxis().getCategories().add(label);
			BenchMarkDataVO myClinicBenchMark = myClinicBenchMarkMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			BenchMarkDataVO otherClinicsBenchMark = otherClinicBenchMarkMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			GraphDataVO myClinicGraphData = new GraphDataVO();
			// Since it is kind of mirror image graph, we make the value negative
			myClinicGraphData.setY(0-getYAxisValueForBenchMark(benchMarkFilter.getBenchMarkParameter(), myClinicBenchMark));
			myClinicSeries.getData().add(myClinicGraphData);
			
			GraphDataVO otherClinicsGraphData = new GraphDataVO();
			otherClinicsGraphData.setY(getYAxisValueForBenchMark(benchMarkFilter.getBenchMarkParameter(), otherClinicsBenchMark));
			otherClinicSeries.getData().add(otherClinicsGraphData);
		}
		benchMarkGraph.getSeries().add(myClinicSeries);
		benchMarkGraph.getSeries().add(otherClinicSeries);
		return benchMarkGraph;
	}

	private String getFirstSeriesName(BenchMarkFilter benchMarkFilter) {
		StringBuilder myClinicSeriesName = new StringBuilder("Clinic Patients ")
		.append(getBenchMarkTypeLabel(benchMarkFilter.getBenchMarkType()))
		.append(" ")
		.append(getBenchMarkParameterLabel(benchMarkFilter));
		return myClinicSeriesName.toString();
	}

	private String getSecondSeriesName(BenchMarkFilter filter) {
		StringBuilder seriesName = new StringBuilder();
		if(StringUtils.isEmpty(filter.getStateCSV()) && StringUtils.isEmpty(filter.getCityCSV())){
			seriesName.append("National ");
		}else if(StringUtils.isNotEmpty(filter.getStateCSV())){
			seriesName.append("State ");
		}else if(StringUtils.isNotEmpty(filter.getCityCSV())){
			seriesName.append("City ");
		}
		seriesName.append(getBenchMarkTypeLabel(filter.getBenchMarkType()))
		.append(" ").append(getBenchMarkParameterLabel(filter));
		return seriesName.toString();
	}

}
