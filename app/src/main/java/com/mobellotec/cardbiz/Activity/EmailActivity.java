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

import com.mobellotec.cardbiz.Adapter.ContactAdapter;
import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.Utils;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class EmailActivity extends AppCompatActivity {

    private static int REQUEST_READ_CONTACTS = 1;

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
            Boolean entered_first_time = AppPreference.getBoolean(this, "EMAIL_FIRST_TIME");
            if (entered_first_time) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                            REQUEST_READ_CONTACTS);
                } else {
                    new EmailInfo().execute();
                }
                AppPreference.setBoolean(this, "EMAIL_FIRST_TIME", false);
            } else {
                adapter = new ContactAdapter(EmailActivity.this, invites,Constants.INVITE_EMAIL);
                listView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EmailActivity.this, e);
        }

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbHelper.updateAllEmailContactSelected(1);
                    adapter.contacts = dbHelper.getEmailContact();
                    adapter.notifyDataSetChanged();
                    invites.setText("Invite(" + adapter.contacts.size() + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(EmailActivity.this, e);
                }
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbHelper.updateAllEmailContactSelected(0);
                    adapter.contacts = dbHelper.getEmailContact();
                    adapter.notifyDataSetChanged();
                    invites.setText("Invite(0)");
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(EmailActivity.this, e);
                }
            }
        });

        invites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ArrayList<Contact> selectedContacts = dbHelper.getSelectedEmailContact();
                    boolean isFirst = true;
                    String recipients = null;
                    if (selectedContacts.size() > 0) {
                        for (int i = 0; i < selectedContacts.size(); i++) {
                            if(isFirst){
                                recipients = selectedContacts.get(i).getEmail();
                                isFirst = false;
                            }else{
                                recipients = recipients + "," + selectedContacts.get(i).getEmail();
                            }
                        }
                        sendInvites(recipients);
//                    showDialog("Invite Succeed","SMS will be sent to your selected contacts.");
                    } else {
                        showDialog("Invite", "Please select atleast one contacts");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(EmailActivity.this, e);
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

    private void sendInvites(String recipients) {
        try {
            String message = "Please download the app using Google Play Store (" + Html.fromHtml(getLink(Constants.APP_GOOGLE_PLAY_STORE_URL)) +
                    " )or iOS App Store(" + Html.fromHtml(getLink(Constants.APP_APPLE_STORE_URL)) + " )";
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + recipients));
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Invite from CardBiz Android application");
            startActivity(Intent.createChooser(intent,"Select Email Client"));
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EmailActivity.this, e);
        }
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
            Utils.sendReport(EmailActivity.this, e);
        }
    }

    public class EmailInfo extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                CommonClass.showProgress(EmailActivity.this);
                CommonClass.showMessageToast(EmailActivity.this, "Synchronizing your contacts.Please wait..");
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendReport(EmailActivity.this, e);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                readEmails();
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendReport(EmailActivity.this, e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                CommonClass.dismissProgress();
                adapter = new ContactAdapter(EmailActivity.this, invites,Constants.INVITE_EMAIL);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendReport(EmailActivity.this, e);
            }
        }
    }

    private void readEmails() {
        dbHelper.deleteEmailContact();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (cur1.moveToNext()) {
                    //to get the contact names
                    String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    if (email != null || !TextUtils.isEmpty(email)) {
                        dbHelper.insertEmailContact(new Contact(id, name, null,email, 0));
                    }
                }
                cur1.close();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                new EmailInfo().execute();
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(EmailActivity.this, "You can not use without permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getLink(String url) {
        if (url != null)
            return "<a href=\"" + url + "\">" + url + "</a>";
        return null;
    }

    @Override
    public void onBackPressed() {
        try {
            dbHelper.updateAllEmailContactSelected(0);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EmailActivity.this, e);
        }
        super.onBackPressed();
    }
}