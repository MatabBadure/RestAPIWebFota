package com.hillrom.vest.web.rest.dto.monarch;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.PatientComplianceMonarch;
import com.hillrom.vest.domain.UserExtension;

public class PatientComplianceMonarchVO implements Serializable {

	private PatientComplianceMonarch patientCompMonarch;

	private UserExtension hcp;
	
	private String mrnId;
	
	public PatientComplianceMonarchVO() {
		super();
	}

	public PatientComplianceMonarchVO(PatientComplianceMonarch patientCompMonarch, UserExtension hcp) {
		super();
		this.patientCompMonarch = patientCompMonarch;
		this.hcp = hcp;
	}



	/**
	 * @return the patientCompMonarch
	 */
	public PatientComplianceMonarch getPatientCompMonarch() {
		return patientCompMonarch;
	}

	/**
	 * @param patientCompMonarch the patientCompMonarch to set
	 */
	public void setPatientCompMonarch(PatientComplianceMonarch patientCompMonarch) {
		this.patientCompMonarch = patientCompMonarch;
	}

	public UserExtension getHcp() {
		return hcp;
	}

	public void setHcp(UserExtension hcp) {
		this.hcp = hcp;
	}

	public String getMrnId() {
		return mrnId;
	}

	public void setMrnId(String mrnId) {
		this.mrnId = mrnId;
	}
}
