package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.BadVestDeviceData;

@Repository
public interface BadVestDeviceDataRepository extends JpaRepository<BadVestDeviceData, Long> {

}
