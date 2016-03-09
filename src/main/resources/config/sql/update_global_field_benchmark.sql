CREATE PROCEDURE `update_global_field_benchmark`()
BEGIN
	DECLARE temp_id bigint(20);
	DECLARE temp_is_hmr_compliant INT;
	DECLARE temp_settings_deviated_days_count INT ;
	DECLARE temp_missed_therapy_count INT;
	DECLARE temp_global_hmr_non_adherence_count INT DEFAULT 0;
	DECLARE temp_global_settings_deviated_days_count INT DEFAULT 0;
	DECLARE temp_global_missed_therapy_days_count INT DEFAULT 0;

	-- Create temp table to mark processed users
	DROP TABLE IF EXISTS user_processed_validation;
    	CREATE TABLE user_processed_validation (`user_id` int not null, `processed` bit(1) not null) Engine=MyISAM;
	-- Create temp table to mark processed records of user.
	DROP TABLE IF EXISTS each_record_processed_validation;
	CREATE TABLE each_record_processed_validation (`id` int not null, `processed` bit(1) not null, pc_date date null) Engine=MyISAM;

	INSERT INTO user_processed_validation (`user_id`, `processed`) SELECT pc.user_id, 0 FROM PATIENT_COMPLIANCE pc GROUP BY user_id;
	
	-- Iterate over users

	WHILE EXISTS(SELECT `user_id` FROM `user_processed_validation` WHERE processed = 0 LIMIT 1) DO

	SELECT `user_id` INTO @temp_user_id FROM `user_processed_validation` WHERE processed = 0 LIMIT 1;
	
	INSERT INTO each_record_processed_validation (`id`, `processed`, `pc_date`) SELECT id, 0, date FROM PATIENT_COMPLIANCE WHERE user_id = @temp_user_id;

	-- Iterate over records of user
		WHILE EXISTS(SELECT `id` FROM each_record_processed_validation WHERE processed = 0 LIMIT 1) DO
			SELECT `id` INTO @temp_pc_id FROM each_record_processed_validation WHERE processed = 0  ORDER BY pc_date LIMIT 1;
			START TRANSACTION;
			SELECT `id`,
			`is_hmr_compliant`,
			`settings_deviated_days_count`,
			`missed_therapy_count`
			INTO
			temp_id,
			temp_is_hmr_compliant,
			temp_settings_deviated_days_count,
			temp_missed_therapy_count
			FROM `PATIENT_COMPLIANCE` WHERE id = @temp_pc_id;

			IF temp_is_hmr_compliant = 0 THEN 
				SET temp_global_hmr_non_adherence_count = temp_global_hmr_non_adherence_count + 1;
			END IF;
			-- Add n-3 when count is more then three else n. n = count of setting deviated 
			IF temp_settings_deviated_days_count > 3 THEN 
				SET temp_global_settings_deviated_days_count = temp_global_settings_deviated_days_count +(temp_settings_deviated_days_count - 3);
			ELSE
				SET temp_global_settings_deviated_days_count = temp_global_settings_deviated_days_count + temp_settings_deviated_days_count;
			END IF;

			IF temp_missed_therapy_count >= 1 THEN 
				SET temp_global_missed_therapy_days_count = temp_global_missed_therapy_days_count + 1;
			END IF;

			UPDATE `PATIENT_COMPLIANCE` SET `global_hmr_non_adherence_counter` = temp_global_hmr_non_adherence_count ,
			`global_settings_deviated_days_count` = temp_global_settings_deviated_days_count,
			`global_missed_therapy_days_count` = temp_global_missed_therapy_days_count WHERE `id` = @temp_pc_id;

			UPDATE `each_record_processed_validation` SET `processed` = 1 WHERE `id` = @temp_pc_id;
			COMMIT;
		END WHILE;
		UPDATE `user_processed_validation` SET `processed` = 1 WHERE `user_id` = @temp_user_id;	
		SET temp_global_hmr_non_adherence_count = 0 ;
		SET temp_global_settings_deviated_days_count = 0;
		SET temp_global_missed_therapy_days_count = 0;
	END WHILE;
	-- Drop both temp tables
	DROP TABLE user_processed_validation;
	DROP TABLE each_record_processed_validation;
END