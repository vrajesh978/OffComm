package com.example.prasanna.offcom;

import java.util.HashMap;

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

        String userList = "";
        for (UserInfo u: group.getGroupParticipant()) {
            userList += u.userName + " " + u.ip + " " + u.port + ",";
        }

        content.put("participants", userList);
        return content;
    }
}
