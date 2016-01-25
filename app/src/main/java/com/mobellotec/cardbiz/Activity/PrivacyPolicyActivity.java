package com.mobellotec.cardbiz.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView webView;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        try {
            webView = (WebView) findViewById(R.id.webview);
            title = (TextView) findViewById(R.id.home_title);

            boolean isPrivacy = getIntent().getBooleanExtra("privacy", true);
            if (!isPrivacy)
                title.setText("Terms and Conditions");

            loadContent(isPrivacy);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(PrivacyPolicyActivity.this, e);
        }
    }

    private void loadContent(boolean isPrivacy) {
        CommonClass.showProgress(this);
        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<ApiStatus> contentCall;
        if (isPrivacy)
            contentCall = apiInterface.privacyPolicyCall();
        else
            contentCall = apiInterface.termsAndConditionsCall();
        contentCall.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                CommonClass.dismissProgress();
                if (response.isSuccess()) {
                    ApiStatus apiStatus = response.body();
                    if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        webView.loadData(apiStatus.getResult(), "text/html", "UTF-8");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                CommonClass.dismissProgress();
                CommonClass.showErrorToast(PrivacyPolicyActivity.this, ErrorType.SERVER_ERROR);
            }
        });
    }
}
