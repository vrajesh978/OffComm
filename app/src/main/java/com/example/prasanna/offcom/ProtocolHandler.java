package com.example.prasanna.offcom;

import java.util.HashMap;

/**
 * Created by prasanna on 7/1/17.
 */

public class ProtocolHandler {
    public HashMap<String, String> message_wrapper(Message msg) {
        HashMap<String, String> content = new HashMap<String, String>();
        content.put("route", "message");
        content.put("text", msg.getText());
        content.put("from", msg.getSender());
        content.put("to", msg.getReceiver());
        content.put("chat_type", msg.isGroup() ? "group" : "individual");
        return content;
    }
}
