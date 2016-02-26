package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

public class Series {
	
	private String name;
	List<GraphDataVO> data = new LinkedList<>();
	
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
	
	
}
