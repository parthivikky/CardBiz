package com.mobellotec.cardbiz.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Activity.NoCardActivity;
import com.mobellotec.cardbiz.Activity.OwnCardViewActivity;
import com.mobellotec.cardbiz.Adapter.CardHolderAdapter;
import com.mobellotec.cardbiz.Adapter.ContactAdapter;
import com.mobellotec.cardbiz.Model.AllSharedContacts;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.Model.ContactsResult;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class CardHolderFragment extends Fragment {

    private ArrayList<GroupInfo> sharedContacts = new ArrayList<>();
    private ArrayList<GroupInfo> sharedSearchContacts = new ArrayList<>();
    private ListView contactListView;
    private DBHelper helper;
    private EditText edtxtSearch;
    private CardHolderAdapter adapter;
    private TextView emptyView;
    private String searchText, userId;
    private RelativeLayout search_container;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        try {
            init(view);
            helper = new DBHelper(getActivity());
            userId = AppPreference.getString(getActivity(), AppPreference.USER_ID);
            searchText = "";
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
                        sharedContacts = helper.getSharedContact();
                        adapter = new CardHolderAdapter(sharedContacts, getActivity());
                        if (sharedContacts.size() > 0) {
                            showContacts();
                        } else {
                            showEmptyView();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        sharedSearchContacts = new ArrayList<>();
                        for (int i = 0; i < sharedContacts.size(); i++) {
                            String contactName = sharedContacts.get(i).getName().toLowerCase();
                            if (searchText.length() <= contactName.length()) {
                                if (contactName.startsWith(searchText)) {
                                    sharedSearchContacts.add(sharedContacts.get(i));
                                }
                            }
                        }
                        adapter = new CardHolderAdapter(sharedContacts, getActivity());
                        if (sharedSearchContacts.size() > 0) {
                            showContacts();
                        } else {
                            showEmptyView();
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });

            contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, position);
                    } else {
                        itemClicked(position);
                    }
                }
            });

            contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (searchText.length() <= 0) {
                        showDialog(sharedContacts.get(position).getCardID());
                    } else {
                        showDialog(sharedSearchContacts.get(position).getCardID());
                    }
                    return true;
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
            itemClicked(requestCode);
        } else {
            // Permission was denied or request was cancelled
            Toast.makeText(getActivity(), "You can not use without the storage permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void itemClicked(int position){
        String card_id, user_id, phone, name;
        if (searchText.length() <= 0) {
            card_id = sharedContacts.get(position).getCardID();
            user_id = sharedContacts.get(position).getUserID();
            phone = sharedContacts.get(position).getContactNumber();
            name = sharedContacts.get(position).getName();
        } else {
            card_id = sharedSearchContacts.get(position).getCardID();
            user_id = sharedSearchContacts.get(position).getUserID();
            phone = sharedSearchContacts.get(position).getContactNumber();
            name = sharedSearchContacts.get(position).getName();
        }
        Intent intent;
        if (card_id != null || !TextUtils.isEmpty(card_id)) {
            intent = new Intent(getActivity(), OwnCardViewActivity.class);
            intent.putExtra("card_id", card_id);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        } else {
            intent = new Intent(getActivity(), NoCardActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("phNumber", phone);
            startActivity(intent);
        }
    }

    private void init(View view) {
        contactListView = (ListView) view.findViewById(R.id.listView);
        edtxtSearch = (EditText) view.findViewById(R.id.edtxt_search);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        search_container = (RelativeLayout) view.findViewById(R.id.search_container);
    }

    private void showDialog(final String card_id) {
        try {
            final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            title.setText("Delete");
            message.setText("Do you want to delete this card holder?");
            submit.setText("Yes");
            cancel.setText("No");
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.isNetworkAvailable(getActivity()))
                        deleteCardHolder(card_id);
                    else
                        CommonClass.showMessageToast(getActivity(), R.string.check_network);
                    alert.dismiss();
                }
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void deleteCardHolder(final String cardId) {
        try {
            CommonClass.showProgress(getActivity());
            final RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<ApiStatus> deleteCall = apiInterface.deleteSharedCard(userId, cardId);
            deleteCall.enqueue(new Callback<ApiStatus>() {
                @Override
                public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        ApiStatus apiStatus = response.body();
                        if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            helper.deleteSharedCard(cardId);
                            adapter.sharedContacts = sharedContacts = helper.getSharedContact();
                            adapter.notifyDataSetChanged();
                            if (sharedContacts.size() > 0)
                                showContacts();
                            else
                                showEmptyView();
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            Boolean entered_first_time = AppPreference.getBoolean(getActivity(), "ENTERED_FIRST_TIME");
            AppPreference.setBoolean(getActivity(), "ENTERED_FIRST_TIME", false);
            if (entered_first_time) {
                if (Helper.isNetworkAvailable(getActivity())) {
                    getSharedContacts();
                } else {
                    CommonClass.showErrorToast(getActivity(), ErrorType.NETWORK_ERROR);
                    getLocalContacts();
                }
                AppPreference.setBoolean(getActivity(), "ENTERED_FIRST_TIME", false);
            } else {
                getLocalContacts();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void getLocalContacts() {
        try {
            sharedContacts = helper.getSharedContact();
            if (sharedContacts.size() > 0) {
                adapter = new CardHolderAdapter(sharedContacts, getActivity());
                contactListView.setAdapter(adapter);
                showContacts();
            } else {
                showEmptyView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void getSharedContacts() {
        try {
            CommonClass.showProgress(getActivity());
            RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
            Call<AllSharedContacts> listContactsCall = apiInterface.listSharedCard(userId);
            listContactsCall.enqueue(new Callback<AllSharedContacts>() {
                @Override
                public void onResponse(Response<AllSharedContacts> response, Retrofit retrofit) {
                    CommonClass.dismissProgress();
                    if (response.isSuccess()) {
                        AllSharedContacts allSharedContacts = response.body();
                        if (allSharedContacts.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            sharedContacts = allSharedContacts.getResult();
                            helper.clearSharedContacts();
                            helper.insertSharedContactList(sharedContacts);
                            adapter = new CardHolderAdapter(sharedContacts, getActivity());
                            contactListView.setAdapter(adapter);
                            if (sharedContacts.size() <= 0)
                                showEmptyView();
                            else
                                showContacts();
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

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        contactListView.setVisibility(View.GONE);
    }

    private void showContacts() {
        emptyView.setVisibility(View.GONE);
        contactListView.setVisibility(View.VISIBLE);
    }
}