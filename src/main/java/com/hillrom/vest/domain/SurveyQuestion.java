package com.hillrom.vest.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "QUESTIONS")
public class SurveyQuestion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "question_text")
	private String questionText;

	@ManyToOne
	@JoinColumn(name = "survey_ans_format_id")
	private HillromTypeCodeFormat typeCodeFormat;

	@Column(name = "mandatory")
	private Boolean mandatory;

	@Column(name = "possible_ans_1")
	private String answer1;

	@Column(name = "possible_ans_2")
	private String answer2;

	@Column(name = "possible_ans_3")
	private String answer3;

	@Column(name = "possible_ans_4")
	private String answer4;

	@Column(name = "possible_ans_5")
	private String answer5;

	@Column(name = "possible_ans_6")
	private String answer6;

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

	public HillromTypeCodeFormat getSurveyAnsFormatId() {
		return typeCodeFormat;
	}

	public void setSurveyAnsFormatId(HillromTypeCodeFormat typeCodeFormat) {
		this.typeCodeFormat = typeCodeFormat;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}

	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}

	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}

	public String getAnswer4() {
		return answer4;
	}

	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}

	public String getAnswer5() {
		return answer5;
	}

	public void setAnswer5(String answer5) {
		this.answer5 = answer5;
	}

	public String getAnswer6() {
		return answer6;
	}

	public void setAnswer6(String answer6) {
		this.answer6 = answer6;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((typeCodeFormat == null) ? 0 : typeCodeFormat.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyQuestion other = (SurveyQuestion) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (typeCodeFormat == null) {
			if (other.typeCodeFormat != null)
				return false;
		} else if (!typeCodeFormat.equals(other.typeCodeFormat))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SurveyQuestion [id=" + id + ", questionText=" + questionText + ", SurveyAnsFormatId="
				+ typeCodeFormat + ", mandatory=" + mandatory + ", answer1=" + answer1 + ", answer2=" + answer2
				+ ", answer3=" + answer3 + ", answer4=" + answer4 + ", answer5=" + answer5 + ", answer6=" + answer6
				+ "]";
	}
}
