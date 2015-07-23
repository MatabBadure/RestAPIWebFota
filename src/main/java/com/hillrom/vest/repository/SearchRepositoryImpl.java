package com.hillrom.vest.repository;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.stereotype.Component;

@Component
public class SearchRepositoryImpl<T> implements SearchRepository<T> {

	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	@Override
	public List<T> findBy(SearchCriteria<T> criteria) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createNamedQuery("findBy");
		query.setParameter("queryString", criteria.getSearchString());
		
		System.out.println(" query : "+query);
		return query.getResultList();
	}

}
