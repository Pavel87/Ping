package com.pacmac.pinger;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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

        AdView mAdView2 = findViewById(R.id.adView2);

        int orientation = getRequestedOrientation();
//        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            mAdView2.setAdSize(AdSize.SMART_BANNER);
//        }
        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest2);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utility.transitionLeftToRight(this);
    }
}
