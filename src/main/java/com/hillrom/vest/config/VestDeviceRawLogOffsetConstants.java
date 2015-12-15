package com.hillrom.vest.config;

public class VestDeviceRawLogOffsetConstants {

	private VestDeviceRawLogOffsetConstants(){
		
	}
	public static int PACKET_SIZE_START_OFFSET = 4;//2(3-1)
	public static int PACKET_SIZE_END_OFFSET = 6;//2(3-1)+2
	public static int BT_ADDR_START_OFFSET = 6;// 2(4-1) as per existing code
	public static int BT_ADDR_END_OFFSET = 18; // 2(4-1)+2*6 as per existing code
	public static int PROTOCOL_VERSION_START_OFFSET = 18;//2(10-1) 
	public static int PROTOCOL_VERSION_END_OFFSET = 20;//2(10-1)+2
	public static int VEST__FIRM_WARE_VERSION_START_OFFSET = 20;//2(11-1)
	public static int VEST__FIRM_WARE_VERSION_END_OFFSET = 40;//2(11-1)+2*10
	public static int BT__FIRM_WARE_VERSION_START_OFFSET = 40;//2(21-1)
	public static int BT__FIRM_WARE_VERSION_END_OFFSET = 50;//2(21-1)+2*5
	public static int HOUR_HMR_START_OFFSET = 50;//2(26-1)
	public static int HOUR_HMR_END_OFFSET = 54;//2(26-1)+2*2
	public static int MINUTE_HMR_START_OFFSET = 54;//2(26+1)
	public static int MINUTE_HMR_END_OFFSET = 56;//2(26+1)+2
	public static int SECOND_HMR_START_OFFSET = 56;//2(26+1)+2
	public static int SECOND_HMR_END_OFFSET = 58;//2(26+1)+2*2
	public static int TOTAL_RECORDS_START_OFFSET = 58;//2(30-1)
	public static int TOTAL_RECORDS_END_OFFSET = 62;//2(30-1)+2*2
	public static int CHECKSUM_START_OFFSET = 62;//2(32-1)
	public static int CHECKSUM_END_OFFSET = 64;//2(32-1)+2
	public static final String INFO_PACKET_HEADER = "2449";
}
