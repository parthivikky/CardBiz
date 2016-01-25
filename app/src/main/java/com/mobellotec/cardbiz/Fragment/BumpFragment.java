package com.mobellotec.cardbiz.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Activity.ChooseCardActivity;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Utility.GPSTracker;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;
import com.skyfishjy.library.RippleBackground;
import com.squareup.seismic.ShakeDetector;

import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class BumpFragment extends Fragment implements ShakeDetector.Listener, View.OnClickListener {

    private static int REQUEST_LOCATION = 1;
    private static boolean bump_initialized = false;
    private SensorManager sensorManager = null;
    private ShakeDetector shakeDetector = null;
    private GPSTracker gpsTracker;
    private TextView card_name, start;
    private LinearLayout card_container;
    private ImageView delete, center_image, ripple_image;
    private RippleBackground rippleBackground;
    private Context context;
    private String strUserId, strCardId = null, strName, strTime, strCompanyName, strPhoneNo;
    private double latitude = 0.0, longitude = 0.0;
    private long time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bump, container, false);
        try {
            init(view);
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            shakeDetector = new ShakeDetector(this);
            strUserId = AppPreference.getString(context, AppPreference.USER_ID);
            shakeDetector.setSensitivity(ShakeDetector.SENSITIVITY_LIGHT);
            shakeDetector.stop();
            shakeDetector.start(sensorManager);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
            else {
                getCurrentLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getCurrentLocation() {
        try {
            gpsTracker = new GPSTracker(context);
            latitude = 0.0;
            longitude = 0.0;
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
            } else {
                gpsTracker.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                getCurrentLocation();
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getActivity(),"Permission denied by the user. ",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode) {
                case Constants.FRAGMENT_BUMP:
                    try {
                        strCardId = data.getStringExtra(Constants.CARD_ID);
                        if (strCardId != null) {
                            card_name.setText(data.getStringExtra("card_name"));
                            card_container.setBackgroundColor(Color.parseColor("#9a59b5"));
                            delete.setVisibility(View.VISIBLE);
//                        center_image.setBackgroundResource(R.drawable.profile);
                            center_image.setVisibility(View.VISIBLE);
                            ripple_image.setVisibility(View.GONE);
                            strName = data.getStringExtra("name");
                            strCompanyName = data.getStringExtra("companyName");
                            strPhoneNo = data.getStringExtra("phoneNo");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.sendReport(getActivity(), e);
                    }
                    break;
            }
        }
    }

    private void init(View view) {
        context = getActivity();
        rippleBackground = (RippleBackground) view.findViewById(R.id.content);
        start = (TextView) view.findViewById(R.id.start);
        card_name = (TextView) view.findViewById(R.id.card_name);
        delete = (ImageView) view.findViewById(R.id.delete);
        center_image = (ImageView) view.findViewById(R.id.center_image);
        ripple_image = (ImageView) view.findViewById(R.id.ripple_image);
        card_container = (LinearLayout) view.findViewById(R.id.card_container);
        card_container.setOnClickListener(BumpFragment.this);
        delete.setOnClickListener(this);
        start.setOnClickListener(this);
    }

    public void sendBumpData() {
        try {
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ApiStatus> bumpCall = apiInterface.sendBumpData(strUserId, strCardId, strName, strPhoneNo, strCompanyName, String.valueOf(latitude),
                    String.valueOf(longitude), strTime, AppPreference.getString(context, AppPreference.GCM_REG_ID), "android");
            bumpCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    if (response.isSuccess()) {
                        ApiStatus apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            CommonClass.showMessageToast(context, "Your bump card shared successfully. Please wait for some time to get notification");
                        }
                    } else {
                        CommonClass.showErrorToast(context, ErrorType.SERVER_ERROR);
                    }
                    shakeDetector.start(sensorManager);
                    bump_initialized = false;
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    shakeDetector.start(sensorManager);
                    bump_initialized = false;
                    CommonClass.showMessageToast(context, "Error occurred.Bump again");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void startBumpDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(context).create();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            title.setText("Start Bump");
            message.setText("Start Bump in order to start sharing the selected card.");
            cancel.setText("Cancel");
            submit.setText("Start");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shakeDetector.start(sensorManager);
                    startBump();
                    alert.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shakeDetector.start(sensorManager);
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void noCardDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(context).create();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            title.setText("Select Card");
            submit.setText("Select");
            message.setText("No card selected. Please select a card to start bump.");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shakeDetector.start(sensorManager);
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCardList();
                    shakeDetector.start(sensorManager);
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    @Override
    public void hearShake() {
        try {
            shakeDetector.stop();
            if (strCardId == null) {
                noCardDialog();
            } else if (!bump_initialized && strCardId != null) {
                startBumpDialog();
            } else {
                time = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1500);
                strTime = String.valueOf(time);
                rippleBackground.stopRippleAnimation();
                ripple_image.setVisibility(View.GONE);
                center_image.setVisibility(View.VISIBLE);
                if (Helper.isNetworkAvailable(context))
                    validate();
                else
                    CommonClass.showErrorToast(context, ErrorType.NETWORK_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void validate() {
        try {
            if (latitude == 0.0 || longitude == 0.0) {
                showSettingsAlert();
            } else {
                sendBumpData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }


    public void showSettingsAlert() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
        dialog.setTitle("GPS");
        dialog.setMessage("Please enable GPS Location in Settings?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_container:
                getCardList();
                break;
            case R.id.delete:
                try {
                    strCardId = null;
                    card_name.setText("No card Selected");
                    card_container.setBackgroundColor(Color.parseColor("#c1392b"));
                    delete.setVisibility(View.GONE);
                    center_image.setVisibility(View.VISIBLE);
                    ripple_image.setVisibility(View.GONE);
                    rippleBackground.stopRippleAnimation();
                    bump_initialized = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(getActivity(), e);
                }
                break;
            case R.id.start:
                if (strCardId != null) {
                    startBump();
                } else {
                    noCardDialog();
                }
                break;
        }
    }

    public void startBump() {
        try {
            center_image.setVisibility(View.GONE);
            ripple_image.setVisibility(View.VISIBLE);
            rippleBackground.startRippleAnimation();
            bump_initialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void getCardList() {
        try {
            Intent chooseCard = new Intent(context, ChooseCardActivity.class);
            startActivityForResult(chooseCard, Constants.FRAGMENT_BUMP);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        try {
            shakeDetector.stop();
            sensorManager = null;
            super.onDestroyView();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }


}

