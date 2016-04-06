package com.hillrom.vest.web.rest.dto;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

	public boolean isDataAvailable(){
		List<Integer> yValues = new LinkedList<>();
		List<Integer> toolTipData = new LinkedList<>();
		for(Series series : series){
			List<GraphDataVO> graphData = series.getData();
			for(GraphDataVO data : graphData){
				yValues.add(data.getY());
				Map<String,Object> tooltipMap = data.getToolText();
				for(String key : tooltipMap.keySet()){
					int value = Objects.nonNull(tooltipMap.get(key))? (Integer)tooltipMap.get(key) : 0 ;
					toolTipData.add(value);
				}
			}
		}
		Collections.reverse(yValues);
		Collections.reverse(toolTipData);
		// if tool tip data and yValues both are 0 , then no data available
		if((yValues.isEmpty() && toolTipData.isEmpty()) 
				|| yValues.get(0).equals(0) && toolTipData.get(0).equals(0)){
			return true;
		}
		return false;
	}
}
