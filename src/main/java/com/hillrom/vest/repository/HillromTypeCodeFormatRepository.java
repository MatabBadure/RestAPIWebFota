package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.HillromTypeCodeFormat;

public interface HillromTypeCodeFormatRepository extends JpaRepository<HillromTypeCodeFormat, Long> {

	@Query("from HillromTypeCodeFormat where type = ?1")
	public List<String> findCodeValuesList(String type);
	
	@Query(nativeQuery=true,
			value="select * from HILLROM_TYPE_CODE_VALUES "
            + "where type='patient_diagnostic_code' and "
            + "(lower(type_code) like lower(?1) OR lower(type_code_value) like lower(?1))")
    public List<HillromTypeCodeFormat> findDiagnosisTypeCode(String searchString);
}
