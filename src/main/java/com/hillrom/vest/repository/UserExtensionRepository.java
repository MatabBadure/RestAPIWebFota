package com.hillrom.vest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hillrom.vest.domain.UserExtension;

/**
 * Spring Data JPA repository for the UserExtension entity.
 */
public interface UserExtensionRepository extends JpaRepository<UserExtension,Long> {

}
