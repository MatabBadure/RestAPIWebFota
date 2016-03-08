package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.DURATION_LABEL;
import static com.hillrom.vest.config.Constants.FREQUENCY_LABEL;
import static com.hillrom.vest.config.Constants.KEY_COUGH_PAUSES;
import static com.hillrom.vest.config.Constants.KEY_DURATION;
import static com.hillrom.vest.config.Constants.KEY_FREQUENCY;
import static com.hillrom.vest.config.Constants.KEY_MAX;
import static com.hillrom.vest.config.Constants.KEY_MIN;
import static com.hillrom.vest.config.Constants.KEY_NOTE_TEXT;
import static com.hillrom.vest.config.Constants.KEY_PRESSURE;
import static com.hillrom.vest.config.Constants.KEY_PROTOCOL;
import static com.hillrom.vest.config.Constants.KEY_THERAPY_DATA;
import static com.hillrom.vest.config.Constants.MMddyyyyHHMM;
import static com.hillrom.vest.config.Constants.PRESSURE_LABEL;
import static com.hillrom.vest.config.Constants.MMddyyyyHHmmss;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.TherapyDataVO;

@Component("complianceGraphService")
public class ComplianceGraphService extends AbstractGraphService {

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
		Map<String,Object> complianceData = (Map<String, Object>) data;
		ProtocolConstants protocol = (ProtocolConstants) complianceData.get(KEY_PROTOCOL);
		List<TherapyDataVO> therapyData = (List<TherapyDataVO>) complianceData.get(KEY_THERAPY_DATA);
		String[] seriesNames = new String[]{DURATION_LABEL,FREQUENCY_LABEL,PRESSURE_LABEL};
		Graph complianceGraph = GraphUtils.buildGraphObectWithXAxisType(Constants.XAXIS_TYPE_DATETIME);
		for(String seriesName : seriesNames){
			Series series = GraphUtils.createSeriesObjectWithName(seriesName);
			List<String> xAxisLabels = new LinkedList<>();
			for(TherapyDataVO therapy : therapyData){
				xAxisLabels.add(DateUtil.formatDate(therapy.getTimestamp(),MMddyyyyHHmmss));
				GraphDataVO point = null;
				if(DURATION_LABEL.equalsIgnoreCase(seriesName)){
					point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getDuration());
					series.getPlotLines().put(KEY_MIN, protocol.getMinDuration());
				}else if(FREQUENCY_LABEL.equalsIgnoreCase(seriesName)){
					point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getFrequency());
					series.getPlotLines().put(KEY_MIN, protocol.getMinFrequency());
					series.getPlotLines().put(KEY_MAX, protocol.getMaxFrequency());
				}else{
					point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getPressure());
					series.getPlotLines().put(KEY_MIN, protocol.getMinPressure());
					series.getPlotLines().put(KEY_MAX, protocol.getMaxPressure());
				}
				complianceGraph.getxAxis().setCategories(xAxisLabels);
				series.getData().add(point);
				populateToolTextValues(therapy, point);
			}
			complianceGraph.getSeries().add(series);
		}
		return complianceGraph;
	}

	private void populateToolTextValues(TherapyDataVO therapy, GraphDataVO point) {
		point.setMissedTherapy(therapy.isMissedTherapy());
		point.getToolText().put(KEY_PRESSURE, therapy.getPressure());
		point.getToolText().put(KEY_DURATION, therapy.getDuration());
		point.getToolText().put(KEY_FREQUENCY, therapy.getFrequency());
		point.getToolText().put(KEY_COUGH_PAUSES, therapy.getCoughPauses());
		if(Objects.nonNull(therapy.getNote()))
			point.getToolText().put(KEY_NOTE_TEXT, therapy.getNote().getNote());
	}

}
