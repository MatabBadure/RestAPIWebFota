package com.hillrom.vest.service;



import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.util.DateUtil;

@Service
public class HillromPdfFactory{
	
	
	HillromPDFDocument document;
		
	public File createPDFDoc(File file, User currentUser, User patientUser,
			List<PatientProtocolData> patientProtocolDataList) throws IOException {
		document = new HillromPDFDocument();

		PDPageContentStream pdPageContentStream;
		PDPage pdPage = new PDPage(PDRectangle.A4);
		pdPageContentStream = new PDPageContentStream(document, pdPage);
	    document.addPage(pdPage);
	    
		PDRectangle pdRectangle = pdPage.getBBox();

		int line = 50;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontBold, 8);
		pdPageContentStream.newLineAtOffset((pdRectangle.getWidth()/2 - document.pdfHeaderText.length()) - 40,
		pdRectangle.getHeight() - line);
		pdPageContentStream.showText(document.pdfHeaderText);
		pdPageContentStream.endText();

		line += 20;

		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontBold, 12);
		pdPageContentStream.setNonStrokingColor(124, 163, 220);
		pdPageContentStream.newLineAtOffset(pdRectangle.getWidth() - document.hillromText.length() - 60,
				pdRectangle.getHeight() - line);
		pdPageContentStream.showText(document.hillromText);
		pdPageContentStream.endText();
		
		line += 10;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontBold, 6);
		pdPageContentStream.setNonStrokingColor(Color.GRAY);
		pdPageContentStream.newLineAtOffset(pdRectangle.getWidth() - document.visiViewText.length() - 70,
		pdRectangle.getHeight() - line);
		pdPageContentStream.showText(document.visiViewText);
		pdPageContentStream.endText();

		line += 10;
		pdPageContentStream.setNonStrokingColor(Color.black);
		pdPageContentStream.moveTo(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.lineTo(pdRectangle.getWidth() - document.rightOffset, pdRectangle.getHeight() - line);
		pdPageContentStream.stroke();

		line += 20;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontPlain, 8);
		pdPageContentStream.setNonStrokingColor(Color.BLACK);
		pdPageContentStream.newLineAtOffset(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Report Generation Date : ");
		pdPageContentStream.setNonStrokingColor(124, 163, 220);
		pdPageContentStream.showText(DateUtil.formatDate(new LocalDate(), null));
		pdPageContentStream.endText();
		
		line += 20;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontPlain, 8);
		pdPageContentStream.setNonStrokingColor(Color.GRAY);
		pdPageContentStream.newLineAtOffset(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Patient Name ");
		pdPageContentStream.endText();
		
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(document.leftOffSet+100, pdRectangle.getHeight() - line);
		String name = getName(patientUser);
		pdPageContentStream.showText(name);
		pdPageContentStream.endText();
				
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(pdRectangle.getWidth()/2, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Patient DOB");
		pdPageContentStream.endText();
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(pdRectangle.getWidth()/2+document.leftOffSet+100, pdRectangle.getHeight() - line);
		pdPageContentStream.showText(DateUtil.formatDate(patientUser.getDob(), null));
		pdPageContentStream.endText();
		

		line += 20;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontPlain, 8);
		pdPageContentStream.setNonStrokingColor(Color.GRAY);
		pdPageContentStream.newLineAtOffset(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Prescriber Name");
		pdPageContentStream.endText();
		
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(document.leftOffSet+100, pdRectangle.getHeight() - line);
		name = getName(currentUser);
		pdPageContentStream.showText(name);
		pdPageContentStream.endText();
		
		
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(pdRectangle.getWidth()/2, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Date");
		pdPageContentStream.endText();
		
		pdPageContentStream.beginText();
		pdPageContentStream.newLineAtOffset(pdRectangle.getWidth()/2+document.leftOffSet+100, pdRectangle.getHeight() - line);
		pdPageContentStream.showText(DateUtil.formatDate(new LocalDate(), null));
		pdPageContentStream.endText();

		line += 50;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontPlain, 12);
		pdPageContentStream.setNonStrokingColor(124, 163, 220);
		pdPageContentStream.newLineAtOffset(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Protocol");
		pdPageContentStream.endText();

		line += 20;
		pdPageContentStream.setFont(document.fontPlain, 8);
		pdPageContentStream.setNonStrokingColor(124, 163, 220);
		
		
		addProtocalHeader(document.protocolTableHeader, pdPageContentStream, pdRectangle.getHeight() - line);

		line += 10;

		pdPageContentStream.setNonStrokingColor(Color.GRAY);
		pdPageContentStream.moveTo(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.lineTo(pdRectangle.getWidth() - document.rightOffset, pdRectangle.getHeight() - line);
		pdPageContentStream.stroke();
		
		pdPageContentStream.setFont(document.fontPlain, 8);
		pdPageContentStream.setStrokingColor(Color.DARK_GRAY);
		
		for (PatientProtocolData patientProtocolData : patientProtocolDataList) {
			line += 10;	
			
			float  i = document.leftOffSet + 90;
			
			String treatementLebel = Objects.nonNull(patientProtocolData.getTreatmentLabel())?patientProtocolData.getTreatmentLabel() : "";
			addNewLine(patientProtocolData.getType()+" "+ treatementLebel, pdPageContentStream, document.leftOffSet+20, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getTreatmentsPerDay()), pdPageContentStream, i+=90, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getMinMinutesPerTreatment()), pdPageContentStream, i+=100, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getMinFrequency()), pdPageContentStream, i+=100, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getMinPressure()), pdPageContentStream, i+=100, pdRectangle.getHeight() - line);
			line += 10;
			pdPageContentStream.setNonStrokingColor(Color.GRAY);
			pdPageContentStream.moveTo(document.leftOffSet, pdRectangle.getHeight() - line);
			pdPageContentStream.lineTo(pdRectangle.getWidth() - document.rightOffset, pdRectangle.getHeight() - line);
			pdPageContentStream.stroke();
		}

		line += 60;
		pdPageContentStream.beginText();
		pdPageContentStream.setFont(document.fontBold, 8);
		pdPageContentStream.setStrokingColor(Color.BLACK);
		pdPageContentStream.newLineAtOffset(document.leftOffSet, pdRectangle.getHeight() - line);
		pdPageContentStream.showText("Signature: Electronically signed by " + currentUser.getFirstName() + " "
				+ currentUser.getLastName()+" on "+DateUtil.formatDateWithDaySuffix(new DateTime(), document.SIGN_DATETIME_PATTERN)+".");
		pdPageContentStream.endText();
		
		pdPageContentStream.setNonStrokingColor(Color.BLACK);
		pdPageContentStream.moveTo(document.leftOffSet + 50, line);
		pdPageContentStream.lineTo(pdRectangle.getWidth()/2,line);
		pdPageContentStream.stroke();
		
		line = 30;

		pdPageContentStream.setNonStrokingColor(Color.BLACK);
		pdPageContentStream.moveTo(document.leftOffSet, line);
		pdPageContentStream.lineTo(pdRectangle.getWidth() - document.rightOffset,line);
		pdPageContentStream.stroke();
		
		
		
		pdPageContentStream.close();
		document.save(file);
		document.close();
		return file;
	}
	
	private void addProtocalHeader(String[] headerArr, PDPageContentStream pageContentStream, Float height)
			throws IOException {
		int i = 40;
		for (String str : headerArr) {
			addNewLine(str, pageContentStream, (float) (document.leftOffSet + i), height);
			i += 100;
		}
	}

	private void addNewLine(String strValue, PDPageContentStream pageContentStream, Float x, Float y)
			throws IOException {
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(x, y);
		pageContentStream.showText(strValue);
		pageContentStream.endText();
	}
	
	private String getName(User user) {
		StringBuilder name  = new StringBuilder(user.getFirstName()).append(" ").append(user.getLastName());
		return name.length()>25 ? name.substring(0, 22) + "..." : name.toString() ;
	}
}
