package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.LocalDate;
import org.joda.time.DateTime;

import javax.validation.constraints.Size;

public class PatientInfoDTO {
	
	private String id;
	
	private String hillrom_id;
	private String hub_id;
	private String serial_number;
	private String bluetooth_id;
	private String title;
	private String first_name;
	private String middle_name;
	private String last_name;
	private DateTime dob;
	private String email;
	private String zipcode;
	private Boolean web_login_created;
	private String primary_phone;
	private String mobile_phone;
	private String gender;
	private String lang_key;
	private Boolean expired;
	private DateTime expired_date;
	private String address;
	private String city;
	private String state;
	private DateTime device_assoc_date;
	private DateTime training_date;
	private String primary_diagnosis;
	private String garment_type;
	private String garment_size;
	private String garment_color;


	public PatientInfoDTO() {
		super();
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the hillrom_id
	 */
	public String getHillrom_id() {
		return hillrom_id;
	}


	/**
	 * @param hillrom_id the hillrom_id to set
	 */
	public void setHillrom_id(String hillrom_id) {
		this.hillrom_id = hillrom_id;
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
	 * @return the serial_number
	 */
	public String getSerial_number() {
		return serial_number;
	}


	/**
	 * @param serial_number the serial_number to set
	 */
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
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
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}


	/**
	 * @param first_name the first_name to set
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}


	/**
	 * @return the middle_name
	 */
	public String getMiddle_name() {
		return middle_name;
	}


	/**
	 * @param middle_name the middle_name to set
	 */
	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}


	/**
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}


	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}


	/**
	 * @return the dob
	 */
	public DateTime getDob() {
		return dob;
	}


	/**
	 * @param dob the dob to set
	 */
	public void setDob(DateTime dob) {
		this.dob = dob;
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
	 * @return the zipcode
	 */
	public String getZipcode() {
		return zipcode;
	}


	/**
	 * @param zipcode the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}


	/**
	 * @return the web_login_created
	 */
	public Boolean getWeb_login_created() {
		return web_login_created;
	}


	/**
	 * @param web_login_created the web_login_created to set
	 */
	public void setWeb_login_created(Boolean web_login_created) {
		this.web_login_created = web_login_created;
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
	 * @return the expired
	 */
	public Boolean getExpired() {
		return expired;
	}


	/**
	 * @param expired the expired to set
	 */
	public void setExpired(Boolean expired) {
		this.expired = expired;
	}


	/**
	 * @return the expired_date
	 */
	public DateTime getExpired_date() {
		return expired_date;
	}


	/**
	 * @param expired_date the expired_date to set
	 */
	public void setExpired_date(DateTime expired_date) {
		this.expired_date = expired_date;
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
	 * @return the device_assoc_date
	 */
	public DateTime getDevice_assoc_date() {
		return device_assoc_date;
	}


	/**
	 * @param device_assoc_date the device_assoc_date to set
	 */
	public void setDevice_assoc_date(DateTime device_assoc_date) {
		this.device_assoc_date = device_assoc_date;
	}


	/**
	 * @return the training_date
	 */
	public DateTime getTraining_date() {
		return training_date;
	}


	/**
	 * @param training_date the training_date to set
	 */
	public void setTraining_date(DateTime training_date) {
		this.training_date = training_date;
	}


	/**
	 * @return the primary_diagnosis
	 */
	public String getPrimary_diagnosis() {
		return primary_diagnosis;
	}


	/**
	 * @param primary_diagnosis the primary_diagnosis to set
	 */
	public void setPrimary_diagnosis(String primary_diagnosis) {
		this.primary_diagnosis = primary_diagnosis;
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
		result = prime * result + ((device_assoc_date == null) ? 0 : device_assoc_date.hashCode());
		result = prime * result + ((dob == null) ? 0 : dob.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((expired == null) ? 0 : expired.hashCode());
		result = prime * result + ((expired_date == null) ? 0 : expired_date.hashCode());
		result = prime * result + ((first_name == null) ? 0 : first_name.hashCode());
		result = prime * result + ((garment_color == null) ? 0 : garment_color.hashCode());
		result = prime * result + ((garment_size == null) ? 0 : garment_size.hashCode());
		result = prime * result + ((garment_type == null) ? 0 : garment_type.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((hillrom_id == null) ? 0 : hillrom_id.hashCode());
		result = prime * result + ((hub_id == null) ? 0 : hub_id.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lang_key == null) ? 0 : lang_key.hashCode());
		result = prime * result + ((last_name == null) ? 0 : last_name.hashCode());
		result = prime * result + ((middle_name == null) ? 0 : middle_name.hashCode());
		result = prime * result + ((mobile_phone == null) ? 0 : mobile_phone.hashCode());
		result = prime * result + ((primary_diagnosis == null) ? 0 : primary_diagnosis.hashCode());
		result = prime * result + ((primary_phone == null) ? 0 : primary_phone.hashCode());
		result = prime * result + ((serial_number == null) ? 0 : serial_number.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((training_date == null) ? 0 : training_date.hashCode());
		result = prime * result + ((web_login_created == null) ? 0 : web_login_created.hashCode());
		result = prime * result + ((zipcode == null) ? 0 : zipcode.hashCode());
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
		if (device_assoc_date == null) {
			if (other.device_assoc_date != null)
				return false;
		} else if (!device_assoc_date.equals(other.device_assoc_date))
			return false;
		if (dob == null) {
			if (other.dob != null)
				return false;
		} else if (!dob.equals(other.dob))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (expired == null) {
			if (other.expired != null)
				return false;
		} else if (!expired.equals(other.expired))
			return false;
		if (expired_date == null) {
			if (other.expired_date != null)
				return false;
		} else if (!expired_date.equals(other.expired_date))
			return false;
		if (first_name == null) {
			if (other.first_name != null)
				return false;
		} else if (!first_name.equals(other.first_name))
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
		if (hillrom_id == null) {
			if (other.hillrom_id != null)
				return false;
		} else if (!hillrom_id.equals(other.hillrom_id))
			return false;
		if (hub_id == null) {
			if (other.hub_id != null)
				return false;
		} else if (!hub_id.equals(other.hub_id))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lang_key == null) {
			if (other.lang_key != null)
				return false;
		} else if (!lang_key.equals(other.lang_key))
			return false;
		if (last_name == null) {
			if (other.last_name != null)
				return false;
		} else if (!last_name.equals(other.last_name))
			return false;
		if (middle_name == null) {
			if (other.middle_name != null)
				return false;
		} else if (!middle_name.equals(other.middle_name))
			return false;
		if (mobile_phone == null) {
			if (other.mobile_phone != null)
				return false;
		} else if (!mobile_phone.equals(other.mobile_phone))
			return false;
		if (primary_diagnosis == null) {
			if (other.primary_diagnosis != null)
				return false;
		} else if (!primary_diagnosis.equals(other.primary_diagnosis))
			return false;
		if (primary_phone == null) {
			if (other.primary_phone != null)
				return false;
		} else if (!primary_phone.equals(other.primary_phone))
			return false;
		if (serial_number == null) {
			if (other.serial_number != null)
				return false;
		} else if (!serial_number.equals(other.serial_number))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (training_date == null) {
			if (other.training_date != null)
				return false;
		} else if (!training_date.equals(other.training_date))
			return false;
		if (web_login_created == null) {
			if (other.web_login_created != null)
				return false;
		} else if (!web_login_created.equals(other.web_login_created))
			return false;
		if (zipcode == null) {
			if (other.zipcode != null)
				return false;
		} else if (!zipcode.equals(other.zipcode))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PatientInfoDTO [id=" + id + ", hillrom_id=" + hillrom_id + ", hub_id=" + hub_id + ", serial_number="
				+ serial_number + ", bluetooth_id=" + bluetooth_id + ", title=" + title + ", first_name=" + first_name
				+ ", middle_name=" + middle_name + ", last_name=" + last_name + ", dob=" + dob + ", email=" + email
				+ ", zipcode=" + zipcode + ", web_login_created=" + web_login_created + ", primary_phone="
				+ primary_phone + ", mobile_phone=" + mobile_phone + ", gender=" + gender + ", lang_key=" + lang_key
				+ ", expired=" + expired + ", expired_date=" + expired_date + ", address=" + address + ", city=" + city
				+ ", state=" + state + ", device_assoc_date=" + device_assoc_date + ", training_date=" + training_date
				+ ", primary_diagnosis=" + primary_diagnosis + ", garment_type=" + garment_type + ", garment_size="
				+ garment_size + ", garment_color=" + garment_color + "]";
	}

	


}
