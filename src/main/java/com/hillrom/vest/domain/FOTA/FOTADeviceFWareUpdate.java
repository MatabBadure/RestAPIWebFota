package com.hillrom.vest.domain.FOTA;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

@Entity
@Audited
@Table(name = "FOTA_DEVICE_FWARE_UPDATE_LOG")
public class FOTADeviceFWareUpdate {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name = "fota_info_id")
	private Long fotaInfoId;
	
	@Column(name = "device_serial_number")
	private String  deviceSerialNumber;
	
    @Column(name="download_time")
	private String downloadTime;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="completed_date")
	private DateTime currentDate;
	
	@Column(name = "downloaded_status")
	private String  status;
	
	@Column(name = "connection_type")
	private String  connectionType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFotaInfoId() {
		return fotaInfoId;
	}

	public void setFotaInfoId(Long long1) {
		this.fotaInfoId = long1;
	}

	public String getDeviceSerialNumber() {
		return deviceSerialNumber;
	}

	public void setDeviceSerialNumber(String deviceSerialNumber) {
		this.deviceSerialNumber = deviceSerialNumber;
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public DateTime getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(DateTime currentDate) {
		this.currentDate = currentDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	
	

}
