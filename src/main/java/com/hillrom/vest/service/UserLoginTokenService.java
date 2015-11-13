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
			
			DateTime tokenCreatedAt = securityToken.getCreatedTime();
			long expiryTimeInMillis = securityToken.getCreatedTime().plus(1000 * tokenProvider.getTokenValidity()).getMillis();
			
			boolean flag = false;
			if(expiryTimeInMillis - System.currentTimeMillis() < 0 ){
				log.debug("Last update time for Token : " + securityToken.getCreatedTime());
				log.debug("Current application  time  : " + DateTime.now());
				log.debug("Difference in seconds      : " + (expiryTimeInMillis - System.currentTimeMillis())/1000);
				flag = true;
				securityToken.setCreatedTime(DateTime.now());
				log.debug("flag : " + flag);
				tokenRepository.save(securityToken);
				return flag;				
			}else{
				flag = false;
				log.debug("Last update time for Token : " + securityToken.getCreatedTime());
				log.debug("Current application  time  : " + DateTime.now());
				log.debug("Difference in seconds      : " + (expiryTimeInMillis - System.currentTimeMillis())/1000);
				log.debug("flag : " + flag);
				return flag;				
			}
		}
	} 
}
