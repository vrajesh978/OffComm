package com.example.prasanna.offcom;

/**
 * Created by Vrajesh Mehta on 16/2/17.
 */

public class GlobalVariables {
    public static UserList userList;
    public static GroupList groupList;
    public static AllMessages allMessages;
    public static String myUserName;

    static {
        userList = new UserList();
        groupList = new GroupList();
        allMessages = new AllMessages(userList, groupList);
        myUserName = null;
    }
    public static GroupList getGroupList(){
        return groupList;
    }
    public static UserList getUserList(){
        return userList;
    }
    public static AllMessages getAllMessages() {
        return allMessages;
    }
    public static String getMyUserName() {
        return myUserName;
    }
    public static void setMyUserName(String name) {
        myUserName = name;
    }
}
