package com.hillrom.vest.service;

import com.hillrom.vest.config.Constants;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;

public abstract class AbstractGraphService implements GraphService{

	@Override
	public Graph populateGraphData(Object data, Filter filter) {
		if(Constants.DAY.equalsIgnoreCase(filter.getDuration())){
			return populateGraphDataForDay(data, filter);
		}else if(Constants.WEEK.equalsIgnoreCase(filter.getDuration())){
			return populateGraphDataForWeek(data, filter);
		}else if(Constants.MONTH.equalsIgnoreCase(filter.getDuration())){
			return populateGraphDataForMonth(data, filter);
		}else if(Constants.YEAR.equalsIgnoreCase(filter.getDuration())){
			return populateGraphDataForYear(data, filter);
		}else if(Constants.CUSTOM.equalsIgnoreCase(filter.getDuration())){
			return populateGraphDataForCustomDateRange(data, filter);
		}
		return null;
	}

}
