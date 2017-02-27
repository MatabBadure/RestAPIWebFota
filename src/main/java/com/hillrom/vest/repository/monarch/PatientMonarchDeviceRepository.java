package com.hillrom.vest.repository.monarch;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientVestDeviceHistoryMonarch;
import com.hillrom.vest.domain.PatientVestDevicePK;

public interface PatientMonarchDeviceRepository extends
		JpaRepository<PatientVestDeviceHistoryMonarch, PatientVestDevicePK> {

	@Query("from PatientVestDeviceHistoryMonarch pvd where pvd.patientVestDevicePK.patient.id = ?1")
	List<PatientVestDeviceHistoryMonarch> findByPatientId(String patientId);

	@Query("from PatientVestDeviceHistoryMonarch pvd where pvd.patientVestDevicePK.serialNumber = ?1")
	List<PatientVestDeviceHistoryMonarch> findBySerialNumber(String serialNumber);

	@Query("from PatientVestDeviceHistoryMonarch pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.patientVestDevicePK.serialNumber = ?2")
	Optional<PatientVestDeviceHistoryMonarch> findOneByPatientIdAndSerialNumber(
			String patientId, String serialNumber);

	@Query("from PatientVestDeviceHistoryMonarch pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.active = ?2")
	Optional<PatientVestDeviceHistoryMonarch> findOneByPatientIdAndActiveStatus(
			String patientId, Boolean active);

	@Query("from PatientVestDeviceHistoryMonarch pvd where pvd.wifiId = ?1 or pvd.lteId = ?1 and pvd.active = true")
	Optional<PatientVestDeviceHistoryMonarch> findByWifiIdAndStatusActive(
			String wifiId);

	@Query("from PatientVestDeviceHistoryMonarch pvd where pvd.patientVestDevicePK.patient.id = ?1 order by pvd.lastModifiedDate desc")
	List<PatientVestDeviceHistoryMonarch> findLatestDeviceForPatient(String patientId);

	@Query(nativeQuery = true, value = "SELECT * FROM PATIENT_VEST_DEVICE_HISTORY_MONARCH pvd where patient_id = :patientId and is_active = :isActive order by last_modified_by desc limit 1 ")
	PatientVestDeviceHistoryMonarch findLatestInActiveDeviceByPatientId(
			@Param("patientId")String pateitnId, @Param("isActive")Boolean active);
}
