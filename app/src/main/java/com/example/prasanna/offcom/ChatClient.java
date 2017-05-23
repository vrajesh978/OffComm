package com.example.prasanna.offcom;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.RunnableFuture;

import static android.content.ContentValues.TAG;

/**
 * Created by prasanna on 16/12/16.
 */

public class ChatClient {
    Socket socket;
    UserList ul = GlobalVariables.getUserList();
    GroupList gl = GlobalVariables.getGroupList();

    private void connectSocket(Inet4Address ip, int port) throws SocketException, IOException{
        Log.d(TAG, "Connecting to socket");
        socket = new Socket(ip, port);
        Log.d(TAG, "Connected to socket");
    }

    private void _sendData(HashMap<String, String> content) throws IOException{
        JsonWriter jw = new JsonWriter(new OutputStreamWriter(socket.getOutputStream()));
        jw.beginObject();
        for (HashMap.Entry<String, String> it : content.entrySet()) {
            jw.name(it.getKey()).value(it.getValue());
        }
        jw.endObject();
        jw.flush();
    }

    public void sendData(final UserInfo u, final HashMap<String, String> content) throws UnknownHostException {
        final Inet4Address inet = (Inet4Address) InetAddress.getByName(u.ip);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectSocket(inet, u.port);
                    _sendData(content);
                    socket.close();
                    Log.d(TAG, "Data sent to " + u.userName);
                } catch (IOException e) {
                    Log.w(TAG, "Could not connect to other device: ");
                }
            }
        }).start();
    }

    public void sendMessage(Message msg) throws UnknownHostException {
        boolean isGroup = msg.isGroup();
        final HashMap<String, String> content = ProtocolHandler.messageWrapper(msg);
        if (isGroup) {
            GroupInfo g = gl.getGroup(msg.getReceiver());

            for (final UserInfo u: g.getGroupParticipant()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendData(u, content);
                        }
                        catch (UnknownHostException e) {};
                    }
                }).start();
            }

        }
        else {
            final UserInfo u = ul.getUser(msg.getReceiver());
            sendData(u, content);
        }

    }

    public void sendGroupCreationMessage(GroupInfo g) {
        final HashMap<String, String> content = ProtocolHandler.groupCreationWrapper(g);
        for (final UserInfo u: g.getGroupParticipant()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendData(u, content);
                    }
                    catch (UnknownHostException e) {};
                }
            }).start();
        }
    }

    public void get_file(Inet4Address ip, int port) {
        // Download file shared by server.
    }
}
