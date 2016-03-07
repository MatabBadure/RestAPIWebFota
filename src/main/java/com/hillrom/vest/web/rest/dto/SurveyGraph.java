package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

public class SurveyGraph extends Graph {

	private int count;
	private List<String> surveyQuestions = new LinkedList<>();
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<String> getSurveyQuestions() {
		return surveyQuestions;
	}
	public void setSurveyQuestions(List<String> surveyQuestions) {
		this.surveyQuestions = surveyQuestions;
	}
	
	
	
}
