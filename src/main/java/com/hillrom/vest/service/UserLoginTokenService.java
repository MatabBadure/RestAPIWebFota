package com.hillrom.vest.service;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.UserLoginTokenRepository;
import com.hillrom.vest.security.xauth.TokenProvider;

@Service
@Transactional
public class UserLoginTokenService {
	
	@Inject
	private UserLoginTokenRepository tokenRepository;
	
	@Inject
	private TokenProvider tokenProvider;
	
	public UserLoginToken findOneById(String id){
		return tokenRepository.findOne(id);
	}
	
	public void deleteToken(String id){
		tokenRepository.delete(id);
	}
	
	public boolean validateToken(String authToken){
		UserLoginToken securityToken = findOneById(authToken);
		if(null == securityToken){
			return false;
		}else{
			DateTime tokenCreatedAt = securityToken.getCreatedTime();
			System.out.println("tokenCreatedAt : "+tokenCreatedAt);
			long expiryTimeInMillis = securityToken.getCreatedTime().plus(1000 * tokenProvider.getTokenValidity()).getMillis();
			boolean flag = System.currentTimeMillis() <= expiryTimeInMillis ;
			securityToken.setCreatedTime(DateTime.now());
			tokenRepository.save(securityToken);
			return flag;
		}
	} 
	
	
}
