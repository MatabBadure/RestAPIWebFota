package com.hillrom.vest.domain;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import com.hillrom.vest.service.AuditRevisionListener;

@Entity
@Table(name = "AUDIT_REVISION_INFO")
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevisionInfoEntity extends DefaultRevisionEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7387549815500904487L;
	
	@Column(name = "user_id")
	private String userId;

	public String getAuditor() {
		return userId;
	}

	public void setAuditor(String userId) {
		this.userId = userId;
	}
}