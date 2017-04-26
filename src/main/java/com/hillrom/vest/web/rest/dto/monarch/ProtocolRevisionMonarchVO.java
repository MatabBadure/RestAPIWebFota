package com.hillrom.vest.web.rest.dto.monarch;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.DateTimeSerializer;
import com.hillrom.vest.domain.util.MMDDYYYYLocalDateSerializer;

public class ProtocolRevisionMonarchVO implements Serializable{

	private DateTime from;
	private DateTime to;
	private List<ProtocolDataMonarchVO> protcols= new LinkedList<>();
	private List<AdherenceTrendMonarchVO> adherenceTrends = new LinkedList<>();
	
	public ProtocolRevisionMonarchVO() {
		super();
	}
	
	public ProtocolRevisionMonarchVO(DateTime from, DateTime to) {
		super();
		this.from = from;
		this.to = to;
	}

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
	public List<ProtocolDataMonarchVO> getProtcols() {
		return protcols;
	}
	public void setProtcols(List<ProtocolDataMonarchVO> protcols) {
		this.protcols = protcols;
	}
	public List<AdherenceTrendMonarchVO> getAdherenceTrends() {
		return adherenceTrends;
	}
	public void setAdherenceTrends(List<AdherenceTrendMonarchVO> adherenceTrends) {
		this.adherenceTrends = adherenceTrends;
	}
	public void addProtocol(ProtocolDataMonarchVO protocol){
		if(!protcols.contains(protocol))
			this.protcols.add(protocol);
	}
	public void addAdherenceTrend(AdherenceTrendMonarchVO adherenceTrend){
		this.adherenceTrends.add(adherenceTrend);
	}
	@Override
	public String toString() {
		return "ProtocolRevisionMonarchVO [from=" + from + ", to=" + to
				+ ", protcols=" + protcols + ", adherenceTrends="
				+ adherenceTrends + "]";
	}

	
	
}
