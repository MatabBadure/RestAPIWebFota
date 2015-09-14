package com.hillrom.vest.web.rest.util;

import java.util.Objects;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.web.rest.dto.ClinicVO;

public class ClinicVOBuilder {

	private static ClinicVO createClinicVo(Clinic clinic){
		return new ClinicVO(clinic.getId(),clinic.getName(), clinic.getAddress(), clinic.getZipcode(), clinic.getCity(),
				clinic.getState(), clinic.getPhoneNumber(), clinic.getFaxNumber(), clinic.getClinicAdminId(), 
				clinic.isParent(), clinic.getHillromId(),clinic.isDeleted(),clinic.getCreatedAt());
	}
	
	public static ClinicVO build(Clinic clinic){
		ClinicVO clinicVo = null;
		if(Objects.nonNull(clinic)){
			clinicVo = createClinicVo(clinic);
			Clinic parentClinic = clinic.getParentClinic();
			if(Objects.nonNull(parentClinic))
					clinicVo.setParentClinic(createClinicVo(clinic.getParentClinic()));
			
		}
		return clinicVo;
	}

	public static ClinicVO buildWithChildClinics(Clinic clinic){
		ClinicVO clinicVO = build(clinic);
		clinic.getChildClinics().forEach(childClinic -> {
			clinicVO.getChildClinicVOs().add(build(childClinic));
		});
		return clinicVO;
	}
}
