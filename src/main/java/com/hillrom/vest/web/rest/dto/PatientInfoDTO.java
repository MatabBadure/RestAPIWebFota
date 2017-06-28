package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import javax.validation.constraints.Size;

public class PatientInfoDTO {
	

	private String device_type;
	private String tims_cust;
	private String serial_num;
	private LocalDate ship_dt;
	private String hub_id;
	private String garment_cd;
	private String garment_type;
	private String garment_size;
	private String garment_color;
	private String first_nm;
	private String last_nm;
	private String zip_cd;
	private LocalDate train_dt;
	private LocalDate dob;
	private String dx1;
	private String dx2;
	private String dx3;
	private String dx4;



	public PatientInfoDTO() {
		super();
	}



	/**
	 * @return the device_type
	 */
	public String getDevice_type() {
		return device_type;
	}



	/**
	 * @param device_type the device_type to set
	 */
	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}



	/**
	 * @return the tims_cust
	 */
	public String getTims_cust() {
		return tims_cust;
	}



	/**
	 * @param tims_cust the tims_cust to set
	 */
	public void setTims_cust(String tims_cust) {
		this.tims_cust = tims_cust;
	}



	/**
	 * @return the serial_num
	 */
	public String getSerial_num() {
		return serial_num;
	}



	/**
	 * @param serial_num the serial_num to set
	 */
	public void setSerial_num(String serial_num) {
		this.serial_num = serial_num;
	}



	/**
	 * @return the ship_dt
	 */
	public LocalDate getShip_dt() {
		return ship_dt;
	}



	/**
	 * @param ship_dt the ship_dt to set
	 */
	public void setShip_dt(LocalDate ship_dt) {
		this.ship_dt = ship_dt;
	}



	/**
	 * @return the hub_id
	 */
	public String getHub_id() {
		return hub_id;
	}



	/**
	 * @param hub_id the hub_id to set
	 */
	public void setHub_id(String hub_id) {
		this.hub_id = hub_id;
	}



	/**
	 * @return the garment_cd
	 */
	public String getGarment_cd() {
		return garment_cd;
	}



	/**
	 * @param garment_cd the garment_cd to set
	 */
	public void setGarment_cd(String garment_cd) {
		this.garment_cd = garment_cd;
	}



	/**
	 * @return the garment_type
	 */
	public String getGarment_type() {
		return garment_type;
	}



	/**
	 * @param garment_type the garment_type to set
	 */
	public void setGarment_type(String garment_type) {
		this.garment_type = garment_type;
	}



	/**
	 * @return the garment_size
	 */
	public String getGarment_size() {
		return garment_size;
	}



	/**
	 * @param garment_size the garment_size to set
	 */
	public void setGarment_size(String garment_size) {
		this.garment_size = garment_size;
	}



	/**
	 * @return the garment_color
	 */
	public String getGarment_color() {
		return garment_color;
	}



	/**
	 * @param garment_color the garment_color to set
	 */
	public void setGarment_color(String garment_color) {
		this.garment_color = garment_color;
	}



	/**
	 * @return the first_nm
	 */
	public String getFirst_nm() {
		return first_nm;
	}



	/**
	 * @param first_nm the first_nm to set
	 */
	public void setFirst_nm(String first_nm) {
		this.first_nm = first_nm;
	}



	/**
	 * @return the last_nm
	 */
	public String getLast_nm() {
		return last_nm;
	}



	/**
	 * @param last_nm the last_nm to set
	 */
	public void setLast_nm(String last_nm) {
		this.last_nm = last_nm;
	}



	/**
	 * @return the zip_cd
	 */
	public String getZip_cd() {
		return zip_cd;
	}



	/**
	 * @param zip_cd the zip_cd to set
	 */
	public void setZip_cd(String zip_cd) {
		this.zip_cd = zip_cd;
	}



	/**
	 * @return the train_dt
	 */
	public LocalDate getTrain_dt() {
		return train_dt;
	}



	/**
	 * @param train_dt the train_dt to set
	 */
	public void setTrain_dt(LocalDate train_dt) {
		this.train_dt = train_dt;
	}



	/**
	 * @return the dob
	 */
	public LocalDate getDob() {
		return dob;
	}



	/**
	 * @param dob the dob to set
	 */
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}



	/**
	 * @return the dx1
	 */
	public String getDx1() {
		return dx1;
	}



	/**
	 * @param dx1 the dx1 to set
	 */
	public void setDx1(String dx1) {
		this.dx1 = dx1;
	}



	/**
	 * @return the dx2
	 */
	public String getDx2() {
		return dx2;
	}



	/**
	 * @param dx2 the dx2 to set
	 */
	public void setDx2(String dx2) {
		this.dx2 = dx2;
	}



	/**
	 * @return the dx3
	 */
	public String getDx3() {
		return dx3;
	}



	/**
	 * @param dx3 the dx3 to set
	 */
	public void setDx3(String dx3) {
		this.dx3 = dx3;
	}



	/**
	 * @return the dx4
	 */
	public String getDx4() {
		return dx4;
	}



	/**
	 * @param dx4 the dx4 to set
	 */
	public void setDx4(String dx4) {
		this.dx4 = dx4;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device_type == null) ? 0 : device_type.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((dx1 == null) ? 0 : dx1.hashCode());
		result = prime * result + ((dx2 == null) ? 0 : dx2.hashCode());
		result = prime * result + ((dx3 == null) ? 0 : dx3.hashCode());
		result = prime * result + ((dx4 == null) ? 0 : dx4.hashCode());
		result = prime * result + ((first_nm == null) ? 0 : first_nm.hashCode());
		result = prime * result + ((garment_cd == null) ? 0 : garment_cd.hashCode());
		result = prime * result + ((garment_color == null) ? 0 : garment_color.hashCode());
		result = prime * result + ((garment_size == null) ? 0 : garment_size.hashCode());
		result = prime * result + ((garment_type == null) ? 0 : garment_type.hashCode());
		result = prime * result + ((hub_id == null) ? 0 : hub_id.hashCode());
		result = prime * result + ((last_nm == null) ? 0 : last_nm.hashCode());
		result = prime * result + ((serial_num == null) ? 0 : serial_num.hashCode());
		result = prime * result + ((ship_dt == null) ? 0 : ship_dt.hashCode());
		result = prime * result + ((tims_cust == null) ? 0 : tims_cust.hashCode());
		result = prime * result + ((train_dt == null) ? 0 : train_dt.hashCode());
		result = prime * result + ((zip_cd == null) ? 0 : zip_cd.hashCode());
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
		PatientInfoDTO other = (PatientInfoDTO) obj;
		if (device_type == null) {
			if (other.device_type != null)
				return false;
		} else if (!device_type.equals(other.device_type))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (dx1 == null) {
			if (other.dx1 != null)
				return false;
		} else if (!dx1.equals(other.dx1))
			return false;
		if (dx2 == null) {
			if (other.dx2 != null)
				return false;
		} else if (!dx2.equals(other.dx2))
			return false;
		if (dx3 == null) {
			if (other.dx3 != null)
				return false;
		} else if (!dx3.equals(other.dx3))
			return false;
		if (dx4 == null) {
			if (other.dx4 != null)
				return false;
		} else if (!dx4.equals(other.dx4))
			return false;
		if (first_nm == null) {
			if (other.first_nm != null)
				return false;
		} else if (!first_nm.equals(other.first_nm))
			return false;
		if (garment_cd == null) {
			if (other.garment_cd != null)
				return false;
		} else if (!garment_cd.equals(other.garment_cd))
			return false;
		if (garment_color == null) {
			if (other.garment_color != null)
				return false;
		} else if (!garment_color.equals(other.garment_color))
			return false;
		if (garment_size == null) {
			if (other.garment_size != null)
				return false;
		} else if (!garment_size.equals(other.garment_size))
			return false;
		if (garment_type == null) {
			if (other.garment_type != null)
				return false;
		} else if (!garment_type.equals(other.garment_type))
			return false;
		if (hub_id == null) {
			if (other.hub_id != null)
				return false;
		} else if (!hub_id.equals(other.hub_id))
			return false;
		if (last_nm == null) {
			if (other.last_nm != null)
				return false;
		} else if (!last_nm.equals(other.last_nm))
			return false;
		if (serial_num == null) {
			if (other.serial_num != null)
				return false;
		} else if (!serial_num.equals(other.serial_num))
			return false;
		if (ship_dt == null) {
			if (other.ship_dt != null)
				return false;
		} else if (!ship_dt.equals(other.ship_dt))
			return false;
		if (tims_cust == null) {
			if (other.tims_cust != null)
				return false;
		} else if (!tims_cust.equals(other.tims_cust))
			return false;
		if (train_dt == null) {
			if (other.train_dt != null)
				return false;
		} else if (!train_dt.equals(other.train_dt))
			return false;
		if (zip_cd == null) {
			if (other.zip_cd != null)
				return false;
		} else if (!zip_cd.equals(other.zip_cd))
			return false;
		return true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatientInfoDTO [device_type=" + device_type + ", tims_cust=" + tims_cust + ", serial_num=" + serial_num
				+ ", ship_dt=" + ship_dt + ", hub_id=" + hub_id + ", garment_cd=" + garment_cd + ", garment_type="
				+ garment_type + ", garment_size=" + garment_size + ", garment_color=" + garment_color + ", first_nm="
				+ first_nm + ", last_nm=" + last_nm + ", zip_cd=" + zip_cd + ", train_dt=" + train_dt + ", dob=" + dob
				+ ", dx1=" + dx1 + ", dx2=" + dx2 + ", dx3=" + dx3 + ", dx4=" + dx4 + "]";
	}






	


}
