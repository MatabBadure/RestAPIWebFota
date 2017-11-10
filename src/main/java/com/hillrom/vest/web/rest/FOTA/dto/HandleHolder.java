package com.hillrom.vest.web.rest.FOTA.dto;

import org.joda.time.DateTime;

public class HandleHolder {
	private String currentChunk;
	private String previousChunkTransStatus;
	private String partNo;
	private DateTime downloadStartTime;
	private Long fotaInfoId;
	private String deviceSerialNumber;
	private String connectionType;
	private String softwareVersion;
	private String deviceSoftwareVersion;
	private DateTime deviceSoftwareDateTime;
	private String updatedSoftVersion;
	private DateTime checkupdateDateTime;
	private DateTime downloadStartDateTime;
	private DateTime downloadEndDateTime;
	int chunkSize = 0;
	private String handleId;
	
	
	public String getHandleId() {
		return handleId;
	}
	public void setHandleId(String handleId) {
		this.handleId = handleId;
	}
	public String getUpdatedSoftVersion() {
		return updatedSoftVersion;
	}
	public void setUpdatedSoftVersion(String updatedSoftVersion) {
		this.updatedSoftVersion = updatedSoftVersion;
	}
	public DateTime getCheckupdateDateTime() {
		return checkupdateDateTime;
	}
	public void setCheckupdateDateTime(DateTime checkupdateDateTime) {
		this.checkupdateDateTime = checkupdateDateTime;
	}
	public DateTime getDownloadStartDateTime() {
		return downloadStartDateTime;
	}
	public void setDownloadStartDateTime(DateTime downloadStartDateTime) {
		this.downloadStartDateTime = downloadStartDateTime;
	}
	public DateTime getDownloadEndDateTime() {
		return downloadEndDateTime;
	}
	public void setDownloadEndDateTime(DateTime downloadEndDateTime) {
		this.downloadEndDateTime = downloadEndDateTime;
	}
	public DateTime getDeviceSoftwareDateTime() {
		return deviceSoftwareDateTime;
	}
	public void setDeviceSoftwareDateTime(DateTime deviceSoftwareDateTime) {
		this.deviceSoftwareDateTime = deviceSoftwareDateTime;
	}
	public String getDeviceSoftwareVersion() {
		return deviceSoftwareVersion;
	}
	public void setDeviceSoftwareVersion(String deviceSoftwareVersion) {
		this.deviceSoftwareVersion = deviceSoftwareVersion;
	}
	public String getSoftwareVersion() {
		return softwareVersion;
	}
	public void setSoftwareVersion(String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}
	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}
	public Long getFotaInfoId() {
		return fotaInfoId;
	}
	public void setFotaInfoId(Long fotaInfoId) {
		this.fotaInfoId = fotaInfoId;
	}
	
	public DateTime getDownloadStartTime() {
		return downloadStartTime;
	}
	public void setDownloadStartTime(DateTime downloadStartTime) {
		this.downloadStartTime = downloadStartTime;
	}
	public int getChunkSize() {
		return chunkSize;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	public String getCurrentChunk() {
		return currentChunk;
	}
	public void setCurrentChunk(String currentChunk) {
		this.currentChunk = currentChunk;
	}
	public String getPreviousChunkTransStatus() {
		return previousChunkTransStatus;
	}
	public void setPreviousChunkTransStatus(String previousChunkTransStatus) {
		this.previousChunkTransStatus = previousChunkTransStatus;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	
	/*public int hashCode() {
        return partNo.hashCode();
    }
*/
   /* public boolean equals(HandleHolder hh) {
        if (hh == null)
            return false;
        else if (hh.partNo.equalsIgnoreCase(this.partNo))
            return true;
        else
            return false;
    }
	*/
	
/*	public boolean equals(Object o) {
	    if (o == null)
	        return false;
	    if (!(o instanceof HandleHolder))
	        return false;

	    HandleHolder hh = (HandleHolder) o;
	    if (hh.partNo.equalsIgnoreCase(this.partNo))
	        return true;
	    else
	        return false;
	}*/
}
