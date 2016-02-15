package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.CityStateZipMap;

public interface CityStateZipMapRepository extends JpaRepository<CityStateZipMap, Long> {

	@Query("Select distinct(state) from CityStateZipMap")
	List<String> findUniqueStates();
	
	List<CityStateZipMap> findByZipCode(String zipCode);

	List<CityStateZipMap> findByState(String state);
}