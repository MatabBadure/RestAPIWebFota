package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.WEEK_SEPERATOR;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;

@Service
@Transactional
public class UserLoginTokenService {
	
	private final Logger log = LoggerFactory.getLogger(UserLoginTokenService.class);
	
	@Inject
	private UserLoginTokenRepository tokenRepository;
	
	@Inject
	private TokenProvider tokenProvider;
	
	@Inject
	@Qualifier("loginAnalyticsGraphService")
	private GraphService graphService;
	
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
		Filter filter = new Filter(from,to,duration,authorities);
		return graphService.populateGraphData(loginAnalytics, filter);
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
				String[] weekStartEnd = weekString.split(WEEK_SEPERATOR);
				LocalDate start = DateUtil.parseStringToLocalDate(weekStartEnd[0], Constants.MMddyyyy) ;
				LocalDate end = DateUtil.parseStringToLocalDate(weekStartEnd[1], Constants.MMddyyyy) ;
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
		List<String> monthStrings = DateUtil.getDatesStringGroupByMonth(requestedDates);
		for(String monthString : monthStrings){
			for(String authority: authorities){
				defaultAnalytics.add(new LoginAnalyticsVO(monthString, 0, authority));
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
		List<String> weekStrings = DateUtil.getDatesStringGroupByWeek(requestedDates);
		for(String weekString : weekStrings){
			for(String authority : authorities){
				defaultAnalytics.add(new LoginAnalyticsVO(weekString, 0, authority));
			}
		}		
		return defaultAnalytics;
	}
}
