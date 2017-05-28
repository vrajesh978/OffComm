package com.example.prasanna.offcom;


import android.content.Intent;
import android.net.wifi.WifiManager;

import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.math.BigInteger;
import java.net.Inet4Address;
import java.util.List;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {
    private float scale;
    static int port;
    private static String myUserName;
    private boolean isUserNameSet;
    private static MainActivity mainActivity;

    private static Inet4Address localIp;

    NSDManager mNSDManager;

    private AllMessages allMessages;
    private UserList ul = GlobalVariables.getUserList();
    private GroupList gl = GlobalVariables.getGroupList();
    private ChatServer cs;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //URLHandler.setActivity(this);
        mainActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        allMessages = GlobalVariables.getAllMessages();
        cs = new ChatServer(allMessages);
        cs.start();
        isUserNameSet = false;

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResume() {
        //Log.d(TAG,"MAINACTIVITY ON-RESUME METHOD CALLED");
        URLHandler.setActivity(this);
        super.onResume();
        localIp = get_ip_address();
        port = cs.mPort;
        if (isUserNameSet) {
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
        //Log.d(TAG,"MAINACTIVITY ON-PAUSE METHOD CALLED");
        URLHandler.removeActivity();
        if (mNSDManager != null) {
            mNSDManager.stopDiscovery();
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onDestroy() {
        //Log.d(TAG,"MAINACTIVITYY ON-DESTROY METHOD CALLED");
        URLHandler.removeActivity();
        if (mNSDManager != null) {
            mNSDManager.tearDown();
            mNSDManager = null;
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onStop() {
        //Log.d(TAG,"MAINACTIVITY ON-STOP METHOD CALLED");
       // URLHandler.removeActivity();
        if (mNSDManager != null) {
            mNSDManager.tearDown();
            mNSDManager = null;
        }
        super.onStop();
    }

    public Inet4Address get_ip_address() {
        Inet4Address ip = null;

        try {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            byte[] bytes = BigInteger.valueOf(wm.getConnectionInfo().getIpAddress()).toByteArray();
            byte temp;

            // Reverse bytes.
            temp = bytes[0]; bytes[0] = bytes[3]; bytes[3] = temp;
            temp = bytes[1]; bytes[1] = bytes[2]; bytes[2] = temp;

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
            TextView userNameView = ViewWrapper.createUserNameView(u, this);

            // Set on click.
            userNameView.setClickable(true);
            userNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChat(u);
                }
            });

            ll.addView(userNameView);
        }

        List<GroupInfo> groupList = gl.getGroups();
        for (final GroupInfo g : groupList) {
            TextView groupNameView = ViewWrapper.createGroupNameView(g, this);

            // Set on click.
            groupNameView.setClickable(true);
            groupNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToGroupChat(g);
                }
            });

            ll.addView(groupNameView);
        }
    }

    public void goToChat(UserInfo user) {
        if (isUserNameSet) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("recipient", user.userName);
            intent.putExtra("isGroup", false);
            startActivity(intent);
        }
    }

    public void goToGroupChat(GroupInfo g) {
        if(isUserNameSet) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("recipient", g.getName());
            intent.putExtra("isGroup", true);
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

    public void createGroupEntry(View view) {
        Intent intent = new Intent(this, CreateGroupActivity.class);
        startActivity(intent);
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    public static String getMyDetails(){
        return myUserName + " " +localIp +" "+ port + ",";
    }

    public static String getMyUserName(){
        return myUserName;
    }

}
