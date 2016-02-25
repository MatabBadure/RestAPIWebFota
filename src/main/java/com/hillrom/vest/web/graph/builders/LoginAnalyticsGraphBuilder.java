package com.hillrom.vest.web.graph.builders;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;
import com.hillrom.vest.web.rest.dto.Series;

@Component(value="loginAnalyticsGraphBuilder")
public class LoginAnalyticsGraphBuilder extends AbstractGraphBuilder{

	/**
	 * Build the series object required for loginAnalytics graph as per the filters applied
	 * @param loginAnalyticsData,filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Series> buildSeries(Object data, Filter filter) {
		List<Series> series = new LinkedList<>();
		List<LoginAnalyticsVO> loginAnalyticsData = (List<LoginAnalyticsVO>) data;
		if(Constants.DAY.equalsIgnoreCase(filter.getDuration())){
			series = populateSeriesDataByDay(loginAnalyticsData);
		}else  if(Constants.WEEK.equalsIgnoreCase(filter.getDuration()) 
				|| Objects.isNull(filter.getDuration())){
			series = populateSeriesDataByWeek(loginAnalyticsData);
		}else if(Constants.MONTH.equalsIgnoreCase(filter.getDuration()) || Constants.YEAR.equalsIgnoreCase(filter.getDuration())){
			series = populateSeriesDataByMonthOrYear(loginAnalyticsData);
		}else{
			series = populateSeriesData(loginAnalyticsData);
		}
		return series;
	}
	
	/**
	 * Returns List<Series> Objects required for building y-axis 
	 * @param loginAnalyticsData
	 * @return
	 */
	private List<Series> populateSeriesDataByWeek(List<LoginAnalyticsVO> loginAnalyticsData){
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		for(String authority : groupByAuthority.keySet()){
			// get login analytics date wise for each authority
			SortedMap<LocalDate,List<LoginAnalyticsVO>> groupByDate = new TreeMap<>(groupByAuthority.get(authority).stream().collect(Collectors.groupingBy(LoginAnalyticsVO :: getDate)));
			Series seriesData = createSeriesObjectWithAuthority(authority);
			// populate y-axis data with analyticsData for each date
			for(LocalDate date : groupByDate.keySet()){
				GraphDataVO graphData = new GraphDataVO();
				graphData.setY(groupByDate.get(date).stream().collect(Collectors.summingInt(LoginAnalyticsVO::getLoginCount)).toString());
				seriesData.getData().add(graphData);
			}
			seriesList.add(seriesData);
		}
		return seriesList;
	}

	/**
	 * Provides the labels appropriate to authority
	 * @param authority
	 * @return
	 */
	private Series createSeriesObjectWithAuthority(String authority) {
		Series seriesData = new Series();
		Map<String,String> authorityLabelsMap = new HashMap<>();
		authorityLabelsMap.put(AuthoritiesConstants.HCP,"HCP");
		authorityLabelsMap.put(AuthoritiesConstants.PATIENT,"Patient");
		authorityLabelsMap.put(AuthoritiesConstants.CARE_GIVER,"Care Giver");
		authorityLabelsMap.put(AuthoritiesConstants.CLINIC_ADMIN,"Clinic Admin");
		authorityLabelsMap.put(AuthoritiesConstants.ADMIN,"Admin");
		authorityLabelsMap.put(AuthoritiesConstants.ACCT_SERVICES,"RC Admin");
		authorityLabelsMap.put(AuthoritiesConstants.HILLROM_ADMIN,"Hill-Rom Admin");
		authorityLabelsMap.put(AuthoritiesConstants.ASSOCIATES,"Associates");
		seriesData.setName(authorityLabelsMap.get(authority));
		return seriesData;
	}
		
	/**
	 * Returns List<Series> Objects required for building y-axis
	 * @param loginAnalyticsData
	 * @return
	 */
	private List<Series> populateSeriesDataByMonthOrYear(List<LoginAnalyticsVO> loginAnalyticsData){
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates		
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		
		for(String authority : groupByAuthority.keySet()){
			Series series = createSeriesObjectWithAuthority(authority);
			// analyticsData for specific authority
			List<LoginAnalyticsVO> analyticsData = groupByAuthority.get(authority);
			// Group analyticsData by monthString (ex: jan'16)
			List<String> categories = graph.getxAxis().getCategories();
			for(String category: categories){
				for(LoginAnalyticsVO analytics : analyticsData){
					if(analytics.getWeekOrMonthString().equalsIgnoreCase(category)){
						GraphDataVO graphData = new GraphDataVO();
						graphData.setY(analytics.getLoginCount()+"");
						series.getData().add(graphData);
					}
				}
			}
			seriesList.add(series);
		}
		return seriesList;
	}
	
	/**
	 * Returns List<Series> Objects required for building y-axis
	 * @param loginAnalyticsData
	 * @return
	 */
	private List<Series> populateSeriesDataByDay(List<LoginAnalyticsVO> loginAnalyticsData){
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		// As per requirement, day view should give the count of logins specific to role
		for(String authority : groupByAuthority.keySet()){
			Series seriesData = createSeriesObjectWithAuthority(authority);
			List<LoginAnalyticsVO> analytics = groupByAuthority.get(authority);
			GraphDataVO graphData = new GraphDataVO();
			graphData.setY(analytics.stream().collect(Collectors.summingInt(LoginAnalyticsVO::getLoginCount)).toString());
			seriesData.getData().add(graphData);
			seriesList.add(seriesData);
		}
		return seriesList;
	}
	
	/**
	 * Returns List<Series> Objects required for building y-axis
	 * @param loginAnalyticsData
	 * @return
	 */
	private List<Series> populateSeriesData(List<LoginAnalyticsVO> loginAnalyticsData){
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		// As per requirement, custom date range selection should display the login count specific to role for each date
		for(String authority : groupByAuthority.keySet()){
			Series seriesData = createSeriesObjectWithAuthority(authority);
			List<LoginAnalyticsVO> analytics = groupByAuthority.get(authority);
			for(LoginAnalyticsVO data: analytics){
				GraphDataVO graphData = new GraphDataVO();
				graphData.setX(DateUtil.convertLocalDateToStringFromat(data.getDate(), Constants.DATEFORMAT_ddMMMyy));
				graphData.setY(String.valueOf(data.getLoginCount()));
				seriesData.getData().add(graphData);
			}
			seriesList.add(seriesData);
		}
		return seriesList;
	}
}
