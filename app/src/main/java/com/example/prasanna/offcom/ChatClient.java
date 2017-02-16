package com.example.prasanna.offcom;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.JsonWriter;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.RunnableFuture;

import static android.content.ContentValues.TAG;

/**
 * Created by prasanna on 16/12/16.
 */

public class ChatClient {
    Socket socket;
    private void connect_socket(Inet4Address ip, int port) throws SocketException, IOException{
        Log.d(TAG, "Connecting to socket");
        socket = new Socket(ip, port);
        Log.d(TAG, "Connected to socket");
    }

    private void send_data(HashMap<String, String> content) throws IOException{
        JsonWriter jw = new JsonWriter(new OutputStreamWriter(socket.getOutputStream()));
        jw.beginObject();
        for (HashMap.Entry<String, String> it : content.entrySet()) {
            jw.name(it.getKey()).value(it.getValue());
        }
        jw.endObject();
        jw.flush();
    }

    public void send_message(final Inet4Address ip, final int port, final HashMap<String, String> content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connect_socket(ip, port);
                    send_data(content);
                    socket.close();
                } catch (IOException e) {
                    Log.w(TAG, "Could not connect to other device: ");
                    //Could not connect to server;
                }
                Log.d(TAG, "Message sent.");
            }
        }).start();
    }


    public void get_file(Inet4Address ip, int port) {
        // Download file shared by server.
    }
}
