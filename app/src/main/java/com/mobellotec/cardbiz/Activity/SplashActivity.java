package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobellotec.cardbiz.PushNotification.GCMConfig;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import bolts.AppLinks;

public class SplashActivity extends AppCompatActivity {
    private DBHelper helper;
    private String strRegID;
    private byte[] contactImage = null;
    public static int attempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            helper = new DBHelper(this);
            attempt = 1;
            Helper.contactArray = new JSONArray();

            FacebookSdk.sdkInitialize(SplashActivity.this);
            getHashKey();

            AppPreference.setBoolean(SplashActivity.this, "ENTERED_FIRST_TIME", true);
            AppPreference.setBoolean(SplashActivity.this, "CONTACTS_FIRST_TIME", true);
            AppPreference.setBoolean(SplashActivity.this, "EMAIL_FIRST_TIME", true);

            if (!isGCMEnabled()) {
                CommonClass.showMessageToast(SplashActivity.this, "Device not supported GCM");
            }
            strRegID = AppPreference.getString(SplashActivity.this, AppPreference.GCM_REG_ID);
            if (TextUtils.isEmpty(strRegID) || strRegID.length() <= 0) {
                if (Helper.isNetworkAvailable(SplashActivity.this)) {
                    gcmRegistor();
                } else {
                    CommonClass.showMessageToast(SplashActivity.this, "Please check your internet connection");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, Constants.SPLASH_TIME_OUT);
                }
            } else {
                callIntent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SplashActivity.this, e);
        }
    }

    private void gcmRegistor() {
        try {
            final GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
            new AsyncTask<Void, Void, String>() {

                @Override
                protected String doInBackground(Void... params) {
                    try {
                        strRegID = gcm.register(GCMConfig.GOOGLE_PROJECT_ID);
                        AppPreference.setString(SplashActivity.this, AppPreference.GCM_REG_ID, strRegID);
                    } catch (IOException e) {
                        e.printStackTrace();
                        strRegID = "";
                        AppPreference.setString(SplashActivity.this, AppPreference.GCM_REG_ID, strRegID);
                    }
                    return strRegID;
                }

                @Override
                protected void onPostExecute(String strRegID) {
                    super.onPostExecute(strRegID);
                    if (TextUtils.isEmpty(strRegID) || strRegID.length() <= 0) {
                        CommonClass.showMessageToast(SplashActivity.this, "GCM not properly registered! try again");
                        if (attempt <= 3) {
                            attempt = attempt + 1;
                            if (Helper.isNetworkAvailable(SplashActivity.this)) {
                                gcmRegistor();
                            }
                        } else {
                            CommonClass.showMessageToast(SplashActivity.this, "GCM registered attempt finished");
                            finish();
                        }
                    } else {
                        callIntent();
                    }
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SplashActivity.this, e);
        }
    }

    private void callIntent() {
        try {
            Thread timeThread = new Thread() {
                public void run() {
                    try {
                        sleep(Constants.SPLASH_TIME_OUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        Intent intent = null;
                        if (!AppPreference.getBoolean(SplashActivity.this, AppPreference.APP_INTRO)) {
                            intent = new Intent(SplashActivity.this, TutorialActivity.class);
                        } else if (!AppPreference.getBoolean(SplashActivity.this, AppPreference.IS_LOGIN)) {
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timeThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SplashActivity.this, e);
        }
    }

    private boolean isGCMEnabled() {
        try {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(SplashActivity.this);
            if (resultCode == ConnectionResult.SUCCESS) {
                AppPreference.setBoolean(SplashActivity.this, AppPreference.IS_GCM_AVAILABLE, true);
                return true;
            } else {
                AppPreference.setBoolean(SplashActivity.this, AppPreference.IS_GCM_AVAILABLE, false);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SplashActivity.this, e);
        }
        return false;
    }

    private void getHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.mobellotec.cardbiz", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.i("sign"," "+sign);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SplashActivity.this, e);
        }
    }

    @Override
    public void onBackPressed() {

    }

}
