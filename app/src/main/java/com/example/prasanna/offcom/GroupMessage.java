package com.example.prasanna.offcom;

import java.util.*;

public class GroupMessage extends MessageList{
    GroupInfo groupInfo;
	
	public GroupMessage(GroupInfo g) {
		messageList = new ArrayList<Message>();
        groupInfo = g;
	}

	public void addMessage(Message msg) {
		messageList.add(msg);
	}

}