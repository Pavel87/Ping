package com.pacmac.pinger;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class AboutActivity extends AppCompatActivity {

    private TextView versionText = null;

    private InterstitialAd mInterstitialAd;
    private boolean shouldShowAd = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle(getString(R.string.about_name));

        versionText = findViewById(R.id.version);
        versionText.setText(Constants.VERSION);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_id_1));

        findViewById(R.id.feedbackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldShowAd = true;
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"pacmac.dev@gmail.com"});
                Email.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback));
                Email.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (shouldShowAd) {
            shouldShowAd = false;
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
