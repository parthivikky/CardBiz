package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.Group;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.TemplatesDownload;
import com.mobellotec.cardbiz.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class SharedCardViewActivity extends AppCompatActivity {

    private String cardId, userId, firstName, lastName, companyName, phoneNo;
    private TextView contactName, add;
    private DBHelper helper;
    private ArrayList<GroupInfo> sharedContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_card_view);
        try {
            initView();
            String value = getIntent().getStringExtra("share");
            Log.i("share",value);
            String[] link = value.split("&");
            for (int i = 0; i < link.length; i++) {
                String[] contents = link[i].split("=");
                switch (contents[0]) {
                    case "userID":
                        userId = contents[1];
                        break;
                    case "cardID":
                        cardId = contents[1];
                        break;
                    case "firstName":
                        firstName = contents[1];
                        break;
                    case "lastName":
                        lastName = contents[1];
                        break;
                    case "companyName":
                        companyName = contents[1];
                        break;
                    case "phoneNo":
                        phoneNo = contents[1];
                        break;
                }
            }

            contactName.setText(firstName + " " + lastName);
            sharedContacts = helper.getSharedContact();
            final boolean isAddable = validateContact();
            if (isAddable) {
                add.setText("Add to Card Holder");
            } else {
                add.setText("View Card");
            }
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAddable) {
                        addContact();
                    } else {
                        Intent intent = new Intent(SharedCardViewActivity.this, OwnCardViewActivity.class);
                        intent.putExtra("card_id", cardId);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SharedCardViewActivity.this, e);
        }
    }

    private void initView() {
        contactName = (TextView) findViewById(R.id.contact_name);
        add = (TextView) findViewById(R.id.add);
        helper = new DBHelper(this);
    }

    private boolean validateContact() {
        boolean isAddable = true;
        try {
            for (int i = 0; i < sharedContacts.size(); i++) {
                if (cardId.equalsIgnoreCase(sharedContacts.get(i).getCardID())) {
                    isAddable = false;
                    CommonClass.showMessageToast(this, "Contacts already added in your contact list");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SharedCardViewActivity.this, e);
        }
        return isAddable;
    }

    private void addContact() {
        CommonClass.showProgress(this);
        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<ApiStatus> addCardCall = apiInterface.addSharedCard(userId, cardId);
        addCardCall.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    CommonClass.dismissProgress();
                    ApiStatus status = response.body();
                    if (status.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        helper.insertSharedContact(userId, firstName + " " + lastName, phoneNo, cardId, companyName);
                        CommonClass.showMessageToast(SharedCardViewActivity.this, "Card added successfully in your card holder.");
                        startActivity(new Intent(SharedCardViewActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        CommonClass.showMessageToast(SharedCardViewActivity.this, "Card is not added.");
                    }
                } else {
                    CommonClass.showErrorToast(SharedCardViewActivity.this, ErrorType.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                CommonClass.showErrorToast(SharedCardViewActivity.this, ErrorType.SERVER_ERROR);
                CommonClass.dismissProgress();
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            startActivity(new Intent(SharedCardViewActivity.this, HomeActivity.class));
            finish();
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(SharedCardViewActivity.this, e);
        }
    }
}
