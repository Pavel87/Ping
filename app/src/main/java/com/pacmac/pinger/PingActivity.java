package com.pacmac.pinger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class PingActivity extends AppCompatActivity implements PingListener {

    private EditText ipEditText = null;
    private Button pingBtn = null;
    private TextView pingOutput = null;
    private ScrollView outputScrollView = null;
    private TextInputLayout pingTextLayout = null;
    private ImageView exportBtn = null;

    private int size = Constants.PING_SIZE_DEFAULT;
    private int count = Constants.PING_COUNT_DEFAULT;
    private int interval = Constants.PING_INTERVAL_DEFAULT;
    private int ttl = Constants.PING_TTL_DEFAULT;
    private int deadline = Constants.PING_DEADLINE_DEFAULT;
    private boolean isRoute = Constants.PING_ROUTE_DEFAULT;
    private boolean isTimestamp = Constants.PING_TIMESTAMPS_DEFAULT;

    private boolean isPingRunning = false;


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
                Log.d("PACMAC", pingOutput.getText().toString());
            }
        });

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Ping.cancelProcess();
    }
}
