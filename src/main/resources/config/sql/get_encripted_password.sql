DROP FUNCTION IF EXISTS `get_encripted_password`;
DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `get_encripted_password`(zipcode int(11),last_name varchar(50), dob date) RETURNS varchar(60) CHARSET utf8
    DETERMINISTIC
BEGIN
-- encrypt(zipcode+1st 4 letters of last_name+dob in MMddyyyy)
	DECLARE PASSWORD VARCHAR(255);
	SELECT CONCAT(CAST(zipcode AS CHAR),SUBSTRING(last_name,1,4),CAST(DATE_FORMAT(dob,'%d%m%Y') AS CHAR)) INTO password;
	RETURN encrypt(PASSWORD) ;
END$$
DELIMITER ;
