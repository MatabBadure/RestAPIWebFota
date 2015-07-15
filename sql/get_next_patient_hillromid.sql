CREATE PROCEDURE `get_next_patient_hillromid`(IN patient_id varchar(6), OUT hillrom_id varchar(15))
BEGIN
select id into patient_id from patient_id_sequence;
    update patient_id_sequence set id = patient_id + 1 where id = patient_id;
    set hillrom_id := concat('HR', year(curdate()), LPAD(`patient_id`, 6, '0'));
END
