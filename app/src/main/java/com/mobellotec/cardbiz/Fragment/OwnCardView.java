package com.mobellotec.cardbiz.Fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.TemplatesDownload;
import com.mobellotec.cardbiz.Utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import me.grantland.widget.AutofitTextView;

/**
 * Created by MobelloTech on 23-10-2015.
 */
public class OwnCardView extends Fragment implements View.OnClickListener {


    private Card card;
    private FrameLayout frontLayout, backLayout;
    private ProgressBar frontProgressBar, backProgressBar;
    private TextView noCardFront, noCardBack;
    private ImageView img_call, img_sms;
    private AssetManager assetManager;
    private String[] assetList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_card_view, container, false);
        try {
            initView(view);
            card = getArguments().getParcelable("card");
            assetManager = getActivity().getAssets();
            try {
                assetList = assetManager.list("fonts");
            } catch (IOException e) {
                e.printStackTrace();
            }
            dynamicCardView(card);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
        return view;
    }

    private void initView(View view) {
        frontLayout = (FrameLayout) view.findViewById(R.id.frontContainer);
        backLayout = (FrameLayout) view.findViewById(R.id.backContainer);
        frontProgressBar = (ProgressBar) view.findViewById(R.id.front_progress_bar);
        backProgressBar = (ProgressBar) view.findViewById(R.id.back_progress_bar);
        noCardFront = (TextView) view.findViewById(R.id.no_card_front);
        noCardBack = (TextView) view.findViewById(R.id.no_card_back);
        img_call = (ImageView) view.findViewById(R.id.img_call);
        img_sms = (ImageView) view.findViewById(R.id.img_sms);
        img_call.setOnClickListener(this);
        img_sms.setOnClickListener(this);
    }

    private void dynamicCardView(Card card) {
        try {
            if (card != null) {
                designFront(card);
                designBack(card);
            } else
                CommonClass.showMessageToast(getActivity(), "No card available");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void designFront(Card card) {
        try {
            frontProgressBar.setVisibility(View.VISIBLE);
            if (card.getCardType().equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                String frontJson = card.getTempFrontDetails();
                JSONObject jsonObject = new JSONObject(frontJson);
                JSONArray label = jsonObject.getJSONArray("label");
                for (int j = 0; j < label.length(); j++) {
                    AutofitTextView textView = new AutofitTextView(getActivity());
                    String array[] = label.getString(j).split(",");
                    setTextAlignment(array, textView, card, frontLayout);
                }
            }
            String backgroundImage = card.getTemplateFront();
            if (backgroundImage.length() > 0) {
                String fileName = Helper.getFileNameFromUrl(backgroundImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    Drawable d = Helper.getBackgroundImageFromStorage(getActivity(), file.getAbsolutePath());
                    if (d != null)
                        frontLayout.setBackground(d);
                    else {
                        frontLayout.setVisibility(View.GONE);
                        noCardFront.setVisibility(View.VISIBLE);
                    }
                    frontProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplatesDownload(getActivity(), backgroundImage, fileName, frontLayout, frontProgressBar, new TemplatesDownload.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, FrameLayout frontLayout, ProgressBar progressBar) {
                            Drawable d = Helper.getBackgroundImageFromStorage(getActivity(), filePath);
                            if (d != null)
                                frontLayout.setBackground(d);
                            else {
                                frontLayout.setVisibility(View.GONE);
                                noCardFront.setVisibility(View.VISIBLE);
                            }
                            frontProgressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            } else {
                frontLayout.setVisibility(View.GONE);
                noCardFront.setVisibility(View.VISIBLE);
            }
            frontProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            frontProgressBar.setVisibility(View.GONE);
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void designBack(Card card) {
        try {
            if (card.getCardType().equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                String backJson = card.getTempBackDetails();
                JSONObject jsonObject = new JSONObject(backJson);

                JSONArray label = jsonObject.getJSONArray("label");
                for (int j = 0; j < label.length(); j++) {
                    AutofitTextView textView = new AutofitTextView(getActivity());
                    String array[] = label.getString(j).split(",");
                    setTextAlignment(array, textView, card, backLayout);
                }
            }
            String backgroundImage = card.getTemplateBack();
            if (backgroundImage.length() > 0) {
                String fileName = Helper.getFileNameFromUrl(backgroundImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    Drawable d = Helper.getBackgroundImageFromStorage(getActivity(), file.getAbsolutePath());
                    if (d != null)
                        backLayout.setBackground(d);
                    else {
                        backLayout.setVisibility(View.GONE);
                        noCardBack.setVisibility(View.VISIBLE);
                    }
                    backProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplatesDownload(getActivity(), backgroundImage, fileName, backLayout, backProgressBar, new TemplatesDownload.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, FrameLayout backLayout, ProgressBar progressBar) {
                            Drawable d = Helper.getBackgroundImageFromStorage(getActivity(), filePath);
                            if (d != null)
                                backLayout.setBackground(d);
                            else {
                                backLayout.setVisibility(View.GONE);
                                noCardBack.setVisibility(View.VISIBLE);
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            } else {
                backLayout.setVisibility(View.GONE);
                noCardBack.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            backProgressBar.setVisibility(View.GONE);
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void setImage(String[] array, FrameLayout layout, String filePath) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getPixels(getInt(array[3])), getPixels(getInt(array[4])));
            ImageView imageView = new ImageView(getActivity());
            imageView.setMaxWidth(getPixels(getInt(array[3])));
            imageView.setMaxHeight(getPixels(getInt(array[4])));
            params.topMargin = getPixels(getInt(array[2]));
            params.leftMargin = getPixels(getInt(array[1]));
            if (filePath.length() > 0) {
                Picasso.with(getActivity()).load(filePath).centerCrop().resize(getPixels(getInt(array[3])), getPixels(getInt(array[4]))).into(imageView);
            }
            imageView.setLayoutParams(params);
            layout.addView(imageView);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private void setTextAlignment(String[] array, AutofitTextView textView, Card card, FrameLayout layout) {
        try {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getPixels(getInt(array[3])), getPixels(getInt(array[4])));
            textView.setWidth(getPixels(getInt(array[3])));
            textView.setHeight(getPixels(getInt(array[4])));
            textView.setSingleLine();
            textView.setMaxTextSize(Math.round(getFloat(array[6])));
            textView.setMinTextSize(2);
            textView.setTextColor(Color.parseColor(array[7]));
            params.topMargin = getPixels(getInt(array[2]));
            params.leftMargin = getPixels(getInt(array[1]));
            String alignment = array[8];
            String address1, address2;
            address1 = card.getBlock() + " " + card.getStreet();
            address2 = card.getCity() + " " + card.getCountry() + " " + card.getPostalCode();
            switch (array[0]) {
                case "name":
                    textView.setText(card.getFirstName() + " " + card.getLastName());
                    break;
                case "firstname":
                    textView.setText(card.getFirstName());
                    break;
                case "lastname":
                    textView.setText(card.getLastName());
                    break;
                case "addressline1":
                    textView.setText(address1);
                    break;
                case "addressline2":
                    textView.setText(address2);
                    break;
                case "address":
                    textView.setText(address1 + " " + address2);
                    break;
                case "fullAddress":
                    textView.setText(address1 + " " + address2);
                    break;
                case "phoneNo":
                    textView.setText(card.getMobile());
                    break;
                case "officeLandline":
                    textView.setText(card.getLandline());
                    break;
                case "companyName":
                    textView.setText(card.getCompanyName());
                    break;
                case "email":
                    textView.setText(card.getEmail());
                    break;
                case "webAddress":
                    textView.setText(card.getWebsite());
                    break;
                case "roleInCompany":
                    textView.setText(card.getRole());
                    break;
                case "fax":
                    textView.setText(card.getFax());
                    break;
                case "companyLogo":
                    setImage(array, layout, card.getCompanyImage());
                    break;
                case "userLogo":
                    setImage(array, layout, card.getPersonImage());
                    break;
            }
            switch (alignment) {
                case "left":
                    textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//                    textView.setPadding(getPixels(5), 0, 0, 0);
                    break;
                case "right":
                    textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//                    textView.setPadding(0, 0, getPixels(5), 0);
                    break;
                case "top":
                    textView.setGravity(Gravity.TOP);
                    break;
                case "bottom":
                    textView.setGravity(Gravity.BOTTOM);
                    break;
                case "center":
                    textView.setGravity(Gravity.CENTER);
                    break;
            }
            for (String fileName : assetList) {
                if (fileName.equalsIgnoreCase(array[5].toLowerCase().replaceAll("[-+.^:,]", "") + ".ttf")) {
                    textView.setTypeface(Typeface.createFromAsset(assetManager, "fonts/" + fileName));
                } else {
                    textView.setTypeface(Typeface.create(array[5], Typeface.NORMAL));
                }
            }
            textView.setLayoutParams(params);
            layout.addView(textView, params);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(getActivity(), e);
        }
    }

    private int getPixels(int value) {
        float scale = this.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private int getInt(String value) {
        return Integer.parseInt(value);
    }

    private float getFloat(String value) {
        return Float.parseFloat(value);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.img_call:
                try {
                    Uri number = Uri.parse("tel:" + card.getMobile());
                    intent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(getActivity(), e);
                }
                break;
            case R.id.img_sms:
                try {
                    Uri uri = Uri.parse("smsto:" + card.getMobile());
                    intent = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(getActivity(), e);
                }
                break;

        }
    }
}
