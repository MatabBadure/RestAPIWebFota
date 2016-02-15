package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.DateTimeSerializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

public class ProtocolRevisionVO implements Serializable{

	private DateTime from;
	private DateTime to;
	private List<ProtocolDataVO> protcols= new LinkedList<>();
	private List<AdherenceTrendVO> adherenceTrends = new LinkedList<>();
	
	@JsonSerialize(using= DateTimeSerializer.class)
	public DateTime getFrom() {
		return from;
	}
	public void setFrom(DateTime from) {
		this.from = from;
	}
	
	@JsonSerialize(using= DateTimeSerializer.class)
	public DateTime getTo() {
		return to;
	}
	public void setTo(DateTime to) {
		this.to = to;
	}
	public List<ProtocolDataVO> getProtcols() {
		return protcols;
	}
	public void setProtcols(List<ProtocolDataVO> protcols) {
		this.protcols = protcols;
	}
	public List<AdherenceTrendVO> getAdherenceTrends() {
		return adherenceTrends;
	}
	public void setAdherenceTrends(List<AdherenceTrendVO> adherenceTrends) {
		this.adherenceTrends = adherenceTrends;
	}
	public void addProtocol(ProtocolDataVO protocol){
		if(!protcols.contains(protocol))
			this.protcols.add(protocol);
	}
	public void addAdherenceTrend(AdherenceTrendVO adherenceTrend){
		this.adherenceTrends.add(adherenceTrend);
	}
	@Override
	public String toString() {
		return "ProtocolRevisionVO [from=" + from + ", to=" + to
				+ ", protcols=" + protcols + ", adherenceTrends="
				+ adherenceTrends + "]";
	}

	
	
}
