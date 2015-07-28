package com.hillrom.vest.repository;

import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.PatientVestDeviceRawLogPK;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PatientVestDeviceRawLog entity.
 */
public interface PatientVestDeviceRawLogRepository extends JpaRepository<PatientVestDeviceRawLog,PatientVestDeviceRawLogPK> {

}
