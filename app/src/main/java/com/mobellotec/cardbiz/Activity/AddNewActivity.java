package com.mobellotec.cardbiz.Activity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.Utils;


public class AddNewActivity extends AppCompatActivity implements View.OnClickListener {

    private static int REQUEST_PHOTO = 1;
    private static int REQUEST_TEMPLATE = 2;
    private TextView takePhoto, create, txtMyCards;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_new_card);
        try {
            Helper.listActivity.add(AddNewActivity.this);
            init();
            create.setOnClickListener(this);
            takePhoto.setOnClickListener(this);
        } catch (Exception exception) {
            exception.printStackTrace();
            Utils.sendReport(AddNewActivity.this, exception);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        takePhoto = (TextView) findViewById(R.id.takePhoto);
        create = (TextView) findViewById(R.id.create);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePhoto:
                if (getApplicationContext() != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PHOTO);
                        } else {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PHOTO);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CaptureCardActivity.class);
                        startActivityForResult(intent, Constants.ACTIVITY_ADD_NEW_CARD);
                    }
                }
                break;
            case R.id.create:
                if (getApplicationContext() != null) {
                    if (ActivityCompat.checkSelfPermission(AddNewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(AddNewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddNewActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_TEMPLATE);
                    } else {
                        AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, false);
                        Intent createCard = new Intent(AddNewActivity.this, CreateCardActivity.class);
                        startActivityForResult(createCard, Constants.ACTIVITY_ADD_NEW_CARD);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent(getApplicationContext(), CaptureCardActivity.class);
                    startActivityForResult(intent, Constants.ACTIVITY_ADD_NEW_CARD);
                }
                else if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    // We can now safely use the API we requested access to
                    Intent intent = new Intent(getApplicationContext(), CaptureCardActivity.class);
                    startActivityForResult(intent, Constants.ACTIVITY_ADD_NEW_CARD);
                } else {
                    // Permission was denied or request was cancelled
                    Toast.makeText(AddNewActivity.this, "You can not take picture without permission.", Toast.LENGTH_SHORT).show();

                }
                break;
            case 2:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // We can now safely use the API we requested access to
                    AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, false);
                    Intent createCard = new Intent(AddNewActivity.this, CreateCardActivity.class);
                    startActivityForResult(createCard, Constants.ACTIVITY_ADD_NEW_CARD);
                } else {
                    // Permission was denied or request was cancelled
                    Toast.makeText(AddNewActivity.this, "You can not take picture without permission.", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.ACTIVITY_CUSTOM_CAMERA:
                    Intent crateCard = new Intent(AddNewActivity.this, CreateCardActivity.class);
                    startActivity(crateCard);
                    finish();
                    break;
                case Constants.ACTIVITY_ADD_NEW_CARD:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
