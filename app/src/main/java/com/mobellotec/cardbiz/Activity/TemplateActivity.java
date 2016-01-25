package com.mobellotec.cardbiz.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mobellotec.cardbiz.Adapter.TemplateAdapter;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.Model.Template;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TemplateActivity extends AppCompatActivity {

    private ListView listView;
    private TextView txtEmptyView;
    private TemplateAdapter adapter;
    private List<Template> templates;
    private String strImageUrl, strPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        try {
            init();
            if (Helper.isNetworkAvailable(getApplicationContext()))
                loadPaidTemplates();
            else {
                hideListView();
                CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(TemplateActivity.this, e);
        }
    }


    private void init() {
        txtEmptyView = (TextView) findViewById(R.id.empty_view);
        listView = (ListView) findViewById(R.id.listView);
    }


    private void loadPaidTemplates() {
        try {
            CommonClass.showProgress(getApplicationContext());
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
                            for (int i = 0; i < length; i++) {
                                strPrice = data.getJSONObject(i).getString("price");
                                if (!strPrice.equalsIgnoreCase("0")) {
                                    Template template = new Template();
                                    template.setFilePath(strImageUrl);
                                    template.setTemplateId(data.getJSONObject(i).getString("templateID"));
                                    template.setFrontImage(data.getJSONObject(i).getString("imageFront"));
                                    template.setBackImage(data.getJSONObject(i).getString("imageBack"));
                                    template.setName(data.getJSONObject(i).getString("name"));
                                    template.setPrice(strPrice);
                                    templates.add(template);
                                }
                            }
                            if (templates.size() > 0) {
                                showListView();
                                adapter = new TemplateAdapter(getApplicationContext(), templates);
                                listView.setAdapter(adapter);
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
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(TemplateActivity.this, e);
        }
    }

    private void hideListView() {
        listView.setVisibility(View.GONE);
        txtEmptyView.setVisibility(View.VISIBLE);
    }

    private void showListView() {
        listView.setVisibility(View.VISIBLE);
        txtEmptyView.setVisibility(View.GONE);
    }
}
