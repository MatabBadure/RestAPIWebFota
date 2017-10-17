ALTER table hillromvest_xxxx.PATIENT_VEST_DEVICE_RAW_LOGS MODIFY COLUMN device_data LONGTEXT;
ALTER table hillromvest_xxxx.PATIENT_VEST_DEVICE_RAW_LOGS MODIFY COLUMN raw_message LONGTEXT;

ALTER TABLE  hillromvest_xxxx.PATIENT_VEST_DEVICE_HISTORY DROP foreign key fk_pvd_patient_id;

ALTER TABLE  hillromvest_xxxx.PATIENT_VEST_DEVICE_HISTORY DROP PRIMARY KEY;

ALTER TABLE  hillromvest_xxxx.PATIENT_VEST_DEVICE_HISTORY
ADD PRIMARY KEY (patient_id,serial_number,is_active);


ALTER TABLE  hillromvest_xxxx.PATIENT_VEST_DEVICE_HISTORY_MONARCH  DROP foreign key  fk_monarch_pvd_patient_id;

ALTER TABLE  hillromvest_xxxx.PATIENT_VEST_DEVICE_HISTORY_MONARCH DROP PRIMARY KEY;

ALTER TABLE  hillromvest_xxxx.PATIENT_VEST_DEVICE_HISTORY_MONARCH
ADD PRIMARY KEY (patient_id,serial_number,is_active);