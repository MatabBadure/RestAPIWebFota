package com.hillrom.vest.repository;

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
		Query query = entityManager.createNamedQuery("findHillRomTeamUserBy");
		query.setFirstResult(pageable.getPageNumber());
		query.setMaxResults(pageable.getOffset());
		query.setParameter("queryString", queryString);
		List<HillRomUserVO> hillromUsers =  query.getResultList();
		Page<HillRomUserVO> page = new PageImpl<HillRomUserVO>(hillromUsers);
		return page;
	}

}
