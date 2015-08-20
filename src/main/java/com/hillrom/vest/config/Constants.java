package com.hillrom.vest.config;

/**
 * Application constants.
 */
public final class Constants {

    private Constants() {
    }

    // Spring profile for development, production and "fast", see http://jhipster.github.io/profiles.html
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";
    public static final String SPRING_PROFILE_FAST = "fast";
    // Spring profile used when deploying with Spring Cloud (used when deploying to CloudFoundry)
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

}
