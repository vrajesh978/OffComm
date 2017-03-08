package com.example.prasanna.offcom;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

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

    WiFiDirectBroadcastReceiver receiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        cs.start();
        isUserNameSet = false;
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
            intent.putExtra("selfUser", myUserName);
            startActivity(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setUserName(View view) {
        EditText userNameBox = (EditText) findViewById(R.id.myUserName);
        myUserName = userNameBox.getText().toString();
        isUserNameSet = true;
        receiver = null;
        receiver = new WiFiDirectBroadcastReceiver(
                mManager, mChannel, this, localIp, cs.mPort, myUserName);
        receiver.startRegistration();
        receiver.initializeDiscoverService();
        receiver.startDiscovery();
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
            receiver = new WiFiDirectBroadcastReceiver(
                    mManager, mChannel, this, localIp, cs.mPort, myUserName);
            receiver.startRegistration();
            receiver.initializeDiscoverService();
            receiver.startDiscovery();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        receiver = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        receiver = null;
    }
}
