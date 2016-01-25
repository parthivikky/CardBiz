package com.mobellotec.cardbiz.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Activity.GroupInfoActivity;
import com.mobellotec.cardbiz.Adapter.GroupAdapter;
import com.mobellotec.cardbiz.Model.Group;
import com.mobellotec.cardbiz.Model.GroupResult;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class GroupFragment extends Fragment implements View.OnClickListener {

    private static int IS_LEAVE_GROUP = 100;
    private GridView groupView;
    private List<Group> groupList, searchGroupList;
    private GroupAdapter adapter;
    private TextView txtEmptyView;
    private ImageView txtCreateGroup;
    private EditText edtxtSearch;
    private String strCurrentUserID, searchText;
    private RelativeLayout search_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        try {
            init(view);
            strCurrentUserID = AppPreference.getString(getActivity(), AppPreference.USER_ID);

            if (Helper.isNetworkAvailable(getActivity()))
                loadGroup();
            else
                CommonClass.showMessageToast(getActivity(), R.string.check_network);

            edtxtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    searchText = edtxtSearch.getText().toString().toLowerCase();
                    if (searchText.length() <= 0) {
                        setGroupValues();
                        adapter.notifyDataSetChanged();
                    } else {
                        searchGroupList = new ArrayList<Group>();
                        for (int i = 0; i < Helper.groupList.size(); i++) {
                            Group group = new Group();
                            String groupName = Helper.groupList.get(i).getGroupName().toLowerCase();
                            if (searchText.length() <= groupName.length()) {
                                if (!groupName.equalsIgnoreCase("Join Group")) {
                                    if (groupName.contains(searchText)) {
                                        searchGroupList.add(Helper.groupList.get(i));
                                    }
                                }
                            }
                        }
                        if (searchGroupList.size() > 0) {
                            adapter = new GroupAdapter(getActivity(), searchGroupList);
                            groupView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else
                            hideGrid();
                    }
                }
            });

            groupView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, position);
                    } else {
                        String groupID = groupList.get(position).getGroupID();
                        Intent intent = new Intent(getActivity(), GroupInfoActivity.class);
                        intent.putExtra("group_id", groupID);
                        startActivityForResult(intent, IS_LEAVE_GROUP);
                    }
                }
            });

            search_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edtxtSearch.requestFocus();
                    InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(edtxtSearch, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // We can now safely use the API we requested access to
            String groupID = groupList.get(requestCode).getGroupID();
            Intent intent = new Intent(getActivity(), GroupInfoActivity.class);
            intent.putExtra("group_id", groupID);
            startActivityForResult(intent, IS_LEAVE_GROUP);
        } else {
            // Permission was denied or request was cancelled
            Toast.makeText(getActivity(), "You can not use without storage permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadGroup() {
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<GroupResult> groupCall = apiInterface.groupList(strCurrentUserID,
                    CommonClass.convertMd5(Constants.BASE_URL + Constants.LIST_GROUP + Constants.SECRET_KEY));
            groupCall.enqueue(new Callback<GroupResult>() {
                @Override
                public void onResponse(Response<GroupResult> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        GroupResult groupResult = response.body();
                        Helper.groupList = new ArrayList<Group>();
                        if (groupResult.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            for (int i = 0; i < groupResult.getResult().size(); i++) {
                                Helper.groupList.add(groupResult.getResult().get(i));
                            }
                        }
                        if (Helper.groupList.size() > 0)
                            setGroupValues();
                        else
                            hideGrid();
                    } else {
                        hideGrid();
                        CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    t.fillInStackTrace();
                    CommonClass.dismissProgress();
                    hideGrid();
                    CommonClass.showErrorToast(getActivity().getApplicationContext(), ErrorType.SERVER_ERROR);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void setGroupValues() {
        try {
            groupList = Helper.groupList;
            if (getActivity() != null) {
                adapter = new GroupAdapter(getActivity().getApplicationContext(), groupList);
                groupView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void init(View view) {
        groupView = (GridView) view.findViewById(R.id.group_view);
        txtCreateGroup = (ImageView) view.findViewById(R.id.txt_create_group);
        txtEmptyView = (TextView) view.findViewById(R.id.empty_view);
        edtxtSearch = (EditText) view.findViewById(R.id.edtxt_search);
        search_container = (RelativeLayout) view.findViewById(R.id.search_container);
        txtCreateGroup.setOnClickListener(this);
    }

    private void hideGrid() {
        groupView.setVisibility(View.GONE);
        txtEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_create_group:
                showCreateDialog();
                break;
        }
    }

    private void showCreateDialog() {
        try {
            final Fragment fragment = new CreateGroupFragment();
            final Bundle bundle1 = new Bundle();
            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            title.setText("Private Group");
            submit.setText("Create");
            cancel.setText("Join");
            message.setText("Do you want to create a new group or join existing group?");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreference.setBoolean(getActivity(), AppPreference.GROUP_JOIN, true);
                    bundle1.putBoolean("join", true);
                    fragment.setArguments(bundle1);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppPreference.setBoolean(getActivity(), AppPreference.GROUP_JOIN, false);
                    bundle1.putBoolean("join", false);
                    fragment.setArguments(bundle1);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == IS_LEAVE_GROUP) {
                if (Helper.isNetworkAvailable(getActivity()))
                    loadGroup();
                else
                    CommonClass.showMessageToast(getActivity(), R.string.check_network);
            }
        }
    }
}
