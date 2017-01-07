package com.example.prasanna.offcom;

import java.util.*;

public class PersonalMessage {
	ArrayList<Message> messageList;		//ArrayList is unsynchronized implementation of List interface.
    UserInfo userInfo;
	
	public PersonalMessage(UserInfo u) {
		messageList = new ArrayList<Message>();
        userInfo = u;
	}

	public void addMessage(Message msg) {
		messageList.add(msg);
	}
	
	public void storeMessageInDatabase() { //for backup purpose, when session closes.
		//implementation on bases of Android libs;
	}
	
	public void retrieveMessageFromDatabase() {
		//implementation on bases of Android libs;
	}
	
	//public void sortMessages(){}

}