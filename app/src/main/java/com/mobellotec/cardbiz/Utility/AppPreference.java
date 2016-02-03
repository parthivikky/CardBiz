package com.mobellotec.cardbiz.Utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MobelloTech on 14-07-2015.
 */
public class AppPreference {

    private static final String prefernce_name = "CardBiz";

    public static final String IS_LOGIN = "isLogin";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String MOBILE = "mobile";
    public static final String USER_ID = "userId";
    public static final String CARD_UPDATE = "card_update";
//    public static final String LOAD_NEW_CARD = "new_card";
    public static final String CARD_LOAD_SERVER = "card_load_server";
    public static final String APP_INTRO = "app_intro";
    public static final String CONTACT_CARD_DETAIL_FROM_SERVER = "contact_card_from_server";
    public static final String GROUP_LOAD_FROM_SERVER = "group_load_from_server";
    public static final String GROUP_EDIT = "group_edit";
    public static final String GROUP_JOIN = "group_join";
    public static final String FILE_PATH = "strTemplateFilePath";
    public static final String CREATE_CARD_FROM_PHOTO = "card_create_using_photo";
    public static final String ISCREATECARD = "from_create_card";
    public static final String TEMPLATE_ID = "template_id";
    public static final String TEMPLATE_IMAGE = "template_image";
    public static final String IS_GCM_AVAILABLE = "is_gcm_availale";
    public static final String GCM_REG_ID = "gcm_reg_id";
    public static final String LI_TOKEN = "li_token";
    public static final String LI_TOKEN_SECRET = "li_token_secret";
    public static final String LI_REQTOKEN_SECRET = "li_reqtoken_secret";




    public static boolean getBoolean(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(prefernce_name, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static void setBoolean(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefernce_name, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(prefernce_name, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(prefernce_name, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }
}
