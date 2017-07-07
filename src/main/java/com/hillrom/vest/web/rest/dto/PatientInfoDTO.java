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
	

	private String operation_type;
	private String device_type;
	private String tims_cust;
	private String serial_num;
	private LocalDate ship_dt;
	private String hub_id;
	private String bluetooth_id;
	private String garment_cd;
	private String garment_type;
	private String garment_size;
	private String garment_color;
	private String title;
	private String first_nm;
	private String middle_nm;
	private String last_nm;
	private String email;
	private String zip_cd;
	private LocalDate train_dt;
	private LocalDate dob;
	private String dx1;
	private String dx2;
	private String dx3;
	private String dx4;
	
	private String patient_id;
	private String old_serial_number;
	private String new_serial_number;
	private String old_patient_id;
	private String is_active;
	private String protocol_type_key = "Normal";
	private String created_by;
	private String primary_phone;
	private String mobile_phone;
	private String gender;
	private String lang_key;
	private String address;
	private String city;
	private String state;
	private String patient_user_id;



	public PatientInfoDTO() {
		super();
	}



	/**
	 * @return the operation_type
	 */
	public String getOperation_type() {
		return operation_type;
	}



	/**
	 * @param operation_type the operation_type to set
	 */
	public void setOperation_type(String operation_type) {
		this.operation_type = operation_type;
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
	 * @return the bluetooth_id
	 */
	public String getBluetooth_id() {
		return bluetooth_id;
	}



	/**
	 * @param bluetooth_id the bluetooth_id to set
	 */
	public void setBluetooth_id(String bluetooth_id) {
		this.bluetooth_id = bluetooth_id;
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}



	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
	 * @return the middle_nm
	 */
	public String getMiddle_nm() {
		return middle_nm;
	}



	/**
	 * @param middle_nm the middle_nm to set
	 */
	public void setMiddle_nm(String middle_nm) {
		this.middle_nm = middle_nm;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}



	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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



	/**
	 * @return the patient_id
	 */
	public String getPatient_id() {
		return patient_id;
	}



	/**
	 * @param patient_id the patient_id to set
	 */
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}



	/**
	 * @return the old_serial_number
	 */
	public String getOld_serial_number() {
		return old_serial_number;
	}



	/**
	 * @param old_serial_number the old_serial_number to set
	 */
	public void setOld_serial_number(String old_serial_number) {
		this.old_serial_number = old_serial_number;
	}



	/**
	 * @return the new_serial_number
	 */
	public String getNew_serial_number() {
		return new_serial_number;
	}



	/**
	 * @param new_serial_number the new_serial_number to set
	 */
	public void setNew_serial_number(String new_serial_number) {
		this.new_serial_number = new_serial_number;
	}



	/**
	 * @return the old_patient_id
	 */
	public String getOld_patient_id() {
		return old_patient_id;
	}



	/**
	 * @param old_patient_id the old_patient_id to set
	 */
	public void setOld_patient_id(String old_patient_id) {
		this.old_patient_id = old_patient_id;
	}



	/**
	 * @return the is_active
	 */
	public String getIs_active() {
		return is_active;
	}



	/**
	 * @param is_active the is_active to set
	 */
	public void setIs_active(String is_active) {
		this.is_active = is_active;
	}



	/**
	 * @return the protocol_type_key
	 */
	public String getProtocol_type_key() {
		return protocol_type_key;
	}



	/**
	 * @param protocol_type_key the protocol_type_key to set
	 */
	public void setProtocol_type_key(String protocol_type_key) {
		this.protocol_type_key = protocol_type_key;
	}



	/**
	 * @return the created_by
	 */
	public String getCreated_by() {
		return created_by;
	}



	/**
	 * @param created_by the created_by to set
	 */
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}



	/**
	 * @return the primary_phone
	 */
	public String getPrimary_phone() {
		return primary_phone;
	}



	/**
	 * @param primary_phone the primary_phone to set
	 */
	public void setPrimary_phone(String primary_phone) {
		this.primary_phone = primary_phone;
	}



	/**
	 * @return the mobile_phone
	 */
	public String getMobile_phone() {
		return mobile_phone;
	}



	/**
	 * @param mobile_phone the mobile_phone to set
	 */
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}



	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}



	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}



	/**
	 * @return the lang_key
	 */
	public String getLang_key() {
		return lang_key;
	}



	/**
	 * @param lang_key the lang_key to set
	 */
	public void setLang_key(String lang_key) {
		this.lang_key = lang_key;
	}



	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}



	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}



	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}



	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}



	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}



	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}



	/**
	 * @return the patient_user_id
	 */
	public String getPatient_user_id() {
		return patient_user_id;
	}



	/**
	 * @param patient_user_id the patient_user_id to set
	 */
	public void setPatient_user_id(String patient_user_id) {
		this.patient_user_id = patient_user_id;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((bluetooth_id == null) ? 0 : bluetooth_id.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((created_by == null) ? 0 : created_by.hashCode());
		result = prime * result + ((device_type == null) ? 0 : device_type.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((dx1 == null) ? 0 : dx1.hashCode());
		result = prime * result + ((dx2 == null) ? 0 : dx2.hashCode());
		result = prime * result + ((dx3 == null) ? 0 : dx3.hashCode());
		result = prime * result + ((dx4 == null) ? 0 : dx4.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((first_nm == null) ? 0 : first_nm.hashCode());
		result = prime * result + ((garment_cd == null) ? 0 : garment_cd.hashCode());
		result = prime * result + ((garment_color == null) ? 0 : garment_color.hashCode());
		result = prime * result + ((garment_size == null) ? 0 : garment_size.hashCode());
		result = prime * result + ((garment_type == null) ? 0 : garment_type.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((hub_id == null) ? 0 : hub_id.hashCode());
		result = prime * result + ((is_active == null) ? 0 : is_active.hashCode());
		result = prime * result + ((lang_key == null) ? 0 : lang_key.hashCode());
		result = prime * result + ((last_nm == null) ? 0 : last_nm.hashCode());
		result = prime * result + ((middle_nm == null) ? 0 : middle_nm.hashCode());
		result = prime * result + ((mobile_phone == null) ? 0 : mobile_phone.hashCode());
		result = prime * result + ((new_serial_number == null) ? 0 : new_serial_number.hashCode());
		result = prime * result + ((old_patient_id == null) ? 0 : old_patient_id.hashCode());
		result = prime * result + ((old_serial_number == null) ? 0 : old_serial_number.hashCode());
		result = prime * result + ((operation_type == null) ? 0 : operation_type.hashCode());
		result = prime * result + ((patient_id == null) ? 0 : patient_id.hashCode());
		result = prime * result + ((patient_user_id == null) ? 0 : patient_user_id.hashCode());
		result = prime * result + ((primary_phone == null) ? 0 : primary_phone.hashCode());
		result = prime * result + ((protocol_type_key == null) ? 0 : protocol_type_key.hashCode());
		result = prime * result + ((serial_num == null) ? 0 : serial_num.hashCode());
		result = prime * result + ((ship_dt == null) ? 0 : ship_dt.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((tims_cust == null) ? 0 : tims_cust.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (bluetooth_id == null) {
			if (other.bluetooth_id != null)
				return false;
		} else if (!bluetooth_id.equals(other.bluetooth_id))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (created_by == null) {
			if (other.created_by != null)
				return false;
		} else if (!created_by.equals(other.created_by))
			return false;
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
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
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (hub_id == null) {
			if (other.hub_id != null)
				return false;
		} else if (!hub_id.equals(other.hub_id))
			return false;
		if (is_active == null) {
			if (other.is_active != null)
				return false;
		} else if (!is_active.equals(other.is_active))
			return false;
		if (lang_key == null) {
			if (other.lang_key != null)
				return false;
		} else if (!lang_key.equals(other.lang_key))
			return false;
		if (last_nm == null) {
			if (other.last_nm != null)
				return false;
		} else if (!last_nm.equals(other.last_nm))
			return false;
		if (middle_nm == null) {
			if (other.middle_nm != null)
				return false;
		} else if (!middle_nm.equals(other.middle_nm))
			return false;
		if (mobile_phone == null) {
			if (other.mobile_phone != null)
				return false;
		} else if (!mobile_phone.equals(other.mobile_phone))
			return false;
		if (new_serial_number == null) {
			if (other.new_serial_number != null)
				return false;
		} else if (!new_serial_number.equals(other.new_serial_number))
			return false;
		if (old_patient_id == null) {
			if (other.old_patient_id != null)
				return false;
		} else if (!old_patient_id.equals(other.old_patient_id))
			return false;
		if (old_serial_number == null) {
			if (other.old_serial_number != null)
				return false;
		} else if (!old_serial_number.equals(other.old_serial_number))
			return false;
		if (operation_type == null) {
			if (other.operation_type != null)
				return false;
		} else if (!operation_type.equals(other.operation_type))
			return false;
		if (patient_id == null) {
			if (other.patient_id != null)
				return false;
		} else if (!patient_id.equals(other.patient_id))
			return false;
		if (patient_user_id == null) {
			if (other.patient_user_id != null)
				return false;
		} else if (!patient_user_id.equals(other.patient_user_id))
			return false;
		if (primary_phone == null) {
			if (other.primary_phone != null)
				return false;
		} else if (!primary_phone.equals(other.primary_phone))
			return false;
		if (protocol_type_key == null) {
			if (other.protocol_type_key != null)
				return false;
		} else if (!protocol_type_key.equals(other.protocol_type_key))
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
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (tims_cust == null) {
			if (other.tims_cust != null)
				return false;
		} else if (!tims_cust.equals(other.tims_cust))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
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
		return "PatientInfoDTO [operation_type=" + operation_type + ", device_type=" + device_type + ", tims_cust="
				+ tims_cust + ", serial_num=" + serial_num + ", ship_dt=" + ship_dt + ", hub_id=" + hub_id
				+ ", bluetooth_id=" + bluetooth_id + ", garment_cd=" + garment_cd + ", garment_type=" + garment_type
				+ ", garment_size=" + garment_size + ", garment_color=" + garment_color + ", title=" + title
				+ ", first_nm=" + first_nm + ", middle_nm=" + middle_nm + ", last_nm=" + last_nm + ", email=" + email
				+ ", zip_cd=" + zip_cd + ", train_dt=" + train_dt + ", dob=" + dob + ", dx1=" + dx1 + ", dx2=" + dx2
				+ ", dx3=" + dx3 + ", dx4=" + dx4 + ", patient_id=" + patient_id + ", old_serial_number="
				+ old_serial_number + ", new_serial_number=" + new_serial_number + ", old_patient_id=" + old_patient_id
				+ ", is_active=" + is_active + ", protocol_type_key=" + protocol_type_key + ", created_by=" + created_by
				+ ", primary_phone=" + primary_phone + ", mobile_phone=" + mobile_phone + ", gender=" + gender
				+ ", lang_key=" + lang_key + ", address=" + address + ", city=" + city + ", state=" + state
				+ ", patient_user_id=" + patient_user_id + "]";
	}




	
}
