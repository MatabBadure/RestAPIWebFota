package com.hillrom.vest.security;

import net.minidev.json.JSONObject;

import org.springframework.security.core.AuthenticationException;

public class FirstLoginException extends AuthenticationException {

	private static final long serialVersionUID = 1L;
	
	private JSONObject jsonObject;
	
	public JSONObject getJsonObject() {
		return jsonObject;
	}
	
	public FirstLoginException(String msg) {
		super(msg);
	}

	public FirstLoginException(String msg,JSONObject jsonObject) {
		super(msg);
		this.jsonObject = jsonObject;
	}
}
