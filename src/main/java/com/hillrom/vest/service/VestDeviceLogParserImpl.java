package com.hillrom.vest.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;

import com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.domain.PatientVestDeviceRawLogPK;
import com.hillrom.vest.service.util.ParserUtil;

public class VestDeviceLogParserImpl implements DeviceLogParser {

	private static final String YYYY_MMM_DD_HH_MM_SS = "yyyy-MMM-dd hh:mm:ss";

	@Override
	public PatientVestDeviceRawLog parseBase16StringToPatientVestDeviceRawLog(
			String base16String) {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				YYYY_MMM_DD_HH_MM_SS);
		DateTime hts = null;
		DateTime spts = null;		
		String hub_timestamp = ParserUtil.getValueFromMessage(base16String, PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME);
		String sp_timestamp = ParserUtil.getValueFromMessage(base16String, PatientVestDeviceRawLogModelConstants.SP_RECEIVE_TIME);
		
		try {
			hts = new DateTime(dateFormat.parse(hub_timestamp).getTime());
			spts = new DateTime(dateFormat.parse(sp_timestamp).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		PatientVestDeviceRawLogPK deviceRawLogPK = new PatientVestDeviceRawLogPK();
		deviceRawLogPK.setDeviceAddress(ParserUtil.getValueFromMessage(base16String, PatientVestDeviceRawLogModelConstants.DEVICE_ADDRESS));
		deviceRawLogPK.setHubReceiveTime(hts);

		PatientVestDeviceRawLog patientVestDeviceRawLog = createPatientVestDeviceRawLog(base16String);
		patientVestDeviceRawLog.setId(deviceRawLogPK);
		patientVestDeviceRawLog.setSpReceiveTime(spts);
		return patientVestDeviceRawLog;
	}

	private PatientVestDeviceRawLog createPatientVestDeviceRawLog(
			String base16String) {
		PatientVestDeviceRawLog patientVestDeviceRawLog = new PatientVestDeviceRawLog();
		patientVestDeviceRawLog
				.setAirInterfaceType(ParserUtil
						.getValueFromMessage(
								base16String,
								PatientVestDeviceRawLogModelConstants.AIR_INTERFACE_TYPE));
		patientVestDeviceRawLog.setCucVersion(ParserUtil
				.getValueFromMessage(base16String,
						PatientVestDeviceRawLogModelConstants.CUC_VERSION));
		patientVestDeviceRawLog.setCustomerId(ParserUtil
				.getValueFromMessage(base16String,
						PatientVestDeviceRawLogModelConstants.CUSTOMER_ID));
		patientVestDeviceRawLog.setCustomerName(ParserUtil.getValueFromMessage(
				base16String,
				PatientVestDeviceRawLogModelConstants.CUSTOMER_NAME));
		
		patientVestDeviceRawLog.setDeviceData(ParserUtil
				.getValueFromMessage(base16String,
						PatientVestDeviceRawLogModelConstants.DEVICE_DATA));
		patientVestDeviceRawLog
				.setDeviceModelType(ParserUtil
						.getValueFromMessage(
								base16String,
								PatientVestDeviceRawLogModelConstants.DEVICE_MODEL_TYPE));
		patientVestDeviceRawLog
				.setDeviceSerialNumber(ParserUtil
						.getValueFromMessage(
								base16String,
								PatientVestDeviceRawLogModelConstants.DEVICE_SERIAL_NUMBER));
		patientVestDeviceRawLog.setDeviceType(ParserUtil
				.getValueFromMessage(base16String,
						PatientVestDeviceRawLogModelConstants.DEVICE_TYPE));
		patientVestDeviceRawLog.setHubId(ParserUtil.getValueFromMessage(
				base16String, PatientVestDeviceRawLogModelConstants.HUB_ID));
		return patientVestDeviceRawLog;
	}

}
