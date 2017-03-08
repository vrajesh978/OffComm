package com.example.prasanna.offcom;

import android.app.Activity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by prasanna on 16/12/16.
 */

public class ChatServer implements Runnable {
    static int mPort;
    ServerSocket mServer;
    static boolean mRunning;
    URLHandler handler;
    AllMessages allMessages;

    ChatServer(AllMessages allMessages) {
        this.allMessages = allMessages;
        handler = new URLHandler(allMessages);
    }

    public void start() {
        mRunning = true;
        new Thread((Runnable) this).start();
    }

    public void stop() {
        mRunning = false;
        if(mServer != null) {
            try {
                mServer.close();
            } catch (IOException e) {}
            mServer = null;
        }
    }
    public  void setLocalPort(int port) {
        mPort = port;
    }

    public void run() {
        try {
            mServer = new ServerSocket(0);
            setLocalPort(mServer.getLocalPort());
            while (mRunning) {
                final Socket client = mServer.accept();
                new Thread( new Runnable() {
                    public void run() {
                        try {
                            handler.handle(client);
                            client.close();
                        } catch (IOException e) {}
                    }
                }).start();
            }
        }
        catch (SocketException e) {}
        catch (IOException e) {}
    }
}
