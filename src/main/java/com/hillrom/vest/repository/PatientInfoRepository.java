package com.hillrom.vest.repository;

import com.hillrom.vest.domain.PatientInfo;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the PatientInfo entity.
 */
public interface PatientInfoRepository extends JpaRepository<PatientInfo,Long> {

    @Query("select patientInfo from PatientInfo patientInfo where patientInfo.user.login = ?#{principal.username}")
    List<PatientInfo> findAllForCurrentUser();

}
