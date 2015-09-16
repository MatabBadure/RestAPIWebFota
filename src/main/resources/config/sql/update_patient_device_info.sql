DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `update_patient_device_info`(
    IN patient_id varchar(50), 
    IN pat_hub_id varchar(50), 
    IN pat_device_serial_number varchar(50), 
	IN pat_bluetooth_id varchar(50)
    )
BEGIN

UPDATE `hillromvest_local`.`patient_info` SET
`hub_id` = pat_hub_id,
`serial_number` = pat_device_serial_number,
`bluetooth_id` = pat_bluetooth_id WHERE `id` = patient_id;

END$$
DELIMITER ;
