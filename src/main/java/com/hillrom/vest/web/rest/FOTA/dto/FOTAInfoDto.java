package com.hillrom.vest.web.rest.FOTA.dto;


public class FOTAInfoDto {
	private String modelId;
	private String boardId;
	private String bedId;
	private String bootCompVer;
	private String fillPattern;
	private String mCUSize;
	private String softVersion;
	private String releaseNumber;
	private String releaseDate;
	private String devicePartNumber;
	//private String checksum;
	private String filePath;
	private String uploadUser;
	private String publishedUser;
	//added new attribute
	//private boolean softDeleteFlag;
	private String region1StartAddress;
	private String region1EndAddress;
	private String region1CRCLocation;
	private String region2StartAddress;
	private String region2EndAddress;
	private String region2CRCLocation;
	//Added new attribute
	private String productType;
	//oldRecord
	private boolean oldRecord;
	
	public boolean getOldRecord() {
		return oldRecord;
	}
	public void setOldRecord(boolean oldRecord) {
		this.oldRecord = oldRecord;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public String getPublishedUser() {
		return publishedUser;
	}
	public void setPublishedUser(String publishedUser) {
		this.publishedUser = publishedUser;
	}
	public String getModelId() {
		return modelId;
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
	public String getFillPattern() {
		return fillPattern;
	}
	public void setFillPattern(String fillPattern) {
		this.fillPattern = fillPattern;
	}
	public String getmCUSize() {
		return mCUSize;
	}
	public void setmCUSize(String mCUSize) {
		this.mCUSize = mCUSize;
	}
	public String getSoftVersion() {
		return softVersion;
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
	/*public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}*/
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
	/*public boolean getSoftDeleteFlag() {
		return softDeleteFlag;
	}
	public void setSoftDeleteFlag(boolean softDeleteFlag) {
		this.softDeleteFlag = softDeleteFlag;
	}*/
	public String getRegion1StartAddress() {
		return region1StartAddress;
	}
	public void setRegion1StartAddress(String region1StartAddress) {
		this.region1StartAddress = region1StartAddress;
	}
	public String getRegion1EndAddress() {
		return region1EndAddress;
	}
	public void setRegion1EndAddress(String region1EndAddress) {
		this.region1EndAddress = region1EndAddress;
	}
	public String getRegion1CRCLocation() {
		return region1CRCLocation;
	}
	public void setRegion1CRCLocation(String region1crcLocation) {
		region1CRCLocation = region1crcLocation;
	}
	public String getRegion2StartAddress() {
		return region2StartAddress;
	}
	public void setRegion2StartAddress(String region2StartAddress) {
		this.region2StartAddress = region2StartAddress;
	}
	public String getRegion2EndAddress() {
		return region2EndAddress;
	}
	public void setRegion2EndAddress(String region2EndAddress) {
		this.region2EndAddress = region2EndAddress;
	}
	public String getRegion2CRCLocation() {
		return region2CRCLocation;
	}
	public void setRegion2CRCLocation(String region2crcLocation) {
		region2CRCLocation = region2crcLocation;
	}
	
}
