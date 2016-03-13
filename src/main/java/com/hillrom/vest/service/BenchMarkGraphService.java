package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;
import static com.hillrom.vest.config.Constants.*;

import java.util.List;

import com.hillrom.vest.service.util.GraphUtils;
import com.hillrom.vest.web.rest.dto.BenchMarkDataVO;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.Series;

public class BenchMarkGraphService extends AbstractGraphService {

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
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) {
		/*List<BenchMarkDataVO> benchMarkData = (List<BenchMarkDataVO>) data;
		BenchMarkFilter benchMarkFilter = (BenchMarkFilter) filter;
		Graph benchMarkGraph = GraphUtils.buildGraphObectWithXAxisType(XAXIS_TYPE_CATEGORIES);
		Series series = GraphUtils.createSeriesObjectWithName(benchMarkFilter.getyAxisParameter());
		for(BenchMarkDataVO benchMarkVO : benchMarkData){
			benchMarkGraph.getxAxis().getCategories().add(benchMarkVO.getGroupLabel());
			GraphDataVO graphData = null;
			if(BM_PARAM_ADHERENCE_SCORE.equalsIgnoreCase(benchMarkFilter.getBenchMarkParameter())){
				graphData = new GraphDataVO(null, benchMarkVO.getAdherenceScoreBenchMark());
				GraphDataVO
			}
		}*/
		return null;
	}

}
