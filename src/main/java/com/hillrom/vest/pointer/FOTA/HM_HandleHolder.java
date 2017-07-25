package com.hillrom.vest.pointer.FOTA;

import java.util.LinkedHashMap;
import java.util.Map;

public class HM_HandleHolder {
	private Map<String, Map<Integer, String>> handles = new LinkedHashMap<>();
	
	private Map<String, Map<String,Integer>> sendChunkCounterHandle = new LinkedHashMap<>();

	private static HM_HandleHolder instance;

	private HM_HandleHolder() {
	}

	public static HM_HandleHolder getInstance() {
		if (instance == null) {
			instance = new HM_HandleHolder();

		}
		return instance;
	}

	public Map<String, Map<Integer, String>> getHandles() {
		return handles;
	}

	public void setHandles(Map<String, Map<Integer, String>> handles) {
		this.handles = handles;
	}

	public Map<String, Map<String, Integer>> getSendChunkCounterHandle() {
		return sendChunkCounterHandle;
	}

	public void setSendChunkCounterHandle(
			Map<String, Map<String, Integer>> sendChunkCounterHandle) {
		this.sendChunkCounterHandle = sendChunkCounterHandle;
	}

	
	
}
