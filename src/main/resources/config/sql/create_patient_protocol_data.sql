DROP PROCEDURE IF EXISTS create_patient_protocol_data;
DELIMITER $$
CREATE PROCEDURE `create_patient_protocol_data`(
	IN type_key varchar(15),
	IN in_patient_id varchar(45),
    IN in_created_by varchar(50)
)
BEGIN
	DECLARE temp_user_id bigint(20);
	DECLARE protocol_id varchar(45) ;
	DECLARE created_date datetime;

	DECLARE temp_protocal_id INT; 
	DECLARE temp_patient_id varchar(45); 
	DECLARE temp_type_key varchar(15);
	DECLARE temp_treatments_per_day bigint(20);
	DECLARE temp_treatment_label varchar(45);
	DECLARE temp_min_minutes_per_treatment bigint(20);
	DECLARE temp_max_minutes_per_treatment bigint(20);
	DECLARE temp_min_frequency bigint(20);
	DECLARE temp_max_frequency bigint(20);
	DECLARE temp_min_pressure bigint(20);
	DECLARE temp_max_pressure bigint(20);
	DECLARE temp_protocol_key varchar(45);
    
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    
	BEGIN
		ROLLBACK;
        RESIGNAL;
    END;
    
	SET created_date = now();
    
	SELECT `user_id` INTO temp_user_id FROM `USER_PATIENT_ASSOC` WHERE `patient_id`= in_patient_id AND `user_role` = 'PATIENT';

	IF type_key = 'S' THEN
		
        call get_next_protocol_hillromid(@gen_protocol_id);

		SELECT `id`,`type`,
		`treatments_per_day`,
		`treatment_label`,
		`min_minutes_per_treatment`,
		`max_minutes_per_treatment`,
		`min_frequency`,
		`max_frequency`,
		`min_pressure`,
		`max_pressure`,
		`protocol_key` INTO
        temp_protocal_id,
		 temp_type_key,
		 temp_treatments_per_day,
		 temp_treatment_label,
		 temp_min_minutes_per_treatment,
		 temp_max_minutes_per_treatment,
		 temp_min_frequency,
		 temp_max_frequency,
		 temp_min_pressure,
		 temp_max_pressure,
		 temp_protocol_key FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id;
         
		call get_next_protocol_hillromid(@gen_protocol_id);
		INSERT INTO PATIENT_PROTOCOL_DATA
		( `id`, `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_pressure`, 
        `max_pressure`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, in_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_pressure,
        temp_max_pressure,in_created_by,created_date ,in_created_by ,created_date,0,temp_protocol_key); 


-- For Minnesota
	ELSEIF type_key = 'M' THEN 
		
		
      WHILE EXISTS(SELECT `id` FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id AND `to_be_inserted`= 1) DO
      
      SELECT `id`,`type`,
		`treatments_per_day`,
		`treatment_label`,
		`min_minutes_per_treatment`,
		`max_minutes_per_treatment`,
		`min_frequency`,
		`max_frequency`,
		`min_pressure`,
		`max_pressure`,
		`protocol_key` INTO
         temp_protocal_id,
		 temp_type_key,
		 temp_treatments_per_day,
		 temp_treatment_label,
		 temp_min_minutes_per_treatment,
		 temp_max_minutes_per_treatment,
		 temp_min_frequency,
		 temp_max_frequency,
		 temp_min_pressure,
		 temp_max_pressure,
		 temp_protocol_key FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id AND `to_be_inserted` = 1 LIMIT 1;
         
		UPDATE `protocol_data_temp_table` SET `to_be_inserted` = 0 where `id` = temp_protocal_id;
		
        call get_next_protocol_hillromid(@gen_protocol_id);
		INSERT INTO PATIENT_PROTOCOL_DATA
		( `id`, `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_pressure`, 
        `max_pressure`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, in_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_pressure,
        temp_max_pressure,in_created_by,created_date ,in_created_by ,created_date,0,temp_protocol_key); 
        
        UPDATE `protocol_data_temp_table` SET `to_be_inserted` = 0 where `id` = temp_protocal_id;
    END WHILE;
        

-- For customer single step
	ELSEIF type_key = 'SO' THEN 
    	SELECT `id`,`type`,
		`treatments_per_day`,
		`treatment_label`,
		`min_minutes_per_treatment`,
		`max_minutes_per_treatment`,
		`min_frequency`,
		`max_frequency`,
		`min_pressure`,
		`max_pressure`,
		`protocol_key` INTO
        temp_protocal_id,
		 temp_type_key,
		 temp_treatments_per_day,
		 temp_treatment_label,
		 temp_min_minutes_per_treatment,
		 temp_max_minutes_per_treatment,
		 temp_min_frequency,
		 temp_max_frequency,
		 temp_min_pressure,
		 temp_max_pressure,
		 temp_protocol_key FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id;
         
		call get_next_protocol_hillromid(@gen_protocol_id);
		INSERT INTO PATIENT_PROTOCOL_DATA
		( `id`, `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_pressure`, 
        `max_pressure`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, in_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_pressure,
        temp_max_pressure,in_created_by,created_date ,in_created_by ,created_date,0,temp_protocol_key); 
		
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only S, M and SO are supported as type_key';
	END IF;
END$$
DELIMITER ;
