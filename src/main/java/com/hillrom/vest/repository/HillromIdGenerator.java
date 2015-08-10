package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.Calendar;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class HillromIdGenerator {

	private static final int LENGTH_OF_PADDED_STRING = 6;
	private static final char PAD_CHAR = '0';
	@Inject
	private EntityManager entityManager;

	int year  = Calendar.YEAR;
	String hillromId = "HR"+year;
	
	public String getNextPatientHillromId(){
		Query query = entityManager.createNativeQuery("Select id from patient_id_sequence ");
		BigInteger id = (BigInteger) query.getSingleResult();
		int idValue = id == null ? 0 : id.intValue(); 
		String updateQuery = null;
		if(null == id || id.intValue() == 0){
			++idValue;
			updateQuery = "insert into patient_id_sequence(id) values(1)";
		}else{
			++idValue;
			updateQuery = "update patient_id_sequence set id = "+idValue+" where id = "+(idValue - 1);
		}
		entityManager.createNativeQuery(updateQuery).executeUpdate();
		return hillromId.concat(StringUtils.leftPad(id.toString(), LENGTH_OF_PADDED_STRING,PAD_CHAR));
		
	}
	
	public String getNextClinicHillromId(){
		Query query = entityManager.createNativeQuery("Select id from clinic_id_sequence ");
		BigInteger id = (BigInteger) query.getSingleResult();
		int idValue = id == null ? 0 : id.intValue(); 
		String updateQuery = null;
		if(null == id || id.equals(0)){
			++idValue;
			updateQuery = "insert into clinic_id_sequence(id) values(1)";
		}else{
			++idValue;
			updateQuery = "update clinic_id_sequence set id = "+idValue+" where id = "+(idValue - 1);
		}
		entityManager.createNativeQuery(updateQuery).executeUpdate();
		return hillromId.concat(StringUtils.leftPad(id.toString(), LENGTH_OF_PADDED_STRING,PAD_CHAR));
		
	}
}
