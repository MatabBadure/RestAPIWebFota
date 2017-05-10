DELIMITER $$
CREATE DEFINER=`root`@`%` PROCEDURE `create_patient_protocol_monarch`(
	IN type_key varchar(15),
	IN in_patient_id varchar(45),
    IN in_created_by varchar(50)
)
BEGIN
	DECLARE temp_user_id bigint(20);
	DECLARE protocol_id varchar(45) ;
	DECLARE created_date datetime;
    DECLARE temp_max_rev bigint(20);

	DECLARE temp_protocal_id INT; 
	DECLARE temp_patient_id varchar(45); 
	DECLARE temp_type_key varchar(15);
	DECLARE temp_treatments_per_day bigint(20);
	DECLARE temp_treatment_label varchar(45);
	DECLARE temp_min_minutes_per_treatment bigint(20);
	DECLARE temp_max_minutes_per_treatment bigint(20);
	DECLARE temp_min_frequency bigint(20);
	DECLARE temp_max_frequency bigint(20);
	DECLARE temp_min_intensity bigint(20);
	DECLARE temp_max_intensity bigint(20);
	DECLARE temp_protocol_key varchar(45);
    
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
    
	BEGIN
		ROLLBACK;
        RESIGNAL;
    END;
    
	SET created_date = now();
    
    IF  (SELECT COUNT(*)  FROM PATIENT_PROTOCOL_DATA_MONARCH WHERE patient_id = in_patient_id AND is_deleted = 0 LIMIT 1) > 0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Protocol already exists for the Patient.';
	END IF;
    
	SELECT `user_id` INTO temp_user_id FROM `USER_PATIENT_ASSOC` WHERE `patient_id`= in_patient_id AND `user_role` = 'PATIENT';
	


	IF type_key = 'Normal' THEN

		SELECT MAX(`id`)+1 INTO temp_max_rev FROM `AUDIT_REVISION_INFO`;			
		
        call get_next_protocol_monarch_hillromid(@gen_protocol_id);

		SELECT `id`,`type`,
		`treatments_per_day`,
		`treatment_label`,
		`min_minutes_per_treatment`,
		`max_minutes_per_treatment`,
		`min_frequency`,
		`max_frequency`,
		`min_pressure`,
		`max_pressure` INTO
        temp_protocal_id,
		 temp_type_key,
		 temp_treatments_per_day,
		 temp_treatment_label,
		 temp_min_minutes_per_treatment,
		 temp_max_minutes_per_treatment,
		 temp_min_frequency,
		 temp_max_frequency,
		 temp_min_intensity,
		 temp_max_intensity 
         FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id AND `to_be_inserted` = 1 LIMIT 1;
         
         IF temp_protocal_id IS NOT NULL THEN
         
		call get_next_protocol_monarch_hillromid(@gen_protocol_id);
		INSERT INTO PATIENT_PROTOCOL_DATA_MONARCH
		( `id`, `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_intensity`, 
        `max_intensity`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, in_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_intensity,
        temp_max_intensity,in_created_by,created_date ,in_created_by ,created_date,0,@gen_protocol_id);
		
		INSERT INTO AUDIT_REVISION_INFO (`id`, `timestamp`, `user_id`) 
		VALUES 
		(temp_max_rev, UNIX_TIMESTAMP(created_date), in_created_by);
		
		INSERT INTO PATIENT_PROTOCOL_DATA_MONARCH_AUD (`id`, `REV`, `REVTYPE`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`, `is_deleted`, `max_frequency`, `max_intensity`, `min_frequency`, `min_minutes_per_treatment`, `min_intensity`, `protocol_key`, `treatments_per_day`, `type`, `PATIENT_ID`, `USER_ID`) 
		VALUES 
		(@gen_protocol_id, temp_max_rev, 0, in_created_by, created_date, in_created_by, created_date,0 , temp_max_frequency, temp_max_intensity, temp_min_frequency, temp_min_minutes_per_treatment, temp_min_intensity, @gen_protocol_id, temp_treatments_per_day, type_key, in_patient_id, temp_user_id);

        
        UPDATE `protocol_data_temp_table` SET `to_be_inserted` = 0 where `id` = temp_protocal_id;
        END IF;


	ELSEIF type_key = 'Custom' THEN 
		
	  START TRANSACTION;
      call get_next_protocol_monarch_hillromid(@gen_protocol_id);
      
      SET temp_protocol_key = @gen_protocol_id;
      
      WHILE EXISTS(SELECT `id` FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id AND `to_be_inserted`= 1) DO
      
	  SELECT MAX(`id`)+1 INTO temp_max_rev FROM `AUDIT_REVISION_INFO`;
	  
      SELECT `id`,`type`,
		`treatments_per_day`,
		`treatment_label`,
		`min_minutes_per_treatment`,
		`max_minutes_per_treatment`,
		`min_frequency`,
		`max_frequency`,
		`min_pressure`,
		`max_pressure` INTO
         temp_protocal_id,
		 temp_type_key,
		 temp_treatments_per_day,
		 temp_treatment_label,
		 temp_min_minutes_per_treatment,
		 temp_max_minutes_per_treatment,
		 temp_min_frequency,
		 temp_max_frequency,
		 temp_min_intensity,
		 temp_max_intensity
         FROM `protocol_data_temp_table` WHERE `type` = type_key AND `patient_id` = in_patient_id AND `to_be_inserted` = 1 LIMIT 1;
         
		UPDATE `protocol_data_temp_table` SET `to_be_inserted` = 0 where `id` = temp_protocal_id;
        
		INSERT INTO PATIENT_PROTOCOL_DATA_MONARCH
		( `id`, `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_intensity`, 
        `max_intensity`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, in_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_intensity,
        temp_max_intensity,in_created_by,created_date ,in_created_by ,created_date,0,temp_protocol_key); 

		INSERT INTO AUDIT_REVISION_INFO (`id`, `timestamp`, `user_id`) 
		VALUES 
		(temp_max_rev, UNIX_TIMESTAMP(created_date), in_created_by);

		
		INSERT INTO PATIENT_PROTOCOL_DATA_MONARCH_AUD (`id`, `REV`, `REVTYPE`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`, `is_deleted`, `max_frequency`, `max_intensity`, `min_frequency`, `min_minutes_per_treatment`, `min_intensity`, `protocol_key`, `treatments_per_day`, `type`, `PATIENT_ID`, `USER_ID`) 
		VALUES 
		(@gen_protocol_id, temp_max_rev, 0, in_created_by, created_date, in_created_by, created_date,0 , temp_max_frequency, temp_max_intensity, temp_min_frequency, temp_min_minutes_per_treatment, temp_min_intensity, @gen_protocol_id, temp_treatments_per_day, type_key, in_patient_id, temp_user_id);

        
        UPDATE `protocol_data_temp_table` SET `to_be_inserted` = 0 where `id` = temp_protocal_id;
        call get_next_protocol_monarch_hillromid(@gen_protocol_id);
    END WHILE;
    COMMIT;
		
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only Normal and Custom are supported as type_key.';
	END IF;
END$$
DELIMITER ;
