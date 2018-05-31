package com.pacmac.pinger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class PingActivity extends AppCompatActivity implements PingListener {

    private static final String TAG = PingActivity.class.getSimpleName();

    private EditText ipEditText = null;
    private Button pingBtn = null;
    private TextView pingOutput = null;
    private ScrollView outputScrollView = null;

    private int size = Constants.PING_SIZE_DEFAULT;
    private int count = Constants.PING_COUNT_DEFAULT;
    private int interval = Constants.PING_INTERVAL_DEFAULT;
    private int ttl = Constants.PING_TTL_DEFAULT;

    private boolean isPingRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        ipEditText = findViewById(R.id.pingAddress);
        pingBtn = findViewById(R.id.pingBtn);

        pingOutput = findViewById(R.id.pingOutput);
        outputScrollView = findViewById(R.id.mScrollView);

        count = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_COUNT_DEFAULT, Constants.PING_COUNT_PREF);
        size = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_SIZE_DEFAULT, Constants.PING_SIZE_PREF);
        interval = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_INTERVAL_DEFAULT, Constants.PING_INTERVAL_PREF);
        ttl = Utility.getIntFromPreference(getApplicationContext(), Constants.PING_TTL_DEFAULT, Constants.PING_TTL_PREF);


        pingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPingRunning) {
                    Ping.cancelProcess();

                    isPingRunning = false;
                    pingBtn.setText("PING");

                } else {
                    String countSTR = "";

                    String sizeSTR = "-s " + String.valueOf(size);
                    if (count != 0) {
                        countSTR = "-c " + String.valueOf(count);
                    }
                    String intervalSTR = "-i " + String.valueOf(interval);
                    String ttlSTR = "-t " + String.valueOf(ttl);

                    String ipAddress = ipEditText.getText().toString();
                    String command = sizeSTR + " " + countSTR + " " + intervalSTR + " " + ttlSTR + " " + ipAddress;

                    // Clear output window
                    pingOutput.setText("");
                    // Start ping
                    new AsyncPingTask(PingActivity.this).execute(command);
                    isPingRunning = true;
                    pingBtn.setText("CANCEL");
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode ==  Constants.PING_SETTINGS_RC) {
            count = data.getIntExtra(Constants.PING_COUNT_PREF, Constants.PING_COUNT_DEFAULT);
            size = data.getIntExtra(Constants.PING_SIZE_PREF, Constants.PING_SIZE_DEFAULT);
            interval = data.getIntExtra(Constants.PING_INTERVAL_PREF, Constants.PING_INTERVAL_DEFAULT);
            ttl = data.getIntExtra(Constants.PING_TTL_PREF, Constants.PING_TTL_DEFAULT);
        }

    }

    @Override
    public void onComplete(final String result) {
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
}
