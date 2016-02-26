package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.XAXIS_TYPE_CATEGORIES;
import static com.hillrom.vest.config.Constants.XAXIS_TYPE_DATETIME;
import static com.hillrom.vest.config.Constants.LA_DAYVIEW_LABEL;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.security.AuthoritiesConstants;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.GraphDataVO;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;
import com.hillrom.vest.web.rest.dto.Series;
import com.hillrom.vest.web.rest.dto.XaxisData;

@Component(value="loginAnalyticsGraphService")
public class LoginAnalyticsGraphService extends AbstractGraphService {

	@SuppressWarnings("unchecked")
	@Override
	public Graph populateGraphDataForDay(Object data, Filter filter) {
		List<LoginAnalyticsVO> loginAnalyticsData = (List<LoginAnalyticsVO>) data;
		Graph analyticsGraph = buildGraphObect(XAXIS_TYPE_CATEGORIES);
		for(String authority : filter.getLegends()){
			analyticsGraph.getxAxis().getCategories().add(getAuthorityLabel(authority));
		}
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		// As per requirement, day view should give the count of logins specific to role
		Series seriesData = new Series();
		seriesData.setName(LA_DAYVIEW_LABEL);
		for(String authority : groupByAuthority.keySet()){
			List<LoginAnalyticsVO> analytics = groupByAuthority.get(authority);
			int loginCount = analytics.stream().collect(Collectors.summingInt(LoginAnalyticsVO::getLoginCount));
			GraphDataVO graphData = new GraphDataVO(null,loginCount);
			seriesData.getData().add(graphData);
		}
		seriesList.add(seriesData);
		analyticsGraph.setSeries(seriesList);
		return analyticsGraph;
	}

	private Series createSeriesObjectWithName(String authority) {
		Series seriesData = new Series();
		seriesData.setName(getAuthorityLabel(authority));
		return seriesData;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph populateGraphDataForWeek(Object data, Filter filter) {
		List<LoginAnalyticsVO> loginAnalyticsData = (List<LoginAnalyticsVO>) data;
		Graph analyticsGraph = buildGraphObect(XAXIS_TYPE_CATEGORIES);
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		for(String authority : groupByAuthority.keySet()){
			Series seriesData = createSeriesObjectWithName(authority);
			List<String> xAxisLabels = new LinkedList<>();
			// get login analytics date wise for each authority
			SortedMap<LocalDate,List<LoginAnalyticsVO>> groupByDate = new TreeMap<>(groupByAuthority.get(authority).stream().collect(Collectors.groupingBy(LoginAnalyticsVO :: getDate)));
			// populate y-axis data with analyticsData for each date
			for(LocalDate date : groupByDate.keySet()){
				xAxisLabels.add(DateUtil.formatDate(date, Constants.MMddyyyy));
				int loginCount = groupByDate.get(date).stream().collect(Collectors.summingInt(LoginAnalyticsVO::getLoginCount));
				GraphDataVO graphData = new GraphDataVO(null,loginCount);
				seriesData.getData().add(graphData);
			}
			analyticsGraph.getxAxis().setCategories(xAxisLabels);
			seriesList.add(seriesData);
		}
		analyticsGraph.setSeries(seriesList);
		return analyticsGraph;
	}

	private Graph buildGraphObect(String type) {
		Graph analyticsGraph = new Graph();
		XaxisData xAxis = new XaxisData();
		xAxis.setType(type);
		analyticsGraph.setxAxis(xAxis);
		return analyticsGraph;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Graph populateGraphDataForMonth(Object data, Filter filter) {
		List<LoginAnalyticsVO> loginAnalyticsData = (List<LoginAnalyticsVO>) data;
		Graph analyticsGraph = buildGraphObect(XAXIS_TYPE_CATEGORIES);
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates		
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		
		for(String authority : groupByAuthority.keySet()){
			Series seriesData = createSeriesObjectWithName(authority);
			List<String> xAxisLabels = new LinkedList<>();
			// analyticsData for specific authority
			List<LoginAnalyticsVO> analyticsData = groupByAuthority.get(authority);
			// Group analyticsData by monthString (ex: jan'16)
			for(LoginAnalyticsVO analytics : analyticsData){
				xAxisLabels.add(analytics.getWeekOrMonthString());
				GraphDataVO graphData = new GraphDataVO(null,analytics.getLoginCount());
				seriesData.getData().add(graphData);
			}
			seriesList.add(seriesData);
			analyticsGraph.getxAxis().setCategories(xAxisLabels);
		}
		analyticsGraph.setSeries(seriesList);
		return analyticsGraph;
	}

	@Override
	public Graph populateGraphDataForYear(Object data, Filter filter) {
		return populateGraphDataForMonth(data,filter);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Graph populateGraphDataForCustomDateRange(Object data, Filter filter) {
		List<LoginAnalyticsVO> loginAnalyticsData = (List<LoginAnalyticsVO>) data;
		Graph analyticsGraph = buildGraphObect(XAXIS_TYPE_DATETIME); 
		List<Series> seriesList = new LinkedList<>();
		// Group by authority gives authority wise list of analytics wrt dates
		Map<String, List<LoginAnalyticsVO>> groupByAuthority = loginAnalyticsData
				.stream().collect(
						Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		for(String authority : groupByAuthority.keySet()){
			Series seriesData = createSeriesObjectWithName(authority);
			List<String> xAxisLabels = new LinkedList<>();
			// get login analytics date wise for each authority
			SortedMap<LocalDate,List<LoginAnalyticsVO>> groupByDate = new TreeMap<>(groupByAuthority.get(authority).stream().collect(Collectors.groupingBy(LoginAnalyticsVO :: getDate)));
			// populate y-axis data with analyticsData for each date
			for(LocalDate date : groupByDate.keySet()){
				String xAxisLabel = DateUtil.formatDate(date, Constants.MMddyyyy);
				xAxisLabels.add(xAxisLabel);
				int loginCount = groupByDate.get(date).stream().collect(Collectors.summingInt(LoginAnalyticsVO::getLoginCount));
				GraphDataVO graphData = new GraphDataVO(xAxisLabel,loginCount);
				seriesData.getData().add(graphData);
			}
			analyticsGraph.getxAxis().setCategories(xAxisLabels);
			seriesList.add(seriesData);
		}
		analyticsGraph.setSeries(seriesList);
		return analyticsGraph;
	}

	/**
	 * Provides the labels appropriate to authority
	 * @param authority
	 * @return
	 */
	private String getAuthorityLabel(String authority) {
		Map<String,String> authorityLabelsMap = new HashMap<>();
		authorityLabelsMap.put(AuthoritiesConstants.HCP,"HCP");
		authorityLabelsMap.put(AuthoritiesConstants.PATIENT,"Patient");
		authorityLabelsMap.put(AuthoritiesConstants.CARE_GIVER,"Care Giver");
		authorityLabelsMap.put(AuthoritiesConstants.CLINIC_ADMIN,"Clinic Admin");
		authorityLabelsMap.put(AuthoritiesConstants.ADMIN,"Admin");
		authorityLabelsMap.put(AuthoritiesConstants.ACCT_SERVICES,"RC Admin");
		authorityLabelsMap.put(AuthoritiesConstants.HILLROM_ADMIN,"Hill-Rom Admin");
		authorityLabelsMap.put(AuthoritiesConstants.ASSOCIATES,"Associates");
		return authorityLabelsMap.get(authority);
	}
}
