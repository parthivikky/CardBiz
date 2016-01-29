package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.messenger.ShareToMessengerParams;
import com.facebook.share.Sharer;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.internal.ShareContentValidation;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareModel;
import com.facebook.share.model.ShareModelBuilder;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.SendButton;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Constants;

public class FacebookInviteActivity extends AppCompatActivity {

    private SendButton sendButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_invite);

        String message = "Please download the app using Google Play Store (" + Html.fromHtml(getLink(Constants.APP_GOOGLE_PLAY_STORE_URL)) +
                " )or iOS App Store(" + Html.fromHtml(getLink(Constants.APP_APPLE_STORE_URL)) + " )";

        ShareLinkContent content = new ShareLinkContent.Builder()
                .build();
        callbackManager = CallbackManager.Factory.create();
        sendButton = (SendButton)findViewById(R.id.messenger_send_button);
        sendButton.setShareContent(content);
        sendButton.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    private String getLink(String url) {
        if (url != null)
            return "<a href=\"" + url + "\">" + url + "</a>";
        return null;
    }
}
