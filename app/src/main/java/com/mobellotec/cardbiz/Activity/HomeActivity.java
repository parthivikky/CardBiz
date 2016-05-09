package com.mobellotec.cardbiz.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Fragment.BumpFragment;
import com.mobellotec.cardbiz.Fragment.CardRadarFragment;
import com.mobellotec.cardbiz.Fragment.CardsFragment;
import com.mobellotec.cardbiz.Fragment.CardHolderFragment;
import com.mobellotec.cardbiz.Fragment.CreateGroupFragment;
import com.mobellotec.cardbiz.Fragment.GroupFragment;
import com.mobellotec.cardbiz.Utility.GPSTracker;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.Utils;

public class HomeActivity extends AppCompatActivity {
    private static int REQUEST_LOCATION = 1;
    private LinearLayout bumpContainer, nearByContainer, groupContainer, cardHolderContainer, myCardContainer;
    private ImageView btnMyCard, btnNearBy, btnCardHolder, btnGroup, btnBump;
    private TextView txtMyCard, txtTemplate, txtCardHolder, txtGroup, txtShare;
    private ImageView[] btnArray = new ImageView[5];
    private TextView[] txtArray = new TextView[5];
    private String strCurrentUserID;
    public static int menuPosition;
    private boolean doubleBackToExitPressedOnce;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {
            Helper.listActivity.add(this);
            initViews();
            if(getIntent().getBooleanExtra("is_register",false)){
                showSuccessDialog();
            }
            strCurrentUserID = AppPreference.getString(getApplicationContext(), AppPreference.USER_ID);
            AppPreference.setBoolean(getApplicationContext(), AppPreference.CARD_LOAD_SERVER, true);
            AppPreference.setBoolean(getApplicationContext(), AppPreference.GROUP_LOAD_FROM_SERVER, true);
            AppPreference.setBoolean(getApplicationContext(), AppPreference.ISCREATECARD, false);

            menuPosition = Constants.FRAGMENT_MY_CARD;
            btnMyCard.setSelected(true);
            txtMyCard.setSelected(true);
            displayView(menuPosition);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(HomeActivity.this, e);
        }
        myCardContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelector(btnMyCard, txtMyCard);
                displayView(Constants.FRAGMENT_MY_CARD);
            }
        });
        nearByContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelector(btnNearBy, txtTemplate);
                displayView(Constants.FRAGMENT_NEARBY);
            }
        });
        cardHolderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelector(btnCardHolder, txtCardHolder);
                displayView(Constants.FRAGMENT_CARD_HOLDER);
            }
        });
        groupContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelector(btnGroup, txtGroup);
                displayView(Constants.FRAGMENT_GROUP);
            }
        });
        bumpContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSelector(btnBump, txtShare);
                displayView(Constants.FRAGMENT_BUMP);
            }
        });
    }



    public void displayView(int position) {
        try {
            menuPosition = position;
            Fragment fragment = null;
            switch (menuPosition) {
                case Constants.FRAGMENT_MY_CARD:
                    fragment = new CardsFragment();
                    break;
                case Constants.FRAGMENT_CARD_HOLDER:
                    fragment = new CardHolderFragment();
                    break;
                case Constants.FRAGMENT_GROUP:
                    fragment = new GroupFragment();
                    break;
                case Constants.FRAGMENT_NEARBY:
                    fragment = new CardRadarFragment();
                    break;
                case Constants.FRAGMENT_BUMP:
                    fragment = new BumpFragment();
                    break;
                case Constants.FRAGMENT_CREATE_GROUP:
                    fragment = new CreateGroupFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putBoolean("join", false);
                    fragment.setArguments(bundle1);
                    break;
                case Constants.FRAGMENT_JOIN_GROUP:
                    fragment = new CreateGroupFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("join", true);
                    fragment.setArguments(bundle);
                    break;
                default:
                    break;
            }
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(HomeActivity.this, e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (menuPosition == Constants.FRAGMENT_MY_CARD || menuPosition == Constants.FRAGMENT_BUMP || menuPosition == Constants.FRAGMENT_CARD_HOLDER || menuPosition == Constants.FRAGMENT_GROUP || menuPosition == Constants.FRAGMENT_NEARBY) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                CommonClass.showMessageToast(getApplicationContext(), "Press again to exit");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 3000);

            /*Thread timerThread = new Thread() {
                public void run() {

                    try {
                        sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        doubleBackToExitPressedOnce = false;
                    }
                }
            };
            timerThread.start();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(HomeActivity.this, e);
        }
    }

    private void initViews() {
        myCardContainer = (LinearLayout) findViewById(R.id.myCardContainer);
        cardHolderContainer = (LinearLayout) findViewById(R.id.cardHolderContainer);
        groupContainer = (LinearLayout) findViewById(R.id.groupContainer);
        nearByContainer = (LinearLayout) findViewById(R.id.nearByContainer);
        bumpContainer = (LinearLayout) findViewById(R.id.bumpContainer);
        btnMyCard = (ImageView) findViewById(R.id.btnMyCard);
        btnNearBy = (ImageView) findViewById(R.id.btn_nearby);
        btnCardHolder = (ImageView) findViewById(R.id.btnCardHolder);
        btnGroup = (ImageView) findViewById(R.id.btnGroup);
        btnBump = (ImageView) findViewById(R.id.btn_bump);
        txtMyCard = (TextView) findViewById(R.id.txtMyCard);
        txtGroup = (TextView) findViewById(R.id.txtGroup);
        txtTemplate = (TextView) findViewById(R.id.txtTemplate);
        txtCardHolder = (TextView) findViewById(R.id.txtCardHolder);
        txtShare = (TextView) findViewById(R.id.txtShare);
        btnArray[0] = btnMyCard;
        btnArray[1] = btnNearBy;
        btnArray[2] = btnCardHolder;
        btnArray[3] = btnGroup;
        btnArray[4] = btnBump;
        txtArray[0] = txtMyCard;
        txtArray[1] = txtTemplate;
        txtArray[2] = txtCardHolder;
        txtArray[3] = txtGroup;
        txtArray[4] = txtShare;
    }

    private void changeSelector(ImageView btn, TextView txt) {
        try {
            btn.setSelected(true);
            txt.setSelected(true);
            for (int i = 0; i < btnArray.length; i++) {
                if (btn.getId() != btnArray[i].getId()) {
                    btnArray[i].setSelected(false);
                    txtArray[i].setSelected(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(HomeActivity.this, e);
        }
    }

    private void showSuccessDialog() {
        final AlertDialog alert = new AlertDialog.Builder(HomeActivity.this).create();
        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
        alert.setView(view);
        TextView cancel = (TextView) view.findViewById(R.id.negative);
        TextView submit = (TextView) view.findViewById(R.id.positive);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView text = (TextView) view.findViewById(R.id.text);
        title.setText("Registered Successfully");
        text.setText("Congratulations and welcome to Cardbiz");
        cancel.setVisibility(View.GONE);
        submit.setText("Ok");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }
}
