package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.CityStateZipMap;

public interface CityStateZipMapRepository extends JpaRepository<CityStateZipMap, String> {

	List<CityStateZipMap> findAllByCity(String city);

	List<CityStateZipMap> findAllByZipCode(Integer zipCode);

	List<CityStateZipMap> findAllByState(String state);
}