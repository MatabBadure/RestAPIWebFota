package com.hillrom.vest.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.PatientVestDeviceDataPK;
import com.hillrom.vest.domain.TempPatientVestDeviceData;

@Repository
public interface TempPatientVestDeviceDataRepository extends
		JpaRepository<TempPatientVestDeviceData, PatientVestDeviceDataPK> {

	@Query("Select pvdd from PatientVestDeviceData pvdd where patient.id = :patientId order by timestamp desc ")
	public Page<TempPatientVestDeviceData> findLatest(@Param("patientId")String patientId,Pageable pageable);
	
	public List<TempPatientVestDeviceData> findByPatientUserIdAndTimestampBetween(Long id,Long from,Long to);
}
