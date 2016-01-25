package com.mobellotec.cardbiz.Activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mobellotec.cardbiz.Adapter.TutotialAdapter;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.Utils;
import com.viewpagerindicator.CirclePageIndicator;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private CirclePageIndicator circlePageIndicator;
    private Button skip;
    private TutotialAdapter tutotialAdapter;
    private String urlSchemeHost = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        try {
            AppPreference.setBoolean(getApplicationContext(), AppPreference.APP_INTRO, true);
            initView();
            urlSchemeHost = getIntent().getStringExtra("share");
            int[] arrayOfDrawables = new int[]{R.drawable.screen1,
                    R.drawable.screen2, R.drawable.screen3, R.drawable.screen4, R.drawable.screen5};

            tutotialAdapter = new TutotialAdapter(TutorialActivity.this, arrayOfDrawables);
            viewPager.setAdapter(tutotialAdapter);
            circlePageIndicator.setViewPager(viewPager);
            circlePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 4)
                        skip.setText("Done");
                    else
                        skip.setText("Skip");
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(TutorialActivity.this, LoginActivity.class).putExtra("share", urlSchemeHost));
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(TutorialActivity.this, e);
        }
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        skip = (Button) findViewById(R.id.btn_skip);
    }


}
