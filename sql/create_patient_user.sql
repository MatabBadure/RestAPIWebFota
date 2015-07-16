CREATE DEFINER=`root`@`localhost` PROCEDURE `create_patient_user`(
    IN hr_id varchar(15),
    IN device_serial_number varchar(10), 
    IN pat_first_name varchar(50),
    IN pat_middle_name varchar(50),
    IN pat_last_name varchar(50),
    IN pat_title varchar(5), 
    IN pat_address varchar(100),
    IN pat_city varchar(50),
    IN pat_state varchar(10),
    IN pat_zip varchar(10),
    IN pat_dob date,
    IN pat_lang varchar(10),
    IN pat_gender varchar(5),
    IN pat_email varchar(50)
)
BEGIN
    DECLARE created_by varchar(10);
    DECLARE hillrom_id varchar(15);
    DECLARE today_date date;

-- This is a block of final values that are used in the procedure.
    SET created_by = 'system';
    SET today_date = curdate();

-- Calling procedure to get the next hillrom_id for patient 
call get_next_patient_hillromid(@hillrom_id);

-- Splitting patient_name into first name / last name / middle name.

START TRANSACTION;

-- Insert a record for patient
insert into patinet_info (id, hillrom_id, serial_number, first_name, middle_name, 
                          last_name, dob, email, web_login_created, gender, lang, 
                          address, zip_code, city, state) 
                  values (hillrom_id, hr_id, device_serial_number, pat_first_name, 
                          pat_middle_name, pat_last_name, pat_dob, pat_email, 1, 
                          pat_gender, pat_lang, pat_address, pat_zip, pat_city, 
                          pat_state);

-- Insert a record into user table for patient
insert into user (email, password, first_name, middle_name, last_name, activated,
                  lang_key, created_by, created_date, gender)
          values (pat_email, null, pat_first_name, pat_middle_name, pat_last_name, 
                  1, pat_lang, created_by, today_date, pat_gender);
COMMIT;

END