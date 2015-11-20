package com.hillrom.vest.security;

import java.util.List;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.UserLoginTokenRepository;

@Component
public class CredentialsChangeListener implements
		ApplicationListener<OnCredentialsChangeEvent> {

	@Inject
	private UserLoginTokenRepository authenticationTokenRepository;
	
	@Override
	public void onApplicationEvent(OnCredentialsChangeEvent event) {
		//System.out.println("Event class : "+event.getClass()+" timestamp : "+event.getTimestamp());
		Long userId = event.getUserId();
		deleteAllAuthenticationTokens(userId);
		
	}

	private void deleteAllAuthenticationTokens(Long userId) {
		List<UserLoginToken> tokens = authenticationTokenRepository.findAllByUserId(userId);
		authenticationTokenRepository.deleteInBatch(tokens);
		SecurityContextHolder.getContext().setAuthentication(null);
	}
}
