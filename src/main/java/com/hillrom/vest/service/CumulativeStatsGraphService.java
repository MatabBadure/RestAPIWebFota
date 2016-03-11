package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.HMR_NON_ADHERENCE_LABEL;
import static com.hillrom.vest.config.Constants.MISSED_THERAPY_DAYS_LABEL;
import static com.hillrom.vest.config.Constants.NO_TRANSMISSION_RECORDED_LABEL;
import static com.hillrom.vest.config.Constants.SETTING_DEVIATION_LABEL;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_DATETIME;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.StatisticsVO;

@Component("cumulativeStatsGraphService")
public class CumulativeStatsGraphService extends AbstractGraphService {

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
		return populateGraphDataForCustomDateRange(data, filter);	}

	@Override
	public Graph populateGraphDataForYear(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);	}

	@Override
	@SuppressWarnings("unchecked")
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) {
		List<StatisticsVO> statistics = (List<StatisticsVO>) data;
		Graph cumulativeStatsGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_DATETIME);
		cumulativeStatsGraph.getxAxis().setCategories(getXaxisLabels(statistics));
		String[] seriesNames = new String[]{MISSED_THERAPY_DAYS_LABEL,NO_TRANSMISSION_RECORDED_LABEL,SETTING_DEVIATION_LABEL,HMR_NON_ADHERENCE_LABEL};
		for(String seriesName: seriesNames){
			Series series = GraphUtils.createSeriesObjectWithName(seriesName);
			for(StatisticsVO stats : statistics){
				if(MISSED_THERAPY_DAYS_LABEL.equalsIgnoreCase(seriesName))
					series.getData().add(new GraphDataVO(DateUtil.formatDate(stats.getStartTimestamp(), Constants.MMddyyyyHHmmss), stats.getMissedTherapy()));
				else if(NO_TRANSMISSION_RECORDED_LABEL.equalsIgnoreCase(seriesName))
					series.getData().add(new GraphDataVO(DateUtil.formatDate(stats.getStartTimestamp(), Constants.MMddyyyyHHmmss), stats.getNoEvent()));
				else if(SETTING_DEVIATION_LABEL.equalsIgnoreCase(seriesName))
					series.getData().add(new GraphDataVO(DateUtil.formatDate(stats.getStartTimestamp(), Constants.MMddyyyyHHmmss), stats.getSettingDeviation()));
				else 
					series.getData().add(new GraphDataVO(DateUtil.formatDate(stats.getStartTimestamp(), Constants.MMddyyyyHHmmss), stats.getNonCompliance()));
			}
			cumulativeStatsGraph.getSeries().add(series);
		}
		return cumulativeStatsGraph;
	}

	private List<String> getXaxisLabels(List<StatisticsVO> stats){
		List<String> labels = new LinkedList<>();
		stats.forEach(stat -> {
			labels.add(DateUtil.formatDate(stat.getStartTimestamp(), Constants.MMddyyyyHHmmss));
		});
		return labels;
	}
}
