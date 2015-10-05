package com.hillrom.vest.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    public static final String PATIENT = "PATIENT";
    
    public static final String HCP = "HCP";
    
    public static final String RC_ADMIN = "RC_ADMIN";
    
    public static final String ASSOCIATES = "ASSOCIATES";
    
    public static final String CLINIC_ADMIN = "CLINIC_ADMIN";
    
    public static final String CARE_GIVER = "CARE_GIVER";

    public static final String ANONYMOUS = "ANONYMOUS";
}
