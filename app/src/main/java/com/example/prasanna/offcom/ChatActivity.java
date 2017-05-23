package com.example.prasanna.offcom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Intent intent;
    UserList ul;
    GroupList gl;
    String currentRecipient;
    String myUserName;
    AllMessages allMessages;
    int cursor;
    boolean isGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        intent = getIntent();
        ul = GlobalVariables.getUserList();
        gl = GlobalVariables.getGroupList();
        allMessages = GlobalVariables.getAllMessages();
        cursor = 0;

        // Show already received messages.
        this.checkExisitingMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
        scale = this.getResources().getDisplayMetrics().density;
        isGroup = intent.getBooleanExtra("isGroup", false);
        currentRecipient = intent.getStringExtra("recipient");

        myUserName = GlobalVariables.getMyUserName();
        URLHandler.setActivity(this);

        // Show name of current recipient.
        TextView name = (TextView) findViewById(R.id.userName);
        name.setText(currentRecipient);
    }

    public void onPause() {
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
        URLHandler.removeActivity();
        super.onStop();
    }

    public void displayMessage() {
        MessageList msgList;
        if (isGroup) {
            msgList = allMessages.getMessagesForGroup(currentRecipient);
        }
        else {
            msgList = allMessages.getMessagesForUser(currentRecipient);
        }

        LinearLayout ll = (LinearLayout) findViewById(R.id.messageList);
        for (Message msg: msgList.messageList.subList(cursor, msgList.messageList.size())) {
            TextView msgView = ViewWrapper.getMessageBox(msg, this);
            ll.addView(msgView);
        }
        cursor = msgList.messageList.size();
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
        this.displayMessage();
    }
}
