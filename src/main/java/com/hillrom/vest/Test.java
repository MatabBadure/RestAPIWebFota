package com.hillrom.vest;

import com.hillrom.vest.service.DeviceLogParser;
import com.hillrom.vest.service.VestDeviceLogParserImpl;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String base64String = "JEkdAAZmYU59ATIuMDAoMDApAAA2LjE1AOwBLyQFBFkkVuwBCw8BCAg3LgcKBgroJFbsARUPAQgJBTMIDAUICiRW7AEdDwEICQ03CQ4FCPMkVuwBJQ8BCAkVOwoPBArcJFbsAS8PAQgJHzkQAAAA4SRFT1Dw8PDw";
		String requestData = "device_model_type=HillRom_Vest&device_data=JEkdAAZmYU59ATIuMDAoMDApAAA2LjE1AOwBLyQFBFkkVuwBCw8BCAg3LgcKBgroJFbsARUPAQgJBTMIDAUICiRW7AEdDwEICQ03CQ4FCPMkVuwBJQ8BCAkVOwoPBArcJFbsAS8PAQgJHzkQAAAA4SRFT1Dw8PDw&device_serial_number=62-30502&device_type=CPAP&hub_id=QUALC00100012129&air_interface_type=BLUETOOTH&customer_name=Hill+Rom&cde_version=2014.03.3&exporter_version=1.0&timezone=GMT&sp_receive_time=2015-Jan-09+00%3A50%3A17&hub_receive_time=2015-Jan-09+00%3A50%3A09&device_address=00%3A06%3A66%3A61%3A4E%3A7D&qcl_json_data=%7B%22qclJsonVersion%22%3A1%2C%22twonetProperties%22%3A%7B%22deviceData%22%3A%7B%22value%22%3A%22JEkdAAZmYU59ATIuMDAoMDApAAA2LjE1AOwBLyQFBFkkVuwBCw8BCAg3LgcKBgroJFbsARUPAQgJBTMIDAUICiRW7AEdDwEICQ03CQ4FCPMkVuwBJQ8BCAkVOwoPBArcJFbsAS8PAQgJHzkQAAAA4SRFT1Dw8PDw%22%7D%2C%22customerName%22%3A%7B%22value%22%3A%22Hill+Rom%22%7D%2C%22hubId%22%3A%7B%22value%22%3A%22QUALC00100012129%22%7D%2C%22hubReceiveTimeOffset%22%3A%7B%22value%22%3A0%7D%2C%22twonetId%22%3A%7B%22value%22%3A%221-VM3.1989558.4359812%22%7D%2C%22cdeVersion%22%3A%7B%22value%22%3A%222014.03.3%22%7D%2C%22deviceModel%22%3A%7B%22value%22%3A%22HillRom_Vest%22%7D%2C%22deviceAddress%22%3A%7B%22value%22%3A%2200%3A06%3A66%3A61%3A4E%3A7D%22%7D%2C%22timeZone%22%3A%7B%22value%22%3A%22GMT%22%7D%2C%22hubReceiveTime%22%3A%7B%22value%22%3A1420764609000%7D%2C%22airInterfaceType%22%3A%7B%22value%22%3A%22BLUETOOTH%22%7D%2C%22spReceiveTime%22%3A%7B%22value%22%3A1420764617474%7D%2C%22customerId%22%3A%7B%22value%22%3A%22HILLR001%22%7D%2C%22exporterVersion%22%3A%7B%22value%22%3A%221.0%22%7D%2C%22deviceType%22%3A%7B%22value%22%3A%22CPAP%22%7D%2C%22deviceSerialNumber%22%3A%7B%22value%22%3A%2262-30502%22%7D%7D%7D&twonet_id=1-VM3.1989558.4359812&hub_receive_time_offset=0&cuc_version=2014.03.3&customer_id=HILLR001";

		DeviceLogParser parser = new VestDeviceLogParserImpl();
		//System.out.println(parser.parseBase64StringToPatientVestDeviceLogEntry(base64String));
		System.out.println(parser.parseBase64StringToPatientVestDeviceRawLog(requestData));
		

	}

}
