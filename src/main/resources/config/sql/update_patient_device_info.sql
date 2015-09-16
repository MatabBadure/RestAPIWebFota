DROP PROCEDURE IF EXISTS `update_patient_device_info`;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_patient_device_info`(
    IN patient_id varchar(50), 
    IN pat_hub_id varchar(50), 
    IN pat_device_serial_number varchar(50), 
	IN pat_bluetooth_id varchar(50)
    )
BEGIN

DECLARE today_date date;
DECLARE temp_serial_number VARCHAR(50);
DECLARE temo_patient_info_id VARCHAR(50);
DECLARE created_by VARCHAR(50);


SET today_date = now();
SET created_by = 'System';

-- check if same serial number exists for the patient
SELECT `id`, `serial_number` INTO temo_patient_info_id, temp_serial_number FROM `patient_info`
		WHERE `id`= patient_id AND `serial_number`=pat_device_serial_number ;

IF temp_serial_number IS NOT NULL THEN

-- if serial number exists for patient, update patient_info and patient_vest_device_history tables 
START TRANSACTION;
	UPDATE `patient_info` SET
	`hub_id` = pat_hub_id,
	`bluetooth_id` = pat_bluetooth_id
	WHERE `id` = patient_id;
    
    UPDATE `patient_vest_device_history` SET
	`bluetooth_id` = pat_bluetooth_id,
	`hub_id` = pat_hub_id,
	`last_modified_date` = today_date
	 WHERE `patient_id` = patient_id AND `serial_number` = pat_device_serial_number;
	COMMIT;
ELSE 
-- if serial number does not exist for patient
-- update patient_info table 
-- make other devide inactive in patient_vest_device_history tables 
-- make insert device into patient_vest_device_history with active.

  START TRANSACTION;
  
  -- update patient_info table 
  
	UPDATE `patient_info` SET
	`hub_id` = pat_hub_id,
	`serial_number` = pat_device_serial_number,
	`bluetooth_id` = pat_bluetooth_id,
	`device_assoc_date`= today_date WHERE `id` = patient_id;
    
    -- make other devices inactive in patient_vest_device_history tables 
    
	UPDATE `patient_vest_device_history` SET
	`is_active` = 0 WHERE `patient_id` = patient_id;
    
     -- make insert device into patient_vest_device_history with active.
     
    INSERT INTO `patient_vest_device_history`
		(`patient_id`, `serial_number`,	`bluetooth_id`,	`hub_id`, `created_by`, `created_date`, `last_modified_by`, `last_modified_date`, `is_active`)
		VALUES
		(patient_id,pat_device_serial_number, pat_bluetooth_id,pat_hub_id,created_by,today_date,created_by,today_date,1);
        
  COMMIT;
END IF;

END$$
DELIMITER ;