package com.example.prasanna.offcom;

import android.util.Log;

import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by prasanna on 7/1/17.
 */

public class ProtocolHandler {
    public static HashMap<String, String> messageWrapper(Message msg) {
        HashMap<String, String> content = new HashMap<>();
        content.put("route", "message");
        content.put("text", msg.getText());
        content.put("from", msg.getSender());
        content.put("to", msg.getReceiver());
        content.put("chat_type", msg.isGroup() ? "group" : "individual");
        return content;
    }

    public static HashMap<String, String> groupCreationWrapper(GroupInfo group) {
        HashMap<String, String> content = new HashMap<>();
        content.put("route", "groupCreationMessage");
        content.put("groupName", group.getName());

        String userList = MainActivity.getMyDetails();
        for (UserInfo u: group.getGroupParticipant()) {
            Log.d(TAG,"USER-DETAILS = " + u.userName + "=> " + u.ip);
            userList += u.userName + " " + u.ip + " " + u.port + ",";
        }

        content.put("participants", userList);
        return content;
    }
}
