package com.example.prasanna.offcom;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupMessage extends MessageList{
    GroupInfo groupInfo;
	
	public GroupMessage(GroupInfo g) {
		messageList = new CopyOnWriteArrayList<Message>();
        groupInfo = g;
	}

	public void addMessage(Message msg) {
		messageList.add(msg);
	}

}