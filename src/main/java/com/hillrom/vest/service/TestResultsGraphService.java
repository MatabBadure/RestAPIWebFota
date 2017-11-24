package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.MMddyyyyHHmmss;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_DATE;
import static com.hillrom.vest.config.Constants.FVC_P;
import static com.hillrom.vest.config.Constants.FEV1_P;

import java.util.List;
import org.springframework.stereotype.Component;

import com.hillrom.vest.domain.PatientTestResult;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component("testResultsGraphService")
public class TestResultsGraphService extends AbstractGraphService {
	
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
		String[] seriesNames = new String[] { FVC_P, FEV1_P };
		Graph patientTestResultsGraph = GraphUtils
				.buildGraphObectWithXAxisType(XAXIS_TYPE_DATE);
		List<PatientTestResult> patientTestResultList = (List<PatientTestResult>) data;
		String datePattern = MMddyyyyHHmmss;
		for (PatientTestResult patientTestResult : patientTestResultList) {
			patientTestResultsGraph
					.getxAxis()
					.getCategories()
					.add(DateUtil.formatDate(
							patientTestResult.getTestResultDate(), datePattern));
		}
		for (String seriesname : seriesNames) {
			Series durationSeries = GraphUtils
					.createSeriesObjectWithName(seriesname);
			for (PatientTestResult patientTestRes : patientTestResultList) {

				GraphDataVO point = createSeriesData(filter,
						patientTestRes, seriesname);
				durationSeries.getData().add(point);
			}
			patientTestResultsGraph.getSeries().add(durationSeries);
		}
		return patientTestResultsGraph;
	}
	/**
	 * Series data for patient test Result for FVC_P and FEV1_P
	 * @param filter
	 * @param patientTestResult
	 * @param seriesName
	 * @return
	 */
	private GraphDataVO createSeriesData(Filter filter,
			PatientTestResult patientTestResult, String seriesName) {
		GraphDataVO point;
		if(seriesName.equalsIgnoreCase("FVC_P")){
			point = new GraphDataVO(DateUtil.formatDate(
				patientTestResult.getTestResultDate(), MMddyyyyHHmmss),
				patientTestResult.getFVC_P());
		}else{
			 point = new GraphDataVO(DateUtil.formatDate(
					patientTestResult.getTestResultDate(), MMddyyyyHHmmss),
					patientTestResult.getFEV1_P());
		}		
		return point;
	}
}