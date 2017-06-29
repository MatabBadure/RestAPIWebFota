package com.hillrom.vest.repository;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;

import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Announcements;
import com.hillrom.vest.domain.Tims;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.joda.time.LocalDate;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface TimsRepository extends JpaRepository<Tims, Long> {
	
	
    @Procedure(name = "create_patient_protocol_monarch")
    void createPatientProtocolMonarch(@Param("type_key") String typeKey,
    								  @Param("operation_type") String operationType,
    								  @Param("in_patient_id") String inPatientId,
    								  @Param("in_created_by") String inCreatedBy);
    
    @Procedure(name = "create_patient_protocol")
    void createPatientProtocol(@Param("type_key") String typeKey,
			  					 @Param("operation_type") String operationType,
			  					 @Param("in_patient_id") String inPatientId,
			  					 @Param("in_created_by") String inCreatedBy);
    //Start of my code 
    @Procedure(name = "manage_patient_device")
    void managePatientDevice(@Param("operation_type") String operationType,
				 @Param("patient_id") String inPatientId,
				 @Param("pat_old_device_serial_number") String inPatientoldDeviceSerialNumber,
				 @Param("pat_new_device_serial_number") String inPatientNewDeviceSerialNumber,
				 @Param("pat_bluetooth_id") String inPatientBluetoothId,
				 @Param("pat_hub_id") String inPatientHubId);
    
    @Procedure(name = "manage_patient_device_assoc")
    void managePatientDeviceAssociation(@Param("operation_type_indicator") String operationType,
				 @Param("pat_patient_id") String inpatientPatientId,//Need Clarification
				 @Param("pat_device_type") String inpatientDeviceType,
				 @Param("pat_device_is_active") String inpatientDeviceIsActive,
				 @Param("pat_device_serial_number") String inPatientBluetoothId,
				 @Param("pat_hillrom_id") String inpatientHillromId,
				 @Param("pat_old_id") String inpatientOldId,
				 @Param("pat_training_date") LocalDate inpatientTrainingDate,//Need to check for datetime
				 @Param("pat_diagnosis_code1") String inpatientDiagnosisCode1,
				 @Param("pat_diagnosis_code2") String inpatientDiagnosisCode2,
				 @Param("pat_diagnosis_code3") String inpatientDiagnosisCode3,
				 @Param("pat_diagnosis_code4") String inpatientDiagnosisCode4,
				 @Param("pat_garment_type") String inpatientGarmentType,
				 @Param("pat_garment_size") String inpatientGarmentSize,
				 @Param("pat_garment_color") String inpatientGarmentColor);
   
				 
				 

   
    
    @Procedure(name = "manage_patient_device_monarch")
    void managePatientDeviceMonarch(@Param("operation_type_indicator") String operationTypeIndicator,
				 @Param("patient_id") String inPatientId,
				 @Param("pat_old_device_serial_number") String inPatientoldDeviceSerialNumber,
				 @Param("pat_new_device_serial_number") String inPatientNewDeviceSerialNumber);
   
    
}

