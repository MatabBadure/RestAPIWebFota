CREATE DEFINER=`root`@`%` PROCEDURE `get_next_clinic_hillromid`(OUT hillrom_id varchar(15))
BEGIN
	declare clinic_id varchar(6);
	select id into clinic_id from clinic_id_sequence;
	IF clinic_id is null 
		THEN 
		insert into clinic_id_sequence (id) values (0);
	set clinic_id = 0;
	END IF;
    update clinic_id_sequence set id = clinic_id + 1 where id = clinic_id;
    set hillrom_id := concat('HR', year(curdate()), LPAD(`clinic_id`, 6, '0'));
END