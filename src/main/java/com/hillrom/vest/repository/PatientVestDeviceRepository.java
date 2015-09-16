package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDevicePK;

public interface PatientVestDeviceRepository extends
		JpaRepository<PatientVestDeviceHistory, PatientVestDevicePK> {

	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.active = false")
	List<PatientVestDeviceHistory> findByPatientId(String patientId);
	
	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.serialNumber = ?1")
	List<PatientVestDeviceHistory> findBySerialNumber(String serialNumber);
	
	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.patientVestDevicePK.serialNumber = ?2")
	Optional<PatientVestDeviceHistory> findOneByPatientIdAndSerialNumber(String patientId, String serialNumber);
	
	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.active = ?2")
	Optional<PatientVestDeviceHistory> findOneByPatientIdAndActiveStatus(String patientId, Boolean active);
}
