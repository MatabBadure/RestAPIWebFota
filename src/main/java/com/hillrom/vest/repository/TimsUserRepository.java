package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
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
import org.springframework.transaction.annotation.Transactional;
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
	/* @Procedure(name = "manage_patient_user")
	@Transactional*/
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
		
				

				StoredProcedureQuery proc=  entityManager.createStoredProcedureQuery("manage_patient_user");
				
				proc.registerStoredProcedureParameter("operation_type_indicator",String.class, ParameterMode.IN);
				proc.registerStoredProcedureParameter("hr_id",String.class, ParameterMode.IN);
				proc.registerStoredProcedureParameter("pat_hub_id",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_bluetooth_id",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_device_serial_number",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_title",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_first_name",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_middle_name",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_last_name",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_dob",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_email",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_zipcode",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_primary_phone",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_mobile_phone",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_gender",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_lang_key",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_address",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_city",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_state",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_training_date",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_primary_diagnosis",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_garment_type",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_garment_size",String.class, ParameterMode.IN) ;
				proc.registerStoredProcedureParameter("pat_garment_color",String.class, ParameterMode.IN) ;
				
				
				proc.setParameter("operation_type_indicator", operationTypeIndicator);
				proc.setParameter("hr_id", inhillRomId);
				proc.setParameter("pat_hub_id",inPatientHubId);
				proc.setParameter("pat_bluetooth_id", inPatientBluetoothId);
				proc.setParameter("pat_device_serial_number", inPatientDeviceSerialNumber );
				proc.setParameter("pat_title", inPatientTitle);
				proc.setParameter("pat_first_name", inPatientFirstName);
				proc.setParameter("pat_middle_name", inPatientMiddleName);
				proc.setParameter("pat_last_name", inPatientLastName);
				proc.setParameter("pat_dob", inPatientdob);
				proc.setParameter("pat_email", inPatientEmail);
				proc.setParameter("pat_zipcode", inPatientZipCode);
				proc.setParameter("pat_primary_phone", inPatientPrimaryPhone);
				proc.setParameter("pat_mobile_phone", inPatientMobilePhone);
				proc.setParameter("pat_gender", inPatientGender);
				proc.setParameter("pat_lang_key", inPatientlangKey);
				proc.setParameter("pat_address", inPatientAddress);
				proc.setParameter("pat_city", inPatientCity);
				proc.setParameter("pat_state", inPatientState);
				proc.setParameter("pat_training_date",  inPatientTrainingDate);
				proc.setParameter("pat_primary_diagnosis",inPatientPrimaryDiagnosis);
				proc.setParameter("pat_garment_type", inPatientgarmentType);
				proc.setParameter("pat_garment_size", inPatientGarmentSize);
				proc.setParameter("pat_garment_color", inPatientGarmentColor);

				
				proc.registerStoredProcedureParameter("return_patient_id",String.class, ParameterMode.OUT);
				proc.registerStoredProcedureParameter("return_user_id",String.class, ParameterMode.OUT);
				

				proc.executeUpdate();
				String outPatientId =(String) proc.getOutputParameterValue("return_patient_id");
				String outPatientUser =(String) proc.getOutputParameterValue("return_user_id");
			
				

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

