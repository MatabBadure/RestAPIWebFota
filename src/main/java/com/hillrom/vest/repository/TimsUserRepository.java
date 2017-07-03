package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.sql.CallableStatement;
import java.sql.Types;
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
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
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
		
				
				
						
				java.sql.Connection connection = entityManager.unwrap(SessionImpl.class).connection();
				


				 
				  CallableStatement callableStatement = connection.prepareCall("{call manage_patient_user(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				  callableStatement.setString(1, operationTypeIndicator);//Parameter #1
				  callableStatement.setString(2, inhillRomId);////Parameter #2
				  callableStatement.setString(3, inPatientHubId);// //Parameter #3
				  callableStatement.setString(4, inPatientBluetoothId);// //Parameter #3
				  callableStatement.setString(5, inPatientDeviceSerialNumber);// //Parameter #3
				  callableStatement.setString(6, inPatientTitle);// //Parameter #3
				  callableStatement.setString(7, inPatientFirstName);// //Parameter #3
				  callableStatement.setString(8, inPatientMiddleName);// //Parameter #3
				  callableStatement.setString(9, inPatientLastName);// //Parameter #3
				  callableStatement.setString(10, inPatientdob);// //Parameter #3
				  callableStatement.setString(11, inPatientEmail);// //Parameter #3
				  callableStatement.setString(12, inPatientZipCode);// //Parameter #3
				  callableStatement.setString(13, inPatientPrimaryPhone);// //Parameter #3
				  callableStatement.setString(14, inPatientMobilePhone);// //Parameter #3
				  callableStatement.setString(15, inPatientGender);// //Parameter #3
				  callableStatement.setString(16, inPatientlangKey);// //Parameter #3
				  callableStatement.setString(17, inPatientAddress);// //Parameter #3
				  callableStatement.setString(18, inPatientCity);// //Parameter #3
				  callableStatement.setString(19, inPatientState);// //Parameter #3
				  callableStatement.setString(20, inPatientTrainingDate);// //Parameter #3
				  callableStatement.setString(21, inPatientPrimaryDiagnosis);// //Parameter #3
				  callableStatement.setString(22, inPatientgarmentType);// //Parameter #3
				  callableStatement.setString(23, inPatientGarmentSize);// //Parameter #3
				  callableStatement.setString(24, inPatientGarmentColor);// //Parameter #3				  
				  callableStatement.registerOutParameter(25, Types.VARCHAR); //Output # 1
				  callableStatement.registerOutParameter(26, Types.VARCHAR); //Output # 2
				  callableStatement.execute();

				  String outPatientId = callableStatement.getString(25);
				  String outPatientUser = callableStatement.getString(26);
				
				
				/*
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
				proc.setParameter("pat_training_date", inPatientTrainingDate);
				proc.setParameter("pat_primary_diagnosis",inPatientPrimaryDiagnosis);
				proc.setParameter("pat_garment_type", inPatientgarmentType);
				proc.setParameter("pat_garment_size", inPatientGarmentSize);
				proc.setParameter("pat_garment_color", inPatientGarmentColor);

				
				proc.registerStoredProcedureParameter("return_patient_id",String.class, ParameterMode.OUT);
				proc.registerStoredProcedureParameter("return_user_id",String.class, ParameterMode.OUT);
				
				String outPatientId =(String) proc.getOutputParameterValue("return_patient_id");
				String outPatientUser =(String) proc.getOutputParameterValue("return_user_id");
				proc.executeUpdate();
				
				System.out.println("Value of patientId " +  outPatientId + "Value of user_id " + outPatientUser );

				*/
				returnValues.put("return_patient_id", outPatientId);
				returnValues.put("return_user_id", outPatientUser);
				
			}
			catch(Exception ex){
				ex.printStackTrace();
				return returnValues;
			}
				
				return returnValues;


	}

	
			public void createPatientProtocolMonarch(String typeKey,
						  String operationType,
						  String inPatientId,
						  String inCreatedBy){
				entityManager
				.createNativeQuery("call create_patient_protocol_monarch("
				+ ":type_key,"
				+ ":operation_type,"
				+ ":inPatientId,"
				+ ":inCreatedBy)")
				.setParameter("type_key", typeKey)
				.setParameter("operation_type", operationType)
				.setParameter("inPatientId",inPatientId)
				.setParameter("inCreatedBy", inCreatedBy)
				.executeUpdate();
				

			
			}
			
			
			public void createPatientProtocol(String typeKey,
					 String operationType,
					 String inPatientId,
					 String inCreatedBy){
			
				entityManager
				.createNativeQuery("call create_patient_protocol("
				+ ":type_key,"
				+ ":operation_type,"
				+ ":inPatientId,"
				+ ":inCreatedBy)")
				.setParameter("type_key", typeKey)
				.setParameter("operation_type", operationType)
				.setParameter("inPatientId",inPatientId)
				.setParameter("inCreatedBy", inCreatedBy)
				.executeUpdate();
				
			}
			
			
			
			public void managePatientDevice(String operationType,
			String inPatientId,
			String inPatientoldDeviceSerialNumber,
			String inPatientNewDeviceSerialNumber,
			String inPatientBluetoothId,
			String inPatientHubId){
			
				try{
					entityManager
					.createNativeQuery("call manage_patient_device("
					+ ":operation_type,"
					+ ":patient_id,"
					+ ":pat_old_device_serial_number,"
					+ ":pat_new_device_serial_number,"
					+ ":pat_bluetooth_id,"				   		
					+ ":pat_hub_id)")
					.setParameter("operation_type", operationType)
					.setParameter("patient_id", inPatientId)
					.setParameter("pat_old_device_serial_number",inPatientoldDeviceSerialNumber)
					.setParameter("pat_new_device_serial_number", inPatientNewDeviceSerialNumber)
					.setParameter("pat_bluetooth_id", inPatientBluetoothId)
					.setParameter("pat_hub_id",inPatientHubId)					
					.executeUpdate();
				}catch(Exception ex){
					ex.printStackTrace();
				}
				

			
			}
			
			
			public void managePatientDeviceAssociation(String operationType,
			String inpatientPatientId,//Need Clarification
			String inpatientDeviceType,
			String inpatientDeviceIsActive,
			String inDeviceSerialNumber,
			String inpatientHillromId,
			String inpatientOldId,
			LocalDate inpatientTrainingDate,//Need to check for datetime
			String inpatientDiagnosisCode1,
			String inpatientDiagnosisCode2,
			String inpatientDiagnosisCode3,
			String inpatientDiagnosisCode4,
			String inpatientGarmentType,
			String inpatientGarmentSize,
			String inpatientGarmentColor){
			
			
					entityManager
					.createNativeQuery("call manage_patient_device_assoc("
					+ ":operation_type_indicator,"
					+ ":pat_patient_id,"
					+ ":pat_device_type,"
					+ ":pat_device_is_active,"
					+ ":pat_device_serial_number,"
					+ ":pat_hillrom_id,"
					+ ":pat_old_id,"
					+ ":pat_training_date,"
					+ ":pat_diagnosis_code1,"
					+ ":pat_diagnosis_code2,"
					+ ":pat_diagnosis_code3,"
					+ ":pat_diagnosis_code4,"
					+ ":pat_garment_type,"
					+ ":pat_garment_size,"
					+ ":pat_garment_color)")
					.setParameter("operation_type_indicator", operationType)
					.setParameter("pat_patient_id", inpatientPatientId)
					.setParameter("pat_device_type",inpatientDeviceType)
					.setParameter("pat_device_is_active", inpatientDeviceIsActive)
					.setParameter("pat_device_serial_number", inDeviceSerialNumber )
					.setParameter("pat_hillrom_id", inpatientHillromId)
					.setParameter("pat_old_id", inpatientOldId)
					.setParameter("pat_training_date", inpatientTrainingDate)
					.setParameter("pat_diagnosis_code1", inpatientDiagnosisCode1)
					.setParameter("pat_diagnosis_code2", inpatientDiagnosisCode2)
					.setParameter("pat_diagnosis_code3", inpatientDiagnosisCode3)
					.setParameter("pat_diagnosis_code4", inpatientDiagnosisCode4)
					.setParameter("pat_garment_type", inpatientGarmentType)
					.setParameter("pat_garment_size", inpatientGarmentSize)
					.setParameter("pat_garment_color", inpatientGarmentColor)
					.executeUpdate();
					
			
			
			}
			
			
			
			
			
			
			public void managePatientDeviceMonarch(String operationTypeIndicator,
						String inPatientId,
						String inPatientoldDeviceSerialNumber,
						String inPatientNewDeviceSerialNumber){
			
					List result = entityManager
					.createNativeQuery("call manage_patient_device_monarch("
					+ ":operation_type_indicator,"
					+ ":patient_id,"
					+ ":pat_old_device_serial_number,"			   		
					+ ":pat_new_device_serial_number)")
					.setParameter("operation_type_indicator", operationTypeIndicator)
					.setParameter("patient_id", inPatientId)
					.setParameter("pat_old_device_serial_number",inPatientoldDeviceSerialNumber)
					.setParameter("pat_new_device_serial_number", inPatientNewDeviceSerialNumber)			
					.getResultList();
					
					System.out.println("Returned values from manage_patient_device_monarch : " + result);
			}
   
			
			@Transactional
            public void insertIntoProtocolDataTempTable(String patient_id,
                                                                                String type,
                                                                                int treatments_per_day,
                                                                                String treatment_label,
                                                                                int min_minutes_per_treatment,
                                                                                int max_minutes_per_treatment,
                                                                                int min_frequency,
                                                                                int max_frequency,
                                                                                int min_pressure,
                                                                                int max_pressure,
                                                                                int to_be_inserted,
                                                                                String user_id){
           
                         entityManager
                         .createNativeQuery("insert into protocol_data_temp_table("
                                + "patient_id,"
                                + "type,"
                                + "treatments_per_day,"          
                                + "treatment_label,"      
                                + "min_minutes_per_treatment,"          
                                + "max_minutes_per_treatment,"          
                                + "min_frequency,"        
                                + "max_frequency,"        
                                + "min_pressure,"         
                                + "max_pressure,"         
                                + "to_be_inserted,"       
                                + "id) values ("
                                + ":patient_id,"
                                + ":type,"
                                + ":treatments_per_day,"         
                                + ":treatment_label,"            
                                + ":min_minutes_per_treatment,"         
                                + ":max_minutes_per_treatment,"         
                                + ":min_frequency,"       
                                + ":max_frequency,"       
                                + ":min_pressure,"        
                                + ":max_pressure,"        
                                + ":to_be_inserted,"
                                + ":id)")
                         .setParameter("patient_id", patient_id)
                         .setParameter("type", type)
                         .setParameter("treatments_per_day",treatments_per_day)
                         .setParameter("treatment_label", treatment_label)     
                         .setParameter("min_minutes_per_treatment", min_minutes_per_treatment)
                         .setParameter("max_minutes_per_treatment", max_minutes_per_treatment)
                         .setParameter("min_frequency",min_frequency)
                         .setParameter("max_frequency", max_frequency)  
                         .setParameter("min_pressure", min_pressure)
                         .setParameter("max_pressure", max_pressure)
                         .setParameter("to_be_inserted",to_be_inserted)
                         .setParameter("id", user_id)                                 
                         .executeUpdate();
           

            }
    
}

