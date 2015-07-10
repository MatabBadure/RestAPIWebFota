package com.hillrom.vest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RecaptchaService {
	
	
	private String recaptchaPrivateKey;
	
	public RecaptchaService(){
		
	}

	@Autowired
	@Qualifier("recaptchaPrivateKey")
	public void setRecaptchaPrivateKey(String recaptchaPrivateKey) {
		this.recaptchaPrivateKey = recaptchaPrivateKey;
	}

	public String getRecaptchaPrivateKey() {
		return recaptchaPrivateKey;
	}

	
}
