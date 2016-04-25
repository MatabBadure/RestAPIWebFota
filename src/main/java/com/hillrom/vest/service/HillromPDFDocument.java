package com.hillrom.vest.service;

import java.io.File;
import java.io.IOException;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class HillromPDFDocument {
	@Inject
	private HillromPdfFactory hillromPdfFactory;
	
    public DataSource createPDFDoc(File file) throws IOException{
    	
    	DataSource dataSource; 
    	
    	PDDocument document = new PDDocument();
    	PDPageContentStream pageContentStream = hillromPdfFactory.getPDPageContentStream(document);
    	PDPage page1 = new PDPage(PDRectangle.A4);
    	PDRectangle rect = page1.getMediaBox();
    	PDFont fontPlain = PDType1Font.HELVETICA;
        PDFont fontBold = PDType1Font.HELVETICA_BOLD;
        PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
        PDFont fontMono = PDType1Font.COURIER;
    	
    	int line = 0;
	    pageContentStream.beginText();
	    pageContentStream.setFont(fontPlain, 12);
	    pageContentStream.newLineAtOffset(100, rect.getHeight() - 50*(++line));
	    pageContentStream.showText("Hill-Rom Visiview Application");
	    pageContentStream.endText();
	
	    pageContentStream.beginText();
	    pageContentStream.setFont(fontItalic, 12);
	    pageContentStream.newLineAtOffset(100, rect.getHeight() - 50*(++line));
	    pageContentStream.showText("Italic");
	    pageContentStream.endText();
	
	    pageContentStream.beginText();
	    pageContentStream.setFont(fontBold, 12);
	    pageContentStream.newLineAtOffset(100, rect.getHeight() - 50*(++line));
	    pageContentStream.showText("Bold");
	    pageContentStream.endText();
	
	    hillromPdfFactory.closePDPageContentStream();
	    document.save(file);
        document.close();
        return  new FileDataSource(file);
        
    }
}
