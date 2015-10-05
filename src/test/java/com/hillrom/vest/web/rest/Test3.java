package com.hillrom.vest.web.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

@Service
public class Test3 {

	private static final String USER_DIR = "user.dir";
	private static final String SORTED_REQUESTS_CSV = "sorted_requests.xls.csv";
	private static final String RECEIVE_DATA_XLS_CSV = "receiveData.xls.csv";
	private static final String API_RECEIVE_DATA = "http://localhost:8080/api/receiveData";
	private static final String YYYY_MMM_DD_HH_MM_SS = "yyyy-MMM-dd hh:mm:ss";

	public static List<String> readCSV(String filename) {
		String absoluteFilePath = prepareAbsoluteFilePath(filename);

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
		return requestList;
	}

	public static DateTime getTimeStamp(String timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MMM_DD_HH_MM_SS);
		try {
			return new DateTime(dateFormat.parse(timestamp).getTime());
		} catch (ParseException | RuntimeException e) {
			throw new IllegalArgumentException(
					"Could not parse data, Bad Content");
		}
	}

	public static void main(String[] args) {
		//writeSortedRequestsToCSV("receiveData.xls.csv");
		makeApiCallsByReadingCSV("messages1.txt");
		/*System.out.println("Enter message : ");
		Scanner sc = new Scanner(System.in);
		String msg = sc.next();
		sendPostRequest(msg);*/	
		}

	public static void makeApiCallsByReadingCSV(String filename) {
		String absoluteFilePath = prepareAbsoluteFilePath(filename);
		BufferedReader br = null;
		//String line = "";
		try {
			Scanner in = new Scanner(new File(absoluteFilePath));
			while(in.hasNext()){
				String message = in.next();
				System.out.println("message : "+message);
				sendPostRequest(message.replace(",", ""));
				System.out.println("Posted, Going to sleep  @ : "+System.currentTimeMillis());
				Thread.sleep(6000);
				System.out.println("Posted, Wokeup  @ : "+System.currentTimeMillis());
			}
		} catch (IOException | InterruptedException e) {
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

		System.out
				.println("######################################################################################");

	}

	public static String prepareAbsoluteFilePath(String filename) {
		String workingDirectory = System.getProperty(USER_DIR);
		String absoluteFilePath = "";
		absoluteFilePath = workingDirectory + File.separator + filename;
		return absoluteFilePath;
	}

	public static Collection<String> getSortedRequestList(String filename) {
		List<String> requestList = readCSV(filename);

		Map<DateTime, String> messagesMap = new TreeMap<>();
		System.out.println("Size : " + requestList.size());
		for (String message : requestList) {
			List<NameValuePair> params = URLEncodedUtils.parse(message,
					Charset.defaultCharset());
			for (NameValuePair nvp : params) {
				if ("hub_receive_time".equalsIgnoreCase(nvp.getName())) {
					messagesMap.put(getTimeStamp(nvp.getValue()), message);
					System.out.println("timestamp value : "
							+ getTimeStamp(nvp.getValue()));
				}
			}
		}

		Collection<String> sortedRequestList = messagesMap.values();
		return sortedRequestList;
	}

	public static void writeSortedRequestsToCSV(String filename) {
		try {
			Collection<String> sortedMessages = getSortedRequestList(filename);
			String absoluteFilePath = prepareAbsoluteFilePath("sorted_requests.txt");
			FileWriter writer = new FileWriter(absoluteFilePath);
			for(String message : sortedMessages){
				writer.write(message);
				writer.write("\n");
			}
			writer.close();
			System.out.println("Exported Sorted requests into "+SORTED_REQUESTS_CSV);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static RestTemplate getTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new LinkedList<>();
		converters.add(new FormHttpMessageConverter());
		converters.add(new StringHttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		return restTemplate;
	}

	public static void sendPostRequest(final String message) {
		final RestTemplate restTemplate = getTemplate();
		HttpHeaders requestHeaders = getHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(
				message.toString(), requestHeaders);
		restTemplate.postForEntity(API_RECEIVE_DATA, requestEntity, null);
		System.out
				.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Processed message @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	}

	public static HttpHeaders getHeaders() {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.TEXT_PLAIN);
		requestHeaders.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		return requestHeaders;
	}

}
