package com.hillrom.vest.service;

import static com.hillrom.vest.service.HillromPDFDocument.fontBold;
import static com.hillrom.vest.service.HillromPDFDocument.fontPlain;
import static com.hillrom.vest.service.HillromPDFDocument.hillromText;
import static com.hillrom.vest.service.HillromPDFDocument.leftOffSet;
import static com.hillrom.vest.service.HillromPDFDocument.pdfHeaderText;
import static com.hillrom.vest.service.HillromPDFDocument.protocolTableHeader;
import static com.hillrom.vest.service.HillromPDFDocument.rightOffset;
import static com.hillrom.vest.service.HillromPDFDocument.visiViewText;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject; 

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.util.DateUtil;

@Service
public class HillromPDFHandler {
	
	@Inject
	private HillromPdfFactory hillromPdfFactory; 
	
	public boolean deletePdf(File file) {
		if(file.exists())
			return file.delete();
		else
			return false;
	}
	
	public File createPDFDoc(File file, User currentUser, User patientUser,
			List<PatientProtocolData> patientProtocolDataList) throws IOException {

		PDDocument document = new PDDocument();
		PDPageContentStream pageContentStream = hillromPdfFactory.getPDPageContentStream(document);
		PDPage pdPage = new PDPage(PDRectangle.A4);
		PDRectangle pdRectangle = pdPage.getBBox();

		int line = 50;
		pageContentStream.beginText();
		pageContentStream.setFont(fontBold, 8);
		pageContentStream.newLineAtOffset((pdRectangle.getWidth()/2 - pdfHeaderText.length()) - 60,
		pdRectangle.getHeight() - line);
		pageContentStream.showText(pdfHeaderText);
		pageContentStream.endText();

		line += 20;

		pageContentStream.beginText();
		pageContentStream.setFont(fontBold, 12);
		pageContentStream.setNonStrokingColor(124, 163, 220);
		pageContentStream.newLineAtOffset(pdRectangle.getWidth() - hillromText.length() - 60,
				pdRectangle.getHeight() - line);
		pageContentStream.showText(hillromText);
		pageContentStream.endText();
		
		line += 10;
		pageContentStream.beginText();
		pageContentStream.setFont(fontBold, 6);
		pageContentStream.setNonStrokingColor(Color.GRAY);
		pageContentStream.newLineAtOffset(pdRectangle.getWidth() - visiViewText.length() - 70,
		pdRectangle.getHeight() - line);
		pageContentStream.showText(visiViewText);
		pageContentStream.endText();

		line += 10;
		pageContentStream.setNonStrokingColor(Color.black);
		pageContentStream.moveTo(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.lineTo(pdRectangle.getWidth() - rightOffset, pdRectangle.getHeight() - line);
		pageContentStream.stroke();

		line += 20;
		pageContentStream.beginText();
		pageContentStream.setFont(fontPlain, 8);
		pageContentStream.setNonStrokingColor(Color.BLACK);
		pageContentStream.newLineAtOffset(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.showText("Report Generation Date : ");
		pageContentStream.setNonStrokingColor(124, 163, 220);
		pageContentStream.showText(DateUtil.formatDate(new LocalDate(), null));
		pageContentStream.endText();
		
		line += 20;
		pageContentStream.beginText();
		pageContentStream.setFont(fontPlain, 8);
		pageContentStream.setNonStrokingColor(Color.GRAY);
		pageContentStream.newLineAtOffset(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.showText("Patient Name ");
		pageContentStream.endText();
		
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(leftOffSet+100, pdRectangle.getHeight() - line);
		String name = getName(patientUser);
		pageContentStream.showText(name);
		pageContentStream.endText();
				
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(pdRectangle.getWidth()/2, pdRectangle.getHeight() - line);
		pageContentStream.showText("Patient DOB");
		pageContentStream.endText();
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(pdRectangle.getWidth()/2+leftOffSet+100, pdRectangle.getHeight() - line);
		pageContentStream.showText(DateUtil.formatDate(patientUser.getDob(), null));
		pageContentStream.endText();
		

		line += 20;
		pageContentStream.beginText();
		pageContentStream.setFont(fontPlain, 8);
		pageContentStream.setNonStrokingColor(Color.GRAY);
		pageContentStream.newLineAtOffset(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.showText("Prescriber Name");
		pageContentStream.endText();
		
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(leftOffSet+100, pdRectangle.getHeight() - line);
		name = getName(currentUser);
		pageContentStream.showText(name);
		pageContentStream.endText();
		
		
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(pdRectangle.getWidth()/2, pdRectangle.getHeight() - line);
		pageContentStream.showText("Date");
		pageContentStream.endText();
		
		pageContentStream.beginText();
		pageContentStream.newLineAtOffset(pdRectangle.getWidth()/2+leftOffSet+100, pdRectangle.getHeight() - line);
		pageContentStream.showText(DateUtil.formatDate(new LocalDate(), null));
		pageContentStream.endText();

		line += 50;
		pageContentStream.beginText();
		pageContentStream.setFont(fontPlain, 12);
		pageContentStream.setNonStrokingColor(124, 163, 220);
		pageContentStream.newLineAtOffset(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.showText("Protocol");
		pageContentStream.endText();

		line += 20;
		pageContentStream.setFont(fontPlain, 8);
		pageContentStream.setNonStrokingColor(124, 163, 220);
		
		
		addProtocalHeader(protocolTableHeader, pageContentStream, pdRectangle.getHeight() - line);

		line += 10;

		pageContentStream.setNonStrokingColor(Color.GRAY);
		pageContentStream.moveTo(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.lineTo(pdRectangle.getWidth() - rightOffset, pdRectangle.getHeight() - line);
		pageContentStream.stroke();
		
		pageContentStream.setFont(fontPlain, 8);
		pageContentStream.setStrokingColor(Color.DARK_GRAY);
		for (PatientProtocolData patientProtocolData : patientProtocolDataList) {
			line += 10;	
			
			float  i = leftOffSet + 90;
			addNewLine(patientProtocolData.getType(), pageContentStream, leftOffSet+20, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getTreatmentsPerDay()), pageContentStream, i+=90, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getMinMinutesPerTreatment()), pageContentStream, i+=100, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getMinFrequency()), pageContentStream, i+=100, pdRectangle.getHeight() - line);
			addNewLine(String.valueOf(patientProtocolData.getMinPressure()), pageContentStream, i+=100, pdRectangle.getHeight() - line);
			line += 10;
			pageContentStream.setNonStrokingColor(Color.GRAY);
			pageContentStream.moveTo(leftOffSet, pdRectangle.getHeight() - line);
			pageContentStream.lineTo(pdRectangle.getWidth() - rightOffset, pdRectangle.getHeight() - line);
			pageContentStream.stroke();
		}

		line += 40;
		pageContentStream.beginText();
		pageContentStream.setFont(fontBold, 8);
		pageContentStream.setStrokingColor(Color.BLACK);
		pageContentStream.newLineAtOffset(leftOffSet, pdRectangle.getHeight() - line);
		pageContentStream.showText("This is an Electronically signed document by " + currentUser.getFirstName() + " "
				+ currentUser.getLastName());
		pageContentStream.endText();

		line = 80;
		pageContentStream.beginText();
		pageContentStream.setFont(fontBold, 8);
		pageContentStream.setStrokingColor(Color.BLACK);
		pageContentStream.newLineAtOffset(leftOffSet, line);
		pageContentStream.showText("Name: _________________________________________________________________    "
				+ "Date: _____________________________________");
		pageContentStream.endText();
		
		line = 60;
		pageContentStream.beginText();
		pageContentStream.setFont(fontBold, 8);
		pageContentStream.setStrokingColor(Color.BLACK);
		pageContentStream.newLineAtOffset(leftOffSet, line);
		pageContentStream.showText("Signature: ______________________________________");
		pageContentStream.endText();
		
		line = 30;

		pageContentStream.setNonStrokingColor(Color.BLACK);
		pageContentStream.moveTo(leftOffSet, line);
		pageContentStream.lineTo(pdRectangle.getWidth() - rightOffset,line);
		pageContentStream.stroke();

		hillromPdfFactory.closePDPageContentStream();
		document.save(file);
		document.close();
		return file;
	}
	
	private String getName(User user) {
		StringBuilder name  = new StringBuilder(user.getFirstName()).append(" ").append(user.getLastName());
		return name.length()>25 ? name.substring(0, 22) + "..." : name.toString() ;
	}

	private void addProtocalHeader(String[] headerArr, PDPageContentStream pageContentStream, Float height)
			throws IOException {
		int i = 40;
		for (String str : headerArr) {
			addNewLine(str, pageContentStream, (float) (leftOffSet + i), height);
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
}
