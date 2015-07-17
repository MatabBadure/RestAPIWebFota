package com.hillrom.vest.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "PATIENT_ID_SEQUENCE")
public class PatientIdSequence {

	@Id
    @GenericGenerator(name = "table", strategy = "enhanced-table", parameters = {
            @org.hibernate.annotations.Parameter(name = "table_name", value = "PATIENT_ID_SEQUENCE")
    })
    @GeneratedValue(generator = "table", strategy=GenerationType.TABLE)
    private Long id;
}
