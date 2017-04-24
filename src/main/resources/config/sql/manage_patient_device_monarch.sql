delimiter //
CREATE PROCEDURE `manage_patient_device_monarch`(
	IN operation_type_indicator VARCHAR(10),
    IN patient_id varchar(50), 
    IN pat_device_serial_number varchar(50)
    )
BEGIN

DECLARE today_date date;
DECLARE temp_serial_number VARCHAR(50);
DECLARE temp_patient_info_id VARCHAR(50);
DECLARE created_by VARCHAR(50);
DECLARE latest_hmr DECIMAL(10,0);
DECLARE pvdhm_patient_id VARCHAR(50); 
DECLARE pvdhm_serial_number VARCHAR(50); 
DECLARE pvdhm_is_active VARCHAR(10);



SET today_date = now();
SET created_by = 'JDE APP';

-- check if same serial number or bluetooth_id exists for any patient

        
IF operation_type_indicator = 'CREATE' THEN

	SELECT `id`, `serial_number` INTO temp_patient_info_id, temp_serial_number FROM `PATIENT_INFO`
	WHERE `serial_number` = pat_device_serial_number;

	SELECT `patient_id`, `serial_number`, `is_active` INTO pvdhm_patient_id, pvdhm_serial_number, pvdhm_is_active FROM `PATIENT_VEST_DEVICE_HISTORY_MONARCH`
	WHERE `serial_number` = pat_device_serial_number;
	
	IF temp_patient_info_id IS NOT NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No. already associated with a patient';
	END IF;
    
	START TRANSACTION;
	  
	  -- update patient_info table 
	  
		UPDATE `PATIENT_INFO` SET
		`serial_number` = pat_device_serial_number,
		`device_assoc_date`= today_date WHERE `id` = patient_id;
		
		-- make other devices inactive in patient_vest_device_history_monarch tables 

		UPDATE `PATIENT_VEST_DEVICE_HISTORY_MONARCH` pvdhm SET
		`is_active` = 0 WHERE pvdhm.`patient_id` = patient_id;
		
		 -- make insert device into patient_vest_device_history_monarch with active.

		IF pvdhm_is_active IS NOT NULL THEN
			IF pvdhm_is_active =0 THEN
					UPDATE `PATIENT_VEST_DEVICE_HISTORY_MONARCH` pvdhm SET
					`is_active` = 1 WHERE pvdhm.`serial_number` = pvdhm_serial_number;
			END IF;
		ELSE 		 
			INSERT INTO `PATIENT_VEST_DEVICE_HISTORY_MONARCH`
				(`patient_id`, `serial_number`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`, `is_active`,`hmr`)
				VALUES
				(patient_id,pat_device_serial_number, created_by,today_date,created_by,today_date,1,0);
		END IF;		
			
	  COMMIT;
      
ELSEIF operation_type_indicator ='UPDATE' THEN

		SELECT `id`, `serial_number` INTO temp_patient_info_id, temp_serial_number FROM `PATIENT_INFO`
		WHERE `serial_number` = pat_device_serial_number AND `id`= patient_id;
        IF temp_patient_info_id IS NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No. not associated with the patient';
		END IF;

-- if serial number exists for patient, update patient_info and patient_vest_device_history_monarch tables 
		START TRANSACTION;
        
			-- UPDATE `PATIENT_INFO` SET
			-- WHERE `id` = patient_id;
			
			UPDATE `PATIENT_VEST_DEVICE_HISTORY_MONARCH` pvdhm SET
			`last_modified_date` = today_date
			 WHERE pvdhm.`patient_id` = patient_id AND `serial_number` = pat_device_serial_number;
			COMMIT;
            
ELSEIF operation_type_indicator ='INACTIVATE' THEN

		SELECT `id`, `serial_number` INTO temp_patient_info_id, temp_serial_number FROM `PATIENT_INFO`
		WHERE `serial_number` = pat_device_serial_number  AND `patient_id` = patient_id;
        
        IF temp_patient_info_id IS NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No.  not associated with the patient';
		END IF;
        START TRANSACTION;
			SELECT max(hmr) INTO latest_hmr FROM PATIENT_VEST_DEVICE_DATA_MONARCH
			WHERE patient_id = patient_id AND serial_number = pat_device_serial_number;

			UPDATE `PATIENT_INFO` SET
            `serial_number`=null
			WHERE `id` = patient_id;
			
			UPDATE `PATIENT_VEST_DEVICE_HISTORY_MONARCH` pvdhm SET
			`is_active` = 0, `hmr` = IFNULL(latest_hmr,0), 
			`last_modified_by` = created_by,
			`last_modified_date` = today_date
			WHERE pvdhm.`patient_id` = patient_id
			AND serial_number = pat_device_serial_number;
			
			UPDATE `PATIENT_DEVICES_ASSOC` 
			SET `is_active` = 0
            WHERE `patient_id` = patient_id and `serial_number` = pat_device_serial_number;
            			
		COMMIT;
ELSE  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Operation not supported';
END IF;
END
//
delimiter ;