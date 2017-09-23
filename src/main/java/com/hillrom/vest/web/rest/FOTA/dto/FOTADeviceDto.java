package com.hillrom.vest.web.rest.FOTA.dto;

import java.util.Comparator;

import org.joda.time.DateTime;

public class FOTADeviceDto {
	private Long id;
	
	private Long fotaInfoId;
	
	private String  deviceSerialNumber;
	
	private String  connectionType;
	
	private String deviceSoftVersion;
	
	private DateTime deviceSoftwareDateTime;
    
	private String updatedSoftVersion;
	
	private DateTime checkupdateDateTime;
	
	private DateTime downloadStartDateTime;
	
	private DateTime downloadEndDateTime;
	
	private String downloadTime;
	
	private String  downloadStatus;
	
	private long devicePartNumber;
	
	private String productType;
	
	
	public static Comparator<FOTADeviceDto> downLoadTimeAscComparator = new Comparator<FOTADeviceDto>() {

		public int compare(FOTADeviceDto s1, FOTADeviceDto s2) {
			String downloadTime1 = s1.getDownloadTime();
			String downloadTime2 = s2.getDownloadTime();

			// ascending order
			return downloadTime1.compareTo(downloadTime2);
		}
	};
	public static Comparator<FOTADeviceDto> downLoadTimeDescComparator = new Comparator<FOTADeviceDto>() {
		public int compare(FOTADeviceDto s1, FOTADeviceDto s2) {
			String downloadTime1 = s1.getDownloadTime();
			String downloadTime2 = s2.getDownloadTime();
			// descending order
			return downloadTime2.compareTo(downloadTime1);
		}
	};
		
	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFotaInfoId() {
		return fotaInfoId;
	}

	public void setFotaInfoId(Long fotaInfoId) {
		this.fotaInfoId = fotaInfoId;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}

	public String getDeviceSoftVersion() {
		return deviceSoftVersion;
	}

	public void setDeviceSoftVersion(String deviceSoftVersion) {
		this.deviceSoftVersion = deviceSoftVersion;
	}

	public DateTime getDeviceSoftwareDateTime() {
		return deviceSoftwareDateTime;
	}

	public void setDeviceSoftwareDateTime(DateTime deviceSoftwareDateTime) {
		this.deviceSoftwareDateTime = deviceSoftwareDateTime;
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

	public String getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(String downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public long getDevicePartNumber() {
		return devicePartNumber;
	}

	public void setDevicePartNumber(long devicePartNumber) {
		this.devicePartNumber = devicePartNumber;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	


}