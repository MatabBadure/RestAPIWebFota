package com.hillrom.vest.web.rest.FOTA.dto;

public class ApproverCRCDto {
	private long fotaId;
	private String region1CRC;
	private String region2CRC;
	private String publishedUser;
	private boolean isValideCRC32;
	
	
	
	public boolean getIsValideCRC32() {
		return isValideCRC32;
	}
	public void setIsValideCRC32(boolean isValideCRC32) {
		this.isValideCRC32 = isValideCRC32;
	}
	public String getPublishedUser() {
		return publishedUser;
	}
	public void setPublishedUser(String publishedUser) {
		this.publishedUser = publishedUser;
	}
	public long getFotaId() {
		return fotaId;
	}
	public void setFotaId(long fotaId) {
		this.fotaId = fotaId;
	}
	public String getRegion1CRC() {
		return region1CRC;
	}
	public void setRegion1CRC(String region1crc) {
		region1CRC = region1crc;
	}
	public String getRegion2CRC() {
		return region2CRC;
	}
	public void setRegion2CRC(String region2crc) {
		region2CRC = region2crc;
	}
	
	
}
