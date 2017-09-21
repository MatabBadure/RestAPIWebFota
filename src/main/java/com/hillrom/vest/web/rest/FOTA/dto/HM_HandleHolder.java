package com.hillrom.vest.web.rest.FOTA.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class HM_HandleHolder {
	private Map<String, Integer> handles = new LinkedHashMap<>();
	
	//Passing handle with count and part number
	private Map<String, Map<String,String>> handleWithPartNumber = new LinkedHashMap<>();

	private static HM_HandleHolder instance;

	private HM_HandleHolder() {
	}

	public static HM_HandleHolder getInstance() {
		if (instance == null) {
			instance = new HM_HandleHolder();

		}
		return instance;
	}

	public Map<String, Integer> getHandles() {
		return handles;
	}

	public void setHandles(Map<String, Integer> handles) {
		this.handles = handles;
	}

	public Map<String, Map<String, String>> getHandleWithPartNumber() {
		return handleWithPartNumber;
	}

	public void setHandleWithPartNumber(
			Map<String, Map<String, String>> handleWithPartNumber) {
		this.handleWithPartNumber = handleWithPartNumber;
	}

	
	
	
}
