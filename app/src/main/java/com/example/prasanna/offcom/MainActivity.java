package com.example.prasanna.offcom;

import android.opengl.EGLDisplay;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Inet4Address inet;
    int port;
    float scale;
    AllMessages allMessages;
    ProtocolHandler protocol;
    UserList ul;
    GroupList gl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        protocol = new ProtocolHandler();
        ul = new UserList();
        gl = new GroupList();
        allMessages = new AllMessages(ul, gl);
        ChatServer cs = new ChatServer(5555, this, allMessages);

        cs.start();
        port = cs.mPort;
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
}
