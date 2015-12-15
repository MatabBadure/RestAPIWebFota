package com.hillrom.vest.batch.processing;

import java.util.List;

import javax.inject.Inject;

import org.springframework.batch.item.ItemWriter;

import com.hillrom.vest.batch.util.BatchUtil;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.repository.PatientVestDeviceDataRepository;

public class PatientVestDeviceDataWriter implements ItemWriter<List<PatientVestDeviceData>>{
	
	@Inject
	private PatientVestDeviceDataRepository deviceDataRepository;
	
	@Override
	public void write(List<? extends List<PatientVestDeviceData>> vestDeviceData)
			throws Exception {
		if(vestDeviceData.size() > 0){
			for(List<PatientVestDeviceData> devData : vestDeviceData){
				deviceDataRepository.save(devData);
			}
		}
	}
}
