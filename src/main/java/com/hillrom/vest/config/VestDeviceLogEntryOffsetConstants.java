package com.hillrom.vest.config;

public class VestDeviceLogEntryOffsetConstants {

	private VestDeviceLogEntryOffsetConstants(){
		
	}
	public static int HMR_HOUR_START_OFFSET1 = 4;//2(3-1)
	public static int HMR_HOUR_END_OFFSET1 = 6;//2(3-1)+2*2
	public static int HMR_HOUR_START_OFFSET = 6;//2(3-1)
	public static int HMR_HOUR_END_OFFSET = 8;//2(3-1)+2*2
	public static int HMR_MINUTE_START_OFFSET = 8;//2(5-1)
	public static int HMR_MINUTE_END_OFFSET = 10;//2(5-1)+2
	public static int YEAR_START_OFFSET = 10;//2(6-1)
	public static int YEAR_END_OFFSET = 12;//2(6-1)+2
	public static int MONTH_START_OFFSET = 12;//2(6)
	public static int MONTH_END_OFFSET = 14;//2(6)+2
	public static int DAY_START_OFFSET = 14;//2(6)+2
	public static int DAY_END_OFFSET = 16;//2(6)+2
	public static int HOUR_START_OFFSET = 16;//2(6)+2
	public static int HOUR_END_OFFSET = 18;//2(6)+2
	public static int MINUTE_START_OFFSET = 18;//2(6)+2
	public static int MINUTE_END_OFFSET = 20;//2(6)+2
	public static int SECOND_START_OFFSET = 20;//2(6)+2
	public static int SECOND_END_OFFSET = 22;//2(6)+2
	public static int EVENT_CODE_START_OFFSET = 22;//2(12-1)
	public static int EVENT_CODE_END_OFFSET = 24;//2(12-1)+2
	public static int FREQUENCY_START_OFFSET = 24;//2(13-1)
	public static int FREQUENCY_END_OFFSET = 26;//2(13-1)+2
	public static int PRESSURE_START_OFFSET = 26;//2(14-1)
	public static int PRESSURE_END_OFFSET = 28;//2(14-1)+2
	public static int DURATION_START_OFFSET = 28;//2(15-1)
	public static int DURATION_END_OFFSET = 30;//2(15-1)+2
	public static int CHECKSUM_START_OFFSET = 30;//2(16-1)
	public static int CHECKSUM_END_OFFSET = 32;//2(16-1)+2

}

