package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.PatientVestDeviceRawLog;

/**
 * Spring Data JPA repository for the PatientVestDeviceRawLog entity.
 */
public interface PatientVestDeviceRawLogRepository extends JpaRepository<PatientVestDeviceRawLog,Long> {

}
