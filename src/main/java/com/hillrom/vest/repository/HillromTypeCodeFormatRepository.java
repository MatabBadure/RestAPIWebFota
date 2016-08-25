package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.HillromTypeCodeFormat;

public interface HillromTypeCodeFormatRepository extends JpaRepository<HillromTypeCodeFormat, Long> {

	@Query("from HillromTypeCodeFormat where type = ?1")
	public List<String> findTypeCodeListVal(String type);
}
