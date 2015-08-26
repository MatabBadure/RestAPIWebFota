package com.hillrom.vest.service.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;

public class DateUtil {

	private DateUtil(){
		
	}
	
	/**
	 * Get All LocalDates between two LocalDate
	 * @param from
	 * @param to
	 * @return
	 */
	public static List<LocalDate> getAllLocalDatesBetweenDates(LocalDate from, LocalDate to) {
		LocalDate startDate = from;
		List<LocalDate> dates = new LinkedList<>();
		while(startDate.isBefore(to)){
			dates.add(startDate);
			startDate = startDate.plusDays(1);
		}
		return dates;
	}
	
	/**
	 * Group List of LocalDate by week of week year
	 * @param dates
	 * @return
	 */
	public static Map<Integer, List<LocalDate>> groupListOfLocalDatesByWeekOfWeekyear(
			List<LocalDate> dates) {
		Map<Integer,List<LocalDate>> groupByWeek = dates.stream().collect(Collectors.groupingBy(LocalDate :: getWeekOfWeekyear));
		return groupByWeek;
	}
	
	/**
	 * Group List of LocalDate by Month Of Year
	 * @param dates
	 * @return
	 */
	public static Map<Integer, List<LocalDate>> groupListOfLocalDatesByMonthOfYear(
			List<LocalDate> dates) {
		Map<Integer,List<LocalDate>> groupByWeek = dates.stream().collect(Collectors.groupingBy(LocalDate :: getMonthOfYear));
		return groupByWeek;
	}
}
