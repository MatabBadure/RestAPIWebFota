package com.hillrom.vest.audit.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hillrom.vest.audit.repository.AuditableRepository;

@Service("auditableService")
public class AuditableService<T extends Serializable> implements IAuditService<T> {

	@Inject
	@Qualifier("auditableRepository")
	private AuditableRepository<T> auditableRepository;
	
	protected void setClazz(Class<T> clazz){
		auditableRepository.setClass(clazz);
	}
	
	@Override
	public List<T> getEntitiesAtRevision(Number revision,Class<T> clazz) {
		setClazz(clazz);
		return auditableRepository.getEntitiesAtRevision(revision);
	}

	@Override
	public List<T> getEntitiesModifiedAtRevision(Number revision,Class<T> clazz) {
		setClazz(clazz);
		return auditableRepository.getEntitiesModifiedAtRevision(revision);
	}

	@Override
	public List<T> getRevisions(Class<T> clazz) {
		setClazz(clazz);
		return auditableRepository.getRevisions();
	}

}
