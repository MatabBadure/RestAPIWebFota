DROP PROCEDURE IF EXISTS create_patient_protocol_data;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `create_patient_protocol_data`(
	IN type_key varchar(15),
	IN t_patient_id varchar(45),
    IN in_created_by varchar(50),
	IN param_table varchar(50)
)
BEGIN
	DECLARE temp_user_id bigint(20);
	DECLARE protocol_id varchar(45) ;
	DECLARE created_by varchar(255);
	DECLARE created_date datetime;


	DECLARE temp_protocal_id varchar(45); 
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
    
	
    
	DECLARE done INT;
    
	DECLARE temp_table_cursor_mannesota CURSOR FOR  SELECT * FROM param_table WHERE `type` = type_key;
	DECLARE temp_table_cursor_custom CURSOR FOR  SELECT * FROM param_table WHERE `type` = type_key;
	DECLARE CONTINUE HANDLER for not found set done= 1;
	DECLARE EXIT HANDLER FOR SQLEXCEPTION
	BEGIN
		ROLLBACK;
        RESIGNAL;
    END;
	SET created_date = now();
    
    call get_next_protocol_hillromid(@gen_protocol_id);

	SELECT `user_id` INTO temp_user_id FROM `USER_PATIENT_ASSOC` WHERE `patient_id`= t_patient_id AND `user_role` = 'PATIENT';

	IF type_key = 'S' THEN

		INSERT INTO PATIENT_PROTOCOL_DATA
		( `id`,`patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,`min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`,
		`max_frequency`, `min_pressure`, `max_pressure`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, t_patient_id,temp_user_id,type_key,'2','',10,20,10,14,10,10,created_by,created_date ,in_created_by,created_date,0,@gen_protocol_id);


-- For Mannesota
	ELSEIF type_key = 'M' THEN 
		SET done = 0;
		
		open temp_table_cursor_mannesota;
		
		table_loop: loop
        IF done = 1 then leave table_loop; end if;
        
		FETCH temp_table_cursor_mannesota INTO temp_protocal_id, temp_patient_id, temp_type_key, temp_treatments_per_day , temp_treatment_label , temp_min_minutes_per_treatment , temp_max_minutes_per_treatment, temp_min_frequency, temp_max_frequency , temp_min_pressure, temp_max_pressure,temp_protocol_key;

		call get_next_protocol_hillromid(@gen_protocol_id);
        INSERT INTO PATIENT_PROTOCOL_DATA
		( `id`,  `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_pressure`, 
        `max_pressure`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, t_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_pressure,
        temp_max_pressure,created_by,created_date ,in_created_by ,created_date,0,@gen_protocol_id); 
		end loop table_loop;
		close temp_table_cursor_mannesota;

-- For customer single step
	ELSEIF type_key = 'SO' THEN 
    	SET done = 0;
    
		open temp_table_cursor_custom;
		START TRANSACTION;
        
		table_loop: loop
		FETCH temp_table_cursor_custom INTO temp_protocal_id, temp_patient_id, temp_type_key, temp_treatments_per_day , temp_treatment_label , temp_min_minutes_per_treatment , temp_max_minutes_per_treatment, temp_min_frequency, temp_max_frequency , temp_min_pressure, temp_max_pressure,temp_protocol_key;
		IF done = 1 then leave table_loop; end if;

		call get_next_protocol_hillromid(@gen_protocol_id);
        INSERT INTO PATIENT_PROTOCOL_DATA
		( `id`,  `patient_id`,`user_id`, `type`, `treatments_per_day`,`treatment_label`,
        `min_minutes_per_treatment`,`max_minutes_per_treatment`,`min_frequency`, `max_frequency`, `min_pressure`, 
        `max_pressure`,`created_by`,`created_date`, `last_modified_by`,`last_modified_date`,`is_deleted`,
		`protocol_key`)
		VALUES
		(@gen_protocol_id, t_patient_id, temp_user_id, type_key, temp_treatments_per_day, temp_treatment_label, 
        temp_min_minutes_per_treatment, temp_max_minutes_per_treatment, temp_min_frequency,temp_max_frequency,temp_min_pressure,
        temp_max_pressure,created_by,created_date ,in_created_by ,created_date,0,@gen_protocol_id); 
		end loop table_loop;
		COMMIT;
        close temp_table_cursor_custom;
		
		ELSE
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Only S, M and SO are supported as type_key';
	END IF;
END$$
DELIMITER ;
