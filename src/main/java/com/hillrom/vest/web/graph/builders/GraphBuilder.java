package com.hillrom.vest.web.graph.builders;

import java.util.List;

import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.XaxisDataVO;

public interface GraphBuilder {

	public Graph populateXaxisData(XaxisDataVO xAxisData);
	public Graph buildGraph(XaxisDataVO xAxisData,Object data,Filter filter);
	public List<Series> buildSeries(Object data,Filter filter);
}
