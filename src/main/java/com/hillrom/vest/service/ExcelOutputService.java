package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.DATE;
import static com.hillrom.vest.config.Constants.DATE_RANGE_REPORT;
import static com.hillrom.vest.config.Constants.DEVICE_ADDRESS;
import static com.hillrom.vest.config.Constants.DURATION;
import static com.hillrom.vest.config.Constants.EVENT;
import static com.hillrom.vest.config.Constants.EVENT_ERROR_CODE;
import static com.hillrom.vest.config.Constants.FREQUENCY;
import static com.hillrom.vest.config.Constants.HILLROM_ID;
import static com.hillrom.vest.config.Constants.HMR;
import static com.hillrom.vest.config.Constants.HUB_ADDRESS;
import static com.hillrom.vest.config.Constants.INTENSITY;
import static com.hillrom.vest.config.Constants.MONARCH_ERROR_CODES;
import static com.hillrom.vest.config.Constants.MONARCH_EVENT_CODES;
import static com.hillrom.vest.config.Constants.PATIENT_ID;
import static com.hillrom.vest.config.Constants.PRESSURE;
import static com.hillrom.vest.config.Constants.SERIAL_NO;
import static com.hillrom.vest.config.Constants.THERAPY_SESSION_TOTAL;
import static com.hillrom.vest.config.Constants.TIME;
import static com.hillrom.vest.config.Constants.WIFIorLTE_SERIAL_NO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hillrom.monarch.service.util.PatientVestDeviceTherapyUtilMonarch;
import com.hillrom.vest.domain.PatientDevicesAssoc;
import com.hillrom.vest.domain.PatientInfo;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.repository.HillromTypeCodeFormatForExcel;
import com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil;
import com.hillrom.vest.web.rest.dto.PatientVestDeviceDataExcelDTO;


@Service
public class ExcelOutputService {

	private static final Logger log = LoggerFactory.getLogger(ExcelOutputService.class);
	
	@Inject
    private HillromTypeCodeFormatForExcel hillromTypeCodeFormatForExcel;
	
	/**
	 * Old Method
	 * @param response
	 * @param deviceEventsList
	 * @throws IOException
	 */
	public void createExcelOutputExcel(HttpServletResponse response,List<PatientVestDeviceData> deviceEventsList) throws IOException{
		log.debug("Received Device Data "+deviceEventsList);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
    	String[] header = { PATIENT_ID,DATE,TIME, EVENT,
				SERIAL_NO, DEVICE_ADDRESS, HUB_ADDRESS, FREQUENCY, PRESSURE,DURATION,HMR};
        setExcelHeader(excelSheet,header);
        setExcelRows(workBook, excelSheet, deviceEventsList);
        autoSizeColumns(excelSheet,11);
        
        workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
	}
	/**
	 * createExcelOutputExcel_Vest new method
	 * @param response
	 * @param deviceEventsList
	 * @param checkPatientTypeVest 
	 * @param deviceType
	 * @param dateRangeReport
	 * @throws IOException
	 */
	public void createExcelOutputExcel_Vest(HttpServletResponse response,List<PatientVestDeviceData> deviceEventsList, PatientDevicesAssoc checkPatientTypeVest, String deviceType, String dateRangeReport) throws IOException{
		log.debug("Received Device Data "+deviceEventsList);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Vest");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
        
        	
		String ReportDate = getReportDate();

		String[] header1 = {
				deviceEventsList.get(0).getPatient().getHillromId(),
				deviceEventsList.get(0).getPatient().getFirstName(),
				deviceEventsList.get(0).getPatient().getLastName(), " ", "VEST", " ", " ",
				checkPatientTypeVest.getSerialNumber(), " ", ReportDate, DATE_RANGE_REPORT,
				dateRangeReport };
        setExcelHeader(excelSheet,header1);
        setExcelRowGrayColor(workBook,excelSheet);
		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataExcelDTO>>>> therapySessions = PatientVestDeviceTherapyUtil.prepareTherapySessionFromDeviceDataForExcel(deviceEventsList);
			
			String[] header2 = {DATE,TIME, EVENT,SERIAL_NO,DEVICE_ADDRESS,HUB_ADDRESS,FREQUENCY,PRESSURE,DURATION,HMR};
	        setExcelHeader_Row3(excelSheet,header2);
	        setExcelRows_3(workBook, excelSheet, deviceEventsList,therapySessions);
			
			log.debug("deviceEventsList"+deviceEventsList.size()); 
		} catch (Exception e) {
			e.printStackTrace();
		}
        autoSizeColumns(excelSheet,12);
        
        workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
	}
	
	/**
	 * getReportDate
	 * @return
	 */
	private String getReportDate() {
		Date date = new Date();
        String DATE_FORMAT = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		return sdf.format(date);
	}

	public void createExcelOutputExcelForMonarch(HttpServletResponse response,List<PatientVestDeviceDataMonarch> deviceEventsList) throws IOException{
		log.debug("Received Device Data "+deviceEventsList);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
    	String[] header = { HILLROM_ID,DATE,TIME, EVENT,
				SERIAL_NO, WIFIorLTE_SERIAL_NO, FREQUENCY, PRESSURE,DURATION,HMR};
        setExcelHeader(excelSheet,header);
        setExcelRowsForMonarch(workBook, excelSheet, deviceEventsList);
        autoSizeColumns(excelSheet,11);
        
        workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
	}
	
	public void createExcelOutputNewExcelForMonarch(
			HttpServletResponse response,
			List<PatientVestDeviceDataMonarch> deviceEventsList,
			PatientDevicesAssoc checkPatientTypeMonarch, PatientInfo patientInfo, String deviceType, String dateRangeReport) throws IOException {
		log.debug("Received Device Data "+deviceEventsList);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Monarch");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
        
      //Report Date as current date in mm/dd/YYYY
    	String ReportDate = getReportDate();
        String[] header1 = {patientInfo.getHillromId(),patientInfo.getFirstName(),patientInfo.getLastName()," ",
    			"MONARCH"," ",checkPatientTypeMonarch.getSerialNumber()," ",ReportDate,DATE_RANGE_REPORT,dateRangeReport};
        
        setExcelHeader(excelSheet,header1);
        
        setExcelRowGrayColor_Monarch(workBook,excelSheet);
        
		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataMonarch>>>> therapySessions = PatientVestDeviceTherapyUtilMonarch.prepareTherapySessionFromDeviceMonarchDataForExcel(deviceEventsList);
			
			String[] header2 = {DATE,TIME, EVENT,SERIAL_NO,WIFIorLTE_SERIAL_NO,FREQUENCY,INTENSITY,DURATION,HMR};
	        setExcelHeader_Row3(excelSheet,header2);
	        setExcelRows_3_ForMonarch(workBook, excelSheet, deviceEventsList,therapySessions);
			
			log.debug("TherapySession"+therapySessions.get(0)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		autoSizeColumns(excelSheet,11);
        workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
	}

	private void setExcelRowGrayColor(HSSFWorkbook workBook, HSSFSheet excelSheet) {
		HSSFRow excelRow = excelSheet.createRow(1);
		HSSFCellStyle style = workBook.createCellStyle();
	    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		for(int cellCount = 0; cellCount<=11;cellCount++ ){
			excelRow.createCell(cellCount).setCellStyle(style);
		}
	}
	
	private void setExcelRowGrayColor_Monarch(HSSFWorkbook workBook, HSSFSheet excelSheet) {
		HSSFRow excelRow = excelSheet.createRow(1);
		HSSFCellStyle style = workBook.createCellStyle();
	    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		for(int cellCount = 0; cellCount<=10;cellCount++ ){
			excelRow.createCell(cellCount).setCellStyle(style);
		}
	}
	
	private void setExcelRows_3_ForMonarch(
			HSSFWorkbook workBook,
			HSSFSheet excelSheet,
			List<PatientVestDeviceDataMonarch> deviceEventsList,
			Map<DateTime, Map<Integer, Map<Long, List<PatientVestDeviceDataMonarch>>>> therapySessions) {

		int record = 3;
		HSSFCellStyle dateStyle = createCellStyle(workBook,"m/d/yy");
		HSSFCellStyle timeStyle = createCellStyle(workBook,"h:mm AM/PM");
		
		
		Map<String,String> monarchEventCodes = new LinkedHashMap<>();
		List<Object[]> resultEventList = hillromTypeCodeFormatForExcel.findCodeValuesListForExcel(MONARCH_EVENT_CODES);
		for (Object[] result : resultEventList) {
			monarchEventCodes.put((String)result[0], (String)result[1]);
		}
		
		Map<String,String> monarchErrorCodes = new LinkedHashMap<>();
		List<Object[]> resultErroList = hillromTypeCodeFormatForExcel.findCodeValuesListForExcel(MONARCH_ERROR_CODES);
		for (Object[] result : resultErroList) {
			monarchErrorCodes.put((String)result[0], (String)result[1]);
		}
		
		log.debug("Total therapySessions for Date Range :"+therapySessions.size());
		for (Map<Integer, Map<Long, List<PatientVestDeviceDataMonarch>>> therapySessionsForTheDay : therapySessions.values()) {
			log.debug("Total therapySessions for day :"+therapySessionsForTheDay.size());
				for (Map<Long, List<PatientVestDeviceDataMonarch>> therapySessionForTheDay: therapySessionsForTheDay.values()) {
					log.debug("Therapy Session  :"+therapySessionForTheDay.size());
					for (List<PatientVestDeviceDataMonarch> eventDetailsList : therapySessionForTheDay.values()) {
						log.debug("Therapy eventDetailsList :"+therapySessionForTheDay.size());
						//Sort By Date
						Collections.sort(eventDetailsList,PatientVestDeviceDataMonarch.monarchDateAscComparator);
						
						 int duration = 0; int i = 0; int j = 0;
						 for(PatientVestDeviceDataMonarch eventDetails : eventDetailsList){
							 
							 	String deviceEventCodeStr = "";
							 	if(eventDetails.getEventCode() != EVENT_ERROR_CODE){
									deviceEventCodeStr = monarchEventCodes.get(eventDetails.getEventCode());
									log.debug("MonarchEventCodes : "+eventDetails.getEventCode());
									log.debug("MonarchEventCodes map str: "+deviceEventCodeStr);
								}else{
									deviceEventCodeStr = monarchErrorCodes.get(eventDetails.getIntensity());
									log.debug("MonarchEventCodes : "+eventDetails.getEventCode());
									log.debug("MonarchErrorCodes map str: "+deviceEventCodeStr);
								}
							 	
								HSSFRow excelRow = excelSheet.createRow(record++);
								if(++i == 1){
									HSSFCell dateCell = excelRow.createCell(0);
									dateCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									dateCell.setCellValue(eventDetails.getDate().toDate());
									dateCell.setCellStyle(dateStyle);
								}
								HSSFCell timeCell = excelRow.createCell(1);
								timeCell.setCellValue(eventDetails.getDate().toDate());
								timeCell.setCellStyle(timeStyle);
								//Printing event string for Monarch
								excelRow.createCell(2).setCellValue(deviceEventCodeStr);
								excelRow.createCell(3).setCellValue(eventDetails.getSerialNumber());
								if(Objects.nonNull(eventDetails.getDevWifi()) && Objects.nonNull(eventDetails.getDevLte())){
								excelRow.createCell(4).setCellValue(eventDetails.getDevWifi());
								}
								else if(Objects.isNull(eventDetails.getDevWifi()) && Objects.nonNull(eventDetails.getDevLte())){
								excelRow.createCell(4).setCellValue(eventDetails.getDevLte());
								}
								else{
								excelRow.createCell(4).setCellValue(eventDetails.getDevWifi());
								}
								excelRow.createCell(5).setCellValue(eventDetails.getFrequency());
								excelRow.createCell(6).setCellValue(eventDetails.getIntensity());
								excelRow.createCell(7).setCellValue(eventDetails.getDuration());
								excelRow.createCell(8).setCellValue(eventDetails.getHmrInHours());
								duration = duration + eventDetails.getDuration();

								log.debug("total duration in loop[" + record + "]  :"+duration);
								if(++j == eventDetailsList.size()){
									log.debug("Inside total : " + j);
										excelRow = excelSheet.createRow(record++);
										
										HSSFCellStyle style = workBook.createCellStyle();
									    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
									    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
										Font font = workBook.createFont();//Create font
									    font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
									    for(int cellCount = 0; cellCount<=10;cellCount++ ){
											if(cellCount == 2){
											    style.setFont(font);//set it to bold
												HSSFCell dateCell = excelRow.createCell(cellCount);
												dateCell.setCellValue(THERAPY_SESSION_TOTAL);
												dateCell.setCellStyle(style);
											}else if(cellCount == 7){
												style.setFont(font);
												HSSFCell dateCell2 = excelRow.createCell(cellCount);
												dateCell2.setCellValue(duration);
												dateCell2.setCellStyle(style);
											}else{
												excelRow.createCell(cellCount).setCellStyle(style);
											}
										}
								}
							}
						} 
				}
		}
		autoSizeColumns(excelSheet,10);
	}
	public void createExcelOutputExcelForAll(HttpServletResponse response,List<PatientVestDeviceData> deviceEventsListVest,List<PatientVestDeviceDataMonarch> deviceEventsListMonarch) throws IOException{
		log.debug("Received Device Data for Vest :"+deviceEventsListVest+" & Monarch"+deviceEventsListMonarch);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Vest");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
    	String[] headerVest = { PATIENT_ID,DATE,TIME, EVENT,
				SERIAL_NO, DEVICE_ADDRESS, HUB_ADDRESS, FREQUENCY, PRESSURE,DURATION,HMR};
        setExcelHeader(excelSheet,headerVest);
        setExcelRows(workBook, excelSheet, deviceEventsListVest);
        autoSizeColumns(excelSheet,11);
        
        //HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheetMonarch = workBook.createSheet("Therapy Report Monarch");
        /* Freeze top row alone */
        excelSheetMonarch.createFreezePane(0,1);
    	String[] headerMonarch = { HILLROM_ID,DATE,TIME, EVENT,
				SERIAL_NO, WIFIorLTE_SERIAL_NO, FREQUENCY, INTENSITY,DURATION,HMR};
        setExcelHeader(excelSheetMonarch,headerMonarch);
        setExcelRowsForMonarch(workBook, excelSheetMonarch, deviceEventsListMonarch);
        autoSizeColumns(excelSheetMonarch,11);
        
        workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
	}
	

	public void setExcelHeader(HSSFSheet excelSheet,String ...headerNames) {
		HSSFRow excelHeader = excelSheet.createRow(0);
		int cellCount = 0;
		for(String headerName : headerNames){
			excelHeader.createCell(cellCount++).setCellValue(headerName);
		}
	}
	
	/**
	 * setExcelHeader_Row3 for vest
	 * @param excelSheet
	 * @param headerNames
	 */
	public void setExcelHeader_Row3(HSSFSheet excelSheet,String ...headerNames) {
		HSSFRow excelHeader = excelSheet.createRow(2);
		int cellCount = 0;
		for(String headerName : headerNames){
			excelHeader.createCell(cellCount++).setCellValue(headerName);
		}
	}
	
	/**
	 * setExcelRows_3 for Vest
	 * @param workBook
	 * @param excelSheet
	 * @param deviceEventsList
	 * @param therapySessions
	 */
	public void setExcelRows_3(HSSFWorkbook workBook,HSSFSheet excelSheet, List<PatientVestDeviceData> deviceEventsList, Map<DateTime, Map<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>>> therapySessions){
		int record = 3;
		HSSFCellStyle dateStyle = createCellStyle(workBook,"m/d/yy");
		HSSFCellStyle timeStyle = createCellStyle(workBook,"h:mm AM/PM");
		
		/*for (PatientVestDeviceData deviceEvent : deviceEventsList) {
			
			Map<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>>	therapySessionsForTheDay = new LinkedHashMap<>();
			log.debug("deviceEvent.getDate() :" + deviceEvent.getDate());
			therapySessionsForTheDay = therapySessions.get(deviceEvent.getDate());
			log.debug("therapySessionsForTheDay :" + therapySessionsForTheDay);*/
		log.debug("therapySessions :" + therapySessions.size());
		
		for (Map<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>> therapySessionsForTheDay : therapySessions.values()) {
		
		//for(Map.Entry<DateTime, Map<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>>> therapySessions1 : therapySessions.entrySet()){
			
			/*Map<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>>	therapySessionsForTheDay = new LinkedHashMap<>();
			therapySessionsForTheDay = therapySessions1.getValue();*/
			log.debug("therapySessionsForTheDay:"+therapySessionsForTheDay.size());
			
			//for(Map.Entry<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>> therapySession : therapySessionsForTheDay.entrySet()){
				
			for(Map<Long, List<PatientVestDeviceDataExcelDTO>> therapySessionForTheDay : therapySessionsForTheDay.values()){
				/*Map<Long, List<PatientVestDeviceDataExcelDTO>> therapySessionForTheDay = new LinkedHashMap<>();
				therapySessionForTheDay =  therapySession.getValue();*/
				log.debug("TherapySessionForTheDay:"+therapySessionForTheDay.size());
				
					//for(Map.Entry<Long, List<PatientVestDeviceDataExcelDTO>> session : therapySessionForTheDay.entrySet()){
						
				for(List<PatientVestDeviceDataExcelDTO> eventDetailsList : therapySessionForTheDay.values()){
						//Sort by Vest Date
						Collections.sort(eventDetailsList,PatientVestDeviceDataExcelDTO.vestDateAscComparator);
					
						 log.debug("eventDetailsList:"+eventDetailsList.size());
						 int duration = 0; int i = 0; int j = 0;
						 for(PatientVestDeviceDataExcelDTO eventDetails : eventDetailsList){

							 log.debug("eventDetails:"+eventDetails.getTimestamp());
							 log.debug("eventDetails:"+eventDetails);
							 String eventCode = eventDetails.getEventId().split(":")[0];
							 
							 String eventCodeStr = getEventStringByEventCode(eventCode);
							 
							 HSSFRow excelRow = excelSheet.createRow(record++);
								//excelRow.createCell(0).setCellValue(deviceEvent.getPatientBlueToothAddress());
								
								if(++i == 1){
									HSSFCell dateCell = excelRow.createCell(0);
									dateCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									dateCell.setCellValue(eventDetails.getDate().toDate());
									dateCell.setCellStyle(dateStyle);
								}
								HSSFCell timeCell = excelRow.createCell(1);
								timeCell.setCellValue(eventDetails.getDate().toDate());
								timeCell.setCellStyle(timeStyle);
								
								excelRow.createCell(2).setCellValue(eventCodeStr);
								excelRow.createCell(3).setCellValue(eventDetails.getSerialNumber());
								excelRow.createCell(4).setCellValue(eventDetails.getBluetoothId());
								excelRow.createCell(5).setCellValue(eventDetails.getHubId());
								excelRow.createCell(6).setCellValue(eventDetails.getFrequency());
								excelRow.createCell(7).setCellValue(eventDetails.getPressure());
								excelRow.createCell(8).setCellValue(eventDetails.getDuration());
								excelRow.createCell(9).setCellValue(eventDetails.getHmr());//Get HMR in Hrs values set only name is HMR
								duration = duration + eventDetails.getDuration();

								log.debug("total duration in loop[" + record + "]  :"+duration);
								if(++j == eventDetailsList.size()){
									log.debug("Inside total : " + j);
										
										excelRow = excelSheet.createRow(record++);
										
										HSSFCellStyle style = workBook.createCellStyle();
									    style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
									    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
										Font font = workBook.createFont();
										font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
										for(int cellCount = 0; cellCount<=11;cellCount++ ){
											if(cellCount == 2){
											    style.setFont(font);//set it to bold
												HSSFCell dateCell = excelRow.createCell(cellCount);
												dateCell.setCellValue(THERAPY_SESSION_TOTAL);
												dateCell.setCellStyle(style);
											}else if(cellCount == 8){
												style.setFont(font);
												HSSFCell dateCell2 = excelRow.createCell(cellCount);
												dateCell2.setCellValue(duration);
												dateCell2.setCellStyle(style);
											}else{
												excelRow.createCell(cellCount).setCellStyle(style);
											}
										}
								}
							}
						} 
				}
		}
		autoSizeColumns(excelSheet,9);
	}
	
	
	//Removing Unwanted Strings
	private String getEventStringByEventCode(String eventId) {

		String eventString;
		
		int eventCode = Integer.parseInt(eventId);
		switch (eventCode) {
		case 1:
			eventString = "NormalStarted";
			break;
		case 2:
			eventString = "NormalSPChanged";
			break;
		case 3:
			eventString = "Completed";
			break;
		case 4:
			eventString = "NormalIncomplete";
			break;
		case 5:
			eventString = "NormalPaused";
			break;
		case 6:
			eventString = "NormalResumed";
			break;
		case 7:
			eventString = "ProgramPt1Started";
			break;
		case 8:
			eventString = "ProgramPt2Started";
			;
			break;
		case 9:
			eventString = "ProgramPt3Started";
			;
			break;
		case 10:
			eventString = "ProgramPt4Started";
			;
			break;
		case 11:
			eventString = "ProgramPt5Started";
			break;
		case 12:
			eventString = "ProgramPt6Started";
			;
			break;
		case 13:
			eventString = "ProgramPt7Started";
			;
			break;
		case 14:
			eventString = "ProgramPt8Started";
			;
			break;
		case 15:
			eventString = "ProgramSPChanged";
			;
			break;
		case 16:
			eventString = "ProgramCompleted";
			;
			break;
		case 17:
			eventString = "ProgramIncomplete";
			;
			break;
		case 18:
			eventString = "ProgramPaused";
			;
			break;
		case 19:
			eventString = "ProgramResumed";
			;
			break;
		case 20:
			eventString = "RampStarted";
			;
			break;
		case 21:
			eventString = "RampingPaused";
			;
			break;
		case 22:
			eventString = "RampReached";
			;
			break;
		case 23:
			eventString = "RampReachedSPChanged";
			;
			break;
		case 24:
			eventString = "RampReachedPaused";
			;
			break;
		case 25:
			eventString = "RampCompleted";
			;
			break;
		case 26:
			eventString = "RampIncomplete";
			;
			break;
		case 27:
			eventString = "RampResumed";
			;
			break;
		case 28:
			eventString = "CoughPaused";
			;
			break;
		default:
			eventString = "Unknown";
			break;
		}
		return eventString;
	}
	public void setExcelRows(HSSFWorkbook workBook,HSSFSheet excelSheet, List<PatientVestDeviceData> deviceEventsList){
		int record = 1;
		HSSFCellStyle dateStyle = createCellStyle(workBook,"m/d/yy");
		HSSFCellStyle timeStyle = createCellStyle(workBook,"h:mm AM/PM");
		for (PatientVestDeviceData deviceEvent : deviceEventsList) {
			HSSFRow excelRow = excelSheet.createRow(record++);
			excelRow.createCell(0).setCellValue(deviceEvent.getPatientBlueToothAddress());
			
			HSSFCell dateCell = excelRow.createCell(1);
			dateCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			dateCell.setCellValue(deviceEvent.getDate().toDate());
			dateCell.setCellStyle(dateStyle);
			
			HSSFCell timeCell = excelRow.createCell(2);
			timeCell.setCellValue(deviceEvent.getDate().toDate());
			timeCell.setCellStyle(timeStyle);
			
			excelRow.createCell(3).setCellValue(deviceEvent.getEventId());
			excelRow.createCell(4).setCellValue(deviceEvent.getSerialNumber());
			excelRow.createCell(5).setCellValue(deviceEvent.getBluetoothId());
			excelRow.createCell(6).setCellValue(deviceEvent.getHubId());
			excelRow.createCell(7).setCellValue(deviceEvent.getFrequency());
			excelRow.createCell(8).setCellValue(deviceEvent.getPressure());
			excelRow.createCell(9).setCellValue(deviceEvent.getDuration());
			excelRow.createCell(10).setCellValue(deviceEvent.getHmrInHours());
		}
	}
	
	public void setExcelRowsForMonarch(HSSFWorkbook workBook,HSSFSheet excelSheet, List<PatientVestDeviceDataMonarch> deviceEventsList){
		int record = 1;
		HSSFCellStyle dateStyle = createCellStyle(workBook,"m/d/yy");
		HSSFCellStyle timeStyle = createCellStyle(workBook,"h:mm AM/PM");
		for (PatientVestDeviceDataMonarch deviceEvent : deviceEventsList) {
			HSSFRow excelRow = excelSheet.createRow(record++);
			excelRow.createCell(0).setCellValue(deviceEvent.getPatient().getHillromId());
			
			HSSFCell dateCell = excelRow.createCell(1);
			dateCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			dateCell.setCellValue(deviceEvent.getDate().toDate());
			dateCell.setCellStyle(dateStyle);
			
			HSSFCell timeCell = excelRow.createCell(2);
			timeCell.setCellValue(deviceEvent.getDate().toDate());
			timeCell.setCellStyle(timeStyle);
			
			excelRow.createCell(3).setCellValue(deviceEvent.getEventCode());
			excelRow.createCell(4).setCellValue(deviceEvent.getSerialNumber());
			if(Objects.nonNull(deviceEvent.getDevWifi()) && Objects.nonNull(deviceEvent.getDevLte())){
			excelRow.createCell(5).setCellValue(deviceEvent.getDevWifi());
			}
			else if(Objects.isNull(deviceEvent.getDevWifi()) && Objects.nonNull(deviceEvent.getDevLte())){
			excelRow.createCell(5).setCellValue(deviceEvent.getDevLte());
			}
			else{
			excelRow.createCell(5).setCellValue(deviceEvent.getDevWifi());
			}
			excelRow.createCell(6).setCellValue(deviceEvent.getFrequency());
			excelRow.createCell(7).setCellValue(deviceEvent.getIntensity());
			excelRow.createCell(8).setCellValue(deviceEvent.getDuration());
			excelRow.createCell(9).setCellValue(deviceEvent.getHmrInHours());
		}
	}
	
	
	
	public HSSFCellStyle createCellStyle(HSSFWorkbook workBook,String dataFormat){
		HSSFCellStyle hssfCellStyle = workBook.createCellStyle();
		if(Objects.nonNull(dataFormat)){
			CreationHelper createHelper = workBook.getCreationHelper();
	        // Set the date format of date
			hssfCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
	                dataFormat));
			hssfCellStyle.setWrapText(true);
		}
		return hssfCellStyle;
	}
	///change
	public void autoSizeColumns(HSSFSheet excelSheet,int columnCount){
		for (int i = 0; i < columnCount; i++){
			excelSheet.autoSizeColumn(i);
		}
	}

	public StringBuilder getDateRangeReport(StringBuilder dateRangeReport,
			LocalDate from, LocalDate to) {
		log.debug("From Date" + from.toString("MM/dd/yyyy"));
		log.debug("To Date" + to.toString("MM/dd/yyyy"));
		StringBuilder sb = new StringBuilder();
		sb.append(from);
		StringBuilder sb1 = new StringBuilder();
		sb1.append(to);
		dateRangeReport.append(from.toString("MM/dd/yyyy"));
		dateRangeReport.append(" to ");
		dateRangeReport.append(to.toString("MM/dd/yyyy"));
		return dateRangeReport;
	}
	public void createExcelOutputNewExcelForAll(HttpServletResponse response,
			List<PatientVestDeviceData> deviceEventsListVest,
			PatientDevicesAssoc checkPatientTypeVest, List<PatientVestDeviceDataMonarch> deviceEventsListMonarch, PatientDevicesAssoc checkPatientTypeMonarch, PatientInfo patientInfo, String dateRangeReport) throws IOException {
		
		log.debug("Received Device Data for Vest :"+deviceEventsListVest+" & Monarch"+deviceEventsListMonarch);
		
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Vest");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
        
		// Report Date as current date in mm/dd/YYYY
		String ReportDate = getReportDate();

		String[] headerVest1 = { deviceEventsListVest.get(0).getPatient().getHillromId(),
				deviceEventsListVest.get(0).getPatient().getFirstName(),
				deviceEventsListVest.get(0).getPatient().getLastName(), " ", "VEST", " ", " ",
				checkPatientTypeVest.getSerialNumber(), " ", ReportDate, DATE_RANGE_REPORT,
				dateRangeReport };
		setExcelHeader(excelSheet, headerVest1);
        setExcelRowGrayColor(workBook,excelSheet);

		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataExcelDTO>>>> therapySessions = PatientVestDeviceTherapyUtil.prepareTherapySessionFromDeviceDataForExcel(deviceEventsListVest);
			
			String[] headerVest2 = {DATE,TIME, EVENT,SERIAL_NO,DEVICE_ADDRESS,HUB_ADDRESS,FREQUENCY,PRESSURE,DURATION,HMR};
	        setExcelHeader_Row3(excelSheet,headerVest2);
	        setExcelRows_3(workBook, excelSheet, deviceEventsListVest,therapySessions);
		} catch (Exception e) {
			e.printStackTrace();
		}
        autoSizeColumns(excelSheet,12);

        //HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheetMonarch = workBook.createSheet("Therapy Report Monarch");
        /* Freeze top row alone */
        excelSheetMonarch.createFreezePane(0,1);

		String[] headerMonarch1 = { patientInfo.getHillromId(),
				patientInfo.getFirstName(),
				patientInfo.getLastName(), " ", "MONARCH", " ",
				checkPatientTypeMonarch.getSerialNumber(), " ", ReportDate,
				DATE_RANGE_REPORT, dateRangeReport };
		setExcelHeader(excelSheetMonarch, headerMonarch1);
        
        setExcelRowGrayColor_Monarch(workBook,excelSheetMonarch);
        
		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataMonarch>>>> therapySessions = PatientVestDeviceTherapyUtilMonarch.prepareTherapySessionFromDeviceMonarchDataForExcel(deviceEventsListMonarch);
			
			String[] headerMonarch2 = {DATE,TIME, EVENT,SERIAL_NO,WIFIorLTE_SERIAL_NO,FREQUENCY,INTENSITY,DURATION,HMR};
	        setExcelHeader_Row3(excelSheetMonarch,headerMonarch2);
	        setExcelRows_3_ForMonarch(workBook, excelSheetMonarch, deviceEventsListMonarch,therapySessions);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		autoSizeColumns(excelSheetMonarch,11);

		workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
		
	}
	
}
