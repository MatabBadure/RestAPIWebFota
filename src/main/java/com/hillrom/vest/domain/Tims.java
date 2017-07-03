package com.hillrom.vest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomDateTimeDeserializer;
import com.hillrom.vest.domain.util.CustomDateTimeSerializer;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;



@Entity
@Table(name = "TIMS_TABLE")
@NamedStoredProcedureQueries({
   @NamedStoredProcedureQuery(name = "create_patient_protocol_monarch", 
                              procedureName = "create_patient_protocol_monarch",
                              parameters = {
                                 @StoredProcedureParameter(mode = ParameterMode.IN, name = "type_key", type = String.class),
                                 @StoredProcedureParameter(mode = ParameterMode.IN, name = "operation_type", type = String.class),
                                 @StoredProcedureParameter(mode = ParameterMode.IN, name = "in_patient_id", type = String.class),
                                 @StoredProcedureParameter(mode = ParameterMode.IN, name = "in_created_by", type = String.class)
                              }),
   @NamedStoredProcedureQuery(name = "create_patient_protocol", 
                              procedureName = "create_patient_protocol",
                              parameters = {
                                  @StoredProcedureParameter(mode = ParameterMode.IN, name = "type_key", type = String.class),
                                  @StoredProcedureParameter(mode = ParameterMode.IN, name = "operation_type", type = String.class),
                                  @StoredProcedureParameter(mode = ParameterMode.IN, name = "in_patient_id", type = String.class),
                                  @StoredProcedureParameter(mode = ParameterMode.IN, name = "in_created_by", type = String.class)
                              }),
   @NamedStoredProcedureQuery(name = "manage_patient_device", 
                             procedureName = "manage_patient_device",
						     parameters = {
						         @StoredProcedureParameter(mode = ParameterMode.IN, name = "operation_type", type = String.class),
						         @StoredProcedureParameter(mode = ParameterMode.IN, name = "patient_id", type = String.class),
						         @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_old_device_serial_number", type = String.class),
						         @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_new_device_serial_number", type = String.class),
						         @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_bluetooth_id", type = String.class),
						         @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_hub_id", type = String.class)						
						   }),
  @NamedStoredProcedureQuery(name = "manage_patient_device_assoc", 
		                     procedureName = "manage_patient_device_assoc",
						     parameters = {
							     @StoredProcedureParameter(mode = ParameterMode.IN, name = "operation_type_indicator", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_patient_id", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_device_type", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_device_is_active", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_device_serial_number", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_hillrom_id", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_old_id", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_training_date", type = org.jadira.usertype.dateandtime.joda.PersistentLocalDate.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_diagnosis_code1", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_diagnosis_code2", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_diagnosis_code3", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_diagnosis_code4", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_garment_type", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_garment_size", type = String.class),
								 @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_garment_color", type = String.class)
								   }),
								   
								   

			
								   
 @NamedStoredProcedureQuery(name = "manage_patient_device_monarch", 
                            procedureName = "manage_patient_device_monarch",
                            parameters = {
	                            @StoredProcedureParameter(mode = ParameterMode.IN, name = "operation_type_indicator", type = String.class),
	                            @StoredProcedureParameter(mode = ParameterMode.IN, name = "patient_id", type = String.class),
	                            @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_old_device_serial_number", type = String.class),
	                            @StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_new_device_serial_number", type = String.class)
	                              }),							   
								   


@NamedStoredProcedureQuery(  
	    name="managepatientuser",  
	    procedureName="manage_patient_user",   
	    parameters={          
	        @StoredProcedureParameter(mode = ParameterMode.IN, name = "operation_type_indicator", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "hr_id", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_hub_id", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_bluetooth_id", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_device_serial_number", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_title", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_first_name", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_middle_name", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_last_name", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_dob", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_email", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_zipcode", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_primary_phone", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_mobile_phone", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_gender", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_lang_key", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_address", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_city", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_state", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_training_date", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_primary_diagnosis", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_garment_type", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_garment_size", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "pat_garment_color", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.OUT, name = "return_patient_id", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.OUT, name = "return_user_id", type = String.class)
	    }  
	    )

})
public class Tims implements Serializable {
	
	   	@Id 
	    @GeneratedValue
	    private long id;

}


