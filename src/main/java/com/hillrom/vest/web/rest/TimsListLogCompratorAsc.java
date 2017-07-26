package com.hillrom.vest.web.rest;

import java.util.Comparator;

import org.joda.time.DateTime;

public class TimsListLogCompratorAsc implements Comparator<TimsListLog>{
 
    @Override
    public int compare(TimsListLog e1, TimsListLog e2) {
    	
    	DateTime compareDate1 = e1.getLastMod();
    	DateTime compareDate2 = e2.getLastMod();
		

	        if(compareDate1.isAfter(compareDate2)){
	            return -1;
	        } 
	        else if (compareDate1.isBefore(compareDate2)){
	            return +1;
	        }
	        else
	        	return 0;
		
		
    	
    	
    }

	
}
