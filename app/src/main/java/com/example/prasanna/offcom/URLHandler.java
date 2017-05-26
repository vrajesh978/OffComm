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
import java.net.InterfaceAddress;
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
    private static Activity callback;
    HashMap<String, String> data;
    AllMessages allMessages;

    UserList ul = GlobalVariables.getUserList();
    GroupList gl = GlobalVariables.getGroupList();

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
            switch (data.get("route")) {
                case "message":
                    this.handleMessage();
                    break;

                case "groupCreationMessage":
                    this.handleGroupCreationMessage();
                    break;

                case "getFile":
                    break;

                case "shareFile":
                    break;

                case "usernameVerification":
                    break;

                default:
                    Log.w(TAG, "Unknown route: " + data.get("route"));
            }

        } catch (IOException e) {}
    }

    private void handleMessage() {
        String text = data.get("text");
        String sender = data.get("from");
        String receiver = data.get("to");
        boolean isFile = false;
        boolean isGroup = data.get("chat_type").equals("individual") ? false : true;
        // Message(txt, to, from, isfile, isgroup)
        Message m = new Message(text, receiver, sender, isFile, isGroup);
        allMessages.addReceivedMessage(m);
        this.displayMessage();
    }

    private void handleGroupCreationMessage() {
        String groupName = data.get("groupName");
        String userList = data.get("participants");
        String[] userInfo = userList.split(",");

        GroupInfo g = new GroupInfo(groupName);
        for (String user: userInfo) {
            if (user.length() > 0) {
                String[] userData = user.split(" ");
                String username = userData[0];
                String ip = userData[1];
                int port = Integer.parseInt(userData[2]);
                Log.d(TAG,"username = " + username + " ip = " + ip + " port = " + port);
                UserInfo u = ul.getUser(username);
                if(u == null) {
                    u = new UserInfo(ip, port, username);
                }
                if(u.userName.equals(MainActivity.getMyUserName()))
                    continue;
                g.addUserToGroup(u);
            }
        }
        gl.addGroup(g);
        this.displayUsers();
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

    private void displayMessage() {
        Log.d(TAG, "displayMessage: calling display msg from urlHandler after getting msg ");
        //Log.d(TAG,callback.toString());
        //not working
        callback = ChatActivity.getInstance();
        if (callback == null) {
            callback = ChatActivity.getInstance();
            Log.d(TAG,"no callback");
        }
        callback.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "displayMessage:run method called display msg from urlHandler");
                    ((ChatActivity) callback).displayMessage();
                }

            });

    }

    private void displayUsers() {
        //Log.d(TAG,callback.toString());
        callback = MainActivity.getInstance();
        if (callback != null) {
            callback.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "displayUsers:run method called display msg from urlHandler");
                    ((MainActivity) callback).displayUsers();
                }
            });
        }
    }

    public static void setActivity(Activity activity) {
        Log.d(TAG,"setActivity called" + activity.toString());
        callback = activity;
    }

    public static void removeActivity() {
        //Log.d(TAG,"destroy called" + callback.toString());
        if(callback.equals(Activity.class))
            callback = null;
    }
}
