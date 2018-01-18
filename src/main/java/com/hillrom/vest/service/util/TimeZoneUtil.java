package com.hillrom.vest.service.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class TimeZoneUtil {

	public static Map<String,String> getTimezones() {
		Map<String,String> returnedNewZones = TimeZoneUtil.timezones();
		if(returnedNewZones==null){
			return null;
		}
		return returnedNewZones;
	}

	private final static Map<String,String> timezones() {
		Map<String,String> timeZones = new LinkedHashMap<String, String>();
		
		timeZones.put("Etc/GMT+12", "(UTC-12:00) International Date Line West");
		timeZones.put("Etc/GMT+11", "(UTC-11:00) Coordinated Universal Time-11");
		timeZones.put("US/Aleutian","(UTC-10:00) Aleutian Islands");
		timeZones.put("Pacific/Honolulu", "(UTC-10:00) Hawaii");
		timeZones.put("Pacific/Marquesas", "(UTC-09:30) Marquesas Islands");
		timeZones.put("America/Anchorage", "(UTC-09:00) Alaska");
		timeZones.put("Etc/GMT+9", "(UTC-09:00) Coordinated Universal Time-09");
		timeZones.put("America/Tijuana", "(UTC-08:00) Baja California");
		timeZones.put("Etc/GMT+8", "(UTC-08:00) Coordinated Universal Time-08");
		timeZones.put("America/Los_Angeles", "(UTC-08:00) Pacific Time (US & Canada)");
		timeZones.put("America/Phoenix", "(UTC-07:00) Arizona");
		timeZones.put("America/Chihuahua", "(UTC-07:00) Chihuahua, La Paz, Mazatlan");
		timeZones.put("America/Denver", "(UTC-07:00) Mountain Time (US & Canada)");
		timeZones.put("America/Guatemala", "(UTC-06:00) Central America");
		timeZones.put("America/Chicago", "(UTC-06:00) Central Time (US & Canada)");
		timeZones.put("Pacific/Easter", "(UTC-06:00) Easter Island");
		timeZones.put("America/Mexico_City", "(UTC-06:00) Guadalajara, Mexico City, Monterrey");
		timeZones.put("America/Regina", "(UTC-06:00) Saskatchewan");
		timeZones.put("America/Bogota", "(UTC-05:00) Bogota, Lima, Quito,Rio Branco");
		timeZones.put("America/Cancun", "(UTC-05:00) Chetumal");
		timeZones.put("America/New_York", "(UTC-05:00) Eastern Time (US & Canada)");
		timeZones.put("America/Port-au-Prince", "(UTC-05:00) Haiti");
		timeZones.put("America/Havana", "(UTC-05:00) Havana");
		timeZones.put("America/Indianapolis", "(UTC-05:00) Indiana (East)");
		timeZones.put("America/Asuncion", "(UTC-04:00) Asuncion");
		timeZones.put("America/Halifax", "(UTC-04:00) Atlantic Time (Canada)");
		timeZones.put("America/Caracas", "(UTC-04:30) Caracas");
		timeZones.put("America/Cuiaba", "(UTC-04:00) Cuiaba");
		timeZones.put("America/La_Paz", "(UTC-04:00) Georgetown, La Paz, Manaus, San Juan");
		timeZones.put("America/Santiago", "(UTC-04:00) Santiago");
		timeZones.put("America/Grand_Turk", "(UTC-04:00) Turks and Caicos");
		timeZones.put("America/St_Johns", "(UTC-03:30) Newfoundland");
		timeZones.put("America/Araguaina", "(UTC-03:00) Araguaina");
		timeZones.put("America/Sao_Paulo", "(UTC-03:00) Brasilia");
		timeZones.put("America/Cayenne", "(UTC-03:00) Cayenne, Fortaleza");	
		timeZones.put("America/Buenos_Aires", "(UTC-03:00) City of Buenos Aires");
		timeZones.put("America/Godthab", "(UTC-03:00) Greenland");
		timeZones.put("America/Montevideo", "(UTC-03:00) Montevideo");
		timeZones.put("America/Punta_Arenas", "(UTC-03:00) Punta Arenas");
		timeZones.put("America/Miquelon", "(UTC-03:00) Saint Pierre and Miquelon");
		timeZones.put("America/Bahia", "(UTC-03:00) Salvador");
		timeZones.put("Etc/GMT+2", "(UTC-02:00) Coordinated Universal Time-02");
		timeZones.put("Atlantic/Azores", "(UTC-01:00) Azores");
		timeZones.put("Atlantic/Cape_Verde", "(UTC-01:00) Cabo Verde Is.");
		timeZones.put("Etc/GMT", "(UTC) Coordinated Universal Time");
		timeZones.put("Africa/Casablanca", "(UTC+00:00) Casablanca");
		timeZones.put("Europe/London", "(UTC+00:00) Dublin, Edinburgh, Lisbon, London");
		timeZones.put("Atlantic/Reykjavik", "(UTC+00:00) Monrovia, Reykjavik");
		timeZones.put("Europe/Berlin", "(UTC+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
		timeZones.put("Europe/Budapest", "(UTC+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague");
		timeZones.put("Europe/Paris", "(UTC+01:00) Brussels, Copenhagen, Madrid, Paris");
		timeZones.put("Europe/Warsaw", "(UTC+01:00) Sarajevo, Skopje, Warsaw, Zagreb");
		timeZones.put("Africa/Lagos", "(UTC+01:00) West Central Africa");
		timeZones.put("Africa/Windhoek", "(UTC+02:00) Windhoek");
		timeZones.put("Asia/Amman", "(UTC+02:00) Amman");
		timeZones.put("Europe/Bucharest", "(UTC+02:00) Athens, Bucharest");
		timeZones.put("Asia/Beirut", "(UTC+02:00) Beirut");
		timeZones.put("Africa/Cairo", "(UTC+02:00) Cairo");
		timeZones.put("Europe/Chisinau", "(UTC+02:00) Chisinau");
		timeZones.put("Asia/Damascus", "(UTC+02:00) Damascus");
		timeZones.put("Asia/Hebron", "(UTC+02:00) Gaza, Hebron");
		timeZones.put("Africa/Johannesburg", "(UTC+02:00) Harare, Pretoria");
		timeZones.put("Europe/Helsinki", "(UTC+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
		timeZones.put("Asia/Jerusalem", "(UTC+02:00) Jerusalem");
		timeZones.put("Europe/Kaliningrad", "(UTC+02:00) Kaliningrad");
		timeZones.put("Africa/Khartoum", "(UTC+02:00) Khartoum");
		timeZones.put("Africa/Tripoli", "(UTC+02:00) Tripoli");
		timeZones.put("Asia/Baghdad", "(UTC+03:00) Baghdad");
		timeZones.put("Europe/Istanbul", "(UTC+03:00) Istanbul");
		timeZones.put("Asia/Riyadh", "(UTC+03:00) Kuwait, Riyadh");
		timeZones.put("Europe/Minsk", "(UTC+03:00) Minsk");
		timeZones.put("Europe/Moscow", "(UTC+03:00) Moscow, St. Petersburg, Volgograd");
		timeZones.put("Africa/Nairobi", "(UTC+03:00) Nairobi");
		timeZones.put("Asia/Tehran", "(UTC+03:30) Tehran");
		timeZones.put("Asia/Dubai", "(UTC+04:00) Abu Dhabi, Muscat");
		timeZones.put("Europe/Astrakhan", "(UTC+04:00) Astrakhan, Ulyanovsk");
		timeZones.put("Asia/Baku", "(UTC+04:00) Baku");
		timeZones.put("Europe/Samara", "(UTC+04:00) Izhevsk, Samara");
		timeZones.put("Indian/Mauritius", "(UTC+04:00) Port Louis");
		timeZones.put("Europe/Saratov", "(UTC+04:00) Saratov");
		timeZones.put("Asia/Tbilisi", "(UTC+04:00) Tbilisi");
		timeZones.put("Asia/Yerevan", "(UTC+04:00) Yerevan");
		timeZones.put("Asia/Kabul", "(UTC+04:30) Kabul");
		timeZones.put("Asia/Tashkent", "(UTC+05:00) Ashgabat, Tashkent");
		timeZones.put("Asia/Yekaterinburg", "(UTC+05:00) Ekaterinburg");
		timeZones.put("Asia/Karachi", "(UTC+05:00) Islamabad, Karachi");
		timeZones.put("Asia/Calcutta","(UTC+05:30) Chennai, Kolkata, Mumbai, New Delhi");
		timeZones.put("Asia/Colombo", "(UTC+05:30) Sri Jayawardenepura");
		timeZones.put("Asia/Katmandu", "(UTC+05:45) Kathmandu");
		timeZones.put("Asia/Almaty", "(UTC+06:00) Astana");
		timeZones.put("Asia/Dhaka", "(UTC+06:00) Dhaka");
		timeZones.put("Asia/Omsk", "(UTC+06:00) Omsk");
		timeZones.put("Asia/Rangoon","(UTC+06:30) Yangon (Rangoon)");
		timeZones.put("Asia/Bangkok", "(UTC+07:00) Bangkok, Hanoi, Jakarta");
		timeZones.put("Asia/Barnaul", "(UTC+07:00) Barnaul, Gorno-Altaysk");
		timeZones.put("Asia/Hovd", "(UTC+07:00) Hovd");
		timeZones.put("Asia/Krasnoyarsk", "(UTC+07:00) Krasnoyarsk");
		timeZones.put("Asia/Novosibirsk", "(UTC+07:00) Novosibirsk");
		timeZones.put("Asia/Tomsk", "(UTC+07:00) Tomsk");
		timeZones.put("Asia/Shanghai", "(UTC+08:00) Beijing, Chongqing, Hong Kong, Urumqi");
		timeZones.put("Asia/Irkutsk", "(UTC+08:00) Irkutsk");
		timeZones.put("Asia/Singapore", "(UTC+08:00) Kuala Lumpur, Singapore");
		timeZones.put("Australia/Perth", "(UTC+08:00) Perth");
		timeZones.put("Asia/Taipei", "(UTC+08:00) Taipei");
		timeZones.put("Asia/Ulaanbaatar", "(UTC+08:00) Ulaanbaatar");
		timeZones.put("Asia/Pyongyang", "(UTC+08:30) Pyongyang");
		timeZones.put("Australia/Eucla", "(UTC+08:45) Eucla");
		timeZones.put("Asia/Chita", "(UTC+09:00) Chita");
		timeZones.put("Asia/Tokyo", "(UTC+09:00) Osaka, Sapporo, Tokyo");
		timeZones.put("Asia/Seoul", "(UTC+09:00) Seoul");
		timeZones.put("Asia/Yakutsk", "(UTC+09:00) Yakutsk");
		timeZones.put("Australia/Adelaide", "(UTC+09:30) Adelaide");
		timeZones.put("Australia/Darwin", "(UTC+09:30) Darwin");
		timeZones.put("Australia/Brisbane", "(UTC+10:00) Brisbane");
		timeZones.put("Australia/Sydney", "(UTC+10:00) Canberra, Melbourne, Sydney");
		timeZones.put("Pacific/Port_Moresby", "(UTC+10:00) Guam, Port Moresby");
		timeZones.put("Australia/Hobart", "(UTC+10:00) Hobart");
		timeZones.put("Asia/Vladivostok", "(UTC+10:00) Vladivostok");
		timeZones.put("Australia/Lord_Howe", "(UTC+10:30) Lord Howe Island");
		timeZones.put("Pacific/Bougainville", "(UTC+11:00) Bougainville Island");
		timeZones.put("Asia/Srednekolymsk", "(UTC+11:00) Chokurdakh");
		timeZones.put("Asia/Magadan", "(UTC+11:00) Magadan");
		timeZones.put("Pacific/Norfolk", "(UTC+11:00) Norfolk Island");
		timeZones.put("Asia/Sakhalin", "(UTC+11:00) Sakhalin");
		timeZones.put("Pacific/Guadalcanal", "(UTC+11:00) Solomon Is., New Caledonia");
		timeZones.put("Asia/Anadyr", "(UTC+12:00) Anadyr, Petropavlovsk-Kamchatsky");
		timeZones.put("Pacific/Auckland", "(UTC+12:00) Auckland, Wellington");
		timeZones.put("Etc/GMT-12", "(UTC+12:00) Coordinated Universal Time+12");
		timeZones.put("Pacific/Fiji", "(UTC+12:00) Fiji");
		timeZones.put("Pacific/Chatham", "(UTC+12:45) Chatham Islands");
		timeZones.put("Etc/GMT-13", "(UTC+13:00) Coordinated Universal Time+13");
		timeZones.put("Pacific/Tongatapu", "(UTC+13:00) Nuku'alofa");
		timeZones.put("Pacific/Apia", "(UTC+13:00) Samoa");
		timeZones.put("Pacific/Kiritimati", "(UTC+14:00) Kiritimati Island");
		
		return timeZones;
	}
	
}