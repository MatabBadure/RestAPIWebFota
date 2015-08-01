package com.hillrom.vest.service.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Utility class for generating random Strings.
 */
public final class RandomUtil {

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
}
