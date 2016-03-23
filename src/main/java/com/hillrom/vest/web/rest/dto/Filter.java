package com.hillrom.vest.web.rest.dto;

import static com.hillrom.vest.config.Constants.AGE_GROUP;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;

public class Filter {

	private LocalDate from;
	private LocalDate to;
	private String duration;
	private List<String> legends = new LinkedList<>();
	private String xAxisParameter;
	private String stateCSV;
	private String cityCSV;
	private String ageRangeCSV;
	private String clinicSizeRangeCSV;
	private boolean ignoreXAxis;
	
	public Filter() {
		super();
	}
	public Filter(LocalDate from, LocalDate to, String duration,
			List<String> legends) {
		super();
		this.from = from;
		this.to = to;
		this.duration = duration;
		this.legends = legends;
	}
	
	public Filter(LocalDate from,LocalDate to,
			String xAxisParameter,String stateCSV,
			String cityCSV,String rangeCSV) {
		this(from,to,"",new LinkedList<>());
		this.xAxisParameter = xAxisParameter;
		this.stateCSV = stateCSV;
		this.cityCSV = cityCSV;
		if(AGE_GROUP.equalsIgnoreCase(xAxisParameter))
			ageRangeCSV = rangeCSV;
		else
			clinicSizeRangeCSV = rangeCSV;
	}
	
	public Filter(LocalDate from,LocalDate to,
			String xAxisParameter,String stateCSV,
			String cityCSV,String ageRangeCSV,String clinicSizeRangeCSV) {
		this(from,to,"",new LinkedList<>());
		this.xAxisParameter = xAxisParameter;
		this.stateCSV = stateCSV;
		this.cityCSV = cityCSV;
		this.ageRangeCSV = ageRangeCSV;
		this.clinicSizeRangeCSV = clinicSizeRangeCSV;
	}
	
	public Filter(LocalDate from,LocalDate to,
			String xAxisParameter, String stateCSV,
			String cityCSV, String ageRangeCSV, String clinicSizeRangeCSV,
			boolean ignoreXAxis) {
		this(from,to,xAxisParameter,stateCSV,cityCSV,ageRangeCSV,clinicSizeRangeCSV);
		this.ignoreXAxis = ignoreXAxis;
	}

	public LocalDate getFrom() {
		return from;
	}
	public void setFrom(LocalDate from) {
		this.from = from;
	}
	public LocalDate getTo() {
		return to;
	}
	public void setTo(LocalDate to) {
		this.to = to;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public List<String> getLegends() {
		return legends;
	}
	public void setLegends(List<String> legends) {
		this.legends = legends;
	}
	public String getxAxisParameter() {
		return xAxisParameter;
	}
	public void setxAxisParameter(String xAxisParameter) {
		this.xAxisParameter = xAxisParameter;
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
	public String getAgeRangeCSV() {
		return ageRangeCSV;
	}
	public void setAgeRangeCSV(String ageRangeCSV) {
		this.ageRangeCSV = ageRangeCSV;
	}
	public String getClinicSizeRangeCSV() {
		return clinicSizeRangeCSV;
	}
	public void setClinicSizeRangeCSV(String clinicSizeRangeCSV) {
		this.clinicSizeRangeCSV = clinicSizeRangeCSV;
	}
	public boolean isIgnoreXAxis() {
		return ignoreXAxis;
	}
	public void setIgnoreXAxis(boolean ignoreXAxis) {
		this.ignoreXAxis = ignoreXAxis;
	}
	
}
