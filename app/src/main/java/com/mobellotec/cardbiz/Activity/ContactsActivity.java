package com.mobellotec.cardbiz.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.mobellotec.cardbiz.Adapter.ContactAdapter;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ContactsActivity extends AppCompatActivity {

    private static int REQUEST_READ_CONTACTS = 1;
    private static int CONTACT_INVITE_REQUEST = 2;

    private StickyListHeadersListView listView;
    private TextView invites, selectAll, clearAll;
    private DBHelper dbHelper;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        try {
            initViews();
            Boolean entered_first_time = AppPreference.getBoolean(this, "CONTACTS_FIRST_TIME");
            if (entered_first_time) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_READ_CONTACTS);
                } else {
                    new ContactInfo().execute();
                }
                AppPreference.setBoolean(this, "CONTACTS_FIRST_TIME", false);
            } else {
                adapter = new ContactAdapter(ContactsActivity.this, invites, Constants.INVITE_SMS);
                listView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(ContactsActivity.this, e);
        }

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbHelper.updateAllContactSelected(1);
                    adapter.contacts = dbHelper.getContact();
                    adapter.notifyDataSetChanged();
                    invites.setText("Invite(" + adapter.contacts.size() + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(ContactsActivity.this, e);
                }
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbHelper.updateAllContactSelected(0);
                    adapter.contacts = dbHelper.getContact();
                    adapter.notifyDataSetChanged();
                    invites.setText("Invite(0)");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(ContactsActivity.this, e);
                }
            }
        });

        invites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<Contact> selectedContacts = dbHelper.getSelectedContact();
                    String phoneNos = null;
                    boolean isFirst = true;
                    if (selectedContacts.size() > 0) {
                        for (int i = 0; i < selectedContacts.size(); i++) {
                            if (isFirst) {
                                phoneNos = selectedContacts.get(i).getPhone();
                                isFirst = false;
                            } else
                                phoneNos = phoneNos + ";" + selectedContacts.get(i).getPhone();
                        }
                        sendInvites(phoneNos);
//                    showDialog("Invite Succeed","SMS will be sent to your selected contacts.");
                    } else {
                        showDialog("Invite", "Please select atleast one contacts");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(ContactsActivity.this, e);
                }
            }
        });
    }

    private void initViews() {
        listView = (StickyListHeadersListView) findViewById(R.id.listView);
        invites = (TextView) findViewById(R.id.invites);
        selectAll = (TextView) findViewById(R.id.selectAll);
        clearAll = (TextView) findViewById(R.id.clearAll);
        dbHelper = new DBHelper(this);
    }

    private void showDialog(String header, String msg) {
        try {
            final AlertDialog alert = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            title.setText(header);
            message.setText(msg);
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
            Utils.sendReport(ContactsActivity.this, e);
        }
    }

    private void sendInvites(String phoneNos) {
        try {
            String message = "Please download the app using Google Play Store (" + Html.fromHtml(getLink(Constants.APP_GOOGLE_PLAY_STORE_URL)) +
                    " )or iOS App Store(" + Html.fromHtml(getLink(Constants.APP_APPLE_STORE_URL)) + " )";
            Uri uri = Uri.parse("smsto:" + phoneNos);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", message);
            startActivityForResult(intent,CONTACT_INVITE_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(ContactsActivity.this, e);
        }
    }

    public class ContactInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                CommonClass.showProgress(ContactsActivity.this);
                CommonClass.showMessageToast(ContactsActivity.this, "Synchronizing your contacts.Please wait..");
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendReport(ContactsActivity.this, e);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                readContacts();
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendReport(ContactsActivity.this, e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                CommonClass.dismissProgress();
                adapter = new ContactAdapter(ContactsActivity.this, invites, Constants.INVITE_SMS);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendReport(ContactsActivity.this, e);
            }
        }
    }

    private void readContacts() throws Exception {
        dbHelper.deleteContact();
        String id, name, phone = null;
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.Contacts.DISPLAY_NAME};
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
        if (cur.getCount() > 0) {
            int idColumnIndex = cur.getColumnIndex(ContactsContract.Contacts._ID);
            int nameColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int hasPhoneNoIndex = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            while (cur.moveToNext()) {
                if (Integer.parseInt(cur.getString(hasPhoneNoIndex)) > 0) {
                    id = cur.getString(idColumnIndex);
                    name = cur.getString(nameColumnIndex);
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur.moveToFirst()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phone = phone.replaceAll("\\s+", "");
                        phone = phone.replaceAll("[^0-9]", "");
                    }
                    pCur.close();
                    if (phone != null || !TextUtils.isEmpty(phone)) {
                        dbHelper.insertContact(new Contact(id, name, phone, null, 0));
                    }
                }
            }
        }
        cur.close();
    }


    private String getLink(String url) {
        if (url != null)
            return "<a href=\"" + url + "\">" + url + "</a>";
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                new ContactInfo().execute();
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(ContactsActivity.this, "You can not use without permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_INVITE_REQUEST){
            dbHelper.updateAllContactSelected(0);
            adapter.contacts = dbHelper.getContact();
            adapter.notifyDataSetChanged();
            invites.setText("Invite(0)");
        }
    }

    @Override
    public void onBackPressed() {
        try {
            dbHelper.updateAllContactSelected(0);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(ContactsActivity.this, e);
        }
        super.onBackPressed();
    }
}
