package com.example.prasanna.offcom;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PersonalMessage extends MessageList{
    UserInfo userInfo;
	
	public PersonalMessage(UserInfo u) {
		messageList = new CopyOnWriteArrayList<>();
        userInfo = u;
	}

	public void addMessage(Message msg) {
		messageList.add(msg);
	}
}