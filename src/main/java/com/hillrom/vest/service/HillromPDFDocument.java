package com.hillrom.vest.service;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class HillromPDFDocument {
	
	public static PDFont fontPlain = PDType1Font.HELVETICA;
	public static PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	public static String pdfHeaderText = "Hill-Rom Respiratory Care";
	public static String hillromText = "HillRom";
	public static String visiViewText = "VisiView™ Health Portal";
	public static Float leftOffSet = 20.0f;
	public static Integer rightOffset = 20;
	public static String[]  protocolTableHeader = "Type,Treatment Per Day,Minutes Per Treatment,Frequency Per Treatment,Pressure Per Treatment".split(",");
	
}
