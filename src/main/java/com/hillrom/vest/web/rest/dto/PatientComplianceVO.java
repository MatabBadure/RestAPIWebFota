package com.hillrom.vest.web.rest.dto;

import java.io.Serializable;

import com.hillrom.vest.domain.PatientCompliance;
import com.hillrom.vest.domain.UserExtension;

public class PatientComplianceVO implements Serializable {

	private PatientCompliance patientComp;

	private UserExtension hcp;

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
}
