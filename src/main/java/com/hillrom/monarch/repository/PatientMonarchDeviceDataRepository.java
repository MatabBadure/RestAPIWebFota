package com.hillrom.monarch.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarchPK;

@Repository
public interface PatientMonarchDeviceDataRepository extends
		JpaRepository<PatientVestDeviceDataMonarch, PatientVestDeviceDataMonarchPK> {

	@Query("Select pvdd from PatientVestDeviceDataMonarch pvdd where patient.id = :patientId order by timestamp desc ")
	public Page<PatientVestDeviceDataMonarch> findLatest(@Param("patientId")String patientId,Pageable pageable);
	
	public List<PatientVestDeviceDataMonarch> findByPatientUserIdAndTimestampBetween(Long id,Long from,Long to);
	
	public PatientVestDeviceDataMonarch findTop1ByPatientUserIdAndSerialNumberOrderByHmrDesc(Long id,String serialNumber);
	
	public List<PatientVestDeviceDataMonarch> findByPatientUserIdAndSerialNumberAndTherapyIndex(Long id,String serialNumber,Integer therapyIndex);

	
	public List<PatientVestDeviceDataMonarch> findByPatientIdAndTimestampBetween(String patientId,Long from,Long to);
	
	// Query to retrieve list of event under therapy 
	@Query(nativeQuery=true,value="select * from PATIENT_VEST_DEVICE_DATA_MONARCH where patient_id = :patientId and from_unixtime(timestamp/1000) between :startTime and :endTime")
	public List<PatientVestDeviceDataMonarch> returnByPatientIdAndTimeStampBetween(@Param("patientId") String patientId, @Param("startTime")String startTime, @Param("endTime")String endTime);
	
}
