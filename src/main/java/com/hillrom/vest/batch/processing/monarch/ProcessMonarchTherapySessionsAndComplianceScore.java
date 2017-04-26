package com.hillrom.vest.batch.processing.monarch;


import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;

@Configuration
@EnableBatchProcessing
public class ProcessMonarchTherapySessionsAndComplianceScore{

	@Autowired
	private JobBuilderFactory jobBuildersMonarch;
 
	@Autowired
	private StepBuilderFactory stepBuildersMonarch;
	
	@Bean
	public Job processMonarchTherapySessionsAndCompliance(){
		return jobBuildersMonarch.get("ProcessMonarchTherapySessionsAndComplianceScore")
				.start(getPatientMonarchDeviceDataDelta())
				.build();
	}
	
	@Bean
	public Step getPatientMonarchDeviceDataDelta(){
		return stepBuildersMonarch.get("getPatientMonarchDeviceDataDelta")
				.<List<PatientVestDeviceDataMonarch>,List<PatientVestDeviceDataMonarch>>chunk(2048)
				.reader(patientMonarchDeviceDataReader())
				.listener(getCustomItemReaderListenerMonarch())
				.writer(patientMonarchDeviceDataWriter())
				.build();
	}	
	
	@Bean
	@StepScope
	public ItemReader<List<PatientVestDeviceDataMonarch>> patientMonarchDeviceDataReader(){
		return new PatientMonarchDeviceDataReader();
	}
	
	@Bean
    public ItemWriter<List<PatientVestDeviceDataMonarch>> patientMonarchDeviceDataWriter() {
    	return new PatientMonarchDeviceDataWriter();
    }
	
	@Bean
	@StepScope
    public CustomItemReaderListenerMonarch getCustomItemReaderListenerMonarch() {
    	return new CustomItemReaderListenerMonarch();
    }
}
