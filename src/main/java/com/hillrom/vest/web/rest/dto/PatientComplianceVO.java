package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.UserExtension;

public class PatientComplianceVO implements Serializable {

	private PatientCompliance patientComp;

	private UserExtension hcp;
	
	private String mrnId;
	
	public PatientComplianceVO() {
		super();
	}

	public PatientComplianceVO(PatientCompliance patientComp, UserExtension hcp) {
		super();
		this.patientComp = patientComp;
		this.hcp = hcp;
	}

	public PatientCompliance getPatientComp() {
		return patientComp;
	}

	public void setPatientComp(PatientCompliance patientComp) {
		this.patientComp = patientComp;
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
