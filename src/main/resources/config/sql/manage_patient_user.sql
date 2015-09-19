DELIMITER $$
CREATE PROCEDURE `manage_patient_user`(
	IN operation_type_indicator VARCHAR(10),
    IN hr_id varchar(255),
	IN pat_hub_id varchar(255),
	IN pat_bluetooth_id varchar(255),
    IN pat_device_serial_number varchar(255), 
    IN pat_title varchar(255),
    IN pat_first_name varchar(255),
    IN pat_middle_name varchar(255),
    IN pat_last_name varchar(255),
	IN pat_dob date,
    IN pat_email varchar(255),
    IN pat_zipcode varchar(11),
    IN pat_primary_phone varchar(20),
    IN pat_mobile_phone varchar(20),
    IN pat_gender varchar(10),
    IN pat_lang_key varchar(10),
	IN pat_address varchar(255),
    IN pat_city varchar(255),
    IN pat_state varchar(255),
    OUT return_patient_id varchar(255),
    OUT return_user_id varchar(255)
)
BEGIN

    DECLARE created_by varchar(255);
    DECLARE created_date datetime;
    DECLARE encrypted_password varchar(60);
    DECLARE gen_patient_id varchar(255);
    DECLARE temp_serial_number VARCHAR(255);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
        RESIGNAL;
    END;
		 
    

  -- This is a block of final values that are used in the procedure.
	SET created_by = 'system';
    SET encrypted_password = encrypt(CONCAT(CAST(pat_zipcode AS CHAR),SUBSTRING(pat_last_name,1,4),CAST(DATE_FORMAT(pat_dob,'%d%m%Y') AS CHAR)));
-- Creare patient user when operation_type_indicator 0,
	
	IF operation_type_indicator = 'CREATE' THEN
    
		SELECT `serial_number` INTO temp_serial_number FROM `PATIENT_INFO` WHERE `serial_number` = pat_device_serial_number;
        
		-- When Hillrom id already exists
        IF temp_serial_number IS NOT NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Patient with same device serial number already exits.';
		END IF;
        
		START TRANSACTION;
        -- Get Hillrom ID
		call get_next_patient_hillromid(@gen_patient_id);
		
		INSERT INTO `PATIENT_INFO` (`id`, `hillrom_id`, `hub_id`, `serial_number`, `bluetooth_id`, `title`, `first_name`, `middle_name`,
		`last_name`, `dob`, `email`, `zipcode`, `web_login_created`, `primary_phone`, `mobile_phone`, `gender`, `lang_key`, `expired`, `expired_date`, `address`, `city`, `state`)
		VALUES
		(@gen_patient_id, hr_id, pat_hub_id, pat_device_serial_number, pat_bluetooth_id, pat_title, pat_first_name, pat_middle_name,
			pat_last_name, pat_dob, pat_email, pat_zipcode,0, pat_primary_phone, pat_mobile_phone, pat_gender, pat_lang_key, 0, NULL, pat_address, pat_city, pat_state);
		
        SET created_date = now();
        
		INSERT INTO `USER`(
		`email`, `PASSWORD`, `title`, `first_name`, `middle_name`, `last_name`, `activated`, `lang_key`, `activation_key`, `reset_key`, 
		`created_by`, `created_date`, `reset_date`, `last_loggedin_at`, `last_modified_by`, 
		`last_modified_date`, `is_deleted`, `gender`, `zipcode`, `terms_condition_accepted`,`primary_phone`, `mobile_phone`,
		`terms_condition_accepted_date`, `dob`, `hillrom_id`,`hmr_notification`,`accept_hmr_notification`,`accept_hmr_setting`)
		VALUES(
		pat_email, encrypted_password, pat_title, pat_first_name, pat_middle_name, pat_last_name, 0, pat_lang_key,NULL, NULL,
		created_by, created_date, NULL, NULL, created_by, 
		created_date, 0, pat_gender, pat_zipcode,0,pat_primary_phone, pat_mobile_phone,
		NULL, pat_dob, hr_id,NULL,0,0);
		 
		SELECT id INTO return_user_id FROM `USER` WHERE email = pat_email;
		
		INSERT INTO `USER_EXTENSION` (`user_id`,`address`,`city`,`state`,`is_deleted`)
		VALUES (return_user_id, pat_address, pat_city, pat_state,0);
		
		INSERT INTO `USER_PATIENT_ASSOC` (`user_id`,  `patient_id`, `user_role`, `relation_label`)
		VALUES(return_user_id,@gen_patient_id,'PATIENT','SELF');
        
        INSERT INTO `USER_AUTHORITY` (`user_id`,  `authority_name`)
		VALUES(return_user_id,'PATIENT');
	
		COMMIT;
		SET return_patient_id = @gen_patient_id;
	-- Update Patient user
    
	ELSEIF operation_type_indicator = 'UPDATE' THEN 
    
		SELECT `id` INTO return_patient_id FROM `PATIENT_INFO` WHERE `serial_number` = pat_device_serial_number;
		SELECT `user_id` INTO return_user_id FROM `USER_PATIENT_ASSOC` WHERE `patient_id`= return_patient_id AND `user_role`= 'PATIENT';
        
        IF return_patient_id IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Patient with given device number does not exist.';
		END IF;
    
		START TRANSACTION;
		UPDATE `PATIENT_INFO`   SET
			`hub_id` = pat_hub_id,
			`bluetooth_id` = pat_bluetooth_id,
            `hillrom_id` = hr_id,
			`title` = pat_title,
			`first_name` = pat_first_name,
			`middle_name` = pat_middle_name,
			`last_name` = pat_last_name,
			`dob` = pat_dob,
			`email` = pat_email,
			`zipcode` = pat_zipcode,
			`primary_phone` = pat_primary_phone,
			`mobile_phone` = pat_mobile_phone,
			`gender` = pat_gender,
			`lang_key` = pat_lang_key,
			`address` = pat_address,
			`city` = pat_city,
			`state` = pat_state
		WHERE `serial_number`= pat_device_serial_number;
		
		UPDATE `USER` SET
			`hillrom_id` = hr_id,
			`email` = pat_email,
			`title` = pat_title,
			`first_name` = pat_first_name,
			`middle_name` = pat_middle_name,
			`last_name` = pat_last_name,
			`lang_key` = pat_lang_key,
			`last_modified_by` = created_by,
			`last_modified_date` = now(),
			`gender` = pat_gender,
			`zipcode` = pat_zipcode,
			`dob` = pat_dob
			WHERE `id` = return_user_id;
            
		UPDATE `USER_EXTENSION` SET 
			`address` = pat_address ,
			`city` = pat_address,
			`state` = pat_state ,
			`primary_phone` = pat_primary_phone,
            `mobile_phone`= pat_mobile_phone
            WHERE `user_id` = return_user_id;
            
		COMMIT;
        
	ELSEIF operation_type_indicator = 'DELETE' THEN 
    
		SELECT `id` INTO return_patient_id FROM `PATIENT_INFO` WHERE `serial_number` = pat_device_serial_number;
		SELECT `user_id` INTO return_user_id FROM `USER_PATIENT_ASSOC` WHERE `patient_id`= return_patient_id;
        
        IF (return_user_id IS NULL) THEN 
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Pateint with given device serial number does not exist';
        ELSE
        START TRANSACTION;
			UPDATE `USER` SET `is_deleted` = 1 WHERE `id` = return_user_id;
            UPDATE `USER_EXTENSION` SET `is_deleted` = 1 WHERE `user_id` = return_user_id;
		COMMIT;
		END IF;
	ELSE
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only CREATE, UPDATE and DELETE are supported as opperation type ID';
    END IF;
END$$
DELIMITER ;
