package com.pacmac.pinger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

/**
 * Created by pacmac on 2019-02-02.
 */

public class NetworkMonitor {

    interface NetworkMonitorCallback {
        void onWiFiConnectionChanged(boolean isConnected);
    }


    private NetworkMonitorCallback listener = null;
    private boolean isConnectivityReceiverRegistred = false;
    private Handler handler = new Handler();
    private Runnable runnable = null;
    private boolean isWIFIConnected = false;


    public NetworkMonitor(NetworkMonitorCallback listener) {
        this.listener = listener;
    }

    public void registerConnectivityReceiver(Context context) {
        if (!isConnectivityReceiverRegistred) {
            IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(connectivityReceiver, intentFilter);
            isConnectivityReceiverRegistred = true;
        }
    }

    public void unregisterConnectivityReceiver(Context context) {
        if (isConnectivityReceiverRegistred) {
            context.unregisterReceiver(connectivityReceiver);
            isConnectivityReceiverRegistred = false;
        }
    }

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {

            isWIFIConnected = isWiFiConnected(context);

            if (isWIFIConnected) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                        public void run() {
                        if (listener != null) {
                            listener.onWiFiConnectionChanged(isWiFiConnected(context));
                        }
                    }
                };
                handler.postDelayed(runnable, 2000);


            }

        }
    };


    public boolean isWiFiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null) return false;
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
            return true;
        } else return false;
    }

    public boolean isWIFIConnected() {
        return isWIFIConnected;
    }
}
