package com.pacmac.pinger;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsPingActivity extends AppCompatActivity {

    private SeekBar sizeSB = null;
    private SeekBar countSB = null;
    private SeekBar intervalSB = null;
    private SeekBar ttlSB = null;

    private TextView countText = null;
    private TextView sizeText = null;
    private TextView intervalText = null;
    private TextView ttlText = null;

    int size = 0;
    int count = 0;
    int interval = 0;
    int ttl = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_ping);

        sizeSB = findViewById(R.id.seekBarSize);
        countSB = findViewById(R.id.seekBarCount);
        intervalSB = findViewById(R.id.seekBarInterval);
        ttlSB = findViewById(R.id.seekBarTTL);

        countText = findViewById(R.id.countValue);
        sizeText = findViewById(R.id.sizeValue);
        intervalText = findViewById(R.id.intervalValue);
        ttlText = findViewById(R.id.ttlValue);

        setStartValues(getIntent());

        sizeSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                sizeText.setText(String.valueOf(size));
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

                if(count != 0){
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
                intervalText.setText(String.valueOf(interval));
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
                ttlText.setText(String.valueOf(ttl));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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

        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    private void saveSettings(Context context) {
        Utility.setIntToPreference(context, size, Constants.PING_SIZE_PREF);
        Utility.setIntToPreference(context, count, Constants.PING_COUNT_PREF);
        Utility.setIntToPreference(context, interval, Constants.PING_INTERVAL_PREF);
        Utility.setIntToPreference(context, ttl, Constants.PING_TTL_PREF);
    }

    private void setStartValues(Intent intent) {

        count = intent.getIntExtra(Constants.PING_COUNT_PREF, Constants.PING_COUNT_DEFAULT);
        size = intent.getIntExtra(Constants.PING_SIZE_PREF, Constants.PING_SIZE_DEFAULT);
        interval = intent.getIntExtra(Constants.PING_INTERVAL_PREF, Constants.PING_INTERVAL_DEFAULT);
        ttl = intent.getIntExtra(Constants.PING_TTL_PREF, Constants.PING_TTL_DEFAULT);

        if(count != 0){
            countText.setText(String.valueOf(count));
        } else {
            countText.setText(Constants.INFINITY);

        }
        sizeText.setText(String.valueOf(size));
        intervalText.setText(String.valueOf(interval));
        ttlText.setText(String.valueOf(ttl));

        countSB.setProgress(count);
        sizeSB.setProgress(size);
        intervalSB.setProgress(interval);
        ttlSB.setProgress(ttl);


    }

}
