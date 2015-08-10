package com.hillrom.vest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataPK;

@Repository
public interface PatientVestDeviceDataRepository extends
		JpaRepository<PatientVestDeviceData, PatientVestDeviceDataPK> {

	@Query("Select pvdd from PatientVestDeviceData pvdd where patient.id = :patientId order by timestamp desc ")
	public Page<PatientVestDeviceData> findLatest(@Param("patientId")String patientId,Pageable pageable);
}
