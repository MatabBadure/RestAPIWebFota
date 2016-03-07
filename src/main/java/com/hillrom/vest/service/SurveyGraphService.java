package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.KEY_COUNT;
import static com.hillrom.vest.config.Constants.KEY_FIVE_DAYS_SURVEY_REPORT;
import static com.hillrom.vest.config.Constants.KEY_THIRTY_DAYS_SURVEY_REPORT;
import static com.hillrom.vest.config.Constants.NO;
import static com.hillrom.vest.config.Constants.Q_PREFIX;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;
import static com.hillrom.vest.config.Constants.YES;
import static com.hillrom.vest.service.util.GraphUtils.createSeriesObjectWithName;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.repository.FiveDaySurveyReportVO;
import com.hillrom.vest.repository.ThirtyDaySurveyReportVO;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.SurveyGraph;
import com.hillrom.vest.web.rest.dto.XaxisData;

@Component("surveyGraphService")
public class SurveyGraphService extends AbstractGraphService {
	
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

	@SuppressWarnings("unchecked")
	@Override
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) {
		Map<String,Object> surveyReports =  (Map<String, Object>) data;
		if(Objects.nonNull(surveyReports.get(KEY_FIVE_DAYS_SURVEY_REPORT))){
			return prepareGraphForFiveDaysSurvey(surveyReports);
		}else if(Objects.nonNull(surveyReports.get(KEY_THIRTY_DAYS_SURVEY_REPORT))){
			return prepareGraphForThirtyDaysSurvey(surveyReports);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private SurveyGraph prepareGraphForThirtyDaysSurvey(Map<String, Object> surveyReports){
		SurveyGraph surveyGraph = (SurveyGraph) buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		surveyGraph.setCount((int) surveyReports.get(KEY_COUNT));
		List<ThirtyDaySurveyReportVO> thirtyDaysSurveyReport =  (List<ThirtyDaySurveyReportVO>) surveyReports.get(KEY_THIRTY_DAYS_SURVEY_REPORT);
		Series stronglyDisAgreeSeries = createSeriesObjectWithName(Constants.STRONGLY_DISAGREE);
		Series someWhatDisAgreeSeries = createSeriesObjectWithName(Constants.SOMEWHAT_DISAGREE);
		Series neutralSeries = createSeriesObjectWithName(Constants.NEUTRAL);
		Series somewhatAgreeSeries = createSeriesObjectWithName(Constants.SOMEWHAT_AGREE);
		Series stronglyAgreeSeries = createSeriesObjectWithName(Constants.STRONGLY_AGREE);
		Series unableToAssessSeries = createSeriesObjectWithName(Constants.UNABLE_TO_ASSESS);
		int questionId = 1; 
		for(ThirtyDaySurveyReportVO reportVO : thirtyDaysSurveyReport){
			String questionPrefix = Q_PREFIX+(questionId++);
			surveyGraph.getxAxis().getCategories().add(questionPrefix);
			stronglyDisAgreeSeries.getData().add(new GraphDataVO(null, reportVO.getStronglyDisagreeCount()));
			someWhatDisAgreeSeries.getData().add(new GraphDataVO(null, reportVO.getSomewhatDisagreeCount()));
			neutralSeries.getData().add(new GraphDataVO(null, reportVO.getNeutralCount()));
			somewhatAgreeSeries.getData().add(new GraphDataVO(null, reportVO.getSomewhatAgreeCount()));
			stronglyAgreeSeries.getData().add(new GraphDataVO(null, reportVO.getStronglyAgreeCount()));
			unableToAssessSeries.getData().add(new GraphDataVO(null, reportVO.getUnableToAccessCount()));
			surveyGraph.getSurveyQuestions().add(questionPrefix.concat(" ").concat(reportVO.getQuestionText()));
		}
		surveyGraph.getSeries().add(stronglyDisAgreeSeries);
		surveyGraph.getSeries().add(someWhatDisAgreeSeries);
		surveyGraph.getSeries().add(neutralSeries);
		surveyGraph.getSeries().add(somewhatAgreeSeries);
		surveyGraph.getSeries().add(stronglyAgreeSeries);
		surveyGraph.getSeries().add(unableToAssessSeries);
		return surveyGraph;
	}
	
	@SuppressWarnings("unchecked")
	private SurveyGraph prepareGraphForFiveDaysSurvey(
			Map<String, Object> surveyReports) {
		SurveyGraph surveyGraph = (SurveyGraph) buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		surveyGraph.setCount((int) surveyReports.get(KEY_COUNT));
		List<FiveDaySurveyReportVO> fiveDaysSurveyReport =  (List<FiveDaySurveyReportVO>) surveyReports.get(KEY_FIVE_DAYS_SURVEY_REPORT);
		Series yesSeries = createSeriesObjectWithName(YES);
		Series noSeries = createSeriesObjectWithName(NO);
		int questionId = 1; 
		for(FiveDaySurveyReportVO reportVO : fiveDaysSurveyReport){
			String questionPrefix = Q_PREFIX+(questionId++);
			surveyGraph.getxAxis().getCategories().add(questionPrefix);
			yesSeries.getData().add(new GraphDataVO(null, reportVO.getYesCount()));
			noSeries.getData().add(new GraphDataVO(null, reportVO.getNoCount()));
			surveyGraph.getSurveyQuestions().add(questionPrefix.concat(" ").concat(reportVO.getQuestionText()));
		}
		surveyGraph.getSeries().add(yesSeries);
		surveyGraph.getSeries().add(noSeries);
		return surveyGraph;
	}

	private SurveyGraph buildGraphObectWithXAxisType(String type) {
		SurveyGraph graph = new SurveyGraph();
		XaxisData xAxis = new XaxisData();
		xAxis.setType(type);
		graph.setxAxis(xAxis);
		return graph;
	}
}
