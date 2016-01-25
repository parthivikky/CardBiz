package com.mobellotec.cardbiz.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.mobellotec.cardbiz.Fragment.LoginFragment;
import com.mobellotec.cardbiz.Fragment.RegisterFragment;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Utils;

public class LoginActivity extends AppCompatActivity {

    private TextView login, register;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            initViews();
            login.setBackgroundColor(Color.parseColor("#ffffff"));
            login.setTextColor(Color.parseColor("#66cc33"));
            register.setBackgroundResource(R.drawable.unselecet_reg_bg);
            register.setTextColor(Color.parseColor("#ffffff"));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(LoginActivity.this, e);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
                    login.setBackgroundColor(Color.parseColor("#ffffff"));
                    login.setTextColor(Color.parseColor("#66cc33"));
                    register.setBackgroundResource(R.drawable.unselecet_reg_bg);
                    register.setTextColor(Color.parseColor("#ffffff"));
                    title.setText("CardBiz Login");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(LoginActivity.this, e);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
                    register.setBackgroundColor(Color.parseColor("#ffffff"));
                    register.setTextColor(Color.parseColor("#66cc33"));
                    login.setBackgroundResource(R.drawable.unselect_log_bg);
                    login.setTextColor(Color.parseColor("#ffffff"));
                    title.setText("CardBiz Register");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(LoginActivity.this, e);
                }
            }
        });
    }

    private void initViews() {
        login = (TextView) findViewById(R.id.btn_login);
        register = (TextView) findViewById(R.id.btn_register);
        title = (TextView) findViewById(R.id.title);
    }
}
