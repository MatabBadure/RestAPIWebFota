package com.hillrom.vest.batch.processing;

import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.item.ItemWriter;

import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;
import com.hillrom.vest.repository.PatientVestDeviceRepository;
import com.hillrom.vest.service.PatientVestDeviceService;

public class PatientVestDeviceDataWriter implements ItemWriter<List<PatientVestDeviceData>>{
	
	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	@Inject
	private PatientVestDeviceService deviceService;
	
	@Override
	public void write(List<? extends List<PatientVestDeviceData>> vestDeviceData)
			throws Exception {
		if(vestDeviceData.size() > 0){
			if(!vestDeviceData.get(0).isEmpty()){
				User patientUser = vestDeviceData.get(0).get(0).getPatientUser();
				PatientInfo patient = vestDeviceData.get(0).get(0).getPatient();
				Double maxHMR = 0d;
				for(List<PatientVestDeviceData> devData : vestDeviceData){
					deviceDataRepository.save(devData);
					for(PatientVestDeviceData deviceEvent : devData){
						if(maxHMR < deviceEvent.getHmr())
							maxHMR = deviceEvent.getHmr();
					}
				}
				deviceService.updateHMR(patientUser, patient);
			}
		}
	}
}
