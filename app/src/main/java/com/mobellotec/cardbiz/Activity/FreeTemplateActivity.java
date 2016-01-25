package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Adapter.FreeTemplateAdapter;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.Model.Template;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.ServerRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FreeTemplateActivity extends AppCompatActivity  {

    private ListView listView;
    private TextView txtEmptyView;
    private List<Template> templates;
    private String strImageUrl, strPrice;
    private FreeTemplateAdapter adapter;
    private String cardId, templateId;
    private TextView txtBuy;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_free_template);
        init();
        helper = new DBHelper(getApplicationContext());
        cardId = AppPreference.getString(getApplicationContext(), "card_id");
        if (Helper.isNetworkAvailable(getApplicationContext()))
            loadFreeTemplates();
        else {
            hideListView();
            CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
        }
//        txtMyCards.setOnClickListener(this);
//        txtBuy.setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    templateId = templates.get(position).getTemplateId();
                        Template template = templates.get(position);
                        Intent intent = new Intent(FreeTemplateActivity.this,CreateCardActivity.class);
                        intent.putExtra("template",template);
//                        AppPreference.setString(getApplicationContext(), AppPreference.TEMPLATE_ID, templateId);
//                        String templateImage = templates.get(position).getFilePath() + templates.get(position).getFrontImage();
//                        AppPreference.setString(getApplicationContext(), AppPreference.TEMPLATE_IMAGE, Helper.removeBackslashChar(templateImage));
                        setResult(RESULT_OK, intent);
                        finish();
                     /*else {
                        if (Helper.isNetworkAvailable(getApplicationContext())) {
                            updateTemplate();
                            AppPreference.setBoolean(getApplicationContext(), AppPreference.CARD_LOAD_SERVER, true);
                            helper.updateTemplate(cardId, templateId);
                            setResult(RESULT_OK);
                            finish();
                        } else
                            CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
//        txtBuy = (TextView) findViewById(R.id.txt_buy);
        listView = (ListView) findViewById(R.id.listView);
        txtEmptyView = (TextView) findViewById(R.id.empty_view);
    }


    private void loadFreeTemplates() {
        CommonClass.showProgress(FreeTemplateActivity.this);
        ArrayList<Parameter> parameters = new ArrayList<>();
        new ServerRequest(getApplicationContext(), ServerRequest.POST, Constants.MY_CARD_TEMPLATE, parameters, new ServerRequest.RequestListener() {
            @Override
            public void onRequestSuccess(String result) {
                CommonClass.dismissProgress();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String strStatus = jsonObject.getString("status");
                    templates = new ArrayList<Template>();
                    if (strStatus.equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        strImageUrl = jsonObject.getString("filePath");
                        JSONArray data = jsonObject.getJSONArray("data");
                        int length = data.length();
                        if (length > 0) {
                            showListView();
                            for (int i = 0; i < length; i++) {
                                strPrice = data.getJSONObject(i).getString("price");
                                if (strPrice.equalsIgnoreCase("0")) {
                                    Template template = new Template();
                                    template.setFilePath(strImageUrl);
                                    template.setTemplateId(data.getJSONObject(i).getString("templateID"));
                                    template.setFrontModel(strImageUrl + data.getJSONObject(i).getString("imageFront"));
                                    template.setBackModel(strImageUrl + data.getJSONObject(i).getString("imageBack"));
                                    JSONArray templatesArray = data.getJSONObject(i).getJSONArray("templateDetails");
                                    template.setFrontDetails(templatesArray.getJSONObject(0).toString());
                                    template.setBackDetails(templatesArray.getJSONObject(1).toString());
                                    template.setFrontImage(strImageUrl + templatesArray.getJSONObject(0).getString("backgrounImage"));
                                    template.setBackImage(strImageUrl + templatesArray.getJSONObject(1).getString("backgrounImage"));
                                    template.setName(data.getJSONObject(i).getString("name"));
                                    template.setPrice(" Select ");
                                    templates.add(template);
                                }
                                adapter = new FreeTemplateAdapter(getApplicationContext(), templates);
                               /* adapter.setApplyTemplateListener(new FreeTemplateAdapter.applyTemplateListener() {
                                    @Override
                                    public void onApplyTemplateListener(int position) {
                                        try {
                                            templateId = templates.get(position).getTemplateId();
                                            Log.i("templateId", templateId);
                                            if (AppPreference.getBoolean(getApplicationContext(), AppPreference.ISCREATECARD)) {
                                                AppPreference.setString(getApplicationContext(), AppPreference.TEMPLATE_ID, templateId);
                                                String templateImage = templates.get(position).getFilePath() + templates.get(position).getFrontImage();
                                                AppPreference.setString(getApplicationContext(), AppPreference.TEMPLATE_IMAGE, Helper.removeBackslashChar(templateImage));
                                                setResult(RESULT_OK);
                                                finish();
                                            } else {
                                                if (Helper.isNetworkAvailable(getApplicationContext())) {
                                                    updateTemplate();
                                                    AppPreference.setBoolean(getApplicationContext(), AppPreference.CARD_LOAD_SERVER, true);
                                                    helper.updateTemplate(cardId, templateId);
                                                    setResult(RESULT_OK);
                                                    finish();

                                                } else
                                                    CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });*/
                                listView.setAdapter(adapter);
                            }
                        } else {
                            hideListView();
                        }
                    } else {
                        hideListView();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestError(ErrorType error) {
                CommonClass.dismissProgress();
                CommonClass.showErrorToast(getApplicationContext(), error);
            }
        }).execute();
    }

    private void hideListView() {
        listView.setVisibility(View.GONE);
        txtEmptyView.setVisibility(View.VISIBLE);
    }

    private void showListView() {
        listView.setVisibility(View.VISIBLE);
        txtEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
