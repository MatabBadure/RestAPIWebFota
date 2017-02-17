package com.hillrom.vest.service.monarch;

import static com.hillrom.vest.config.Constants.DAY;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_DATETIME;
import static com.hillrom.vest.config.Constants.HMR_LABEL;
import static com.hillrom.vest.config.Constants.KEY_COUGH_PAUSES;
import static com.hillrom.vest.config.Constants.KEY_DURATION;
import static com.hillrom.vest.config.Constants.KEY_FREQUENCY;
import static com.hillrom.vest.config.Constants.KEY_NOTE_TEXT;
import static com.hillrom.vest.config.Constants.KEY_SESSION_NO;
import static com.hillrom.vest.config.Constants.MINUTES_LABEL;
import static com.hillrom.vest.config.Constants.MMddyyyyHHmmss;
import static com.hillrom.vest.config.Constants.MMddyyyyHHMM;
import static com.hillrom.vest.config.Constants.KEY_MISSED_THERAPY;
import static com.hillrom.vest.config.Constants.KEY_INTENSITY;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.hillrom.vest.service.AbstractGraphService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.monarch.TherapyDataMonarchVO;

@Component("hmrGraphServiceMonarch")
public class HMRGraphServiceMonarch extends AbstractGraphService {

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
		Graph hmrGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_DATETIME);
		List<TherapyDataMonarchVO> therapyData = (List<TherapyDataMonarchVO>) data;
		String seriesLabel = HMR_LABEL;
		String datePattern = MMddyyyyHHmmss;
		if(DAY.equalsIgnoreCase(filter.getDuration())){
			seriesLabel = MINUTES_LABEL;
		}
		Series durationSeries = GraphUtils.createSeriesObjectWithName(seriesLabel);
		for(TherapyDataMonarchVO therapy : therapyData){
			hmrGraph.getxAxis().getCategories().add(DateUtil.formatDate(therapy.getTimestamp(), datePattern));
			GraphDataVO point = createSeriesDataWithToolText(filter, therapy);
			durationSeries.getData().add(point);
		}
		hmrGraph.getSeries().add(durationSeries);
		return hmrGraph;
	}

	private GraphDataVO createSeriesDataWithToolText(Filter filter,
			TherapyDataMonarchVO therapy) {
		GraphDataVO point = null;
		if(DAY.equalsIgnoreCase(filter.getDuration())){
			point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getDuration());
		}else{
			point = new GraphDataVO(DateUtil.formatDate(therapy.getTimestamp(), MMddyyyyHHMM), therapy.getHmr());
		}
		point.getToolText().put(KEY_MISSED_THERAPY, therapy.isMissedTherapy());
		point.getToolText().put(KEY_SESSION_NO,therapy.getSessionNo()+"/"+therapy.getTreatmentsPerDay());
		point.getToolText().put(KEY_INTENSITY, therapy.getIntensity());
		point.getToolText().put(KEY_DURATION, therapy.getDuration());
		point.getToolText().put(KEY_FREQUENCY, therapy.getFrequency());
		point.getToolText().put(KEY_COUGH_PAUSES, therapy.getCoughPauses());
		if(Objects.nonNull(therapy.getNote()))
			point.getToolText().put(KEY_NOTE_TEXT, therapy.getNote().getNote());
		return point;
	}

}

