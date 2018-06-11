package com.pacmac.pinger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

public class PingActivity extends AppCompatActivity implements PingListener, ExportListener {

    private EditText ipEditText = null;
    private Button pingBtn = null;
    private TextView pingOutput = null;
    private ScrollView outputScrollView = null;
    private TextInputLayout pingTextLayout = null;
    private View exportBtn = null;
    private PopupWindow popupWindow = null;

    private int size = Constants.PING_SIZE_DEFAULT;
    private int count = Constants.PING_COUNT_DEFAULT;
    private int interval = Constants.PING_INTERVAL_DEFAULT;
    private int ttl = Constants.PING_TTL_DEFAULT;
    private int deadline = Constants.PING_DEADLINE_DEFAULT;
    private boolean isRoute = Constants.PING_ROUTE_DEFAULT;
    private boolean isTimestamp = Constants.PING_TIMESTAMPS_DEFAULT;
    private String pingAddress = Constants.PING_ADDRESS_DEFAULT;

    private boolean isPingRunning = false;
    private boolean isAppPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        pingTextLayout = findViewById(R.id.pingTextLayout);
        ipEditText = findViewById(R.id.pingAddress);
        pingBtn = findViewById(R.id.pingBtn);
        exportBtn = findViewById(R.id.exportBtn);

        pingOutput = findViewById(R.id.pingOutput);
        outputScrollView = findViewById(R.id.mScrollView);

        count = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_COUNT_DEFAULT, Constants.PING_COUNT_PREF);
        size = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_SIZE_DEFAULT, Constants.PING_SIZE_PREF);
        interval = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_INTERVAL_DEFAULT, Constants.PING_INTERVAL_PREF);
        ttl = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_TTL_DEFAULT, Constants.PING_TTL_PREF);
        deadline = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_DEADLINE_DEFAULT, Constants.PING_DEADLINE_PREF);
        isRoute = Utility.getBooleanFromPreference(getApplicationContext(), Constants.PING_ROUTE_DEFAULT, Constants.PING_ROUTE_PREF);
        isTimestamp = Utility.getBooleanFromPreference(getApplicationContext(), Constants.PING_TIMESTAMPS_DEFAULT, Constants.PING_TIMESTAMPS_PREF);

        ipEditText.setSelection(ipEditText.getText().length());
        ipEditText.clearFocus();

        pingAddress = Utility.getStringFromPreference(getApplicationContext(), Constants.PING_ADDRESS_DEFAULT, Constants.PING_ADDRESS_PREF);
        ipEditText.setText(pingAddress);

        pingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPingRunning) {
                    Ping.cancelProcess();
                    pingBtn.setEnabled(false);
                } else {

                    String address = ipEditText.getText().toString().trim();
                    boolean isValid = Utility.isAddressValid(address);

                    if (!isValid) {
                        pingTextLayout.setError("Incorrect Address");
                        return;
                    } else {
                        pingTextLayout.setError("");
                    }

                    String routeSTR = "";
                    String timestampSTR = "";
                    String deadlineSTR = "";
                    String countSTR = "";
                    String sizeSTR = "-s " + String.valueOf(Utility.getPacketSizeFromProgress(size));
                    if (count != 0) {
                        countSTR = "-c " + String.valueOf(count);
                    }
                    if (deadline != 0) {
                        deadlineSTR = "-w " + String.valueOf(deadline);
                    }
                    if (isRoute && isTimestamp) {
                        isRoute = false;
                        isTimestamp = false;
                    } else if (isRoute) {
                        routeSTR = "-R";
                    } else if (isTimestamp) {
                        timestampSTR = "-T tsandaddr";
                    }

                    String intervalSTR = "-i " + String.format("%.1f", Utility.getIntervalFromProgress(interval));
                    String ttlSTR = "-t " + String.valueOf(Utility.getTTLFromProgress(ttl));

                    String command = sizeSTR
                            + " " + countSTR
                            + " " + intervalSTR
                            + " " + ttlSTR
                            + " " + deadlineSTR
                            + " " + routeSTR
                            + " " + timestampSTR
                            + " " + address;

                    // Clear output window
                    pingOutput.setText("");
                    // Start ping
                    new AsyncPingTask(PingActivity.this).execute(command);
                    pingAddress = address;
                    Utility.setStringToPreference(getApplicationContext(), pingAddress, Constants.PING_ADDRESS_PREF);
                    isPingRunning = true;
                    pingBtn.setText("CANCEL");
                    exportBtn.setVisibility(View.INVISIBLE);
                    pingOutput.setText(">> ping " + command + "\n");
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

    }

    // display the popup[![enter image description here][1]][1]
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//        mPopupWindow.showAsDropDown(v, 0, 0, Gravity.END);
//    } else {
//        mPopupWindow.showAsDropDown(v, v.getWidth() - mPopupWindow.getWidth(), 0);
//    }

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
                Utility.shareResult(PingActivity.this, pingAddress, pingOutput.getText().toString());
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
        String message = "Exported to /sdcard/ICMP Ping/ping_" + pingAddress + ".txt";
        if (!success) {
            length = Snackbar.LENGTH_SHORT;
            message = "Ping log not exported due to ERROR";
        }
        Snackbar.make(findViewById(android.R.id.content), message, length)
                .setActionTextColor(Color.RED)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            isRoute = data.getBooleanExtra(Constants.PING_ROUTE_PREF, Constants.PING_ROUTE_DEFAULT);
            isTimestamp = data.getBooleanExtra(Constants.PING_TIMESTAMPS_PREF, Constants.PING_TIMESTAMPS_DEFAULT);
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
            settingsIntent.putExtra(Constants.PING_ROUTE_PREF, isRoute);
            settingsIntent.putExtra(Constants.PING_TIMESTAMPS_PREF, isTimestamp);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ping.cancelProcess();
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
                Snackbar.make(findViewById(android.R.id.content), "Export to SD enabled.", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.RED)
                        .show();
            }
        }
    }
}
