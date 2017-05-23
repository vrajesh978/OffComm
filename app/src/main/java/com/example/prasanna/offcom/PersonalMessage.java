package com.example.prasanna.offcom;

import java.util.*;

public class PersonalMessage extends MessageList{
    UserInfo userInfo;
	
	public PersonalMessage(UserInfo u) {
		messageList = new ArrayList<Message>();
        userInfo = u;
	}

	public void addMessage(Message msg) {
		messageList.add(msg);
	}
}