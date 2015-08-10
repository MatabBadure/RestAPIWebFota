package com.hillrom.vest.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name="VEST_DEVICE_BAD_DATA")
public class VestDeviceBadData {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name="received_at")
	private DateTime receivedAt = DateTime.now();
	
	@Column(name="request_data")
	private String requestData;
	
	public VestDeviceBadData(String requestData) {
		super();
		this.requestData = requestData;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public DateTime getReceivedAt() {
		return receivedAt;
	}
	public void setReceivedAt(DateTime receivedAt) {
		this.receivedAt = receivedAt;
	}
	public String getRequestData() {
		return requestData;
	}
	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VestDeviceBadData other = (VestDeviceBadData) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "BadVestDeviceData [id=" + id + ", receivedAt=" + receivedAt
				+ ", requestData=" + requestData + "]";
	}

	
}
