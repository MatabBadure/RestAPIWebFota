package com.hillrom.vest.service.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.web.rest.dto.CareGiverVO;
import com.hillrom.vest.web.rest.dto.ClinicVO;
import com.hillrom.vest.web.rest.dto.HcpClinicsVO;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {
	
	public static final Long FIVE_DAY_SURVEY_ID = 1L;
	public static final Long THIRTY_DAY_SURVEY_ID = 2L;
	public static final Long NIGHTY_DAY_SURVEY_ID = 3L;

	public static final Integer FIVE_DAYS = 5;
	public static final Integer THIRTY_DAYS = 30;
	public static final Integer NINTY_DAYS = 90;

    private static final int DEF_COUNT = 20;
    
    private static String EMAIL_PATTERN = "^(.+)@(.+)$";
    
    private static String HILLROM_ID_PATTERN = "[A-Z]{3,3}_[A-Z]{2,2}:[A-Z]{2,2}:"
    		+ "[0-9]{2,2}:[0-9]{2,2}:[0-9]{2,2}:[0-9]{2,2}:[0-9]{2,2}:[A-Z]{1}[0-9]{1}";//PAT_ID:BT:00:06:66:08:54:B6
    
    private static Pattern pattern;
    private static Matcher matcher;

    private RandomUtil() {
    }

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(DEF_COUNT);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(DEF_COUNT);
    }

    /**
    * Generates a reset key.
    *
    * @return the generated reset key
    */
   public static String generateResetKey() {
       return RandomStringUtils.randomNumeric(DEF_COUNT);
   }
   
   public static boolean isValidEmail(String email){
	   if(StringUtils.isBlank(email))
		   return false;
	   pattern = Pattern.compile(EMAIL_PATTERN);
	   matcher = pattern.matcher(email);
	   return matcher.matches();
   }
   
   public static boolean isValidHillromId(String hillromId){
	   pattern = Pattern.compile(HILLROM_ID_PATTERN);
	   matcher = pattern.matcher(hillromId);
	   return matcher.matches();
   }
   
   public static <T> List<T> getDifference(List<T> list1, List<T> list2){
	   List<T> diff = list1.stream()
               .filter(i -> !list2.contains(i))
               .collect (Collectors.toList());
	   return diff;
   }
   
	public static List<ClinicVO> sortClinicVOListByName(List<ClinicVO> clinics) {
		return clinics.stream()
				.sorted((clinicVO1, clinicVO2) -> clinicVO1.getName().compareToIgnoreCase(clinicVO2.getName()))
				.collect(Collectors.toList());
	}
	
	public static List<User> sortUserListByLastNameFirstName(List<User> user) {
		return user.stream()
				.sorted((user1, user2) -> concatUserName(user1).compareToIgnoreCase(concatUserName(user2)))
				.collect(Collectors.toList());
	}
	
	public static List<CareGiverVO> sortCareGiverVOListByLastNameFirstName(List<CareGiverVO> careGiverVO) {
		return careGiverVO.stream()
				.sorted((careGiverVO1, careGiverVO2) -> concatCareGiverVOName(careGiverVO1).compareToIgnoreCase(concatCareGiverVOName(careGiverVO2)))
				.collect(Collectors.toList());
	}
	
	public static List<HcpClinicsVO> sortHcpClinicsVOListByLastNameFirstName(List<HcpClinicsVO> hcpVOs) {
		return hcpVOs.stream()
				.sorted((hcpVOs1, hcpVOs2) -> concatHCPVOsName(hcpVOs1).compareToIgnoreCase(concatHCPVOsName(hcpVOs2)))
				.collect(Collectors.toList());
	}
	
	private static String concatUserName(User user){
		return user.getLastName().concat(user.getFirstName());
	}
	
	private static String concatHCPVOsName(HcpClinicsVO hcpClinicsVO){
		return hcpClinicsVO.getLastName().concat(hcpClinicsVO.getFirstName());
	}
	
	private static String concatCareGiverVOName(CareGiverVO careGiverVO){
		return careGiverVO.getUser().getLastName().concat(careGiverVO.getUser().getFirstName());
	}
}
