CREATE PROCEDURE `get_next_protocol_hillromid`(OUT hillrom_id varchar(15))
BEGIN
	declare protocol_id varchar(6);
	select id into protocol_id from protocol_id_sequence;
	IF protocol_id is null 
		THEN 
		insert into protocol_id_sequence (id) values (0);
	set protocol_id = 0;
	END IF;
    update protocol_id_sequence set id = protocol_id + 1 where id = protocol_id;
    set hillrom_id := concat('HR', year(curdate()), LPAD(`protocol_id`, 6, '0'));
END