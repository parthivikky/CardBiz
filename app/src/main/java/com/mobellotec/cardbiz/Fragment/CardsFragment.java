package com.mobellotec.cardbiz.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.mobellotec.cardbiz.Activity.AddNewActivity;
import com.mobellotec.cardbiz.Activity.CreateCardActivity;
import com.mobellotec.cardbiz.Activity.EditCardActivity;
import com.mobellotec.cardbiz.Activity.LoginActivity;
import com.mobellotec.cardbiz.Activity.SettingsActivity;
import com.mobellotec.cardbiz.Adapter.CardsAdapter;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.GPSTracker;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.RetrofitApi;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.TemplatesDownload;
import com.viewpagerindicator.CirclePageIndicatorWithTitle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class CardsFragment extends Fragment implements View.OnClickListener {

    private static int REQUEST_STORAGE = 1;
    private TextView empty_card, txtCardPosition;
    private ImageView imgAddNew, imgCardDelete, imgCardEdit, imgCardShare, img_settings;
    private DBHelper helper;
    private String strCurrentUserID, strStatus, cardID, templateID, firstName, lastName, userImage, phoneNo, email, userTwitterID, userFacebookID, userLinkedInID, designation, companyName, companyPhoneNo, companyEmail, companyFax, companyWebsite, companyTwitterID, companyFacebookID, companyLinkedInID, companyLogo, block, streetName, city, country, postCode, aboutCompany, strTemplateName, strTemplateFilePath, strTemplateFront, strTemplateBack, strCardType;
    private JSONObject jsonCardFront, jsonCardBack;
    private JSONArray cardInfo, templateDetails;
    private ArrayList<Card> cards;
    private Handler handlerThread;
    private CardsAdapter adapter;
    private ViewPager viewPager;
    private CirclePageIndicatorWithTitle circlePageIndicator;
    private LinearLayout actionLayout;
    private RelativeLayout cardContainer;
    private static int currentItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empty_cards, container, false);
        initViews(view);
        empty_card.setVisibility(View.VISIBLE);
        currentItem = 0;
        strCurrentUserID = AppPreference.getString(getActivity(), AppPreference.USER_ID);
        helper = new DBHelper(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        } else {
            if (AppPreference.getBoolean(getActivity(), AppPreference.CARD_LOAD_SERVER)) {
                currentItem = 0;
                AppPreference.setBoolean(getActivity(), AppPreference.CARD_LOAD_SERVER, false);
                if (Helper.isNetworkAvailable(getActivity())) {
                    loadMyCards();
                } else {
                    CommonClass.showMessageToast(getActivity(), R.string.check_network);
                    loadLocalCards();
                }
            } else {
                currentItem = 0;
                loadLocalCards();
            }
        }

        imgAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewIntent = new Intent(getActivity(), AddNewActivity.class);
                startActivityForResult(addNewIntent, Constants.ACTIVITY_ADD_NEW_CARD);
            }
        });

        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        return view;
    }

    private void loadLocalCards() {
        CommonClass.showProgress(getActivity());
        handlerThread = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    handlerThread.post(new Runnable() {
                        @Override
                        public void run() {
                            loadFromDB();
                            CommonClass.dismissProgress();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loadMyCards() {
        CommonClass.showProgress(getActivity());
        ArrayList<Parameter> parameters = new ArrayList<>();
        parameters.add(new Parameter("userID", AppPreference.getString(getActivity(), AppPreference.USER_ID)));

        new ServerRequest(getActivity(), ServerRequest.POST, Constants.MY_CARD_LIST, parameters, new ServerRequest.RequestListener() {
            @Override
            public void onRequestSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    strStatus = jsonObject.getString("status");
                    if (strStatus.equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        cards = new ArrayList<Card>();
                        cardInfo = jsonObject.getJSONArray("cardInfo");
                        helper.deleteAllCards();
                        if (cardInfo.length() > 0) {
                            for (int i = 0; i < cardInfo.length(); i++) {
                                Card card = new Card();
                                byte[] bytes = null;
                                strCardType = cardInfo.getJSONObject(i).getString("type");
                                strTemplateFilePath = cardInfo.getJSONObject(i).getString("filePath");
                                if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
                                    card.setTemplateId("");
                                    strTemplateFront = Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("cardFront"));
                                    strTemplateBack = Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("cardBack"));
                                    jsonCardFront = new JSONObject();
                                    jsonCardBack = new JSONObject();
                                } else if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                                    card.setTemplateId(cardInfo.getJSONObject(i).getString("templateID"));
                                    card.setTemplateName(cardInfo.getJSONObject(i).getString("templateName"));
                                    card.setTempModelFront(strTemplateFilePath + cardInfo.getJSONObject(i).getString("imageFront"));
                                    card.setTempModelBack(strTemplateFilePath + cardInfo.getJSONObject(i).getString("imageBack"));
                                    templateDetails = cardInfo.getJSONObject(i).getJSONArray("templateDetails");
                                    strTemplateFront = Helper.removeBackslashChar(strTemplateFilePath + templateDetails.getJSONObject(0).getString("backgrounImage"));
                                    strTemplateBack = Helper.removeBackslashChar(strTemplateFilePath + templateDetails.getJSONObject(1).getString("backgrounImage"));
                                    jsonCardFront = templateDetails.getJSONObject(0);
                                    jsonCardBack = templateDetails.getJSONObject(1);
                                }
//                                card.setTempFrontDrawable(downloadImage(strTemplateFront));
//                                card.setTempBackDrawable(downloadImage(strTemplateBack));
                                card.setTemplateFront(strTemplateFront);
                                card.setTemplateBack(strTemplateBack);
                                card.setCardId(cardInfo.getJSONObject(i).getString("cardID"));
                                card.setCardType(strCardType);
                                card.setFilePath(strTemplateFilePath);
                                card.setTempFrontDetails(jsonCardFront.toString());
                                card.setTempBackDetails(jsonCardBack.toString());
                                card.setFirstName(cardInfo.getJSONObject(i).getString("firstName"));
                                card.setLastName(cardInfo.getJSONObject(i).getString("lastName"));
                                card.setPersonImageBitmap(downloadImage(Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("userImage"))));
                                card.setPersonImage(Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("userImage")));
                                card.setMobile(cardInfo.getJSONObject(i).getString("phoneNo"));
                                card.setEmail(cardInfo.getJSONObject(i).getString("email"));
                                card.setPersonTwitter(cardInfo.getJSONObject(i).getString("userTwitterID"));
                                card.setPersonFacebook(cardInfo.getJSONObject(i).getString("userFacebookID"));
                                card.setPersonLinkenin(cardInfo.getJSONObject(i).getString("userLinkedInID"));
                                card.setRole(cardInfo.getJSONObject(i).getString("designation"));
                                card.setCompanyName(cardInfo.getJSONObject(i).getString("companyName"));
                                card.setLandline(cardInfo.getJSONObject(i).getString("companyPhoneNo"));
                                card.setOfficeMail(cardInfo.getJSONObject(i).getString("companyEmail"));
                                card.setFax(cardInfo.getJSONObject(i).getString("companyFax"));
                                card.setWebsite(cardInfo.getJSONObject(i).getString("companyWebsite"));
                                card.setCompanyFacebook(cardInfo.getJSONObject(i).getString("companyFacebookID"));
                                card.setCompanyLinkedin(cardInfo.getJSONObject(i).getString("companyLinkedInID"));
                                card.setCompanyTwitter(cardInfo.getJSONObject(i).getString("companyTwitterID"));
                                card.setCompanyLogoBitmap(downloadImage(Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("companyLogo"))));
                                card.setCompanyImage(Helper.removeBackslashChar(cardInfo.getJSONObject(i).getString("companyLogo")));
                                card.setBlock(cardInfo.getJSONObject(i).getString("block"));
                                card.setStreet(cardInfo.getJSONObject(i).getString("streetName"));
                                card.setCity(cardInfo.getJSONObject(i).getString("city"));
                                card.setCountry(cardInfo.getJSONObject(i).getString("country"));
                                card.setPostalCode(cardInfo.getJSONObject(i).getString("postCode"));
                                card.setAboutCompany(cardInfo.getJSONObject(i).getString("aboutCompany"));
                                cards.add(card);
                            }
                            insertCards(cards);
                        } else {
                            empty_card.setVisibility(View.VISIBLE);
                            cardContainer.setVisibility(View.GONE);
                        }
                    }
                    CommonClass.dismissProgress();
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonClass.dismissProgress();
                    CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
                }
            }

            @Override
            public void onRequestError(ErrorType error) {
                CommonClass.dismissProgress();
                CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
            }
        }).execute();
    }

    private void setCardView(ArrayList<Card> cards) {
        if (cards.size() == 0) {
            empty_card.setVisibility(View.VISIBLE);
            cardContainer.setVisibility(View.GONE);
        } else {

            adapter = new CardsAdapter(getActivity(), cards);
            viewPager.setAdapter(adapter);
            empty_card.setVisibility(View.GONE);
            cardContainer.setVisibility(View.VISIBLE);
            circlePageIndicator.setViewPager(viewPager);
            viewPager.setCurrentItem(currentItem, true);
            actionLayout.setVisibility(View.VISIBLE);
            txtCardPosition.setVisibility(View.VISIBLE);
            circlePageIndicator.setCardTitle(new CirclePageIndicatorWithTitle.onCardChanged() {
                @Override
                public void onCardChangedListenr(int position) {
                    txtCardPosition.setText("Card " + position);
                }
            });
        }

        CommonClass.dismissProgress();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                if (AppPreference.getBoolean(getActivity(), AppPreference.CARD_LOAD_SERVER)) {
                    currentItem = 0;
                    AppPreference.setBoolean(getActivity(), AppPreference.CARD_LOAD_SERVER, false);
                    if (Helper.isNetworkAvailable(getActivity())) {
                        loadMyCards();
                    } else {
                        CommonClass.showMessageToast(getActivity(), R.string.check_network);
                        loadLocalCards();
                    }
                } else {
                    currentItem = 0;
                    loadLocalCards();
                }
            } else {
                // Permission was denied or request was cancelled
                Toast.makeText(getActivity(), "You can not use without the storage permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadFromDB() {
        try {
            cards = new ArrayList<Card>();
            cards = helper.getAllCardDetails();
            if (cards.size() > 0) {
                setCardView(cards);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertCards(ArrayList<Card> cards) {
        helper.deleteAllCards();
        for (int i = 0; i < cards.size(); i++) {
            helper.insertCard(cards.get(i));
        }
        setCardView(cards);
    }

    private void initViews(View view) {
        cardContainer = (RelativeLayout) view.findViewById(R.id.card_container);
        imgAddNew = (ImageView) view.findViewById(R.id.img_add);
        img_settings = (ImageView) view.findViewById(R.id.img_settings);
        empty_card = (TextView) view.findViewById(R.id.empty_card);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        imgCardDelete = (ImageView) view.findViewById(R.id.img_card_delete);
        imgCardEdit = (ImageView) view.findViewById(R.id.img_card_edit);
        imgCardShare = (ImageView) view.findViewById(R.id.img_card_share);
        circlePageIndicator = (CirclePageIndicatorWithTitle) view.findViewById(R.id.indicator);
        txtCardPosition = (TextView) view.findViewById(R.id.txt_card_position);
        actionLayout = (LinearLayout) view.findViewById(R.id.action_layout);
        txtCardPosition.setText("Card " + 1);
        actionLayout.setVisibility(View.GONE);
        txtCardPosition.setVisibility(View.GONE);
        imgCardShare.setOnClickListener(this);
        imgCardDelete.setOnClickListener(this);
        imgCardEdit.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case Constants.ACTIVITY_FREE_TEMPLATES:
                    if (Helper.isNetworkAvailable(getActivity())) {
                        loadMyCards();
                    }
                    break;
                case Constants.ACTIVITY_ADD_NEW_CARD:
                    cards = helper.getAllCardDetails();
                    if (cards.size() == 1) {
                        setCardView(cards);
                    } else {
                        adapter.cards = cards;
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(cards.size() - 1);
                    }

                    break;
                case Constants.ACTIVITY_Edit_CARD:
                    adapter.cards = cards = helper.getAllCardDetails();
                    adapter.notifyDataSetChanged();
            }
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();
        if (AppPreference.getBoolean(getActivity(), AppPreference.LOAD_NEW_CARD)) {
            AppPreference.setBoolean(getActivity(), AppPreference.LOAD_NEW_CARD, false);
            if (Helper.isNetworkAvailable(getActivity())) {
                loadMyCards();
            } else {
                CommonClass.showMessageToast(getActivity(), R.string.check_network);
            }
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_card_share:
                shareCard(viewPager.getCurrentItem());
                break;
            case R.id.img_card_delete:
                showDialog(viewPager.getCurrentItem());
                break;
            case R.id.img_card_edit:
                getCardDetails(viewPager.getCurrentItem());
                break;
        }
    }

    private void shareCard(int currentItem) {
        String cardId = cards.get(currentItem).getCardId();
        String firstName = cards.get(currentItem).getFirstName();
        String lastName = cards.get(currentItem).getLastName();
        String phoneNo = cards.get(currentItem).getMobile();
        String companyName = cards.get(currentItem).getCompanyName();
        String bindValue = "userID=" + strCurrentUserID + "&cardID=" + cardId + "&firstName=" + firstName + "&lastName=" + lastName + "&phoneNo=" + phoneNo + "&companyName=" + companyName;
        try {
            String bindUrl = "http://54.169.86.42/cardbiz/share.php?value=" + Base64.encodeToString(bindValue.getBytes("UTF-8"), Base64.NO_WRAP);
            String subject = "I would like to share my card with you using CardBiz";
            PackageManager pm = getActivity().getPackageManager();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/html");
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            sendIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + Html.fromHtml(getLink(bindUrl)));
            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setType("vnd.android-dir/mms-sms");
            viewIntent.putExtra("sms_body", subject + "\n" + Html.fromHtml(getLink(bindUrl)));
            Intent chooserIntent = Intent.createChooser(sendIntent, "Share");
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
        }
    }

    private void getCardDetails(int position) {
        currentItem = position;
//        AppPreference.setString(getActivity(), "card_id", cards.get(position).getCardId());
//        AppPreference.setBoolean(getActivity(), AppPreference.CARD_UPDATE, true);
        Intent editCard = new Intent(getActivity(), EditCardActivity.class);
        editCard.putExtra("card", cards.get(position));
        startActivityForResult(editCard, Constants.ACTIVITY_Edit_CARD);
    }

    private void deleteCard(final String cardID, final int position) {
        helper.deleteMyCard(cardID);
        CommonClass.showProgress(getActivity());
        final RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<ApiStatus> deleteCard = apiInterface.deleteCard(cardID);
        deleteCard.enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Response<ApiStatus> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    ApiStatus apiStatus = response.body();
                    if (apiStatus.getStatus().equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                        helper.deleteCardInGroup(cardID);
                        helper.deleteMyCard(cardID);
                        adapter.cards = cards = helper.getAllCardDetails();
                        adapter.notifyDataSetChanged();
                        viewPager.invalidate();
                        if (position == 0) {
                            viewPager.setCurrentItem(0, true);
                        } else {
                            viewPager.setCurrentItem(position, true);
                        }
                        if (cards.size() == 0) {
                            empty_card.setVisibility(View.VISIBLE);
                            cardContainer.setVisibility(View.GONE);
                        }

                    }
                }
                CommonClass.dismissProgress();
            }

            @Override
            public void onFailure(Throwable t) {
                CommonClass.dismissProgress();
                CommonClass.showErrorToast(getActivity(), ErrorType.SERVER_ERROR);
            }
        });
    }

    private void showDialog(final int position) {
        final AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = (LayoutInflater) getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
        alert.setView(view);
        TextView cancel = (TextView) view.findViewById(R.id.negative);
        TextView submit = (TextView) view.findViewById(R.id.positive);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardID = cards.get(position).getCardId();
                if (Helper.isNetworkAvailable(getActivity()))
                    deleteCard(cardID, position);
                else
                    CommonClass.showMessageToast(getActivity(), R.string.check_network);
                alert.dismiss();
            }
        });
        alert.show();
    }

    private Bitmap downloadImage(String backgroundImage) {
        final Bitmap[] bitmap = {null};
        if (backgroundImage.length() > 0) {
            String fileName = Helper.getFileNameFromUrl(backgroundImage);
            File file = new File(Helper.sdCardRoot + "/" + fileName);
            if (file.exists()) {
                bitmap[0] = Helper.getBitmap(file.getAbsolutePath());
                return bitmap[0];
            } else {
                new TemplatesDownload(getActivity(), fileName, backgroundImage, new TemplatesDownload.ImageLoaderListener() {
                    @Override
                    public void onSuccess(String filePath, FrameLayout frontLayout, ProgressBar progressBar) {
                        bitmap[0] = Helper.getBitmap(filePath);
                    }
                }).execute();
                return bitmap[0];
            }
        }
        return null;
    }

    private String getLink(String url) {
        if (url != null)
            return "<a href=\"" + url + "\">" + url + "</a>";
        return null;
    }
}

