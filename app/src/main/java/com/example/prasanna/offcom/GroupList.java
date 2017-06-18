package com.example.prasanna.offcom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by prasanna on 7/1/17.
 */

public class GroupList {
    HashMap<String, GroupInfo> groupList;

    public GroupList() {
        groupList = new HashMap<>();
    }

    public void addGroup(GroupInfo g) {
        groupList.put(g.getName(), g);
    }

    public GroupInfo getGroup(String name) {
        return groupList.get(name);
    }

    public List<GroupInfo> getGroups() {
        return (List<GroupInfo>) new CopyOnWriteArrayList<GroupInfo>(groupList.values());
    }

    public void addToGroup(String name, UserInfo u) {
        groupList.get(name).addUserToGroup(u);
    }
}
