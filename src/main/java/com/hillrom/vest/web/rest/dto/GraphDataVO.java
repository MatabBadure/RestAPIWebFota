package com.hillrom.vest.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class GraphDataVO {

	@JsonInclude(Include.NON_NULL)
	private String x;
	private int y;
	
	public GraphDataVO() {
		super();
	}
	
	public GraphDataVO(String x, int y) {
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

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
