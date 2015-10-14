package com.hillrom.vest.batch.processing;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.neo4j.cypher.internal.helpers.Converge.iterateUntilConverged;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.PatientInfoRepository;
import com.hillrom.vest.repository.TempRepository;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.web.rest.dto.PatientVestDeviceDataVO;
import com.hillrom.vest.batch.util.BatchUtil;

public class PatientVestDeviceDataDeltaReader implements
		ItemReader<List<PatientVestDeviceData>> {

	@Inject
	private TempRepository tempRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private PatientInfoRepository patientInfoRepository;
	
	@Override
	public List<PatientVestDeviceData> read() throws Exception,
			UnexpectedInputException, ParseException,
			NonTransientResourceException {
		List<PatientVestDeviceDataVO>  deviceDataDelta = tempRepository.getPatientDeviceDataDelta();
		User patientUser = null;
		PatientInfo patient = null;
		if(Objects.nonNull(deviceDataDelta) && deviceDataDelta.size() > 0){
			Long userId = deviceDataDelta.get(0).getUserId();
			String patientId = deviceDataDelta.get(0).getPatientId();
			patient = patientInfoRepository.findOne(patientId);
			patientUser = userRepository.findOne(userId); 
		}
		else
			return null;
		if(BatchUtil.flag)
			return null;

		return convertVOToPatientVestDeviceData(deviceDataDelta, patientUser,
				patient);
	}
	
	private List<PatientVestDeviceData> convertVOToPatientVestDeviceData(
			List<PatientVestDeviceDataVO> deviceDataDelta, User patientUser,
			PatientInfo patient) {
		List<PatientVestDeviceData> vestDeviceDatas = new ArrayList<>();
		PatientVestDeviceData patientVestDeviceData;

		for (PatientVestDeviceDataVO dataVO : deviceDataDelta) {
			patientVestDeviceData = new PatientVestDeviceData();

			patientVestDeviceData.setBluetoothId(dataVO.getBluetoothId());
			patientVestDeviceData.setChecksum(dataVO.getChecksum());
			patientVestDeviceData.setDuration(dataVO.getDuration());
			patientVestDeviceData.setEventId(dataVO.getEventId());
			patientVestDeviceData.setFrequency(dataVO.getFrequency());
			patientVestDeviceData.setHmr(dataVO.getHmr());
			patientVestDeviceData.setHubId(dataVO.getHubId());
			patientVestDeviceData.setPatient(patient);
			patientVestDeviceData.setPatientUser(patientUser);
			patientVestDeviceData.setPressure(dataVO.getPressure());
			patientVestDeviceData.setSequenceNumber(dataVO.getSequenceNumber());
			patientVestDeviceData.setTimestamp(dataVO.getTimestamp());

			vestDeviceDatas.add(patientVestDeviceData);
		}
		BatchUtil.flag = true;
		return vestDeviceDatas;
	}

}
