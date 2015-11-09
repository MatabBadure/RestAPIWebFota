package com.hillrom.vest.service.util;

import static com.hillrom.vest.config.Constants.*;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
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
    			new FmtLocalDate("MM/dd/yyyy"), // ISBN
    	        new NotNull(), // treatmentsPerDay
    	        new NotNull(), // sessionType
    	        new FmtDateTime("HH:MM a"), // Start Time
    	        new FmtDateTime("HH:MM a"), // End Time
    	        new ParseInt(), // frequency
    	        new ParseInt(), // pressure
    	        new ParseInt(), // duration in minutes
    	        new ParseInt(), // programmed cough pauses
    	        new ParseInt(), // normal cough pauses
    	        new ParseInt() // cough pause duration
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
    			new FmtLocalDate("MM/dd/yyyy"), // Date
    			new ParseInt(), // Sequence number
    	        new NotNull(), // Event String
    	        new NotNull(), // Serial Number String
    	        new NotNull(), // Bluetooth Id String
    	        new NotNull(), // hubId String
    	        new ParseInt(), // Frequency
    	        new ParseInt(), // Pressure
    	        new ParseInt(), // Duration
    	        new ParseDouble() // Hmr
    	};
		return processors;
	}
}
