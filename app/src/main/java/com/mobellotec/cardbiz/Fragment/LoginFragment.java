package com.mobellotec.cardbiz.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientException;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.mobellotec.cardbiz.Activity.HomeActivity;
import com.mobellotec.cardbiz.Activity.LoginActivity;
import com.mobellotec.cardbiz.Activity.MobileNoActivity;
import com.mobellotec.cardbiz.Activity.SharedCardViewActivity;
import com.mobellotec.cardbiz.Activity.VerificationActivity;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.LinkedinConfig;
import com.mobellotec.cardbiz.Model.LinkedinDialog;
import com.mobellotec.cardbiz.Model.Login;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.EnumSet;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by MobelloTech on 13-07-2015.
 */
public class LoginFragment extends Fragment {

    private EditText emailId, password;
    private TextView login, forgotPassword;
    private TextView linkedIn;
    private LoginButton facebook;
    private CallbackManager callbackManager;
    private String first_name, last_name, email_id, mobile;

    //    public String scopeParams = "rw_nus+r_basicprofile";
//    private LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(LinkedinConfig.LINKEDIN_CONSUMER_KEY, LinkedinConfig.LINKEDIN_CONSUMER_SECRET);
//    private LinkedInRequestToken requestToken;
    private LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(LinkedinConfig.LINKEDIN_CONSUMER_KEY, LinkedinConfig.LINKEDIN_CONSUMER_SECRET);
    private LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(LinkedinConfig.LINKEDIN_CONSUMER_KEY, LinkedinConfig.LINKEDIN_CONSUMER_SECRET);
    private LinkedInApiClient client;
    private LinkedInAccessToken accessToken;
    private String urlSchemeHost = null;
    private static final int USER_VERIFICATION = 100;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        try {
            initViews(view);
            urlSchemeHost = getActivity().getIntent().getStringExtra("share");
            TextView title = (TextView) getActivity().findViewById(R.id.title);
            title.setText("CardBiz Login");

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            facebook.setReadPermissions(Arrays.asList("public_profile", "email", "user_about_me"));
            facebook.setFragment(this);
            callbackManager = CallbackManager.Factory.create();
            if (AccessToken.getCurrentAccessToken() != null) {
                getFacebookUserData();
            }

            facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    getFacebookUserData();
                }

                @Override
                public void onCancel() {
                    CommonClass.showMessageToast(getActivity(), "Sorry! Can not login.");
                }

                @Override
                public void onError(FacebookException e) {
                    e.printStackTrace();
                    CommonClass.showMessageToast(getActivity(), "Sorry! Can not login.");
                }
            });

            linkedIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    linkedInLogin();
                    linkInLog();


                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(getActivity()))
                        validate();
                    else
                        CommonClass.showMessageToast(getActivity(), R.string.check_network);
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(getActivity()))
                        forgotPassword();
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

    private void forgotPassword() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_edittext, null);
            alert.setView(view);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            final EditText text = (EditText) view.findViewById(R.id.text);
            title.setText("Forgot Password");
            text.setHint("Enter your email id");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.isEmpty(text.getText().toString())) {
                        text.setError("Enter your email id");
                    } else if (!CommonClass.isValidEmail(text.getText().toString())) {
                        text.setError("Invalid email id");
                    } else {
                        CommonClass.showProgress(getActivity());
                        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
                        Call<ApiStatus> forgetCall = apiInterface.forgotPassword(text.getText().toString(),
                                CommonClass.convertMd5(Constants.BASE_URL + Constants.FORGOT_PASSWORD + Constants.SECRET_KEY));
                        forgetCall.enqueue(new Callback<ApiStatus>() {
                            @Override
                            public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                                CommonClass.dismissProgress();
                                if (response.isSuccess()) {
                                    ApiStatus apiStatus = response.body();
                                    CommonClass.showMessageToast(getActivity(), apiStatus.getMessage());
                                    if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                                        alert.dismiss();
                                    } else {
                                        text.setText("");
                                    }
                                } else {
                                    CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                CommonClass.dismissProgress();
                                CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                            }
                        });
                    }
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void initViews(View view) {
        emailId = (EditText) view.findViewById(R.id.ed_corporate_id);
        password = (EditText) view.findViewById(R.id.ed_password);
        login = (TextView) view.findViewById(R.id.btn_login);
        forgotPassword = (TextView) view.findViewById(R.id.forgot_password);
        facebook = (LoginButton) view.findViewById(R.id.btn_facebook);
        linkedIn = (TextView) view.findViewById(R.id.btn_linkedin);
    }

    private void validate() {
        try {
            if (TextUtils.isEmpty(emailId.getText().toString()))
                showDialog("Please enter the email id");
            else if (!CommonClass.isValidEmail(emailId.getText().toString()))
                showDialog("Invalid email id");
            else if (TextUtils.isEmpty(password.getText().toString()))
                showDialog("Please enter the password");
            else if (password.getText().toString().length() < 8)
                showDialog("Password should be minimum 8 characters");
            else {
                verifyLogin(emailId.getText().toString(), password.getText().toString(), "normal");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void verifyLogin(String email, String password, final String userType) {
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<Login> loginCall = apiInterface.login(email, password, userType,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.LOGIN + Constants.SECRET_KEY),
                    "android", AppPreference.getString(getActivity(), AppPreference.GCM_REG_ID));
            loginCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Response<Login> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        Login login = response.body();
                        if (login.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            first_name = login.getUserInfo().getFirstName();
                            last_name = login.getUserInfo().getLastName();
                            email_id = login.getUserInfo().getEmail();
                            mobile = login.getUserInfo().getPhoneNo();
                            AppPreference.setString(getActivity(), AppPreference.FIRST_NAME, first_name);
                            AppPreference.setString(getActivity(), AppPreference.LAST_NAME, last_name);
                            AppPreference.setString(getActivity(), AppPreference.EMAIL, email_id);
                            AppPreference.setString(getActivity(), AppPreference.MOBILE, mobile);
                            AppPreference.setString(getActivity(), AppPreference.USER_ID, login.getUserInfo().getUserID());
                            if (login.getUserInfo().getIsVerified().equalsIgnoreCase("1")) {
                                AppPreference.setBoolean(getActivity(), AppPreference.IS_LOGIN, true);
                                if (urlSchemeHost != null) {
                                    Intent intent = new Intent(getActivity(), SharedCardViewActivity.class);
                                    intent.putExtra("share", urlSchemeHost);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    startActivity(new Intent(getActivity(), HomeActivity.class));
                                    getActivity().finish();
                                }
                            } else {
                                Intent intent = new Intent(getActivity(), VerificationActivity.class);
                                intent.putExtra("share", urlSchemeHost);
                                intent.putExtra("requestID", login.getRequestID());
                                startActivityForResult(intent, USER_VERIFICATION);
                            }
                        } else {
                            if (userType.equalsIgnoreCase("normal")) {
                                CommonClass.showMessageToast(getActivity(), "Invalid username and password");
                                LoginManager.getInstance().logOut();
                            } else {
                                Intent intent = new Intent(getActivity(), MobileNoActivity.class);
                                intent.putExtra("share", urlSchemeHost);
                                intent.putExtra("userType", userType);
                                startActivityForResult(intent, USER_VERIFICATION);
                            }
                        }
                    } else {
                        CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            CommonClass.dismissProgress();
            Utils.sendReport(getActivity(), e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == USER_VERIFICATION && resultCode == getActivity().RESULT_OK)
                getActivity().finish();
            else
                callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void getFacebookUserData() {
        try {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                    try {
                        first_name = jsonObject.getString("first_name");
                        last_name = jsonObject.getString("last_name");
                        email_id = jsonObject.getString("email");
                        AppPreference.setString(getActivity(), AppPreference.FIRST_NAME, first_name);
                        AppPreference.setString(getActivity(), AppPreference.LAST_NAME, last_name);
                        AppPreference.setString(getActivity(), AppPreference.EMAIL, email_id);
                        verifyLogin(email_id, "", "fb");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "first_name,last_name,email");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void linkInLog() {
        LISessionManager.getInstance(getActivity().getApplicationContext()).init(getActivity(), buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                String url = "https://api.linkedin.com/v1/people/~:(email-address,first-name,last-name)";
                APIHelper apiHelper = APIHelper.getInstance(getActivity());
                apiHelper.getRequest(getActivity(), url, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse result) {
                        try {
                            Log.i("result",""+result.getResponseDataAsJson());
                            first_name = result.getResponseDataAsJson().getString("firstName");
                            last_name = result.getResponseDataAsJson().getString("lastName");
                            email_id = result.getResponseDataAsJson().getString("emailAddress");
                            if (TextUtils.isEmpty(email_id)) {
                                CommonClass.showMessageToast(getActivity(), "Email id is required.");
                            } else {
                                AppPreference.setString(getActivity(), AppPreference.FIRST_NAME, first_name);
                                AppPreference.setString(getActivity(), AppPreference.LAST_NAME, last_name);
                                AppPreference.setString(getActivity(), AppPreference.EMAIL, email_id);
                                verifyLogin(email_id, "", "li");
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onApiError(LIApiError error) {
                        CommonClass.showMessageToast(getActivity(), "Sorry! Can not login.");
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.i("error",error.toString());
                CommonClass.showMessageToast(getActivity(), "Sorry! Can not access LinkedIn.");
            }
        }, true);
    }



    private void linkedInLogin() {
        try {
            ProgressDialog progressHUD = new ProgressDialog(getActivity());
            LinkedinDialog linkedinDialog = new LinkedinDialog(getActivity(), progressHUD);
            linkedinDialog.show();
            linkedinDialog.setVerifierListener(new LinkedinDialog.OnVerifyListener() {
                @Override
                public void onVerify(String verifier) {
                    accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.requestToken, verifier);
                    LinkedinDialog.factory.createAsyncLinkedInApiClient(accessToken);
                    client = factory.createLinkedInApiClient(accessToken);
                /*Log.i("LinkedinSample",
                        "ln_access_token: " + accessToken.getToken());
                Log.i("LinkedinSample",
                        "ln_access_token: " + accessToken.getTokenSecret());*/
                    Person person = client.getProfileForCurrentUser(EnumSet
                            .of(ProfileField.ID, ProfileField.FIRST_NAME,
                                    ProfileField.LAST_NAME,
                                    ProfileField.EMAIL_ADDRESS,
                                    ProfileField.PHONE_NUMBERS));

//                Log.e("access token secret", client.getAccessToken().getTokenSecret());

                    if (person != null) {
                        first_name = person.getFirstName();
                        last_name = person.getLastName();
                        email_id = person.getEmailAddress();
                        if (TextUtils.isEmpty(email_id)) {
                            CommonClass.showMessageToast(getActivity(), "Email id is required.");
                        } else {
                            AppPreference.setString(getActivity(), AppPreference.FIRST_NAME, first_name);
                            AppPreference.setString(getActivity(), AppPreference.LAST_NAME, last_name);
                            AppPreference.setString(getActivity(), AppPreference.EMAIL, email_id);
                            verifyLogin(email_id, "", "li");
                        }
                    }
                }
            });
            progressHUD.setMessage("Loading...");
            progressHUD.setCancelable(true);
            progressHUD.show();
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

    private static Scope buildScope(){
        return Scope.build(Scope.R_BASICPROFILE,Scope.R_EMAILADDRESS);
    }
}
