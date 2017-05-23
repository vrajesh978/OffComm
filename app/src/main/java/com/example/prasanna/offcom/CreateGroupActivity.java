package com.example.prasanna.offcom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    private UserList ul = GlobalVariables.getUserList();;
    private GroupList gl = GlobalVariables.getGroupList();
    private float scale;
    private List<UserInfo> groupUserList = new ArrayList<>();
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
    }

    public void onResume() {
        super.onResume();
        scale = this.getResources().getDisplayMetrics().density;

        List<UserInfo> userList = ul.getUsers();
        LinearLayout ll = (LinearLayout) findViewById(R.id.groupUsers);
        ll.removeAllViews();
        for (final UserInfo u : userList) {
            int width = (int) (400 * scale + 0.5f);
            int height = (int) (50 * scale + 0.5f);
            TextView tv = new TextView(this);
            tv.setWidth(width);
            tv.setHeight(height);
            tv.setText(u.userName);

            // Set on click.
            tv.setClickable(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addUserToGroup(u);
                }
            });

            ll.addView(tv);
        }
    }

    public void addUserToGroup(UserInfo u) {
        groupUserList.add(u);
    }

    public void setGroupName(View view) {
        EditText groupName = (EditText) findViewById(R.id.groupName);
        this.groupName = groupName.getText().toString();
    }

    public void createGroup(View view) {
        GroupInfo group = new GroupInfo(groupName);
        group.addUsersToGroup(this.groupUserList);
        gl.addGroup(group);

        final GroupInfo gi = group;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatClient cc = new ChatClient();
                cc.sendGroupCreationMessage(gi);
            }
        }).start();

        finishActivity(0);
    }
}
