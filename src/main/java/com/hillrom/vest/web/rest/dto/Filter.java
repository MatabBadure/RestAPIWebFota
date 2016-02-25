package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.LocalDate;

public class Filter {

	private LocalDate from;
	private LocalDate to;
	private String duration;
	private List<String> legends = new LinkedList<>();
	
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
	
	
}
