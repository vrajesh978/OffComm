package com.example.prasanna.offcom;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class NSDManager {

    Context mContext;

    NsdManager mNsdManager;
    NsdManager.DiscoveryListener mDiscoveryListener;
    NsdManager.RegistrationListener mRegistrationListener;

    public static final String SERVICE_TYPE = "_offcom._tcp.";

    public String mServiceName = "offcom_";

    NsdServiceInfo mService;
    UserList ul;

    public NSDManager(Context context, String serviceName) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        mServiceName += serviceName;
        ul = GlobalVariables.getUserList();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initializeNsd() {
        initializeDiscoveryListener();
        initializeRegistrationListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initializeDiscoveryListener() {
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d(TAG, "Service discovery success " + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same machine: " + mServiceName);
                } else if (service.getServiceName().contains("offcom")){
                    Log.d(TAG, "Resolving service.");
                    mNsdManager.resolveService(service, getResolveListener());
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e(TAG, "service lost" + service);
                if (mService == service) {
                    mService = null;
                }
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public NsdManager.ResolveListener getResolveListener() {
        NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(TAG, "Same IP.");
                    return;
                }
                mService = serviceInfo;
                String ip = serviceInfo.getHost().toString().substring(1);
                int port = serviceInfo.getPort();
                String username = serviceInfo.getServiceName().substring(7);
                UserInfo u = new UserInfo(ip, port, username);
                ul.addUser(u);
                sendToActivity();
            }

            public void sendToActivity() {
                final MainActivity activity = (MainActivity) mContext;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.displayUsers();
                    }
                });
            }
        };
        return mResolveListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                mServiceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                Log.d(TAG, "Unregistration successful");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Cannot unregister the service.");
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void registerService(int port) {
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setPort(port);
        serviceInfo.setServiceName(mServiceName);
        serviceInfo.setServiceType(SERVICE_TYPE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void discoverServices() {
        mNsdManager.discoverServices(
                SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopDiscovery() {
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
    }
}

