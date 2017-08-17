package com.hillrom.vest.web.rest.FOTA.dto;

public class FOTAInfoDto {
	private String modelId;
	private String boardId;
	private String bedId;
	private String bootCompVer;
	private String filePattern;
	private String mCUSize;
	private String softVersion;
	private String releaseNumber;
	private String releaseDate;
	private String devicePartNumber;
	private String checksum;
	private String filePath;
	private String uploadUser;
	private String effectiveDate;
	private boolean oldSoftVerFlag;
	/*private String filePath;
	private String uploadUser;
	private String uploadDatetime;
	private String effectivedatetime;*/
	
	public String getModelId() {
		return modelId;
	}
	public boolean getOldSoftVerFlag() {
		return oldSoftVerFlag;
	}
	public void setOldSoftVerFlag(boolean oldSoftVerFlag) {
		this.oldSoftVerFlag = oldSoftVerFlag;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public String getBoardId() {
		return boardId;
	}
	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}
	public String getBedId() {
		return bedId;
	}
	public void setBedId(String bedId) {
		this.bedId = bedId;
	}
	public String getBootCompVer() {
		return bootCompVer;
	}
	public void setBootCompVer(String bootCompVer) {
		this.bootCompVer = bootCompVer;
	}
	public String getFilePattern() {
		return filePattern;
	}
	public void setFilePattern(String filePattern) {
		this.filePattern = filePattern;
	}
	/*public String getMCUSize() {
		return MCUSize;
	}
	public void setMCUSize(String mCUSize) {
		MCUSize = mCUSize;
	}*/
	
	public String getSoftVersion() {
		return softVersion;
	}
	public String getmCUSize() {
		return mCUSize;
	}
	public void setmCUSize(String mCUSize) {
		this.mCUSize = mCUSize;
	}
	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}
	public String getReleaseNumber() {
		return releaseNumber;
	}
	public void setReleaseNumber(String releaseNumber) {
		this.releaseNumber = releaseNumber;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getDevicePartNumber() {
		return devicePartNumber;
	}
	public void setDevicePartNumber(String devicePartNumber) {
		this.devicePartNumber = devicePartNumber;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getUploadUser() {
		return uploadUser;
	}
	public void setUploadUser(String uploadUser) {
		this.uploadUser = uploadUser;
	}
	/*
	public String getUploadDatetime() {
		return uploadDatetime;
	}
	public void setUploadDatetime(String uploadDatetime) {
		this.uploadDatetime = uploadDatetime;
	}
	public String getEffectivedatetime() {
		return effectivedatetime;
	}
	public void setEffectivedatetime(String effectivedatetime) {
		this.effectivedatetime = effectivedatetime;
	}*/
	
}
