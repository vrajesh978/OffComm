package com.example.prasanna.offcom;

import android.util.Log;

import java.util.*;

public class GroupInfo {
	String groupName;
	List<UserInfo> groupParticipants;

    public GroupInfo(String name) {
        groupName = name;
        groupParticipants = new ArrayList<UserInfo>();
    }
    public void addUserToGroup(UserInfo u) {
        //Log.d("AdduserTOGruop","USERNAME = " + u.userName + " IS ADDED TO GROUP");
        groupParticipants.add(u);
    }

    public void addUsersToGroup(List<UserInfo> userList) {
        this.groupParticipants = userList;
    }

    public List<UserInfo> getGroupParticipant() {
        return groupParticipants;
    }

    public String getName() {
        return groupName;
    }
}