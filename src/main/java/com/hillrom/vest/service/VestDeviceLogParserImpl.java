package com.hillrom.vest.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.hillrom.vest.config.PatientVestDeviceRawLogModelConstants;
import com.hillrom.vest.config.VestDeviceLogEntryOffsetConstants;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.service.util.ParserUtil;

@Component
public class VestDeviceLogParserImpl implements DeviceLogParser {

	private static final String EXPECTED_STRING = "24454F50F0F0F0F0";
	private static final String YYYY_MMM_DD_HH_MM_SS = "yyyy-MMM-dd hh:mm:ss";
	private static final int RECORD_SIZE = 16;

	@Override
	public PatientVestDeviceRawLog parseBase64StringToPatientVestDeviceRawLog(
			String base64String) {

		String hub_timestamp = ParserUtil.getValueFromMessage(base64String,
				PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME);
		String sp_timestamp = ParserUtil.getValueFromMessage(base64String,
				PatientVestDeviceRawLogModelConstants.SP_RECEIVE_TIME);

		PatientVestDeviceRawLog patientVestDeviceRawLog = createPatientVestDeviceRawLog(base64String);
		patientVestDeviceRawLog.setDeviceAddress(ParserUtil
				.getValueFromMessage(base64String,
						PatientVestDeviceRawLogModelConstants.DEVICE_ADDRESS));

		patientVestDeviceRawLog.setHubReceiveTime(getTimeStamp(hub_timestamp)
				.getMillis());
		patientVestDeviceRawLog.setSpReceiveTime(getTimeStamp(sp_timestamp)
				.getMillis());
		return patientVestDeviceRawLog;
	}

	private DateTime getTimeStamp(String timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MMM_DD_HH_MM_SS);
		try {
			return new DateTime(dateFormat.parse(timestamp).getTime());
		} catch (ParseException | RuntimeException e) {
			throw new IllegalArgumentException(
					"Could not parse data, Bad Content");
		}
	}

	private PatientVestDeviceRawLog createPatientVestDeviceRawLog(
			String rawMessage) {
		PatientVestDeviceRawLog patientVestDeviceRawLog = new PatientVestDeviceRawLog();
		patientVestDeviceRawLog.setRawMessage(rawMessage);
		patientVestDeviceRawLog
				.setAirInterfaceType(ParserUtil
						.getValueFromMessage(
								rawMessage,
								PatientVestDeviceRawLogModelConstants.AIR_INTERFACE_TYPE));
		patientVestDeviceRawLog.setCucVersion(ParserUtil.getValueFromMessage(
				rawMessage, PatientVestDeviceRawLogModelConstants.CUC_VERSION));
		patientVestDeviceRawLog.setCustomerId(ParserUtil.getValueFromMessage(
				rawMessage, PatientVestDeviceRawLogModelConstants.CUSTOMER_ID));
		patientVestDeviceRawLog.setCustomerName(ParserUtil
				.getValueFromMessage(rawMessage,
						PatientVestDeviceRawLogModelConstants.CUSTOMER_NAME));

		patientVestDeviceRawLog.setDeviceData(ParserUtil.getValueFromMessage(
				rawMessage, PatientVestDeviceRawLogModelConstants.DEVICE_DATA));
		patientVestDeviceRawLog
				.setDeviceModelType(ParserUtil
						.getValueFromMessage(
								rawMessage,
								PatientVestDeviceRawLogModelConstants.DEVICE_MODEL_TYPE));
		patientVestDeviceRawLog
				.setDeviceSerialNumber(ParserUtil
						.getValueFromMessage(
								rawMessage,
								PatientVestDeviceRawLogModelConstants.DEVICE_SERIAL_NUMBER));
		patientVestDeviceRawLog.setDeviceType(ParserUtil.getValueFromMessage(
				rawMessage, PatientVestDeviceRawLogModelConstants.DEVICE_TYPE));
		patientVestDeviceRawLog.setHubId(ParserUtil.getValueFromMessage(
				rawMessage, PatientVestDeviceRawLogModelConstants.HUB_ID));
		patientVestDeviceRawLog.setTimezone(ParserUtil.getValueFromMessage(
				rawMessage, PatientVestDeviceRawLogModelConstants.TIMEZONE));
		patientVestDeviceRawLog
				.setHubReceiveTimeOffset(ParserUtil
						.getValueFromMessage(
								rawMessage,
								PatientVestDeviceRawLogModelConstants.HUB_RECEIVE_TIME_OFFSET));
		return patientVestDeviceRawLog;
	}

	@Override
	public List<PatientVestDeviceData> parseBase64StringToPatientVestDeviceLogEntry(
			String base64String) {

		List<PatientVestDeviceData> patientVestDeviceRecords = new LinkedList<>();
		String base16String = ParserUtil.convertToBase16String(base64String);
		if (base16String.length() <= 16)
			return patientVestDeviceRecords;

		// To validate bad data
		String endTags = base16String.substring(base16String.length() - 16);

		if (!EXPECTED_STRING.equals(endTags.toUpperCase().trim()))
			throw new IllegalArgumentException(
					"Could not parse data, Request contains Partial Data");

		int logcount = 1;
		int start;
		int end;
		start = 32 * 2 + (logcount - 1) * RECORD_SIZE * 2;
		end = 32 * 2 + (logcount * RECORD_SIZE * 2);
		while (start < base16String.length() & (end < base16String.length())) {
			String log_segment = base16String.substring(start, end);
			PatientVestDeviceData patientVestDeviceRecord = getPatientVestDeviceData(
					log_segment, logcount);
			logcount++;
			start = 32 * 2 + (logcount - 1) * RECORD_SIZE * 2;
			end = 32 * 2 + (logcount * RECORD_SIZE * 2);
			patientVestDeviceRecords.add(patientVestDeviceRecord);
		}
		return patientVestDeviceRecords;
	}

	private PatientVestDeviceData getPatientVestDeviceData(String base16String,
			int sequenceNumber) {
		PatientVestDeviceData patientVestDeviceData = new PatientVestDeviceData();
		patientVestDeviceData.setSequenceNumber(sequenceNumber);
		patientVestDeviceData.setHmr(getPatientVestDeviceDataHMR(base16String));
		patientVestDeviceData
				.setPressure(getPatientVestDeviceDataPressure(base16String));
		patientVestDeviceData
				.setFrequency(getPatientVestDeviceDataFrequency(base16String));
		patientVestDeviceData
				.setDuration(getPatientVestDeviceDataDuration(base16String));
		patientVestDeviceData
				.setEventId(getPatientVestDeviceEventCode(base16String));
		patientVestDeviceData.setTimestamp(getPatientVestDeviceDataTimeStamp(
				base16String).getMillis());
		patientVestDeviceData.setChecksum(getPatientVestDeviceDataChecksum(base16String));
		return patientVestDeviceData;
	}

	private Integer getPatientVestDeviceDataDuration(String base16String) {
		String durationReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.DURATION_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.DURATION_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(durationReadingString);
	}

	private Integer getPatientVestDeviceDataFrequency(String base16String) {
		String frequencyReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.FREQUENCY_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.FREQUENCY_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(frequencyReadingString);
	}

	private Integer getPatientVestDeviceDataPressure(String base16String) {
		String pressureReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.PRESSURE_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.PRESSURE_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(pressureReadingString);
	}

	private Double getPatientVestDeviceDataHMR(String base16String) {
		String hmrHourReadingString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.HMR_HOUR_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.HMR_HOUR_END_OFFSET);
		hmrHourReadingString = hmrHourReadingString
				.concat(ParserUtil
						.getFieldByStartAndEndOffset(
								base16String,
								VestDeviceLogEntryOffsetConstants.HMR_HOUR_START_OFFSET1,
								VestDeviceLogEntryOffsetConstants.HMR_HOUR_END_OFFSET1));
		Long hmrHourReadingLong = ParserUtil
				.convertHexStringToLong(hmrHourReadingString);
		String hmrMinutesReadingString = ParserUtil
				.getFieldByStartAndEndOffset(
						base16String,
						VestDeviceLogEntryOffsetConstants.HMR_MINUTE_START_OFFSET,
						VestDeviceLogEntryOffsetConstants.HMR_MINUTE_END_OFFSET);
		Long hmrMinutesReadingLong = ParserUtil
				.convertHexStringToLong(hmrMinutesReadingString);
		return (double) (hmrHourReadingLong * 60 * 60 + hmrMinutesReadingLong * 60);
	}

	private String getPatientVestDeviceEventCode(String base16String) {
		String eventCodeString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.EVENT_CODE_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.EVENT_CODE_END_OFFSET);
		String eventCode = getEventString(ParserUtil
				.convertHexStringToInteger(eventCodeString));
		return eventCode;
	}

	private DateTime getPatientVestDeviceDataTimeStamp(String base16String) {
		String yearString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.YEAR_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.YEAR_END_OFFSET);
		int year = 2000 + ParserUtil.convertHexStringToInteger(yearString);
		String monthString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.MONTH_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.MONTH_END_OFFSET);
		int month = ParserUtil.convertHexStringToInteger(monthString);
		String dayString = ParserUtil.getFieldByStartAndEndOffset(base16String,
				VestDeviceLogEntryOffsetConstants.DAY_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.DAY_END_OFFSET);
		int day = ParserUtil.convertHexStringToInteger(dayString);
		String hourString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.HOUR_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.HOUR_END_OFFSET);
		int hour = ParserUtil.convertHexStringToInteger(hourString);
		String minuteString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.MINUTE_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.MINUTE_END_OFFSET);
		int minute = ParserUtil.convertHexStringToInteger(minuteString);
		String secondString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.SECOND_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.SECOND_END_OFFSET);
		int second = ParserUtil.convertHexStringToInteger(secondString);
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month-1, day, hour, minute, second);
		DateTime timestamp = new DateTime(calendar.getTimeInMillis() / 1000);
		return timestamp;
	}

	private int getPatientVestDeviceDataChecksum(String base16String){
		String checksumString = ParserUtil.getFieldByStartAndEndOffset(
				base16String,
				VestDeviceLogEntryOffsetConstants.CHECKSUM_START_OFFSET,
				VestDeviceLogEntryOffsetConstants.CHECKSUM_END_OFFSET);
		return ParserUtil.convertHexStringToInteger(checksumString);
	}
	
	private String getEventString(int eventCode) {

		String eventString;
		switch (eventCode) {
		case 1:
			eventString = eventCode + ":SessionEventCodeNormalStarted";
			break;
		case 2:
			eventString = eventCode + ":SessionEventCodeNormalSPChanged";
			break;
		case 3:
			eventString = eventCode + ":SessionEventCodeCompleted";
			break;
		case 4:
			eventString = eventCode + ":SessionEventCodeNormalIncomplete";
			break;
		case 5:
			eventString = eventCode + ":SessionEventCodeNormalPaused";
			break;
		case 6:
			eventString = eventCode + ":SessionEventCodeNormalResumed";
			break;
		case 7:
			eventString = eventCode + ":SessionEventCodeProgramPt1Started";
			break;
		case 8:
			eventString = eventCode + ":SessionEventCodeProgramPt2Started";
			;
			break;
		case 9:
			eventString = eventCode + ":SessionEventCodeProgramPt3Started";
			;
			break;
		case 10:
			eventString = eventCode + ":SessionEventCodeProgramPt4Started";
			;
			break;
		case 11:
			eventString = eventCode + ":SessionEventCodeProgramPt5Started";
			break;
		case 12:
			eventString = eventCode + ":SessionEventCodeProgramPt6Started";
			;
			break;
		case 13:
			eventString = eventCode + ":SessionEventCodeProgramPt7Started";
			;
			break;
		case 14:
			eventString = eventCode + ":SessionEventCodeProgramPt8Started";
			;
			break;
		case 15:
			eventString = eventCode + ":SessionEventCodeProgramSPChanged";
			;
			break;
		case 16:
			eventString = eventCode + ":SessionEventCodeProgramCompleted";
			;
			break;
		case 17:
			eventString = eventCode + ":SessionEventCodeProgramIncomplete";
			;
			break;
		case 18:
			eventString = eventCode + ":SessionEventCodeProgramPaused";
			;
			break;
		case 19:
			eventString = eventCode + ":SessionEventCodeProgramResumed";
			;
			break;
		case 20:
			eventString = eventCode + ":SessionEventCodeRampStarted";
			;
			break;
		case 21:
			eventString = eventCode + ":SessionEventCodeRampingPaused";
			;
			break;
		case 22:
			eventString = eventCode + ":SessionEventCodeRampReached";
			;
			break;
		case 23:
			eventString = eventCode + ":SessionEventCodeRampReachedSPChanged";
			;
			break;
		case 24:
			eventString = eventCode + ":SessionEventCodeRampReachedPaused";
			;
			break;
		case 25:
			eventString = eventCode + ":SessionEventCodeRampCompleted";
			;
			break;
		case 26:
			eventString = eventCode + ":SessionEventCodeRampIncomplete";
			;
			break;
		case 27:
			eventString = eventCode + ":SessionEventCodeRampResumed";
			;
			break;
		case 28:
			eventString = eventCode + ":SessionEventCodeCoughPaused";
			;
			break;
		default:
			eventString = eventCode + ":Unknown";
			break;
		}
		return eventString;
	}

}
