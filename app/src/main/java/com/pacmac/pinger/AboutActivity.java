package com.pacmac.pinger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView versionText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle(getString(R.string.about_name));

        versionText = findViewById(R.id.version);
        versionText.setText(Constants.VERSION);

        findViewById(R.id.feedbackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
