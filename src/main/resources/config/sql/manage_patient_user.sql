DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `manage_patient_user`(
	IN operation_type_indicator INT,
    IN hr_id varchar(15),
	IN pat_hub_id varchar(50),
	IN pat_bluetooth_id varchar(50),
    IN pat_device_serial_number varchar(10), 
    IN pat_title varchar(50),
    IN pat_first_name varchar(50),
    IN pat_middle_name varchar(50),
    IN pat_last_name varchar(50),
	IN pat_dob date,
    IN pat_email varchar(50),
    IN pat_zipcode varchar(10),
    IN pat_primary_phone varchar(20),
    IN pat_mobile_phone varchar(20),
    IN pat_gender varchar(5),
    IN pat_lang_key varchar(10),
	IN pat_address varchar(100),
    IN pat_city varchar(50),
    IN pat_state varchar(10),
    OUT return_pateint_id varchar(50),
    OUT return_user_id varchar(50)
)
proc_label:BEGIN


    DECLARE temp_hillrom_id varchar(15);
    DECLARE created_by varchar(255);
    DECLARE today_date datetime;
    DECLARE encrypted_password varchar(225);-- = NULL ;-- get_encrypted_password();
    DECLARE gen_patient_id varchar(50);
    DECLARE temp_user_id varchar(50);

  -- This is a block of final values that are used in the procedure.
	SET created_by = 'system';
	SET today_date = curdate();
    
    SELECT  `id`,`hillrom_id` INTO return_pateint_id, temp_hillrom_id FROM `patient_info` WHERE `serial_number` = pat_device_serial_number;
    
    IF temp_hillrom_id IS NOT NULL THEN
        SELECT  user_id INTO return_user_id FROM `user_patient_assoc` WHERE `patient_id` = return_pateint_id AND relation_label='SELF';
        LEAVE proc_label;
    END IF;

-- Calling procedure to get the next hillrom_id for patient

	START TRANSACTION;
	call get_next_patient_hillromid(@gen_patient_id);
    
    

	INSERT INTO `patient_info` (`id`, `hillrom_id`, `hub_id`, `serial_number`, `bluetooth_id`, `title`, `first_name`, `middle_name`,
    `last_name`, `dob`, `email`, `zipcode`, `web_login_created`, `primary_phone`, `mobile_phone`, `gender`, `lang_key`, `expired`, `expired_date`, `address`, `city`, `state`)
	VALUES
	(@gen_patient_id, hr_id, pat_hub_id, pat_device_serial_number, pat_bluetooth_id, pat_title, pat_first_name, pat_middle_name,
		pat_last_name, pat_dob, pat_email, pat_zipcode,0, pat_primary_phone, pat_mobile_phone, pat_gender, pat_lang_key, 0, NULL, pat_address, pat_city, pat_state);

	INSERT INTO `user`(
    `email`, `PASSWORD`, `title`, `first_name`, `middle_name`, `last_name`, `activated`, `lang_key`, `activation_key`, `reset_key`, 
    `created_by`, `created_date`, `reset_date`, `last_loggedin_at`, `last_modified_by`, 
    `last_modified_date`, `is_deleted`, `gender`, `zipcode`, `terms_condition_accepted`, 
    `terms_condition_accepted_date`, `dob`, `hillrom_id`)
	VALUES(
    pat_email, encrypted_password, pat_title, pat_first_name, pat_middle_name, pat_last_name, 0, pat_lang_key,NULL, NULL,
    created_by, today_date, NULL, NULL, created_by, 
    today_date, 0, pat_gender, pat_zipcode,0,
    NULL, pat_dob, hr_id );
     
    SELECT id INTO return_user_id FROM `user` WHERE email = pat_email;
    
    INSERT INTO `user_extension` (`user_id`,`address`,`city`,`state`,`is_deleted`)
    VALUES (return_user_id, pat_address, pat_city, pat_state,0);
	
    INSERT INTO `user_patient_assoc` (`user_id`,  `patient_id`, `user_role`, `relation_label`)
	VALUES(return_user_id,@gen_patient_id,'PATIENT','SELF');
    COMMIT;
    
    SET return_pateint_id = @gen_patient_id;
END$$
DELIMITER ;
