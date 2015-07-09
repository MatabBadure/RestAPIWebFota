package com.hillrom.vest.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;

import net.minidev.json.JSONObject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hillrom.vest.service.RecaptchaService;

@RestController
@RequestMapping("/api")
public class RecaptchaResource {

	private static final String EQUALTO = "=";
	private static final String SECRET_KEY = "secret";
	private static final String RESPONSE_KEY = "response";
	private static final String RECAPTCHA_API_SITEVERIFY = "https://www.google.com/recaptcha/api/siteverify";
	@Inject
	private RecaptchaService recaptchaService;
	
	@RequestMapping(value="/recaptcha",method=RequestMethod.POST)
	public ResponseEntity<JSONObject> processRecaptchaVerification(@RequestBody String response)throws URISyntaxException{
		RestTemplate restClient = new RestTemplate();
		StringBuilder uriBuilder = buildUri(response);
		return ResponseEntity.ok().body(restClient.postForObject(new URI(uriBuilder.toString()), null,JSONObject.class));
	}

	private StringBuilder buildUri(String response) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(RECAPTCHA_API_SITEVERIFY).append("?")
				.append(RESPONSE_KEY).append(EQUALTO).append(response)
				.append("&").append(SECRET_KEY).append(EQUALTO)
				.append(recaptchaService.getRecaptchaPrivateKey());
		return uriBuilder;
	}

}
