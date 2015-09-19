package com.hillrom.vest.config;

/**
 * Application constants.
 */
public final class Constants {

	private Constants() {
	}

	// Spring profile for development, production and "fast", see
	// http://jhipster.github.io/profiles.html
	public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
	public static final String SPRING_PROFILE_PRODUCTION = "prod";
	public static final String SPRING_PROFILE_FAST = "fast";
	// Spring profile used when deploying with Spring Cloud (used when deploying
	// to CloudFoundry)
	public static final String SPRING_PROFILE_CLOUD = "cloud";
	// Spring profile used when deploying to Heroku
	public static final String SPRING_PROFILE_HEROKU = "heroku";

	public static final String SYSTEM_ACCOUNT = "system";

	public static final String DATEFORMAT_MMddyyyy = "MMddyyyy";

	public static final int NO_OF_CHARACTERS_TO_BE_EXTRACTED = 4;

	public static final int MAX_NO_OF_CAREGIVERS = 5;

	public static final String TREATMENTS_PER_DAY = "treatmentsPerDay";

	public static final String MINUTES_PER_TREATMENT = "minutesPerTreatment";

	public static final String FREQUENCIES = "frequencies";

	public static final String MIN_MINUTES_PER_DAY = "minimumMinutesOfUsePerDay";

	public static final String CUSTOM_PROTOCOL = "Custom";

	public static final String NORMAL_PROTOCOL = "Normal";

	public static final String ALL = "All";

	public static final String PROGRAMMED_CAUGH_PAUSES = "ProgrammedCaughPauses";

	public static final String DURATION_IN_MINUTES = "DurationInMinutes";

	public static final String END_TIME = "EndTime";

	public static final String START_TIME = "StartTime";

	public static final String SESSION_TYPE2 = "SessionType";

	public static final String SESSION_NO = "SessionNo";

	public static final String NORMAL_CAUGH_PAUSES = "NormalCaughPauses";

	public static final String CAUGH_PAUSE_DURATION = "CaughPauseDuration";

	public static final String NORMAL_COUGH_PAUSES = "Normal Cough Pauses";

	public static final String COUGH_PAUSE_DURATION = "Cough Pause Duration";

	public static final String PROGRAMMED_COUGH_PAUSES = "Programmed Cough Pauses";

	public static final String MINUTES = "Minutes";

	public static final String PRESSURE = "Pressure";

	public static final String FREQUENCY = "Frequency";

	public static final String FINISH = "Finish";

	public static final String START = "Start";

	public static final String SESSION_TYPE = "Session Type";

	public static final String DAILY_TREATMENT_NUMBER = "Daily Treatment Number";

	public static final String DATE = "Date";

	public static final String HMR_IN_MINUTES = "hmrInMinutes";

	public static final String DURATION = "duration";

	public static final String HUB_ID = "hubId";

	public static final String BLUETOOTH_ID = "bluetoothId";

	public static final String SERIAL_NUMBER = "serialNumber";

	public static final String EVENT_ID = "eventId";

	public static final String SEQUENCE_NUMBER = "sequenceNumber";

	public static final String HMR = "hmr";

	public static final String HUB_ADDRESS = "Hub Id";

	public static final String DEVICE_ADDRESS = "Device Address";

	public static final String SERIAL_NO = "Serial Number";

	public static final String SEQUENCE_NO = "Sequence No";

	public static final String EVENT = "Event";

	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	
	public static final String NON_HMR_COMPLIANCE = "non_hmr_compliance";

	public static final String MISSED_THERAPY = "missed_therapy";

	public static final String SETTING_DEVIATION = "setting_deviation";

	public static final String NO_EVENT = "no_event";

	public static final String GROUP_BY_YEARLY = "yearly";
	
	public static final String GROUP_BY_MONTHLY = "monthly";
	
	public static final String GROUP_BY_WEEKLY = "weekly";
}
