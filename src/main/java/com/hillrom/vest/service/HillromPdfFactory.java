package com.hillrom.vest.service;

import java.io.IOException;
import java.util.Objects;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Service;

@Service
public class HillromPdfFactory{
	
	PDPageContentStream pdPageContentStream;
	
	public PDPageContentStream getPDPageContentStream(PDDocument document) throws IOException{
		
	    PDPage pdPage = new PDPage(PDRectangle.A4);
	    document.addPage(pdPage);
	    pdPageContentStream = new PDPageContentStream(document, pdPage);
	   
	    return pdPageContentStream;
	}
	
	public void closePDPageContentStream() throws IOException{
		if(Objects.nonNull(pdPageContentStream))
		    pdPageContentStream.close();
	}

	
	
	

}
