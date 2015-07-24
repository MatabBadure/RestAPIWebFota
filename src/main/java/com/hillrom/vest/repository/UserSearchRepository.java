package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserSearchRepository {
	
	@Inject
	private EntityManager entityManager;
	
	public Page<HillRomUserVO> findHillRomTeamUsersBy(String queryString,Pageable pageable){
	
		int firstResult = pageable.getPageNumber()*pageable.getOffset();
		int maxResult = firstResult+pageable.getPageSize();
		
		String countSqlQuery = "select count(hillromUsers.id) from (select user.id,user.first_name,user.last_name,user.email,authority.name from  USER_EXTENSION userExt join USER user "
				+ " join  USER_AUTHORITY user_authority join  AUTHORITY authority "
				+ " where user.id = userExt.user_id and user_authority.user_id = user.id "
				+ " and user_authority.authority_name = authority.name "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(user.email) like lower(:queryString)) order by user.first_name,user.last_name,user.email ) hillromUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		countQuery.setParameter("queryString", queryString);
		BigInteger count =  (BigInteger) countQuery.getSingleResult();
		
		Query query = entityManager.createNamedQuery("findHillRomTeamUserBy");
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		
		query.setParameter("queryString", queryString);
		
		List<HillRomUserVO> hillromUsers =  query.getResultList();
	
		Page<HillRomUserVO> page = new PageImpl<HillRomUserVO>(hillromUsers,null,count.intValue());
	
		return page;
	}

}
