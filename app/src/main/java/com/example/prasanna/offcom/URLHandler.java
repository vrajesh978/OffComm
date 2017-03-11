package com.example.prasanna.offcom;

import android.app.Activity;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by prasanna on 16/12/16.
 */

public class URLHandler {
    private PrintStream out;
    private OutputStream sout;
    private InputStream sin;
    private static ChatActivity callback;
    HashMap<String, String> data;
    AllMessages allMessages;

    public URLHandler(AllMessages allMessages) {
        callback = null;
        this.allMessages = allMessages;
    }

    public void handle(Socket socket) {
        try {
            sout = socket.getOutputStream();
            sin = socket.getInputStream();
            JsonReader jr = new JsonReader(new InputStreamReader(sin));
            out = new PrintStream(sout);
            jr.beginObject();
            data = new HashMap<String, String>();
            while(jr.hasNext()) {
                String key = jr.nextName();
                String name = jr.nextString();
                data.put(key, name);
            }

            Log.d(TAG, "Message handler." );
            HashMap<String, String> content;
            if (data.get("route").equals("message")) {
                this.handle_message();
                //Handle incoming messages.
            }

            else if (data.get("route").equals("get_file")) {
                this.send_file();
                //Handle incoming file request.
            }

            else if (data.get("route").equals("share_file")) {
                //Handle incoming file sharing message.
            }

            else if(data.get("route").equals("username_verification")) {
                //Verify new user name.
            }


        } catch (IOException e) {}
    }

    private void handle_message() {
        String text = data.get("text");
        String sender = data.get("from");
        String receiver = data.get("to");
        boolean isFile = false;
        boolean isGroup = data.get("chat_type").equals("individual") ? false : true;
        // Message(txt, to, from, isfile, isgroup)
        Message m = new Message(text, receiver, sender, isFile, isGroup);
        allMessages.addReceivedMessage(m);
        this.send_display();
    }

    private void send_file() {
        //TODO: Implement function to send file.
    }

    private HashMap<String, String> share_file() {
        HashMap<String, String> retMessage = new HashMap<String, String>();
        retMessage.put("content_type", "file");
        retMessage.put("file_name", data.get("file_name"));
        retMessage.put("file_size", data.get("file_size"));
        retMessage.put("from", data.get("from"));
        retMessage.put("to", data.get("to"));
        retMessage.put("chat_type", data.get("chat_type"));
        return retMessage;
    }

    private void user_name_verification() {
        boolean available=false;
        String username = data.get("username");
        JsonWriter jw = new JsonWriter(new OutputStreamWriter(sout));
        // Verify user name calling user info class object.
        try {
            jw.beginObject();
            if (available) {
                jw.name("available").value("true");
            } else {
                jw.name("available").value("false");
            }
            jw.flush();
        } catch (IOException e) {}
    }

    private void send_display() {
        if (callback != null) {
            callback.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.displayMessage();
                }
            });
        }
    }

    public static void setChatActivity(Activity activity) {
        callback = (ChatActivity) activity;
    }

    public static void removeChatActivity() {
        callback = null;
    }
}
