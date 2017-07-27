package com.hillrom.vest.web.rest;

import java.util.Comparator;

//import org.joda.time.DateTime;

import java.util.Date;

public class TimsListLogCompratorDesc implements Comparator<TimsListLog>{
 
    @Override
    public int compare(TimsListLog e1, TimsListLog e2) {
    	
    	Date compareDate1  = e1.getLastMod();;
    	Date compareDate2 = 	e2.getLastMod();;
			
		if(compareDate1.after(compareDate2)){
	            return +1;
	        } 
	        else if (compareDate1.before(compareDate2)){
	            return -1;
	        }
	        else
	        	return 0;
		
		
    	
    	
    }

	
}
