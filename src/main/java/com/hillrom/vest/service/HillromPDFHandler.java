package com.hillrom.vest.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.PatientProtocolData;
import com.hillrom.vest.domain.PatientProtocolDataMonarch;
import com.hillrom.vest.domain.User;

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
		return hillromPdfFactory.createPDFDoc(file, currentUser, patientUser, patientProtocolDataList);

	}
	
	public File createPDFDocMonarch(File file, User currentUser, User patientUser,
			List<PatientProtocolDataMonarch> patientProtocolDataList) throws IOException {
		return hillromPdfFactory.createPDFDocMonarch(file, currentUser, patientUser, patientProtocolDataList);

	}
}