package com.example.prasanna.offcom;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.net.UnknownHostException;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ChatActivity extends AppCompatActivity {

    float scale;
    private Intent intent;
    private UserList ul;
    private GroupList gl;
    private String currentRecipient;
    private String myUserName;
    private AllMessages allMessages;
    private int cursor;
    private boolean isGroup;
    private  static ChatActivity chatActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatActivity = this;
        setContentView(R.layout.activity_chat);
        Log.d(TAG,"CHATACTIVITY ON-CREATE METHOD CALLED");
        //URLHandler.setActivity(this);
        intent = getIntent();
        ul = GlobalVariables.getUserList();
        gl = GlobalVariables.getGroupList();
        allMessages = GlobalVariables.getAllMessages();
        cursor = 0;
    }



    @Override
    public void onResume() {
        //Log.d(TAG,"CHATACTIVITY ON-RESUME METHOD CALLED");
        URLHandler.setActivity(this);
        super.onResume();

        //this.checkExisitingMessages();
        scale = this.getResources().getDisplayMetrics().density;
        isGroup = intent.getBooleanExtra("isGroup", false);
        currentRecipient = intent.getStringExtra("recipient");

        myUserName = GlobalVariables.getMyUserName();


        // Show name of current recipient.
        TextView name = (TextView) findViewById(R.id.userName);
        name.setText(currentRecipient);

        try {
            displayMessageFromStart();
        }catch (NullPointerException e){}
    }

    public void onPause() {
        //Log.d(TAG,"CHATACTIVITY ON-PAUSE METHOD CALLED");
        URLHandler.removeActivity();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        URLHandler.removeActivity();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        //Log.d(TAG,"CHATACTIVITY ON-CREATE METHOD CALLED");
        //URLHandler.removeActivity();
        super.onStop();
    }

    public void displayMessage() {
        MessageList msgList;

        if (isGroup) {
            msgList = allMessages.getMessagesForGroup(currentRecipient);
            //Log.d(TAG,"messages size = " + msgList.messageList.size());
        }
        else {
            msgList = allMessages.getMessagesForUser(currentRecipient);
           // Log.d(TAG,"messages size = " + msgList.getMessageList().size());
        }
        cursor = msgList.getMessageList().size();
        LinearLayout ll = (LinearLayout) findViewById(R.id.messageList);
        for (Message msg: msgList.getMessageList().subList(cursor-1, msgList.getMessageList().size())) {
            TextView msgView = ViewWrapper.getMessageBox(msg, this);
            ll.addView(msgView);
        }

        // Scroll to bottom of messages.
        ScrollView scroll = (ScrollView) findViewById(R.id.chatScroll);
        scroll.fullScroll(View.FOCUS_DOWN);
    }

    public void checkExisitingMessages() {
        try {
            // TODO: Optimize this code.
            MessageList msgList;
            if (isGroup) {
                msgList = allMessages.getMessagesForGroup(currentRecipient);
            }
            else {
                msgList = allMessages.getMessagesForUser(currentRecipient);
            }
            if (msgList != null) {
                this.displayMessage();
            }
        } catch (NullPointerException e) {
            // No existing messages are available.
        }
    }

    /*public Activity getActivity(){
        return this;
    }*/
    public void sendMessage(View view) {
        EditText ed = (EditText) findViewById(R.id.chatText);
        String text = ed.getText().toString();

        Message msg;
        if (isGroup)
            msg = new Message(text, currentRecipient, myUserName, false, true);
        else
            msg = new Message(text, currentRecipient, myUserName, false, false);

        ChatClient cc = new ChatClient();
        allMessages.addSentMessage(msg);

        try {
            cc.sendMessage(msg);
        } catch (UnknownHostException e) {
            Log.d(TAG, "No host found for " + currentRecipient);
        }
        //Log.d(TAG,"u r about to call chatActivity's displayMessage method");
        this.displayMessage();
    }


    private void displayMessageFromStart() {
        int cur = 0;
        MessageList msgList;
        if (isGroup) {
            msgList = allMessages.getMessagesForGroup(currentRecipient);
            Log.d(TAG,"messages size = " + msgList.messageList.size());
        }
        else {
            msgList = allMessages.getMessagesForUser(currentRecipient);
            Log.d(TAG,"messages size = " + msgList.getMessageList().size());
        }

        LinearLayout ll = (LinearLayout) findViewById(R.id.messageList);
        for (Message msg: msgList.getMessageList().subList(cur, msgList.getMessageList().size())) {
            TextView msgView = ViewWrapper.getMessageBox(msg, this);
            ll.addView(msgView);
        }
        cur = msgList.getMessageList().size();
        // Scroll to bottom of messages.
        ScrollView scroll = (ScrollView) findViewById(R.id.chatScroll);
        scroll.fullScroll(View.FOCUS_DOWN);
    }

    //get the instance of ChatActivity
    public static ChatActivity getInstance(){
        return  chatActivity;
    }
}


