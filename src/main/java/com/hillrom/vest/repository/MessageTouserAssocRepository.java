package com.hillrom.vest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.ClinicPatientAssoc;
import com.hillrom.vest.domain.ClinicPatientAssocPK;
import com.hillrom.vest.domain.MessageTouserAssoc;

public interface MessageTouserAssocRepository extends
		JpaRepository<MessageTouserAssoc, Long> {


}
