package com.example.prasanna.offcom;

/**
 * Created by Vrajesh Mehta on 16/2/17.
 */

public class GlobalVariables {
    public static UserList userList;
    public static GroupList groupList;

    static {
        userList = new UserList();
        groupList = new GroupList();
    }
    public static GroupList getGroupList(){
        return groupList;
    }
    public static UserList getUserList(){
        return userList;
    }
}
