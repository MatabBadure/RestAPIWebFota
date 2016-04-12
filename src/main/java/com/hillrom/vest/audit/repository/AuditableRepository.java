package com.hillrom.vest.audit.repository;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Component;

@Component("auditableRepository")
public class AuditableRepository <T extends Serializable> implements IAuditOperations<T>{
	
		@Inject
		private EntityManager entityManager;

		private Class clazz;
		
		public void setClass(Class<T> clazz){
			this.clazz = clazz;
		}
		
		public AuditReader getAuditReader(){
			return AuditReaderFactory.get(entityManager);
		}
	
	 	@Override
	    public List<T> getEntitiesAtRevision(final Number revision) {
	        final AuditReader auditReader = getAuditReader();
	        final AuditQuery query = auditReader.createQuery().forEntitiesAtRevision(clazz, revision);
	        List<Object[]> results= query.getResultList();
	        final List<T> resultList = convertToEntity(results);
	        return resultList;
	    }

		public List<T> convertToEntity(List<Object[]> results) {
			final List<T> resultList = new LinkedList<>();
	        for(Object row : results) {
	            if(row instanceof Object[]) {
	              Object[] array = (Object[])row;
	              T entity = (T) array[0];
	              resultList.add(entity);
	            }
	          }
			return resultList;
		}

	    @Override
	    public List<T> getEntitiesModifiedAtRevision(final Number revision) {
	        final AuditReader auditReader = getAuditReader();
	        final AuditQuery query = auditReader.createQuery().forEntitiesModifiedAtRevision(clazz, revision);
	        List<Object[]> results= query.getResultList();
	        final List<T> resultList = convertToEntity(results);
	        return resultList;
	    }

	    @Override
	    public List<T> getRevisions() {
	        final AuditReader auditReader = getAuditReader();
	        final AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, true, true);
	        List<Object[]> results= query.getResultList();
	        final List<T> resultList = convertToEntity(results);
	        return resultList;
	    }

}
