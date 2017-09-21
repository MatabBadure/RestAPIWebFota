package com.hillrom.vest.web.rest.FOTA.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class HM_part03 {

	private Map<Integer, String> fileChunks = new LinkedHashMap<Integer, String>();

	private static HM_part03 instance;

	private HM_part03() {
	} // avoid instantiation.

	public static HM_part03 getInstance() {
		if (instance == null) {
			instance = new HM_part03();
			// Read the file here
		}
		return instance;
	}

	public Map<Integer, String> getFileChunks() {
		return fileChunks;
	}

	public void setFileChunks(Map<Integer, String> fileChunks) {
		this.fileChunks = fileChunks;
	}

}
