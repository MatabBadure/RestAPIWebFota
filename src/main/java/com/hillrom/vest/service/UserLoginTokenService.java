package com.hillrom.vest.service;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.hillrom.vest.domain.UserLoginToken;
import com.hillrom.vest.repository.UserLoginTokenRepository;
import com.hillrom.vest.security.xauth.TokenProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserLoginTokenService {
	
	private final Logger log = LoggerFactory.getLogger(UserLoginTokenService.class);
	
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
			
			DateTime tokenModifiedAt = securityToken.getLastModifiedTime();
			long expiryTimeInMillis = tokenModifiedAt.plus(1000 * tokenProvider.getTokenValidity()).getMillis();
			DateTime now = DateTime.now();
			
			boolean flag = false;
			if(expiryTimeInMillis - System.currentTimeMillis() >= 0 && !securityToken.isExpired() ){
				log.debug("Created time for Token : " + securityToken.getCreatedTime());
				log.debug("Last updated time for Token : " + tokenModifiedAt);
				log.debug("Current application  time  : " + DateTime.now());
				log.debug("Difference in seconds      : " + (expiryTimeInMillis - System.currentTimeMillis())/1000);
				flag = true;
				securityToken.setLastModifiedTime(now);
				log.debug("flag : " + flag);
				tokenRepository.save(securityToken);
				return flag;				
			}else{
				flag = false;
				log.debug("Created time for Token : " + securityToken.getCreatedTime());
				log.debug("Last updated time for Token : " + tokenModifiedAt);
				log.debug("Current application  time  : " + DateTime.now());
				log.debug("Difference in seconds      : " + (expiryTimeInMillis - System.currentTimeMillis())/1000);
				return flag;				
			}
		}
	} 
}
