package com.hillrom.vest.audit.service;

import java.io.Serializable;
import java.util.List;

public interface IAuditService<T extends Serializable>{
	
	List<T> getEntitiesAtRevision(Number revision,Class<T> clazz);

    List<T> getEntitiesModifiedAtRevision(Number revision,Class<T> clazz);

    List<T> getRevisions(Class<T> clazz);

}
