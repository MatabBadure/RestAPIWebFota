package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class LoginAnalyticsVO implements Serializable{

	private LocalDate date;
	private int loginCount;
	private String authority;
	private String weekOrMonthString;
	private Long userId;
	
	
	public LoginAnalyticsVO() {
		super();
	}

	public LoginAnalyticsVO(LocalDate date, int loginCount, String authority) {
		super();
		this.date = date;
		this.loginCount = loginCount;
		this.authority = authority;
	}
	
	public LoginAnalyticsVO(String weekOrMonth, int loginCount, String authority) {
		super();
		this.weekOrMonthString = weekOrMonth;
		this.loginCount = loginCount;
		this.authority = authority;
	}

	public LoginAnalyticsVO(LocalDate date, int loginCount, String authority,
			Long userId) {
		super();
		this.date = date;
		this.loginCount = loginCount;
		this.authority = authority;
		this.setUserId(userId);
	}

	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public int getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public int getYearOfCentury(){
		return date.getYearOfCentury();
	}
	public int getMonthOfYear(){
		return date.getMonthOfYear();
	}
	public String getWeekOrMonthString() {
		return weekOrMonthString;
	}
	public void setWeekOrMonthString(String weekOrMonthString) {
		this.weekOrMonthString = weekOrMonthString;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


}
