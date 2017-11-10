package com.hillrom.vest.web.rest.dto;

import java.util.List;

public class CountryStateDTO {

	List<String> country;
	List<String> state;
	
	public CountryStateDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CountryStateDTO(List<String> country, List<String> state) {
		super();
		this.country = country;
		this.state = state;
	}
	
	public List<String> getCountry() {
		return country;
	}
	public void setCountry(List<String> country) {
		this.country = country;
	}
	public List<String> getState() {
		return state;
	}
	public void setState(List<String> state) {
		this.state = state;
	}
	
}
