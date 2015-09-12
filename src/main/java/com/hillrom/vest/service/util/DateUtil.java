package com.hillrom.vest.service.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.util.ExceptionConstants;

import static  com.hillrom.vest.config.Constants.*;
import static com.hillrom.vest.service.util.DateUtil.convertLocalDateToDateTime;

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
	
	/**
	 * Converts LocalDate to DateTime
	 * @param date
	 * @return
	 */
	public static DateTime convertLocalDateToDateTime(
			LocalDate date) {
		return new DateTime(date.toDateTime(org.joda.time.LocalTime.MIDNIGHT));
	}
	
	public static LocalDate parseStringToLocalDate(String dateString, String dateFormat) throws HillromException{
		dateFormat = Objects.nonNull(dateFormat) ? dateFormat : YYYY_MM_DD; 
		final DateTimeFormatter dtf = DateTimeFormat.forPattern(dateFormat);
		try{
			return dtf.parseLocalDate(dateString);
		}catch(Exception ex){
			throw new HillromException(ExceptionConstants.HR_600.concat(dateFormat), ex);
		}
    	
	}
	
	
	/** Get Date by Plus or Minus Days
	 * @param days
	 * @return
	 */
	public static LocalDate getPlusOrMinusTodayLocalDate(int days){
		LocalDate today = LocalDate.now();
		if(days > 0){
			return today.plusDays(days);
		}else{
			return today.minusDays(Math.abs(days));
		}
	}
	
	/**
	 * Returns today in LocalDate
	 * @return 
	 */
	public static LocalDate getTodayLocalDate(){
		return LocalDate.now();
	}
	
	/**
	 * Return days difference between two Local Dates
	 * @param from
	 * @param to
	 * @return
	 */
	public static int getDaysCountBetweenLocalDates(LocalDate from,LocalDate to){
		return Days.daysBetween(convertLocalDateToDateTime(from), convertLocalDateToDateTime(to)).getDays();
	}
}
