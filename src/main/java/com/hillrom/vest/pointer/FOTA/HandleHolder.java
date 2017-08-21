package com.hillrom.vest.pointer.FOTA;

import org.joda.time.DateTime;

public class HandleHolder {
	private String currentChunk;
	private String previousChunkTransStatus;
	private String partNo;
	private DateTime downloadStartTime;
	private Long fotaInfoId;
	private String deviceSerialNumber;
	private String connectionType;
	int chunkSize = 0;
	
	
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
	
	public int hashCode() {
        return partNo.hashCode();
    }

   /* public boolean equals(HandleHolder hh) {
        if (hh == null)
            return false;
        else if (hh.partNo.equalsIgnoreCase(this.partNo))
            return true;
        else
            return false;
    }
	*/
	
	public boolean equals(Object o) {
	    if (o == null)
	        return false;
	    if (!(o instanceof HandleHolder))
	        return false;

	    HandleHolder hh = (HandleHolder) o;
	    if (hh.partNo.equalsIgnoreCase(this.partNo))
	        return true;
	    else
	        return false;
	}
}
