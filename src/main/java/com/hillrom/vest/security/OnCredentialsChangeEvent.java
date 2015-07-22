package com.hillrom.vest.security;

import org.springframework.context.ApplicationEvent;

public class OnCredentialsChangeEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private Long userId;
	
	public OnCredentialsChangeEvent(Long userId) {
		super(userId);
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}
	
}
