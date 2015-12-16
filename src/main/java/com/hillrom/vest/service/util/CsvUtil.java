package com.hillrom.vest.service.util;

import static com.hillrom.vest.config.Constants.BLUETOOTH_ID;
import static com.hillrom.vest.config.Constants.CAUGH_PAUSE_DURATION;
import static com.hillrom.vest.config.Constants.COUGH_PAUSE_DURATION;
import static com.hillrom.vest.config.Constants.DAILY_TREATMENT_NUMBER;
import static com.hillrom.vest.config.Constants.DATE;
import static com.hillrom.vest.config.Constants.DEVICE_ADDRESS;
import static com.hillrom.vest.config.Constants.DURATION;
import static com.hillrom.vest.config.Constants.DURATION_IN_MINUTES;
import static com.hillrom.vest.config.Constants.END_TIME;
import static com.hillrom.vest.config.Constants.EVENT;
import static com.hillrom.vest.config.Constants.EVENT_ID;
import static com.hillrom.vest.config.Constants.FINISH;
import static com.hillrom.vest.config.Constants.FREQUENCY;
import static com.hillrom.vest.config.Constants.HMR;
import static com.hillrom.vest.config.Constants.HMR_IN_MINUTES;
import static com.hillrom.vest.config.Constants.HUB_ADDRESS;
import static com.hillrom.vest.config.Constants.HUB_ID;
import static com.hillrom.vest.config.Constants.MINUTES;
import static com.hillrom.vest.config.Constants.NORMAL_CAUGH_PAUSES;
import static com.hillrom.vest.config.Constants.NORMAL_COUGH_PAUSES;
import static com.hillrom.vest.config.Constants.PRESSURE;
import static com.hillrom.vest.config.Constants.PROGRAMMED_CAUGH_PAUSES;
import static com.hillrom.vest.config.Constants.PROGRAMMED_COUGH_PAUSES;
import static com.hillrom.vest.config.Constants.SEQUENCE_NO;
import static com.hillrom.vest.config.Constants.SEQUENCE_NUMBER;
import static com.hillrom.vest.config.Constants.SERIAL_NO;
import static com.hillrom.vest.config.Constants.SERIAL_NUMBER;
import static com.hillrom.vest.config.Constants.SESSION_NO;
import static com.hillrom.vest.config.Constants.SESSION_TYPE;
import static com.hillrom.vest.config.Constants.SESSION_TYPE2;
import static com.hillrom.vest.config.Constants.START;
import static com.hillrom.vest.config.Constants.START_TIME;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtDateTime;
import org.supercsv.cellprocessor.joda.FmtLocalDate;

public class CsvUtil {

	private CsvUtil(){
		
	}
	
	public static String[] getHeaderValuesForTherapySessionCSV() {
		String[] header = { DATE, DAILY_TREATMENT_NUMBER, SESSION_TYPE,
				START, FINISH, FREQUENCY, PRESSURE, MINUTES,
				PROGRAMMED_COUGH_PAUSES, NORMAL_COUGH_PAUSES,
				COUGH_PAUSE_DURATION };
		return header;
	}

	public static String[] getHeaderMappingForTherapySessionData() {
		String[] headerMapping = new String[]{ DATE, SESSION_NO, SESSION_TYPE2,
				START_TIME, END_TIME, FREQUENCY, PRESSURE,
				DURATION_IN_MINUTES, PROGRAMMED_CAUGH_PAUSES,
				NORMAL_CAUGH_PAUSES, CAUGH_PAUSE_DURATION };
		return headerMapping;
	}

	public static CellProcessor[] getCellProcessorForTherapySessionData() {
		CellProcessor[] processors = new CellProcessor[] {
				new Optional(new FmtLocalDate("MM/dd/yyyy")), // Therapy Date
    			new Optional(new ParseInt()), // treatmentsPerDay
    			new Optional(), // sessionType
    			new Optional(new FmtDateTime("hh:mm aa")), // Start Time
    			new Optional(new FmtDateTime("hh:mm aa")), // End Time
    			new Optional(new ParseInt()), // frequency
    			new Optional(new ParseInt()), // pressure
    			new Optional(new ParseInt()), // duration in minutes
    			new Optional(new ParseInt()), // programmed cough pauses
    			new Optional(new ParseInt()), // normal cough pauses
    			new Optional(new ParseInt()) // cough pause duration
    	};
		return processors;
	}
	
	public static String[] getHeaderValuesForVestDeviceDataCSV() {
		String[] header = { DATE,SEQUENCE_NO, EVENT,
				SERIAL_NO, DEVICE_ADDRESS, HUB_ADDRESS, FREQUENCY, PRESSURE,MINUTES,HMR};
		return header;
	}

	public static String[] getHeaderMappingForVestDeviceData() {
		String[] headerMapping = new String[]{ DATE, SEQUENCE_NUMBER, EVENT_ID,
				SERIAL_NUMBER,BLUETOOTH_ID,HUB_ID,FREQUENCY, PRESSURE,
				DURATION, HMR_IN_MINUTES };
		return headerMapping;
	}

	public static CellProcessor[] getCellProcessorForVestDeviceData() {
		CellProcessor[] processors = new CellProcessor[] {
				new Optional(new FmtDateTime("MM/dd/yyyy hh:mm aa")), // Date
    			new Optional(new ParseInt()), // Sequence number
    			new Optional(), // Event String
    			new Optional(), // Serial Number String
    			new Optional(), // Bluetooth Id String
    			new Optional(), // hubId String
    			new Optional(new ParseInt()), // Frequency
    			new Optional(new ParseInt()), // Pressure
    			new Optional(new ParseInt()), // Duration
    			new Optional(new ParseDouble()) // Hmr
    	};
		return processors;
	}
}
