package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.AVERAGE_LENGTH_OF_TREATMENT_LABEL;
import static com.hillrom.vest.config.Constants.AVERAGE_TREATMENTS_PER_DAY_LABEL;
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
import com.hillrom.vest.web.rest.dto.TreatmentStatisticsVO;

@Component("treatmentStatsGraphService")
public class TreatmentStatisticsGraphService extends AbstractGraphService {

	@Override
	public Graph populateGraphDataForDay(Object data, Filter filter) {
		return populateGraphDataForCustomDateRange(data, filter);	}

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
		List<TreatmentStatisticsVO> statistics = (List<TreatmentStatisticsVO>) data;
		Graph cumulativeStatsGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_DATETIME);
		cumulativeStatsGraph.getxAxis().setCategories(getXaxisLabels(statistics));
		String[] seriesNames = new String[]{AVERAGE_TREATMENTS_PER_DAY_LABEL,AVERAGE_LENGTH_OF_TREATMENT_LABEL};
		for(String seriesName: seriesNames){
			Series series = GraphUtils.createSeriesObjectWithName(seriesName);
			for(TreatmentStatisticsVO stats : statistics){
				if(AVERAGE_TREATMENTS_PER_DAY_LABEL.equalsIgnoreCase(seriesName))
					series.getData().add(new GraphDataVO(DateUtil.formatDate(stats.getStartTime(), Constants.MMddyyyyHHmmss), stats.getAvgTreatments()));
				else
					series.getData().add(new GraphDataVO(DateUtil.formatDate(stats.getStartTime(), Constants.MMddyyyyHHmmss), stats.getAvgTreatmentDuration()));
			}
			cumulativeStatsGraph.getSeries().add(series);
		}
		return cumulativeStatsGraph;
	}

	private List<String> getXaxisLabels(List<TreatmentStatisticsVO> stats){
		List<String> labels = new LinkedList<>();
		stats.forEach(stat -> {
			labels.add(DateUtil.formatDate(stat.getStartTime(), Constants.MMddyyyyHHmmss));
		});
		return labels;
	}
}
