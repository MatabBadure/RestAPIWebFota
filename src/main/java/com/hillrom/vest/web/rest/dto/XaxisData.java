package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

public class XaxisData {

	private String type;
	private List<String> categories = new LinkedList<>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getCategories() {
		return categories;
	}
	public void setCategories(List<String> categories) {
		this.categories = categories;
	}
	
	
}
