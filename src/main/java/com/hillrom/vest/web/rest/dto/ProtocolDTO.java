package com.hillrom.vest.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

public class ProtocolDTO {
	
	@Size(max = 20)
	private String type;
	
	@Size(max = 50)
	private int treatmentsPerDay;
	
	private List<ProtocolEntryDTO> protocolEntries = new ArrayList<>();
	
	public ProtocolDTO() {
		super();
	}

	public ProtocolDTO(String type, int treatmentsPerDay,
			List<ProtocolEntryDTO> protocolEntries) {
		super();
		this.type = type;
		this.treatmentsPerDay = treatmentsPerDay;
		this.protocolEntries = protocolEntries;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTreatmentsPerDay() {
		return treatmentsPerDay;
	}

	public void setTreatmentsPerDay(int treatmentsPerDay) {
		this.treatmentsPerDay = treatmentsPerDay;
	}

	public List<ProtocolEntryDTO> getProtocolEntries() {
		return protocolEntries;
	}

	public void setProtocolEntries(List<ProtocolEntryDTO> protocolEntries) {
		this.protocolEntries = protocolEntries;
	}

}
