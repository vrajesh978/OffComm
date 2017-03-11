package com.example.prasanna.offcom;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Inet4Address localIp;
    float scale;
    int port;
    String myUserName;
    boolean isUserNameSet;

    AllMessages allMessages;
    ProtocolHandler protocol;

    UserList ul;
    GroupList gl;

    ChatServer cs;

    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    NSDManager mNSDManager;

    WiFiDirectBroadcastReceiver receiver;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        protocol = new ProtocolHandler();
        ul = GlobalVariables.getUserList();
        gl = GlobalVariables.getGroupList();
        allMessages = GlobalVariables.getAllMessages();
        cs = new ChatServer(allMessages);

        /* WiFI P2p peer discrovery.
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);*/

        cs.start();
        isUserNameSet = false;
    }

    public Inet4Address get_ip_address() {
        Inet4Address ip = null;

        try {
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            byte[] bytes = ByteBuffer.allocate(4).putInt(wm.getConnectionInfo().getIpAddress()).array();
            byte temp;

            // Reverse bytes.
            temp = bytes[0];
            bytes[0] = bytes[3];
            bytes[3] = temp;

            temp = bytes[1];
            bytes[1] = bytes[2];
            bytes[2] = temp;

            ip = (Inet4Address) Inet4Address.getByAddress(bytes);
        } catch (Exception e) {
        }

        return ip;
    }

    public void displayUsers() {
        List<UserInfo> userList = ul.getUsers();
        LinearLayout ll = (LinearLayout) findViewById(R.id.llayout);
        ll.removeAllViews();
        for (final UserInfo u : userList) {
            int width = (int) (400 * scale + 0.5f);
            int height = (int) (50 * scale + 0.5f);
            TextView tv = new TextView(this);
            tv.setWidth(width);
            tv.setHeight(height);
            tv.setText(u.userName + " " + u.ip + " " + u.port);

            // Set on click.
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChat(u);
                }
            });

            ll.addView(tv);
        }
    }

    public void goToChat(UserInfo user) {
        if (isUserNameSet) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("recipientUser", user.userName);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setUserName(View view) {
        EditText userNameBox = (EditText) findViewById(R.id.myUserName);
        myUserName = userNameBox.getText().toString();
        // Set username.
        isUserNameSet = true;
        GlobalVariables.setMyUserName(myUserName);

        // start peer service discovery.
        /* WiFI P2p peer discovery.
        receiver = null;
        receiver = new WiFiDirectBroadcastReceiver(
                mManager, mChannel, this, localIp, cs.mPort, myUserName);
        receiver.startRegistration();
        receiver.initializeDiscoverService();
        receiver.startDiscovery();*/

        // NSDManager.
        if (mNSDManager != null) {
            mNSDManager.stopDiscovery();
            mNSDManager.tearDown();
            mNSDManager = null;
        }
        mNSDManager= new NSDManager(this, myUserName);
        mNSDManager.initializeNsd();
        mNSDManager.registerService(cs.mPort);
        mNSDManager.discoverServices();
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResume() {
        super.onResume();
        localIp = get_ip_address();
        scale = this.getResources().getDisplayMetrics().density;
        port = cs.mPort;
        if (isUserNameSet) {
            /* WiFi P2p peer discovery
            receiver = new WiFiDirectBroadcastReceiver(
                    mManager, mChannel, this, localIp, cs.mPort, myUserName);
            receiver.startRegistration();
            receiver.initializeDiscoverService();
            receiver.startDiscovery();*/

            // NSDManager.
            mNSDManager= new NSDManager(this, myUserName);
            mNSDManager.initializeNsd();
            mNSDManager.registerService(cs.mPort);
            mNSDManager.discoverServices();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPause() {
        /* WiFi P2p peer discovery
        receiver.stopDiscovery();
        receiver = null;*/
        if (mNSDManager != null) {
            mNSDManager.stopDiscovery();
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroy() {
        mNSDManager.tearDown();
        mNSDManager = null;
        /* WiFi P2p peer discovery.
        receiver.stopDiscovery();
        receiver = null;*/
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStop() {
        if (mNSDManager != null) {
            mNSDManager.tearDown();
            mNSDManager = null;
        }
        /* WiFi P2p peer discovery.
        receiver.stopDiscovery();
        receiver = null;*/
        super.onStop();
    }
}
