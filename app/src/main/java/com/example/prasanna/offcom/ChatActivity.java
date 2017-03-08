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

import java.net.UnknownHostException;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class ChatActivity extends AppCompatActivity {

    float scale;
    Intent intent;
    UserList ul;
    GroupList gl;
    UserInfo currentUser;
    String myUserName;
    AllMessages allMessages;
    int cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        intent = getIntent();
        ul = GlobalVariables.getUserList();
        gl = GlobalVariables.getGroupList();
        allMessages = GlobalVariables.getAllMessages();
        cursor = 0;

    }

    @Override
    public void onResume() {
        super.onResume();
        scale = this.getResources().getDisplayMetrics().density;
        currentUser = ul.getUser(intent.getStringExtra("recipientUser"));
        myUserName = intent.getStringExtra("selfUser");
        URLHandler.setChatActivity(this);

        // Show username of current user.
        TextView name = (TextView) findViewById(R.id.userName);
        name.setText(currentUser.userName);
    }

    @Override
    public void onStop() {
        super.onStop();
        URLHandler.removeChatActivity();
    }

    public void displayMessage() {
        PersonalMessage pm = allMessages.getMessagesForUser(currentUser.userName);
        LinearLayout ll = (LinearLayout) findViewById(R.id.messageList);
        for (Message msg: pm.messageList.subList(cursor, pm.messageList.size())) {
            int width = (int) (400 * scale + 0.5f);
            int height = (int) (50 * scale + 0.5f);
            TextView tv = new TextView(this);
            tv.setWidth(width);
            tv.setHeight(height);
            tv.setText(msg.getSender()+ ": " + msg.getText());
            ll.addView(tv);
        }
        cursor = pm.messageList.size();

        // Scroll to bottom of messages.
        ScrollView scroll = (ScrollView) findViewById(R.id.chatScroll);
        scroll.fullScroll(View.FOCUS_DOWN);
    }

    public void sendMessage(View view) {
        ChatClient cc = new ChatClient();
        EditText ed = (EditText) findViewById(R.id.chatText);
        String text = ed.getText().toString();
        Message msg = new Message(text, currentUser.userName, myUserName, false, false);
        allMessages.addSentMessage(msg);
        HashMap<String, String> content = ProtocolHandler.message_wrapper(msg);

        try {
            cc.send_message(currentUser.ip, currentUser.port, content);
        } catch (UnknownHostException e) {
            Log.d(TAG, "No host found for " + currentUser.userName + " user.");
        }
        this.displayMessage();
    }
}
