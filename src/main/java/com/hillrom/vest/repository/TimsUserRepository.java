package com.hillrom.vest.repository;

import static com.hillrom.vest.config.Constants.RELATION_LABEL_SELF;
import static com.hillrom.vest.security.AuthoritiesConstants.PATIENT;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

import net.minidev.json.JSONObject;

import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Announcements;
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
	
	private final Logger log = LoggerFactory.getLogger("com.hillrom.vest.tims");
	
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
										String inPatientCreatedBy,
										String inPatientTrainingDate,
										String inPatientPrimaryDiagnosis,
										String inPatientgarmentType,
										String inPatientGarmentSize,
										String inPatientGarmentColor)  throws SQLException,Exception{

		
			JSONObject returnValues = new JSONObject();
			try{
		
				
				
						
				java.sql.Connection connection = entityManager.unwrap(SessionImpl.class).connection();
				


				 
				  CallableStatement callableStatement = connection.prepareCall("{call manage_patient_user(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

				  callableStatement.setString(1, operationTypeIndicator);
				  callableStatement.setString(2, inhillRomId);
				  callableStatement.setString(3, inPatientHubId);
				  callableStatement.setString(4, inPatientBluetoothId);
				  callableStatement.setString(5, inPatientDeviceSerialNumber);
				  callableStatement.setString(6, inPatientTitle);
				  callableStatement.setString(7, inPatientFirstName);
				  callableStatement.setString(8, inPatientMiddleName);
				  callableStatement.setString(9, inPatientLastName);
				  callableStatement.setString(10, inPatientdob);
				  callableStatement.setString(11, inPatientEmail);
				  callableStatement.setString(12, inPatientZipCode);
				  callableStatement.setString(13, inPatientPrimaryPhone);
				  callableStatement.setString(14, inPatientMobilePhone);
				  callableStatement.setString(15, inPatientGender);
				  callableStatement.setString(16, inPatientlangKey);
				  callableStatement.setString(17, inPatientAddress);
				  callableStatement.setString(18, inPatientCity);
				  callableStatement.setString(19, inPatientState);
				  callableStatement.setString(20, inPatientCreatedBy); 
				  callableStatement.setString(21, inPatientTrainingDate);
				  callableStatement.setString(22, inPatientPrimaryDiagnosis);
				  callableStatement.setString(23, inPatientgarmentType);
				  callableStatement.setString(24, inPatientGarmentSize);
				  callableStatement.setString(25, inPatientGarmentColor);			  
				  callableStatement.registerOutParameter(26, Types.VARCHAR);
				  callableStatement.registerOutParameter(27, Types.VARCHAR);
				  callableStatement.execute();

				  String outPatientId = callableStatement.getString(26);
				  String outPatientUser = callableStatement.getString(27);
				
				
				returnValues.put("return_patient_id", outPatientId);
				returnValues.put("return_user_id", outPatientUser);
				
			}
			catch(SQLException se)
			{
				throw se;
				//log.debug(se.getMessage());
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
						  String inCreatedBy) throws SQLException ,Exception{
				try{
				
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
						
				catch(Exception ex){
					ex.printStackTrace();
				}

			
			}
			
			
			public void createPatientProtocol(String typeKey,
					 String operationType,
					 String inPatientId,
					 String inCreatedBy) throws SQLException,Exception{
				try{
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
				catch(Exception ex){
					ex.printStackTrace();
				}
				
			}
			
			
			
			public void managePatientDevice(String operationType,
			String inPatientId,
			String inPatientoldDeviceSerialNumber,
			String inPatientNewDeviceSerialNumber,
			String inPatientBluetoothId,
			String inPatientHubId,
			String inPatientCreatedBy) throws SQLException, Exception{
			
				try{
					entityManager
					.createNativeQuery("call manage_patient_device("
					+ ":operation_type,"
					+ ":patient_id,"
					+ ":pat_old_device_serial_number,"
					+ ":pat_new_device_serial_number,"
					+ ":pat_bluetooth_id,"				   		
					+ ":pat_hub_id,"
					+ ":pat_created_by)")
					.setParameter("operation_type", operationType)
					.setParameter("patient_id", inPatientId)
					.setParameter("pat_old_device_serial_number",inPatientoldDeviceSerialNumber)
					.setParameter("pat_new_device_serial_number", inPatientNewDeviceSerialNumber)
					.setParameter("pat_bluetooth_id", inPatientBluetoothId)
					.setParameter("pat_hub_id",inPatientHubId)	
					.setParameter("pat_created_by",inPatientCreatedBy)
					.executeUpdate();
				}
				catch(Exception ex){
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
			String inpatientGarmentColor,
			String inpatientCreatedBy) throws SQLException, Exception{
			
				try{
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
					+ ":pat_garment_color,"
					+ ":pat_created_by)")
					.setParameter("operation_type_indicator", operationType)
					.setParameter("pat_patient_id", inpatientPatientId)
					.setParameter("pat_device_type",inpatientDeviceType)
					.setParameter("pat_device_is_active", inpatientDeviceIsActive)
					.setParameter("pat_device_serial_number", inDeviceSerialNumber )
					.setParameter("pat_hillrom_id", inpatientHillromId)
					.setParameter("pat_old_id", inpatientOldId)
					.setParameter("pat_training_date", (inpatientTrainingDate==null)?inpatientTrainingDate:new Timestamp(inpatientTrainingDate.toDateTimeAtStartOfDay().getMillis())) 
					.setParameter("pat_diagnosis_code1", inpatientDiagnosisCode1)
					.setParameter("pat_diagnosis_code2", inpatientDiagnosisCode2)
					.setParameter("pat_diagnosis_code3", inpatientDiagnosisCode3)
					.setParameter("pat_diagnosis_code4", inpatientDiagnosisCode4)
					.setParameter("pat_garment_type", inpatientGarmentType)
					.setParameter("pat_garment_size", inpatientGarmentSize)
					.setParameter("pat_garment_color", inpatientGarmentColor)
					.setParameter("pat_created_by", inpatientCreatedBy)
					.executeUpdate();
					
                       }catch(Exception ex){
					ex.printStackTrace();
				}
			
			
			}
			
			
			
			
			
			
			public void managePatientDeviceMonarch(String operationTypeIndicator,
						String inPatientId,
						String inPatientoldDeviceSerialNumber,
						String inPatientNewDeviceSerialNumber,
						String inPatientCreatedBy) throws SQLException ,Exception{
			try{
					entityManager
					.createNativeQuery("call manage_patient_device_monarch("
					+ ":operation_type_indicator,"
					+ ":patient_id,"
					+ ":pat_old_device_serial_number,"			   		
					+ ":pat_new_device_serial_number,"
					+ ":pat_created_by)")
					.setParameter("operation_type_indicator", operationTypeIndicator)
					.setParameter("patient_id", inPatientId)
					.setParameter("pat_old_device_serial_number",inPatientoldDeviceSerialNumber)
					.setParameter("pat_new_device_serial_number", inPatientNewDeviceSerialNumber)
					.setParameter("pat_created_by", inPatientCreatedBy)
					.executeUpdate();
	}catch(Exception ex){
					ex.printStackTrace();
				}

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
                                                                                String user_id) throws Exception{
				try{
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
								
				catch(Exception ex){
				
					ex.printStackTrace();
					//throw new SQLException(ex);
				}
           

            }
    
}

