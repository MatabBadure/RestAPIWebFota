package com.hillrom.vest.service;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.web.rest.dto.Filter;
import com.hillrom.vest.web.rest.dto.Graph;

public interface GraphService {

	public Graph populateGraphDataForDay(Object data,Filter filter);
	public Graph populateGraphDataForWeek(Object data,Filter filter);
	public Graph populateGraphDataForMonth(Object data,Filter filter);
	public Graph populateGraphDataForYear(Object data,Filter filter);
	public Graph populateGraphDataForCustomDateRange(Object data,Filter filter);
	public Graph populateGraphData(Object data,Filter filter) throws HillromException;
}
