package com.hillrom.monarch.web.rest.util;

import com.hillrom.monarch.web.rest.dto.ProtocolDataMonarchVO;
import com.hillrom.vest.domain.PatientProtocolDataMonarch;
import com.hillrom.vest.domain.ProtocolConstants;
import com.hillrom.vest.domain.ProtocolConstantsMonarch;
import com.hillrom.vest.web.rest.dto.ProtocolDataVO;

public class ProtocolDataMonarchVOBuilder {
	public static ProtocolDataMonarchVO convertProtocolDataMonarchToVO(PatientProtocolDataMonarch protocolData){
		return new ProtocolDataMonarchVO(protocolData.getId(),protocolData.getType(),
				protocolData.getTreatmentsPerDay(), protocolData.getMinMinutesPerTreatment(),
				protocolData.getTreatmentLabel(), protocolData.getMinFrequency(), protocolData.getMaxFrequency(), protocolData.getMinIntensity(),
				protocolData.getMaxIntensity());
	}
	
	public static ProtocolDataMonarchVO convertProtocolConstantsMonarchToVO(ProtocolConstantsMonarch protocolData){
		return new ProtocolDataMonarchVO("1","Default",
				protocolData.getTreatmentsPerDay(), protocolData.getMinMinutesPerTreatment(),
				null,protocolData.getMinFrequency(), protocolData.getMaxFrequency(), protocolData.getMinPressure(),
				protocolData.getMaxPressure());
	} 
}
