package com.mobellotec.cardbiz.Utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mobellotec.cardbiz.Model.Card;
import com.mobellotec.cardbiz.Model.Contact;
import com.mobellotec.cardbiz.Model.GroupInfo;

import java.util.ArrayList;

/**
 * Created by MobelloTech on 28-07-2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    /**
     * Seperate database for WAMS named as WAMS
     */
    private static final String DB_NAME = "cardbiz";

    /**
     * WAMS database version number
     */
    private static final int DB_VERSION = 1;

    /**
     * Tabel name for WAMS member is MEMBER
     */
    private static final String CARD_TBL_NAME = "card";
    private static final String CONTACT_TABLE_NAME = "contact";
    private static final String EMAIL_TABLE_NAME = "email_contact";
    private static final String GROUP_SELECTED_CARD_TABLE_NAME = "group_selected_card_table";
    private static final String SHARED_CARD_TABLE_NAME = "shared_card_table";
    private static final String NEAR_BY_SELECTED_CARD_TABLE_NAME = "near_by_selected_card_table";

    private static final String CARD_ID = "card_id";
    private static final String TEMPLATE_ID = "template_id";
    private static final String PERSON_IMAGE_BITMAP = "person_image_bitmap";
    private static final String PERSON_IMAGE = "person_image";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String MOBILE = "mobile";
    private static final String PERSON_TWITTER = "person_twitter";
    private static final String PERSON_FACEBOOK = "person_facebook";
    private static final String PERSON_LINKEDIN = "person_linkedin";
    private static final String PERSON_LOCATION = "person_location";
    private static final String COMPANY_LOGO_BITMAP = "company_logo_bitmap";
    private static final String COMPANY_LOGO = "company_logo";
    private static final String COMPANY_NAME = "company_name";
    private static final String ROLE = "role";
    private static final String OFFICE_LANDLINE = "office_landline";
    private static final String OFFICE_MAIL = "office_mail";
    private static final String OFFICE_FAX = "office_fax";
    private static final String OFFICE_WEBSITE = "office_website";
    private static final String ABOUT_COMPANY = "about_company";
    private static final String COMPANY_TWITTER = "company_twitter";
    private static final String COMPANY_FACEBOOK = "company_facebook";
    private static final String COMPANY_LINKEDIN = "company_linkedin";
    private static final String BLOCK = "block";
    private static final String STREET_NAME = "street_name";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String POSTAL_CODE = "postal_code";
    private static final String TEMP_NAME = "template_name";
    private static final String TEMP_FRONT = "template_front";
    private static final String TEMP_BACK = "template_back";
    private static final String TEMP_FRONT_DETAILS = "temp_front_details";
    private static final String TEMP_BACK_DETAILS = "temp_back_details";
    private static final String CARD_TYPE = "card_type";
    private static final String TEMP_MODEL_FRONT = "template_model_front";
    private static final String TEMP_MODEL_BACK = "template_model_back";

    private static final String GROUP_SELECTED_CARD_ID = "selected_card_id";
    private static final String GROUP_SELECTED_CARD_NAME = "selected_card_name";
    private static final String GROUP_ID = "group_id";

    private static final String NEAR_BY_SELECTED_CARD_ID = "selected_card_id";
    private static final String NEAR_BY_SELECTED_VISIBILITY = "selected_card_visibility";
    private static final String NEAR_BY_SELECTED_CARD_NAME = "selected_card_name";

    private static final String SHARED_USER_ID = "user_id";
    private static final String SHARED_USER_NAME = "user_name";
    private static final String SHARED_PHONE_NO = "phone_no";
    private static final String SHARED_CARD_ID = "card_id";
    private static final String SHARED_COMPANY_NAME = "company_name";


    private static final String CARD_TABLE = "create table if not exists " + CARD_TBL_NAME + "(" + CARD_ID + " integer primary key, "
            + PERSON_IMAGE_BITMAP + " blob, " + FIRST_NAME + " text not null, " + PERSON_IMAGE + " TEXT,"
            + LAST_NAME + " text not null, " + EMAIL + " text not null, " + MOBILE + " text not null, "
            + PERSON_TWITTER + " text, " + PERSON_FACEBOOK + " text, " + PERSON_LINKEDIN + " text, " + COMPANY_LOGO_BITMAP + " blob, " + COMPANY_LOGO + " TEXT," + COMPANY_NAME + " text, "
            + ROLE + " text, " + OFFICE_LANDLINE + " text, " + OFFICE_MAIL + " text, "
            + OFFICE_FAX + " text, " + OFFICE_WEBSITE + " text, " + ABOUT_COMPANY + " text, "
            + COMPANY_TWITTER + " text, " + COMPANY_FACEBOOK + " text, " + COMPANY_LINKEDIN + " text, "
            + BLOCK + " text, " + STREET_NAME + " text, " + CITY + " text, "
            + COUNTRY + " text, " + POSTAL_CODE + " text, " + TEMPLATE_ID + " text, " + TEMP_NAME + " text, " + TEMP_FRONT + " text, " +
            TEMP_BACK + " text, " + TEMP_MODEL_FRONT + " text, " + TEMP_MODEL_BACK + " blob, "
            + TEMP_FRONT_DETAILS + " text, " + CARD_TYPE + " text, " + TEMP_BACK_DETAILS + " text); ";

    private static final String CONTACT_TABLE = "create table if not exists " + CONTACT_TABLE_NAME + "(" + Constants.CONTACT_ID + " text, " + Constants.CONTACT_NUMBER + " text , " + Constants.CONTACT_NAME + " text , " + Constants.CONTACT_SELECTED + " integer);";

    private static final String EMAIL_TABLE = "create table if not exists " + EMAIL_TABLE_NAME + "(" + Constants.CONTACT_EMAIL_ID + " text, " + Constants.CONTACT_EMAIL_EMAIL + " text , " + Constants.CONTACT_EMAIL_NAME + " text , " + Constants.CONTACT_EMAIL_SELECTED + " integer);";

    private static final String GROUP_TABLE = "create table if not exists " + GROUP_SELECTED_CARD_TABLE_NAME + "(" + GROUP_ID + " text, "
            + GROUP_SELECTED_CARD_ID + " text, " + GROUP_SELECTED_CARD_NAME + " text);";

    private static final String SHARED_CARD_TABLE = " create table if not exists " + SHARED_CARD_TABLE_NAME + "(" + SHARED_USER_ID + " text, " + SHARED_USER_NAME + " text, " + SHARED_PHONE_NO + " text, "
            + SHARED_CARD_ID + " text, " + SHARED_COMPANY_NAME + " text);";

    private static final String NEAR_BY_SELECTED_TABLE = "create table if not exists " + NEAR_BY_SELECTED_CARD_TABLE_NAME + "(" + NEAR_BY_SELECTED_CARD_ID + " text, " + NEAR_BY_SELECTED_CARD_NAME + " text, " + NEAR_BY_SELECTED_VISIBILITY + " text );";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CARD_TABLE);
        db.execSQL(CONTACT_TABLE);
        db.execSQL(EMAIL_TABLE);
        db.execSQL(GROUP_TABLE);
        db.execSQL(SHARED_CARD_TABLE);
        db.execSQL(NEAR_BY_SELECTED_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CARD_TBL_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CONTACT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EMAIL_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SHARED_CARD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + NEAR_BY_SELECTED_TABLE);
        onCreate(db);
    }

    public void insertContact(Contact contact) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.CONTACT_ID, contact.getId());
            values.put(Constants.CONTACT_NAME, contact.getName());
            values.put(Constants.CONTACT_NUMBER, contact.getPhone());
            values.put(Constants.CONTACT_SELECTED, contact.getIsSelected());
            database.insert(CONTACT_TABLE_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public ArrayList<Contact> getContact() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + CONTACT_TABLE_NAME + " ORDER BY " + Constants.CONTACT_NAME + " ASC;";
        try {
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();
                    contact.setId(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_ID)));
                    contact.setName(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_NAME)));
                    contact.setPhone(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_NUMBER)));
                    contact.setIsSelected(cursor.getInt(cursor.getColumnIndex(Constants.CONTACT_SELECTED)));
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        database.close();
        return contactList;
    }

    public void insertEmailContact(Contact contact) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.CONTACT_EMAIL_ID, contact.getId());
            values.put(Constants.CONTACT_EMAIL_NAME, contact.getName());
            values.put(Constants.CONTACT_EMAIL_EMAIL, contact.getEmail());
            values.put(Constants.CONTACT_EMAIL_SELECTED, contact.getIsSelected());
            database.insert(EMAIL_TABLE_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public ArrayList<Contact> getEmailContact() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase database = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + EMAIL_TABLE_NAME + " ORDER BY " + Constants.CONTACT_EMAIL_NAME + " ASC;";
        try {
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();
                    contact.setId(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_EMAIL_ID)));
                    contact.setName(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_EMAIL_NAME)));
                    contact.setEmail(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_EMAIL_EMAIL)));
                    contact.setIsSelected(cursor.getInt(cursor.getColumnIndex(Constants.CONTACT_EMAIL_SELECTED)));
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }

        database.close();
        return contactList;
    }

    public void updateContactSelected(String id, int isSelected) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "UPDATE " + CONTACT_TABLE_NAME + " SET " + Constants.CONTACT_SELECTED + " = '" + isSelected + "' WHERE " + Constants.CONTACT_ID + " = '" + id + "'";
            db.execSQL(updateQuery);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void updateEmailContactSelected(String id, int isSelected) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "UPDATE " + EMAIL_TABLE_NAME + " SET " + Constants.CONTACT_EMAIL_SELECTED + " = '" + isSelected + "' WHERE " + Constants.CONTACT_EMAIL_ID + " = '" + id + "'";
            db.execSQL(updateQuery);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void updateAllContactSelected(int isSelected) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.CONTACT_SELECTED, isSelected);
            db.update(CONTACT_TABLE_NAME, values, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void updateAllEmailContactSelected(int isSelected) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Constants.CONTACT_EMAIL_SELECTED, isSelected);
            db.update(EMAIL_TABLE_NAME, values, null, null);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public ArrayList<Contact> getSelectedContact() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            String selectQuery = "SELECT  * FROM " + CONTACT_TABLE_NAME + " WHERE " + Constants.CONTACT_SELECTED + " = " + 1;
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();
                    contact.setId(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_ID)));
                    contact.setName(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_NAME)));
                    contact.setPhone(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_NUMBER)));
                    contact.setIsSelected(cursor.getInt(cursor.getColumnIndex(Constants.CONTACT_SELECTED)));
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }

        database.close();
        return contactList;
    }


    public ArrayList<Contact> getSelectedEmailContact() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        SQLiteDatabase database = this.getReadableDatabase();
        try {
            String selectQuery = "SELECT  * FROM " + EMAIL_TABLE_NAME + " WHERE " + Constants.CONTACT_EMAIL_SELECTED + " = " + 1;
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Contact contact = new Contact();
                    contact.setId(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_EMAIL_ID)));
                    contact.setName(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_EMAIL_NAME)));
                    contact.setEmail(cursor.getString(cursor.getColumnIndex(Constants.CONTACT_EMAIL_EMAIL)));
                    contact.setIsSelected(cursor.getInt(cursor.getColumnIndex(Constants.CONTACT_EMAIL_SELECTED)));
                    contactList.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }

        database.close();
        return contactList;
    }

    public void deleteContact() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(CONTACT_TABLE_NAME, null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void deleteEmailContact() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(EMAIL_TABLE_NAME, null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void deleteAllCards() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(CARD_TBL_NAME, null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void insertCard(Card card) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CARD_ID, card.getCardId());
            values.put(PERSON_IMAGE_BITMAP, Helper.convertBitmapToByteArray(card.getPersonImageBitmap()));
            values.put(PERSON_IMAGE, card.getPersonImage());
            values.put(FIRST_NAME, card.getFirstName());
            values.put(LAST_NAME, card.getLastName());
            values.put(EMAIL, card.getEmail());
            values.put(MOBILE, card.getMobile());
            values.put(PERSON_FACEBOOK, card.getPersonFacebook());
            values.put(PERSON_TWITTER, card.getPersonTwitter());
            values.put(PERSON_LINKEDIN, card.getPersonLinkenin());
            values.put(COMPANY_LOGO_BITMAP, Helper.convertBitmapToByteArray(card.getCompanyLogoBitmap()));
            values.put(COMPANY_LOGO, card.getCompanyImage());
            values.put(COMPANY_NAME, card.getCompanyName());
            values.put(ROLE, card.getRole());
            values.put(OFFICE_LANDLINE, card.getLandline());
            values.put(OFFICE_MAIL, card.getOfficeMail());
            values.put(OFFICE_FAX, card.getFax());
            values.put(OFFICE_WEBSITE, card.getWebsite());
            values.put(ABOUT_COMPANY, card.getAboutCompany());
            values.put(COMPANY_FACEBOOK, card.getCompanyFacebook());
            values.put(COMPANY_TWITTER, card.getCompanyTwitter());
            values.put(COMPANY_LINKEDIN, card.getCompanyLinkedin());
            values.put(BLOCK, card.getBlock());
            values.put(STREET_NAME, card.getStreet());
            values.put(CITY, card.getCity());
            values.put(COUNTRY, card.getCountry());
            values.put(POSTAL_CODE, card.getPostalCode());
            values.put(TEMPLATE_ID, card.getTemplateId());
            values.put(TEMP_NAME, card.getTemplateName());
            values.put(TEMP_FRONT, card.getTemplateFront());
            values.put(TEMP_MODEL_FRONT, card.getTempModelFront());
            values.put(TEMP_BACK, card.getTemplateBack());
            values.put(TEMP_MODEL_BACK, card.getTempModelBack());
            values.put(TEMP_FRONT_DETAILS, card.getTempFrontDetails());
            values.put(TEMP_BACK_DETAILS, card.getTempBackDetails());
            values.put(CARD_TYPE, card.getCardType());
            database.insert(CARD_TBL_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void updateCard(Card card) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CARD_ID, card.getCardId());
            values.put(PERSON_IMAGE_BITMAP, Helper.convertBitmapToByteArray(card.getPersonImageBitmap()));
            values.put(PERSON_IMAGE, card.getPersonImage());
            values.put(FIRST_NAME, card.getFirstName());
            values.put(LAST_NAME, card.getLastName());
            values.put(EMAIL, card.getEmail());
            values.put(MOBILE, card.getMobile());
            values.put(PERSON_FACEBOOK, card.getPersonFacebook());
            values.put(PERSON_TWITTER, card.getPersonTwitter());
            values.put(PERSON_LINKEDIN, card.getPersonLinkenin());
            values.put(COMPANY_LOGO_BITMAP, Helper.convertBitmapToByteArray(card.getCompanyLogoBitmap()));
            values.put(COMPANY_LOGO, card.getCompanyImage());
            values.put(COMPANY_NAME, card.getCompanyName());
            values.put(ROLE, card.getRole());
            values.put(OFFICE_LANDLINE, card.getLandline());
            values.put(OFFICE_MAIL, card.getOfficeMail());
            values.put(OFFICE_FAX, card.getFax());
            values.put(OFFICE_WEBSITE, card.getWebsite());
            values.put(ABOUT_COMPANY, card.getAboutCompany());
            values.put(COMPANY_FACEBOOK, card.getCompanyFacebook());
            values.put(COMPANY_TWITTER, card.getCompanyTwitter());
            values.put(COMPANY_LINKEDIN, card.getCompanyLinkedin());
            values.put(BLOCK, card.getBlock());
            values.put(STREET_NAME, card.getStreet());
            values.put(CITY, card.getCity());
            values.put(COUNTRY, card.getCountry());
            values.put(POSTAL_CODE, card.getPostalCode());
            values.put(TEMPLATE_ID, card.getTemplateId());
            values.put(TEMP_NAME, card.getTemplateName());
            values.put(TEMP_FRONT, card.getTemplateFront());
            values.put(TEMP_MODEL_FRONT, card.getTempModelFront());
            values.put(TEMP_BACK, card.getTemplateBack());
            values.put(TEMP_MODEL_BACK, card.getTempModelBack());
            values.put(TEMP_FRONT_DETAILS, card.getTempFrontDetails());
            values.put(TEMP_BACK_DETAILS, card.getTempBackDetails());
            values.put(CARD_TYPE, card.getCardType());
            database.update(CARD_TBL_NAME, values, CARD_ID + "=?", new String[]{card.getCardId()});
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public ArrayList<Card> getAllCardDetails() {
        ArrayList<Card> cardList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + CARD_TBL_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Card card = new Card();
                    card.setCardId(cursor.getString(cursor.getColumnIndex(CARD_ID)));
                    card.setPersonImageBitmap(Helper.convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(PERSON_IMAGE_BITMAP))));
                    card.setPersonImage(cursor.getString(cursor.getColumnIndex(PERSON_IMAGE)));
                    card.setCompanyLogoBitmap(Helper.convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(COMPANY_LOGO_BITMAP))));
                    card.setCompanyImage(cursor.getString(cursor.getColumnIndex(COMPANY_LOGO)));
                    card.setFirstName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));
                    card.setLastName(cursor.getString(cursor.getColumnIndex(LAST_NAME)));
                    card.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                    card.setMobile(cursor.getString(cursor.getColumnIndex(MOBILE)));
                    card.setPersonFacebook(cursor.getString(cursor.getColumnIndex(PERSON_FACEBOOK)));
                    card.setPersonTwitter(cursor.getString(cursor.getColumnIndex(PERSON_TWITTER)));
                    card.setPersonLinkenin(cursor.getString(cursor.getColumnIndex(PERSON_LINKEDIN)));
                    card.setCompanyName(cursor.getString(cursor.getColumnIndex(COMPANY_NAME)));
                    card.setRole(cursor.getString(cursor.getColumnIndex(ROLE)));
                    card.setOfficeMail(cursor.getString(cursor.getColumnIndex(OFFICE_MAIL)));
                    card.setLandline(cursor.getString(cursor.getColumnIndex(OFFICE_LANDLINE)));
                    card.setFax(cursor.getString(cursor.getColumnIndex(OFFICE_FAX)));
                    card.setWebsite(cursor.getString(cursor.getColumnIndex(OFFICE_WEBSITE)));
                    card.setAboutCompany(cursor.getString(cursor.getColumnIndex(ABOUT_COMPANY)));
                    card.setCompanyFacebook(cursor.getString(cursor.getColumnIndex(COMPANY_FACEBOOK)));
                    card.setCompanyTwitter(cursor.getString(cursor.getColumnIndex(COMPANY_TWITTER)));
                    card.setCompanyLinkedin(cursor.getString(cursor.getColumnIndex(COMPANY_LINKEDIN)));
                    card.setBlock(cursor.getString(cursor.getColumnIndex(BLOCK)));
                    card.setStreet(cursor.getString(cursor.getColumnIndex(STREET_NAME)));
                    card.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
                    card.setCountry(cursor.getString(cursor.getColumnIndex(COUNTRY)));
                    card.setPostalCode(cursor.getString(cursor.getColumnIndex(POSTAL_CODE)));
                    card.setTemplateId(cursor.getString(cursor.getColumnIndex(TEMPLATE_ID)));
                    card.setTemplateName(cursor.getString(cursor.getColumnIndex(TEMP_NAME)));
                    card.setTemplateFront(cursor.getString(cursor.getColumnIndex(TEMP_FRONT)));
                    card.setTemplateBack(cursor.getString(cursor.getColumnIndex(TEMP_BACK)));
                    card.setTempFrontDetails(cursor.getString(cursor.getColumnIndex(TEMP_FRONT_DETAILS)));
                    card.setTempBackDetails(cursor.getString(cursor.getColumnIndex(TEMP_BACK_DETAILS)));
                    card.setCardType(cursor.getString(cursor.getColumnIndex(CARD_TYPE)));
                    card.setTempModelFront(cursor.getString(cursor.getColumnIndex(TEMP_MODEL_FRONT)));
                    card.setTempModelBack(cursor.getString(cursor.getColumnIndex(TEMP_MODEL_BACK)));
                    cardList.add(card);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
        return cardList;
    }

    public Card getCardDetails(String cardId) {
        String selectQuery = "SELECT  * FROM " + CARD_TBL_NAME + " WHERE " + CARD_ID + " = '" + cardId + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Card card = new Card();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                card.setCardId(cursor.getString(cursor.getColumnIndex(CARD_ID)));
                card.setPersonImageBitmap(Helper.convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(PERSON_IMAGE_BITMAP))));
                card.setPersonImage(cursor.getString(cursor.getColumnIndex(PERSON_IMAGE)));
                card.setCompanyLogoBitmap(Helper.convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(COMPANY_LOGO_BITMAP))));
                card.setCompanyImage(cursor.getString(cursor.getColumnIndex(COMPANY_LOGO)));
                card.setFirstName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));
                card.setLastName(cursor.getString(cursor.getColumnIndex(LAST_NAME)));
                card.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
                card.setMobile(cursor.getString(cursor.getColumnIndex(MOBILE)));
                card.setPersonFacebook(cursor.getString(cursor.getColumnIndex(PERSON_FACEBOOK)));
                card.setPersonTwitter(cursor.getString(cursor.getColumnIndex(PERSON_TWITTER)));
                card.setPersonLinkenin(cursor.getString(cursor.getColumnIndex(PERSON_LINKEDIN)));
                card.setCompanyName(cursor.getString(cursor.getColumnIndex(COMPANY_NAME)));
                card.setRole(cursor.getString(cursor.getColumnIndex(ROLE)));
                card.setOfficeMail(cursor.getString(cursor.getColumnIndex(OFFICE_MAIL)));
                card.setLandline(cursor.getString(cursor.getColumnIndex(OFFICE_LANDLINE)));
                card.setFax(cursor.getString(cursor.getColumnIndex(OFFICE_FAX)));
                card.setWebsite(cursor.getString(cursor.getColumnIndex(OFFICE_WEBSITE)));
                card.setAboutCompany(cursor.getString(cursor.getColumnIndex(ABOUT_COMPANY)));
                card.setCompanyFacebook(cursor.getString(cursor.getColumnIndex(COMPANY_FACEBOOK)));
                card.setCompanyTwitter(cursor.getString(cursor.getColumnIndex(COMPANY_TWITTER)));
                card.setCompanyLinkedin(cursor.getString(cursor.getColumnIndex(COMPANY_LINKEDIN)));
                card.setBlock(cursor.getString(cursor.getColumnIndex(BLOCK)));
                card.setStreet(cursor.getString(cursor.getColumnIndex(STREET_NAME)));
                card.setCity(cursor.getString(cursor.getColumnIndex(CITY)));
                card.setCountry(cursor.getString(cursor.getColumnIndex(COUNTRY)));
                card.setPostalCode(cursor.getString(cursor.getColumnIndex(POSTAL_CODE)));
                card.setTemplateId(cursor.getString(cursor.getColumnIndex(TEMPLATE_ID)));
                card.setTemplateName(cursor.getString(cursor.getColumnIndex(TEMP_NAME)));
                card.setTemplateFront(cursor.getString(cursor.getColumnIndex(TEMP_FRONT)));
                card.setTemplateBack(cursor.getString(cursor.getColumnIndex(TEMP_BACK)));
                card.setTempFrontDetails(cursor.getString(cursor.getColumnIndex(TEMP_FRONT_DETAILS)));
                card.setTempBackDetails(cursor.getString(cursor.getColumnIndex(TEMP_BACK_DETAILS)));
                card.setCardType(cursor.getString(cursor.getColumnIndex(CARD_TYPE)));
                card.setTempModelFront(cursor.getString(cursor.getColumnIndex(TEMP_MODEL_FRONT)));
                card.setTempModelBack(cursor.getString(cursor.getColumnIndex(TEMP_MODEL_BACK)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
        return card;
    }

    public void updateTemplate(String card_id, String template_id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "UPDATE " + CARD_TBL_NAME + " SET " + TEMPLATE_ID + " = '" + template_id + "' WHERE " + CARD_ID + " = '" + card_id + "'";
            db.execSQL(updateQuery);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void deleteMyCard(String cardID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + CARD_TBL_NAME + " WHERE " + CARD_ID + " = '" + cardID + "'";
        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
    }

    public void insertGroup(String group_id) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(GROUP_ID, group_id);
            values.put(GROUP_SELECTED_CARD_ID, "");
            values.put(GROUP_SELECTED_CARD_NAME, "");
            database.insert(GROUP_SELECTED_CARD_TABLE_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void deleteAllGroup() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(GROUP_SELECTED_CARD_TABLE_NAME, null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void deleteGroup(String group_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + GROUP_SELECTED_CARD_TABLE_NAME + " WHERE " + GROUP_ID + " = '" + group_id + "'";
        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
    }

    public void updateSelectedCard(String group_id, String card_id, String card_name) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "UPDATE " + GROUP_SELECTED_CARD_TABLE_NAME + " SET " + GROUP_SELECTED_CARD_ID + " = '" + card_id + "' , " + GROUP_SELECTED_CARD_NAME + " = '" + card_name + "' WHERE " + GROUP_ID + " = '" + group_id + "'";
            db.execSQL(updateQuery);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }

    }

    public ArrayList<String> getSelectedGroupDetails(String group_id) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + GROUP_SELECTED_CARD_TABLE_NAME + " WHERE " + GROUP_ID + " = '" + group_id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                arrayList.add(cursor.getString(cursor.getColumnIndex(GROUP_SELECTED_CARD_ID)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(GROUP_SELECTED_CARD_NAME)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
        return arrayList;
    }

    public void deleteCardInGroup(String card_id) {
        try {
            String selectQuery = "SELECT  * FROM " + GROUP_SELECTED_CARD_TABLE_NAME + " WHERE " + GROUP_SELECTED_CARD_ID + " = '" + card_id + "'";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String group_id = cursor.getString(cursor.getColumnIndex(GROUP_ID));
                    String updateQuery = "UPDATE " + GROUP_SELECTED_CARD_TABLE_NAME + " SET " + GROUP_SELECTED_CARD_ID + " = '" + "" + "' , " + GROUP_SELECTED_CARD_NAME + " = '" + "" + "' WHERE " + GROUP_ID + " = '" + group_id + "'";
                    db.execSQL(updateQuery);
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void updateCardId(GroupInfo groupInfo) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String updateQuery = "UPDATE " + CONTACT_TABLE_NAME + " SET " + Constants.CONTACT_ID + " = '" + groupInfo.getCardID() + "' WHERE " + Constants.CONTACT_SUFFIX_NAME + " = '" + groupInfo.getCompanyName() + "' AND " + Constants.CONTACT_NUMBER + " = '" + groupInfo.getContactNumber() + "'";
            db.execSQL(updateQuery);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void insertSharedContact(String user_id, String user_name, String phone_no, String card_id, String company_name) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SHARED_USER_ID, user_id);
            values.put(SHARED_USER_NAME, user_name);
            values.put(SHARED_PHONE_NO, phone_no);
            values.put(SHARED_CARD_ID, card_id);
            values.put(SHARED_COMPANY_NAME, company_name);
            database.insert(SHARED_CARD_TABLE_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void insertSharedContact(GroupInfo groupInfo) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(SHARED_USER_ID, groupInfo.getUserID());
            values.put(SHARED_USER_NAME, groupInfo.getName());
            values.put(SHARED_PHONE_NO, groupInfo.getContactNumber());
            values.put(SHARED_CARD_ID, groupInfo.getCardID());
            values.put(SHARED_COMPANY_NAME, groupInfo.getCompanyName());
            database.insert(SHARED_CARD_TABLE_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void insertSharedContactList(ArrayList<GroupInfo> groupInfos) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            for (int i = 0; i < groupInfos.size(); i++) {
                ContentValues values = new ContentValues();
                GroupInfo groupInfo = groupInfos.get(i);
                values.put(SHARED_USER_ID, groupInfo.getUserID());
                values.put(SHARED_USER_NAME, groupInfo.getName());
                values.put(SHARED_PHONE_NO, groupInfo.getContactNumber());
                values.put(SHARED_CARD_ID, groupInfo.getCardID());
                values.put(SHARED_COMPANY_NAME, groupInfo.getCompanyName());
                database.insert(SHARED_CARD_TABLE_NAME, null, values);
            }
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public ArrayList<GroupInfo> getSharedContact() {
        String selectQuery = "SELECT  * FROM " + SHARED_CARD_TABLE_NAME + " ORDER BY " + SHARED_USER_NAME + " ASC;";
        ArrayList<GroupInfo> groupInfos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToNext()) {
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setUserID(cursor.getString(cursor.getColumnIndex(SHARED_USER_ID)));
                groupInfo.setName(cursor.getString(cursor.getColumnIndex(SHARED_USER_NAME)));
                groupInfo.setContactNumber(cursor.getString(cursor.getColumnIndex(SHARED_PHONE_NO)));
                groupInfo.setCardID(cursor.getString(cursor.getColumnIndex(SHARED_CARD_ID)));
                groupInfo.setCompanyName(cursor.getString(cursor.getColumnIndex(SHARED_COMPANY_NAME)));
                groupInfos.add(groupInfo);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
        return groupInfos;
    }

    public GroupInfo getSharedContsactByCardID(String card_id) {
        GroupInfo groupInfo = new GroupInfo();
        String selectQuery = "SELECT  * FROM " + SHARED_CARD_TABLE_NAME + " WHERE " + SHARED_USER_ID + " = '" + card_id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            while (cursor.moveToFirst()) {
                groupInfo.setUserID(cursor.getString(cursor.getColumnIndex(SHARED_USER_ID)));
                groupInfo.setName(cursor.getString(cursor.getColumnIndex(SHARED_USER_NAME)));
                groupInfo.setContactNumber(cursor.getString(cursor.getColumnIndex(SHARED_PHONE_NO)));
                groupInfo.setCardID(cursor.getString(cursor.getColumnIndex(SHARED_CARD_ID)));
                groupInfo.setCompanyName(cursor.getString(cursor.getColumnIndex(SHARED_COMPANY_NAME)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
        return groupInfo;
    }

    public void clearSharedContacts() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(SHARED_CARD_TABLE_NAME, null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void deleteSharedCard(String cardID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + SHARED_CARD_TABLE_NAME + " WHERE " + SHARED_CARD_ID + " = '" + cardID + "'";
        try {
            db.execSQL(deleteQuery);
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
    }

    public void updateNearByCard(String card_id, String card_name, int visibility) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NEAR_BY_SELECTED_CARD_ID, card_id);
            values.put(NEAR_BY_SELECTED_CARD_NAME, card_name);
            values.put(NEAR_BY_SELECTED_VISIBILITY, String.valueOf(visibility));
            database.insert(NEAR_BY_SELECTED_CARD_TABLE_NAME, null, values);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public void clearNearBy() {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            database.delete(NEAR_BY_SELECTED_CARD_TABLE_NAME, null, null);
            database.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
    }

    public ArrayList<String> getNearByCardDetails() {
        ArrayList<String> arrayList = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + NEAR_BY_SELECTED_CARD_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                arrayList.add(cursor.getString(cursor.getColumnIndex(NEAR_BY_SELECTED_CARD_ID)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(NEAR_BY_SELECTED_CARD_NAME)));
                arrayList.add(cursor.getString(cursor.getColumnIndex(NEAR_BY_SELECTED_VISIBILITY)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.sendReport(context, e);
        }
        db.close();
        return arrayList;
    }
}
