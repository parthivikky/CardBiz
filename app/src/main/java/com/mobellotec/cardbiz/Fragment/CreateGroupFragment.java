package com.mobellotec.cardbiz.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mobellotec.cardbiz.Activity.HomeActivity;
import com.mobellotec.cardbiz.Model.CreateGroup;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class CreateGroupFragment extends Fragment implements View.OnClickListener {

    private EditText edtxtGroupName, edtxtCodeOne, edtxtCodeTwo, edtxtCodeThree, edtxtCodeFour;
    private String strCurrentUserID, strStatus, strGroupName, strCodeOne, strCodeTwo, strCodeThree, strCodeFour, strCode;
    private TextView txtCreateGroup, txtBack, txt_def, home_title;
    private DBHelper dbHelper;
    //private ProgressBar progressBar; txtGroupNameValidate

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);
        try {
            strCurrentUserID = AppPreference.getString(getActivity(), AppPreference.USER_ID);
            init(view);

            edtxtCodeOne.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edtxtCodeOne.length() == 1) {
                        edtxtCodeTwo.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            edtxtCodeTwo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edtxtCodeTwo.length() == 1) {
                        edtxtCodeThree.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            edtxtCodeThree.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edtxtCodeThree.length() == 1) {
                        edtxtCodeFour.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            edtxtCodeFour.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (edtxtCodeFour.length() == 1) {
                        txtCreateGroup.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getFragmentManager().popBackStack();
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    private void init(View view) {
        txt_def = (TextView) view.findViewById(R.id.def_text);
        txtBack = (TextView) view.findViewById(R.id.txt_back);
        edtxtGroupName = (EditText) view.findViewById(R.id.edtxt_group_name);
        edtxtCodeOne = (EditText) view.findViewById(R.id.edtxt_code_one);
        edtxtCodeTwo = (EditText) view.findViewById(R.id.edtxt_code_two);
        edtxtCodeThree = (EditText) view.findViewById(R.id.edtxt_code_three);
        edtxtCodeFour = (EditText) view.findViewById(R.id.edtxt_code_four);
        txtCreateGroup = (TextView) view.findViewById(R.id.txt_create_group);
        home_title = (TextView) view.findViewById(R.id.home_title);
       /* txtGroupNameValidate = (TextView) view.findViewById(R.id.txt_group_name_validate);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);*/
        txtBack.setOnClickListener(this);
        txtCreateGroup.setOnClickListener(this);
        if (AppPreference.getBoolean(getActivity(), AppPreference.GROUP_JOIN)) {
            txtCreateGroup.setText(" Join ");
            txt_def.setText("Please ask for the group name and the code \n from the creator of the group");
            home_title.setText("Join a Group");
            //edtxtGroupName.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        } else {
            txtCreateGroup.setText(" Create ");
            txt_def.setText("Please choose an unique name and a code \n to pass it to your contacts");
            home_title.setText("Create a Group");
        }
        dbHelper = new DBHelper(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_back:
                try {
                    AppPreference.setBoolean(getActivity(), AppPreference.GROUP_LOAD_FROM_SERVER, false);
                    ((HomeActivity) getActivity()).displayView(Constants.FRAGMENT_GROUP);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(getActivity(), e);
                }
                break;
            case R.id.txt_create_group:
                try {
                    if (Helper.isNetworkAvailable(getActivity())) {
                        validate();
                    } else {
                        CommonClass.showMessageToast(getActivity(), R.string.check_network);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(getActivity(), e);
                }
                break;
        }
    }

    private void validate() {
        try {
            strCodeOne = edtxtCodeOne.getText().toString().trim();
            strCodeTwo = edtxtCodeTwo.getText().toString().trim();
            strCodeThree = edtxtCodeThree.getText().toString().trim();
            strCodeFour = edtxtCodeFour.getText().toString().trim();
            strCode = strCodeOne + strCodeTwo + strCodeThree + strCodeFour;
            strGroupName = edtxtGroupName.getText().toString().trim();
            if (strGroupName.length() <= 0) {
                CommonClass.showMessageToast(getActivity(), "Group name is required.");
            } else if (strCode.length() != 4) {
                CommonClass.showMessageToast(getActivity(), "Incorrect code! please check code.");
            } else {
                if (AppPreference.getBoolean(getActivity(), AppPreference.GROUP_JOIN)) {
                    joinGroup();
                } else {
                    createGroup();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void joinGroup() {
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<CreateGroup> joinGroupCall = apiInterface.joinGroup(strCurrentUserID, strGroupName, strCode,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.JOIN_GROUP + Constants.SECRET_KEY));
            joinGroupCall.enqueue(new Callback<CreateGroup>() {
                @Override
                public void onResponse(Response<CreateGroup> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        CreateGroup apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            CommonClass.showMessageToast(getActivity(), "Group joined succesfully");
                            AppPreference.setBoolean(getActivity(), AppPreference.GROUP_LOAD_FROM_SERVER, true);
                            dbHelper.insertGroup(apiStatus.getGroupID());
                            ((HomeActivity) getActivity()).displayView(Constants.FRAGMENT_GROUP);
                        } else {
                            CommonClass.showMessageToast(getActivity(), apiStatus.getErrorMsg());
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
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void createGroup() {
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<CreateGroup> createGroupCall = apiInterface.createGroup(strCurrentUserID, strGroupName, strCode,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.CREATE_GROUP + Constants.SECRET_KEY));
            createGroupCall.enqueue(new Callback<CreateGroup>() {
                @Override
                public void onResponse(Response<CreateGroup> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        CreateGroup apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            CommonClass.showMessageToast(getActivity(), "Group created succesfully");
                            AppPreference.setBoolean(getActivity(), AppPreference.GROUP_LOAD_FROM_SERVER, true);
                            dbHelper.insertGroup(apiStatus.getGroupID());
                            ((HomeActivity) getActivity()).displayView(Constants.FRAGMENT_GROUP);
                        } else {
                            CommonClass.showMessageToast(getActivity(), apiStatus.getErrorMsg());
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
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }
}
