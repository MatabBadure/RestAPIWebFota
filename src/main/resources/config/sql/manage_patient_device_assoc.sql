delimiter //
CREATE PROCEDURE `manage_patient_device_assoc`(
	IN operation_type_indicator VARCHAR(10),
    IN patient_id varchar(50),
	IN pat_device_type varchar(50),
	IN pat_device_is_active varchar(10),	
	IN pat_device_serial_number varchar(50),
	IN pat_hillrom_id varchar(50),
	IN pat_patient_type varchar(50),
	IN training_date datetime,
	IN pat_diagnosis_code1 varchar(255),
	IN pat_diagnosis_code2 varchar(255),
	IN pat_diagnosis_code3 varchar(255),
	IN pat_diagnosis_code4 varchar(255),
	IN pat_garment_type varchar(255),
    IN pat_garment_size varchar(255),
    IN pat_garment_color varchar(255)
    )
BEGIN

DECLARE today_date date;
DECLARE temp_serial_number VARCHAR(50);
DECLARE temp_patient_info_id VARCHAR(50);
DECLARE created_by VARCHAR(50);
DECLARE latest_hmr DECIMAL(10,0);



SET today_date = now();
SET created_by = 'JDE APP';


-- 

        
IF operation_type_indicator = 'CREATE' THEN

    
	START TRANSACTION;
	  
		
		 -- make insert device into PATIENT_DEVICES_ASSOC
		 
		INSERT INTO `PATIENT_DEVICES_ASSOC`
			(`patient_id`, `device_type`, `is_active`, `serial_number`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`)
			VALUES
			(patient_id,pat_device_type,1,pat_device_serial_number,pat_hillrom_id,pat_patient_type,today_date,today_date);			
			
	  COMMIT;
      
ELSEIF operation_type_indicator ='UPDATE' THEN

		SELECT `patient_id`, `serial_number` INTO temp_patient_info_id, temp_serial_number FROM `PATIENT_DEVICES_ASSOC`
		WHERE `serial_number` = pat_device_serial_number AND `patient_id`= patient_id;
        IF temp_patient_info_id IS NULL THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No. not associated with the patient';
		END IF;

-- if serial number exists for patient, update patient_info and patient_vest_device_history_monarch tables 
		START TRANSACTION;
        
			
			UPDATE `PATIENT_DEVICES_ASSOC` pvda SET
			`patient_id` = patient_id,
			`device_type` = pat_device_type,
			`is_active` = 1,
			`serial_number` = pat_device_serial_number,
			`hillrom_id` = pat_hillrom_id,
			`patient_type` = pat_patient_type,
			`modified_date` = today_date
			 WHERE pvda.`patient_id` = patient_id AND pvda.`serial_number` = pat_device_serial_number;
			COMMIT;


ELSE  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Operation not supported';
END IF;
END

//
delimiter ;

-- The above section will be updated with the logic below to take care of
-- Seven cases from the current TIMS script.

-- if(pat_patient_Type == SD){
--	if (pat_device_type == VEST) {
--		if(operation_type == UPDATE){
--			SELECT `hillromid` INTO temp_hillrom_id FROM `PATIENT_DEVICES_ASSOC` WHERE `hillromid` = pat_hillromid;
--			IF temp_hillrom_id IS NOT NULL THEN
--				update PVDA.serial number where device_type='VEST' and hillromid=hillromid
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == SD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == UPDATE){
--			SELECT `hillromid` INTO temp_hillrom_id FROM `PATIENT_DEVICES_ASSOC` WHERE `hillromid` = pat_hillromid;
--			IF temp_hillrom_id IS NOT NULL THEN
--				update PVDA.serial number where device_type='MONARCH' and hillromid=hillromid
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == SD){
--	if (pat_device_type == VEST) {
--		if(operation_type == CREATE){
--			insert into PVDA values (patient_id, 'VEST',serial_number,hillrom_id,'Y')
--			
--		}
--	}
-- }

-- if(pat_patient_Type == SD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == CREATE){
--			insert into PVDA values (patient_id, 'MONARCH',serial_number,hillrom_id,'Y')
--			
--		}
--	}
-- }


-- if(pat_patient_Type == SD){
--	if (pat_device_type == VEST) {
--		if(operation_type == UPDATE){
--			SELECT `serial_number` INTO temp_serial_number FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_serial_number;
--			IF temp_serial_number IS NOT NULL THEN
--				update PVDA.hillromid where device_type='VEST' and serial_number=serial_number
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == SD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == UPDATE){
--			SELECT `serial_number` INTO temp_serial_number FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_serial_number;
--			IF temp_serial_number IS NOT NULL THEN
--				update PVDA.hillromid where device_type='MONARCH' and serial_number=serial_number
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == VEST) {
--		if(operation_type == UPDATE){
--			SELECT `hillromid` INTO temp_hillrom_id FROM `PATIENT_DEVICES_ASSOC` WHERE `hillromid` = pat_hillromid;
--			IF temp_hillrom_id IS NOT NULL THEN
--				update PVDA.serial number where device_type ='VEST' and hillromid=hillromid
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == UPDATE){
--			SELECT `hillromid` INTO temp_hillrom_id FROM `PATIENT_DEVICES_ASSOC` WHERE `hillromid` = pat_hillromid;
--			IF temp_hillrom_id IS NOT NULL THEN
--				update PVDA.serial number where device_type = 'MONARCH' and hillromid=hillromid
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == VEST) {
--		if(operation_type == UPDATE){
--			SELECT `serial_number` INTO temp_serial_number FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_serial_number;
--			IF temp_serial_number IS NOT NULL THEN
--				update PVDA.hillromid where device_type ='VEST' and serial_number=serial_number
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == UPDATE){
--			SELECT `serial_number` INTO temp_serial_number FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_serial_number;
--			IF temp_serial_number IS NOT NULL THEN
--				update PVDA.hillromid where device_type = 'MONARCH' and serial_number=serial_number
--			END IF
--
--		}
--	}
-- }


-- if(pat_patient_Type == CD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == CREATE){
--			
--				 insert into PVDA values (patient_id, 'MONARCH',serial_number,hillrom_id,'Y')
--			
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == VEST) {
--		if(operation_type == CREATE){
--			
--				 insert into PVDA values (patient_id, 'VEST',serial_number,hillrom_id,'Y')
--			
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == VEST) {
--		if(operation_type == UPDATE){
--			SELECT `serial_number`,`is_active` INTO temp_serial_number,temp_is_active FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_serial_number;
--			IF temp_serial_number IS NOT NULL AND temp_is_active = 'Y'  AND pat_device_is_active = 'N' THEN
--				update PVDA.is_active = 'N' where device_type ='VEST' and serial_number=serial_number
--			END IF
--
--		}
--	}
-- }

-- if(pat_patient_Type == CD){
--	if (pat_device_type == MONARCH) {
--		if(operation_type == UPDATE){
--			SELECT `serial_number`,`is_active` INTO temp_serial_number,temp_is_active FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_serial_number;
--			IF temp_serial_number IS NOT NULL AND temp_is_active = 'Y'  AND pat_device_is_active = 'N'  THEN
--				update PVDA.is_active = 'N' where device_type = 'MONARCH' and serial_number=serial_number
--			END IF
--
--		}
--	}
-- }