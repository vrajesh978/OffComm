package com.example.prasanna.offcom;

import java.net.Inet4Address;

/**
 * Created by prasanna on 7/1/17.
 */

public class UserInfo {
    Inet4Address ip;
    int port;
    String userName;

    public UserInfo(Inet4Address ip, int port, String userName) {
        this.ip = ip;
        this.port = port;
        this.userName = userName;
    }
}
