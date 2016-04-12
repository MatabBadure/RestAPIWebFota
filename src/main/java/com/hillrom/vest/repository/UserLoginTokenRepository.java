package com.hillrom.vest.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.web.rest.dto.LoginAnalyticsVO;

/**
 * Spring Data JPA repository for the UserLoginToken entity.
 */
public interface UserLoginTokenRepository extends JpaRepository<UserLoginToken,String> {

	@Query("from UserLoginToken token where token.user.id = ?1")
	List<UserLoginToken> findAllByUserId(Long userId);
	
	@Query(name="getAnalyticsForWeekOrDay")
	List<LoginAnalyticsVO> getAnalyticsForWeekOrDay(
			@Param("from")String from,
			@Param("to")String to,
			@Param("authorities")List<String> authorities);

	@Query(name="getAnalyticsForMonth")
	List<LoginAnalyticsVO> getAnalyticsForMonth(
			@Param("from")String from,
			@Param("to")String to,
			@Param("authorities")List<String> authorities);

	
	@Query(name="getAnalyticsForYear")
	List<LoginAnalyticsVO> getAnalyticsForYear(
			@Param("from")String from,
			@Param("to")String to,
			@Param("authorities")List<String> authorities);


}
