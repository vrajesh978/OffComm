package com.example.prasanna.offcom;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

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
    ChatServer(int port, Activity mainActivity) {
        mPort = port;
        handler = new URLHandler(mainActivity);
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

    public void run() {
        try {
            mServer = new ServerSocket(mPort);
            mPort = mServer.getLocalPort();
            while (mRunning) {
                Socket client = mServer.accept();
                handler.handle(client);
                client.close();
            }
        }
        catch (SocketException e) {}
        catch (IOException e) {}
    }
}
