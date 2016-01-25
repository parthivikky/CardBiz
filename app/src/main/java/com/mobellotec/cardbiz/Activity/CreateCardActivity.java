package com.mobellotec.cardbiz.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobellotec.cardbiz.Adapter.CustomPagerAdapter;
import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.Parameter;
import com.mobellotec.cardbiz.Model.Template;
import com.mobellotec.cardbiz.R;
import com.mobellotec.cardbiz.Utility.AppPreference;
import com.mobellotec.cardbiz.Utility.CommonClass;
import com.mobellotec.cardbiz.Utility.Constants;
import com.mobellotec.cardbiz.Utility.DBHelper;
import com.mobellotec.cardbiz.Utility.ErrorType;
import com.mobellotec.cardbiz.Utility.FileServerRequest;
import com.mobellotec.cardbiz.Utility.Helper;
import com.mobellotec.cardbiz.Utility.MultipleImageDownloader;
import com.mobellotec.cardbiz.Utility.ServerRequest;
import com.mobellotec.cardbiz.Utility.TemplateImageDownloader;
import com.mobellotec.cardbiz.Utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CreateCardActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText first_name, last_name, email, mobile, companyName, role, landline, officeMail, aboutCompany, block,
            streetName, city, postalCode, fax, website, personTwitter, personFacebook, personLinkedin, companyTwitter, companyFacebook, companyLinkedin;
    private ImageView imgTemplateType, personDp, companyDp;
    private TextView save, country, txtCancel, txtCardType, title, txt_add_photo, txt_add_logo, template_msg;
    private Bitmap personBitmap, companyBitmap;
    private ViewPager viewPager;
    private ProgressBar templateProgressBar, companyImageProgressBar, userImageProgressBar;
    private RelativeLayout countryContainer;

    private String personImagePath, companyImagePath, strPersonImage = "", templateID, strFrontDetails = "", strBackDetails = "",
            strFilePath, strCardType, strCardFrontImage = "", strCardBackImage, strTemplateImage = "";
    private ArrayList<String> countries = new ArrayList<>();

    private CustomPagerAdapter pagerAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);
        try {
            Helper.listActivity.add(CreateCardActivity.this);
            initViews();
            personImagePath = "";
            companyImagePath = "";
            dbHelper = new DBHelper(getApplicationContext());
            first_name.setText(AppPreference.getString(getApplicationContext(), AppPreference.FIRST_NAME));
            last_name.setText(AppPreference.getString(getApplicationContext(), AppPreference.LAST_NAME));
            email.setText(AppPreference.getString(getApplicationContext(), AppPreference.EMAIL));
            mobile.setText(AppPreference.getString(getApplicationContext(), AppPreference.MOBILE));
            if (AppPreference.getBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO)) {
                template_msg.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(Helper.photoLists.get(0))) {
                    strCardFrontImage = Helper.photoLists.get(0).toString();
                } else {
                    strCardFrontImage = "";
                }
                if (!TextUtils.isEmpty(Helper.photoLists.get(1))) {
                    strCardBackImage = Helper.photoLists.get(1).toString();
                } else {
                    strCardBackImage = "";
                }
                pagerAdapter = new CustomPagerAdapter(getApplicationContext(), Helper.photoLists);
                viewPager.setAdapter(pagerAdapter);
                strCardType = Constants.CARD_TYPE_AUTO;
                txtCardType.setText(" Front ");
                viewPager.setVisibility(View.VISIBLE);
                imgTemplateType.setVisibility(View.GONE);
            } else {
                template_msg.setVisibility(View.VISIBLE);
                CommonClass.showProgress(CreateCardActivity.this);
                ArrayList<Parameter> parameters = new ArrayList<>();
                new ServerRequest(getApplicationContext(), ServerRequest.POST, Constants.MY_CARD_TEMPLATE, parameters, new ServerRequest.RequestListener() {
                    @Override
                    public void onRequestSuccess(String result) {
                        CommonClass.dismissProgress();
                        try {
                            String frontImage = null, filePath = null;
                            JSONObject jsonObject = new JSONObject(result);
                            String strStatus = jsonObject.getString("status");
                            if (strStatus.equalsIgnoreCase(Constants.STATUS_SUCCESS)) {
                                strFilePath = Helper.removeBackslashChar(jsonObject.getString("filePath"));
                                JSONArray data = jsonObject.getJSONArray("data");
                                int length = data.length();
                                if (length > 0) {
                                    for (int i = 0; i < length; i++) {
                                        String strPrice = data.getJSONObject(i).getString("price");
                                        if (strPrice.equalsIgnoreCase("0")) {
                                            templateID = data.getJSONObject(i).getString("templateID");
                                            frontImage = strFilePath + data.getJSONObject(i).getString("imageFront");
                                            JSONArray jsonArray = data.getJSONObject(i).getJSONArray("templateDetails");
                                            strFrontDetails = jsonArray.getJSONObject(0).toString();
                                            strBackDetails = jsonArray.getJSONObject(1).toString();
                                            strCardFrontImage = strFilePath + jsonArray.getJSONObject(0).getString("backgrounImage");
                                            strCardBackImage = strFilePath + jsonArray.getJSONObject(1).getString("backgrounImage");
                                            break;
                                        }
                                    }
                                    strTemplateImage = frontImage;
                                    setTemplateImage(frontImage);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            CommonClass.showErrorToast(CreateCardActivity.this, ErrorType.SERVER_ERROR);
                        }
                    }

                    @Override
                    public void onRequestError(ErrorType error) {
                        CommonClass.dismissProgress();
                        CommonClass.showErrorToast(getApplicationContext(), error);
                    }
                }).execute();
                strCardType = Constants.CARD_TYPE_MANUAL;
                txtCardType.setText(" SELECT TEMPLATE ");
                viewPager.setVisibility(View.GONE);
                imgTemplateType.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }

        imgTemplateType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AppPreference.setBoolean(getApplicationContext(), AppPreference.ISCREATECARD, true);
                    Intent intent = new Intent(CreateCardActivity.this, FreeTemplateActivity.class);
                    startActivityForResult(intent, Constants.ACTIVITY_FREE_TEMPLATES);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CreateCardActivity.this, e);
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    if (position == 0) {
                        txtCardType.setText(" Front ");
                    } else if (position == 1) {
                        txtCardType.setText(" Back ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CreateCardActivity.this, e);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        parseCountry();

    }

    private void setTemplateImage(String strTemplateImage) {
        try {
            templateProgressBar.setVisibility(View.VISIBLE);
            if (strTemplateImage.length() > 0) {
                String fileName = Helper.getFileNameFromUrl(strTemplateImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    Drawable d = Helper.getBackgroundImageFromStorage(getApplicationContext(), file.getAbsolutePath());
                    imgTemplateType.setBackground(d);
                    templateProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplateImageDownloader(getApplicationContext(), strTemplateImage, fileName, imgTemplateType, templateProgressBar, new TemplateImageDownloader.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
                            Drawable d = Helper.getBackgroundImageFromStorage(getApplicationContext(), filePath);
                            imgTemplateType.setBackground(d);
                            templateProgressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            } else {
                templateProgressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    /*private void setCardValues() {
        String strCardId = AppPreference.getString(getApplicationContext(), "card_id");
        Card cards = dbHelper.getCardDetails(strCardId);
        cardId = cards.getCardId();
        Log.i("cardId", cardId);
        strCardType = cards.getCardType();
        templateID = cards.getTemplateId();
        if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
            AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, true);
            ArrayList<String> fileNameList = new ArrayList<String>();
            fileNameList.add(cards.getTemplateFront());
            fileNameList.add(cards.getTemplateBack());
            new MultipleImageDownloader(getApplicationContext(), fileNameList, new MultipleImageDownloader.ImageLoaderListener() {
                @Override
                public void onSuccess(ArrayList<String> filePath) {
                    setFrontImage(filePath);
                }
            }).execute();
        } else if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
            AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, false);
            templateID = cards.getTemplateId();
            strTemplateImage = cards.getTempModelFront();
            setTemplateImage(strTemplateImage);
        }
        first_name.setText(cards.getFirstName());
        last_name.setText(cards.getLastName());
        email.setText(cards.getEmail());
        mobile.setText(cards.getMobile());
        companyName.setText(cards.getCompanyName());
        companyName.setEnabled(false);
        role.setText(cards.getRole());
        landline.setText(cards.getLandline());
        officeMail.setText(cards.getOfficeMail());
        aboutCompany.setText(cards.getAboutCompany());
        block.setText(cards.getBlock());
        streetName.setText(cards.getStreet());
        city.setText(cards.getCity());
        country.setText(cards.getCountry());
        postalCode.setText(cards.getPostalCode());
        streetName.setText(cards.getStreet());
        fax.setText(cards.getFax());
        website.setText(cards.getWebsite());
        String strPersonImage = cards.getPersonImage();
        Log.i("strPersonImage", strPersonImage);
        if (strPersonImage.length() < 0 || strPersonImage.equalsIgnoreCase("null") || strPersonImage.equalsIgnoreCase("")) {
            personImagePath = "";
        } else {
            userImageProgressBar.setVisibility(View.VISIBLE);
            String fileName = Helper.getFileNameFromUrl(strPersonImage);
            File file = new File(Helper.sdCardRoot + "/" + fileName);
            if (file.exists()) {
//                personDp.setImageBitmap(getImageFromStorage(getApplicationContext(), file.getAbsolutePath()));
                personDp.setImageBitmap(Helper.rotateBitmap(CreateCardActivity.this, file.getAbsolutePath()));
                personDp.setBackground(null);
                userImageProgressBar.setVisibility(View.GONE);
            } else {
                new TemplateImageDownloader(getApplicationContext(), strPersonImage, fileName, personDp, userImageProgressBar, new TemplateImageDownloader.ImageLoaderListener() {
                    @Override
                    public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
                        personDp.setBackground(null);
                        personDp.setImageBitmap(Helper.rotateBitmap(CreateCardActivity.this, filePath));
//                        personDp.setImageBitmap(getImageFromStorage(getApplicationContext(), filePath));
                        personImagePath = filePath;
                        userImageProgressBar.setVisibility(View.GONE);
                    }
                }).execute();
            }
        }
        String strCompanyDP = cards.getCompanyImage();
        if (strCompanyDP.length() < 0 || strCompanyDP.equalsIgnoreCase("null") || strCompanyDP.equalsIgnoreCase("")) {
            companyImagePath = "";
        } else {
            companyImageProgressBar.setVisibility(View.VISIBLE);
            String fileName = Helper.getFileNameFromUrl(strCompanyDP);
            File file = new File(Helper.sdCardRoot + "/" + fileName);
            if (file.exists()) {
                companyDp.setImageBitmap(Helper.rotateBitmap(CreateCardActivity.this, file.getAbsolutePath()));
//                companyDp.setImageBitmap(getImageFromStorage(getApplicationContext(), file.getAbsolutePath()));
                companyDp.setBackground(null);
                companyImageProgressBar.setVisibility(View.GONE);
            } else {
                new TemplateImageDownloader(getApplicationContext(), strCompanyDP, fileName, companyDp, companyImageProgressBar, new TemplateImageDownloader.ImageLoaderListener() {
                    @Override
                    public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
                        companyDp.setBackground(null);
//                        companyDp.setImageBitmap(getImageFromStorage(getApplicationContext(), filePath));
                        companyDp.setImageBitmap(Helper.rotateBitmap(CreateCardActivity.this, filePath));
                        companyImagePath = filePath;
                        companyImageProgressBar.setVisibility(View.GONE);
                    }
                }).execute();
            }
        }
        personFacebook.setText(cards.getPersonFacebook());
        personTwitter.setText(cards.getPersonTwitter());
        personLinkedin.setText(cards.getPersonLinkenin());
        companyTwitter.setText(cards.getCompanyTwitter());
        companyFacebook.setText(cards.getCompanyFacebook());
        companyLinkedin.setText(cards.getCompanyLinkedin());
    }

    private void setFrontImage(List<String> file) {
        Helper.photoLists.add(0, file.get(0));
        Helper.photoLists.add(1, file.get(1));
        if (Helper.photoLists.get(0).toString().length() > 0) {
            strCardFrontImage = Helper.photoLists.get(0).toString();
        } else {
            strCardFrontImage = "";
        }
        if (Helper.photoLists.get(1).toString().length() > 0) {
            strCardBackImage = Helper.photoLists.get(1).toString();
        } else {
            strCardBackImage = "";
        }
        pagerAdapter = new CustomPagerAdapter(getApplicationContext(), Helper.photoLists);
        viewPager.setAdapter(pagerAdapter);
    }*/

    private void initViews() {
        txtCardType = (TextView) findViewById(R.id.txt_card_type);
        viewPager = (ViewPager) findViewById(R.id.pager);
        imgTemplateType = (ImageView) findViewById(R.id.img_template_type);
        templateProgressBar = (ProgressBar) findViewById(R.id.template_progress_bar);
        companyImageProgressBar = (ProgressBar) findViewById(R.id.company_image_progress_bar);
        userImageProgressBar = (ProgressBar) findViewById(R.id.user_image_progress_bar);
        save = (TextView) findViewById(R.id.save);
        txtCancel = (TextView) findViewById(R.id.cancel);
        template_msg = (TextView) findViewById(R.id.template_msg);
        first_name = (EditText) findViewById(R.id.firstName);
        last_name = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);
        companyName = (EditText) findViewById(R.id.companyName);
        role = (EditText) findViewById(R.id.role);
        landline = (EditText) findViewById(R.id.landline);
        officeMail = (EditText) findViewById(R.id.officeMail);
        aboutCompany = (EditText) findViewById(R.id.aboutCompany);
        block = (EditText) findViewById(R.id.block);
        streetName = (EditText) findViewById(R.id.streetName);
        city = (EditText) findViewById(R.id.city);
        country = (TextView) findViewById(R.id.country);
        title = (TextView) findViewById(R.id.home_title);
        postalCode = (EditText) findViewById(R.id.postalCode);
        fax = (EditText) findViewById(R.id.fax);
        website = (EditText) findViewById(R.id.website);
        personTwitter = (EditText) findViewById(R.id.personTwitter);
        personFacebook = (EditText) findViewById(R.id.personFacebook);
        personLinkedin = (EditText) findViewById(R.id.personLinkedin);
        companyDp = (ImageView) findViewById(R.id.companyDp);
        companyTwitter = (EditText) findViewById(R.id.companyTwitter);
        companyFacebook = (EditText) findViewById(R.id.companyFacebook);
        companyLinkedin = (EditText) findViewById(R.id.companyLinkedin);
        countryContainer = (RelativeLayout) findViewById(R.id.country_container);
        personDp = (ImageView) findViewById(R.id.personDp);
        personDp.setScaleType(ImageView.ScaleType.CENTER_CROP);
        companyDp.setScaleType(ImageView.ScaleType.CENTER_CROP);
        txtCancel.setOnClickListener(this);
        save.setOnClickListener(this);
        personDp.setOnClickListener(this);
        companyDp.setOnClickListener(this);
        countryContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                try {
                    if (Helper.isNetworkAvailable(getApplicationContext()))
                        validate();
                    else
                        CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CreateCardActivity.this, e);
                }
                break;
            case R.id.personDp:
                getImageFromGallery(Constants.PICK_PERSON_DP_REQUEST_CODE);
                break;
            case R.id.companyDp:
                getImageFromGallery(Constants.PICK_COMPANY_DP_REQUEST_CODE);
                break;
            case R.id.country_container:
                try {
                    Intent intent = new Intent(getApplicationContext(), CountryActivity.class);
                    intent.putStringArrayListExtra("list", countries);
                    startActivityForResult(intent, Constants.COUNTRY_REQUEST_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sendReport(CreateCardActivity.this, e);
                }
                break;
        }
    }

    private void validate() {
        try {
            if (!AppPreference.getBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO)) {
                if (templateID.equalsIgnoreCase("")) {
                    showDialog("Please select your template.");
                    return;
                }
            }
            if (TextUtils.isEmpty(first_name.getText().toString()))
                showDialog("First name should not be empty");
            else if (TextUtils.isEmpty(last_name.getText().toString()))
                showDialog("Last name should not be empty");
            else if (TextUtils.isEmpty(email.getText().toString()))
                showDialog("Email should not be empty");
            else if (!Helper.isValidEmail(email.getText().toString().trim()))
                showDialog("Invalid email address");
            else if (TextUtils.isEmpty(mobile.getText().toString()))
                showDialog("Mobile number should not be empty");
            else if (!Helper.isValidPhone(mobile.getText().toString().trim()))
                showDialog("Invalid mobile number");
            else if (TextUtils.isEmpty(companyName.getText().toString())) {
                showDialog("Company name should not be empty");
                companyName.requestFocus();
            } else if (!TextUtils.isEmpty(officeMail.getText().toString()))
                if (!Helper.isValidEmail(officeMail.getText().toString()))
                    showDialog("Invalid email address");
                else
                    createCard();
            else
                createCard();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    private void showDialog(String message) {
        try {
            final Dialog dialog = new Dialog(CreateCardActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.login_error_dialog, null);
            dialog.setContentView(view);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);
            TextView txt_msg = (TextView) view.findViewById(R.id.message);
            TextView txt_ok = (TextView) view.findViewById(R.id.ok);
            txt_msg.setText(message);
            txt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    private void getImageFromGallery(int requestCode) {
        try {
            Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            gallerypickerIntent.setType("image/*");
            startActivityForResult(gallerypickerIntent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    private void createCard() {
        try {
            CommonClass.showProgress(CreateCardActivity.this);
            ArrayList<Parameter> parameters = new ArrayList<>();
            parameters.add(new Parameter("userID", AppPreference.getString(getApplicationContext(), AppPreference.USER_ID)));
            parameters.add(new Parameter("firstName", first_name.getText().toString().trim()));
            parameters.add(new Parameter("lastName", last_name.getText().toString().trim()));
            parameters.add(new Parameter("email", email.getText().toString().trim()));
            parameters.add(new Parameter("phoneNo", mobile.getText().toString().trim()));
            parameters.add(new Parameter("companyName", companyName.getText().toString().trim()));
            parameters.add(new Parameter("designation", role.getText().toString().trim()));
            parameters.add(new Parameter("companyPhoneNo", landline.getText().toString().trim()));
            parameters.add(new Parameter("companyEmail", officeMail.getText().toString().trim()));
            parameters.add(new Parameter("companyFax", fax.getText().toString().trim()));
            parameters.add(new Parameter("companyWebsite", website.getText().toString().trim()));
            parameters.add(new Parameter("block", block.getText().toString().trim()));
            parameters.add(new Parameter("street", streetName.getText().toString().trim()));
            parameters.add(new Parameter("city", city.getText().toString().trim()));
            parameters.add(new Parameter("country", country.getText().toString().trim()));
            parameters.add(new Parameter("postCode", postalCode.getText().toString().trim()));
            parameters.add(new Parameter("aboutCompany", aboutCompany.getText().toString().trim()));
            parameters.add(new Parameter("companyFacebookID", companyFacebook.getText().toString().trim()));
            parameters.add(new Parameter("companyLinkedInID", companyLinkedin.getText().toString().trim()));
            parameters.add(new Parameter("companyTwitterID", companyTwitter.getText().toString().trim()));
            parameters.add(new Parameter("userFacebookID", personFacebook.getText().toString().trim()));
            parameters.add(new Parameter("userLinkedInID", personLinkedin.getText().toString().trim()));
            parameters.add(new Parameter("userTwitterID", personTwitter.getText().toString().trim()));
            parameters.add(new Parameter("type", strCardType));
            ArrayList<Parameter> filepaths = new ArrayList<>();
            filepaths.add(new Parameter("userfile", personImagePath));
            filepaths.add(new Parameter("companyfile", companyImagePath));
            if (AppPreference.getBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO)) {
                filepaths.add(new Parameter("cardFront", strCardFrontImage));
                filepaths.add(new Parameter("cardBack", strCardBackImage));
            } else {
                parameters.add(new Parameter("templateID", templateID));
            }
        /*if (AppPreference.getBoolean(getApplicationContext(), AppPreference.CARD_UPDATE)) {
            parameters.add(new Parameter("cardID", cardId));
            if (AppPreference.getBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO)) {
                filepaths.add(new Parameter("cardFront", strCardFrontImage));
                filepaths.add(new Parameter("cardBack", strCardBackImage));
            } else {
                parameters.add(new Parameter("templateID", templateID));
            }
        }*/
            new FileServerRequest(getApplicationContext(), FileServerRequest.POST, Constants.CREATE_CARD, parameters, filepaths, new FileServerRequest.FileRequestListener() {

                @Override
                public void onRequestSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        CommonClass.showMessageToast(getApplicationContext(), jsonObject.getString("message"));
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            String cardId = jsonObject.getString("cardID");
                            Card card = assignCard(cardId);
                            dbHelper.insertCard(card);
                            setResult(RESULT_OK);
                            finish();
                        }
                        CommonClass.dismissProgress();
                    } catch (JSONException e) {
                        CommonClass.dismissProgress();
                        CommonClass.showErrorToast(getApplicationContext(), ErrorType.SERVER_ERROR);
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
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.PICK_PERSON_DP_REQUEST_CODE:
                    previewImage(data, Constants.PICK_PERSON_DP_REQUEST_CODE);
                    break;
                case Constants.PICK_COMPANY_DP_REQUEST_CODE:
                    previewImage(data, Constants.PICK_COMPANY_DP_REQUEST_CODE);
                    break;
                case Constants.COUNTRY_REQUEST_CODE:
                    country.setText(data.getStringExtra("country"));
                    break;
                case Constants.ACTIVITY_FREE_TEMPLATES:
                    if (data != null) {
                        Template template = data.getParcelableExtra("template");
                        templateID = template.getTemplateId();
                        strTemplateImage = template.getFrontModel();
                        strFilePath = template.getFilePath();
                        strCardFrontImage = template.getFrontImage();
                        strCardBackImage = template.getBackImage();
                        strFrontDetails = template.getFrontDetails();
                        strBackDetails = template.getBackDetails();
                        setTemplateImage(strTemplateImage);
                    }
                    break;
            }
        }
    }

    private void previewImage(Intent data, int code) {
        try {
            Uri fileUri = data.getData();
            String[] path = {MediaStore.MediaColumns.DATA};
            Cursor cursor = getApplicationContext().getContentResolver().query(fileUri, path, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(path[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            File oldFile = new File(imagePath);
            Bitmap oldBitmap = Helper.rotateBitmap(CreateCardActivity.this, oldFile.getAbsolutePath());
            /*File newFile = Helper.getOutputMediaFile();
            FileOutputStream out = new FileOutputStream(newFile);
            oldBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Bitmap newBitmap = Helper.rotateBitmap(CreateCardActivity.this, newFile.getAbsolutePath());*/
            if (code == Constants.PICK_PERSON_DP_REQUEST_CODE) {
                personBitmap = oldBitmap;
                personImagePath = oldFile.getAbsolutePath();
                personDp.setImageBitmap(personBitmap);
                personDp.setBackground(null);
            } else if (code == Constants.PICK_COMPANY_DP_REQUEST_CODE) {
                companyBitmap = oldBitmap;
                companyImagePath = oldFile.getAbsolutePath();
                companyDp.setImageBitmap(companyBitmap);
                companyDp.setBackground(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    private void parseCountry() {
        try {
            InputStream is = getApplicationContext().getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            JSONArray jsonArray = new JSONArray(new String(buffer, "UTF-8"));
            for (int i = 0; i < jsonArray.length(); i++) {
                countries.add(jsonArray.getString(i));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, ex);
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this, e);
        }
    }

    private Card assignCard(String cardId) {
        Card card = new Card();
        try {
            card.setCardId(cardId);
            card.setCardType(strCardType);
            card.setFirstName(first_name.getText().toString());
            card.setLastName(last_name.getText().toString());
            card.setPersonImageBitmap(personBitmap);
            card.setMobile(mobile.getText().toString());
            card.setEmail(email.getText().toString());
            card.setPersonTwitter(personTwitter.getText().toString());
            card.setPersonFacebook(personFacebook.getText().toString());
            card.setPersonLinkenin(personLinkedin.getText().toString());
            card.setRole(role.getText().toString());
            card.setCompanyName(companyName.getText().toString());
            card.setLandline(landline.getText().toString());
            card.setOfficeMail(officeMail.getText().toString());
            card.setFax(fax.getText().toString());
            card.setWebsite(website.getText().toString());
            card.setCompanyFacebook(companyFacebook.getText().toString());
            card.setCompanyLinkedin(companyLinkedin.getText().toString());
            card.setCompanyTwitter(companyTwitter.getText().toString());
            card.setCompanyLogoBitmap(companyBitmap);
            card.setBlock(block.getText().toString());
            card.setStreet(streetName.getText().toString());
            card.setCity(city.getText().toString());
            card.setCountry(country.getText().toString());
            card.setPostalCode(postalCode.getText().toString());
            card.setAboutCompany(aboutCompany.getText().toString());
            card.setFilePath(strFilePath);
            card.setTemplateFront(strCardFrontImage);
            card.setTemplateBack(strCardBackImage);
            if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                card.setTempModelFront(strTemplateImage);
                card.setTempFrontDetails(strFrontDetails);
                card.setTempBackDetails(strBackDetails);
                card.setTemplateId(templateID);
            } else if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
                card.setTemplateId("");
            }
            card.setPersonImage(personImagePath);
            card.setCompanyImage(companyImagePath);
        }catch (Exception e){
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this,e);
        }
        return card;
    }

    private void onBackPressedDialog() {
        try {
            final AlertDialog alert = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
            final View view = inflater.inflate(R.layout.delete_alert_dialog, null);
            alert.setView(view);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView message = (TextView) view.findViewById(R.id.text);
            TextView cancel = (TextView) view.findViewById(R.id.negative);
            TextView submit = (TextView) view.findViewById(R.id.positive);
            title.setText("Card Update");
            submit.setText("Yes");
            cancel.setText("No");
            message.setText("You will lose any unsaved changes you made. Do you like to proceed?");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                    finish();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });
            alert.show();
        }
        catch (Exception e){
            e.printStackTrace();
            Utils.sendReport(CreateCardActivity.this,e);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onBackPressedDialog();
    }
}
