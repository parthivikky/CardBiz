package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.Utils;

public class NoCardActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_call, img_sms;
    private String name, email, phone;
    private TextView nameView, emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_card);
        try {
            initView();
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            phone = getIntent().getStringExtra("phone");
            nameView = (TextView) findViewById(R.id.name);
            emailView = (TextView) findViewById(R.id.email);
            nameView.setText(name);
            emailView.setText(email);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(NoCardActivity.this, e);
        }
    }

    private void initView() {
        img_call = (ImageView) findViewById(R.id.img_call);
        img_sms = (ImageView) findViewById(R.id.img_sms);
        img_call.setOnClickListener(this);
        img_sms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.img_call:
                try {
                    if (phone == null || TextUtils.isEmpty(phone)) {
                        CommonClass.showMessageToast(NoCardActivity.this, "Phone number not available");
                    } else {
                        Uri number = Uri.parse("tel:" + phone);
                        intent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(NoCardActivity.this, e);
                }
                break;
            case R.id.img_sms:
                try {
                    if (phone == null || TextUtils.isEmpty(phone)) {
                        CommonClass.showMessageToast(NoCardActivity.this, "Phone number not available");
                    } else {
                        Uri uri = Uri.parse("smsto:" + phone);
                        intent = new Intent(Intent.ACTION_SENDTO, uri);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(NoCardActivity.this, e);
                }
                break;
        }
    }
}
