DELIMITER $$
CREATE DEFINER=`root`@`%` FUNCTION `str_random_date`(p_date_start VARCHAR(20)
                               ,p_date_end VARCHAR(20)
                               ,p_format VARCHAR(20)
                                 ) RETURNS varchar(50) CHARSET latin1
    NO SQL
BEGIN
    /**
    * String function. Returns a random date string
    * <br>
    * %author Ronald Speelman
    * %version 1.0
    * Example usage:
    * SELECT str_random_date('1980-01-01','2012-01-01','%Y-%m-%d') AS MysqlDate;
    * See more complex examples and a description on www.moinne.com/blog/ronald
    *
    * %param p_date_start   string: the start date/ time
    * %param p_date_end     string: the end date/ time
    * %param p_format       string: the format of the returned date/time 
    * %return String
    */

    DECLARE v_format VARCHAR(20) DEFAULT '%Y-%m-%d';
    DECLARE v_rand_secs BIGINT DEFAULT 0;

    SET v_format := COALESCE(p_format, v_format);
    SET v_rand_secs  := FLOOR(0 + (RAND() * (86400 * (DATEDIFF(p_date_end , p_date_start)))));
    RETURN DATE_FORMAT(DATE_ADD(p_date_start , INTERVAL  v_rand_secs SECOND),v_format);
END$$
DELIMITER ;