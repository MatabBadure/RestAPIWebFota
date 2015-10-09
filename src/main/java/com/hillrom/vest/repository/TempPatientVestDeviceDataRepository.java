package com.hillrom.vest.repository;



import static com.hillrom.vest.security.AuthoritiesConstants.ACCT_SERVICES;
import static com.hillrom.vest.security.AuthoritiesConstants.ADMIN;
import static com.hillrom.vest.security.AuthoritiesConstants.ASSOCIATES;
import static com.hillrom.vest.security.AuthoritiesConstants.HILLROM_ADMIN;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
