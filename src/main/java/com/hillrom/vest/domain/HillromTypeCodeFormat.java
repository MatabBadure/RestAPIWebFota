package com.hillrom.vest.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "HILLROM_TYPE_CODE_VALUES")

public class HillromTypeCodeFormat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4691535693847111349L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "type")
	private String type;

	@Column(name = "type_code")
	private String type_code;

	@Column(name = "type_code_value")
	private String type_code_value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType_code() {
		return type_code;
	}

	public void setType_code(String type_code) {
		this.type_code = type_code;
	}

	public String getType_code_value() {
		return type_code_value;
	}

	public void setType_code_value(String type_code_value) {
		this.type_code_value = type_code_value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		HillromTypeCodeFormat typeCodeFormat = (HillromTypeCodeFormat) o;

		if (!Objects.equals(id, typeCodeFormat.id))
			return false;

		return true;
	}
	
	 @Override
	    public int hashCode() {
	        return Objects.hashCode(id);
	    }
}
