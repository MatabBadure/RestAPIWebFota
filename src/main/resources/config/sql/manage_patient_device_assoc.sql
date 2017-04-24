DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `manage_patient_device_assoc`(
	IN operation_type_indicator VARCHAR(10),
    IN pat_patient_id varchar(50),
	IN pat_device_type varchar(50),
	IN pat_device_is_active varchar(10),	
	IN pat_device_serial_number varchar(50),
	IN pat_hillrom_id varchar(50),
	IN pat_old_id varchar(50),
	IN pat_training_date datetime,
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
DECLARE vest_device_patient_id VARCHAR(50);
DECLARE device_patient_type VARCHAR(50);
DECLARE temp_device_type VARCHAR(50);
DECLARE vest_device_hillrom_id  VARCHAR(50);
DECLARE device_hillrom_id  VARCHAR(50);
DECLARE temp_patient_info_id  VARCHAR(50);

DECLARE created_by VARCHAR(50);
DECLARE latest_hmr DECIMAL(10,0);
DECLARE no_of_rec integer(10);


SET today_date = now();
SET created_by = 'JDE APP';
SET device_patient_type = 'SD';

-- All cases from Leah's document "VisiView Phase 3 Test Cases for TIMs Integration"

        
IF operation_type_indicator = 'CREATE' THEN
		-- Case 1 : New Monarch System (initially created from TIMs)
		-- Case 3 :	New Monarch, Existing VisiVest (initially created from TIMs)
		-- Also applicable for
		-- Case 5 :	New Monarch, Existing VisiVest (initially created from TIMs) and VisiVest protocol Inactive
		-- Case 6 :	New Monarch, Existing VisiVest (initially created from TIMs) and VisiVest protocol Inactive without Monarch protocol
    
		SELECT `patient_id`, `serial_number` ,`patient_type` ,`device_type`,`hillrom_id` INTO temp_patient_info_id, temp_serial_number ,device_patient_type , temp_device_type,device_hillrom_id
		FROM `PATIENT_DEVICES_ASSOC`
		WHERE `patient_id` = pat_patient_id;
		
		IF  pat_device_type = 'MONARCH' AND device_hillrom_id IS NOT NULL THEN
		
				SELECT `hillrom_id`,`patient_id` INTO vest_device_hillrom_id ,vest_device_patient_id FROM `PATIENT_DEVICES_ASSOC` 
				WHERE `hillrom_id` = pat_hillrom_id AND `device_type` = 'VEST';
				
				IF  vest_device_hillrom_id IS NOT NULL THEN
				
					START TRANSACTION;
					
						UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
						`patient_type` ='CD'
						where PVDA.`hillrom_id` =  pat_hillrom_id 
						AND PVDA.`device_type` = 'VEST' ;

						SELECT  `patient_id` ,`serial_number` ,`device_type` INTO temp_patient_info_id , temp_serial_number , temp_device_type
						FROM `PATIENT_DEVICES_ASSOC` WHERE `serial_number` = pat_device_serial_number AND `device_type` = 'MONARCH';
						
						IF temp_patient_info_id <> pat_patient_id AND temp_serial_number = pat_device_serial_number 
							AND temp_device_type = 'MONARCH' THEN
							
							UPDATE `PATIENT_INFO` SET
							`expired` = 1,
							`expired_date` = today_date
							WHERE `id` = temp_patient_info_id;
							
							UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
							`patient_id` = pat_patient_id,
							`hillrom_id` =  pat_hillrom_id,
							`patient_type` ='CD', 
							`modified_date` = today_date,
							`old_patient_id` = temp_patient_info_id,
							`diagnosis1` = pat_diagnosis_code1,
							`diagnosis2` = pat_diagnosis_code2,
							`diagnosis3` = pat_diagnosis_code3,
							`diagnosis4` = pat_diagnosis_code4,
							`garment_type` = pat_garment_type,
							`garment_size` = pat_garment_size,
							`garment_color` = pat_garment_color
							where PVDA.`serial_number` = pat_device_serial_number  
							AND  (PVDA.`hillrom_id` = '' OR PVDA.`hillrom_id` IS NULL) AND PVDA. `patient_type` = 'SD' 
							AND PVDA.`device_type` = 'MONARCH' ;
							
						ELSE
						
							INSERT INTO `PATIENT_DEVICES_ASSOC`
							(`patient_id`, `device_type`, `is_active`, `serial_number`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
							`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
							VALUES	(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hillrom_id,'CD',today_date,today_date,pat_old_id,pat_training_date,pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);
							
						END IF;
						
					COMMIT;						

				ELSE
				
					START TRANSACTION;
				
						INSERT INTO `PATIENT_DEVICES_ASSOC`
						(`patient_id`, `device_type`, `is_active`, `serial_number`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
						`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
						VALUES
						(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hillrom_id,device_patient_type,today_date,null,pat_old_id,pat_training_date,pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);
					
					COMMIT;
				END IF;
		
		ELSE
			START TRANSACTION;
				
				INSERT INTO `PATIENT_DEVICES_ASSOC`
				(`patient_id`, `device_type`, `is_active`, `serial_number`, `hillrom_id`, `patient_type`, `created_date`, `modified_date`,
				`old_patient_id`,`training_date`,`diagnosis1`,`diagnosis2`,`diagnosis3`,`diagnosis4`,`garment_type`,`garment_size`,`garment_color`)
				VALUES
				(pat_patient_id,pat_device_type,1,pat_device_serial_number,pat_hillrom_id,device_patient_type,today_date,null,pat_old_id,pat_training_date,
				pat_diagnosis_code1,pat_diagnosis_code2,pat_diagnosis_code3,pat_diagnosis_code4,pat_garment_type,pat_garment_size,pat_garment_color);	
				
			COMMIT;
			
		END IF;
      
ELSEIF operation_type_indicator ='UPDATE' THEN

		SELECT `patient_id`, `serial_number` ,`patient_type` ,`device_type`,`hillrom_id` INTO temp_patient_info_id, temp_serial_number ,device_patient_type , temp_device_type,device_hillrom_id
		FROM `PATIENT_DEVICES_ASSOC`
		WHERE `patient_id` = pat_patient_id;
		
		IF temp_patient_info_id IS NULL THEN
			SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Device Serial No.  not associated with the patient';
		END IF;


		-- Case 4:	New Monarch, Existing VisiVest (initially created from VisiView.with hillrom_id null).Subsequently following details coming in from TIMS
		IF temp_serial_number =  pat_device_serial_number AND  device_patient_type ='SD' AND temp_device_type = 'MONARCH' AND device_hillrom_id = null THEN
		
			SELECT `hillrom_id`,`patient_id` INTO vest_device_hillrom_id ,vest_device_patient_id FROM `PATIENT_DEVICES_ASSOC` WHERE `hillrom_id` = pat_hillrom_id AND `device_type` = 'VEST' AND `patient_type` = 'SD';
			
			IF  temp_patient_info_id IS NOT NULL THEN
				START TRANSACTION;
				
					UPDATE PATIENT_DEVICES_ASSOC PVDA SET 
					`patient_id` = vest_device_patient_id,
					`hillrom_id` = vest_device_hillrom_id ,
					`patient_type` ='CD', 
					`modified_date` = today_date,
					`old_patient_id` = pat_patient_id,
					`training_date` = pat_training_date,
					`diagnosis1` = pat_diagnosis_code1,
					`diagnosis2` = pat_diagnosis_code2,
					`diagnosis3` = pat_diagnosis_code3,
					`diagnosis4` = pat_diagnosis_code4,
					`garment_type` = pat_garment_type,
					`garment_size` = pat_garment_size,
					`garment_color` = pat_garment_color
					where PVDA.`serial_number` = pat_device_serial_number  
					AND  (PVDA.`hillrom_id` = '' OR PVDA.`hillrom_id` IS NULL) AND PVDA. `patient_type` = device_patient_type 
					AND PVDA.`device_type` = 'MONARCH' ;
					
					UPDATE `PATIENT_INFO` SET
					`expired` = 1,
					`expired_date` = today_date
					WHERE `id` = pat_patient_id;
			
				COMMIT;
			END IF;
			
		ELSE 
		
			-- Case 2: New Monarch System (initially created from VisiView). Subsequently following details coming in from TIMS
			IF temp_patient_info_id IS NOT NULL AND temp_serial_number IS NOT NULL  THEN
				START TRANSACTION;
			
				UPDATE `PATIENT_DEVICES_ASSOC` pvda SET
				`patient_id` = pat_patient_id,
				`device_type` = pat_device_type,
				`is_active` = 1,
				`serial_number` = pat_device_serial_number,
				`hillrom_id` = pat_hillrom_id,
				`patient_type` = device_patient_type,
				`old_patient_id` = pat_old_id,
				`training_date` = pat_training_date,
				`diagnosis1` = pat_diagnosis_code1,
				`diagnosis2` = pat_diagnosis_code2,
				`diagnosis3` = pat_diagnosis_code3,
				`diagnosis4` = pat_diagnosis_code4,
				`garment_type` = pat_garment_type,
				`garment_size` = pat_garment_size,
				`garment_color` = pat_garment_color
				 WHERE pvda.`patient_id` = pat_patient_id AND pvda.`serial_number` = pat_device_serial_number;
				 
				COMMIT;
			END IF;
		
		END IF;
ELSE  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Operation not supported';
END IF;
END$$
DELIMITER ;