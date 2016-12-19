package com.hillrom.vest.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ADMIN";

    public static final String PATIENT = "PATIENT";
    
    public static final String HCP = "HCP";
    
    public static final String ACCT_SERVICES = "ACCT_SERVICES";
    
    public static final String ASSOCIATES = "ASSOCIATES";
    
    public static final String HILLROM_ADMIN = "HILLROM_ADMIN";
    
    public static final String CLINIC_ADMIN = "CLINIC_ADMIN";
    
    public static final String CARE_GIVER = "CARE_GIVER";

    public static final String ANONYMOUS = "ANONYMOUS";
    
    //hill-1845
    public static final String CUSTOMER_SERVICES = "CUSTOMER_SERVICES";
    //hill-1845 
}
