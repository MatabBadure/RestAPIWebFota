package com.hillrom.vest.web.rest.dto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class Series {
	
	private String name;
	private List<GraphDataVO> data = new LinkedList<>();
	@JsonInclude(Include.NON_EMPTY)
	private Map<String,Object> plotLines = new HashMap<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<GraphDataVO> getData() {
		return data;
	}
	public void setData(List<GraphDataVO> data) {
		this.data = data;
	}
	public Map<String, Object> getPlotLines() {
		return plotLines;
	}
	public void setPlotLines(Map<String, Object> plotLines) {
		this.plotLines = plotLines;
	}
	
	
}
