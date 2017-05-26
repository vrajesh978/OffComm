package com.example.prasanna.offcom;

import android.util.Log;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class AllMessages {
	private HashMap<String, PersonalMessage> pMsg;
	private HashMap<String, GroupMessage> gMsg;
	UserList ul;
    GroupList gl;

	public AllMessages(UserList ul, GroupList gl) {
		pMsg = new HashMap<>();
		gMsg = new HashMap<>();
        this.ul = ul;
        this.gl = gl;
	}
	
	public void addSentMessage(Message msg) {
		if(msg.isGroup()) {
			GroupMessage groupmsg = gMsg.get(msg.getReceiver());
            Log.d(TAG,"receiver name = " + msg.getReceiver());
            if (groupmsg == null) {
                gMsg.put(msg.getReceiver(), new GroupMessage(gl.getGroup(msg.getReceiver())));
                groupmsg = gMsg.get(msg.getReceiver());
            }
			groupmsg.addMessage(msg);			
		}
		else {
			PersonalMessage personalmsg = pMsg.get(msg.getReceiver());
            if (personalmsg== null) {
                pMsg.put(msg.getReceiver(), new PersonalMessage(ul.getUser(msg.getReceiver())));
                personalmsg = pMsg.get(msg.getReceiver());
            }
			personalmsg.addMessage(msg);
		}
	}

	public void addReceivedMessage(Message msg) {
        if(msg.isGroup()) {
            GroupMessage groupmsg = gMsg.get(msg.getReceiver());
            Log.d(TAG,"receiver name = " + msg.getReceiver());
            if (groupmsg == null) {
                gMsg.put(msg.getReceiver(), new GroupMessage(gl.getGroup(msg.getReceiver()))); //getSender
                groupmsg = gMsg.get(msg.getReceiver()); //getSender
                Log.d(TAG, "addReceivedMessage: first Groupmsg ");
            }
            groupmsg.addMessage(msg);
            Log.d(TAG, "addReceivedMessage: Groupmsg  added to list");
        }
        else {
            PersonalMessage personalmsg = pMsg.get(msg.getSender());
            if (personalmsg== null) {
                pMsg.put(msg.getSender(), new PersonalMessage(ul.getUser(msg.getSender())));
                personalmsg = pMsg.get(msg.getSender());
                Log.d(TAG, "addReceivedMessage: first msg ");
            }
            personalmsg.addMessage(msg);
            Log.d(TAG, "addReceivedMessage: msg  added to list");
        }
	}
	
	public PersonalMessage getMessagesForUser(String name) {
        return pMsg.get(name);
	}

    public GroupMessage getMessagesForGroup(String name) {
        return gMsg.get(name);
    }
}