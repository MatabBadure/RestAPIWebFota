package com.hillrom.vest.repository;

import com.hillrom.vest.domain.UserLoginToken;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the UserLoginToken entity.
 */
public interface UserLoginTokenRepository extends JpaRepository<UserLoginToken,Long> {

}
