package com.hillrom.vest.web.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class TimsListLogComprator implements Comparator<TimsListLog>{
 
    @Override
    public int compare(TimsListLog e1, TimsListLog e2) {
    	
    	Date compareDate1;
    	Date compareDate2;
		try {
			compareDate1 = new SimpleDateFormat("MM/dd/yyyy").parse(e1.getLastMod());
			compareDate2 = 	new SimpleDateFormat("MM/dd/yyyy").parse(e2.getLastMod());

	        if(compareDate1.after(compareDate2)){
	            return 1;
	        } else {
	            return -1;
	        }
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
    	
    	
    }

	
}
