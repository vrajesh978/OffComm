package com.example.prasanna.offcom;

/**
 * Created by Vrajesh Mehta on 16/2/17.
 */

public class GlobalVariables {
    public static UserList userList;
    public static GroupList groupList;
    public static AllMessages allMessages;

    static {
        userList = new UserList();
        groupList = new GroupList();
        allMessages = new AllMessages(userList, groupList);
    }
    public static GroupList getGroupList(){
        return groupList;
    }
    public static UserList getUserList(){
        return userList;
    }
    public static AllMessages getAllMessages(){
        return allMessages;
    }
}
