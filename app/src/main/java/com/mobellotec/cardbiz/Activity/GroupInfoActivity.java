package com.mobellotec.cardbiz.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Adapter.GroupInfoAdapter;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.Model.GroupInfoResult;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GroupInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<GroupInfo> sharedContacts = new ArrayList<>();
    private TextView txtGroupLeave, title, refresh, card_name, group_code;
    private ImageView delete;
    private String strCurrentUserID, strGroupId, strSelectedCardId = "", strGroupCode, strSelectedCardName;
    private ListView memberList;
    private LinearLayout card_container;
    private List<GroupInfo> list;
    private GroupInfoAdapter adapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        try {
            init();
            strCurrentUserID = AppPreference.getString(GroupInfoActivity.this, AppPreference.USER_ID);
            strGroupId = getIntent().getStringExtra("group_id");

            ArrayList<String> arrayList = dbHelper.getSelectedGroupDetails(strGroupId);
            if (arrayList.size() > 0) {
                if (!TextUtils.isEmpty(arrayList.get(0))) {
                    strSelectedCardName = arrayList.get(1);
                    strSelectedCardId = arrayList.get(0);
                    card_name.setText(strSelectedCardName);
                    card_container.setBackgroundColor(Color.parseColor("#9a59b5"));
                    delete.setVisibility(View.VISIBLE);
                }
            }

            sharedContacts = dbHelper.getSharedContact();
            if (Helper.isNetworkAvailable(getApplicationContext()))
                getGroupInfo(strGroupId);
            else
                CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }

    private void init() {
        title = (TextView) findViewById(R.id.home_title);
        refresh = (TextView) findViewById(R.id.refresh);
        card_name = (TextView) findViewById(R.id.card_name);
        delete = (ImageView) findViewById(R.id.delete);
        txtGroupLeave = (TextView) findViewById(R.id.txt_leave);
        group_code = (TextView) findViewById(R.id.group_code);
        card_container = (LinearLayout) findViewById(R.id.card_container);
        memberList = (ListView) findViewById(R.id.member_list);
        txtGroupLeave.setOnClickListener(this);
        group_code.setOnClickListener(this);
        refresh.setOnClickListener(this);
        card_container.setOnClickListener(this);
        delete.setOnClickListener(this);
        dbHelper = new DBHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_leave:
                showConfirmDialog();
                break;
            case R.id.card_container:
                getCardList();
                break;
            case R.id.refresh:
                try {
                    if (Helper.isNetworkAvailable(getApplicationContext()))
                        getGroupInfo(strGroupId);
                    else
                        CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(GroupInfoActivity.this, e);
                }
                break;
            case R.id.delete:
                strSelectedCardId = "";
                updateCard();
                break;
            case R.id.group_code:
                showGroupCodeDialog();
                break;
        }
    }

    private void getCardList() {
        try {
            Intent chooseCard = new Intent(GroupInfoActivity.this, ChooseCardActivity.class);
            startActivityForResult(chooseCard, Constants.FRAGMENT_GROUP);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }

    private void getGroupInfo(String groupID) {
        try {
            CommonClass.showProgress(GroupInfoActivity.this);
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<GroupInfoResult> groupInfoResultCall = apiInterface.groupInfo(strCurrentUserID,
                    groupID, CommonClass.convertMd5(Constants.BASE_URL + Constants.GROUP_INFO + Constants.SECRET_KEY));
            groupInfoResultCall.enqueue(new Callback<GroupInfoResult>() {
                @Override
                public void onResponse(Response<GroupInfoResult> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        GroupInfoResult groupInfoResult = response.body();
                        if (groupInfoResult.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            title.setText(groupInfoResult.getResult().getGroupName() + "(" + groupInfoResult.getResult().getMembersCount() + ")");
                            strGroupCode = groupInfoResult.getResult().getGroupCode();
                            list = groupInfoResult.getResult().getMembers();
                            if (list.size() > 0) {
                                adapter = new GroupInfoAdapter(GroupInfoActivity.this, list, sharedContacts);
                                memberList.setAdapter(adapter);
                            }
                        }
                    } else {
                        CommonClass.showErrorToast(GroupInfoActivity.this, ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(GroupInfoActivity.this, ErrorType.SERVER_ERROR);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }

    private void showConfirmDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = (LayoutInflater) this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView text = (TextView) view.findViewById(R.id.text);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText("Leave Group");
            text.setText("Do you really want to leave this group?");
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            cancel.setText("No");
            submit.setText("Yes");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(GroupInfoActivity.this))
                        leaveGroup();
                    else
                        CommonClass.showMessageToast(GroupInfoActivity.this, R.string.check_network);
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }

    private void showGroupCodeDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = (LayoutInflater) this.getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView text = (TextView) view.findViewById(R.id.text);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText("Group Code");
            text.setText("Your group code is " + strGroupCode);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
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
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }

    private void leaveGroup() {
        try {
            CommonClass.showProgress(GroupInfoActivity.this);
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ApiStatus> groupDeleteCall = apiInterface.groupDelete(strCurrentUserID, strGroupId,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.DELETE_GROUP + Constants.SECRET_KEY));
            groupDeleteCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        ApiStatus apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            dbHelper.deleteGroup(strGroupId);
                            setResult(RESULT_OK);
                            finish();
                        } else
                            CommonClass.showMessageToast(GroupInfoActivity.this, "Can not delete group");
                    } else {
                        CommonClass.showErrorToast(getApplicationContext(), ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    CommonClass.showErrorToast(getApplicationContext(), ErrorType.SERVER_ERROR);
                    CommonClass.dismissProgress();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.FRAGMENT_GROUP:
                    try {
                        strSelectedCardId = data.getStringExtra(Constants.CARD_ID);
                        if (!TextUtils.isEmpty(strSelectedCardId)) {
                            strSelectedCardName = data.getStringExtra("card_name");
                            if (Helper.isNetworkAvailable(getApplicationContext()))
                                updateCard();
                            else
                                CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.sendReport(GroupInfoActivity.this, e);
                    }
                    break;
            }
        }
    }

    private void updateCard() {
        try {
            CommonClass.showProgress(GroupInfoActivity.this);
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ApiStatus> apiStatusCall = apiInterface.updateCard(strCurrentUserID, strGroupId, strSelectedCardId,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.GROUP_UPDATE_CARD + Constants.SECRET_KEY));
            apiStatusCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        ApiStatus apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            if (!TextUtils.isEmpty(strSelectedCardId)) {
                                CommonClass.showMessageToast(GroupInfoActivity.this, "Selected card will be shown to group members");
                                card_name.setText(strSelectedCardName);
                                card_container.setBackgroundColor(Color.parseColor("#9a59b5"));
                                delete.setVisibility(View.VISIBLE);
                                dbHelper.updateSelectedCard(strGroupId, strSelectedCardId, strSelectedCardName);
                            } else {
                                card_name.setText("Select a card");
                                card_container.setBackgroundColor(Color.parseColor("#c1392b"));
                                delete.setVisibility(View.GONE);
                                dbHelper.updateSelectedCard(strGroupId, "", "");
                            }
                        } else {
                            CommonClass.showMessageToast(getApplicationContext(), "Failed to update your selected card.");
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.fillInStackTrace();
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(GroupInfoActivity.this, ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(GroupInfoActivity.this, e);
        }
    }
}
