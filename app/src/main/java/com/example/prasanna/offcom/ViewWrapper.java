package com.example.prasanna.offcom;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;


/**
 * Created by prasanna on 23/5/17.
 */

public class ViewWrapper {

    public static TextView createUserNameView(UserInfo u, Activity activity) {
        float scale = activity.getResources().getDisplayMetrics().density;
        TextView tv = new TextView(activity);
        int width = (int) (400 * scale + 0.5f);
        int height = (int) (50 * scale + 0.5f);
        tv.setWidth(width);
        tv.setHeight(height);
        tv.setText(u.userName + " " + u.ip + " " + u.port);
        return tv;
    }

    public static TextView createGroupNameView(GroupInfo g, Activity activity) {
        float scale = activity.getResources().getDisplayMetrics().density;
        TextView tv = new TextView(activity);
        int width = (int) (400 * scale + 0.5f);
        int height = (int) (50 * scale + 0.5f);
        tv.setWidth(width);
        tv.setHeight(height);
        tv.setText(g.getName());
        return tv;
    }

    public static TextView getMessageBox(Message msg, Activity activity) {
        float scale = activity.getResources().getDisplayMetrics().density;
        int width = (int) (400 * scale + 0.5f);
        int height = (int) (50 * scale + 0.5f);
        TextView tv = new TextView(activity);
        tv.setWidth(width);
        tv.setHeight(height);
        tv.setText(msg.getSender()+ ": " + msg.getText());
        return tv;
    }
}
