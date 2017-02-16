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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * Created by Vrajesh Mehta on 16/2/17.
 */

public class WiFiDirectBroadcastReceiver extends android.content.BroadcastReceiver {
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    MainActivity activity;
    UserList ul;

    private WifiP2pManager.PeerListListener peerListListener;
    public List<WifiP2pDevice> peers;

    HashMap<String, String> buddies;
    int SERVER_PORT = 0;

    public WiFiDirectBroadcastReceiver(
            WifiP2pManager manager, WifiP2pManager.Channel channel, Context context,int port) {
        mManager = manager;
        mChannel = channel;
        activity = (MainActivity) context;
        ul = GlobalVariables.getUserList();
        buddies = new HashMap<>();
        SERVER_PORT = port;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                //activity.setIsWifiP2pEnabled(true);
            } else {
                //activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            /*DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));*/

        }
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
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "Vrajesh" + (int) (Math.random() * 1000));
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
    public void discoverService() {
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
                buddies.put(device.deviceAddress, (String) record.get("buddyname"));
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = buddies
                        .containsKey(resourceType.deviceAddress) ? buddies
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
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initializePeerListener() {
        peers = new ArrayList<WifiP2pDevice>();

        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {

                List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peerList.getDeviceList());

                if (!refreshedPeers.equals(peers)) {
                    peers.clear();
                    peers.addAll(refreshedPeers);
                    // If an AdapterView is backed by this data, notify it
                    // of the change.  For instance, if you have a ListView of
                    // available peers, trigger an update.
                    //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();

                    // Perform any other updates needed based on the new list of
                    // peers connected to the Wi-Fi P2P network.

                }

                if (peers.size() == 0) {
                    Log.d(TAG, "No devices found");
                    return;
                }
            }
        };
    }

}