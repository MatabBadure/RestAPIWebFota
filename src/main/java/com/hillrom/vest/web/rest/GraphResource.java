package com.hillrom.vest.web.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


/**
 * REST controller for managing Graph Functions.
 */
@RestController
@RequestMapping("/api")
public class GraphResource {

    private final Logger log = LoggerFactory.getLogger(GraphResource.class);



    /**
     * POST  /securityQuestions -> Post base64 Graph PDF string.
     * @throws IOException 
     */
    @RequestMapping(value = "/graph/pdfDownload",
            method = RequestMethod.GET,
            produces = "application/pdf")
    public ResponseEntity<?> downloadPDF(HttpServletResponse response) throws IOException {
    	Document document = new Document();
        try
        {
           PdfWriter.getInstance(document, response.getOutputStream());
           document.open();
           document.add(new Paragraph("<h1>A Hello World PDF document.</h1>"));
           document.add(new Paragraph("<h1>Next line.</h1>"));
           document.close();
           return null;
        } catch (DocumentException e)
        {
           e.printStackTrace();
        } catch (FileNotFoundException e)
        {
           e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public void emitJPG( HttpServletRequest request,
    	    HttpServletResponse response, String svgString )
    	{
    	    response.setContentType("image/jpeg");

    	    JPEGTranscoder t = new JPEGTranscoder();
    	    t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
    	                         new Float(.8));

    	    TranscoderInput input =
    	        new TranscoderInput( new StringReader(svgString) );
    	    try {
    	        TranscoderOutput output =
    	            new TranscoderOutput(response.getOutputStream());
    	        t.transcode(input, output);
    	        response.getOutputStream().close();
    	    }
    	    catch (Exception e)
    	    {
    	        e.printStackTrace();
    	    }
    	}
}
