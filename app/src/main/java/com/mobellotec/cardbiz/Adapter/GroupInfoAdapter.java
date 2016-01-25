package com.mobellotec.cardbiz.Adapter;

import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobellotec.cardbiz.Activity.OwnCardViewActivity;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class GroupInfoAdapter extends ArrayAdapter<GroupInfo> {

    private Context context;
    private List<GroupInfo> groupInfo;
    private LayoutInflater inflater;
    private ArrayList<GroupInfo> sharedContacts;
    private DBHelper helper;
    private String userId;

    public GroupInfoAdapter(Context context, List<GroupInfo> groupInfo, ArrayList<GroupInfo> sharedContacts) {
        super(context, R.layout.group_info_row, groupInfo);
        this.groupInfo = groupInfo;
        this.context = context;
        this.sharedContacts = sharedContacts;
        this.userId = AppPreference.getString(this.context, AppPreference.USER_ID);
        helper = new DBHelper(context);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group_info_row, parent, false);
        } else {
            view = convertView;
        }
        viewHolder.txtName = (TextView) view.findViewById(R.id.txt_name);
        viewHolder.txtContact = (TextView) view.findViewById(R.id.txt_contact);
        viewHolder.you = (TextView) view.findViewById(R.id.you);
        viewHolder.imgAdd = (ImageView) view.findViewById(R.id.img_add);
        viewHolder.imgInfo = (ImageView) view.findViewById(R.id.img_info);
        viewHolder.txtName.setText(groupInfo.get(position).getName());
        viewHolder.txtContact.setText(groupInfo.get(position).getContactNumber());
        for (int i = 0; i < sharedContacts.size(); i++) {
            Log.i("group_card_id", "" + groupInfo.get(position).getCardID());
            if (groupInfo.get(position).getCardID() != null) {
                if (groupInfo.get(position).getCardID().equalsIgnoreCase(sharedContacts.get(i).getCardID())) {
//                if (groupInfo.get(position).getCompanyName().equalsIgnoreCase(contacts.get(i).getSuffixName())) {
                    viewHolder.contactAvailable = true;
                    break;
                }
            }
        }
        if (userId.equalsIgnoreCase(groupInfo.get(position).getUserID())) {
            viewHolder.imgInfo.setVisibility(View.GONE);
            viewHolder.imgAdd.setVisibility(View.GONE);
            viewHolder.you.setVisibility(View.VISIBLE);
        } else if (viewHolder.contactAvailable == true) {
            viewHolder.imgInfo.setVisibility(View.VISIBLE);
            viewHolder.imgAdd.setVisibility(View.GONE);
            viewHolder.you.setVisibility(View.GONE);
        } else {
            viewHolder.imgAdd.setVisibility(View.VISIBLE);
            viewHolder.imgInfo.setVisibility(View.GONE);
            viewHolder.you.setVisibility(View.GONE);
        }

        viewHolder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OwnCardViewActivity.class);
                intent.putExtra("card_id", groupInfo.get(position).getCardID());
                intent.putExtra("user_id",groupInfo.get(position).getUserID());
                context.startActivity(intent);
            }
        });

        viewHolder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateContact(groupInfo.get(position));
            }
        });
        return view;
    }

    private void validateContact(GroupInfo groupInfo) {
        if (!TextUtils.isEmpty(groupInfo.getCardID())) {
            CommonClass.showProgress(context);
            boolean isAddable = true;
            for (int i = 0; i < sharedContacts.size(); i++) {
                if (groupInfo.getCardID().equalsIgnoreCase(sharedContacts.get(i).getCardID())) {
                    isAddable = false;
                    CommonClass.showMessageToast(context, "Contacts already added in your contact list");
                    break;
                }
            }
            if (isAddable)
                addContact(groupInfo);
        } else {
            showDialog();
        }
    }

    private void addContact(final GroupInfo groupInfo) {
        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<ApiStatus> addCardCall = apiInterface.addSharedCard(userId, groupInfo.getCardID());
        addCardCall.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    CommonClass.dismissProgress();
                    ApiStatus status = response.body();
                    if (status.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        helper.insertSharedContact(groupInfo);
                        sharedContacts = helper.getSharedContact();
                        CommonClass.showMessageToast(context, "Card added successfully in your card holder.");
                        notifyDataSetChanged();
                    } else {
                        CommonClass.showMessageToast(context, "Card is not added.");
                    }
                } else {
                    CommonClass.showErrorToast(context, ErrorType.SERVER_ERROR);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                CommonClass.showErrorToast(context, ErrorType.SERVER_ERROR);
                CommonClass.dismissProgress();
            }
        });
    }

    private void showDialog() {
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    }

    public class ViewHolder {
        TextView txtName, txtContact, you;
        ImageView imgAdd, imgInfo;
        Boolean contactAvailable = false;
    }

}
