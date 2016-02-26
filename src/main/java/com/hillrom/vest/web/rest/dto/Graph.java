package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

public class Graph {

	private XaxisData xAxis = new XaxisData();
	private List<Series> series = new LinkedList<>();
	
	public XaxisData getxAxis() {
		return xAxis;
	}
	public void setxAxis(XaxisData xAxis) {
		this.xAxis = xAxis;
	}
	public List<Series> getSeries() {
		return series;
	}
	public void setSeries(List<Series> series) {
		this.series = series;
	}
	
	
	
}
