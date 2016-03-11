package com.hillrom.vest.repository;

import java.util.Objects;

import com.hillrom.vest.web.rest.dto.SurveyReportVO;

public class ThirtyDaySurveyReportVO extends SurveyReportVO{

	private Integer stronglyDisagreeCount;
	private Integer somewhatDisagreeCount;
	private Integer neutralCount;
	private Integer somewhatAgreeCount;
	private Integer stronglyAgreeCount;
	private Integer unableToAccessCount;

	public ThirtyDaySurveyReportVO(Long id, String questionText, Integer stronglyDisagreeCount,
			Integer somewhatDisagreeCount, Integer neutralCount, Integer somewhatAgreeCount, Integer stronglyAgreeCount,
			Integer unableToAccessCount) {
		super(id,questionText);
		this.stronglyDisagreeCount = Objects.nonNull(stronglyDisagreeCount) ? stronglyDisagreeCount : 0;
		this.somewhatDisagreeCount = Objects.nonNull(somewhatDisagreeCount) ? somewhatDisagreeCount : 0;
		this.neutralCount = Objects.nonNull(neutralCount) ? neutralCount : 0;
		this.somewhatAgreeCount = Objects.nonNull(somewhatAgreeCount) ? somewhatAgreeCount : 0;
		this.stronglyAgreeCount = Objects.nonNull(stronglyAgreeCount) ? stronglyAgreeCount : 0;
		this.unableToAccessCount = Objects.nonNull(unableToAccessCount) ? unableToAccessCount : 0;
	}

	public Integer getStronglyDisagreeCount() {
		return stronglyDisagreeCount;
	}

	public void setStronglyDisagreeCount(Integer stronglyDisagreeCount) {
		this.stronglyDisagreeCount = stronglyDisagreeCount;
	}

	public Integer getSomewhatDisagreeCount() {
		return somewhatDisagreeCount;
	}

	public void setSomewhatDisagreeCount(Integer somewhatDisagreeCount) {
		this.somewhatDisagreeCount = somewhatDisagreeCount;
	}

	public Integer getNeutralCount() {
		return neutralCount;
	}

	public void setNeutralCount(Integer neutralCount) {
		this.neutralCount = neutralCount;
	}

	public Integer getSomewhatAgreeCount() {
		return somewhatAgreeCount;
	}

	public void setSomewhatAgreeCount(Integer somewhatAgreeCount) {
		this.somewhatAgreeCount = somewhatAgreeCount;
	}

	public Integer getStronglyAgreeCount() {
		return stronglyAgreeCount;
	}

	public void setStronglyAgreeCount(Integer stronglyAgreeCount) {
		this.stronglyAgreeCount = stronglyAgreeCount;
	}

	public Integer getUnableToAccessCount() {
		return unableToAccessCount;
	}

	public void setUnableToAccessCount(Integer unableToAccessCount) {
		this.unableToAccessCount = unableToAccessCount;
	}

	@Override
	public String toString() {
		return "ThirtyDaySurveyReportVO [stronglyDisagreeCount=" + stronglyDisagreeCount + ", somewhatDisagreeCount="
				+ somewhatDisagreeCount + ", neutralCount=" + neutralCount + ", somewhatAgreeCount="
				+ somewhatAgreeCount + ", stronglyAgreeCount=" + stronglyAgreeCount + ", unableToAccessCount="
				+ unableToAccessCount + "]";
	}

}
