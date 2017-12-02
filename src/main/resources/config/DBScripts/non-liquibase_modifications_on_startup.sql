ALTER table hillromvest_xxxx.PATIENT_VEST_DEVICE_RAW_LOGS MODIFY COLUMN device_data LONGTEXT;
ALTER table hillromvest_xxxx.PATIENT_VEST_DEVICE_RAW_LOGS MODIFY COLUMN raw_message LONGTEXT;


ALTER table hillromvest_xxxx.BATCH_JOB_EXECUTION_PARAMS MODIFY COLUMN STRING_VAL BLOB;

ALTER TABLE `hillrom-everest`.hillrom_type_code_values MODIFY COLUMN type_code_value VARCHAR(255)  
    CHARACTER SET utf8 COLLATE utf8_general_ci;
