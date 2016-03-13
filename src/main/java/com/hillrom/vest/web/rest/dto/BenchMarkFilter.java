package com.hillrom.vest.web.rest.dto;

import java.util.List;

import org.joda.time.LocalDate;

public class BenchMarkFilter extends Filter{

	private String xAxisParameter;
	private String yAxisParameter;
	private String benchMarkType;
	private String benchMarkParameter;
	
	
	public BenchMarkFilter(
			String xAxisParameter, String yAxisParameter,
			String benchMarkType, String benchMarkParameter) {
		super();
		this.xAxisParameter = xAxisParameter;
		this.yAxisParameter = yAxisParameter;
		this.benchMarkType = benchMarkType;
		this.benchMarkParameter = benchMarkParameter;
	}
	
	public String getxAxisParameter() {
		return xAxisParameter;
	}
	public void setxAxisParameter(String xAxisParameter) {
		this.xAxisParameter = xAxisParameter;
	}
	public String getyAxisParameter() {
		return yAxisParameter;
	}
	public void setyAxisParameter(String yAxisParameter) {
		this.yAxisParameter = yAxisParameter;
	}
	public String getBenchMarkType() {
		return benchMarkType;
	}
	public void setBenchMarkType(String benchMarkType) {
		this.benchMarkType = benchMarkType;
	}
	public String getBenchMarkParameter() {
		return benchMarkParameter;
	}
	public void setBenchMarkParameter(String benchMarkParameter) {
		this.benchMarkParameter = benchMarkParameter;
	}
	
	
}
