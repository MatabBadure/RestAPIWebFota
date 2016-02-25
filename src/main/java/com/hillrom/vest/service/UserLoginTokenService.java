package com.hillrom.vest.service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.UserLoginTokenRepository;
import com.hillrom.vest.security.xauth.TokenProvider;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.graph.builders.GraphBuilder;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;
import com.hillrom.vest.web.rest.dto.XaxisDataVO;

@Service
@Transactional
public class UserLoginTokenService {
	
	private final Logger log = LoggerFactory.getLogger(UserLoginTokenService.class);
	
	@Inject
	private UserLoginTokenRepository tokenRepository;
	
	@Inject
	private TokenProvider tokenProvider;
	
	@Inject
	@Qualifier("loginAnalyticsGraphBuilder")
	private GraphBuilder graphBuilder;
	
	public UserLoginToken findOneById(String id){
		return tokenRepository.findOne(id);
	}
	
	public void deleteToken(String id){
		tokenRepository.delete(id);
	}
	
	public boolean validateToken(String authToken){
		UserLoginToken securityToken = findOneById(authToken);
		
		if(null == securityToken){
			return false;
		}else{
			
			DateTime tokenModifiedAt = securityToken.getLastModifiedTime();
			long expiryTimeInMillis = tokenModifiedAt.plus(1000 * tokenProvider.getTokenValidity()).getMillis();
			DateTime now = DateTime.now();
			
			boolean flag = false;
			if(expiryTimeInMillis - System.currentTimeMillis() >= 0 && !securityToken.isExpired() ){
				log.debug("Created time for Token : " + securityToken.getCreatedTime());
				log.debug("Last updated time for Token : " + tokenModifiedAt);
				log.debug("Current application  time  : " + DateTime.now());
				log.debug("Difference in seconds      : " + (expiryTimeInMillis - System.currentTimeMillis())/1000);
				flag = true;
				securityToken.setLastModifiedTime(now);
				log.debug("flag : " + flag);
				tokenRepository.save(securityToken);
				return flag;				
			}else{
				flag = false;
				log.debug("Created time for Token : " + securityToken.getCreatedTime());
				log.debug("Last updated time for Token : " + tokenModifiedAt);
				log.debug("Current application  time  : " + DateTime.now());
				log.debug("Difference in seconds      : " + (expiryTimeInMillis - System.currentTimeMillis())/1000);
				return flag;				
			}
		}
	}
	
	public Graph getLoginAnalytics(LocalDate from,LocalDate to,String authorityCSV,String duration) throws HillromException{
		List<String> authorities = Arrays.asList(authorityCSV.split(","));
		List<LoginAnalyticsVO> actualLoginAnalytics = new LinkedList<>();
		List<LocalDate> dates = new LinkedList<>();
		if(Constants.WEEK.equalsIgnoreCase(duration) || Constants.DAY.equalsIgnoreCase(duration)||
				Constants.CUSTOM.equalsIgnoreCase(duration)){
			actualLoginAnalytics = tokenRepository.getAnalyticsForWeekOrDay(from.toString(), to.toString(), authorities);
		}else if(Constants.YEAR.equalsIgnoreCase(duration)){
			actualLoginAnalytics = tokenRepository.getAnalyticsForYear(from.toString(), to.toString(), authorities);
		}else {
			actualLoginAnalytics = tokenRepository.getAnalyticsForMonth(from.toString(), to.toString(), authorities);
			actualLoginAnalytics = applyGroupByWeek(actualLoginAnalytics, from, to);
		}
		List<LoginAnalyticsVO> loginAnalytics = prepareDefaultDataForLoginAnalytics(from,to,authorities,duration);
		// update the default with actual 
		updateDefualtWithActualAnalytics(actualLoginAnalytics, loginAnalytics,duration);
		boolean isUseLegends = false;
		List<String> legends = new LinkedList<>();
		// if duration is month,year : loginAnalytics to be populated with weekOrMonthString
		// since dates field of analytics not populated , need to pass the strings in legend and flag to be true 
		if(Constants.MONTH.equalsIgnoreCase(duration) || Constants.YEAR.equalsIgnoreCase(duration)){
			isUseLegends = true;
			for(LoginAnalyticsVO analytic : loginAnalytics){
				if(Objects.nonNull(analytic.getWeekOrMonthString()) && !legends.contains(analytic.getWeekOrMonthString()))
					legends.add(analytic.getWeekOrMonthString());
			}
		}else if(from.equals(to)){// if the duration is Day , populate x-axis with authorities
			isUseLegends = true;
			legends = authorities;
		}else {// for any other duration , populate x-axis with date string
			legends = authorities;
			loginAnalytics.forEach(analytic -> {
				if(Objects.nonNull(analytic.getDate()) && !dates.contains(analytic.getDate()))
					dates.add(analytic.getDate());
			});
		}
		Filter filter = new Filter(from,to,duration,authorities);
		XaxisDataVO xaxisDataVO = new XaxisDataVO(null,dates, duration, isUseLegends, legends);
		return graphBuilder.buildGraph(xaxisDataVO, loginAnalytics, filter);
	}

	private List<LoginAnalyticsVO> applyGroupByWeek(
			List<LoginAnalyticsVO> actualLoginAnalytics,LocalDate from,LocalDate to) throws HillromException {
		List<LoginAnalyticsVO> groupByWeek = new LinkedList<>();
		// Get the analytics group by role
		Map<String,List<LoginAnalyticsVO>> groupByRole = (Map<String,List<LoginAnalyticsVO>>) actualLoginAnalytics.stream().collect(Collectors.groupingBy(LoginAnalyticsVO::getAuthority));
		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		// prepare week strings for the duration selected
		List<String> weekStrings = DateUtil.getDatesStringGroupByWeek(requestedDates);
		for(String authority: groupByRole.keySet()){
			List<LoginAnalyticsVO> analytics = groupByRole.get(authority);
			// get the analytics group by userId,to count the logins with unique users
			Map<Long,List<LoginAnalyticsVO>> groupByUserId = analytics.stream().collect(Collectors.groupingBy(LoginAnalyticsVO :: getUserId));
			for(String weekString : weekStrings){
				String[] weekStartEnd = weekString.substring(weekString.indexOf('(')+1,weekString.indexOf(')')).split(" to ");
				LocalDate start = DateUtil.parseStringToLocalDate(weekStartEnd[0], Constants.DATEFORMAT_ddMMMyy) ;
				LocalDate end = DateUtil.parseStringToLocalDate(weekStartEnd[1], Constants.DATEFORMAT_ddMMMyy) ;
				LoginAnalyticsVO loginAnalyticsVO = new LoginAnalyticsVO();
				loginAnalyticsVO.setAuthority(authority);
				loginAnalyticsVO.setWeekOrMonthString(weekString);
				List<Long> uniqueUserIds = new LinkedList<>();// used to make the logincount with unique users
				int loginCount = 0;
				// for each user, get the analytics and check whether user logged in the current week
				for(Long userId : groupByUserId.keySet()){
					List<LoginAnalyticsVO> analyticsOfUser = groupByUserId.get(userId);
					for(LoginAnalyticsVO analyticsData: analyticsOfUser){
						// increment counter if the user login with in the current week,don't count the same user twice
						if((analyticsData.getDate().isAfter(start) || analyticsData.getDate().isEqual(start))
								&& (analyticsData.getDate().isEqual(end) || analyticsData.getDate().isBefore(end)) 
								&& !uniqueUserIds.contains(userId)){
							loginAnalyticsVO.setLoginCount(++loginCount);
							uniqueUserIds.add(userId);
						}
					}
				}
				groupByWeek.add(loginAnalyticsVO);
			}
		}
		return groupByWeek;
	}

	private void updateDefualtWithActualAnalytics(
			List<LoginAnalyticsVO> actualLoginAnalytics,
			List<LoginAnalyticsVO> loginAnalytics,String duration) {
		for(LoginAnalyticsVO actual : actualLoginAnalytics){
			int index = -1;
			for(LoginAnalyticsVO defaultAnalytics : loginAnalytics){
				index++;
				if(defaultAnalytics.getAuthority().equals(actual.getAuthority())){
					if(Constants.WEEK.equalsIgnoreCase(duration) || Constants.DAY.equalsIgnoreCase(duration)||
							Constants.CUSTOM.equalsIgnoreCase(duration)){
						if(defaultAnalytics.getDate().equals(actual.getDate())){
							loginAnalytics.set(index, actual);
						}
					}else {
						if(defaultAnalytics.getWeekOrMonthString().equalsIgnoreCase(actual.getWeekOrMonthString())){
							loginAnalytics.set(index, actual);
						}
					}
				}
			}
		}
	}

	private List<LoginAnalyticsVO> prepareDefaultDataForLoginAnalytics(LocalDate from,
			LocalDate to, List<String> authorities,String duration) {
		List<LoginAnalyticsVO> defaultAnalytics = new LinkedList<>();
		List<LocalDate> requestedDates = DateUtil.getAllLocalDatesBetweenDates(from, to);
		if(Constants.WEEK.equalsIgnoreCase(duration) || Constants.DAY.equalsIgnoreCase(duration)||
				Constants.CUSTOM.equalsIgnoreCase(duration)){
			return populateLoginAnalyticsForWeekOrDay(authorities,requestedDates);
		}else if(Constants.YEAR.equalsIgnoreCase(duration)){
			return populateLoginAnalyticsForYear(authorities,requestedDates);
		}else if(Constants.MONTH.equalsIgnoreCase(duration)){
			return populateLoginAnalyticsForMonth(authorities,requestedDates);
		}
		return defaultAnalytics;
	}

	private List<LoginAnalyticsVO> populateLoginAnalyticsForYear(List<String> authorities,
			List<LocalDate> requestedDates) {
		List<LoginAnalyticsVO> defaultAnalytics = new LinkedList<>();
		SortedMap<Integer,List<LocalDate>> groupByYear = new TreeMap<>(requestedDates.stream().collect(Collectors.groupingBy(LocalDate::getYearOfCentury)));
		for(int year: groupByYear.keySet()){
			List<LocalDate> dates = groupByYear.get(year);
			SortedMap<Integer,List<LocalDate>> groupByMonth = new TreeMap<>(dates.stream().collect(Collectors.groupingBy(LocalDate::getMonthOfYear)));
			for(int month : groupByMonth.keySet()){
				String monthString = new StringBuilder(DateUtil.getShortMonthNameByIndex(month)).append("'").append(year).toString();
				for(String authority: authorities){
					defaultAnalytics.add(new LoginAnalyticsVO(monthString, 0, authority));
				}
			}
		}
		return defaultAnalytics;
	}

	private List<LoginAnalyticsVO> populateLoginAnalyticsForWeekOrDay(List<String> authorities,
			List<LocalDate> requestedDates) {
		List<LoginAnalyticsVO> defaultAnalytics = new LinkedList<>();
		for(LocalDate requestedDate: requestedDates){
			for(String authority: authorities){
				defaultAnalytics.add(new LoginAnalyticsVO(requestedDate, 0, authority));
			}
		}
		return defaultAnalytics;
	}

	private List<LoginAnalyticsVO> populateLoginAnalyticsForMonth(List<String> authorities,
			List<LocalDate> requestedDates) {
		List<LoginAnalyticsVO> defaultAnalytics = new LinkedList<>();
		int DAYS = 7;
		int weekCounter = 1;
		// for each authority, in given duration make weeks from date selection
		for (int i = 0; i < requestedDates.size(); i += 7) {
			// end of week index
			int lastIndex = i + DAYS > requestedDates.size() ? requestedDates.size() : i + DAYS;
			// creating list of analytics that fall under week duration
			List<LocalDate> subList = requestedDates.subList(i, lastIndex);
			String fromDate = DateUtil.convertLocalDateToStringFromat(subList.get(0), Constants.DATEFORMAT_ddMMMyy);
			String toDate = DateUtil.convertLocalDateToStringFromat(subList.get(subList.size()-1), Constants.DATEFORMAT_ddMMMyy);
			String weekString = "week"+(weekCounter++)+"("+fromDate+" to "+toDate+")";
			for(String authority : authorities){
				defaultAnalytics.add(new LoginAnalyticsVO(weekString, 0, authority));
			}
		}
		return defaultAnalytics;
	}
}
