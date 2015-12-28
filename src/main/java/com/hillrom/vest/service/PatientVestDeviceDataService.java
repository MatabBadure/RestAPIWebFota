package com.hillrom.vest.service;

import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_ADDRESS;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_DATA;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.DEVICE_SERIAL_NUMBER;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_ID;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME;
import static com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants.TWO_NET_PROPERTIES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.service.util.ParserUtil;
import com.hillrom.vest.service.util.RandomUtil;
import com.hillrom.vest.web.rest.PatientVestDeviceDataResource;

@Service
public class PatientVestDeviceDataService {

	private final Logger log = LoggerFactory.getLogger(PatientVestDeviceDataResource.class);
	
	@Inject
	private JobLauncher jobLauncher;
	
	@Inject
	private ApplicationContext applicationContext;

	@Inject
	private MailService mailService;

	public ExitStatus saveData(final String rawData) throws Exception {
		log.debug("saveData has been called , rawData length",rawData.length());

		validateRequest(rawData);
		
		Job addNewDataIngestionJob = applicationContext.getBean("processTherapySessionsAndCompliance", Job.class);
		JobParameters jobParameters = new JobParametersBuilder()
		.addLong("TIME", System.currentTimeMillis())
		.addString("rawData", rawData)
		.toJobParameters();
		JobExecution jobExecution = jobLauncher.run(addNewDataIngestionJob, jobParameters);
		log.debug("Job triggered @ Time ",System.currentTimeMillis());
		ExitStatus exitStatus = jobExecution.getExitStatus();
		// Sending mail Notification on Job Status (ON Success), this code should be removed later
		log.debug("Job triggered @ Time ",exitStatus);
		if(ExitStatus.COMPLETED.equals(exitStatus)){
			mailService.sendStatusOnDataIngestionRequest(rawData, exitStatus.getExitCode(), !ExitStatus.COMPLETED.equals(exitStatus), "");
		}
		return exitStatus;
	}

	private void validateRequest(final String rawData) throws HillromException {
		JSONObject qclJsonData = ParserUtil.getQclJsonDataFromRawMessage(rawData);
		String reqParams[] = new String[]{DEVICE_DATA,
        DEVICE_SERIAL_NUMBER,HUB_ID,HUB_RECEIVE_TIME,DEVICE_ADDRESS};
		if(Objects.isNull(qclJsonData) || qclJsonData.keySet().isEmpty()){
			throw new HillromException("Missing Params : "+String.join(",",reqParams));
		}else if(Objects.nonNull(qclJsonData)){
			JSONObject twoNetProperties = (JSONObject) qclJsonData.getOrDefault(TWO_NET_PROPERTIES, new JSONObject());
			List<String> missingParams = RandomUtil.getDifference(Arrays.asList(reqParams), new ArrayList<String>(twoNetProperties.keySet()));
			if(missingParams.size() > 0)
				throw new HillromException("Missing Params : "+String.join(",",missingParams));
		}
	}
}
