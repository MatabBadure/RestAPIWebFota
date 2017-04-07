package com.hillrom.vest.domain;

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
 * Ping Pong Ping Data for Charger Device.
 */
@Entity
@Table(name = "PING_PONG_PING")
public class PingPongPing implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "created_time")
    private DateTime createdTime;
    
    @Column(name = "serial_number")
    private String serialNumber;
    
    @Column(name = "dev_wifi")
    private String devWifi;
    
    @Column(name = "dev_lte")
    private String devLte;
    
    @Column(name = "dev_bt")
    private String devBt;

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
	
	

	/**
	 * @return the devWifi
	 */
	public String getDevWifi() {
		return devWifi;
	}

	/**
	 * @param devWifi the devWifi to set
	 */
	public void setDevWifi(String devWifi) {
		this.devWifi = devWifi;
	}

	/**
	 * @return the devLte
	 */
	public String getDevLte() {
		return devLte;
	}

	/**
	 * @param devLte the devLte to set
	 */
	public void setDevLte(String devLte) {
		this.devLte = devLte;
	}


	public String getDevBt() {
		return devBt;
	}

	public void setDevBt(String devBt) {
		this.devBt = devBt;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((createdTime == null) ? 0 : createdTime.hashCode());
		result = prime * result + ((devLte == null) ? 0 : devLte.hashCode());
		result = prime * result + ((devWifi == null) ? 0 : devWifi.hashCode());
		result = prime * result + ((devBt == null) ? 0 : devBt.hashCode());
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
		PingPongPing other = (PingPongPing) obj;
		if (createdTime == null) {
			if (other.createdTime != null)
				return false;
		} else if (!createdTime.equals(other.createdTime))
			return false;
		if (devLte == null) {
			if (other.devLte != null)
				return false;
		} else if (!devLte.equals(other.devLte))
			return false;
		if (devWifi == null) {
			if (other.devWifi != null)
				return false;
		} else if (!devWifi.equals(other.devWifi))
			return false;
		if (devBt == null) {
			if (other.devBt != null)
				return false;
		} else if (!devBt.equals(other.devBt))
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
		return "PingPongPing [id=" + id + ", createdTime=" + createdTime + ", serialNumber=" + serialNumber
				+ ", devWifi=" + devWifi + ", devBt=" + devBt + ", devLte=" + devLte + "]";
	}



	
	

}
