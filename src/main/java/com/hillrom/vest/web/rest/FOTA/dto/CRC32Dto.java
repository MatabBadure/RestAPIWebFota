package com.hillrom.vest.web.rest.FOTA.dto;

public class CRC32Dto {
	
	private String filePath;
	private String partNumber;
	private String region1StartAddress;
	private String region1EndAddress;
	private String region1CRCLocation;
	private String region2StartAddress;
	private String region2EndAddress;
	private String region2CRCLocation;
	
	
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
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
