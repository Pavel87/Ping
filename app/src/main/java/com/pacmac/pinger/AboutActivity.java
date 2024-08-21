package com.pacmac.pinger;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    private TextView versionText = null;

    private InterstitialAd mInterstitialAd = null;
    private boolean shouldShowAd = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.about_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        versionText = findViewById(R.id.version);
        versionText.setText(Constants.VERSION);


        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        AdView mAdView2 = findViewById(R.id.adView2);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);

        loadInterstitialAd();

        findViewById(R.id.feedbackBtn).setOnClickListener(v -> {
            shouldShowAd = true;
            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[]{"pacmac.dev@gmail.com"});
            Email.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.feedback));
            Email.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(Email, getResources().getString(R.string.send_feedback)));
        });
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getResources().getString(R.string.interstitial_id_1), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });
    }

    private void showInterstitialAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        } else {

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (shouldShowAd) {
            shouldShowAd = false;
            showInterstitialAd();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utility.transitionLeftToRight(this);
    }
}
