package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;

import org.joda.time.LocalDate;

public class BenchMarkFilter extends Filter{

	private String xAxisParameter;
	private String benchMarkType;
	private String benchMarkParameter;
	private String stateCSV;
	private String cityCSV;
	private String rangeCSV;
	private Long userId; 
	private String clinicId;
	
	public BenchMarkFilter(LocalDate from,LocalDate to,
			String xAxisParameter,String benchMarkType,
			String benchMarkParameter,String stateCSV,
			String cityCSV,String rangeCSV) {
		super(from,to,"",new LinkedList<>());
		this.xAxisParameter = xAxisParameter;
		this.benchMarkType = benchMarkType;
		this.benchMarkParameter = benchMarkParameter;
		this.stateCSV = stateCSV;
		this.cityCSV = cityCSV;
		this.rangeCSV = rangeCSV;
	}
	public BenchMarkFilter(LocalDate from,LocalDate to,
			String benchMarkType,
			String benchMarkParameter) {
		super(from,to,"",new LinkedList<>());
		this.benchMarkType = benchMarkType;
		this.benchMarkParameter = benchMarkParameter;
	}
	public BenchMarkFilter(LocalDate from,LocalDate to,
			String range,String xAxisParameter,String benchMarkType,
			String benchMarkParameter,Long userId, String clinicId) {
		super(from,to,"",new LinkedList<>());
		this.rangeCSV = range;
		this.xAxisParameter = xAxisParameter;
		this.benchMarkType = benchMarkType;
		this.benchMarkParameter = benchMarkParameter;
		this.userId = userId;
		this.clinicId = clinicId;
	}

	public String getxAxisParameter() {
		return xAxisParameter;
	}
	public void setxAxisParameter(String xAxisParameter) {
		this.xAxisParameter = xAxisParameter;
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

	public String getStateCSV() {
		return stateCSV;
	}

	public void setStateCSV(String stateCSV) {
		this.stateCSV = stateCSV;
	}

	public String getCityCSV() {
		return cityCSV;
	}

	public void setCityCSV(String cityCSV) {
		this.cityCSV = cityCSV;
	}

	public String getRangeCSV() {
		return rangeCSV;
	}

	public void setRangeCSV(String rangeCSV) {
		this.rangeCSV = rangeCSV;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getClinicId() {
		return clinicId;
	}
	public void setClinicId(String clinicId) {
		this.clinicId = clinicId;
	}	
	
}
