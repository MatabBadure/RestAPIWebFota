package com.hillrom.vest.repository;

import com.hillrom.vest.domain.UserExtension;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the UserExtension entity.
 */
public interface UserExtensionRepository extends JpaRepository<UserExtension,Long> {

}
