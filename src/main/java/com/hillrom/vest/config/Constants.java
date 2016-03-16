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

	public static final String MINUTES = "Duration";

	public static final String PRESSURE = "Pressure";

	public static final String FREQUENCY = "Frequency";

	public static final String FINISH = "Finish";

	public static final String START = "Start";

	public static final String SESSION_TYPE = "Session Type";

	public static final String DAILY_TREATMENT_NUMBER = "Daily Treatment Number";

	public static final String DATE = "Date";

	public static final String HMR_IN_HOURS = "hmrInHours";

	public static final String DURATION = "Duration";

	public static final String HUB_ID = "hubId";

	public static final String BLUETOOTH_ID = "bluetoothId";

	public static final String SERIAL_NUMBER = "serialNumber";

	public static final String EVENT_ID = "eventId";

	public static final String SEQUENCE_NUMBER = "sequenceNumber";

	public static final String HMR = "Hmr";

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

	public static final String YEAR = "year";
	
	public static final String MONTH = "month";
	
	public static final String WEEK = "week";
	
	public static final String ACTIVE = "active";
	
	public static final String INACTIVE = "inactive";
	
	public static final String EXPIRED = "expired";
	
	public static final String TIME = "Time";
	
	public static final String PATIENT_ID = "Patient Id";
	
	public static final String PATIENT_BLUETOOTH_ADDRESS = "patientBlueToothAddress";
	
	public static final String DAY = "day";
	
	public static final String CUSTOM = "custom";
	
	public static final String MMddyyyy = "MM/dd/yyyy";
	
	public static final String WEEK_SEPERATOR = " - ";
	
	public static final String XAXIS_TYPE_CATEGORIES = "categories";
	
	public static final String XAXIS_TYPE_DATETIME = "datetime";
	
	public static final String LA_DAYVIEW_LABEL = "No.of Logins";
	
	public static final String UNABLE_TO_ASSESS = "Unable to assess";
	public static final String STRONGLY_AGREE = "Strongly agree";
	public static final String SOMEWHAT_AGREE= "Somewhat agree";
	public static final String NEUTRAL = "Neutral";
	public static final String SOMEWHAT_DISAGREE  = "Somewhat disagree";
	public static final String STRONGLY_DISAGREE = "Strongly disagree";
	
	// 5days Survey question ids
	public static final String FIVE_DAYS_SURVEY_QIDS = "6,7,8,9,10,11,12";
	
	// 30 days Survey question ids
	public static final String THIRTY_DAYS_SURVEY_QIDS = "27,28,29,30,31,32,33";
	
	public static final Long FIVE_DAYS_SURVEY_ID = 1L;
	
	public static final Long THIRTY_DAYS_SURVEY_ID = 2L;
	
	// Survey Graph Constants
	public static final String KEY_COUNT = "count";
	public static final String Q_PREFIX = "Q-";
	public static final String NO = "No";
	public static final String YES = "Yes";
	public static final String KEY_THIRTY_DAYS_SURVEY_REPORT = "thirtyDaysSurveyReport";
	public static final String KEY_FIVE_DAYS_SURVEY_REPORT = "fiveDaysSurveyReport";

	//Column name to be checked in Patient search
	public static final String ADHERENCE = "adherence";
	
	// HMR Graph Date Formats
	public static final String MMddyyyyHHMM = "MM/dd/yyyy (HH:MM a)";
	public static final String HHMM = "HH:MM a";
	public static final String MMddyyyyHHmmss = "MM/dd/yyyy HH:mm:ss";
	
	//HMR Graph Y-axis labels
	public static final String MINUTES_LABEL = "Minutes";
	public static final String HMR_LABEL = "HMR";
	
	//HMR Graph tooltip labels
	public static final String KEY_NOTE_TEXT = "noteText";
	public static final String KEY_COUGH_PAUSES = "coughPauses";
	public static final String KEY_MISSED_THERAPY = "missedTherapy";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_PRESSURE = "pressure";
	public static final String KEY_SESSION_NO = "sessionNo";

	// Compliance Graph 
	public static final String KEY_MAX = "max";
	public static final String KEY_MIN = "min";
	public static final String KEY_THERAPY_DATA = "therapyData";
	public static final String KEY_PROTOCOL = "protocol";
	public static final String PRESSURE_LABEL = "Pressure";
	public static final String FREQUENCY_LABEL = "Frequency";
	public static final String DURATION_LABEL = "Duration";

	// Cumulative Stats Graph 
	public static final String HMR_NON_ADHERENCE_LABEL = "HMR Non-Adherence";
	public static final String SETTING_DEVIATION_LABEL = "Setting Deviation";
	public static final String MISSED_THERAPY_DAYS_LABEL = "Missed Therapy Days";
	public static final String NO_TRANSMISSION_RECORDED_LABEL = "No Transmission Recorded";

	// Treatment Stats Graph
	public static final String AVERAGE_LENGTH_OF_TREATMENT_LABEL = "Average Length of Treatment";
	public static final String AVERAGE_TREATMENTS_PER_DAY_LABEL = "Average Treatments Per Day";

	// Bench Mark Parameter
	public static final String BM_PARAM_SETTING_DEVIATION = "settingDeviation";
	public static final String BM_PARAM_HMR_DEVIATION = "HMRDeviation";
	public static final String BM_PARAM_MISSED_THERAPY_DAYS = "missedTherapy";
	public static final String BM_PARAM_ADHERENCE_SCORE = "adherenceScore";

	public static final String KEY_TOTAL_PATIENTS = "totalPatients";

	// Constants used for calculating time-period between days
	public static final String DAY_STRING = "Day";
	public static final String MONTH_STRING = "Month";
	public static final String YEAR_STRING = "Year";
	
	public static final String BM_TYPE_AVERAGE = "Average";
	public static final String BM_TYPE_MEDIAN = "Median";
	public static final String BM_TYPE_PERCENTILE = "Percentile";
	
	public static final String AGE_GROUP = "AgeGroup";
	public static final String CLINIC_SIZE = "clinicSize";

	public static final String KEY_RANGE_LABELS = "rangeLabels";
	public static final String KEY_BENCH_MARK_DATA = "benchMarkData";
	
	// BenchMark Axis Labels
	public static final String BM_PARAM_SETTING_DEVIATION_LABEL = "Setting Deviation";
	public static final String BM_PARAM_HMR_DEVIATION_LABEL = "HMR Deviation";
	public static final String BM_PARAM_MISSED_THERAPY_DAYS_LABEL = "Missed Therapy";
	public static final String BM_PARAM_ADHERENCE_SCORE_LABEL = "Adherence Score";

	public static final String AGE_GROUP_LABEL = "Age Group";
	public static final String CLINIC_SIZE_LABEL = "Clinic Size";
}
