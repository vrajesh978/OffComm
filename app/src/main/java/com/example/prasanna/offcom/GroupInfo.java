package com.example.prasanna.offcom;

import java.util.*;

public class GroupInfo {
	String groupName;
	List<UserInfo> groupParticipants;

    public GroupInfo(String name) {
        groupName = name;
        groupParticipants = new ArrayList<UserInfo>();
    }
    public void addUserToGroup(UserInfo u) {
        groupParticipants.add(u);
    }

    public List<UserInfo> getGroupParticipant() {
        return groupParticipants;
    }

    public String getName() {
        return groupName;
    }
}