package com.mobellotec.cardbiz.Adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
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
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.TemplateImageDownloader;
import com.mobellotec.cardbiz.Utility.TemplatesDownload;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import me.grantland.widget.AutofitTextView;

/**
 * Created by MobelloTech on 29-07-2015.
 */
public class CardsAdapter extends PagerAdapter {

    private Context context;
    private Bitmap companyLogo;
    public ArrayList<Card> cards = new ArrayList<>();
    private JSONObject jsonCardFront, jsonCardBack;
    private LayoutInflater inflater;
    private View convertView;
    private AssetManager assetManager;
    private String[] assetList;

    public CardsAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assetManager = this.context.getAssets();
        try {
            assetList = assetManager.list("fonts");
        } catch (IOException e) {
            e.printStackTrace();
            Utils.sendReport(this.context,e);
        }
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final ViewHolder viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.my_cards_view, container, false);
        viewHolder.frontContainer = (FrameLayout) convertView.findViewById(R.id.frontContainer);
        viewHolder.backContainer = (FrameLayout) convertView.findViewById(R.id.backContainer);
        viewHolder.frontProgressBar = (ProgressBar) convertView.findViewById(R.id.front_progress_bar);
        viewHolder.backProgressBar = (ProgressBar) convertView.findViewById(R.id.back_progress_bar);
        viewHolder.noCardFront = (TextView) convertView.findViewById(R.id.no_card_front);
        viewHolder.noCardBack = (TextView) convertView.findViewById(R.id.no_card_back);

//            viewHolder.frontContainer.setBackground(Helper.convertBitmapToDrawable(context, cards.get(position).getTempFrontDrawable()));
//            viewHolder.backContainer.setBackground(Helper.convertBitmapToDrawable(context, cards.get(position).getTempBackDrawable()));

        designCardFront(viewHolder.frontContainer, position, viewHolder);
        designCardBack(viewHolder.backContainer, position, viewHolder);

        container.addView(convertView);
        return convertView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private void designCardFront(final FrameLayout frontContainer, int position, final ViewHolder viewHolder) {
        try {
            String backgroundImage = cards.get(position).getTemplateFront();
            if (cards.get(position).getCardType().equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                jsonCardFront = new JSONObject(cards.get(position).getTempFrontDetails());
                companyLogo = cards.get(position).getCompanyLogoBitmap();
                JSONArray label = jsonCardFront.getJSONArray("label");
                for (int j = 0; j < label.length(); j++) {
                    AutofitTextView textView = new AutofitTextView(context);
                    String array[] = label.getString(j).split(",");
                    setTextAlignment(array, textView, position, frontContainer);
                }
            }
            if (backgroundImage.length() > 0) {
                String fileName = Helper.getFileNameFromUrl(backgroundImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    Drawable d = Helper.getBackgroundImageFromStorage(context, file.getAbsolutePath());
                    if (d != null) {
                        frontContainer.setBackground(d);
                    } else {
                        frontContainer.setVisibility(View.GONE);
                        viewHolder.noCardBack.setVisibility(View.VISIBLE);
                    }
                    viewHolder.frontProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplatesDownload(context, backgroundImage, fileName, frontContainer, viewHolder.frontProgressBar, new TemplatesDownload.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, FrameLayout frontContainer, ProgressBar progressBar) {
                            Drawable d = Helper.getBackgroundImageFromStorage(context, filePath);
                            if (d != null) {
                                frontContainer.setBackground(d);
                            } else {
                                frontContainer.setVisibility(View.GONE);
                                viewHolder.noCardBack.setVisibility(View.VISIBLE);
                            }
                            viewHolder.frontProgressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            } else {
                frontContainer.setVisibility(View.GONE);
                viewHolder.noCardFront.setVisibility(View.VISIBLE);
            }
            viewHolder.frontProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context,e);
        }
    }

    private void designCardBack(final FrameLayout backContainer, int position, final ViewHolder viewHolder) {
        try {
            String backContainerImage = cards.get(position).getTemplateBack();
            if (cards.get(position).getCardType().equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                jsonCardBack = new JSONObject(cards.get(position).getTempBackDetails());
                JSONArray label = jsonCardBack.getJSONArray("label");
                companyLogo = cards.get(position).getCompanyLogoBitmap();
                for (int j = 0; j < label.length(); j++) {
                    AutofitTextView textView = new AutofitTextView(context);
                    String array[] = label.getString(j).split(",");
                    setTextAlignment(array, textView, position, backContainer);
                }
            }
            if (backContainerImage.length() > 0) {
                String fileName = Helper.getFileNameFromUrl(backContainerImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    Drawable d = Helper.getBackgroundImageFromStorage(context, file.getAbsolutePath());
                    if (d != null) {
                        backContainer.setBackground(d);
                    } else {
                        backContainer.setVisibility(View.GONE);
                        viewHolder.noCardBack.setVisibility(View.VISIBLE);
                    }
                    viewHolder.backProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplatesDownload(context, backContainerImage, fileName, backContainer, viewHolder.backProgressBar, new TemplatesDownload.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, FrameLayout backContainer, ProgressBar progressBar) {
                            Drawable d = Helper.getBackgroundImageFromStorage(context, filePath);
                            if (d != null) {
                                backContainer.setBackground(d);
                            } else {
                                backContainer.setVisibility(View.GONE);
                                viewHolder.noCardBack.setVisibility(View.VISIBLE);
                            }
                            viewHolder.backProgressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            } else {
                backContainer.setVisibility(View.GONE);
                viewHolder.noCardBack.setVisibility(View.VISIBLE);
            }
            viewHolder.backProgressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    private void setCompanyBackLogo(String[] array, FrameLayout frameContainer, String path) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getPixels(getInt(array[3])), getPixels(getInt(array[4])));
        ImageView imageView = new ImageView(context);
        imageView.setMaxWidth(getPixels(getInt(array[3])));
        imageView.setMaxHeight(getPixels(getInt(array[4])));
        params.topMargin = getPixels(getInt(array[2]));
        params.leftMargin = getPixels(getInt(array[1]));
        String fileName = Helper.getFileNameFromUrl(path);
        File file = new File(Helper.sdCardRoot + "/" + fileName);
        if (file.exists()) {
            imageView.setImageBitmap(Helper.rotateBitmap(context, file.getAbsolutePath()));
//                companyDp.setImageBitmap(getImageFromStorage(getApplicationContext(), file.getAbsolutePath()));
        } else {
            new TemplateImageDownloader(context, path, fileName, imageView, new TemplateImageDownloader.ImageLoaderListener() {
                @Override
                public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
//                        companyDp.setImageBitmap(getImageFromStorage(getApplicationContext(), filePath));
                    imageView.setImageBitmap(Helper.rotateBitmap(context, filePath));
                }
            }).execute();
        /*if (companyLogo.length() > 0) {
            Picasso.with(context).load(companyLogo).centerCrop().resize(getPixels(getInt(array[3])), getPixels(getInt(array[4]))).into(imageView);
        }*/
        }
        imageView.setLayoutParams(params);
        frameContainer.addView(imageView);
    }

    private void setTextAlignment(String[] array, AutofitTextView textView, int position, FrameLayout layout) {
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
        address1 = cards.get(position).getBlock() + " " + cards.get(position).getStreet();
        address2 = cards.get(position).getCity() + " " + cards.get(position).getCountry() + " " + cards.get(position).getPostalCode();

        switch (array[0]) {
            case "name":
                textView.setText(cards.get(position).getFirstName() + " " + cards.get(position).getLastName());
                break;
            case "firstname":
                textView.setText(cards.get(position).getFirstName());
                break;
            case "lastname":
                textView.setText(cards.get(position).getLastName());
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
                textView.setText(cards.get(position).getMobile());
                break;
            case "officeLandline":
                textView.setText(cards.get(position).getLandline());
                break;
            case "companyName":
                textView.setText(cards.get(position).getCompanyName());
                break;
            case "email":
                textView.setText(cards.get(position).getEmail());
                break;
            case "webAddress":
                textView.setText(cards.get(position).getWebsite());
                break;
            case "roleInCompany":
                textView.setText(cards.get(position).getRole());
                break;
            case "fax":
                textView.setText(cards.get(position).getFax());
                break;
            case "companyLogo":
                setCompanyBackLogo(array, layout, cards.get(position).getCompanyImage());
                break;
            case "userLogo":
                setCompanyBackLogo(array, layout, cards.get(position).getPersonImage());
                break;
        }
        switch (alignment) {
            case "left":
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
//                textView.setPadding(getPixels(5), 0, 0, 0);
                break;
            case "right":
                textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//                textView.setPadding(0, 0, getPixels(5), 0);
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
    }

    private int getPixels(int value) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private int getInt(String value) {
        return Integer.parseInt(value);
    }

    private float getFloat(String value) {
        return Float.parseFloat(value);
    }

    public class ViewHolder {
        private FrameLayout frontContainer, backContainer;
        private ProgressBar frontProgressBar, backProgressBar;
        private TextView noCardFront, noCardBack;
    }

}
