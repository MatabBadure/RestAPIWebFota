package com.hillrom.vest.web.rest.dto;

public class CityNewVo {

	private String name;
	private boolean ticked;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getTicked() {
		return ticked;
	}
	public void setTicked(boolean ticked) {
		this.ticked = ticked;
	}
	public CityNewVo(String name, boolean ticked) {
		super();
		this.name = name;
		this.ticked = ticked;
	}
	public CityNewVo() {
		// TODO Auto-generated constructor stub
	}
	
}

