package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.XAXIS_TYPE_DATE;
import static com.hillrom.vest.config.Constants.ADHERENCE_SCORE_LABEL;
import static com.hillrom.vest.config.Constants.MMddyyyy;
//import static com.hillrom.vest.config.Constants.KEY_ADHERENCE_SCORE;
//import static com.hillrom.vest.config.Constants.KEY_NOTIFICATIONS_POINT;
import static com.hillrom.vest.config.Constants.RESET_SCORE;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.AdherenceTrendVO;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.ProtocolRevisionVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component("adherenceTrendGraphService")
public class AdherenceTrendGraphService extends AbstractGraphService {

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
		List<ProtocolRevisionVO> protocolRevisionVoList = (List<ProtocolRevisionVO>) data;
		String seriesLabel = ADHERENCE_SCORE_LABEL;
		String datePattern = MMddyyyy;
		Series durationSeries = GraphUtils
				.createSeriesObjectWithName(seriesLabel);
		for (ProtocolRevisionVO protocolRevisionVO : protocolRevisionVoList) {
			for (AdherenceTrendVO adherenceTrendData : protocolRevisionVO
					.getAdherenceTrends()) {
				adherenceTrendGraph
						.getxAxis()
						.getCategories()
						.add(DateUtil.formatDate(adherenceTrendData.getDate(),
								datePattern));
				GraphDataVO point = createSeriesDataWithToolText(filter,
						adherenceTrendData);
				durationSeries.getData().add(point);
			}
		}
		adherenceTrendGraph.getSeries().add(durationSeries);
		return adherenceTrendGraph;
	}

	private GraphDataVO createSeriesDataWithToolText(Filter filter,
			AdherenceTrendVO adherenceTrendData) {
		GraphDataVO point = new GraphDataVO(DateUtil.formatDate(
				adherenceTrendData.getDate(), MMddyyyy),
				adherenceTrendData.getUpdatedScore());
		/*point.getToolText().put(KEY_ADHERENCE_SCORE,
				adherenceTrendData.getUpdatedScore());*/
		/*point.getToolText().put(KEY_NOTIFICATIONS_POINT,
				adherenceTrendData.getNotificationPoints());*/
		point.getToolText().put(RESET_SCORE,
				adherenceTrendData.isScoreReset());
		return point;
	}

}