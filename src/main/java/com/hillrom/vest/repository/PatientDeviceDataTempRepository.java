
package com.hillrom.vest.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.hillrom.vest.web.rest.dto.PatientVestDeviceDataVO;


@Repository
public class PatientDeviceDataTempRepository {

	@Inject
	private EntityManager entityManager;
	
	
	public List<PatientVestDeviceDataVO> getPatientDeviceDataDelta() {
		
		List<PatientVestDeviceDataVO> patientVestDeviceDataVOList = new ArrayList();
			
			
		String patientVestDeviceDataQuery = " select distinct timestamp, sequence_number, event_id, "
				+ "patient_id, serial_number, bluetooth_id, hub_id, hmr, frequency,"
				+ " pressure, duration, checksum, user_id from ("
				+" SELECT 'patient_vest_device_data_temp' AS `set`,r.*"
				+"FROM patient_vest_device_data_temp r "
				+" WHERE timestamp NOT IN (SELECT timestamp FROM PATIENT_VEST_DEVICE_DATA) "
				+" UNION " 
				+" SELECT  'patient_vest_device_data_temp' AS `set`, t.* "
				+" FROM    patient_vest_device_data_temp t "
				+" WHERE   ROW(t.timestamp, t.sequence_number, t.event_id,t.patient_id,t.serial_number,"
				+ "t.bluetooth_id,t.hub_id,t.hmr,t.frequency,t.pressure,t.duration,t.checksum,t.user_id) NOT IN "
				+"(SELECT  *  FROM    PATIENT_VEST_DEVICE_DATA ) ) as diff ";

		Query query = entityManager.createNativeQuery(patientVestDeviceDataQuery);
		
		List<Object[]> results = query.getResultList();
		results.stream().forEach(
				(record) -> {
					Long timestamp = ((BigInteger)  record[0]).longValue();
					Integer sequence_number = (Integer) record[1];
					String event_id = (String) record[2];
					String patient_id = (String) record[3];
					String serial_number = (String) record[4];
					String bluetooth_id = (String) record[5];
					String hub_id = (String) record[6];
					double hmr = ((BigDecimal) record[7]).doubleValue();
					Integer frequency = (Integer) record[8]; 
					Integer pressure = (Integer) record[9]; 
					Integer duration = (Integer)record[10];
					Integer checksum = (Integer)record[11];
					Long userId = ((BigInteger) record[12]).longValue();
					patientVestDeviceDataVOList.add(
							new PatientVestDeviceDataVO(timestamp,sequence_number,event_id,patient_id,serial_number,
									bluetooth_id,hub_id,hmr,frequency,pressure,duration,checksum,userId));
				});
		return patientVestDeviceDataVOList;
	}

}
