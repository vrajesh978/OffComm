package com.example.prasanna.offcom;

import java.util.*;

public class Message
{
	private String to;
	private String from;
	private String text; // In case of a file, it carries the name of the file and carries a message otherwise.
	private Date timestamp; //constructor: public Date(), Allocates a Date object and initializes it so that it represents the time at which it was allocated, measured to the nearest millisecond.
	private boolean isFile;
	private boolean isGroup;

	public Message() {}

    public Message(String text, String to, String from, boolean isFile, boolean isGroup) {
        this.text = text;
        this.to = to;
        this.from = from;
        this.isFile = isFile;
        this.isGroup = isGroup;
        this.timestamp = new Date();
    }

	public void setSender(String from)
	{
		this.from = from;
	}
	
	public void setReceiver(String to)
	{
		this.to = to;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public void setIsFile(boolean isFile)
	{
		this.isFile = isFile;
	}
	
	public void setIsGroup(boolean isGroup)
	{
		this.isGroup = isGroup;
	}
	
	public String getReceiver()
	{
		return to;
	}
	
	public String getSender()
	{
		return from;
	}
	
	public String getText() //In case of a file, it returns the name of the file and returns the message otherwise.
	{
		return text;
	}
	
	public Date getTimestamp() // returns the Date object. this object can be used to get specific form/field of date as per requirement.
	{
		return timestamp; 
	}
	
	public boolean isFile()
	{
		return isFile;
	}
	
	public boolean isGroup()
	{
		return isGroup;
	}
}
