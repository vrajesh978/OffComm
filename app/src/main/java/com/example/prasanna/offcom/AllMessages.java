package com.example.prasanna.offcom;

import java.util.HashMap;

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
            if (groupmsg == null) {
                gMsg.put(msg.getSender(), new GroupMessage(gl.getGroup(msg.getSender())));
                groupmsg = gMsg.get(msg.getSender());
            }
            groupmsg.addMessage(msg);
        }
        else {
            PersonalMessage personalmsg = pMsg.get(msg.getSender());
            if (personalmsg== null) {
                pMsg.put(msg.getSender(), new PersonalMessage(ul.getUser(msg.getSender())));
                personalmsg = pMsg.get(msg.getSender());
            }
            personalmsg.addMessage(msg);
        }
	}
	
	public PersonalMessage getMessagesForUser(String name) {
        return pMsg.get(name);
	}

    public GroupMessage getMessagesForGroup(String name) {
        return gMsg.get(name);
    }
}