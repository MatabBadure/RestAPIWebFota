package com.hillrom.vest.service.monarch;

import static com.hillrom.vest.config.Constants.XAXIS_TYPE_DATE;
import static com.hillrom.vest.config.Constants.ADHERENCE_SCORE_LABEL;
import static com.hillrom.vest.config.Constants.MMddyyyy;
import static com.hillrom.vest.config.Constants.RESET_SCORE;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hillrom.vest.service.AbstractGraphService;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.AdherenceTrendVO;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.monarch.AdherenceTrendMonarchVO;
import com.hillrom.vest.web.rest.dto.monarch.ProtocolRevisionMonarchVO;

@Component("adherenceTrendGraphServiceMonarch")
public class AdherenceTrendGraphServiceMonarch extends AbstractGraphService {

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
		Graph adherenceTrendGraph = GraphUtils
				.buildGraphObectWithXAxisType(XAXIS_TYPE_DATE);
		List<ProtocolRevisionMonarchVO> protocolRevisionMonarchVoList = (List<ProtocolRevisionMonarchVO>) data;
		String seriesLabel = ADHERENCE_SCORE_LABEL;
		String datePattern = MMddyyyy;
		Series durationSeries = GraphUtils
				.createSeriesObjectWithName(seriesLabel);
		for (ProtocolRevisionMonarchVO protocolRevisionMonarchVO : protocolRevisionMonarchVoList) {
			for (AdherenceTrendMonarchVO adherenceTrendMonarchData : protocolRevisionMonarchVO
					.getAdherenceTrends()) {
				adherenceTrendGraph
						.getxAxis()
						.getCategories()
						.add(DateUtil.formatDate(adherenceTrendMonarchData.getDate(),
								datePattern));
				GraphDataVO point = createSeriesDataWithToolText(filter,
						adherenceTrendMonarchData);
				durationSeries.getData().add(point);
			}
		}
		adherenceTrendGraph.getSeries().add(durationSeries);
		return adherenceTrendGraph;
	}

	private GraphDataVO createSeriesDataWithToolText(Filter filter,
			AdherenceTrendMonarchVO adherenceTrendMonarchData) {
		GraphDataVO point = new GraphDataVO(DateUtil.formatDate(
				adherenceTrendMonarchData.getDate(), MMddyyyy),
				adherenceTrendMonarchData.getUpdatedScore());
		point.getToolText().put(RESET_SCORE,
				adherenceTrendMonarchData.isScoreReset());
		return point;
	}

}