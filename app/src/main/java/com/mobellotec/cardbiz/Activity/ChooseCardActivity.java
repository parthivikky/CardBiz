package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Adapter.ChooseCardAdapter;
import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.ChooseCard;
import com.mobellotec.cardbiz.Model.GroupInfo;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseCardActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private TextView emptyView, txtBack;
    private String strStatus, strCardID, firstName, lastName, strCardType, strTemplateFilePath, templateID, strTemplateFront, strTemplateBack, strTemplateName;
    private JSONArray cardInfo;
    private List<GroupInfo> list;
    private ArrayList<Card> cards;
    private ChooseCardAdapter adapter;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_card);
        try {
            init();
            dbHelper = new DBHelper(this);
            cards = dbHelper.getAllCardDetails();
            if (cards.size() > 0) {
                hideEmptyView();
                adapter = new ChooseCardAdapter(getApplicationContext(), cards);
                listView.setAdapter(adapter);
            } else {
                showEmptyView();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(ChooseCardActivity.this, e);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent();
//                GroupInfo info = list.get(position);
                    Card card = cards.get(position);
                    intent.putExtra(Constants.CARD_ID, card.getCardId());
                    intent.putExtra("name", card.getFirstName() + " " + card.getLastName());
                    intent.putExtra("card_name", "Card " + (position + 1));
                    intent.putExtra("phoneNo", card.getMobile());
                    intent.putExtra("companyName", card.getCompanyName());
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(ChooseCardActivity.this, e);
                }
            }
        });
    }

    private void init() {
        listView = (ListView) findViewById(R.id.listView);
        txtBack = (TextView) findViewById(R.id.txt_back);
        emptyView = (TextView) findViewById(R.id.empty_view);
        txtBack.setOnClickListener(this);
    }


    /*private void loadMyCards() {
        CommonClass.showProgress(ChooseCardActivity.this);
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("userID", AppPreference.getString(getApplicationContext(), AppPreference.USER_ID)));

        new ServerRequest(getApplicationContext(), ServerRequest.POST, Constants.MY_CARD_LIST, parameters, new ServerRequest.RequestListener() {
            @Override
            public void onRequestSuccess(String result) {
                CommonClass.dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    strStatus = jsonObject.getString("status");
                    if (strStatus.equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        cardInfo = jsonObject.getJSONArray("cardInfo");
                        list = new ArrayList<GroupInfo>();
                        if (cardInfo.length() > 0) {
                            for (int i = 0; i < cardInfo.length(); i++) {
                                GroupInfo info = new GroupInfo();
                                *//*strCardType = cardInfo.getJSONObject(i).getString("type");
                                strTemplateFilePath = cardInfo.getJSONObject(i).getString("filePath");
                                AppPreference.setString(getApplicationContext(), AppPreference.FILE_PATH, strTemplateFilePath);
                                if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
                                    templateID = "Auto Card";
                                    strTemplateFront = Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("cardFront"));
                                    strTemplateBack = Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("cardBack"));
                                } else if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                                    templateID = cardInfo.getJSONObject(i).getString("templateID");
                                    strTemplateName = cardInfo.getJSONObject(i).getString("templateName");
                                    strTemplateFront = Helper.removeBackslashChar(strTemplateFilePath + cardInfo.getJSONObject(i).getString("imageFront") + "^" + i);
                                    strTemplateBack = Helper.removeBackslashChar(strTemplateFilePath + cardInfo.getJSONObject(i).getString("imageBack") + "^" + i);
                                }*//*

                                info.setCardID(cardInfo.getJSONObject(i).getString("cardID"));
                                info.setName(cardInfo.getJSONObject(i).getString("firstName") + " " + cardInfo.getJSONObject(i).getString("lastName"));
                                info.setCompanyName(cardInfo.getJSONObject(i).getString("companyName"));
                                info.setContactNumber(cardInfo.getJSONObject(i).getString("phoneNo"));
                                list.add(info);
                            }
                            if (list.size() > 0) {
                                hideEmptyView();
                                adapter = new ChooseCardAdapter(getApplicationContext(), list);
                                listView.setAdapter(adapter);
                            } else {
                                showEmptyView();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonClass.showErrorToast(ChooseCardActivity.this, ErrorType.SERVER_ERROR);
                }
            }

            @Override
            public void onRequestError(ErrorType error) {
                CommonClass.dismissProgress();
                CommonClass.showErrorToast(ChooseCardActivity.this,error);
                showEmptyView();
            }
        }).execute();
    }*/

    private void hideEmptyView() {
        listView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        listView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_back:
                finish();
                break;
        }
    }
}
