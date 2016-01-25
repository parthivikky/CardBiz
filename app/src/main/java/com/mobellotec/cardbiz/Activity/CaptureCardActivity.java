package com.mobellotec.cardbiz.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.Utils;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CaptureCardActivity extends AppCompatActivity {

    private static int REQUEST_CAMERA = 1;
    private static final int FRONT_REQUEST_CODE = 1001;
    private static final int FRONT_CROP_REQUEST_CODE = 2001;
    private static final int BACK_REQUEST_CODE = 1002;
    private static final int BACK_CROP_REQUEST_CODE = 2002;

    private ImageView img_front, img_back;
    private TextView txt_front, txt_back, back, save;
    private String front_path = null, back_path = null;
    private Uri cameraOutputUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_card);
        try {
            Helper.listActivity.add(this);
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CaptureCardActivity.this, e);
        }

        img_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cameraOutputUri = Uri.fromFile(Helper.getOutputMediaFile());
                    Crop.pickImage(CaptureCardActivity.this, FRONT_REQUEST_CODE, cameraOutputUri);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CaptureCardActivity.this, e);
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cameraOutputUri = Uri.fromFile(Helper.getOutputMediaFile());
                    Crop.pickImage(CaptureCardActivity.this, BACK_REQUEST_CODE, cameraOutputUri);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CaptureCardActivity.this, e);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (front_path != null) {
                        AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, true);
                        Intent intent = new Intent(CaptureCardActivity.this, CreateCardActivity.class);
                        Helper.photoLists = new ArrayList<String>();
                        Helper.photoLists.add(front_path);
                        Helper.photoLists.add(back_path);
                        startActivityForResult(intent, Constants.ACTIVITY_ADD_NEW_CARD);
                    } else
                        CommonClass.showMessageToast(CaptureCardActivity.this, "Select front side of your visiting card");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CaptureCardActivity.this, e);
                }
            }
        });
    }

    private void initViews() {
        img_front = (ImageView) findViewById(R.id.img_front);
        img_back = (ImageView) findViewById(R.id.img_back);
//        txt_front = (TextView) findViewById(R.id.txt_front);
//        txt_back = (TextView) findViewById(R.id.txt_back);
        save = (TextView) findViewById(R.id.save);
        back = (TextView) findViewById(R.id.back);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }
            else if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(CaptureCardActivity.this, "You can not take picture without permission.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FRONT_REQUEST_CODE:
                    beginCrop(getPickImageResultUri(data), FRONT_CROP_REQUEST_CODE);
                    break;
                case BACK_REQUEST_CODE:
                    beginCrop(getPickImageResultUri(data), BACK_CROP_REQUEST_CODE);
                    break;
                case FRONT_CROP_REQUEST_CODE:
                    handleCrop(data, img_front, FRONT_CROP_REQUEST_CODE);
//                    txt_front.setVisibility(View.GONE);
                    break;
                case BACK_CROP_REQUEST_CODE:
                    handleCrop(data, img_back, BACK_CROP_REQUEST_CODE);
//                    txt_back.setVisibility(View.GONE);
                    break;
                case Constants.ACTIVITY_ADD_NEW_CARD:
                    setResult(RESULT_OK);
                    finish();
                    break;
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(data).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void beginCrop(Uri source, int requestCode) {
        try {
            Uri destination = Uri.fromFile(Helper.getOutputMediaFile());
            Crop.of(source, destination).start(this, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CaptureCardActivity.this, e);
        }
    }

    private void handleCrop(Intent result, ImageView imageView, int requestCode) {
        try {
            Uri uri = Crop.getOutput(result);
            Bitmap bitmap = Helper.getBitmap(uri.getPath());
            File file = Helper.getOutputMediaFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            imageView.setImageBitmap(Helper.rotateBitmap(this, file.getAbsolutePath()));
            if (requestCode == FRONT_CROP_REQUEST_CODE)
                front_path = file.getAbsolutePath();
            else if (requestCode == BACK_CROP_REQUEST_CODE)
                back_path = file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Utils.sendReport(CaptureCardActivity.this, e);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.sendReport(CaptureCardActivity.this, e);
        }
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        try {
            if (data != null) {
                String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CaptureCardActivity.this, e);
        }
        return isCamera ? cameraOutputUri : data.getData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
