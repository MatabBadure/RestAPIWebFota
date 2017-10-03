package com.hillrom.vest.domain.FOTA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "FOTA_DEVICE_FWARE_UPDATE_LOG")
public class FOTADeviceFWareUpdate {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "fota_info_id")
	private Long fotaInfoId;
	
	@Size(max = 10)
	@Column(name="device_serial_number", length = 10)
	private String  deviceSerialNumber;
	
	@Column(name = "connection_type")
	private String  connectionType;
	
	@Size(max = 8)
	@Column(name="device_software_version", length = 8)
	private String deviceSoftVersion;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="device_software_date_time")
	private DateTime deviceSoftwareDateTime;
    
	@Size(max = 8)
    @Column(name="updated_software_version", length = 8)
	private String updatedSoftVersion;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="checkupdate_date_time")
	private DateTime checkupdateDateTime;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="download_start_date_time")
	private DateTime downloadStartDateTime;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="download_end_date_time")
	private DateTime downloadEndDateTime;
	
	@Column(name = "downloaded_status")
	private String  downloadStatus;

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
	
	
	
}
