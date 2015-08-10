package com.hillrom.vest.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.hillrom.vest.Application;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataPK;
import com.hillrom.vest.domain.PatientVestDeviceRawLog;
import com.hillrom.vest.service.util.ParserUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class ParserTest {

	PatientVestDeviceRawLog patientVestDeviceRawLog = null;
	List<PatientVestDeviceData> expectedLogEntries = new LinkedList<>();
	List<PatientVestDeviceData> actualLogEntries = null;
	
	DeviceLogParser vestDeviceLogParser = null;
	Map<String, String> testRawMessageKeyValues = new HashMap<>();
	Map<PatientVestDeviceDataPK,PatientVestDeviceData> expectedDeviceEntries = new HashMap<>();
	
	String rawMessage = "device_model_type=HillRom_Vest&device_data=JEkdAAZmYUwtATIuMDAoMDApAAA2LjE1AAAAEjYCAKokVgAAEQ4LEwEAKwEFAQEVJFYAABIOCxMBASsDAAAAGCRFT1Dw8PDw&device_serial_number=62-30538&device_type=CPAP&hub_id=QUALC00100012140&air_interface_type=BLUETOOTH&customer_name=Hill+Rom&cde_version=2014.02.14&exporter_version=1.0&timezone=GMT&sp_receive_time=2014-Nov-19+04%3A39%3A27&hub_receive_time=2014-Nov-19+04%3A39%3A15&device_address=00%3A06%3A66%3A61%3A4C%3A2D&qcl_json_data=%7B%22qclJsonVersion%22%3A1%2C%22twonetProperties%22%3A%7B%22deviceData%22%3A%7B%22value%22%3A%22JEkdAAZmYUwtATIuMDAoMDApAAA2LjE1AAAAEjYCAKokVgAAEQ4LEwEAKwEFAQEVJFYAABIOCxMBASsDAAAAGCRFT1Dw8PDw%22%7D%2C%22customerName%22%3A%7B%22value%22%3A%22Hill+Rom%22%7D%2C%22hubId%22%3A%7B%22value%22%3A%22QUALC00100012140%22%7D%2C%22hubReceiveTimeOffset%22%3A%7B%22value%22%3A0%7D%2C%22twonetId%22%3A%7B%22value%22%3A%221-VM3.1918997.4276811%22%7D%2C%22cdeVersion%22%3A%7B%22value%22%3A%222014.02.14%22%7D%2C%22deviceModel%22%3A%7B%22value%22%3A%22HillRom_Vest%22%7D%2C%22deviceAddress%22%3A%7B%22value%22%3A%2200%3A06%3A66%3A61%3A4C%3A2D%22%7D%2C%22timeZone%22%3A%7B%22value%22%3A%22GMT%22%7D%2C%22hubReceiveTime%22%3A%7B%22value%22%3A1416371955000%7D%2C%22airInterfaceType%22%3A%7B%22value%22%3A%22BLUETOOTH%22%7D%2C%22spReceiveTime%22%3A%7B%22value%22%3A1416371967703%7D%2C%22customerId%22%3A%7B%22value%22%3A%22HILLR001%22%7D%2C%22exporterVersion%22%3A%7B%22value%22%3A%221.0%22%7D%2C%22deviceType%22%3A%7B%22value%22%3A%22CPAP%22%7D%2C%22deviceSerialNumber%22%3A%7B%22value%22%3A%2262-30538%22%7D%7D%7D&twonet_id=1-VM3.1918997.4276811&hub_receive_time_offset=0&cuc_version=2014.02.14&customer_id=HILLR001";
	String base16String = "24491D000666614C2D01322E3030283030290000362E313500000012360200AA24560000110E0B1301002B010501011524560000120E0B1301012B030000001824454F50F0F0F0F0";
	String expectedDeviceData = "";
	
	@Before
	public void setup() {
		vestDeviceLogParser = new VestDeviceLogParserImpl();
		List<NameValuePair> params = URLEncodedUtils.parse(rawMessage,
				Charset.defaultCharset());
		params.forEach(nameValuePair -> {
			testRawMessageKeyValues.put(nameValuePair.getName(), nameValuePair.getValue());
		});
		expectedDeviceData = testRawMessageKeyValues.get("device_data");
		
		
		
		PatientVestDeviceData expectedRecord1 = new PatientVestDeviceData();
		expectedRecord1.setDuration(1);
		expectedRecord1.setFrequency(5);
		expectedRecord1.setPressure(1);
		expectedRecord1.setEventId(""+1);
		expectedRecord1.setHmr(1020d);
		expectedRecord1.setTimestamp(1416339043L);
		expectedRecord1.setSequenceNumber(1);
		
		PatientVestDeviceDataPK deviceDataPK1 = new PatientVestDeviceDataPK();
		deviceDataPK1.setEventId(""+1);
		deviceDataPK1.setSequenceNumber(1);
		deviceDataPK1.setTimestamp(1416339043L);
		
		PatientVestDeviceDataPK deviceDataPK2 = new PatientVestDeviceDataPK();
		deviceDataPK2.setEventId(""+3);
		deviceDataPK2.setSequenceNumber(2);
		deviceDataPK2.setTimestamp(1416339103L);
		
		expectedDeviceEntries.put(deviceDataPK1, expectedRecord1);
		
		PatientVestDeviceData expectedRecord2 = new PatientVestDeviceData();
		expectedRecord2.setDuration(0);
		expectedRecord2.setFrequency(0);
		expectedRecord2.setPressure(0);
		expectedRecord2.setEventId(""+3);
		expectedRecord2.setHmr(1080d);
		expectedRecord2.setTimestamp(1416339103000L);
		expectedRecord2.setSequenceNumber(2);
		
	
		expectedDeviceEntries.put(deviceDataPK2, expectedRecord2);
		expectedLogEntries.add(expectedRecord1);
		expectedLogEntries.add(expectedRecord2);
	}

	@Test
	public void assertHexaConversionStringSuccess(){
		String actualbase16String = ParserUtil.convertToBase16String(expectedDeviceData);
		assertThat(expectedDeviceData);
		assertThat(base16String.equals(actualbase16String)).isTrue();
	}
	
	@Test
	public void assertHexaConversionStringFailure(){
		String base16String = ParserUtil.convertToBase16String(expectedDeviceData);
		assertThat(expectedDeviceData);
		assertThat(base16String.concat("1234").equals(expectedDeviceData)).isFalse();
	}
	
	@Test
	public void assertValidDeviceData(){
		actualLogEntries = vestDeviceLogParser.parseBase64StringToPatientVestDeviceLogEntry(expectedDeviceData);
		assertThat(actualLogEntries);
		for(int i = 0; i< actualLogEntries.size();i++){
			assertThat(expectedLogEntries.get(i).getEventId());
			assertThat(actualLogEntries.get(i).getEventId().charAt(0)+"");
			assertThat(expectedLogEntries.get(i).getDuration().equals(actualLogEntries.get(i).getDuration())).isTrue();
			assertThat(expectedLogEntries.get(i).getFrequency().equals(actualLogEntries.get(i).getFrequency())).isTrue();
			assertThat(expectedLogEntries.get(i).getPressure().equals(actualLogEntries.get(i).getPressure())).isTrue();
			assertThat(expectedLogEntries.get(i).getHmr().equals(actualLogEntries.get(i).getHmr())).isTrue();
			assertThat(expectedLogEntries.get(i).getTimestamp().equals(actualLogEntries.get(i).getTimestamp())).isTrue();
			assertThat(expectedLogEntries.get(i).getSequenceNumber().equals(actualLogEntries.get(i).getSequenceNumber())).isTrue();
		}
	}

}
