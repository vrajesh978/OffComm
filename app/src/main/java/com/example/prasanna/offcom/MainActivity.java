package com.example.prasanna.offcom;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    Inet4Address localIp;
    Inet4Address inet;

    int port;
    float scale;

    AllMessages allMessages;
    ProtocolHandler protocol;

    UserList ul;
    GroupList gl;

    ChatServer cs;


    IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;

    WiFiDirectBroadcastReceiver receiver;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        protocol = new ProtocolHandler();
        localIp = get_ip_address();
        ul = GlobalVariables.getUserList();
        gl = GlobalVariables.getGroupList();
        allMessages = new AllMessages(ul, gl);
        cs = new ChatServer(0, this, allMessages);

        cs.start();
        port = cs.mPort;

        //WIFIp2p
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        scale = this.getResources().getDisplayMetrics().density;
    }

    public void set_ip_address(View view) {
        EditText ip = (EditText) findViewById(R.id.ipaddr);
        TextView port = (TextView) findViewById(R.id.port);
        port.setText(new Integer(this.port).toString());
        try {
            inet = (Inet4Address) InetAddress.getByName(ip.getText().toString());
        } catch (UnknownHostException e) {}
    }

    public Inet4Address get_ip_address() {
        Inet4Address ip = null;

        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            byte[] bytes = ByteBuffer.allocate(4).putInt(wm.getConnectionInfo().getIpAddress()).array();
            Collections.reverse(Arrays.asList(bytes));
            ip = (Inet4Address) Inet4Address.getByAddress(bytes);
        } catch (Exception e) {}

        return ip;
    }



    public void send_message(View view) {
        EditText ed = (EditText) findViewById(R.id.edittext);
        ChatClient cc = new ChatClient();
        String text = ed.getText().toString();
        Message msg = new Message(text, "Device 1", "Device 2", false, false);
        HashMap<String, String> content = protocol.message_wrapper(msg);
        cc.send_message(inet, port, content);
        this.display("Sent: ", msg);
    }

    public void display(String label, Message msg) {
        Log.d(TAG, "Message received.");
        LinearLayout ll = (LinearLayout) findViewById(R.id.llayout);
        int width = (int) (400 * scale + 0.5f);
        int height = (int) (50 * scale + 0.5f);
        TextView tv = new TextView(this);
        tv.setWidth(width);
        tv.setHeight(height);
        tv.setText(label + msg.getText());
        ll.addView(tv);
    }

    public void displayUsers(){
        HashMap<String, String> userList = receiver.buddies;
        LinearLayout ll = (LinearLayout) findViewById(R.id.llayout);
        //ll.removeAllViews();
        for(String key: userList.keySet()){
            int width = (int) (400 * scale + 0.5f);
            int height = (int) (50 * scale + 0.5f);
            TextView tv = new TextView(this);
            tv.setWidth(width);
            tv.setHeight(height);
            tv.setText(key + userList.get(key));
            ll.addView(tv);
        }
    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this,cs.mPort);
        receiver.startRegistration();
        receiver.discoverService();
        receiver.initializePeerListener();
        registerReceiver(receiver, intentFilter);
        receiver.startDiscovery();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
