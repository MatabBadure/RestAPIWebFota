DROP TABLE IF EXISTS `protocol_data_temp_table`;
CREATE TABLE `protocol_data_temp_table` (
  `patient_id` varchar(45) NOT NULL,
  `type` varchar(10) DEFAULT NULL,
  `treatments_per_day` bigint(20) DEFAULT NULL,
  `treatment_label` varchar(45) DEFAULT NULL,
  `min_minutes_per_treatment` bigint(20) DEFAULT NULL,
  `max_minutes_per_treatment` bigint(20) DEFAULT NULL,
  `min_frequency` bigint(20) DEFAULT NULL,
  `max_frequency` bigint(20) DEFAULT NULL,
  `min_pressure` bigint(20) DEFAULT NULL,
  `max_pressure` bigint(20) DEFAULT NULL,
  `protocol_key` varchar(45) NOT NULL,
  `to_be_inserted` int(11) DEFAULT '1',
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO `protocol_data_temp_table` VALUES 

('HR2015000079','M',2,'Choice2',2,2,14,16,8,12,'M',1,1),
('HR2015000079','M',2,'Choice2',2,2,14,17,8,12,'M',1,2),
('HR2015000079','M',2,'Choice2',2,2,14,18,8,12,'M',1,3),
('HR2015000079','M',2,'Choice2',2,2,14,19,8,12,'M',1,4),
('HR2015000079','M',2,'Choice2',2,2,14,20,8,12,'M',1,5),
('HR2015000079','M',2,'Choice2',2,2,14,21,8,12,'M',1,6),
('HR2015000077','SO',2,'Choice3',2,2,14,20,8,12,'SO',1,7),
('HR2015000078','S',2,'Choice1',20,40,10,14,10,11,'S',1,8);

CALL `create_patient_protocol_data`('M', 'HR2015000079', 'JDE APP');
CALL `create_patient_protocol_data`('S', 'HR2015000079', 'JDE APP');
CALL `create_patient_protocol_data`('SO', 'HR2015000079', 'JDE APP');