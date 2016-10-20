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
	public static final String DEVICE_VER = "devVer";
	public static final String CRC = "crc";
	public static final String DEVICE_MODEL = "device_model_type";
	
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
	public static final double SESSION_INDEX_LOC = 0;
	public static final double START_TIME_LOC = 5;
	public static final double END_TIME_LOC = 12;
	public static final double START_BATTERY_LEVEL_LOC = 19;
	public static final double END_BATTERY_LEVEL_LOC = 20;
	public static final double NUMBER_OF_EVENTS_LOC = 21;
	public static final double NUMBER_OF_PODS_LOC = 22;
	public static final double HMR_SECONDS_LOC = 23;
	/** Log Event */
	public static final double EVENT_TIMESTAMP_LOC = 0;
	public static final double EVENT_CODE_LOC = 3;
	public static final double FREQUENCY_LOC = 4;
	public static final double INTENSITY_LOC = 4;
	public static final double DURATION_LOC = 5;
	
	/** Device_Data variable sizes */ 
	/** Log Data */
	public static final double SESSION_INDEX_LEN = 5;
	public static final double START_TIME_LEN = 7;
	public static final double END_TIME_LEN = 7;
	public static final double START_BATTERY_LEVEL_LEN = 1;
	public static final double END_BATTERY_LEVEL_LEN = 1;
	public static final double NUMBER_OF_EVENTS_LEN = 1;
	public static final double NUMBER_OF_PODS_LEN = 1;
	public static final double HMR_SECONDS_LEN = 4;
	/** Log Event */
	public static final double EVENT_TIMESTAMP_LEN = 3;
	public static final double EVENT_CODE_LEN = 1;
	public static final double FREQUENCY_LEN = 0.5;
	public static final double INTENSITY_LEN = 0.5;
	public static final double DURATION_LEN = 2;
}
