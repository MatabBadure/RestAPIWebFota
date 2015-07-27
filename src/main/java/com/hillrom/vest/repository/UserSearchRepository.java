package com.hillrom.vest.repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class UserSearchRepository {
	
	private static final String ORDER_BY_CLAUSE_START = " order by ";
	@Inject
	private EntityManager entityManager;
	
	public Page<HillRomUserVO> findHillRomTeamUsersBy(String queryString,Pageable pageable,Map<String,Boolean> sortOrder){
	
		int firstResult = pageable.getPageNumber()*pageable.getOffset();
		int maxResult = firstResult+pageable.getPageSize();
		
		String countSqlQuery = "select count(hillromUsers.id) from (select distinct(user.id),user.first_name,user.last_name,user.email,user_authority.authority_name as name,user.is_deleted as isDeleted from  USER_EXTENSION userExt join USER user "
				+ " join  USER_AUTHORITY user_authority "
				+ " where user.id = userExt.user_id and user_authority.user_id = user.id "
				+ " and user_authority.authority_name in ('ADMIN','ACCT_SERVICES','ASSOCIATES','HILLROM_ADMIN','CLINIC_ADMIN') "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(user.email) like lower(:queryString)) order by user.first_name,user.last_name,user.email ) hillromUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		countQuery.setParameter("queryString", queryString);
		BigInteger count =  (BigInteger) countQuery.getSingleResult();
		
		Query query = getNamedQueryOrderedBy("findHillRomTeamUserBy", sortOrder);
	
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		
		query.setParameter("queryString", queryString);
		
		List<HillRomUserVO> hillromUsers =  query.getResultList();
	
		Page<HillRomUserVO> page = new PageImpl<HillRomUserVO>(hillromUsers,null,count.intValue());
	
		return page;
	}
	
	public Page<HcpVO> findHCPBy(String queryString,Pageable pageable,Map<String,Boolean> sortOrder){
		
		int firstResult = pageable.getPageNumber()*pageable.getOffset();
		int maxResult = firstResult+pageable.getPageSize();
		
		String countSqlQuery = "select count(hcpUsers.id) from (select distinct(user.id),user.email,user.first_name,user.last_name,user.is_deleted as isDeleted,user.zipcode,"
				+ " userExt.address,userExt.city,userExt.credentials,userExt.fax_number,userExt.primary_phone,userExt.mobile_phone,userExt.speciality,userExt.state,clinic.id as clinicId,clinic.name as clinicName "
				+ " FROM USER user join USER_EXTENSION userExt "
				+ " join USER_AUTHORITY user_authority join CLINIC clinic "
				+ " join CLINIC_USER_ASSOC clinic_user "
				+ " where user.id = userExt.user_id and user_authority.user_id = user.id "
				+ " and user_authority.authority_name = 'HCP'  and clinic_user.users_id = user.id "
				+ " and clinic_user.clinics_id = clinic.id "
				+ " and (lower(user.first_name) like lower(:queryString) or "
				+ " lower(user.last_name) like lower(:queryString) or "
				+ " lower(user.email) like lower(:queryString)) group by user.id order by user.first_name,user.last_name,user.email) hcpUsers";
		
		Query countQuery = entityManager.createNativeQuery(countSqlQuery);
		countQuery.setParameter("queryString", queryString);
		BigInteger count =  (BigInteger) countQuery.getSingleResult();
		
		Query query = getNamedQueryOrderedBy("findHcpBy", sortOrder);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		
		query.setParameter("queryString", queryString);
		
		Map<Long,HcpVO> hcpUsersMap = new HashMap<>();
		List<Object[]> results = query.getResultList(); 
		results.stream().forEach((record) -> {
	        Long id = ((BigInteger) record[0]).longValue();
	        String email = (String) record[1];
	        String firstName = (String) record[2];
	        String lastName = (String) record[3];
	        Boolean isDeleted = (Boolean) record[4];
	        Integer zipcode = (Integer) record[5];
	        String address = (String) record[6];
	        String city = (String) record[7];
	        String credentials = (String)record[8];
	        Long faxNumber = (Long) record[9];
	        Long primaryPhone = (Long) record[10];
	        Long mobilePhone = (Long) record[11];
	        String speciality = (String) record[12];
	        String state = (String) record[13];
	        BigInteger clinicId = (BigInteger) record[14];
	        String clinicName = (String) record[15];
	        
	        HcpVO hcpVO = hcpUsersMap.get(id);
	        
	        Map<String,String> clinicMap = new HashMap<>();
	        clinicMap.put("id", clinicId.toString());
	        clinicMap.put("name", clinicName);
	        if(hcpVO == null){
	        	hcpVO = new HcpVO(id, firstName, lastName, email, 
		        		isDeleted, zipcode, address,city, credentials, faxNumber,
		        		primaryPhone, mobilePhone, speciality, state);
	        	
	        	hcpVO.getClinics().add(clinicMap);
	        	hcpUsersMap.put(id, hcpVO);
	        }else{
	        	hcpVO.getClinics().add(clinicMap);
	        }
		});
		
		List<HcpVO> hcpUsers =  new LinkedList<>(hcpUsersMap.values());
	
		Page<HcpVO> page = new PageImpl<HcpVO>(hcpUsers,null,count.intValue());
	
		return page;
	}
	
	private String getNamedQueryString(String queryName) {
	    Query tmpQuery = entityManager.createNamedQuery(queryName);
	    SQLQuery sqlQuery = tmpQuery.unwrap(SQLQuery.class);
	    String queryString = sqlQuery.getQueryString();
	    return queryString;
	}


	private Query getNamedQueryOrderedBy(String queryName, Map<String, Boolean> columnNames){

	    StringBuilder sb = new StringBuilder();
	    // Append order by only if there is any sort request.
	    if(columnNames.keySet().size() > 0){
	    	sb.append(ORDER_BY_CLAUSE_START);	    	
	    }

	    int limit = columnNames.size();
	    int i = 0;
	    for (String columnName: columnNames.keySet()) {
	        sb.append(columnName);

	        if (columnNames.get(columnName))
	            sb.append(" ASC");
	        else
	            sb.append(" DESC");

	        if (i != (limit - 1)) {
	            sb.append(", ");
	        }
	    }
	    Query jpaQuery = entityManager.createNativeQuery( getNamedQueryString(queryName)
	                + sb.toString() 
	                );
	    System.out.println("jpaQuery : "+jpaQuery);
	    return jpaQuery;
	}

}
