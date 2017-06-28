package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

import net.minidev.json.JSONObject;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.domain.Tims;
import com.hillrom.vest.web.rest.dto.BenchMarkFilter;
import com.hillrom.vest.web.rest.dto.BenchmarkResultVO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

/**
 * Spring Data JPA repository for the Tims Patient & Patient User entity.
 */
@Repository
public class TimsUserRepository{
	
	@Inject
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public JSONObject managePatientUser(String operationTypeIndicator,
										String inhillRomId,
										String inPatientHubId,
										String inPatientBluetoothId,
										String inPatientDeviceSerialNumber,
										String inPatientTitle,
										String inPatientFirstName,
										String inPatientMiddleName,
										String inPatientLastName,
										String inPatientdob,
										String inPatientEmail,
										String inPatientZipCode,
										String inPatientPrimaryPhone,
										String inPatientMobilePhone,
										String inPatientGender,
										String inPatientlangKey,
										String inPatientAddress,
										String inPatientCity,
										String inPatientState,
										String inPatientTrainingDate,
										String inPatientPrimaryDiagnosis,
										String inPatientgarmentType,
										String inPatientGarmentSize,
										String inPatientGarmentColor) {

		
			JSONObject returnValues = new JSONObject();
			try{
		
				
				StoredProcedureQuery proc = entityManager.createNamedStoredProcedureQuery("manage_patient_user");
		
		
				proc.setParameter("operation_type_indicator", operationTypeIndicator);
				proc.setParameter("hr_id", (inhillRomId == null ? "" : inhillRomId));
				proc.setParameter("pat_hub_id", (inPatientHubId == null ? "" : inPatientHubId));
				proc.setParameter("pat_bluetooth_id", (inPatientBluetoothId == null ? "" : inPatientBluetoothId));
				proc.setParameter("pat_device_serial_number", (inPatientDeviceSerialNumber == null ? "" : inPatientDeviceSerialNumber.toString()));
				proc.setParameter("pat_title", (inPatientTitle == null ? "" : inPatientTitle));
				proc.setParameter("pat_first_name", (inPatientFirstName == null ? "" : inPatientFirstName));
				proc.setParameter("pat_middle_name", (inPatientMiddleName == null ? "" : inPatientMiddleName));
				proc.setParameter("pat_last_name", (inPatientLastName == null ? "" : inPatientLastName.toString()));
				proc.setParameter("pat_dob", (inPatientdob == null ? "" : inPatientdob.toString()));
				proc.setParameter("pat_email", (inPatientEmail == null ? "" : inPatientEmail.toString()));
				proc.setParameter("pat_zipcode", (inPatientZipCode == null ? "" : inPatientZipCode.toString()));
				proc.setParameter("pat_primary_phone", (inPatientPrimaryPhone == null ? "" : inPatientPrimaryPhone.toString()));
				proc.setParameter("pat_mobile_phone", (inPatientMobilePhone == null ? "" : inPatientMobilePhone.toString()));
				proc.setParameter("pat_gender", (inPatientGender == null ? "" : inPatientGender.toString()));
				proc.setParameter("pat_lang_key", (inPatientlangKey == null ? "" : inPatientlangKey.toString()));
				proc.setParameter("pat_address", (inPatientAddress == null ? "" : inPatientAddress.toString()));
				proc.setParameter("pat_city", (inPatientCity == null ? "" : inPatientCity.toString()));
				proc.setParameter("pat_state", (inPatientState == null ? "" : inPatientState.toString()));
				proc.setParameter("pat_training_date",  (inPatientTrainingDate == null ? "" : inPatientTrainingDate.toString()));
				proc.setParameter("pat_primary_diagnosis", (inPatientPrimaryDiagnosis == null ? "" : inPatientPrimaryDiagnosis.toString()));
				proc.setParameter("pat_garment_type", (inPatientgarmentType == null ? "" : inPatientgarmentType.toString()));
				proc.setParameter("pat_garment_size", (inPatientGarmentSize == null ? "" : inPatientGarmentSize.toString()));
				proc.setParameter("pat_garment_color", (inPatientGarmentColor == null ? "" : inPatientGarmentColor.toString()));
				proc.execute();
				String outPatientId = proc.getOutputParameterValue("return_patient_id").toString();
				String outPatientUser = proc.getOutputParameterValue("return_user_id").toString();


				returnValues.put("return_patient_id", outPatientId);
				returnValues.put("return_user_id", outPatientUser);
			}
			catch(Exception ex){
				ex.printStackTrace();
				return returnValues;
			}
				
				return returnValues;


	}

   
    
}

