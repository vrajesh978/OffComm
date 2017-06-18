package com.example.prasanna.offcom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by prasanna on 7/1/17.
 */

public class UserList {
    HashMap<String, UserInfo> userList;

    public UserList() {
        userList = new HashMap<>();
    }

    public void addUser(UserInfo u) {
        userList.put(u.userName, u);
    }

    public UserInfo getUser(String name) {
        return userList.get(name);
    }

    public List<UserInfo> getUsers() {
        return new CopyOnWriteArrayList<UserInfo> (userList.values());
    }
}
