package com.mobellotec.cardbiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.Utils;

import java.util.ArrayList;

public class CountryActivity extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private ListView listView;
    private EditText search;
    private ArrayList<String> searchedList;
    private String searchText;
    private RelativeLayout search_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);
        try {
            arrayList = getIntent().getStringArrayListExtra("list");
            listView = (ListView) findViewById(R.id.listView);
            search = (EditText) findViewById(R.id.edtxt_search);
            search_container = (RelativeLayout) findViewById(R.id.search_container);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CountryActivity.this, e);
        }

        search_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    search.requestFocus();
                    InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(search, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CountryActivity.this, e);
                }
            }
        });
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent();
                    if (TextUtils.isEmpty(searchText)) {
                        intent.putExtra("country", arrayList.get(position));
                    } else {
                        intent.putExtra("country", searchedList.get(position));
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CountryActivity.this, e);
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    searchText = search.getText().toString().toLowerCase();
                    if (TextUtils.isEmpty(searchText)) {
                        listView.setAdapter(new ArrayAdapter<String>(CountryActivity.this, android.R.layout.simple_list_item_1, arrayList));
                    } else {
                        searchedList = new ArrayList<String>();
                        for (int i = 0; i < arrayList.size(); i++) {
                            String contactName = arrayList.get(i).toLowerCase();
                            if (searchText.length() <= contactName.length()) {
                                if (contactName.startsWith(searchText)) {
                                    searchedList.add(arrayList.get(i));
                                }
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CountryActivity.this, android.R.layout.simple_list_item_1, searchedList);
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CountryActivity.this, e);
                }
            }
        });
    }


}
