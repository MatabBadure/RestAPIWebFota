package com.hillrom.vest.web.rest.dto;

import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDate;

import com.hillrom.vest.config.Constants;

public class XaxisDataVO {
	private String type;
	private List<LocalDate> dates;
	private String duration;
	private boolean useLegends;
	private List<String> legends;

	
	public XaxisDataVO(String type,List<LocalDate> dates,
			String duration, boolean useLegends, List<String> legends) {
		super();
		this.type = type;
		this.dates = dates;
		this.duration = duration;
		this.useLegends = useLegends;
		this.legends = legends;
	}
	
	
	public String getType() {
		return Objects.isNull(type) ? (Constants.CUSTOM.equalsIgnoreCase(duration)?"datetime":"categories"):type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<LocalDate> getDates() {
		return dates;
	}
	public void setDates(List<LocalDate> dates) {
		this.dates = dates;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public boolean isUseLegends() {
		return useLegends;
	}
	public void setUseLegends(boolean useLegends) {
		this.useLegends = useLegends;
	}
	public List<String> getLegends() {
		return legends;
	}
	public void setLegends(List<String> legends) {
		this.legends = legends;
	}

}
