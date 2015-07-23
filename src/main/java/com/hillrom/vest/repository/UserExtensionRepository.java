package com.hillrom.vest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.UserExtension;

/**
 * Spring Data JPA repository for the UserExtension entity.
 */
public interface UserExtensionRepository extends JpaRepository<UserExtension,Long> {

	/*select user.*, authority.name from `hillromvest-dev`.USER user join `hillromvest-dev`.USER_EXTENSION userExt
join `hillromvest-dev`.USER_AUTHORITY user_roles join `hillromvest-dev`.AUTHORITY authority where user.id = userExt.user_id and user.id = user_roles.user_id
and lower(user.first_name) like lower('%admin%')
or lower(user.last_name) like lower('%admin%')
or lower(user.email) like lower('%admin%');
	 * */
}
