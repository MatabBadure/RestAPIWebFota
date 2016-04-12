package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AGE_GROUP;
import static com.hillrom.vest.config.Constants.BOTH;
import static com.hillrom.vest.config.Constants.CLINIC_SIZE;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hillrom.vest.service.util.BenchMarkUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.ClinicDiseaseStatisticsResultVO;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component("clinicAndStatsGraphService")
public class ClinicAndDiseaseStatsGraphService extends AbstractGraphService{

	@Override
	public Graph populateGraphData(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data,filter); 
	}
	
	@Override
	public Graph populateGraphDataForDay(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data,filter);
	}

	@Override
	public Graph populateGraphDataForWeek(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data,filter);
	}

	@Override
	public Graph populateGraphDataForMonth(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data,filter);
	}

	@Override
	public Graph populateGraphDataForYear(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data,filter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) {
		Graph clinicAndStatsGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		String xAxisParameter = filter.getxAxisParameter();
		Map<String,List<ClinicDiseaseStatisticsResultVO>> statsMap = (Map<String, List<ClinicDiseaseStatisticsResultVO>>) data;
		if(!BOTH.equalsIgnoreCase(xAxisParameter) || filter.isIgnoreXAxis()){
			Series series = GraphUtils.createSeriesObjectWithName(getSeriesNameByXAxisParam(filter.getxAxisParameter()));
			for(String rangeLabel : statsMap.keySet()){
				clinicAndStatsGraph.getxAxis().getCategories().add(rangeLabel);
				List<ClinicDiseaseStatisticsResultVO> statsResults = statsMap.get(rangeLabel);
				int y = statsResults.stream().collect(Collectors.summingInt(ClinicDiseaseStatisticsResultVO :: getTotalPatients));
				GraphDataVO graphData = new GraphDataVO(null, y);
				series.getData().add(graphData);
			}
			clinicAndStatsGraph.getSeries().add(series);
		}else{
			List<String> clinicSizeRangeLabels = BenchMarkUtil.getRangeLabels(CLINIC_SIZE, filter.getClinicSizeRangeCSV());
			List<String> ageRangeLabels = BenchMarkUtil.getRangeLabels(AGE_GROUP, filter.getAgeRangeCSV());
			clinicAndStatsGraph.getxAxis().getCategories().addAll(ageRangeLabels);
			for(String clinicSizeRangeLabel : clinicSizeRangeLabels){
				List<ClinicDiseaseStatisticsResultVO> clinicSizeStats = statsMap.getOrDefault(clinicSizeRangeLabel,new LinkedList<ClinicDiseaseStatisticsResultVO>());
				Map<String,List<ClinicDiseaseStatisticsResultVO>> clinicSizeStatsGroupByAge = clinicSizeStats.stream().collect(Collectors.groupingBy(ClinicDiseaseStatisticsResultVO :: getAgeGroupLabel));
				Series series = GraphUtils.createSeriesObjectWithName(clinicSizeRangeLabel);
				for(String ageRange : ageRangeLabels){
					List<ClinicDiseaseStatisticsResultVO> clinicSizeStatsForAgeRange = clinicSizeStatsGroupByAge.getOrDefault(ageRange, new LinkedList<ClinicDiseaseStatisticsResultVO>());
					int y = clinicSizeStatsForAgeRange.stream().collect(Collectors.summingInt(ClinicDiseaseStatisticsResultVO :: getTotalPatients));
					GraphDataVO graphData = new GraphDataVO(null, y);
					series.getData().add(graphData);
				}
				clinicAndStatsGraph.getSeries().add(series);
			}
		}
		return clinicAndStatsGraph;
	}

	public static Map<String,Object> populateTooltipData(List<String> clinicSizeRangeLabels,Filter filter){
		Map<String,Object> toolText = new LinkedHashMap<>();
		for(String rangeLabel : clinicSizeRangeLabels){
			toolText.put(rangeLabel, 0);// default #patient = 0
		}
		return toolText;
	}
	
	public static String getSeriesNameByXAxisParam(String xAxisParam){
		switch(xAxisParam.toLowerCase()){
		case AGE_GROUP : return "Total No.of Patients by Age Group";
		case CLINIC_SIZE : return "Total No.of Patients by Clinic Size";
		case BOTH : return "";
		default: return "Total No.of Patients by Geography";
		}
	}
}
