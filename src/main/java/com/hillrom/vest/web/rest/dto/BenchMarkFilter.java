package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;

import org.joda.time.LocalDate;

public class BenchMarkFilter extends Filter{

	private String benchMarkType;
	private String benchMarkParameter;
	private Long userId; 
	private String clinicId;
	
	public BenchMarkFilter(LocalDate from,LocalDate to,
			String xAxisParameter,String benchMarkType,
			String benchMarkParameter,String stateCSV,
			String cityCSV,String rangeCSV) {
		super(from,to,xAxisParameter,stateCSV,cityCSV,rangeCSV);
		this.benchMarkType = benchMarkType;
		this.benchMarkParameter = benchMarkParameter;
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
		super(from,to,xAxisParameter,"","",range);
		this.benchMarkType = benchMarkType;
		this.benchMarkParameter = benchMarkParameter;
		this.userId = userId;
		this.clinicId = clinicId;
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
