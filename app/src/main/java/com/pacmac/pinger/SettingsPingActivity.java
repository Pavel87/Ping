package com.pacmac.pinger;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingsPingActivity extends AppCompatActivity {

    private SeekBar sizeSB = null;
    private SeekBar countSB = null;
    private SeekBar intervalSB = null;
    private SeekBar ttlSB = null;
    private SeekBar deadlineSB = null;
    private SeekBar timeoutSB = null;

    private TextView countText = null;
    private TextView sizeText = null;
    private TextView intervalText = null;
    private TextView ttlText = null;
    private TextView deadlineText = null;
    private TextView timeoutText = null;

    private AppCompatCheckBox ipv6pingBox = null;
    private AppCompatCheckBox routeCheckbox = null;
    private AppCompatCheckBox tsCheckbox = null;

    private int size = 0;
    private int count = 0;
    private int interval = 0;
    private int ttl = 0;
    private int deadline = 0;
    private int timeout = 0;
    private boolean isRoute = false;
    private boolean isTimestampAndAddress = false;
    private boolean useIPv6 = false;

    private Object syncCheckBoxes = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_ping);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.config_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sizeSB = findViewById(R.id.seekBarSize);
        countSB = findViewById(R.id.seekBarCount);
        intervalSB = findViewById(R.id.seekBarInterval);
        ttlSB = findViewById(R.id.seekBarTTL);
        deadlineSB = findViewById(R.id.seekBarDeadline);
        timeoutSB = findViewById(R.id.seekBarTimeout);

        countText = findViewById(R.id.countValue);
        sizeText = findViewById(R.id.sizeValue);
        intervalText = findViewById(R.id.intervalValue);
        ttlText = findViewById(R.id.ttlValue);
        deadlineText = findViewById(R.id.deadlineValue);
        timeoutText = findViewById(R.id.timeoutValue);


        ipv6pingBox = findViewById(R.id.ipv6ping);
        routeCheckbox = findViewById(R.id.routeCheckBox);
        tsCheckbox = findViewById(R.id.timestampCheckBox);

        setStartValues(getIntent());

        sizeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                sizeText.setText(String.valueOf(Utility.getPacketSizeFromProgress(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        countSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                count = progress;

                if (count != 0) {
                    countText.setText(String.valueOf(count));
                } else {
                    countText.setText(Constants.INFINITY);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        intervalSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                interval = progress;
                showIntervalValue(Utility.getIntervalFromProgress(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ttlSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ttl = progress;
                ttlText.setText(String.valueOf(Utility.getTTLFromProgress(ttl)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        deadlineSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                deadline = progress;

                if (deadline != 0) {
                    deadlineText.setText(String.valueOf(deadline));
                } else {
                    deadlineText.setText(Constants.INFINITY);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        timeoutSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeout = progress;

                if (timeout != 0) {
                    timeoutText.setText(String.valueOf(timeout));
                } else {
                    timeoutText.setText(Constants.INFINITY);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ipv6pingBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (syncCheckBoxes) {
                    useIPv6 = isChecked;
                }
            }
        });
        routeCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (syncCheckBoxes) {
                    if (!routeCheckbox.isEnabled()) {
                        return;
                    }
                    isRoute = isChecked;
                    if (isChecked) {
                        isTimestampAndAddress = false;
                        tsCheckbox.setEnabled(false);
                    } else {
                        tsCheckbox.setEnabled(true);
                    }
                }
            }
        });
        tsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (syncCheckBoxes) {
                    if (!tsCheckbox.isEnabled()) {
                        return;
                    }
                    isTimestampAndAddress = isChecked;
                    if (isChecked) {
                        isRoute = false;
                        routeCheckbox.setEnabled(false);
                    } else {
                        routeCheckbox.setEnabled(true);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSettings(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putExtra(Constants.PING_COUNT_PREF, count);
        data.putExtra(Constants.PING_SIZE_PREF, size);
        data.putExtra(Constants.PING_INTERVAL_PREF, interval);
        data.putExtra(Constants.PING_TTL_PREF, ttl);
        data.putExtra(Constants.PING_DEADLINE_PREF, deadline);
        data.putExtra(Constants.PING_TIMEOUT_PREF, timeout);
        data.putExtra(Constants.PING_TIMESTAMPS_PREF, isTimestampAndAddress);
        data.putExtra(Constants.PING_ROUTE_PREF, isRoute);
        data.putExtra(Constants.PING_IP_VERSION_PREF, useIPv6);

        setResult(RESULT_OK, data);
        super.onBackPressed();
        Utility.transitionLeftToRight(this);
    }

    private void saveSettings(Context context) {
        Utility.setIntToPreference(context, size, Constants.PING_SIZE_PREF);
        Utility.setIntToPreference(context, count, Constants.PING_COUNT_PREF);
        Utility.setIntToPreference(context, interval, Constants.PING_INTERVAL_PREF);
        Utility.setIntToPreference(context, ttl, Constants.PING_TTL_PREF);
        Utility.setIntToPreference(context, deadline, Constants.PING_DEADLINE_PREF);
        Utility.setIntToPreference(context, timeout, Constants.PING_TIMEOUT_PREF);
        Utility.setBooleanToPreference(context, isTimestampAndAddress, Constants.PING_TIMESTAMPS_PREF);
        Utility.setBooleanToPreference(context, isRoute, Constants.PING_ROUTE_PREF);
        Utility.setBooleanToPreference(context, useIPv6, Constants.PING_IP_VERSION_PREF);
    }

    private void setStartValues(Intent intent) {

        count = intent.getIntExtra(Constants.PING_COUNT_PREF, Constants.PING_COUNT_DEFAULT);
        size = intent.getIntExtra(Constants.PING_SIZE_PREF, Constants.PING_SIZE_DEFAULT);
        interval = intent.getIntExtra(Constants.PING_INTERVAL_PREF, Constants.PING_INTERVAL_DEFAULT);
        ttl = intent.getIntExtra(Constants.PING_TTL_PREF, Constants.PING_TTL_DEFAULT);
        deadline = intent.getIntExtra(Constants.PING_DEADLINE_PREF, Constants.PING_DEADLINE_DEFAULT);
        timeout = intent.getIntExtra(Constants.PING_TIMEOUT_PREF, Constants.PING_TIMEOUT_DEFAULT);
        isRoute = intent.getBooleanExtra(Constants.PING_ROUTE_PREF, Constants.PING_ROUTE_DEFAULT);
        isTimestampAndAddress = intent.getBooleanExtra(Constants.PING_TIMESTAMPS_PREF, Constants.PING_TIMESTAMPS_DEFAULT);
        useIPv6 = intent.getBooleanExtra(Constants.PING_IP_VERSION_PREF, Constants.PING_IP_VERSION_DEFAULT);

        //error
        if (isRoute && isTimestampAndAddress) {
            isRoute = false;
            isTimestampAndAddress = false;
        }
        if (isRoute) {
            tsCheckbox.setEnabled(false);
        } else if (isTimestampAndAddress) {
            routeCheckbox.setEnabled(false);
        }

        if (count == 0) {
            countText.setText(Constants.INFINITY);
        } else {
            countText.setText(String.valueOf(count));
        }

        if (deadline == 0) {
            deadlineText.setText(Constants.INFINITY);
        } else {
            deadlineText.setText(String.valueOf(deadline));
        }
        if (timeout == 0) {
            timeoutText.setText(Constants.INFINITY);
        } else {
            timeoutText.setText(String.valueOf(timeout));
        }

        sizeText.setText(String.valueOf(Utility.getPacketSizeFromProgress(size)));
        showIntervalValue(Utility.getIntervalFromProgress(interval));
        ttlText.setText(String.valueOf(Utility.getTTLFromProgress(ttl)));

        countSB.setProgress(count);
        sizeSB.setProgress(size);
        intervalSB.setProgress(interval);
        ttlSB.setProgress(ttl);
        deadlineSB.setProgress(deadline);
        timeoutSB.setProgress(timeout);
        routeCheckbox.setChecked(isRoute);
        tsCheckbox.setChecked(isTimestampAndAddress);
        ipv6pingBox.setChecked(useIPv6);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
            Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    private void showIntervalValue(double intervalHuman) {
        if (intervalHuman < 1) {
            intervalText.setText(String.format("%.1f", intervalHuman));
        } else {
            intervalText.setText(String.valueOf((int) intervalHuman));
        }
    }

}
