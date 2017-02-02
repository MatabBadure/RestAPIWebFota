package com.hillrom.vest.repository.monarch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hillrom.vest.domain.VestDeviceBadData;
import com.hillrom.vest.domain.VestDeviceBadDataMonarch;

@Repository
public interface VestDeviceBadDataRepositoryMonarch extends JpaRepository<VestDeviceBadDataMonarch, Long> {

}
