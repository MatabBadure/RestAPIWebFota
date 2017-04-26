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
	
/*	@Query(nativeQuery = true, value = " SELECT IF (t1.s1>0 AND t2.s2>0,'ALL',IF(t1.s1>0 AND t2.s2=0,'VEST', "
			+ " IF(t1.s1=0 AND t2.s2>0,'MONARCH','NONE'))) from "
			+ " (SELECT CASE WHEN COUNT(*) > 0 THEN PATIENT_VEST_DEVICE_HISTORY.serial_number ELSE 0 END AS s1 "
			+ "	from PATIENT_VEST_DEVICE_HISTORY "
			+ " WHERE patient_id =:patientId AND is_active) t1, "
			+ " (SELECT CASE WHEN COUNT(*) > 0 THEN PATIENT_VEST_DEVICE_HISTORY_MONARCH.serial_number ELSE 0 END AS s2 "
			+ " from PATIENT_VEST_DEVICE_HISTORY_MONARCH "
			+ " WHERE patient_id =:patientId AND is_active) t2 ")
	String findDeviceType(@Param("patientId")String patientId);*/
	
	
/*	@Query(nativeQuery = true, value = " SELECT device_type "
			+ " from PATIENT_DEVICES_ASSOC "
			+ " WHERE patient_id =:patientId AND is_active ")
	String findDeviceType(@Param("patientId")String patientId);*/
	
	
	@Query(nativeQuery = true, value = " SELECT IF (pda.patient_type='CD','ALL', pda.device_type) as ptype "
			+ " from PATIENT_DEVICES_ASSOC pda "
			+ " where patient_id =:patientId and is_active=1"
			+ " group by patient_id ")
	String findDeviceType(@Param("patientId")String patientId);

}
