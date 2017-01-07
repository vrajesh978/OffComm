package com.example.prasanna.offcom;

import java.util.*;

public class GroupMessage {
	ArrayList<Message> messageList; //ArrayList is unsynchronized implementation of List interface.
    GroupInfo groupInfo;
	
	public GroupMessage(GroupInfo g) {
		messageList = new ArrayList<Message>();
        groupInfo = g;
	}

	public void addMessage(Message msg) {
		messageList.add(msg);
	}

	public void storeMessageInDatabase() {//for backup purpose, when session closes.
		//implementation on bases of Android libs;
	}
	
	public Message retrieveMessageFromDatabase() {
		//implementation on bases of Android libs;
	    return null;
    }
	
	//public void sortMessages(){}
}