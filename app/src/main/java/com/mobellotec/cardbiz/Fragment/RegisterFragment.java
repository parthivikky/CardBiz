package com.mobellotec.cardbiz.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Activity.HomeActivity;
import com.mobellotec.cardbiz.Activity.SharedCardViewActivity;
import com.mobellotec.cardbiz.Activity.VerificationActivity;
import com.mobellotec.cardbiz.Model.Register;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.Iso2Phone;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class RegisterFragment extends Fragment {

    private EditText first_name, last_name, emailId, mobile, password;
    private TextView register, country_code;
    private ProgressDialog progressDialog;
    private String urlSchemeHost = null;
    private static final int USER_VERIFICATION = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        try {
            initViews(view);
            urlSchemeHost = getActivity().getIntent().getStringExtra("share");
            TextView title = (TextView) getActivity().findViewById(R.id.title);
            title.setText("CardBiz Register");
            country_code.setText(Helper.getCountryCode(getActivity()));
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(getActivity()))
                        validate();
                    else
                        CommonClass.showMessageToast(getActivity(), R.string.check_network);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    private void initViews(View view) {
        first_name = (EditText) view.findViewById(R.id.ed_first_name);
        last_name = (EditText) view.findViewById(R.id.ed_last_name);
        emailId = (EditText) view.findViewById(R.id.ed_email);
        mobile = (EditText) view.findViewById(R.id.ed_mobile_number);
        password = (EditText) view.findViewById(R.id.ed_password);
        register = (TextView) view.findViewById(R.id.btn_signup);
        country_code = (TextView) view.findViewById(R.id.country_code);
    }

    private void validate() {
        try {
            if (TextUtils.isEmpty(first_name.getText().toString()))
                showDialog("Please enter the first name");
            else if (TextUtils.isEmpty(last_name.getText().toString()))
                showDialog("Please enter the last name");
            else if (TextUtils.isEmpty(emailId.getText().toString()))
                showDialog("Please enter the email id");
            else if (!CommonClass.isValidEmail(emailId.getText().toString()))
                showDialog("Invalid email id");
            else if (TextUtils.isEmpty(mobile.getText().toString()))
                showDialog("Please enter the phone number");
            else if (password.getText().toString().length() < 8)
                showDialog("Password should be minimum 8 characters");
            else
                registration();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void registration() {
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<Register> registerCall = apiInterface.register(first_name.getText().toString(),
                    last_name.getText().toString(),
                    emailId.getText().toString(),
                    password.getText().toString(),
                    Helper.removePlusFromMobile(country_code.getText().toString() + mobile.getText().toString()),
                    "normal",
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.REGISTRATION + Constants.SECRET_KEY),
                    "android",
                    AppPreference.getString(getActivity(), AppPreference.GCM_REG_ID));
            registerCall.enqueue(new Callback<Register>() {
                @Override
                public void onResponse(Response<Register> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        Register register = response.body();
                        if (register.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            CommonClass.showMessageToast(getActivity(), "You will get verification code on registered mobile shortly.");
                            String userId = register.getUserID();
                            String requestID = register.getRequestID();
                            AppPreference.setString(getActivity(), AppPreference.FIRST_NAME, first_name.getText().toString());
                            AppPreference.setString(getActivity(), AppPreference.LAST_NAME, last_name.getText().toString());
                            AppPreference.setString(getActivity(), AppPreference.EMAIL, emailId.getText().toString());
                            AppPreference.setString(getActivity(), AppPreference.MOBILE, Helper.removePlusFromMobile(country_code.getText().toString() + mobile.getText().toString()));
                            AppPreference.setString(getActivity(), AppPreference.USER_ID, userId);
                            AppPreference.setBoolean(getActivity(), AppPreference.IS_LOGIN, true);
                            if (urlSchemeHost != null) {
                                startActivity(new Intent(getActivity(), SharedCardViewActivity.class).putExtra("share", urlSchemeHost));
                            } else {
                                startActivity(new Intent(getActivity(), HomeActivity.class).putExtra("is_register",true));
                            }
                            /*Intent intent = new Intent(getActivity(), VerificationActivity.class);
                            intent.putExtra("share", urlSchemeHost);
                            intent.putExtra("requestID", requestID);
                            intent.putExtra("userId", userId);
                            startActivityForResult(intent, USER_VERIFICATION);*/
                        getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), register.getErrorMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void showDialog(String message) {
        try {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.login_error_dialog, null);
            dialog.setContentView(view);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            TextView txt_msg = (TextView) view.findViewById(R.id.message);
            TextView txt_ok = (TextView) view.findViewById(R.id.ok);
            txt_msg.setText(message);
            txt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USER_VERIFICATION && resultCode == getActivity().RESULT_OK) {
            getActivity().finish();
        }

    }

}
