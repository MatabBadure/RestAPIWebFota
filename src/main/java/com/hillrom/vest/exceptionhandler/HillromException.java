package com.hillrom.vest.exceptionhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HillromException extends Exception {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(HillromException.class);
    private String exceptionCode;
    private String message;
	
	public HillromException(String msg, Throwable e)
    {
        super(msg, e);
        exceptionCode = "System Exception";
        message = msg;
        log.error((new StringBuilder("Exception occurred: ")).append(exceptionCode).append(" : ").append(msg).append(", err  ").append(e.getMessage()).toString());
    }

    public HillromException(String msg)
    {
        super(msg);
        exceptionCode = "Application Exception";
        message = msg;
        log.error((new StringBuilder("Exception occurred: ")).append(exceptionCode).append(" : ").append(msg).toString());
    }

    public String getExceptionCode()
    {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode)
    {
        this.exceptionCode = exceptionCode;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
