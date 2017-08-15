package com.hillrom.optimus.domain;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * Optimus Device Data.
 */
@Entity
@Table(name = "OPTIMUS_DATA")
public class OptimusData implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "created_time")
    private DateTime createdTime;

    @Column(name = "device_data")
    private String deviceData;
    

    @Column(name = "current_fragment")
    private int fragCurrent;
    
    @Column(name = "total_fragments")
    private int fragTotal;
    
    @Column(name = "serial_number")
    private String serialNumber;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the createdTime
	 */
	public DateTime getCreatedTime() {
		return createdTime;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(DateTime createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the deviceData
	 */
	public String getDeviceData() {
		return deviceData;
	}

	/**
	 * @param deviceData the deviceData to set
	 */
	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}

	/**
	 * @return the fragCurrent
	 */
	public int getFragCurrent() {
		return fragCurrent;
	}

	/**
	 * @param fragCurrent the fragCurrent to set
	 */
	public void setFragCurrent(int fragCurrent) {
		this.fragCurrent = fragCurrent;
	}

	/**
	 * @return the fragTotal
	 */
	public int getFragTotal() {
		return fragTotal;
	}

	/**
	 * @param fragTotal the fragTotal to set
	 */
	public void setFragTotal(int fragTotal) {
		this.fragTotal = fragTotal;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdTime == null) ? 0 : createdTime.hashCode());
		result = prime * result + ((deviceData == null) ? 0 : deviceData.hashCode());
		result = prime * result + fragCurrent;
		result = prime * result + fragTotal;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptimusData other = (OptimusData) obj;
		if (createdTime == null) {
			if (other.createdTime != null)
				return false;
		} else if (!createdTime.equals(other.createdTime))
			return false;
		if (deviceData == null) {
			if (other.deviceData != null)
				return false;
		} else if (!deviceData.equals(other.deviceData))
			return false;
		if (fragCurrent != other.fragCurrent)
			return false;
		if (fragTotal != other.fragTotal)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OptimusData [id=" + id + ", createdTime=" + createdTime + ", deviceData=" + deviceData
				+ ", fragCurrent=" + fragCurrent + ", fragTotal=" + fragTotal + ", serialNumber=" + serialNumber + "]";
	}



    

	
	

}
