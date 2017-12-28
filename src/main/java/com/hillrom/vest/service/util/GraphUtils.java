package com.hillrom.vest.service.util;

import java.util.Objects;

import org.joda.time.LocalDate;

import com.hillrom.vest.domain.PatientNoEvent;
import com.hillrom.vest.domain.PatientNoEventMonarch;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.XaxisData;

import static com.hillrom.vest.config.AdherenceScoreConstants.FIRST_TRANSMISSION_FIRTS_TYPE;
import static com.hillrom.vest.config.AdherenceScoreConstants.FIRST_TRANSMISSION_TRAINING_TYPE;;

public class GraphUtils {

	/**
	 * Creates Graph Object with type and sets Xaxis
	 * @param type
	 * @return
	 */
	public static Graph buildGraphObectWithXAxisType(String type) {
		Graph graph = new Graph();
		XaxisData xAxis = new XaxisData();
		xAxis.setType(type);
		graph.setxAxis(xAxis);
		return graph;
	}
	
	/**
	 * Creates Series Object with given name
	 * @param seriesName
	 * @return
	 */
	public static Series createSeriesObjectWithName(String seriesName) {
		Series seriesData = new Series();
		seriesData.setName(seriesName);
		return seriesData;
	}

	public static LocalDate getFirstTransmissionDateVestByType(
			PatientNoEvent patientNoEvent) {
		if(Objects.nonNull(patientNoEvent.getFirstTransDateType()) && (patientNoEvent.getFirstTransDateType().equals(FIRST_TRANSMISSION_FIRTS_TYPE))){
			return patientNoEvent.getFirstTransmissionDate();
		}else if(Objects.nonNull(patientNoEvent.getFirstTransDateType()) && (patientNoEvent.getFirstTransDateType().equals(FIRST_TRANSMISSION_TRAINING_TYPE))){
			return patientNoEvent.getFirstTransmissionDateBeforeUpdate();
		}else{
			return patientNoEvent.getFirstTransmissionDate();
		}
	
	}

	public static LocalDate getFirstTransmissionDateMonarchByType(
			PatientNoEventMonarch patientNoEvent) {
		if(Objects.nonNull(patientNoEvent.getFirstTransDateType()) && (patientNoEvent.getFirstTransDateType().equals(FIRST_TRANSMISSION_FIRTS_TYPE))){
			return patientNoEvent.getFirstTransmissionDate();
		}else if(Objects.nonNull(patientNoEvent.getFirstTransDateType()) && (patientNoEvent.getFirstTransDateType().equals(FIRST_TRANSMISSION_TRAINING_TYPE))){
			return patientNoEvent.getFirstTransmissionDateBeforeUpdate();
		}else{
			return patientNoEvent.getFirstTransmissionDate();
		}
	}
}
