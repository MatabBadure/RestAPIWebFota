package com.hillrom.vest.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

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

import net.minidev.json.JSONObject;

import com.hillrom.vest.web.rest.dto.PatientInfoDTO;

/**
 * The class <code>TimsServiceTest</code> contains tests for the class <code>{@link TimsService}</code>.
 *
 * @generatedBy CodePro at 14/9/17 2:21 PM
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
	
	@Inject
	private TimsService timsService;
	
	DateFormat sourceFormat = new SimpleDateFormat("yyyy-mm-dd");
    DateTimeFormatter dobFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
	DateTimeFormatter deviceAssocdateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	
	@Test
	public void testCASE10_PatientHasMonarchAddVisivest_VEST_1()
		throws Exception {
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR_99NUM99512");
		patientInfoDTO.setTims_cust("HR_09NUM9931");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID11");

		boolean result = timsService.CASE10_PatientHasMonarchAddVisivest_VEST(patientInfoDTO);

			assertTrue(result);
	}
	
	@Test
	public void testCASE10_PatientHasVisivestAddMonarch_MONARCH_1()
		throws Exception {
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR1_06NUM91619");
		patientInfoDTO.setTims_cust("HR_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID12");
		boolean result = timsService.CASE10_PatientHasVisivestAddMonarch_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	
	@Test
	public void testCASE11_PatientExistsWithNODevice_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR449_26NUM91619");
		patientInfoDTO.setTims_cust("HR921_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT864_ID13");

		boolean result = timsService.CASE11_PatientExistsWithNODevice_MONARCH(patientInfoDTO);

			assertTrue(result);
	}

	
	@Test
	public void testCASE11_PatientExistsWithNODevice_VEST_4()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR44_26NUM91619");
		patientInfoDTO.setTims_cust("HR92_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT9_ID14");

		boolean result = timsService.CASE11_PatientExistsWithNODevice_VEST(patientInfoDTO);

			assertTrue(result);
	}

	
	@Test
	public void testCASE1_NeitherPatientNorDeviceExist_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR922_16NUM91619");
		patientInfoDTO.setTims_cust("HR922_16NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT4567_ID");

		boolean result = timsService.CASE1_NeitherPatientNorDeviceExist_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	
	@Test
	public void testCASE1_NeitherPatientNorDeviceExist_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR927_16NUM91619");
		patientInfoDTO.setTims_cust("HR927_16NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT967_I1D66199");
		

		boolean result = timsService.CASE1_NeitherPatientNorDeviceExist_VEST(patientInfoDTO);

			assertTrue(result);
	}

	
	@Test
	public void testCASE2_PatientExistsWithNODevice_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR4491_NUM916197");
		patientInfoDTO.setTims_cust("HR922_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID17");

		boolean result = timsService.CASE2_PatientExistsWithNODevice_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	
	@Test
	public void testCASE2_PatientExistsWithNODevice_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR4493_NUM916197");
		patientInfoDTO.setTims_cust("HR923_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT1_ID18");

		boolean result = timsService.CASE2_PatientExistsWithNODevice_VEST(patientInfoDTO);
    	assertTrue(result);
	}

	
	

	@Test
	public void testCASE3_PatientHasMonarchAddVisivest_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR_00NUM191234");
		patientInfoDTO.setTims_cust("HR_00NUM1131");
		patientInfoDTO.setFirst_nm("Bruce1");
		patientInfoDTO.setLast_nm("Thompson1");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID191");
		boolean result = timsService.CASE3_PatientHasMonarchAddVisivest_VEST(patientInfoDTO);
    	assertTrue(result);
	}

	
	
	@Test
	public void testCASE3_PatientHasVisivestAddMonarch_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR_00NUM110122");
		patientInfoDTO.setTims_cust("HR_00NUM114512");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID1101");

		boolean result = timsService.CASE3_PatientHasVisivestAddMonarch_MONARCH(patientInfoDTO);
    	assertTrue(result);
	}

	
	
	@Test
	public void testCASE4_PatientHasDifferentMonarchSwap_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR_00NUM9111");
		patientInfoDTO.setTims_cust("HR_00NUM9131");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID111");

		boolean result = timsService.CASE4_PatientHasDifferentMonarchSwap_MONARCH(patientInfoDTO);

			assertTrue(result);
	}

	
	@Test
	public void testCASE4_PatientHasDifferentVisivestSwap_VEST_1()
		throws Exception {
	
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR9285_06NUM977");
		patientInfoDTO.setTims_cust("HR925_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID112");

		boolean result = timsService.CASE4_PatientHasDifferentVisivestSwap_VEST(patientInfoDTO);

		assertTrue(result);
	}

	
	
	@Test
	public void testCASE5_DeviceOwnedByShell_MONARCH_1()
		throws Exception {
	
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR_00NUM1512");
		patientInfoDTO.setTims_cust("HR_00NUM1131");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID113");
		boolean result = timsService.CASE5_DeviceOwnedByShell_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	
	
	@Test
	public void testCASE5_DeviceOwnedByShell_VEST_1()
		throws Exception {
	
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR_00NUM161");
		patientInfoDTO.setTims_cust("HR_00NUM114512");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID114");

		boolean result = timsService.CASE5_DeviceOwnedByShell_VEST(patientInfoDTO);

			assertTrue(result);
	}

	
	
	@Test
	public void testCASE6_DeviceOwnedByDifferentPatient_MONARCH_1()
		throws Exception {
	
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR_00NUM99512");
		patientInfoDTO.setTims_cust("HR_00NUM9815");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID115");

		boolean result = timsService.CASE6_DeviceOwnedByDifferentPatient_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	
	
	@Test
	public void testCASE6_DeviceOwnedByDifferentPatient_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR_00NUM91619");
		patientInfoDTO.setTims_cust("HR_00NUM96179");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID117");

		boolean result = timsService.CASE6_DeviceOwnedByDifferentPatient_VEST(patientInfoDTO);
        assertTrue(result);
	}

	
	
	@Test
	public void testCASE7_DeviceIsOrphanPatientDoesNotExist_MONARCH_1()
		throws Exception {
	
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");		
		patientInfoDTO.setSerial_num("SR_00NUM118");
		patientInfoDTO.setTims_cust("HR_00NUM118");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID118");
		boolean result = timsService.CASE7_DeviceIsOrphanPatientDoesNotExist_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	
	@Test
	public void testCASE7_DeviceIsOrphanPatientDoesNotExist_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR927_06NUM91619");
		patientInfoDTO.setTims_cust("HR927_16NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID119");
		boolean result = timsService.CASE7_DeviceIsOrphanPatientDoesNotExist_VEST(patientInfoDTO);
    	assertTrue(result);
	}

	
	@Test
	public void testCASE8_DeviceIsOrphanButPatientExist_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR_00NUM120");
		patientInfoDTO.setTims_cust("HR_00NUM120");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID120");

		boolean result = timsService.CASE8_DeviceIsOrphanButPatientExist_MONARCH(patientInfoDTO);

		assertTrue(result);
	}

	

	
	@Test
	public void testCASE8_DeviceIsOrphanButPatientExist_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR927_06NUM91619");
		patientInfoDTO.setTims_cust("HR927_16NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT_ID121");

		boolean result = timsService.CASE8_DeviceIsOrphanButPatientExist_VEST(patientInfoDTO);

			assertTrue(result);
	}

	
	
	@Test
	public void testCASE9_PatientHasDifferentMonarchSwap_MONARCH_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("MONARCH");
		patientInfoDTO.setSerial_num("SR922_06NUM91619 ");
		patientInfoDTO.setTims_cust("HR922_16NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT229_ID122");

		boolean result = timsService.CASE9_PatientHasDifferentMonarchSwap_MONARCH(patientInfoDTO);
    	assertTrue(result);
	}

	
	
	@Test
	public void testCASE9_PatientHasDifferentVisivestSwap_VEST_1()
		throws Exception {
		
		PatientInfoDTO patientInfoDTO = new PatientInfoDTO();
		patientInfoDTO.setDevice_type("VEST");
		patientInfoDTO.setSerial_num("SR927_16NUM91619");
		patientInfoDTO.setTims_cust("HR927_06NUM91619");
		patientInfoDTO.setFirst_nm("Bruce");
		patientInfoDTO.setLast_nm("Thompson");
		patientInfoDTO.setZip_cd("505467");
		patientInfoDTO.setDob("2017-09-01");
		patientInfoDTO.setBluetooth_id("BLT91_ID123");
		boolean result = timsService.CASE9_PatientHasDifferentVisivestSwap_VEST(patientInfoDTO);

		assertTrue(result);
	}

	@Before
	public void setUp()
		throws Exception {
		// add additional set up code here
	}

	
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(TimsServiceTest.class);
	}
}