package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class StateVO {

	private String name;	
	private List<CityVO> cities = new LinkedList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CityVO> getCities() {			
		return cities;
	}
}
