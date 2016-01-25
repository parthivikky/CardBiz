package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.Constants;

/**
 * Created by MobelloTech on 16-10-2015.
 */
public class ShareSplashActivity extends AppCompatActivity {

    private String urlSchemeHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getIntent().getData() != null)
            urlSchemeHost = getIntent().getData().getHost();

        Log.i("urlSchemeHost",urlSchemeHost);

        callIntent();

    }

    private void callIntent() {
        Thread timeThread = new Thread() {
            public void run() {
                try {
                    sleep(Constants.SPLASH_TIME_OUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = null;
                    if (!AppPreference.getBoolean(ShareSplashActivity.this, AppPreference.APP_INTRO)) {
                        intent = new Intent(ShareSplashActivity.this, TutorialActivity.class);
                        intent.putExtra("share", urlSchemeHost);
                    } else if (!AppPreference.getBoolean(ShareSplashActivity.this, AppPreference.IS_LOGIN)) {
                        intent = new Intent(ShareSplashActivity.this, LoginActivity.class);
                        intent.putExtra("share", urlSchemeHost);
                    } else {
                        intent = new Intent(ShareSplashActivity.this, SharedCardViewActivity.class);
                        intent.putExtra("share", urlSchemeHost);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        };
        timeThread.start();
    }
}
