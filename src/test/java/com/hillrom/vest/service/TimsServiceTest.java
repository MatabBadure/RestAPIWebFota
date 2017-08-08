package com.hillrom.vest.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimePrinter;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

import com.hillrom.vest.Application;
import com.hillrom.vest.exceptionhandler.HillromException;
import com.hillrom.vest.repository.PatientDevicesAssocRepository;
import com.hillrom.vest.service.TimsService;
import com.hillrom.vest.web.rest.dto.PatientInfoDTO;

import net.minidev.json.JSONObject;

/**
 * The class <code>TimsServiceTest</code> contains tests for the class <code>{@link TimsService}</code>.
 *
 * @generatedBy CodePro at 19/7/17 4:01 PM
 * @author 20104159
 * @version $Revision: 1.0 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@IntegrationTest
@Transactional 
public class TimsServiceTest {
	/**  
	 * Run the TimsService() constructor test.
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	
	@Inject
	private TimsService timsService;
	
//	@Inject
//	private PatientDevicesAssocRepository patientDevicesAssocRepository;
	
	DateTimeFormatter dobFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
/*	@Test
	public void testTimsService_1()
		throws Exception {
		TimsService result = new TimsService();
		assertNotNull(result);
		// add additional test code here
	}
*/
	
	/**
	 * Run the boolean CASE1_NeitherPatientNorDeviceExist_MONARCH(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	
	@Test
	public void testCASE1_NeitherPatientNorDeviceExist_MONARCH_1()
		throws Exception {		
		dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		
		patientInfoDTO.setDob(new LocalDate().now());
		patientInfoDTO.setTims_cust("HR_asdf_90000");
		patientInfoDTO.setSerial_num("ZZSSEREtttt");

		boolean result = timsService.CASE1_NeitherPatientNorDeviceExist_MONARCH(patientInfoDTO);
System.out.println(result);
		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocMonarch(TimsService.java:379)
		//       at com.hillrom.vest.service.TimsService.CASE1_NeitherPatientNorDeviceExist_MONARCH(TimsService.java:1005)
		assertTrue(result);
	}


	@Test
	public void testCASE1_NeitherPatientNorDeviceExist_VEST()
		throws Exception {
	//	TimsService timsService= new TimsService(); 
		dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		
		patientInfoDTO.setDob(new LocalDate().now());
		patientInfoDTO.setTims_cust("HR_srav_999911");
		patientInfoDTO.setSerial_num("ZZSSravanfff11");

		

		boolean result = timsService.CASE1_NeitherPatientNorDeviceExist_VEST(patientInfoDTO);
System.out.println(result);
		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocVest(TimsService.java:368)
		//       at com.hillrom.vest.service.TimsService.CASE1_NeitherPatientNorDeviceExist_VEST(TimsService.java:530)
		assertTrue(result);
	}

	/**
	 * Run the boolean CASE3_PatientHasMonarchAddVisivest_VEST(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	/*@Test
	public void testCASE3_PatientHasMonarchAddVisivest_VEST_1()
		throws Exception {
		//
		dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		



       patientInfoDTO.setDob(new LocalDate().now());
       patientInfoDTO.setTims_cust("HRX6_0711_3_X1GM");
       patientInfoDTO.setSerial_num("COM_V4H001SMGG");
//patientInfoDTO.setOld_serial_number("COM_V4H001SMSS");

       patientInfoDTO.setDevice_type("VEST");


		boolean result = timsService.CASE3_PatientHasMonarchAddVisivest_VEST(patientInfoDTO);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocVest(TimsService.java:368)
		//       at com.hillrom.vest.service.TimsService.CASE3_PatientHasMonarchAddVisivest_VEST(TimsService.java:604)
		assertTrue(result);
	}*/

	
	/**
	 * Run the boolean CASE3_PatientHasVisivestAddMonarch_MONARCH(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	@Test
	public void testCASE3_PatientHasVisivestAddMonarch_MONARCH_1()
		throws Exception {

		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		

dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);

patientInfoDTO.setDob(new LocalDate().now());
patientInfoDTO.setTims_cust("HRX6_0711_3_X1G");
patientInfoDTO.setSerial_num("SHRX6_0711_3_X1GCX");
//patientInfoDTO.setOld_serial_number("COM_V4H001SMSS");

patientInfoDTO.setDevice_type("MONARCH");

		
	

		boolean result = timsService.CASE3_PatientHasVisivestAddMonarch_MONARCH(patientInfoDTO);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocMonarch(TimsService.java:379)
		//       at com.hillrom.vest.service.TimsService.CASE3_PatientHasVisivestAddMonarch_MONARCH(TimsService.java:1078)
		assertTrue(result);
	}

	
	/* * Run the boolean CASE4_PatientHasDifferentVisivestSwap_VEST(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	*/
	@Test
	public void testCASE4_PatientHasDifferentVisivestSwap_VEST_1()
		throws Exception {
		
		dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDob(new LocalDate().now());
		patientInfoDTO.setTims_cust("HRX6_0711_3_X1G");
		patientInfoDTO.setSerial_num("SHRX6_0711_3_X1GC");
		patientInfoDTO.setDevice_type("VEST");

		boolean result = timsService.CASE4_PatientHasDifferentVisivestSwap_VEST(patientInfoDTO);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocVest(TimsService.java:368)
		//       at com.hillrom.vest.service.TimsService.CASE4_PatientHasDifferentVisivestSwap_VEST(TimsService.java:640)
		assertTrue(result);
	}
	

		/**
	 * Run the boolean CASE5_DeviceOwnedByShell_MONARCH(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	@Test
	public void testCASE5_DeviceOwnedByShell_MONARCH_1()
		throws Exception {
		
	dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
			
		patientInfoDTO.setDob(new LocalDate().now());
		patientInfoDTO.setTims_cust("HRM_asdf_1119");
		patientInfoDTO.setSerial_num("SHRMM6_0711_003");
		
		patientInfoDTO.setFirst_nm("Joel");
		patientInfoDTO.setLast_nm("kk");
		

		boolean result = timsService.CASE5_DeviceOwnedByShell_MONARCH(patientInfoDTO);

		// add additional test code here
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocMonarch(TimsService.java:379)
		//       at com.hillrom.vest.service.TimsService.CASE5_DeviceOwnedByShell_MONARCH(TimsService.java:1160)
		assertTrue(result);
	}


	/**
	 * Run the boolean CASE5_DeviceOwnedByShell_VEST(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	@Test
	public void testCASE5_DeviceOwnedByShell_VEST_1()
		throws Exception {
		
		dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDob(new LocalDate().now());
		patientInfoDTO.setTims_cust("HRM67_0711_003");
		patientInfoDTO.setSerial_num("SHRM6_0711_003");
		
		patientInfoDTO.setFirst_nm("Joel");
		patientInfoDTO.setLast_nm("kk");
		

		boolean result = timsService.CASE5_DeviceOwnedByShell_VEST(patientInfoDTO);

		// add additional test code here	
		// An unexpected exception was thrown in user code while executing this test:
		//    java.lang.NullPointerException
		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocVest(TimsService.java:368)
		//       at com.hillrom.vest.service.TimsService.CASE5_DeviceOwnedByShell_VEST(TimsService.java:679)
		assertTrue(result);
	}
	
	

	
	/**
	 * Run the boolean CASE9_PatientHasDifferentMonarchSwap_MONARCH(PatientInfoDTO) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	
	@Test
	public void testCASE9_PatientHasDifferentMonarchSwap_MONARCH_1()
		throws Exception {
		
		
	timsService.dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();		
		patientInfoDTO.setDob(new LocalDate().now());
		patientInfoDTO.setTims_cust("HRX6_0711_3_X1GM");
		patientInfoDTO.setSerial_num("HRX6_0711_3_X1GMC");
		patientInfoDTO.setDevice_type("MONARCH");

		boolean result = timsService.CASE4_PatientHasDifferentMonarchSwap_MONARCH(patientInfoDTO);
		assertTrue(result);
		}
	//@Test
//	public void testCASE9_PatientHasDifferentMonarchSwap_MONARCH_1()
//		throws Exception {
//		
//		
//		/*timsService.dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
//		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
//		patientInfoDTO.setDob(new LocalDate().now());
//		patientInfoDTO.setTims_cust("HR2017001502");
//		patientInfoDTO.setSerial_num("SR20608191");
//		patientInfoDTO.setDevice_type("VEST");
//
//		boolean result = timsService.CASE4_PatientHasDifferentVisivestSwap_VEST(patientInfoDTO);
//		
//		//
//		dobFormat = new DateTimeFormatter((DateTimePrinter) null, (DateTimeParser) null);
//		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
//		patientInfoDTO.setDob(new LocalDate().now());
//		patientInfoDTO.setTims_cust("COMVE1011");
//		patientInfoDTO.setSerial_num("COM_V4H001SMS");
//		
//		patientInfoDTO.setDevice_type("MONARCH");
//		
//		
//
//		boolean result = timsService.CASE4_PatientHasDifferentMonarchSwap_MONARCH(patientInfoDTO);
//
//		// add additional test code here
//		// An unexpected exception was thrown in user code while executing this test:
//		//    java.lang.NullPointerException
//		//       at com.hillrom.vest.service.TimsService.isSerialNoExistInPatientdeviceAssocMonarch(TimsService.java:379)
//		//       at com.hillrom.vest.service.TimsService.DeviceOwnedByDifferentPatient_MONARCH(TimsService.java:1308)
//		//       at com.hillrom.vest.service.TimsService.CASE9_PatientHasDifferentMonarchSwap_MONARCH(TimsService.java:1338)
//		assertTrue(result);
//	}*/

	
//
//	/**
//	 * Perform pre-test initialization.
//	 *
//	 * @throws Exception
//	 *         if the initialization fails for some reason
//	 *
//	 * @generatedBy CodePro at 19/7/17 4:01 PM
//	 */
	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 19/7/17 4:01 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(TimsServiceTest.class);
		System.exit(1);
	}
}