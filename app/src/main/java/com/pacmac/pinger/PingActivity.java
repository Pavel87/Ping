package com.pacmac.pinger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.core.widget.NestedScrollView;

public class PingActivity extends AppCompatActivity implements PingListener, ExportListener, NetworkMonitor.NetworkMonitorCallback {

    private TextView phoneIP = null;
    private TextView routerIP = null;
    private TextView serverIP = null;
    private EditText ipEditText = null;
    private Button pingBtn = null;
    private TextView pingOutput = null;
    private NestedScrollView outputScrollView = null;
    private TextInputLayout pingTextLayout = null;
    private View exportBtn = null;
    private PopupWindow popupWindow = null;

    View routingView = null;
    View routerView = null;
    View serverView = null;
    ProgressBar progressLocalNet = null;
    ProgressBar progressInternet = null;

    NetworkMonitor networkMonitor = null;

    private int size = Constants.PING_SIZE_DEFAULT;
    private int count = Constants.PING_COUNT_DEFAULT;
    private int interval = Constants.PING_INTERVAL_DEFAULT;
    private int ttl = Constants.PING_TTL_DEFAULT;
    private int deadline = Constants.PING_DEADLINE_DEFAULT;
    private int timeout = Constants.PING_TIMEOUT_DEFAULT;
    private boolean isRoute = Constants.PING_ROUTE_DEFAULT;
    private boolean isTimestamp = Constants.PING_TIMESTAMPS_DEFAULT;
    private boolean useIPv6 = Constants.PING_IP_VERSION_DEFAULT;
    private String pingAddress = Constants.PING_ADDRESS_DEFAULT;

    private boolean isPingRunning = false;
    private boolean isAppPaused = false;

    private boolean shouldShowFullScreenAd = false;

    private String ipAddress = "UNKNOWN";
    private String gatewayIPAddress = "UNKNOWN";
    private String remoteIPAddress = "UNKNOWN";

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id_2));

        pingTextLayout = findViewById(R.id.pingTextLayout);
        ipEditText = findViewById(R.id.pingAddress);
        pingBtn = findViewById(R.id.pingBtn);
        exportBtn = findViewById(R.id.exportBtn);
        phoneIP = findViewById(R.id.phoneIP);
        routerIP = findViewById(R.id.routerIP);
        serverIP = findViewById(R.id.serverIP);

        routingView = findViewById(R.id.ipLocationView);
        routerView = findViewById(R.id.routerView);
        serverView = findViewById(R.id.serverView);
        progressLocalNet = findViewById(R.id.progressLocalNet);
        progressInternet = findViewById(R.id.progressInternet);

        pingOutput = findViewById(R.id.pingOutput);
        outputScrollView = findViewById(R.id.mScrollView);

        count = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_COUNT_DEFAULT, Constants.PING_COUNT_PREF);
        size = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_SIZE_DEFAULT, Constants.PING_SIZE_PREF);
        interval = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_INTERVAL_DEFAULT, Constants.PING_INTERVAL_PREF);
        ttl = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_TTL_DEFAULT, Constants.PING_TTL_PREF);
        deadline = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_DEADLINE_DEFAULT, Constants.PING_DEADLINE_PREF);
        timeout = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_TIMEOUT_DEFAULT, Constants.PING_TIMEOUT_PREF);
        isRoute = Utility.getBooleanFromPreference(getApplicationContext(), Constants.PING_ROUTE_DEFAULT, Constants.PING_ROUTE_PREF);
        isTimestamp = Utility.getBooleanFromPreference(getApplicationContext(), Constants.PING_TIMESTAMPS_DEFAULT, Constants.PING_TIMESTAMPS_PREF);
        useIPv6 = Utility.getBooleanFromPreference(getApplicationContext(), Constants.PING_IP_VERSION_DEFAULT, Constants.PING_IP_VERSION_PREF);

        ipEditText.clearFocus();

        pingAddress = Utility.getStringFromPreference(getApplicationContext(), Constants.PING_ADDRESS_DEFAULT, Constants.PING_ADDRESS_PREF);
        ipEditText.setText(pingAddress);
        ipEditText.setSelection(ipEditText.getText().length());

        pingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isPingRunning) {
                    Ping.cancelProcess();
                    pingBtn.setEnabled(false);
                } else {

                    String address = ipEditText.getText().toString().trim();
                    if (address.length() == 0) {
                        pingTextLayout.setError(getResources().getString(R.string.incorrect_address));
                        return;
                    }
                    boolean isURLValid = Utility.isURLValid(address);

                    if (!isURLValid) {
                        if (useIPv6) {
                            if (!Utility.isIPv6Valid(address)) {
                                pingTextLayout.setError(getResources().getString(R.string.incorrect_address2));
                                return;
                            }
                        } else {
                            if (!Utility.isIPv4Valid(address)) {
                                pingTextLayout.setError(getResources().getString(R.string.incorrect_address));
                                return;
                            }
                        }
                    }

                    pingTextLayout.setError("");

                    setLocalNetworkVisible(true);
                    setRoutingViewVisible(networkMonitor.isWIFIConnected());
//                    setLocalNetworkVisible(false);
                    setInternetNetworkVisible(false);

                    String routeSTR = "";
                    String timestampSTR = "";
                    String deadlineSTR = "";
                    String timeoutSTR = "";
                    String countSTR = "";
                    String sizeSTR = "-s " + Utility.getPacketSizeFromProgress(size);
                    if (count != 0) {
                        countSTR = "-c " + count;
                    }
                    if (deadline != 0) {
                        deadlineSTR = "-w " + deadline;
                    }
                    if (timeout != 0) {
                        timeoutSTR = "-W " + timeout;
                    }
                    if (isRoute && isTimestamp) {
                        isRoute = false;
                        isTimestamp = false;
                    } else if (isRoute) {
                        routeSTR = "-R";
                    } else if (isTimestamp) {
                        timestampSTR = "-T tsandaddr";
                    }

                    String intervalSTR = "-i " + String.format(Locale.ENGLISH, "%.1f", Utility.getIntervalFromProgress(interval));
                    String ttlSTR = "-t " + Utility.getTTLFromProgress(ttl);

                    String command = sizeSTR
                            + " " + countSTR
                            + " " + intervalSTR
                            + " " + ttlSTR
                            + " " + deadlineSTR
                            + " " + timeoutSTR
                            + " " + routeSTR
                            + " " + timestampSTR
                            + " " + address;

                    // Clear output window
                    pingOutput.setText("");
                    // Start ping
                    new AsyncPingTask(useIPv6, PingActivity.this).execute(command);
                    pingAddress = address;
                    Utility.setStringToPreference(getApplicationContext(), pingAddress, Constants.PING_ADDRESS_PREF);
                    isPingRunning = true;
                    pingBtn.setText(getResources().getString(R.string.cancel));
                    exportBtn.setVisibility(View.INVISIBLE);
                    pingOutput.setText(">> " + ((useIPv6) ? "ping6 " : "ping ") + command + "\n");
                }
            }
        });


        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isAppPaused && (popupWindow == null || !popupWindow.isShowing())) {
                    showPopup(exportBtn);
                }
            }
        });

        networkMonitor = new NetworkMonitor(this);
    }


    public void showPopup(View v) {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.export_item, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.RIGHT, v.getWidth() / 2, 3 * v.getHeight());
        } else {
            popupWindow.showAtLocation(v, Gravity.BOTTOM | Gravity.RIGHT, v.getWidth() / 2, 1 * v.getHeight());
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });

        View share = popupView.findViewById(R.id.shareOption);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                shouldShowFullScreenAd = true;
                Utility.sendShareIntent(PingActivity.this, pingAddress, pingOutput.getText().toString());
            }
        });

        View export = popupView.findViewById(R.id.exportOption);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isPermissionEnabled = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        PermissionChecker.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    isPermissionEnabled = false;
                    Utility.displayExplanationForPermission(PingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                }
                if (isPermissionEnabled) {
                    // EXPORT TO SD
                    SaveToSDTask saveTask = new SaveToSDTask(PingActivity.this);
                    String[] pingExport = {pingOutput.getText().toString(), pingAddress};
                    saveTask.execute(pingExport);
                }
            }
        });

        popupView.setVisibility(View.VISIBLE);
        popupView.setAlpha(0.0f);
        popupWindow.showAsDropDown(v);

        // Start the animation
        popupView.animate()
                .alpha(1.0f)
                .setListener(null);

    }

    @Override
    public void onExportComplete(boolean success) {
        popupWindow.dismiss();

        int length = Snackbar.LENGTH_LONG;
        String message = String.format(Locale.ENGLISH, "%s%s.txt", getResources().getString(R.string.exported_to), pingAddress);
        if (!success) {
            length = Snackbar.LENGTH_SHORT;
            message = getResources().getString(R.string.exported_failed);
        }
        Snackbar.make(findViewById(android.R.id.content), message, length)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PING_SETTINGS_RC) {

            if (data == null) {
                return;
            }
            //TODO add NPE handler
            count = data.getIntExtra(Constants.PING_COUNT_PREF, Constants.PING_COUNT_DEFAULT);
            size = data.getIntExtra(Constants.PING_SIZE_PREF, Constants.PING_SIZE_DEFAULT);
            interval = data.getIntExtra(Constants.PING_INTERVAL_PREF, Constants.PING_INTERVAL_DEFAULT);
            ttl = data.getIntExtra(Constants.PING_TTL_PREF, Constants.PING_TTL_DEFAULT);
            deadline = data.getIntExtra(Constants.PING_DEADLINE_PREF, Constants.PING_DEADLINE_DEFAULT);
            timeout = data.getIntExtra(Constants.PING_TIMEOUT_PREF, Constants.PING_TIMEOUT_DEFAULT);
            isRoute = data.getBooleanExtra(Constants.PING_ROUTE_PREF, Constants.PING_ROUTE_DEFAULT);
            isTimestamp = data.getBooleanExtra(Constants.PING_TIMESTAMPS_PREF, Constants.PING_TIMESTAMPS_DEFAULT);
            useIPv6 = data.getBooleanExtra(Constants.PING_IP_VERSION_PREF, Constants.PING_IP_VERSION_DEFAULT);
        }
    }

    @Override
    public void onComplete(final String result) {
        isPingRunning = false;
        pingBtn.setText("PING");
        pingBtn.setEnabled(true);
        if (pingOutput.getText().length() > 0) {
            exportBtn.setVisibility(View.VISIBLE);
        }
        progressLocalNet.setIndeterminate(false);
        progressInternet.setIndeterminate(false);
    }

    @Override
    public void onStep(String line) {
        pingOutput.append(line);
        outputScrollView.post(new Runnable() {
            @Override
            public void run() {
                outputScrollView.smoothScrollTo(0, pingOutput.getBottom());
            }
        });
        if (networkMonitor.isWIFIConnected()) {
            getIPAddress(line);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsPingActivity.class);
            settingsIntent.putExtra(Constants.PING_COUNT_PREF, count);
            settingsIntent.putExtra(Constants.PING_SIZE_PREF, size);
            settingsIntent.putExtra(Constants.PING_INTERVAL_PREF, interval);
            settingsIntent.putExtra(Constants.PING_TTL_PREF, ttl);
            settingsIntent.putExtra(Constants.PING_DEADLINE_PREF, deadline);
            settingsIntent.putExtra(Constants.PING_TIMEOUT_PREF, timeout);
            settingsIntent.putExtra(Constants.PING_ROUTE_PREF, isRoute);
            settingsIntent.putExtra(Constants.PING_TIMESTAMPS_PREF, isTimestamp);
            settingsIntent.putExtra(Constants.PING_IP_VERSION_PREF, useIPv6);
            startActivityForResult(settingsIntent, Constants.PING_SETTINGS_RC);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ping_activity, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        pingBtn.setEnabled(true);
        isAppPaused = false;
        networkMonitor.registerConnectivityReceiver(getApplicationContext());
        onWiFiConnectionChanged(networkMonitor.isWiFiConnected(getApplicationContext()));

        if (shouldShowFullScreenAd) {
            shouldShowFullScreenAd = false;
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ping.cancelProcess();
        networkMonitor.unregisterConnectivityReceiver(getApplicationContext());
        isAppPaused = true;
        try {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (requestCode == Constants.PING_WRITE_EXT_STORAGE_RC) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.exported_permitted), Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.RED)
                        .show();
            }
        }
    }

    public void setRoutingViewVisible(boolean visible) {
        if (visible) {
            routingView.setVisibility(View.VISIBLE);
            getWiFiIPAddress(getApplicationContext());
        } else {
            routingView.setVisibility(View.GONE);
        }
    }

    public void getWiFiIPAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInformation = wifiManager.getDhcpInfo();
        ipAddress = intToInetAddress(dhcpInformation.ipAddress).getHostAddress();
        gatewayIPAddress = intToInetAddress(dhcpInformation.gateway).getHostAddress();
    }

    public static InetAddress intToInetAddress(int hostAddress) {
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            return InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void onWiFiConnectionChanged(boolean isConnected) {
        setRoutingViewVisible(isConnected);
        if (isConnected) {
            phoneIP.setText(ipAddress);
            routerIP.setText(gatewayIPAddress);
        }
    }

    public void setLocalNetworkVisible(boolean visible) {
        if (visible) {
            if (networkMonitor.isWIFIConnected()) {
                progressLocalNet.setVisibility(View.VISIBLE);
                progressLocalNet.setIndeterminate(true);
                routerView.setVisibility(View.VISIBLE);
            }
        } else {
            progressLocalNet.setVisibility(View.GONE);
            routerView.setVisibility(View.GONE);
            progressLocalNet.setIndeterminate(false);
        }
    }

    public void setInternetNetworkVisible(boolean visible) {
        if (visible) {
            if (networkMonitor.isWIFIConnected()) {
                progressInternet.setVisibility(View.VISIBLE);
                progressInternet.setIndeterminate(true);
                serverView.setVisibility(View.VISIBLE);
            }
        } else {
            progressInternet.setVisibility(View.GONE);
            progressInternet.setIndeterminate(false);
            serverView.setVisibility(View.GONE);
        }
    }


    private void getIPAddress(String rawPing) {
        if (rawPing != null) {
            try {
                final String regExp = "[^(]*\\(([^)]*)\\)";
                Pattern patterns = Pattern.compile(regExp);
                Matcher m = patterns.matcher(rawPing);
                if (m.find()) {
                    remoteIPAddress = m.group(1);
                    serverIP.setText(remoteIPAddress);
                    if (!remoteIPAddress.equals(gatewayIPAddress)) {
                        setInternetNetworkVisible(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
