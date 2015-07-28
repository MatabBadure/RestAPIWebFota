package com.hillrom.vest.config;

public class VestDeviceLogEntryOffsetConstants {

	private VestDeviceLogEntryOffsetConstants(){
		
	}
	
	private static int HMR_HOUR_START_OFFSET = 4;//2(3-1)
	private static int HMR_HOUR_END_OFFSET = 8;//2(3-1)+2*2
	private static int HMR_MINUTE_START_OFFSET = 8;//2(5-1)
	private static int HMR_MINUTE_END_OFFSET = 10;//2(5-1)+2
	private static int HMR_YEAR_START_OFFSET = 10;//2(6-1)
	private static int HMR_YEAR_END_OFFSET = 12;//2(6-1)+2
	private static int EVENT_CODE_START_OFFSET = 22;//2(12-1)
	private static int EVENT_CODE_END_OFFSET = 24;//2(12-1)+2
	private static int FREQUENCY_START_OFFSET = 24;//2(13-1)
	private static int FREQUENCY_END_OFFSET = 26;//2(13-1)+2
	private static int PRESSURE_START_OFFSET = 26;//2(14-1)
	private static int PRESSURE_END_OFFSET = 28;//2(14-1)+2
	private static int DURATION_START_OFFSET = 28;//2(15-1)
	private static int DURATION_END_OFFSET = 30;//2(15-1)+2
	private static int CHECKSUM_START_OFFSET = 30;//2(16-1)
	private static int CHECKSUM_END_OFFSET = 32;//2(16-1)+2

}

