package com.hillrom.vest.service.util;

import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.XaxisData;

public class GraphUtils {

	/**
	 * Creates Graph Object with type and sets Xaxis
	 * @param type
	 * @return
	 */
	public static Graph buildGraphObectWithXAxisType(String type) {
		Graph graph = new Graph();
		XaxisData xAxis = new XaxisData();
		xAxis.setType(type);
		graph.setxAxis(xAxis);
		return graph;
	}
	
	/**
	 * Creates Series Object with given name
	 * @param seriesName
	 * @return
	 */
	public static Series createSeriesObjectWithName(String seriesName) {
		Series seriesData = new Series();
		seriesData.setName(seriesName);
		return seriesData;
	}
}
