package com.hillrom.vest.util;

public class ExceptionConstants {

	
	private ExceptionConstants() {
    }

    public static final String HR_501 = "e-mail address already in use";
    public static final String HR_502 = "Parameters missing";
    public static final String HR_503 = "Invalid Reset Key";
    public static final String HR_504 = "Reset Key Expired";
    public static final String HR_505 = "Incorrect Security Question or Password";
    public static final String HR_506 = "Incorrect password";
    public static final String HR_507 = "Invalid Security Question or Answer";//Is it same as HR_505?
    public static final String HR_508 = "Required field Email is missing";//Is it same as HR_505?
    public static final String HR_509 = "Email Already registered, please choose another email";
    public static final String HR_510 = "Please accept terms and conditions";
    public static final String HR_510_1 = "Required field Answer is missing";// We will change variable name
    public static final String HR_510_2 = "Required field SecurityQuestion is missing";// We will change variable name
    public static final String HR_510_3 = "Invalid Activation Key";// We will change variable name
    public static final String HR_510_4 = "Invalid Data";// We will change variable name
    public static final String HR_510_5 = "Incorrect Data";// We will change variable name
    
    // Hillrom team User
    public static final String HR_511 = "Unable to create Hillrom User.";
    public static final String HR_512 = "No such user exist";
    public static final String HR_513 = "Unable to delete User";
    public static final String HR_514 = "Unable to fetch User";
    public static final String HR_515 = "Unable to create Caregiver User";
    public static final String HR_516 = "Unable to delete Caregiver User";
    
    // Patient User
    public static final String HR_521 = "Unable to create Patient.";
    public static final String HR_522 = "HR Id already in use.";
    public static final String HR_523 = "No such patient exist";
    public static final String HR_524 = "Unable to update Patient";
    
    // HCP User
    public static final String HR_531 = "Unable to create HealthCare Professional";
    public static final String HR_532 = "Invalid HCP id";
    public static final String HR_533 = "Unable to fetch HealthCare Professional";
    
    
    //Clinic
    public static final String HR_541 = "Unable to create Clinic";
    public static final String HR_542 = "Clinic can't be parent of his own";
    public static final String HR_543 = "Unable to update Clinic";
    public static final String HR_544 = "No such clinic exists";
    public static final String HR_545 = "Unable to delete Clinic. Clinic admin exists";
    public static final String HR_546 = "Unable to delete Clinic. Healthcare Professionals are associated with it";
    public static final String HR_547 = "Invalid clinic id found";
    public static final String HR_548 = "No such clinic found";// Is it same as HR_544
    
    // Protocol
    public static final String HR_551 = "No protocol found for patient.";
}
