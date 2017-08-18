package com.hillrom.vest.pointer.FOTA;

public class HandleHolder {
	private String currentChunk;
	private String previousChunkTransStatus;
	private String partNo;
	int chunkSize = 0;
	
	public int getChunkSize() {
		return chunkSize;
	}
	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}
	public String getCurrentChunk() {
		return currentChunk;
	}
	public void setCurrentChunk(String currentChunk) {
		this.currentChunk = currentChunk;
	}
	public String getPreviousChunkTransStatus() {
		return previousChunkTransStatus;
	}
	public void setPreviousChunkTransStatus(String previousChunkTransStatus) {
		this.previousChunkTransStatus = previousChunkTransStatus;
	}
	public String getPartNo() {
		return partNo;
	}
	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}
	
	public int hashCode() {
        return partNo.hashCode();
    }

   /* public boolean equals(HandleHolder hh) {
        if (hh == null)
            return false;
        else if (hh.partNo.equalsIgnoreCase(this.partNo))
            return true;
        else
            return false;
    }
	*/
	
	public boolean equals(Object o) {
	    if (o == null)
	        return false;
	    if (!(o instanceof HandleHolder))
	        return false;

	    HandleHolder hh = (HandleHolder) o;
	    if (hh.partNo.equalsIgnoreCase(this.partNo))
	        return true;
	    else
	        return false;
	}
}
