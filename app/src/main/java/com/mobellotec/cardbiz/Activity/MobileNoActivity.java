package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Model.Register;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MobileNoActivity extends AppCompatActivity {

    private EditText mobile;
    private TextView submit, country_code;
    private String urlSchemeHost = null;
    private static final int USER_VERIFICATION = 100;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_no);
        try {
            initViews();
            urlSchemeHost = getIntent().getStringExtra("share");
            userType = getIntent().getStringExtra("userType");
            country_code.setText(Helper.getCountryCode(this));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(MobileNoActivity.this, e);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Helper.isNetworkAvailable(getApplicationContext()))
                        registration();
                    else
                        CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(MobileNoActivity.this, e);
                }
            }
        });
    }

    private void initViews() {
        mobile = (EditText) findViewById(R.id.mobile);
        submit = (TextView) findViewById(R.id.submit);
        country_code = (TextView) findViewById(R.id.country_code);
    }

    private void registration() {
        try {
            CommonClass.showProgress(MobileNoActivity.this);
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<Register> registerCall = apiInterface.register(AppPreference.getString(MobileNoActivity.this, AppPreference.FIRST_NAME),
                    AppPreference.getString(MobileNoActivity.this, AppPreference.LAST_NAME),
                    AppPreference.getString(MobileNoActivity.this, AppPreference.EMAIL), "",
                    Helper.removePlusFromMobile(country_code.getText().toString() + mobile.getText().toString()),
                    userType, CommonClass.convertMd5(Constants.BASE_URL + Constants.REGISTRATION + Constants.SECRET_KEY),
                    "android", AppPreference.getString(MobileNoActivity.this, AppPreference.GCM_REG_ID));
            registerCall.enqueue(new Callback<Register>() {
                @Override
                public void onResponse(Response<Register> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        Register register = response.body();
                        if (register.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            String userId = register.getUserID();
                            String requestID = register.getRequestID();
                            AppPreference.setBoolean(MobileNoActivity.this, AppPreference.IS_LOGIN, true);
                            AppPreference.setString(MobileNoActivity.this, AppPreference.MOBILE, Helper.removePlusFromMobile(country_code.getText().toString() + mobile.getText().toString()));
                            AppPreference.setString(MobileNoActivity.this, AppPreference.USER_ID, userId);
                            if (urlSchemeHost != null) {
                                startActivity(new Intent(MobileNoActivity.this, SharedCardViewActivity.class).putExtra("share", urlSchemeHost));
                            } else {
                                startActivity(new Intent(MobileNoActivity.this, HomeActivity.class).putExtra("is_register",true));
                            }
                            /*Intent intent = new Intent(MobileNoActivity.this, VerificationActivity.class);
                            intent.putExtra("share", urlSchemeHost);
                            intent.putExtra("requestID", requestID);
                            intent.putExtra("userId", userId);
                            startActivityForResult(intent, USER_VERIFICATION);*/
                        finish();
                        } else {
                            Toast.makeText(MobileNoActivity.this, register.getErrorMsg(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        CommonClass.dismissProgress();
                        CommonClass.showErrorToast(MobileNoActivity.this, ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.fillInStackTrace();
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(MobileNoActivity.this, ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(MobileNoActivity.this, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_VERIFICATION && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
