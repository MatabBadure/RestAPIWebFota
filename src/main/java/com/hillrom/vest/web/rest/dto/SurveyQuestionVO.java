package com.hillrom.vest.web.rest.dto;

import java.util.LinkedList;
import java.util.List;

import com.hillrom.vest.domain.HillromTypeCodeFormat;

public class SurveyQuestionVO {
	
	private Long id;
	private String questionText;
	private HillromTypeCodeFormat typeCodeFormat;
	private Boolean mandatory;
	private List<String> answers = new LinkedList<>();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQuestionText() {
		return questionText;
	}
	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}
	public HillromTypeCodeFormat getTypeCodeFormat() {
		return typeCodeFormat;
	}
	public void setTypeCodeFormat(HillromTypeCodeFormat typeCodeFormat) {
		this.typeCodeFormat = typeCodeFormat;
	}
	public Boolean getMandatory() {
		return mandatory;
	}
	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}
	public List<String> getAnswers() {
		return answers;
	}
	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}
}
