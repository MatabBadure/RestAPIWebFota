package com.hillrom.vest.web.rest.util;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.web.rest.dto.ProtocolDataVO;

public class ProtocolDataVOBuilder {
	public static ProtocolDataVO convertProtocolDataToVO(PatientProtocolData protocolData){
		return new ProtocolDataVO(protocolData.getId(),protocolData.getType(),
				protocolData.getTreatmentsPerDay(), protocolData.getMinMinutesPerTreatment(),
				protocolData.getTreatmentLabel(), protocolData.getMinFrequency(), protocolData.getMaxFrequency(), protocolData.getMinPressure(),
				protocolData.getMaxPressure());
	}
	
	public static ProtocolDataVO convertProtocolConstantsToVO(ProtocolConstants protocolData){
		return new ProtocolDataVO("1","Default",
				protocolData.getTreatmentsPerDay(), protocolData.getMinMinutesPerTreatment(),
				null,protocolData.getMinFrequency(), protocolData.getMaxFrequency(), protocolData.getMinPressure(),
				protocolData.getMaxPressure());
	} 
}
