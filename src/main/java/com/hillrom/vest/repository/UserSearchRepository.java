package com.hillrom.vest.repository;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.stereotype.Repository;

import com.hillrom.vest.web.rest.dto.UserExtensionDTO;

@Repository
public class UserSearchRepository {
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
	
	
	public List findHillRomUserBy(String queryString,int pageNo,int maxResults){
		EntityManager manager = entityManagerFactory.createEntityManager();
		CriteriaBuilder builder = entityManagerFactory.getCriteriaBuilder();
		
		/**
		 * select distinct(user.id),user.first_name,user.middle_name,user.last_name,user.email,authority.name from USER user join USER_EXTENSION userExt 
 join USER_AUTHORITY user_roles join AUTHORITY authority where user.id = userExt.user_id and user.id = user_roles.user_id and user_roles.authority_name = authority.name
 and (lower(user.first_name) like lower(?)
or lower(user.last_name) like lower(?) 
or lower(user.email) like lower(?))
order by user.first_name , user.last_name, user.email;


		 */
		
		StringBuilder nativeQueryBuilder = new StringBuilder();
		nativeQueryBuilder.append(" select distinct(user.id),user.first_name,user.middle_name,user.last_name,user.email,authority.name from USER user join USER_EXTENSION userExt ")
		.append(" join USER_AUTHORITY user_roles join AUTHORITY authority where user.id = userExt.user_id and user.id = user_roles.user_id and user_roles.authority_name = authority.name ")
		.append(" and lower(user.first_name) like lower(?) ")
		.append(" or lower(user.last_name) like lower(?) ")
		.append(" or lower(user.email) like lower(?)")
		.append(" order by user.first_name , user.last_name, user.email");
		
		String nativeQuery = nativeQueryBuilder.toString();
		nativeQuery = nativeQuery.replace("?",queryString);
		Query query = manager.createNativeQuery(nativeQuery);
		
		pageNo = pageNo <= 0 ? 1 : pageNo; 
		
		query.setFirstResult((pageNo-1) * maxResults);
		query.setMaxResults(maxResults);
		
		return query.getResultList();
	}

}
