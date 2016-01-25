package com.mobellotec.cardbiz.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.mobellotec.cardbiz.BuildConfig;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.Utils;

public class SettingsActivity extends AppCompatActivity {

    TextView logout, firstName, lastName, email, phoneNo, build, version, invite, privacy, terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        try {
            Helper.listActivity.add(SettingsActivity.this);
            initViews();
            firstName.setText(AppPreference.getString(this, AppPreference.FIRST_NAME));
            lastName.setText(AppPreference.getString(this, AppPreference.LAST_NAME));
            email.setText(AppPreference.getString(this, AppPreference.EMAIL));
            phoneNo.setText(AppPreference.getString(this, AppPreference.MOBILE));
            version.setText(String.valueOf(BuildConfig.VERSION_NAME));
            build.setText(String.valueOf(BuildConfig.VERSION_CODE));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SettingsActivity.this, e);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingsActivity.this, ContactsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(SettingsActivity.this, e);
                }
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingsActivity.this, PrivacyPolicyActivity.class).putExtra("privacy", true));
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(SettingsActivity.this, e);
                }
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(SettingsActivity.this, PrivacyPolicyActivity.class).putExtra("privacy", false));
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(SettingsActivity.this, e);
                }
            }
        });

    }

    private void initViews() {
        logout = (TextView) findViewById(R.id.logout);
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        email = (TextView) findViewById(R.id.email);
        phoneNo = (TextView) findViewById(R.id.phoneNo);
        version = (TextView) findViewById(R.id.version);
        build = (TextView) findViewById(R.id.build);
        invite = (TextView) findViewById(R.id.invite);
        privacy = (TextView) findViewById(R.id.privacy);
        terms = (TextView) findViewById(R.id.terms);
    }

    private void showLogoutDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView text = (TextView) view.findViewById(R.id.text);
            title.setText("Logout");
            text.setText("Are you sure want to logout?");
            submit.setText("Yes");
            cancel.setText("No");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreference.setBoolean(SettingsActivity.this, AppPreference.IS_LOGIN, false);
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    for (Context context : Helper.listActivity) {
                        if (context != null)
                            ((AppCompatActivity) context).finish();
                    }
                    Helper.listActivity.clear();
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SettingsActivity.this, e);
        }
    }
}
