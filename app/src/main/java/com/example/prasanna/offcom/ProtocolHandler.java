package com.example.prasanna.offcom;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by prasanna on 7/1/17.
 */

public class ProtocolHandler {
    public static Map<String, String> messageWrapper(Message msg) {
        Map<String, String> content = new ConcurrentHashMap<>();
        content.put("route", "message");
        content.put("text", msg.getText());
        content.put("from", msg.getSender());
        content.put("to", msg.getReceiver());
        content.put("chat_type", msg.isGroup() ? "group" : "individual");
        return content;
    }

    public static Map<String, String> groupCreationWrapper(GroupInfo group) {
        Map<String, String> content = new ConcurrentHashMap<>();
        content.put("route", "groupCreationMessage");
        content.put("groupName", group.getName());
        //getting own machine details
        String userList = MainActivity.getMyDetails();
        //getting group participants.
        for (UserInfo u: group.getGroupParticipant()) {
            userList += u.userName + " " + u.ip + " " + u.port + ",";
        }
        //send these details to the group members.
        content.put("participants", userList);
        return content;
    }
}
