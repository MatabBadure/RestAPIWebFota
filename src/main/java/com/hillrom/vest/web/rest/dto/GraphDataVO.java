package com.hillrom.vest.web.rest.dto;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.DecimalNumberSerializer;

public class GraphDataVO {

	@JsonInclude(Include.NON_NULL)
	private String x;
	@JsonSerialize(using=DecimalNumberSerializer.class)
	private double y;
	
	@JsonInclude(Include.NON_EMPTY)
	private Map<String,Object> toolText = new HashMap<>();
	
	public GraphDataVO() {
		super();
	}
	
	public GraphDataVO(String x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	public void setToolText(Map<String, Object> toolText) {
		this.toolText = toolText;
	}

	public Map<String, Object> getToolText() {
		return toolText;
	}
	
	
}
