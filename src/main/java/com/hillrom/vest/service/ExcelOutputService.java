package com.hillrom.vest.service;

import static com.hillrom.vest.config.Constants.DATE;
import static com.hillrom.vest.config.Constants.DEVICE_ADDRESS;
import static com.hillrom.vest.config.Constants.DURATION;
import static com.hillrom.vest.config.Constants.EVENT;
import static com.hillrom.vest.config.Constants.FREQUENCY;
import static com.hillrom.vest.config.Constants.HILLROM_ID;
import static com.hillrom.vest.config.Constants.HMR;
import static com.hillrom.vest.config.Constants.HUB_ADDRESS;
import static com.hillrom.vest.config.Constants.INTENSITY;
import static com.hillrom.vest.config.Constants.PATIENT_ID;
import static com.hillrom.vest.config.Constants.PRESSURE;
import static com.hillrom.vest.config.Constants.SERIAL_NO;
import static com.hillrom.vest.config.Constants.TIME;
import static com.hillrom.vest.config.Constants.WIFIorLTE_SERIAL_NO;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hillrom.monarch.service.util.PatientVestDeviceTherapyUtilMonarch;
import com.hillrom.vest.domain.PatientVestDeviceData;
import com.hillrom.vest.domain.PatientVestDeviceDataMonarch;
import com.hillrom.vest.service.util.PatientVestDeviceTherapyUtil;
import com.hillrom.vest.web.rest.dto.PatientVestDeviceDataExcelDTO;


@Service
public class ExcelOutputService {

	private static final Logger log = LoggerFactory.getLogger(ExcelOutputService.class);
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
	 * @param deviceType
	 * @param dateRangeReport
	 * @throws IOException
	 */
	public void createExcelOutputExcel_Vest(HttpServletResponse response,List<PatientVestDeviceData> deviceEventsList, String deviceType, String dateRangeReport) throws IOException{
		log.debug("Received Device Data "+deviceEventsList);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Vest");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
        
        for(PatientVestDeviceData vestData : deviceEventsList ){
        	//Report Date as current date in mm/dd/YYYY
        	String ReportDate = getReportDate();
            
        	String[] header = { vestData.getPatient().getId(),vestData.getPatient().getFirstName(),vestData.getPatient().getLastName()," ",
        			deviceType," "," ",vestData.getSerialNumber()," ",ReportDate,dateRangeReport};
            setExcelHeader(excelSheet,header);
        }
        

		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataExcelDTO>>>> therapySessions = PatientVestDeviceTherapyUtil.prepareTherapySessionFromDeviceDataForExcel(deviceEventsList);
			
			String[] header = {DATE,TIME, EVENT,SERIAL_NO,DEVICE_ADDRESS,HUB_ADDRESS,FREQUENCY,PRESSURE,DURATION,HMR};
	        setExcelHeader_Row3(excelSheet,header);
	        setExcelRows_3(workBook, excelSheet, deviceEventsList,therapySessions);
			
			log.debug("TherapySession"+therapySessions.get(0)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
        autoSizeColumns(excelSheet,11);
        
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
			String deviceType, String dateRangeReport) throws IOException {
		log.debug("Received Device Data "+deviceEventsList);
		
		response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Monarch");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
    	
        for(PatientVestDeviceDataMonarch monarchData : deviceEventsList ){
        	//Report Date as current date in mm/dd/YYYY
        	String ReportDate = getReportDate();
            
        	String[] header = { monarchData.getPatient().getHillromId(),monarchData.getPatient().getFirstName(),monarchData.getPatient().getLastName()," ",
        			deviceType," "," ",monarchData.getSerialNumber()," ",ReportDate,dateRangeReport};
            setExcelHeader(excelSheet,header);
        }
        
		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataMonarch>>>> therapySessions = PatientVestDeviceTherapyUtilMonarch.prepareTherapySessionFromDeviceMonarchDataForExcel(deviceEventsList);
			
			String[] header = {DATE,TIME, EVENT,SERIAL_NO,WIFIorLTE_SERIAL_NO,FREQUENCY,PRESSURE,DURATION,HMR};
	        setExcelHeader_Row3(excelSheet,header);
	        setExcelRows_3_ForMonarch(workBook, excelSheet, deviceEventsList,therapySessions);
			
			log.debug("TherapySession"+therapySessions.get(0)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		autoSizeColumns(excelSheet,11);
        workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
	}

	private void setExcelRows_3_ForMonarch(
			HSSFWorkbook workBook,
			HSSFSheet excelSheet,
			List<PatientVestDeviceDataMonarch> deviceEventsList,
			Map<DateTime, Map<Integer, Map<Long, List<PatientVestDeviceDataMonarch>>>> therapySessions) {

		int record = 3;
		HSSFCellStyle dateStyle = createCellStyle(workBook,"m/d/yy");
		HSSFCellStyle timeStyle = createCellStyle(workBook,"h:mm AM/PM");
		
		for (PatientVestDeviceDataMonarch deviceEvent : deviceEventsList) {
			
				Map<Integer, Map<Long, List<PatientVestDeviceDataMonarch>>>	therapySessionsForTheDay = new LinkedHashMap<>();
				therapySessionsForTheDay = therapySessions.get(deviceEvent.getDate());
				
			Set<Map.Entry<Integer, Map<Long, List<PatientVestDeviceDataMonarch>>>> sessionForDay = therapySessionsForTheDay.entrySet();
			log.debug("therapySessionSet:" + sessionForDay);
			
			for(Map.Entry<Integer, Map<Long, List<PatientVestDeviceDataMonarch>>> therapySession : sessionForDay){
				
				Map<Long, List<PatientVestDeviceDataMonarch>> therapySessionForTheDay = new LinkedHashMap<>();
				therapySessionForTheDay =  therapySession.getValue();
				log.debug("TherapySessionForTheDay:"+therapySessionForTheDay.size());
				
					for(Map.Entry<Long, List<PatientVestDeviceDataMonarch>> session : therapySessionForTheDay.entrySet()){
						 
						 List<PatientVestDeviceDataMonarch> eventDetailsList = new LinkedList<>();
						 
						 eventDetailsList = session.getValue();
						 
						 int duration = 0; int i = 0; int j = 0;
						 for(PatientVestDeviceDataMonarch eventDetails : eventDetailsList){
							 

								HSSFRow excelRow = excelSheet.createRow(record++);
								//excelRow.createCell(0).setCellValue(eventDetails.getPatient().getHillromId());
								if(++i==1){
									HSSFCell dateCell = excelRow.createCell(0);
									dateCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									dateCell.setCellValue(eventDetails.getDate().toDate());
									dateCell.setCellStyle(dateStyle);
								}
								HSSFCell timeCell = excelRow.createCell(1);
								timeCell.setCellValue(eventDetails.getDate().toDate());
								timeCell.setCellStyle(timeStyle);
								
								excelRow.createCell(2).setCellValue(eventDetails.getEventCode());
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
								if(++j==eventDetailsList.size()){
									log.debug("Inside total : " + j);
										//excelRow = excelSheet.createRow(record++);
										
										CellStyle style1 = workBook.createCellStyle();
										style1.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
										excelRow = excelSheet.createRow(record++);
										excelRow.setRowStyle(style1);
										
									
										HSSFCellStyle style = workBook.createCellStyle();
										Font font = workBook.createFont();//Create font
									    font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
									    
									    style.setFont(font);//set it to bold
										HSSFCell dateCell = excelRow.createCell(2);
										dateCell.setCellValue("THERAPY SESSION TOTAL");
										dateCell.setCellStyle(style);
										
										HSSFCell dateCell2 = excelRow.createCell(7);
										dateCell2.setCellValue(duration);
										dateCell2.setCellStyle(style);
								}
							}
						} 
				}
		}
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
		
		for (PatientVestDeviceData deviceEvent : deviceEventsList) {
			
				Map<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>>	therapySessionsForTheDay = new LinkedHashMap<>();
				log.debug("deviceEvent.getDate() :" + deviceEvent.getDate());
				therapySessionsForTheDay = therapySessions.get(deviceEvent.getDate());
				log.debug("therapySessionsForTheDay :" + therapySessionsForTheDay);
				
			if(Objects.nonNull(therapySessionsForTheDay)){
				
			Set<Map.Entry<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>>> sessionForDay = therapySessionsForTheDay.entrySet();
			log.debug("therapySessionSet:" + sessionForDay);
			
			for(Map.Entry<Integer, Map<Long, List<PatientVestDeviceDataExcelDTO>>> therapySession : sessionForDay){
				
				Map<Long, List<PatientVestDeviceDataExcelDTO>> therapySessionForTheDay = new LinkedHashMap<>();
				therapySessionForTheDay =  therapySession.getValue();
				log.debug("TherapySessionForTheDay:"+therapySessionForTheDay.size());
				
					for(Map.Entry<Long, List<PatientVestDeviceDataExcelDTO>> session : therapySessionForTheDay.entrySet()){
						 
						 List<PatientVestDeviceDataExcelDTO> eventDetailsList = new LinkedList<PatientVestDeviceDataExcelDTO>();
						 
						 eventDetailsList = session.getValue();
						 
						 int duration = 0; int i = 0; int j = 0;
						 for(PatientVestDeviceDataExcelDTO eventDetails : eventDetailsList){

							 log.debug("eventDetails:"+eventDetails.getTimestamp());
							 
							 String eventCode = eventDetails.getEventId().split(":")[0];
							 
							 String eventCodeStr = getEventStringByEventCode(eventCode);
							 
							 HSSFRow excelRow = excelSheet.createRow(record++);
								//excelRow.createCell(0).setCellValue(deviceEvent.getPatientBlueToothAddress());
								
								if(++i==1){
									log.debug("First Row : ");
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
								if(++j==eventDetailsList.size()){
									log.debug("Inside total : " + j);
										
									
										CellStyle style1 = workBook.createCellStyle();
										style1.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
										excelRow = excelSheet.createRow(record++);
										excelRow.setRowStyle(style1);
										
										HSSFCellStyle style = workBook.createCellStyle();
										Font font = workBook.createFont();//Create font
									    font.setBoldweight(Font.BOLDWEIGHT_BOLD);//Make font bold
									    
									    style.setFont(font);//set it to bold
										HSSFCell dateCell = excelRow.createCell(2);
										dateCell.setCellValue("THERAPY SESSION TOTAL");
										dateCell.setCellStyle(style);
										
										HSSFCell dateCell2 = excelRow.createCell(8);
										dateCell2.setCellValue(duration);
										dateCell2.setCellStyle(style);
									
									 //excelRow.createCell(2).setCellValue("THERAPY SESSION TOTAL");
									// excelRow.createCell(8).setCellValue(duration);
								}

							}

						} 
				}
			}
		}
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
			eventString = 0 + ":Unknown";
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
			List<PatientVestDeviceDataMonarch> deviceEventsListMonarch, String dateRangeReport) throws IOException {
		
		log.debug("Received Device Data for Vest :"+deviceEventsListVest+" & Monarch"+deviceEventsListMonarch);
		
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=TherapyReport.xls");
        
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheet = workBook.createSheet("Therapy Report Vest");
        /* Freeze top row alone */
        excelSheet.createFreezePane(0,1);
        
        for(PatientVestDeviceData vestData : deviceEventsListVest ){
        	//Report Date as current date in mm/dd/YYYY
        	String ReportDate = getReportDate();
            
        	String[] header = { vestData.getPatient().getId(),vestData.getPatient().getFirstName(),vestData.getPatient().getLastName()," ",
        			"VEST"," "," ",vestData.getSerialNumber()," ",ReportDate,dateRangeReport};
            setExcelHeader(excelSheet,header);
        }
        

		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataExcelDTO>>>> therapySessions = PatientVestDeviceTherapyUtil.prepareTherapySessionFromDeviceDataForExcel(deviceEventsListVest);
			
			String[] header = {DATE,TIME, EVENT,SERIAL_NO,DEVICE_ADDRESS,HUB_ADDRESS,FREQUENCY,PRESSURE,DURATION,HMR};
	        setExcelHeader_Row3(excelSheet,header);
	        setExcelRows_3(workBook, excelSheet, deviceEventsListVest,therapySessions);
			
			log.debug("TherapySession"+therapySessions.get(0)); 
		} catch (Exception e) {
			e.printStackTrace();
		}
        autoSizeColumns(excelSheet,11);

        //HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet excelSheetMonarch = workBook.createSheet("Therapy Report Monarch");
        /* Freeze top row alone */
        excelSheetMonarch.createFreezePane(0,1);
    	
        for(PatientVestDeviceDataMonarch monarchData : deviceEventsListMonarch ){
        	//Report Date as current date in mm/dd/YYYY
        	String ReportDate = getReportDate();
            
        	String[] header = { monarchData.getPatient().getHillromId(),monarchData.getPatient().getFirstName(),monarchData.getPatient().getLastName()," ",
        			"MONARCH"," "," ",monarchData.getSerialNumber()," ",ReportDate,dateRangeReport};
            setExcelHeader(excelSheetMonarch,header);
        }
        
		try {
			Map<DateTime,Map<Integer,Map<Long,List<PatientVestDeviceDataMonarch>>>> therapySessions = PatientVestDeviceTherapyUtilMonarch.prepareTherapySessionFromDeviceMonarchDataForExcel(deviceEventsListMonarch);
			
			String[] header = {DATE,TIME, EVENT,SERIAL_NO,WIFIorLTE_SERIAL_NO,FREQUENCY,INTENSITY,DURATION,HMR};
	        setExcelHeader_Row3(excelSheetMonarch,header);
	        setExcelRows_3_ForMonarch(workBook, excelSheetMonarch, deviceEventsListMonarch,therapySessions);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		autoSizeColumns(excelSheetMonarch,11);

		workBook.write(response.getOutputStream());
        response.getOutputStream().flush();
		
	}
	
}
