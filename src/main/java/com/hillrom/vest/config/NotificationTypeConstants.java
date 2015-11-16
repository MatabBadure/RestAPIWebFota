package com.hillrom.vest.config;

public class NotificationTypeConstants {

	public static String SETTINGS_DEVIATION = "SETTINGS_DEVIATION";
	public static String HMR_NON_COMPLIANCE = "HMR_NON_COMPLIANCE";
	public static String HMR_AND_SETTINGS_DEVIATION = HMR_NON_COMPLIANCE+" AND "+SETTINGS_DEVIATION;
	public static String MISSED_THERAPY = "MISSED_THERAPY";
	
	public static String SETTINGS_DEVIATION_DISPLAY_VALUE = "Setting Deviation";
	public static String HMR_NON_COMPLIANCE_DISPLAY_VALUE = "HMR Non-Adherence";
	public static String HMR_AND_SETTINGS_DEVIATION_DISPLAY_VALUE = HMR_NON_COMPLIANCE+" and "+SETTINGS_DEVIATION;
	public static String MISSED_THERAPY_DISPLAY_VALUE = "Missed Therapy Days";
}
