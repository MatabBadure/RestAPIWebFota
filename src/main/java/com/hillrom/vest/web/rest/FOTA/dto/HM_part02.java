package com.hillrom.vest.web.rest.FOTA.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class HM_part02 {

	private Map<Integer, String> fileChunks = new LinkedHashMap<>();

	private static HM_part02 instance;

	private HM_part02() {
	} // avoid instantiation.

	public static HM_part02 getInstance() {
		if (instance == null) {
			instance = new HM_part02();
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
