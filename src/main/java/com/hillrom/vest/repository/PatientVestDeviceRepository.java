package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.PatientVestDeviceHistory;
import com.hillrom.vest.domain.PatientVestDevicePK;

public interface PatientVestDeviceRepository extends
		JpaRepository<PatientVestDeviceHistory, PatientVestDevicePK> {

	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1")
	List<PatientVestDeviceHistory> findByPatientId(String patientId);

	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.serialNumber = ?1")
	List<PatientVestDeviceHistory> findBySerialNumber(String serialNumber);

	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.patientVestDevicePK.serialNumber = ?2")
	Optional<PatientVestDeviceHistory> findOneByPatientIdAndSerialNumber(
			String patientId, String serialNumber);

	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1 and pvd.active = ?2")
	Optional<PatientVestDeviceHistory> findOneByPatientIdAndActiveStatus(
			String patientId, Boolean active);

	@Query("from PatientVestDeviceHistory pvd where pvd.bluetoothId = ?1 and pvd.active = true")
	Optional<PatientVestDeviceHistory> findByBluetoothIdAndStatusActive(
			String bluetoothId);

	@Query("from PatientVestDeviceHistory pvd where pvd.patientVestDevicePK.patient.id = ?1 order by pvd.lastModifiedDate desc")
	List<PatientVestDeviceHistory> findLatestDeviceForPatient(String patientId);

	@Query(nativeQuery = true, value = "SELECT * FROM PATIENT_VEST_DEVICE_HISTORY pvd where patient_id = :patientId and is_active = :isActive order by last_modified_by desc limit 1 ")
	PatientVestDeviceHistory findLatestInActiveDeviceByPatientId(
			@Param("patientId")String patientId, @Param("isActive")Boolean active);
	
	@Query(nativeQuery = true, value = "SELECT ( IF( EXISTS( SELECT * FROM PATIENT_VEST_DEVICE_HISTORY "
			+ "WHERE patient_id =:patientId AND is_active), 'VEST', 'NO') )"
	       		+ " UNION"
	       	+ " SELECT  ( IF( EXISTS( SELECT * FROM PATIENT_VEST_DEVICE_HISTORY_MONARCH"
	       	+ " WHERE patient_id =:patientId AND is_active), 'MONARCH', 'NO'))")	
	List <String> findDeviceType(@Param("patientId")String patientId);
	
}
