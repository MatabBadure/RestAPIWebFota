package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.ProtocolConstants;

public interface ProtocolConstantsRepository extends
		JpaRepository<ProtocolConstants, Long> {

}
