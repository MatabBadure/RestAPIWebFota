package com.hillrom.vest.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class Test {

	private static final String API_RECEIVE_DATA = "http://localhost:8080/api/receiveData";

	public static List<String> readCSV(){
		String filename = "patient2_data.txt";
		String workingDirectory = System.getProperty("user.dir");
		
		String absoluteFilePath = "";
			
		//absoluteFilePath = workingDirectory + System.getProperty("file.separator") + filename;
		absoluteFilePath = workingDirectory + File.separator + filename;

		System.out.println("Final filepath : " + absoluteFilePath);
		BufferedReader br = null;
		String line = "";
		List<String> requestList = new LinkedList<>();
		try {
			br = new BufferedReader(new FileReader(absoluteFilePath));
			while ((line = br.readLine()) != null) {
				requestList.add(line);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
		for(String request : requestList){
			System.out.println(request);
		}
		System.out.println("######################################################################################");
		return requestList;
	}
	public static void main(String[] args) {
		// register form message converter
		List<String> requestList = readCSV();
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add( new FormHttpMessageConverter() );
		/*Scanner sc = new Scanner(System.in);
		System.out.println("Enter raw Message : ");
		String message = sc.next();*/
		for(String message: requestList){
			System.out.println("**************************Request Params************************************************");
			System.out.println("Message : "+message);
			List<NameValuePair> params = URLEncodedUtils.parse(message, Charset.defaultCharset());
			for(NameValuePair nvp : params){
				System.out.println(nvp.getName()+ " : "+nvp.getValue());
			}
			System.out.println("*****************************************************************************************************************************");
			
			MultiValueMap<String,String> headers=new LinkedMultiValueMap<String,String>();
			headers.set("Accept","application/json");
			headers.set("Content-Type","application/x-www-form-urlencoded");
			
			System.out.println("**************************Form params being passed************************************************");
			final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
			for(NameValuePair nvp : params){
				formData.add( nvp.getName(), nvp.getValue() );
				System.out.println(nvp.getName()+ " : "+nvp.getValue());
			}
			System.out.println("*****************************************************************************************************************************");
			
			HttpHeaders requestHeaders=new HttpHeaders();
	        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);		
	        
			HttpEntity<MultiValueMap<String,String>> requestEntity= new HttpEntity<MultiValueMap<String,String>>(formData,requestHeaders);
			ResponseEntity<Object> response= restTemplate.postForEntity(API_RECEIVE_DATA, requestEntity, Object.class);
			System.out.println("response statusCode : "+response.getStatusCode());
			System.out.println("response body : "+response.getBody());

		}
		
	}

}
