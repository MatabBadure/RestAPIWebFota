CREATE DEFINER=`root`@`%` PROCEDURE `manage_patient_device`(
	IN operation_type_indicator VARCHAR(10),
    IN patient_id varchar(50), 
    IN pat_device_serial_number varchar(50), 
	IN pat_bluetooth_id varchar(50),
    IN pat_hub_id varchar(50)
    )
BEGIN

DECLARE today_date date;
DECLARE temp_serial_number VARCHAR(50);
DECLARE temp_patient_info_id VARCHAR(50);
DECLARE temp_bluetooth_id VARCHAR(50);
DECLARE created_by VARCHAR(50);


SET today_date = now();
SET created_by = 'System';

-- check if same serial number or bluetooth_id exists for any patient

        
IF operation_type_indicator = 'CREATE' THEN

	SELECT `id`, `serial_number`, `bluetooth_id` INTO temp_patient_info_id, temp_serial_number, temp_bluetooth_id FROM `PATIENT_INFO`
	WHERE `serial_number` = pat_device_serial_number OR `bluetooth_id` =  pat_bluetooth_id;

	IF temp_patient_info_id IS NOT NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No. or Bluetooth ID already associated with a patient';
	END IF;
    
	START TRANSACTION;
	  
	  -- update patient_info table 
	  
		UPDATE `PATIENT_INFO` SET
		`hub_id` = pat_hub_id,
		`serial_number` = pat_device_serial_number,
		`bluetooth_id` = pat_bluetooth_id,
		`device_assoc_date`= today_date WHERE `id` = patient_id;
		
		-- make other devices inactive in patient_vest_device_history tables 
		
		UPDATE `PATIENT_VEST_DEVICE_HISTORY` SET
		`is_active` = 0 WHERE `patient_id` = patient_id;
		
		 -- make insert device into patient_vest_device_history with active.
		 
		INSERT INTO `PATIENT_VEST_DEVICE_HISTORY`
			(`patient_id`, `serial_number`,	`bluetooth_id`,	`hub_id`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`, `is_active`)
			VALUES
			(patient_id,pat_device_serial_number, pat_bluetooth_id,pat_hub_id,created_by,today_date,created_by,today_date,1);
			
	  COMMIT;
      
ELSEIF operation_type_indicator ='UPDATE' THEN

		SELECT `id`, `serial_number`, `bluetooth_id` INTO temp_patient_info_id, temp_serial_number, temp_bluetooth_id FROM `PATIENT_INFO`
		WHERE `serial_number` = pat_device_serial_number AND `id`= patient_id;
        
        IF temp_patient_info_id IS NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No. not associated with the patient';
		END IF;

-- if serial number exists for patient, update patient_info and patient_vest_device_history tables 
		START TRANSACTION;
			UPDATE `PATIENT_INFO` SET
			`hub_id` = pat_hub_id,
			`bluetooth_id` = pat_bluetooth_id
			WHERE `id` = patient_id;
			
			UPDATE `PATIENT_VEST_DEVICE_HISTORY` SET
			`bluetooth_id` = pat_bluetooth_id,
			`hub_id` = pat_hub_id,
			`last_modified_date` = today_date
			 WHERE `patient_id` = patient_id AND `serial_number` = pat_device_serial_number;
			COMMIT;
            
ELSEIF operation_type_indicator ='INACTIVATE' THEN

		SELECT `id`, `serial_number`, `bluetooth_id` INTO temp_patient_info_id, temp_serial_number, temp_bluetooth_id FROM `PATIENT_INFO`
		WHERE `serial_number` = pat_device_serial_number  AND `patient_id` = patient_id;
        
        IF temp_patient_info_id IS NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No.  not associated with the patient';
		END IF;
        START TRANSACTION;
			UPDATE `PATIENT_INFO` SET
            `serial_number`=null,
			`hub_id` = null,
			`bluetooth_id` = null
			WHERE `id` = patient_id;
			
			UPDATE `PATIENT_VEST_DEVICE_HISTORY` SET
			`is_active` = 0 WHERE `patient_id` = patient_id;
		COMMIT;
ELSE  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Operation not supported';
END IF;
END