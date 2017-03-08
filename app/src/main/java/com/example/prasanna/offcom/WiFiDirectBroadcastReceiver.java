package com.example.prasanna.offcom;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * Created by Vrajesh Mehta on 16/2/17.
 */

public class WiFiDirectBroadcastReceiver {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    MainActivity activity;
    UserList ul;

    HashMap<String, String> macMapping;

    int MY_PORT;
    Inet4Address MY_IP;
    String userName;

    public WiFiDirectBroadcastReceiver(
            WifiP2pManager manager, WifiP2pManager.Channel channel,
            Context context, Inet4Address ip, int port, String name) {
        mManager = manager;
        mChannel = channel;
        activity = (MainActivity) context;
        ul = GlobalVariables.getUserList();
        macMapping = new HashMap<>();
        MY_PORT = port;
        MY_IP = ip;
        userName = name;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startDiscovery() {
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Success!
                Log.d(TAG,"Discovery start");
            }

            @Override
            public void onFailure(int code) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                if (code == WifiP2pManager.P2P_UNSUPPORTED) {
                    Log.d(TAG, "P2P isn't supported on this device.");
                }
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("buddyname", userName);
        record.put("listenport", String.valueOf(MY_PORT));
        record.put("listenip", MY_IP.getHostAddress());
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("offcom", "_offcom._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Log.d(TAG,"Registration successful");
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG,"Registration unsuccessful");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initializeDiscoverService() {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */

            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map record, WifiP2pDevice device) {
                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
                // Put name in buddies list.
                macMapping.put(device.deviceAddress, (String) record.get("buddyname"));

                // Add user to global UserList.
                final int port = Integer.parseInt((String) record.get("listenport"));
                final String ip_string = (String) record.get("listenip");
                final String userName = (String) record.get("buddyname");
                UserInfo u = new UserInfo(ip_string, port, userName);
                ul.addUser(u);
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = macMapping
                        .containsKey(resourceType.deviceAddress) ? macMapping
                        .get(resourceType.deviceAddress) : resourceType.deviceName;

                // Add to the custom adapter defined specifically for showing
                // wifi devices.
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.displayUsers();
                    }
                });
                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        WifiP2pDnsSdServiceRequest serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        // Success!
                        Log.d(TAG,"service request success");
                    }

                    @Override
                    public void onFailure(int code) {
                        // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                        Log.d(TAG,"Failed service request");
                    }
                }
        );
    }
}
