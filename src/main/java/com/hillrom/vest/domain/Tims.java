package com.hillrom.vest.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hillrom.vest.domain.util.CustomDateTimeDeserializer;
import com.hillrom.vest.domain.util.CustomDateTimeSerializer;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

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
                              })
})

public class Tims implements Serializable {
	
	   	@Id 
	    @GeneratedValue
	    private long id;

}


