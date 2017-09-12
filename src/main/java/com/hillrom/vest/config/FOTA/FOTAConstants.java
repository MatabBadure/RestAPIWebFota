package com.hillrom.vest.config.FOTA;

public final class FOTAConstants {

	//public static final String HEXAFILEPATH = "/opt/FOTA/files";
	/*public static final String HEXAFILEPATH = "D:/FOTA/Hex";
	public static final String FOTA_FILE_PATH = "D:/FOTA/UploadFile/";*/
	public static final String HEXAFILEPATH = "/opt/FOTA/files";
	public static final String FOTA_FILE_PATH = "/opt/FOTA/files";
	public static final int CHUNK_SIZE_VALUE = 200;
	// Raw data constants
	public static final String REQUEST_TYPE = "requestType";
	public static final String CHUNK_SIZE = "chunkSize";
	public static final String CONNECTION_TYPE = "connType";
	public static final String ZERO = "0";
	public static final String ONE = "1";
	public static final String CONNECTION_TYPE_ZERO = "Wifi";
	public static final String CONNECTION_TYPE_ONE = "LTE";
	public static final String DEVICE_PARTNUMBER = "devPartNumber";
	public static final String DEVICE_VER = "devVer";
	public static final String CRC = "crc";
	public static final String CRC_EQ = "crc=";
	public static final String DEVICE_MODEL = "device_model_type";
	public static final String DEVICE_SN = "devSN";
	public static final String PREV_REQ_STATUS = "prevReqStatus";
	public static final String HANDLE = "handle";
	public static final String SOFT_VER_DATE = "softVerDate";
	public static final String RESULT = "result";
	public static final String RESULT_EQ = "result=";
	public static final String HANDLE_EQ = "handle=";
	public static final String TOTAL_CHUNK = "totalChunk=";
	public static final String NOT_OK = "NOT OK";
	public static final String ABORTED = "ABORTED";
	public static final String OK = "OK";
	public static final String YES = "Yes";
	public static final String INIT = "INIT";
	public static final String BUFFER_EQ = "buffer=";
	public static final String BUFFER_LEN_EQ = "bufferLen=";
	public static final String AMPERSAND = "&";
	public static final String REQUEST_TYPE1 = "01";
	public static final String REQUEST_TYPE2 = "02";
	public static final String REQUEST_TYPE3 = "03";
	
	//Device List constants
	public static final String SUCCESS_LIST = "Success";
	public static final String FAILURE_LIST= "Failure";
	public static final String ABORTED_LIST = "Aborted";
	public static final String ALL = "All";
	
	public static final String ACTIVE_PENDING = "Active Pending";
	public static final String ACTIVE_PUBLISHED = "Active Published";
	public static final String INACTIVE_PENDING = "Inactive Pending";
	public static final String INACTIVE_PUBLISHED = "Inactive Published";
	public static final String DELETE_REQUESTED = "Delete Requested";
	
	public static final String DEVICE_PARTNUMBER_01 = "0000000000193164";
	public static final String DEVICE_PARTNUMBER_02 = "0234567890123451";
	public static final String DEVICE_PARTNUMBER_03 = "0345678901234512";
	public static final String DEVICE_PARTNUMBER_04 = "0456789012345123";
	
	//Device listing query 
	public static final String DEVICE_QUERYSTR = "SELECT d.id,d.fota_info_id,d.device_serial_number,d.connection_type,d.device_software_version,d.device_software_date_time,d.updated_software_version,d.checkupdate_date_time,d.download_start_date_time,d.download_end_date_time,d.downloaded_status,f.device_part_number,f.product_Type from FOTA_DEVICE_FWARE_UPDATE_LOG d, FOTA_INFO f where ";
	public static final String DEVICE_QUERYSTR1 ="(d.downloaded_status ='";
	public static final String DEVICE_QUERYSTR2 = "' and lower(f.device_part_number) like lower(";
	public static final String DEVICE_QUERYSTR3 = "' and lower(f.product_type) like lower(";
	public static final String DEVICE_QUERYSTR4 = ") and d.fota_info_id = f.id)";
	
	//For All
	
	
	
	
	
	
	

}
