package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_CLINIC;
import static com.hillrom.vest.config.Constants.BENCHMARK_DATA_SELF;
import static com.hillrom.vest.config.Constants.KEY_BENCH_MARK_DATA;
import static com.hillrom.vest.config.Constants.KEY_RANGE_LABELS;
import static com.hillrom.vest.config.Constants.KEY_TOTAL_PATIENTS;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;
import static com.hillrom.vest.service.util.BenchMarkUtil.getYAxisValueForBenchMark;

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

@Component("benchMarkPatientGraphService")
public class BenchmarkPatientGraphService extends AbstractGraphService{

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
		for(String label : rangeLabels){
			BenchMarkDataVO clinicbenchMarkVO = groupBenchMarkForClinicMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			BenchMarkDataVO userbenchMarkVO = groupBenchMarkForPatientMap.getOrDefault(label, new BenchMarkDataVO(label, 0));
			GraphDataVO clinicLevelGraphData = new GraphDataVO();
			GraphDataVO selfGraphData = new GraphDataVO();
			clinicLevelGraphData.setY(getYAxisValueForBenchMark(benchMarkFilter.getBenchMarkParameter(), clinicbenchMarkVO));
			clinicLevelGraphData.getToolText().put(KEY_TOTAL_PATIENTS, clinicbenchMarkVO.getPatientCount());
			clinicSeries.getData().add(clinicLevelGraphData);
			// Since it is kind of mirror image graph, we make the value negative
			selfGraphData.setY(0-getYAxisValueForBenchMark(benchMarkFilter.getBenchMarkParameter(), userbenchMarkVO));
			selfSeries.getData().add(selfGraphData);
		}
		benchMarkGraph.getSeries().add(clinicSeries);
		benchMarkGraph.getSeries().add(selfSeries);
		return benchMarkGraph;
	}

}
