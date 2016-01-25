package com.mobellotec.cardbiz.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class EditCardActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText first_name, last_name, email, mobile, companyName, role, landline, officeMail, aboutCompany, block,
            streetName, city, postalCode, fax, website, personTwitter, personFacebook, personLinkedin, companyTwitter, companyFacebook, companyLinkedin;
    private ImageView imgTemplateType, personDp, companyDp;
    private TextView save, country, txtCancel, txtCardType, title, txt_add_photo, txt_add_logo, template_msg;
    private ViewPager viewPager;
    private ProgressBar templateProgressBar, companyImageProgressBar, userImageProgressBar;
    private RelativeLayout countryContainer;
    private DBHelper dbHelper;
    private Card card = null;
    private CustomPagerAdapter pagerAdapter;

    private Bitmap personBitmap, companyBitmap;
    private String strFrontDetails, strBackDetails, strFrontImage, strBackImage, personImagePath = "", companyImagePath = "",
            cardId, templateID, strCardType, strCardFrontImage, strCardBackImage, strTemplateImage, serverCompanyDP, serverPersonImage;
    private ArrayList<String> photoList;
    private ArrayList<String> countries = new ArrayList<>();
    private Template template = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);
        try {
            initViews();
            title.setText("Edit Card");
            dbHelper = new DBHelper(getApplicationContext());
            card = getIntent().getParcelableExtra("card");
            setCardValues();
            parseCountry();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    txtCardType.setText(" Front ");
                } else if (position == 1) {
                    txtCardType.setText(" Back ");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imgTemplateType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppPreference.setBoolean(getApplicationContext(), AppPreference.ISCREATECARD, true);
                Intent intent = new Intent(EditCardActivity.this, FreeTemplateActivity.class);
                startActivityForResult(intent, Constants.ACTIVITY_FREE_TEMPLATES);
            }
        });
    }

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
        personTwitter.setOnClickListener(this);
        personFacebook.setOnClickListener(this);
        personLinkedin.setOnClickListener(this);
        companyTwitter.setOnClickListener(this);
        companyFacebook.setOnClickListener(this);
        companyLinkedin.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        save.setOnClickListener(this);
        personDp.setOnClickListener(this);
        companyDp.setOnClickListener(this);
        countryContainer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                if (Helper.isNetworkAvailable(getApplicationContext()))
                    validate();
                else
                    CommonClass.showMessageToast(getApplicationContext(), R.string.check_network);
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
                    Utils.sendReport(EditCardActivity.this, e);
                }
                break;
        }
    }

    private void getImageFromGallery(int requestCode) {
        try {
            Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            gallerypickerIntent.setType("image/*");
            startActivityForResult(gallerypickerIntent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    private void setCardValues() {
        try {
            cardId = card.getCardId();
            strCardType = card.getCardType();
            strFrontImage = card.getTemplateFront();
            strBackImage = card.getTemplateBack();
            if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
                template_msg.setVisibility(View.GONE);
                AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, true);
                ArrayList<String> fileNameList = new ArrayList<String>();
                fileNameList.add(strFrontImage);
                fileNameList.add(strBackImage);
                new MultipleImageDownloader(getApplicationContext(), fileNameList, new MultipleImageDownloader.ImageLoaderListener() {
                    @Override
                    public void onSuccess(ArrayList<String> filePath) {
                        setFrontImage(filePath);
                    }
                }).execute();
            } else if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                template_msg.setVisibility(View.VISIBLE);
                AppPreference.setBoolean(getApplicationContext(), AppPreference.CREATE_CARD_FROM_PHOTO, false);
                templateID = card.getTemplateId();
                strTemplateImage = card.getTempModelFront();
                strBackDetails = card.getTempBackDetails();
                strFrontDetails = card.getTempFrontDetails();
                setTemplateImage(strTemplateImage);
            }
            first_name.setText(card.getFirstName());
            last_name.setText(card.getLastName());
            email.setText(card.getEmail());
            mobile.setText(card.getMobile());
            companyName.setText(card.getCompanyName());
            companyName.setEnabled(false);
            role.setText(card.getRole());
            landline.setText(card.getLandline());
            officeMail.setText(card.getOfficeMail());
            aboutCompany.setText(card.getAboutCompany());
            block.setText(card.getBlock());
            streetName.setText(card.getStreet());
            city.setText(card.getCity());
            country.setText(card.getCountry());
            postalCode.setText(card.getPostalCode());
            streetName.setText(card.getStreet());
            fax.setText(card.getFax());
            website.setText(card.getWebsite());
            serverPersonImage = card.getPersonImage();
            if (serverPersonImage.length() < 0 || serverPersonImage.equalsIgnoreCase(null) || serverPersonImage.equalsIgnoreCase("")) {
                personImagePath = "";
            } else {
                userImageProgressBar.setVisibility(View.VISIBLE);
                String fileName = Helper.getFileNameFromUrl(serverPersonImage);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
//                personDp.setImageBitmap(getImageFromStorage(getApplicationContext(), file.getAbsolutePath()));
                    personImagePath = file.getAbsolutePath();
                    personDp.setImageBitmap(Helper.rotateBitmap(EditCardActivity.this, file.getAbsolutePath()));
                    personDp.setBackground(null);
                    userImageProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplateImageDownloader(getApplicationContext(), personImagePath, fileName, personDp, userImageProgressBar, new TemplateImageDownloader.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
                            personDp.setBackground(null);
                            personDp.setImageBitmap(Helper.rotateBitmap(EditCardActivity.this, filePath));
//                        personDp.setImageBitmap(getImageFromStorage(getApplicationContext(), filePath));
                            personImagePath = filePath;
                            userImageProgressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            }
            serverCompanyDP = card.getCompanyImage();
            if (serverCompanyDP.length() < 0 || serverCompanyDP.equalsIgnoreCase(null) || serverCompanyDP.equalsIgnoreCase("")) {
                companyImagePath = "";
            } else {
                companyImageProgressBar.setVisibility(View.VISIBLE);
                String fileName = Helper.getFileNameFromUrl(serverCompanyDP);
                File file = new File(Helper.sdCardRoot + "/" + fileName);
                if (file.exists()) {
                    companyImagePath = file.getAbsolutePath();
                    companyDp.setImageBitmap(Helper.rotateBitmap(EditCardActivity.this, file.getAbsolutePath()));
//                companyDp.setImageBitmap(getImageFromStorage(getApplicationContext(), file.getAbsolutePath()));
                    companyDp.setBackground(null);
                    companyImageProgressBar.setVisibility(View.GONE);
                } else {
                    new TemplateImageDownloader(getApplicationContext(), companyImagePath, fileName, companyDp, companyImageProgressBar, new TemplateImageDownloader.ImageLoaderListener() {
                        @Override
                        public void onSuccess(String filePath, ImageView imageView, ProgressBar progressBar) {
                            companyDp.setBackground(null);
//                        companyDp.setImageBitmap(getImageFromStorage(getApplicationContext(), filePath));
                            companyDp.setImageBitmap(Helper.rotateBitmap(EditCardActivity.this, filePath));
                            companyImagePath = filePath;
                            companyImageProgressBar.setVisibility(View.GONE);
                        }
                    }).execute();
                }
            }
            personFacebook.setText(card.getPersonFacebook());
            personTwitter.setText(card.getPersonTwitter());
            personLinkedin.setText(card.getPersonLinkenin());
            companyTwitter.setText(card.getCompanyTwitter());
            companyFacebook.setText(card.getCompanyFacebook());
            companyLinkedin.setText(card.getCompanyLinkedin());
            personBitmap = card.getPersonImageBitmap();
            companyBitmap = card.getCompanyLogoBitmap();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    private void setFrontImage(ArrayList<String> file) {
        try {
            txtCardType.setText("Front");
            photoList = new ArrayList<>();
            if (file.get(0).toString().length() > 0) {
                strCardFrontImage = file.get(0);
            } else {
                strCardFrontImage = "";
            }
            if (file.get(1).toString().length() > 0) {
                strCardBackImage = file.get(1);
            } else {
                strCardBackImage = "";
            }
            pagerAdapter = new CustomPagerAdapter(getApplicationContext(), file);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    private void setTemplateImage(String strTemplateImage) {
        try {
            txtCardType.setText("SELECT TEMPLATE");
            templateProgressBar.setVisibility(View.VISIBLE);
            imgTemplateType.setVisibility(View.VISIBLE);
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
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        template = data.getParcelableExtra("template");
                        templateID = template.getTemplateId();
                        strTemplateImage = template.getFrontModel();
                        strFrontImage = template.getFrontImage();
                        strBackImage = template.getBackImage();
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
            Bitmap oldBitmap = Helper.rotateBitmap(EditCardActivity.this, oldFile.getAbsolutePath());
            /*File newFile = Helper.getOutputMediaFile();
            FileOutputStream out = new FileOutputStream(newFile);
            oldBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Bitmap newBitmap = Helper.rotateBitmap(EditCardActivity.this, newFile.getAbsolutePath());
            oldFile.delete();*/
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
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    private void validate() {
        try {
            if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                if (templateID == null || TextUtils.isEmpty(templateID)) {
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
                    editCard();
            else
                editCard();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    private void showDialog(String message) {
        try {
            final Dialog dialog = new Dialog(EditCardActivity.this);
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
            Utils.sendReport(EditCardActivity.this, e);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void editCard() {
        try {
            CommonClass.showProgress(EditCardActivity.this);
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
            if (viewPager.getVisibility() == View.VISIBLE) {
                filepaths.add(new Parameter("cardFront", strCardFrontImage));
                filepaths.add(new Parameter("cardBack", strCardBackImage));
            } else {
                parameters.add(new Parameter("templateID", templateID));
            }
            parameters.add(new Parameter("cardID", cardId));
            new FileServerRequest(getApplicationContext(), FileServerRequest.POST, Constants.UPDATE_CARD, parameters, filepaths, new FileServerRequest.FileRequestListener() {

                @Override
                public void onRequestSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        CommonClass.showMessageToast(getApplicationContext(), jsonObject.getString("message"));
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        /*Intent intent = new Intent(EditCardActivity.this, HomeActivity.class);
                        startActivity(intent);
                        for (Context context : Helper.listActivity) {
                            if (context != null)
                                ((AppCompatActivity) context).finish();
                        }
                        Helper.listActivity.clear();*/
                            Card card = assignCard();
                            dbHelper.updateCard(card);
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
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    private Card assignCard() {
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
            card.setFilePath(this.card.getFilePath());
            card.setTemplateFront(strFrontImage);
            card.setTemplateBack(strBackImage);
            if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_MANUAL)) {
                card.setTempModelFront(strTemplateImage);
                card.setTempFrontDetails(strFrontDetails);
                card.setTempBackDetails(strBackDetails);
                card.setTemplateId(templateID);
            } else if (strCardType.equalsIgnoreCase(Constants.CARD_TYPE_AUTO)) {
                card.setTemplateId("");
            }
        /*if (personImagePath.equalsIgnoreCase(null) || TextUtils.isEmpty(personImagePath))
            card.setPersonImage(strPersonImage);
        else*/
            card.setPersonImage(personImagePath);

        /*if (companyImagePath.equalsIgnoreCase(null) || TextUtils.isEmpty(companyImagePath))
            card.setCompanyImage(strCompanyDP);
        else*/
            card.setCompanyImage(companyImagePath);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
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
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(EditCardActivity.this, e);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onBackPressedDialog();
    }

}
