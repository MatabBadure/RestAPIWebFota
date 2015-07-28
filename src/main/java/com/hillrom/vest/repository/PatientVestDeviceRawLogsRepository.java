package com.hillrom.vest.repository;

import com.hillrom.vest.domain.PATIENT_VEST_DEVICE_RAW_LOGS;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PATIENT_VEST_DEVICE_RAW_LOGS entity.
 */
public interface PatientVestDeviceRawLogsRepository extends JpaRepository<PATIENT_VEST_DEVICE_RAW_LOGS,Long> {

}
