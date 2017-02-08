package com.hillrom.vest.web.rest.dto.monarch;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

public class ProtocolMonarchDTO {
	
	@Size(max = 20)
	private String type;
	
	@Size(max = 50)
	private int treatmentsPerDay;
	
	private List<ProtocolEntryMonarchDTO> protocolEntries = new ArrayList<>();
	
	public ProtocolMonarchDTO() {
		super();
	}

	public ProtocolMonarchDTO(String type, int treatmentsPerDay,
			List<ProtocolEntryMonarchDTO> protocolEntries) {
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

	public List<ProtocolEntryMonarchDTO> getProtocolEntries() {
		return protocolEntries;
	}

	public void setProtocolEntries(List<ProtocolEntryMonarchDTO> protocolEntries) {
		this.protocolEntries = protocolEntries;
	}

}
