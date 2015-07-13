package com.hillrom.vest.security;

import net.minidev.json.JSONObject;

import org.springframework.security.core.AuthenticationException;

public class EmailNotPresentForPatientException extends AuthenticationException {

	private JSONObject jsonObject;
	
	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public EmailNotPresentForPatientException(String msg) {
		super(msg);
	}
	
	public EmailNotPresentForPatientException(String msg,JSONObject jsonObject) {
		super(msg);
		this.jsonObject = jsonObject;
	}

}
