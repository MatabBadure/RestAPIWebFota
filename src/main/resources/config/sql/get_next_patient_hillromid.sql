CREATE DEFINER=`root`@`%` PROCEDURE `get_next_patient_hillromid`(INOUT hillrom_id varchar(15))
BEGIN
	declare patient_id varchar(6);
	select id into patient_id from patient_id_sequence;
	IF patient_id is null 
		THEN 
		insert into patient_id_sequence (id) values (0);
	set patient_id = 0;
	END IF;
    update patient_id_sequence set id = patient_id + 1 where id = patient_id;
    set hillrom_id := concat('HR', year(curdate()), LPAD(`patient_id`, 6, '0'));
END