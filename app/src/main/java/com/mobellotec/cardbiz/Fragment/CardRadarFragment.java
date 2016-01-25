package com.mobellotec.cardbiz.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobellotec.cardbiz.Activity.ChooseCardActivity;
import com.mobellotec.cardbiz.Activity.OwnCardViewActivity;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.CardRadar;
import com.mobellotec.cardbiz.Utility.GPSTracker;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.MapWrapperLayout;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CardRadarFragment extends Fragment implements LocationListener {

    private static int REQUEST_LOCATION = 1;
    private TextView privatee, card_name;
    private ImageView imgRadarPhone, delete;
    private LinearLayout card_container;
    private RippleBackground radarBackground;
    private String strCurrentUserID;
    private ArrayList<CardRadar.CardRadarList> list = new ArrayList<>();
    private GoogleMap map;
    private double currentLat, currentLong;
    private GPSTracker gpsTracker;
    private DBHelper helper;
    private LatLng latLng;
    private MapWrapperLayout mapWrapperLayout;
    private ArrayList<GroupInfo> contacts = new ArrayList<>();
    private BitmapDescriptor icon_marker, current_loc_marker;
    private String strSelectedCardId, strSelectedCardName;
    private int visibility = 0;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card_radar, container, false);
        try {
            init(view);
            contacts = helper.getSharedContact();
            strCurrentUserID = AppPreference.getString(getActivity(), AppPreference.USER_ID);
            ArrayList<String> arrayList = helper.getNearByCardDetails();
            if (arrayList.size() > 0) {
                if (!TextUtils.isEmpty(arrayList.get(0))) {
                    strSelectedCardName = arrayList.get(1);
                    strSelectedCardId = arrayList.get(0);
                    visibility = Integer.parseInt(arrayList.get(2));
                    card_name.setText(strSelectedCardName);
                    card_container.setBackgroundColor(Color.parseColor("#9a59b5"));
                    delete.setVisibility(View.VISIBLE);
                }
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
            else {
                gpsTracker = new GPSTracker(getActivity());
                if (Helper.isNetworkAvailable(getActivity())) {
                    showRadarView();
                }
                currentLat = 0.0;
                currentLong = 0.0;
                if (gpsTracker.getIsGPSTrackingEnabled()) {
                    currentLat = gpsTracker.getLatitude();
                    currentLong = gpsTracker.getLongitude();
                } else {
                    showSettingsAlert();
                }
            }

            privatee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (strSelectedCardId == null || TextUtils.isEmpty(strSelectedCardId))
                        noCardDialog();
                    else {
                        if (currentLat == 0.0 || currentLong == 0.0) {
                            showSettingsAlert();
                        } else {
                            if (visibility == 1)
                                visibility = 0;
                            else
                                visibility = 1;
                            sendRadarRange(strSelectedCardId, visibility);
                        }
                    }
                }
            });

            card_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooseCard = new Intent(getActivity(), ChooseCardActivity.class);
                    startActivityForResult(chooseCard, Constants.FRAGMENT_NEARBY);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentLat == 0.0 || currentLong == 0.0) {
                        showSettingsAlert();
                    } else {
                        strSelectedCardId = null;
                        card_name.setText("Select a card");
                        card_container.setBackgroundColor(Color.parseColor("#c1392b"));
                        delete.setVisibility(View.GONE);
                        helper.clearNearBy();
                        visibility = 0;
                        sendRadarRange(strSelectedCardId, visibility);
                    }

//                dbHelper.updateSelectedCard(strGroupId,"","");
                }
            });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    boolean contactAvailable = false;
                    final int position = Integer.parseInt(marker.getSnippet());
                    Point point = map.getProjection().toScreenLocation(marker.getPosition());
                    View view = inflater.inflate(R.layout.map_info_view, null);
                    final ViewHolder viewHolder = new ViewHolder();
                    viewHolder.txtName = (TextView) view.findViewById(R.id.txt_name);
                    viewHolder.txtPhone = (TextView) view.findViewById(R.id.txt_phone);
                    viewHolder.imgAdd = (Button) view.findViewById(R.id.img_add);
                    viewHolder.imgInfo = (Button) view.findViewById(R.id.img_info);
                    viewHolder.txtName.setText(marker.getTitle());

                    if (position > -1) {
                        viewHolder.txtPhone.setText(list.get(position).getPhoneNo());
                        for (int i = 0; i < contacts.size(); i++) {
                            if (list.get(position).getCardID() != null) {
                                if (list.get(position).getCardID().equalsIgnoreCase(contacts.get(i).getCardID())) {
                                    contactAvailable = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (marker.getTitle().equalsIgnoreCase("Current Position")) {
                        viewHolder.imgInfo.setVisibility(View.GONE);
                        viewHolder.imgAdd.setVisibility(View.GONE);
                    } else if (contactAvailable == true) {
                        viewHolder.imgInfo.setVisibility(View.VISIBLE);
                        viewHolder.imgAdd.setVisibility(View.GONE);
                    } else {
                        viewHolder.imgAdd.setVisibility(View.VISIBLE);
                        viewHolder.imgInfo.setVisibility(View.GONE);
                    }

                    viewHolder.imgInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), OwnCardViewActivity.class);
                            intent.putExtra("card_id", list.get(position).getCardID());
                            intent.putExtra("user_id", list.get(position).getUserID());
                            startActivity(intent);
                        }
                    });

                    viewHolder.imgAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (list.get(position).getCardID() != null) {
                                validateContact(list.get(position), viewHolder.imgInfo, viewHolder.imgAdd);
                            } else
                                showDialog();
                        }
                    });

                    final PopupWindow popupWindow = new PopupWindow(getActivity());
                    popupWindow.setBackgroundDrawable(new ColorDrawable());
                    popupWindow.setContentView(view);
                    popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, point.x, point.y);
                    popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popupWindow.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    private void init(View view) {
        imgRadarPhone = (ImageView) view.findViewById(R.id.center_image);
        radarBackground = (RippleBackground) view.findViewById(R.id.content);
        map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
        icon_marker = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
        current_loc_marker = BitmapDescriptorFactory.fromResource(R.drawable.current_loc_near_by);
        card_name = (TextView) view.findViewById(R.id.card_name);
        privatee = (TextView) view.findViewById(R.id.privatee);
        delete = (ImageView) view.findViewById(R.id.delete);
        card_container = (LinearLayout) view.findViewById(R.id.card_container);
        helper = new DBHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
//            gpsTracker = new GPSTracker(getActivity());

        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void showDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            title.setText("No card found");
            message.setText("This person is not sharing any card at the moment.Please try again later.");
            submit.setText("Ok");
            cancel.setVisibility(View.GONE);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                gpsTracker = new GPSTracker(getActivity());
                if (Helper.isNetworkAvailable(getActivity())) {
                    showRadarView();
                }
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getActivity(),"Permission denied by the user. ",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void showSettingsAlert() {
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
        dialog.setTitle("GPS");
        dialog.setMessage("Please enable GPS Location in Settings?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
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

    private void validateContact(CardRadar.CardRadarList radarList, Button info, Button add) {
        try {
            CommonClass.showProgress(getActivity());
            boolean isAddable = true;
            for (int i = 0; i < contacts.size(); i++) {
                if (radarList.getCardID().equalsIgnoreCase(contacts.get(i).getCardID())) {
                    isAddable = false;
                    CommonClass.showMessageToast(getActivity(), "Contacts already added in your contact list");
                    break;
                }
            }
            if (isAddable)
                addContact(radarList, info, add);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void addContact(final CardRadar.CardRadarList radarList, final Button info, final Button add) {
        try {
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ApiStatus> addCardCall = apiInterface.addSharedCard(strCurrentUserID, radarList.getCardID());
            addCardCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        ApiStatus status = response.body();
                        if (status.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            info.setVisibility(View.VISIBLE);
                            add.setVisibility(View.GONE);
                            helper.insertSharedContact(radarList.getUserID(), radarList.getFirstName() + " " + radarList.getLastName(),
                                    radarList.getPhoneNo(), radarList.getCardID(), radarList.getCompanyName());
                            contacts = helper.getSharedContact();
                            CommonClass.showMessageToast(getActivity(), "Card added successfully in your card holder.");
                        } else {
                            CommonClass.showMessageToast(getActivity(), "Card is already exist or can not add this card .");
                        }
                    } else {
                        CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                    CommonClass.dismissProgress();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case Constants.FRAGMENT_NEARBY:
                    try {
                        if (currentLat == 0.0 || currentLong == 0.0) {
                            showSettingsAlert();
                        } else {
                            strSelectedCardId = data.getStringExtra(Constants.CARD_ID);
                            if (strSelectedCardId != null) {
                                strSelectedCardName = data.getStringExtra("card_name");
                                card_name.setText(strSelectedCardName);
                                card_container.setBackgroundColor(Color.parseColor("#9a59b5"));
                                delete.setVisibility(View.VISIBLE);
                                visibility = 1;
                                if (Helper.isNetworkAvailable(getActivity()))
                                    sendRadarRange(strSelectedCardId, visibility);
                                else
                                    CommonClass.showMessageToast(getActivity(), R.string.check_network);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.sendReport(getActivity(), e);
                    }
                    break;
            }
        }
    }

    private void sendRadarRange(final String card_id, final int visibility) {
        /*if (currentLat == 0.0 || currentLong == 0.0) {
            gpsTracker.showSettingsAlert();
        } else {*/
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<CardRadar> cardRadarCall = apiInterface.nearBy(strCurrentUserID, card_id, String.valueOf(5),
                    String.valueOf(currentLat), String.valueOf(currentLong), String.valueOf(visibility));
            cardRadarCall.enqueue(new Callback<CardRadar>() {
                @Override
                public void onResponse(Response<CardRadar> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        CardRadar cardRadar = response.body();
                        if (cardRadar.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            if (card_id != null || !TextUtils.isEmpty(card_id)) {
                                helper.clearNearBy();
                                helper.updateNearByCard(strSelectedCardId, strSelectedCardName, visibility);
                            }
                            if (visibility == 1) {
                                privatee.setBackground(null);
                            } else {
                                privatee.setBackgroundResource(R.drawable.nearby_private_btn_bg);
                            }
                            list.clear();
                            list = cardRadar.getResult();
                            updateMapMarker();
                            hideRadarView();
                        } else {
                            CommonClass.showMessageToast(getActivity(), "Failed to synchronize near by user");
                        }
                    } else {
                        CommonClass.showErrorToast(getActivity().getApplicationContext(), ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(getActivity().getApplicationContext(), ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
//        }
    }

    private void updateMapMarker() {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            latLng = new LatLng(currentLat, currentLong);
            map.clear();
            map.addMarker(new MarkerOptions().title("Current Position").position(latLng).snippet("-1").icon(current_loc_marker));
            builder.include(latLng);
            for (int i = 0; i < list.size(); i++) {
                latLng = new LatLng(Double.parseDouble(list.get(i).getLatPosition()), Double.parseDouble(list.get(i).getLongPosition()));
                map.addMarker(new MarkerOptions().title(list.get(i).getFirstName() + " " + list.get(i).getLastName()).position(latLng).snippet(String.valueOf(i)).icon(icon_marker));
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void showRadarView() {
        try {
            Handler handler = new Handler();
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        radarBackground.setVisibility(View.VISIBLE);
                        imgRadarPhone.setVisibility(View.VISIBLE);
                        radarBackground.startRippleAnimation();
                        sendRadarRange(strSelectedCardId, visibility);
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void hideRadarView() {
        radarBackground.stopRippleAnimation();
        radarBackground.setVisibility(View.GONE);
        imgRadarPhone.setVisibility(View.GONE);
    }

    /*private void hideListView() {
        radarBackground.stopRippleAnimation();
        radarBackground.setVisibility(View.GONE);
        imgRadarPhone.setVisibility(View.GONE);
    }*/

    @Override
    public void onLocationChanged(Location location) {
    }

    private void noCardDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            title.setText("Share");
            submit.setText("Select Card");
            message.setText("No card selected. Please select a card to show your card.");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chooseCard = new Intent(getActivity(), ChooseCardActivity.class);
                    startActivityForResult(chooseCard, Constants.FRAGMENT_NEARBY);
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }




    public class ViewHolder {
        TextView txtName, txtPhone;
        Button imgAdd;
        Button imgInfo;
    }
}
