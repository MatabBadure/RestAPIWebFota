package com.hillrom.vest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hillrom.vest.domain.UserExtension;

/**
 * Spring Data JPA repository for the UserExtension entity.
 */
public interface UserExtensionRepository extends JpaRepository<UserExtension,Long> {

	@Query("from UserExtension user where user.deleted = false")
    List<UserExtension> findByActiveStatus();
}
