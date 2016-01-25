package com.mobellotec.cardbiz.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class NotificationActivity extends Activity {

    private String userName, userID, cardID, bumpResult, phoneNo, companyName, latPosition, longPosition, time, currentUserId;
    private Bundle bundle;
    private JSONObject jsonObject;
    private ArrayList<GroupInfo> sharedContacts = new ArrayList<>();
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        try {
            bundle = getIntent().getExtras();
            dbHelper = new DBHelper(this);
            sharedContacts = dbHelper.getSharedContact();
            currentUserId = AppPreference.getString(this, AppPreference.USER_ID);

            if (bundle != null) {
                if (bundle.containsKey(Constants.BUMP_RESULT)) {
                    bumpResult = bundle.getString(Constants.BUMP_RESULT);
                    if (bumpResult != null) {
                        try {
                            jsonObject = new JSONObject(bumpResult);
                            userID = jsonObject.getString("userID");
                            userName = jsonObject.getString("name");
                            phoneNo = jsonObject.getString("phoneNo");
                            cardID = jsonObject.getString("cardID");
                            companyName = jsonObject.getString("companyName");

//                        latPosition = jsonObject.getString("latPosition");
//                        longPosition = jsonObject.getString("longPosition");
//                        time = jsonObject.getString("time");
                            validateContact();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(NotificationActivity.this, e);
        }
    }

    private void validateContact() {
        try {
            boolean isAddable = true;
            for (int i = 0; i < sharedContacts.size(); i++) {
                if (cardID.equalsIgnoreCase(sharedContacts.get(i).getCardID())) {
                    isAddable = false;
                    break;
                }
            }
            showDialog(isAddable);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(NotificationActivity.this, e);
        }
    }

    private void addContact() {
        try {
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ApiStatus> addCardCall = apiInterface.addSharedCard(currentUserId, cardID);
            addCardCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        ApiStatus status = response.body();
                        if (status.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            dbHelper.insertSharedContact(userID, userName, phoneNo, cardID, companyName);
                            CommonClass.showMessageToast(NotificationActivity.this, "Card added successfully in your card holder.");
                        } else {
                            CommonClass.showMessageToast(NotificationActivity.this, "Card is not added.");
                        }
                    } else {
                        CommonClass.showErrorToast(NotificationActivity.this, ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.showErrorToast(NotificationActivity.this, ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(NotificationActivity.this, e);
        }
    }

    private void showDialog(final boolean isAddable) {
        try {
            final Dialog dialog = new Dialog(NotificationActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bumb_notification_dialog);
            dialog.setCanceledOnTouchOutside(false);
            TextView textView = (TextView) dialog.findViewById(R.id.card_name);
            textView.setText(userName + " wants to share contact information with you");
            Button accept = (Button) dialog.findViewById(R.id.accept);
            Button reject = (Button) dialog.findViewById(R.id.reject);
            if (!isAddable) {
                accept.setText("View Card");
                reject.setText("Cancel");
            }
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAddable)
                        addContact();
                    else {
                        Intent intent = new Intent(NotificationActivity.this, OwnCardViewActivity.class);
                        intent.putExtra("card_id", cardID);
                        intent.putExtra("user_id", userID);
                        startActivity(intent);
                    }
                    dialog.dismiss();
                    finish();
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(NotificationActivity.this, e);
        }
    }
}
