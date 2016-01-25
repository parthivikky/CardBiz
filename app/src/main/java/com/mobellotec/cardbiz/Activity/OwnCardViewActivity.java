package com.mobellotec.cardbiz.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Fragment.OwnCardTextView;
import com.mobellotec.cardbiz.Fragment.OwnCardView;
import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.TemplatesDownload;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MobelloTech on 23-10-2015.
 */
public class OwnCardViewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private int isCardAvailable = 0;
    private TextView txtUserTextView, txtUserCardView, title;
    private String cardId, userId;
    private Card card;
    private ViewPager viewPager;
    private ImageView img_share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_user);
        try {
            init();
            cardId = getIntent().getStringExtra("card_id");
            userId = getIntent().getStringExtra("user_id");
            if (Helper.isNetworkAvailable(OwnCardViewActivity.this)) {
                loadCards();
            } else {
                CommonClass.showMessageToast(OwnCardViewActivity.this, R.string.check_network);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(OwnCardViewActivity.this, e);
        }
    }

    private void init() {
        txtUserTextView = (TextView) findViewById(R.id.btn_text);
        txtUserCardView = (TextView) findViewById(R.id.btn_card);
        title = (TextView) findViewById(R.id.home_title);
        txtUserCardView.setOnClickListener(this);
        txtUserTextView.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        img_share = (ImageView) findViewById(R.id.img_share);
        title.setText("Card Details");
        txtUserCardView.setBackgroundResource(R.drawable.selected_card_textview_bg);
        txtUserCardView.setTextColor(Color.parseColor("#ffffff"));
        txtUserTextView.setBackgroundResource(R.drawable.unseleceted_card_cardview_bg);
        txtUserTextView.setTextColor(Color.parseColor("#66cc33"));
        img_share.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_text:
                viewPager.setCurrentItem(0);
                break;
            case R.id.btn_card:
                viewPager.setCurrentItem(1);
                break;
            case R.id.img_share:
                try {
                    String bindValue = "userID=" + userId + "&cardID=" + card.getCardId() + "&firstName=" + card.getFirstName() + "&lastName=" +
                            card.getLastName() + "&phoneNo=" + card.getMobile() + "&companyName=" + card.getCompanyName();
                    shareCard(bindValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(OwnCardViewActivity.this, e);
                }
                break;
        }
    }

    private void shareCard(String bindValue) {
        try {
            String bindUrl = "http://54.169.86.42/cardbiz/share.php?value=" + Base64.encodeToString(bindValue.getBytes("UTF-8"), Base64.NO_WRAP);
            String subject = "I would like to share my card with you using CardBiz";
            PackageManager pm = this.getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/html");
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getLink(bindUrl)));
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setType("vnd.android-dir/mms-sms");
            viewIntent.putExtra("sms_body", Html.fromHtml(getLink(bindUrl)));
            Intent chooserIntent = Intent.createChooser(sendIntent, "CardBiz Share");
            Spannable forEditing = new SpannableString("");
            forEditing.setSpan(new ForegroundColorSpan(Color.CYAN), 0, forEditing.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            List<ResolveInfo> resInfo = pm.queryIntentActivities(viewIntent, 0);
            Intent[] extraIntents = new Intent[resInfo.size()];
            for (int i = 0; i < resInfo.size(); i++) {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName;
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_VIEW);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("sms_body", subject + "\n" + Html.fromHtml(getLink(bindUrl)));
                CharSequence label = TextUtils.concat(ri.loadLabel(pm), forEditing);
                extraIntents[i] = new LabeledIntent(intent, packageName, label, ri.icon);
            }
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
            startActivity(chooserIntent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Utils.sendReport(OwnCardViewActivity.this, e);
        }
    }

    private String getLink(String url) {
        if (url != null)
            return "<a href=\"" + url + "\">" + url + "</a>";
        return null;
    }

    private void loadCards() {
        try {
            CommonClass.showProgress(OwnCardViewActivity.this);
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("userID", userId));
            new ServerRequest(OwnCardViewActivity.this, ServerRequest.POST, Constants.MY_CARD_LIST, parameters, new ServerRequest.RequestListener() {
                @Override
                public void onRequestSuccess(String result) {
                    try {
                        card = null;
                        JSONObject jsonCardFront, jsonCardBack;
                        JSONArray templateDetails;
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getString("status").equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                            JSONArray cardsArray = jsonObject.getJSONArray("cardInfo");
                            for (int i = 0; i < cardsArray.length(); i++) {
                                JSONObject cardInfoObject = cardsArray.getJSONObject(i);
                                if (cardInfoObject != null) {
                                    if (cardId.equalsIgnoreCase(cardInfoObject.getString("cardID"))) {
                                        isCardAvailable = 1;
                                        card = new Card();
                                        card.setCardId(cardInfoObject.getString("cardID"));
                                        String type = cardInfoObject.getString("type");
                                        card.setCardType(type);
                                        String filePath = cardInfoObject.getString("filePath");
                                        card.setFilePath(filePath);
                                        if (type.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
                                            card.setTemplateId("");
                                            card.setTemplateFront(Helper.removeBackslashChar(cardInfoObject.getString("cardFront")));
                                            card.setTemplateBack(Helper.removeBackslashChar(cardInfoObject.getString("cardBack")));
                                            jsonCardFront = new JSONObject();
                                            jsonCardBack = new JSONObject();
                                        } else {
                                            card.setTemplateId(cardInfoObject.getString("templateID"));
                                            card.setTemplateName(cardInfoObject.getString("templateName"));
                                            templateDetails = cardInfoObject.getJSONArray("templateDetails");
                                            card.setTemplateFront(Helper.removeBackslashChar(filePath + templateDetails.getJSONObject(0).getString("backgrounImage")));
                                            card.setTemplateBack(Helper.removeBackslashChar(filePath + templateDetails.getJSONObject(1).getString("backgrounImage")));
                                            jsonCardFront = templateDetails.getJSONObject(0);
                                            jsonCardBack = templateDetails.getJSONObject(1);
                                        }
                                        card.setPersonImageBitmap(downloadImage(Helper.removeBackslashChar(cardInfoObject.getString("userImage"))));
                                        card.setTempFrontDetails(jsonCardFront.toString());
                                        card.setTempBackDetails(jsonCardBack.toString());
                                        card.setFirstName(cardInfoObject.getString("firstName"));
                                        card.setLastName(cardInfoObject.getString("lastName"));
                                        card.setPersonImage(Helper.removeBackslashChar(cardInfoObject.getString("userImage")));
                                        card.setMobile(cardInfoObject.getString("phoneNo"));
                                        card.setEmail(cardInfoObject.getString("email"));
                                        card.setPersonTwitter(cardInfoObject.getString("userTwitterID"));
                                        card.setPersonFacebook(cardInfoObject.getString("userFacebookID"));
                                        card.setPersonLinkenin(cardInfoObject.getString("userLinkedInID"));
                                        card.setRole(cardInfoObject.getString("designation"));
                                        card.setCompanyName(cardInfoObject.getString("companyName"));
                                        card.setLandline(cardInfoObject.getString("companyPhoneNo"));
                                        card.setOfficeMail(cardInfoObject.getString("companyEmail"));
                                        card.setFax(cardInfoObject.getString("companyFax"));
                                        card.setWebsite(cardInfoObject.getString("companyWebsite"));
                                        card.setCompanyFacebook(cardInfoObject.getString("companyFacebookID"));
                                        card.setCompanyLinkedin(cardInfoObject.getString("companyLinkedInID"));
                                        card.setCompanyTwitter(cardInfoObject.getString("companyTwitterID"));
                                        card.setCompanyLogoBitmap(downloadImage(Helper.removeBackslashChar(cardInfoObject.getString("companyLogo"))));
                                        card.setCompanyImage(Helper.removeBackslashChar(cardInfoObject.getString("companyLogo")));
                                        card.setBlock(cardInfoObject.getString("block"));
                                        card.setStreet(cardInfoObject.getString("streetName"));
                                        card.setCity(cardInfoObject.getString("city"));
                                        card.setCountry(cardInfoObject.getString("country"));
                                        card.setPostalCode(cardInfoObject.getString("postCode"));
                                        card.setAboutCompany(cardInfoObject.getString("aboutCompany"));
                                        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), card));
                                        viewPager.setCurrentItem(1);
                                    }
                                }
                            }
                            if (isCardAvailable == 0) {
                                JSONObject userObject = jsonObject.getJSONObject("userInfo");
                                Intent intent = new Intent(OwnCardViewActivity.this, NoCardActivity.class);
                                intent.putExtra("name", userObject.getString("firstName") + " " + userObject.getString("lastName"));
                                intent.putExtra("email", userObject.getString("email"));
                                intent.putExtra("phone", userObject.getString("phoneNo"));
                                startActivity(intent);
                                finish();
                            }
                        }
                        CommonClass.dismissProgress();
                    } catch (JSONException e) {
                        CommonClass.dismissProgress();
                        e.printStackTrace();
                        CommonClass.showErrorToast(OwnCardViewActivity.this, ErrorType.SERVER_ERROR);
                    }
                }

                @Override
                public void onRequestError(ErrorType error) {
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(OwnCardViewActivity.this, error);
                }
            }).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(OwnCardViewActivity.this, e);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        try {
            if (position == 0) {
                txtUserCardView.setBackgroundResource(R.drawable.unseleceted_card_cardview_bg);
                txtUserCardView.setTextColor(Color.parseColor("#66cc33"));
                txtUserTextView.setBackgroundResource(R.drawable.selected_card_textview_bg);
                txtUserTextView.setTextColor(Color.parseColor("#ffffff"));
            } else {
                txtUserTextView.setBackgroundResource(R.drawable.unselected_card_textview_bg);
                txtUserTextView.setTextColor(Color.parseColor("#66cc33"));
                txtUserCardView.setBackgroundResource(R.drawable.seleceted_card_cardview_bg);
                txtUserCardView.setTextColor(Color.parseColor("#ffffff"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(OwnCardViewActivity.this, e);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private Bitmap downloadImage(String backgroundImage) {
        final Bitmap[] bitmap = {null};
        try {
            if (backgroundImage.length() > 0) {
                String fileName = Helper.getFileNameFromUrl(backgroundImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    bitmap[0] = Helper.getBitmap(file.getAbsolutePath());
                    return bitmap[0];
                } else {
                    new TemplatesDownload(OwnCardViewActivity.this, fileName, backgroundImage, new TemplatesDownload.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, FrameLayout frontLayout, ProgressBar progressBar) {
                            bitmap[0] = Helper.getBitmap(filePath);
                        }
                    }).execute();
                    return bitmap[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(OwnCardViewActivity.this, e);
        }
        return null;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragments;
        Card card;

        public MyPagerAdapter(FragmentManager fm, Card card) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.card = card;
            fragments.add(new OwnCardTextView());
            fragments.add(new OwnCardView());
        }

        @Override
        public Fragment getItem(int pos) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("card", card);
            Fragment fragment = fragments.get(pos);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
