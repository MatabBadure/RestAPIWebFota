package com.hillrom.vest.service;

import java.util.Objects;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.hillrom.vest.domain.AuditRevisionInfoEntity;

public class AuditRevisionListener implements RevisionListener {

	@Override
	public void newRevision(Object revisionEntity) {
		AuditRevisionInfoEntity auditRevitionInfo = (AuditRevisionInfoEntity) revisionEntity;
		if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (principal instanceof UserDetails)
				auditRevitionInfo.setAuditor(((UserDetails) principal).getUsername());
			else
				auditRevitionInfo.setAuditor(principal.toString());
		} else
			//For reset password or activation as no suer is logged in.
			auditRevitionInfo.setAuditor("AnonymousUser");

	}
}
