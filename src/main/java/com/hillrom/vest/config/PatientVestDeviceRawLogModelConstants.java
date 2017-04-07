package com.hillrom.vest.config;

public class PatientVestDeviceRawLogModelConstants {
	
	// To Restrict instantiation Of this class
	private PatientVestDeviceRawLogModelConstants() {
		super();
	}
	
	public static final String CUSTOMER_ID = "customerId";
	public static final String CUC_VERSION = "cucVersion";
	public static final String HUB_RECEIVE_TIME_OFFSET = "hubReceiveTimeOffset";
	public static final String TIMEZONE = "timeZone";
	public static final String CUSTOMER_NAME = "customerName";
	public static final String AIR_INTERFACE_TYPE = "airInterfaceType";
	public static final String HUB_ID = "hubId";
	public static final String DEVICE_TYPE = "deviceType";
	public static final String DEVICE_SERIAL_NUMBER = "deviceSerialNumber";
	public static final String DEVICE_MODEL_TYPE = "deviceModel";
	public static final String DEVICE_ADDRESS = "deviceAddress";
	public static final String SP_RECEIVE_TIME = "spReceiveTime";
	public static final String HUB_RECEIVE_TIME = "hubReceiveTime";
	public static final String DEVICE_DATA = "deviceData";
	public static final String TWO_NET_PROPERTIES = "twonetProperties";
	public static final String VALUE = "value";
	public static final String QCL_JSON_DATA = "qcl_json_data";
	
	
	public static final String DEVICE_SN = "devSN";
	public static final String DEVICE_WIFI = "devWIFI";
	public static final String DEVICE_LTE = "devLTE";
	public static final String DEVICE_BT = "devBT";
	public static final String DEVICE_VER = "devVer";
	public static final String CRC = "crc";
	public static final String DEVICE_MODEL = "device_model_type";

	
	public static final String FRAG_TOTAL = "fragTotal";
	public static final String FRAG_CURRENT = "fragCurrent";
	
	/** Device_Data variables */ 
	/** Log Data */
	public static final String SESSION_INDEX = "sessionIndex";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String START_BATTERY_LEVEL = "startBatteryLevel";
	public static final String END_BATTERY_LEVEL = "endBatteryLevel";
	public static final String NUMBER_OF_EVENTS = "numberOfEvents";
	public static final String NUMBER_OF_PODS = "numberOfPods";
	public static final String HMR_SECONDS = "hmrSeconds";
	/** Log Event */
	public static final String EVENT_TIMESTAMP = "eventTimestamp";
	public static final String EVENT_CODE = "eventCode";
	public static final String FREQUENCY = "frequency";
	public static final String INTENSITY = "intensity";
	public static final String DURATION = "duration";

	/** Device_Data variable locations */ 
	/** Log Data */
	public static final int SESSION_INDEX_LOC = 0;
	public static final int START_TIME_LOC = 4;
	public static final int END_TIME_LOC = 10;
	public static final int START_BATTERY_LEVEL_LOC = 16;
	public static final int END_BATTERY_LEVEL_LOC = 17;
	public static final int NUMBER_OF_EVENTS_LOC = 18;
	public static final int NUMBER_OF_PODS_LOC = 19;
	public static final int HMR_SECONDS_LOC = 20;
	
	public static final int EVENT_LOG_START_POS = 25;
	
	/** Log Event */
	public static final int EVENT_TIMESTAMP_LOC = 0;
	public static final int EVENT_CODE_LOC = 3;
	public static final int FREQUENCY_LOC = 4;
	public static final int INTENSITY_LOC = 5;
	public static final int DURATION_LOC = 6;
	
	/** Device_Data variable sizes */ 
	/** Log Data */
	public static final int SESSION_INDEX_LEN = 4;
	public static final int START_TIME_LEN = 6;
	public static final int END_TIME_LEN = 6;
	public static final int START_BATTERY_LEVEL_LEN = 1;
	public static final int END_BATTERY_LEVEL_LEN = 1;
	public static final int NUMBER_OF_EVENTS_LEN = 1;
	public static final int NUMBER_OF_PODS_LEN = 1;
	public static final int HMR_SECONDS_LEN = 4;
	/** Log Event */
	public static final int EVENT_LOG_LEN = 9; // Including delimiters
	public static final int EVENT_TIMESTAMP_LEN = 3;
	public static final int EVENT_CODE_LEN = 1;
	public static final int FREQUENCY_LEN = 1; 
	public static final int INTENSITY_LEN = 1; 
	public static final int DURATION_LEN = 1;
	
	public static final byte[] DEVICE_DATA_FIELD_NAME = new byte[]{38,100,101,118,105,99,101,68,97,116,97,61};
	public static final byte[] CRC_FIELD_NAME = new byte[]{38,99,114,99,61};
	public static final byte[] FRAG_TOTAL_FIELD_NAME = new byte[]{38,102,114,97,103,84,111,116,97,108,61};
	public static final byte[] FRAG_CURRENT_FIELD_NAME = new byte[]{38,102,114,97,103,67,117,114,114,101,110,116,61};
	public static final byte[] DEV_SN = new byte[]{38,100,101,118,83,78,61};
	public static final byte[] DEV_WIFI = new byte[]{38,100,101,118,87,73,70,73,61};
	public static final byte[] DEV_LTE = new byte[]{38,100,101,118,76,84,69,61}; // TO BE MODIFIED ACCORDING TO THE REAL DATA
	public static final byte[] DEV_BT = new byte[]{38,100,101,118,66,84,61};
	public static final byte[] DEV_VER = new byte[]{38,100,101,118,86,101,114,61};

}
