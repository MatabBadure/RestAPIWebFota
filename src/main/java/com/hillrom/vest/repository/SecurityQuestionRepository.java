package com.hillrom.vest.repository;

import com.hillrom.vest.domain.SecurityQuestion;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the SecurityQuestion entity.
 */
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion,Long> {

}
