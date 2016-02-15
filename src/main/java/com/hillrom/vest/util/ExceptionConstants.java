package com.hillrom.vest.util;

public class ExceptionConstants {

	
	private ExceptionConstants() {
    }

    public static final String HR_501 = "e-mail address already in use";
    public static final String HR_502 = "Parameters missing";
    public static final String HR_503 = "Required field Answer is missing";
    public static final String HR_504 = "Reset Key Expired";
    public static final String HR_505 = "Incorrect Security Question or Answer";
    public static final String HR_506 = "Incorrect password";
    public static final String HR_507 = "Required field SecurityQuestion is missing";
    public static final String HR_508 = "Required field Email is missing";//Is it same as HR_505?
    public static final String HR_509 = "Email Already registered, please choose another email";
    public static final String HR_510 = "Please accept terms and conditions";

    
    // Hillrom team User
    public static final String HR_511 = "Unable to create Hillrom User.";
    public static final String HR_512 = "No such user exists";
    public static final String HR_513 = "Unable to delete User";
    public static final String HR_514 = "Unable to fetch User";
    public static final String HR_515 = "Unable to create Caregiver User";
    public static final String HR_516 = "Unable to delete Caregiver User";
    public static final String HR_517 = "Unable to Update Hillrom User";
    public static final String HR_518 = "Unable to update User";
    public static final String HR_519 = "Unable to fetch Users";
    public static final String HR_520 = "You can not delete yourself.";
    
    // Patient User
    public static final String HR_521 = "Unable to create Patient.";
    public static final String HR_522 = "HR Id already in use.";
    public static final String HR_523 = "No such patient exist";
    public static final String HR_524 = "Unable to update Patient";
    
    // HCP User
    public static final String HR_531 = "Unable to create HealthCare Professional";
    public static final String HR_532 = "Invalid HCP id";
    public static final String HR_533 = "Unable to fetch HealthCare Professional";
    public static final String HR_534 = "Unable to associate HCP with patient.";
    public static final String HR_535 = "Unable to associate Clinic with patient.";
    public static final String HR_536 = "Unable to dissociate Clinic with patient.";    
    
    // Clinic Admin User
    public static final String HR_537 = "Unable to create Clinic Admin User";
    public static final String HR_538 = "Invalid Clinic Admin id";
    public static final String HR_539 = "Clinic already associated with Clinic Admin.";
    public static final String HR_540 = "Unable to associate Clinic Admin.";
    public static final String HR_550 = "Unable to dissociate Clinic Admin.";
    public static final String HR_607 = "No Clinic Admin associated.";
    
    //Clinic
    public static final String HR_541 = "Unable to create Clinic";
    public static final String HR_542 = "Clinic can't be parent of his own";
    public static final String HR_543 = "Unable to update Clinic";
    public static final String HR_544 = "No such clinic exists";
    public static final String HR_545 = "Unable to delete Clinic. Clinic admin exists";
    public static final String HR_546 = "Unable to delete Clinic. Healthcare Professionals are associated with it";
    public static final String HR_547 = "Invalid clinic id found";
    public static final String HR_548 = "No clinic found";// Is it same as HR_544
    public static final String HR_549 = "Unable to delete Clinic.";
    
    public static final String HR_553 = "Invalid Activation Key";
    public static final String HR_554 = "Invalid Data";
    public static final String HR_555 = "Incorrect Data";
    public static final String HR_556 = "Invalid Reset Key";
    public static final String HR_557 = "Invalid Security Question or Answer";
    public static final String HR_558 = "Forbidden";
    
    // Protocol
    public static final String HR_551 = "No such protocol found for patient.";
    public static final String HR_552 = "Treatments/day should be in between 1 & 7. (both inclusive).";
    public static final String HR_559 = "Unable to create protocol.";
    public static final String HR_560 = "Unable to update protocol.";
    
    //Caregiver user
    public static final String HR_561 = "Unable to create Caregiver.";
    public static final String HR_562 = "Unable to update Caregiver.";
    public static final String HR_563 = "Reached maximum limit to associate caregiver.";
    public static final String HR_564 = "No caregiver found";
    public static final String HR_565 = "No Relationship labels found.";
    public static final String HR_566 = "Unable to delete Caregiver.";
    public static final String HR_567 = "Unable to fetch Caregivers.";
    public static final String HR_568 = "Unable to fetch Caregiver.";
    
    public static final String HR_570 = "Vest device is already in Inactive mode.";
    public static final String HR_571 = "Invalid Serial Number.";
    public static final String HR_572 = "This vest device serial number or bluetooth id is already linked to patient ";
    public static final String HR_573 = "Unable to deactivate vest device from patient.";
    public static final String HR_574 = "Unable to create clinic admin.";
    public static final String HR_575 = "Unable to update clinic admin.";
    public static final String HR_576 = "Unable to get clinic admin.";
    public static final String HR_577 = "Unable to update Caregiver User.";
    public static final String HR_578 = "This bluetooth id is already linked to patient ";
    public static final String HR_579 = "Unable to update Associate User.";
    
    public static final String HR_581 = "No Patient Associated with HCP.";
    public static final String HR_582 = "No Clinics Associated with HCP.";
    public static final String HR_583 = "Unable to associate HCP with clinic.";
    public static final String HR_584 = "No statistics found.";
    public static final String HR_585 = "No such patients found.";
    public static final String HR_586 = "No Clinics Associated with Clinic Admin.";
    public static final String HR_587 = "No patient Associated with Caregiver.";
    public static final String HR_588 = "No HCP Associated with Patient.";
    public static final String HR_589 = "No patient associated with HCP as caregiver.";
    public static final String HR_599 = "MRN Id already in use with this clinic.";
    
    // Notifications
    public static final String HR_591 = "No such Notification found";
    public static final String HR_592 = "Activation link expired.";
    public static final String HR_596 = "Unable to update password";
    public static final String HR_597 = "Password do not match with old password.";
    public static final String HR_598 = "Incorrect new password";
    
    //General 4** Exceptions
    public static final String HR_403 = "Permission Denied";
    
    // DateFormat Exception
    public static final String HR_600 = "Invalid DateFormat, expected is : "; 

    public static final String HR_601 = "Invalid Activation Key"; 
    public static final String HR_602 = "No security question selected";
    public static final String HR_603 = "Invalid username, please enter valid email id.";
    public static final String HR_604 = "Unable to activate User";
    public static final String HR_605 = "User is already in active state.";
    public static final String HR_606 = "Activation link can not be sent.";
    public static final String HR_608 = "No security question available";
    
    public static final String HR_701 = "Note date cannot be before first  transmission date."; 
    
    public static final String HR_702 = "First transmission date does not exist."; 
    public static final String HR_703 = "Currently, this account is inactive. Kindly contact with Administrator.";
    public static final String HR_704 = "e-mail address not registered";
    public static final String HR_705 = "The activation link which you are trying to access is no more valid. ";
    public static final String HR_706 = "Please contact Hill-Rom Respiratory Care Customer Support";
    public static final String HR_707 = "The reset link which you are trying to access is no more valid. ";

    public static final String HR_708 = "Incorrect Hillrom User Authority";
    public static final String HR_709 = "Incorrect City Name";
    public static final String HR_710 = "Incorrect State Name/Code";
    public static final String HR_711 = "Incorrect Zip Code";
    public static final String HR_712 = "Zip Code Not found";
}
