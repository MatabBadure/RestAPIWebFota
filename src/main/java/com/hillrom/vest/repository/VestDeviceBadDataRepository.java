package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.VestDeviceBadData;

@Repository
public interface VestDeviceBadDataRepository extends JpaRepository<VestDeviceBadData, Long> {

}
