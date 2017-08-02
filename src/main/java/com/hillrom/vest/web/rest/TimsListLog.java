package com.hillrom.vest.web.rest;

import java.util.Date;



public class TimsListLog {
	
	private String file;
    private String path;
    private String status;
    private Date lastMod;
    
	     
   public Date getLastMod() {
		return lastMod;
	}
	public void setLastMod(Date lastMod) {
		this.lastMod = lastMod;
	}
    public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	    
	   
	    
	   
	

}
