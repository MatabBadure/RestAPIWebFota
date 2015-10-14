package com.hillrom.vest.batch.processing;


import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.hillrom.vest.domain.PatientVestDeviceData;

@Configuration
@EnableBatchProcessing
public class ProcessTherapySessionsAndComplianceScore {

	@Autowired
	private JobBuilderFactory jobBuilders;
 
	@Autowired
	private StepBuilderFactory stepBuilders;
	
	@Bean
	public Job processTherapySessionsAndCompliance(){
		return jobBuilders.get("processTherapySessionsAndComplianceScore")
				.start(getPatientVestDeviceDataDelta())
				.build();
	}
	
	@Bean
	public Step getPatientVestDeviceDataDelta(){
		return stepBuilders.get("getPatientVestDeviceDataDelta")
				.<List<PatientVestDeviceData>,List<PatientVestDeviceData>>chunk(512)
				.reader(patientVestDeviceDataDeltaReader())
				.writer(patientVestDeviceDataWriter())
				.build();
	}	
	
	@Bean
	public ItemReader<List<PatientVestDeviceData>> patientVestDeviceDataDeltaReader(){
		return new PatientVestDeviceDataDeltaReader();
	}
	
	@Bean
    public ItemWriter<List<PatientVestDeviceData>> patientVestDeviceDataWriter() {
    	return new PatientVestDeviceDataWriter();
    }
}
