package com.hillrom.vest.util;

public class ExceptionConstants {

	
	private ExceptionConstants() {
    }

    public static final String HR_501 = "e-mail address already in use";
    public static final String HR_502 = "Parameters missing";
    public static final String HR_503 = "Required field Answer is missing";
    public static final String HR_504 = "Reset Key Expired";
    public static final String HR_505 = "Incorrect Security Question or Password";
    public static final String HR_506 = "Incorrect password";
    public static final String HR_507 = "Required field SecurityQuestion is missing";
    public static final String HR_508 = "Required field Email is missing";//Is it same as HR_505?
    public static final String HR_509 = "Email Already registered, please choose another email";
    public static final String HR_510 = "Please accept terms and conditions";

    
    // Hillrom team User
    public static final String HR_511 = "Unable to create Hillrom User.";
    public static final String HR_512 = "No such user exist";
    public static final String HR_513 = "Unable to delete User";
    public static final String HR_514 = "Unable to fetch User";
    public static final String HR_515 = "Unable to create Caregiver User";
    public static final String HR_516 = "Unable to delete Caregiver User";
    public static final String HR_517 = "Unable to Update Hillrom User";
    
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
    
    public static final String HR_553 = "Invalid Activation Key";
    public static final String HR_554 = "Invalid Data";
    public static final String HR_555 = "Incorrect Data";
    public static final String HR_556 = "Invalid Reset Key";
    public static final String HR_557 = "Invalid Security Question or Answer";//Is it same as HR_505?
    
    // Protocol
    public static final String HR_551 = "No such protocol found for patient.";
    public static final String HR_552 = "Treatments/day should be in between 1 & 7. (both inclusive).";
    public static final String HR_558 = "Unable to create protocol.";
    public static final String HR_559 = "Unable to update protocol.";
    
    //Caregiver user
    public static final String HR_561 = "Unable to create Caregiver.";
    public static final String HR_562 = "Unable to update Caregiver.";
    public static final String HR_563 = "Reached maximum limit to associate caregiver.";
    public static final String HR_564 = "No such caregiver found";
    public static final String HR_565 = "No Relationship labels found.";
}
