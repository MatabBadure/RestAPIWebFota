DROP TRIGGER IF EXISTS trainingDateAfterUpdate;
DROP TRIGGER IF EXISTS trainingDateAfterInsert;

DELIMITER //
CREATE TRIGGER trainingDateAfterUpdate AFTER UPDATE ON PATIENT_DEVICES_ASSOC
FOR EACH ROW
BEGIN
   
	DECLARE temp_vest_first_transmission_date date;
    DECLARE temp_vest_user_id bigint(60);
    DECLARE temp_vest_user_created_date date;
	DECLARE temp_vest_old_Patient_id varchar(255);
   
	DECLARE temp_monarch_first_transmission_date date;
    DECLARE temp_monarch_user_id bigint(60);
    DECLARE temp_monarch_user_created_date date;
	DECLARE temp_monarch_old_Patient_id varchar(255);
	
	
	INSERT INTO trigger_test(message,value) VALUES ('Start Date Time Monarch',curdate());	
	INSERT INTO trigger_test(message,value) VALUES ('OLD TRAINING DATE',OLD.training_date);
	INSERT INTO trigger_test(message,value) VALUES ('New TRAINING DATE',NEW.training_date);
	INSERT INTO trigger_test(message,value) VALUES ('OLD Patinet Id',OLD.patient_id);
	INSERT INTO trigger_test(message,value) VALUES ('OLD.DEVICE_TYPE',OLD.DEVICE_TYPE);
	
	INSERT INTO trigger_test(message,value) VALUES ('OLD.DEVICE_TYPE',OLD.DEVICE_TYPE);
    INSERT INTO trigger_test(message,value) VALUES ('OLD.patient_type',OLD.patient_type);
	INSERT INTO trigger_test(message,value) VALUES ('OLD.old_patient_id',OLD.old_patient_id);
	INSERT INTO trigger_test(message,value) VALUES ('NEW.patient_type',NEW.patient_type);
	INSERT INTO trigger_test(message,value) VALUES ('NEW.old_patient_id',NEW.old_patient_id);
    SET temp_monarch_old_Patient_id = (SELECT patient_id FROM PATIENT_NO_EVENT_MONARCH WHERE patient_id = NEW.old_patient_id);
	INSERT INTO trigger_test(message,value) VALUES ('temp_monarch_old_Patient_id',temp_monarch_old_Patient_id);	
	SET temp_vest_old_Patient_id = (SELECT patient_id FROM PATIENT_NO_EVENT WHERE patient_id = NEW.old_patient_id);
	INSERT INTO trigger_test(message,value) VALUES ('temp_vest_old_Patient_id',temp_vest_old_Patient_id);
	
	
	IF(( NEW.training_date <> OLD.training_date or
    (NEW.training_date is null and OLD.training_date is not null) or
    (NEW.training_date is not null and OLD.training_date is null)) AND OLD.DEVICE_TYPE = 'MONARCH' )THEN
		SET temp_monarch_first_transmission_date = (SELECT first_transmission_date FROM PATIENT_NO_EVENT_MONARCH WHERE patient_id = OLD.patient_id);
		
		SET temp_monarch_user_created_date = (SELECT user_created_date FROM PATIENT_NO_EVENT_MONARCH WHERE patient_id = OLD.patient_id);
		SET temp_monarch_user_id = (SELECT user_id FROM USER_PATIENT_ASSOC WHERE patient_id = OLD.patient_id and user_role='PATIENT' and relation_label = 'Self');

		INSERT INTO trigger_test(message,value) VALUES ('Get from DB temp_monarch_first_transmission_date',temp_monarch_first_transmission_date);
		
		 IF temp_monarch_first_transmission_date IS NULL THEN
			IF temp_monarch_user_created_date IS NULL THEN
				INSERT INTO PATIENT_NO_EVENT_MONARCH (first_transmission_date, user_created_date, patient_id, user_id,first_transmission_date_before_update,date_first_transmission_date_updated,first_trans_date_type)
						VALUES
						(NEW.training_date, curdate(),NEW.patient_id,temp_monarch_user_id,temp_monarch_first_transmission_date,NULL,'training');
			else
				UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date= NEW.training_date WHERE patient_id = OLD.patient_id; 
				UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date_before_update= temp_monarch_first_transmission_date WHERE patient_id = OLD.patient_id; 
				UPDATE PATIENT_NO_EVENT_MONARCH SET first_trans_date_type= 'training' WHERE patient_id = OLD.patient_id;
				
			END IF;
		 ELSE 
			IF ((NEW.training_date > temp_monarch_first_transmission_date) or (NEW.training_date <=> temp_monarch_first_transmission_date)) THEN
				UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date= NEW.training_date WHERE patient_id = OLD.patient_id; 
				UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date_before_update= temp_monarch_first_transmission_date WHERE patient_id = OLD.patient_id; 
				UPDATE PATIENT_NO_EVENT_MONARCH SET first_trans_date_type= 'training' WHERE patient_id = OLD.patient_id;
				IF(OLD.patient_type <=> 'SD') THEN
					UPDATE PATIENT_NO_EVENT_MONARCH SET date_first_transmission_date_updated= curdate() WHERE patient_id = OLD.patient_id;
				END IF;
				
			END IF;

		 END IF;

	END IF;
	
	IF(( NEW.training_date <> OLD.training_date or
    (NEW.training_date is null and OLD.training_date is not null) or
    (NEW.training_date is not null and OLD.training_date is null) )
	AND OLD.DEVICE_TYPE = 'VEST') THEN
		
		SET temp_vest_first_transmission_date = (SELECT first_transmission_date FROM PATIENT_NO_EVENT WHERE patient_id = OLD.patient_id);
		SET temp_vest_user_created_date = (SELECT user_created_date FROM PATIENT_NO_EVENT WHERE patient_id = OLD.patient_id);
		SET temp_vest_user_id = (SELECT user_id FROM USER_PATIENT_ASSOC WHERE patient_id = OLD.patient_id and user_role='PATIENT' and relation_label = 'Self');

		INSERT INTO trigger_test(message,value) VALUES ('Get from DB temp_vest_first_transmission_date',temp_vest_first_transmission_date);

		 IF temp_vest_first_transmission_date IS NULL THEN
			IF temp_vest_user_created_date IS NULL THEN
				INSERT INTO PATIENT_NO_EVENT (first_transmission_date, user_created_date, patient_id, user_id,first_transmission_date_before_update,date_first_transmission_date_updated,first_trans_date_type)
						VALUES
						(NEW.training_date, curdate(),OLD.patient_id,temp_vest_user_id,temp_vest_first_transmission_date,NULL,'training');
			else
				UPDATE PATIENT_NO_EVENT SET first_transmission_date= NEW.training_date WHERE patient_id = OLD.patient_id;
				UPDATE PATIENT_NO_EVENT SET first_transmission_date_before_update= temp_vest_first_transmission_date WHERE patient_id = OLD.patient_id;
				UPDATE PATIENT_NO_EVENT SET first_trans_date_type= 'training' WHERE patient_id = OLD.patient_id;				
			END IF;
		 ELSE 
			INSERT INTO trigger_test(message,value) VALUES ('NEW.training_date when eqaul',NEW.training_date);
			INSERT INTO trigger_test(message,value) VALUES ('temp_vest_first_transmission_date eqaul',temp_vest_first_transmission_date);
			IF ((NEW.training_date > temp_vest_first_transmission_date) or (NEW.training_date <=> temp_vest_first_transmission_date)) THEN
				UPDATE PATIENT_NO_EVENT SET first_transmission_date= NEW.training_date WHERE patient_id = OLD.patient_id;
				UPDATE PATIENT_NO_EVENT SET first_transmission_date_before_update= temp_vest_first_transmission_date WHERE patient_id = OLD.patient_id;
				UPDATE PATIENT_NO_EVENT SET first_trans_date_type= 'training' WHERE patient_id = OLD.patient_id;
				IF(OLD.patient_type <=> 'SD') THEN
					UPDATE PATIENT_NO_EVENT SET date_first_transmission_date_updated= curdate() WHERE patient_id = OLD.patient_id; 
				END IF;
					
			END IF;

		 END IF;
		
	END IF;
	
	INSERT INTO trigger_test(message,value) VALUES ('End Date Time Monarch',curdate());
END;//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trainingDateAfterInsert AFTER INSERT ON PATIENT_DEVICES_ASSOC
FOR EACH ROW
BEGIN
   
	DECLARE temp_vest_first_transmission_date date;
    DECLARE temp_vest_user_id bigint(60);
    DECLARE temp_vest_user_created_date date;
	
   
	DECLARE temp_monarch_first_transmission_date date;
    DECLARE temp_monarch_user_id bigint(60);
    DECLARE temp_monarch_user_created_date date;

SET temp_monarch_first_transmission_date = (SELECT first_transmission_date FROM PATIENT_NO_EVENT_MONARCH WHERE patient_id = NEW.patient_id);
SET temp_monarch_user_created_date = (SELECT user_created_date FROM PATIENT_NO_EVENT_MONARCH WHERE patient_id = NEW.patient_id);
SET temp_monarch_user_id = (SELECT user_id FROM USER_PATIENT_ASSOC WHERE patient_id = NEW.patient_id and user_role='PATIENT' and relation_label = 'Self');

If NEW.DEVICE_TYPE = 'MONARCH' THEN
 IF temp_monarch_first_transmission_date IS NULL THEN
	IF temp_monarch_user_created_date IS NULL THEN
		INSERT INTO PATIENT_NO_EVENT_MONARCH (first_transmission_date, user_created_date, patient_id, user_id,first_transmission_date_before_update,date_first_transmission_date_updated,first_trans_date_type)
				VALUES
				(NEW.training_date, curdate(),NEW.patient_id,temp_monarch_user_id,temp_monarch_first_transmission_date,NULL,NULL);
	ELSE
		UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date= NEW.training_date WHERE patient_id = NEW.patient_id;UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date_before_update= temp_monarch_first_transmission_date WHERE patient_id = NEW.patient_id;
		IF(NEW.training_date is not null) THEN
		UPDATE PATIENT_NO_EVENT_MONARCH SET first_trans_date_type= 'training' WHERE patient_id = NEW.patient_id;
		END IF;
   END IF;
 ELSE 
	IF NEW.training_date > temp_monarch_first_transmission_date THEN
		UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date= NEW.training_date WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT_MONARCH SET first_transmission_date_before_update= temp_monarch_first_transmission_date WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT_MONARCH SET first_trans_date_type= 'training' WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT_MONARCH SET date_first_transmission_date_updated= curdate() WHERE patient_id = NEW.patient_id;
    END IF;

 END IF;
 
 END IF;
 
SET temp_vest_first_transmission_date = (SELECT first_transmission_date FROM PATIENT_NO_EVENT WHERE patient_id = NEW.patient_id);
SET temp_vest_user_created_date = (SELECT user_created_date FROM PATIENT_NO_EVENT WHERE patient_id = NEW.patient_id);
SET temp_vest_user_id = (SELECT user_id FROM USER_PATIENT_ASSOC WHERE patient_id = NEW.patient_id and user_role='PATIENT' and relation_label = 'Self');

IF NEW.DEVICE_TYPE = 'VEST' THEN

 IF temp_vest_first_transmission_date IS NULL THEN
	IF temp_vest_user_created_date IS NULL THEN
		INSERT INTO PATIENT_NO_EVENT (first_transmission_date, user_created_date, patient_id, user_id,first_transmission_date_before_update,date_first_transmission_date_updated,first_trans_date_type)
				VALUES
				(NEW.training_date, curdate(),NEW.patient_id,temp_vest_user_id,temp_vest_first_transmission_date,NULL,NULL);
	ELSE
		UPDATE PATIENT_NO_EVENT SET first_transmission_date= NEW.training_date WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT SET first_transmission_date_before_update= temp_vest_first_transmission_date WHERE patient_id = NEW.patient_id;
		IF(NEW.training_date is not null) THEN
		UPDATE PATIENT_NO_EVENT SET first_trans_date_type= 'training' WHERE patient_id = NEW.patient_id;
		END IF;
    END IF;
 ELSE 
	IF NEW.training_date > temp_vest_first_transmission_date THEN
		UPDATE PATIENT_NO_EVENT SET first_transmission_date= NEW.training_date WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT SET first_transmission_date_before_update= temp_vest_first_transmission_date WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT SET first_trans_date_type= 'training' WHERE patient_id = NEW.patient_id;
		UPDATE PATIENT_NO_EVENT SET date_first_transmission_date_updated= curdate() WHERE patient_id = NEW.patient_id; 
    END IF;

 END IF;
 
 END IF;

END;//
DELIMITER ;