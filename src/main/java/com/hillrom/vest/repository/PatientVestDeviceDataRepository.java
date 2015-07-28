package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataPK;

@Repository
public interface PatientVestDeviceDataRepository extends
		JpaRepository<PatientVestDeviceData, PatientVestDeviceDataPK> {

}
