package com.hillrom.vest.web.graph.builders;

import static com.hillrom.vest.config.Constants.CUSTOM;
import static com.hillrom.vest.config.Constants.MONTH;
import static com.hillrom.vest.config.Constants.WEEK;
import static com.hillrom.vest.config.Constants.YEAR;

import java.util.LinkedList;
import java.util.List;

import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.XaxisData;
import com.hillrom.vest.web.rest.dto.XaxisDataVO;

public abstract class AbstractGraphBuilder implements GraphBuilder{

	protected Graph graph = new Graph();

	/**
	 * populates the xAxisData with dates/legends selected based on the configuration requested
	 * @param xAxisData
	 * @return
	 */
	public Graph populateXaxisData(XaxisDataVO xAxisData){
		XaxisData xAxis = new XaxisData();
		xAxis.setType(xAxisData.getType());
		if (xAxisData.isUseLegends()) {
			xAxis.setCategories(xAxisData.getLegends());
		} else {
			List<String> categories = new LinkedList<>();
			if (WEEK.equalsIgnoreCase(xAxisData.getDuration())
					|| CUSTOM.equalsIgnoreCase(xAxisData.getDuration())) {
				categories = DateUtil.getDatesStringGroupByDay(xAxisData.getDates());
			} else if (MONTH.equalsIgnoreCase(xAxisData.getDuration())) {
				categories = DateUtil.getDatesStringGroupByWeek(xAxisData.getDates());
			} else if (YEAR.equalsIgnoreCase(xAxisData.getDuration())) {
				categories = DateUtil.getDatesStringGroupByMonth(xAxisData.getDates());
			}
			xAxis.setCategories(categories);
		}
		graph.setxAxis(xAxis);
		return graph;
	}

	/**
	 * Template method to build the Graph Objects
	 * Steps 1.populate X-axis data 2. populate series data 3. assign series object to graph and return
	 * @param xAxisData, data , filter
	 * @return
	 */
	public Graph buildGraph(XaxisDataVO xAxisData,Object data,Filter filter){
		populateXaxisData(xAxisData);
		List<Series> series = buildSeries(data,filter);
		graph.setSeries(series);
		return graph;
	}
	
	/**
	 * abstract method to be overriden by sub classes
	 */
	public abstract List<Series> buildSeries(Object data,Filter filter);
}
