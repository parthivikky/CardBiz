package com.mobellotec.cardbiz.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.ResendVerifyCode;
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

public class VerificationActivity extends AppCompatActivity {

    private EditText num1, num2, num3, num4;
    private Button resend_code, back, verify;
    private String requestID, userId;
    private String urlSchemeHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        try {
            initViews();
            urlSchemeHost = getIntent().getStringExtra("share");
            requestID = getIntent().getStringExtra("requestID");
            userId = AppPreference.getString(VerificationActivity.this, AppPreference.USER_ID);
        /*final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Verification code");
        alertDialog.setMessage("Your verification code is " + verificationCode);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();*/
            timerCount();
            num1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });

            num1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (num1.length() == 1) {
                        num2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            num2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (num2.length() == 1) {
                        num3.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            num3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (num3.length() == 1) {
                        num4.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            num4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (num4.length() == 1) {
                        if (Helper.isNetworkAvailable(getApplicationContext()))
                            verifyCode();
                        else
                            CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        /*num5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (num5.length() == 1) {
                    num6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        num6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (num6.length() == 1) {
                    if (Helper.isNetworkAvailable(getApplicationContext()))
                        verifyCode();
                    else
                        CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                }
            }
        });*/

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            if (resend_code.getText().toString().equals("Resend code")) {
                resend_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Helper.isNetworkAvailable(getApplicationContext())) {
                            timerCount();
                            resendActivationCode();
                        } else {
                            CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                        }
                    }
                });
            }

            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(VerificationActivity.this, e);
        }
    }

    private void resendActivationCode() {
        try {
            CommonClass.showProgress(VerificationActivity.this);
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ResendVerifyCode> resendCall = apiInterface.resendVerifyCode(userId,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.RESEND_VERIFY_CODE + Constants.SECRET_KEY));
            resendCall.enqueue(new Callback<ResendVerifyCode>() {
                @Override
                public void onResponse(Response<ResendVerifyCode> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        ResendVerifyCode resendVerifyCode = response.body();
                        if (resendVerifyCode.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            CommonClass.showMessageToast(VerificationActivity.this, "You will get verification code shortly.");
                        } else {
                            CommonClass.showMessageToast(VerificationActivity.this, "Unable to send verification code.");
                        }
                    } else {
                        CommonClass.showErrorToast(getApplicationContext(), ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(getApplicationContext(), ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(VerificationActivity.this, e);
        }

    }

    private void timerCount() {
        try {
            new CountDownTimer(120000, 1000) {

                public void onTick(long millisUntilFinished) {
                    resend_code.setEnabled(false);
                    resend_code.setText("" + millisUntilFinished / 1000 + " seconds");
                }

                public void onFinish() {
                    resend_code.setText("Resend code");
                    resend_code.setEnabled(true);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(VerificationActivity.this, e);
        }
    }

    private void verifyCode() {
        try {
            CommonClass.showProgress(VerificationActivity.this);
            String code = num1.getText().toString() + num2.getText().toString() + num3.getText().toString() + num4.getText().toString();
            final RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Log.i("verify", userId + " " + requestID + " " + code + " " + CommonClass.convertMd5(Constants.BASE_URL + Constants.VERIFICATION + Constants.SECRET_KEY));
            Call<ApiStatus> verifyCall = apiInterface.verifyCode(userId, requestID, code,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.VERIFICATION + Constants.SECRET_KEY));
            verifyCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        CommonClass.dismissProgress();
                        ApiStatus apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            AppPreference.setBoolean(VerificationActivity.this, AppPreference.IS_LOGIN, true);
                            if (urlSchemeHost != null) {
                                startActivity(new Intent(VerificationActivity.this, SharedCardViewActivity.class).putExtra("share", urlSchemeHost));
                            } else {
                                startActivity(new Intent(VerificationActivity.this, HomeActivity.class));
                            }
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            CommonClass.showMessageToast(VerificationActivity.this, "You have entered wrong code");
                        }
                    } else {
                        CommonClass.dismissProgress();
                        CommonClass.showErrorToast(VerificationActivity.this, ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(VerificationActivity.this, ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(VerificationActivity.this, e);
        }
    }

    private void initViews() {
        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        num3 = (EditText) findViewById(R.id.num3);
        num4 = (EditText) findViewById(R.id.num4);
//        num5 = (EditText) findViewById(R.id.num5);
//        num6 = (EditText) findViewById(R.id.num6);
        resend_code = (Button) findViewById(R.id.resend_code);
        back = (Button) findViewById(R.id.back);
        verify = (Button) findViewById(R.id.verify);
    }

    private void validate() {
        try {
            if (num1.getText().toString().length() != 1) {
                CommonClass.showMessageToast(getApplicationContext(), "Please fill your verification code.");
            } else if (num2.getText().toString().length() != 1) {
                CommonClass.showMessageToast(getApplicationContext(), "Please fill your verification code.");
            } else if (num3.getText().toString().length() != 1) {
                CommonClass.showMessageToast(getApplicationContext(), "Please fill your verification code.");
            } else if (num4.getText().toString().length() != 1) {
                CommonClass.showMessageToast(getApplicationContext(), "Please fill your verification code.");
            }
        /*else if(num5.getText().toString().length() != 1){
            CommonClass.showMessageToast(getApplicationContext(),"Please fill your verification code.");
        }
        else if(num6.getText().toString().length() != 1){
            CommonClass.showMessageToast(getApplicationContext(),"Please fill your verification code.");
        }*/
            else {
                if (Helper.isNetworkAvailable(getApplicationContext()))
                    verifyCode();
                else
                    CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(VerificationActivity.this, e);
        }
    }
}
