package com.hillrom.vest.service.monarch;

import static com.hillrom.vest.config.Constants.DURATION_LABEL;
import static com.hillrom.vest.config.Constants.FREQUENCY_LABEL;
import static com.hillrom.vest.config.Constants.KEY_COUGH_PAUSES;
import static com.hillrom.vest.config.Constants.KEY_DURATION;
import static com.hillrom.vest.config.Constants.KEY_FREQUENCY;
import static com.hillrom.vest.config.Constants.KEY_MAX;
import static com.hillrom.vest.config.Constants.KEY_MIN;
import static com.hillrom.vest.config.Constants.KEY_MISSED_THERAPY;
import static com.hillrom.vest.config.Constants.KEY_NOTE_TEXT;
import static com.hillrom.vest.config.Constants.KEY_INTENSITY;
import static com.hillrom.vest.config.Constants.KEY_PROTOCOL;
import static com.hillrom.vest.config.Constants.KEY_THERAPY_DATA;
import static com.hillrom.vest.config.Constants.MMddyyyyHHMM;
import static com.hillrom.vest.config.Constants.MMddyyyyHHmmss;
import static com.hillrom.vest.config.Constants.INTENSITY_LABEL;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.service.AbstractGraphService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.monarch.TherapyDataMonarchVO;

@Component("complianceGraphServiceMonarch")
public class ComplianceGraphServiceMonarch extends AbstractGraphService {

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
		ProtocolConstantsMonarch protocol = (ProtocolConstantsMonarch) complianceData.get(KEY_PROTOCOL);
		int minDuration = getMinDuration(protocol); 
		List<TherapyDataMonarchVO> therapyData = (List<TherapyDataMonarchVO>) complianceData.get(KEY_THERAPY_DATA);
		String[] seriesNames = new String[]{DURATION_LABEL,FREQUENCY_LABEL,INTENSITY_LABEL};
		Graph complianceGraph = GraphUtils.buildGraphObectWithXAxisType(Constants.XAXIS_TYPE_DATETIME);
		for(String seriesName : seriesNames){
			Series series = GraphUtils.createSeriesObjectWithName(seriesName);
			List<String> xAxisLabels = new LinkedList<>();
			for(TherapyDataMonarchVO therapy : therapyData){
				xAxisLabels.add(DateUtil.formatDate(therapy.getTimestamp(),MMddyyyyHHmmss));
				GraphDataVO point = null;
				if(DURATION_LABEL.equalsIgnoreCase(seriesName)){
					point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getDuration());
					series.getPlotLines().put(KEY_MIN, minDuration);
				}else if(FREQUENCY_LABEL.equalsIgnoreCase(seriesName)){
					point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getFrequency());
					series.getPlotLines().put(KEY_MIN, protocol.getMinFrequency());
					series.getPlotLines().put(KEY_MAX, protocol.getMaxFrequency());
				}else{
					point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getIntensity());
					series.getPlotLines().put(KEY_MIN, protocol.getMinIntensity());
					series.getPlotLines().put(KEY_MAX, protocol.getMaxIntensity());
				}
				complianceGraph.getxAxis().setCategories(xAxisLabels);
				series.getData().add(point);
				populateToolTextValues(therapy, point);
			}
			complianceGraph.getSeries().add(series);
		}
		return complianceGraph;
	}

	private int getMinDuration(ProtocolConstantsMonarch protocol) {
		int minDuration = 0;
		if(Objects.nonNull(protocol)){
			minDuration = Objects.nonNull(protocol.getMinDuration()) ? protocol
					.getMinDuration()
					: (protocol.getTreatmentsPerDay() * protocol
							.getMinMinutesPerTreatment()); 
		}
		return minDuration;
	}

	private void populateToolTextValues(TherapyDataMonarchVO therapy, GraphDataVO point) {
		point.getToolText().put(KEY_MISSED_THERAPY, therapy.isMissedTherapy());
		point.getToolText().put(KEY_INTENSITY, therapy.getIntensity());
		point.getToolText().put(KEY_DURATION, therapy.getDuration());
		point.getToolText().put(KEY_FREQUENCY, therapy.getFrequency());
		point.getToolText().put(KEY_COUGH_PAUSES, therapy.getCoughPauses());
		if(Objects.nonNull(therapy.getNote()))
			point.getToolText().put(KEY_NOTE_TEXT, therapy.getNoteMonarch().getNote());
	}

}
