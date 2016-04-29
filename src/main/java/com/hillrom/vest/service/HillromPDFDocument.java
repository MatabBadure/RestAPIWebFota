package com.hillrom.vest.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class HillromPDFDocument extends PDDocument{
	
	public PDFont fontPlain = PDType1Font.HELVETICA;
	public PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	public String pdfHeaderText = "Hill-Rom Respiratory Care";
	public String hillromText = "HillRom";
	public String visiViewText = "VisiView™ Health Portal";
	public Float leftOffSet = 20.0f;
	public Integer rightOffset = 20;
	public String[]  protocolTableHeader = "Type,Treatment Per Day,Minutes Per Treatment,Frequency Per Treatment,Pressure Per Treatment".split(",");
	
}
