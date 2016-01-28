package com.mobellotec.cardbiz.Utility;

/**
 * Created by MobelloTech on 13-07-2015.
 */
public class Constants {
    public static final int SPLASH_TIME_OUT = 3000;
    public static final String SECRET_KEY = "cardbizAPP";
//    public static final String BASE_URL = "http://54.169.86.42/cardbiz/index.php/api";  //Test url
    public static final String BASE_URL = "http://cardbizapp.com/app/index.php/api";  //live url
    public static final String LOGIN = "/users/login/";
    public static final String REGISTRATION = "/users/registration/";
    public static final String VERIFICATION = "/users/verifyCode/";
    public static final String RESEND_VERIFY_CODE = "/users/resendCode/";
    public static final String FORGOT_PASSWORD = "/users/forgotPassword/";
    public static final String CREATE_CARD = "/myCard/create/";
    public static final String UPDATE_CARD = "/myCard/update/";
    public static final String UPDATE_CONTACT = "/users/contacts/";
    public static final String MY_CARD_LIST = "/myCard/display/";
    public static final String MY_CARD_INFO = "/myCard/info/";
    public static final String TEMPLATE_UPDATE = "/myCard/updateTemplate/";
    public static final String MY_CARD_DELETE = "/myCard/delete/";
    public static final String MY_CARD_TEMPLATE = "/myCard/templates/";
    public static final String UPDATE_LOCATION = "/users/updateLocation/";
    public static final String CREATE_GROUP = "/groups/create/";
    public static final String JOIN_GROUP = "/groups/join/";
    public static final String LIST_GROUP = "/groups/listAll/";
    public static final String GROUP_INFO = "/groups/info/";
    public static final String DELETE_GROUP = "/groups/delete/";
    public static final String GROUP_UPDATE_CARD = "/groups/updateCard/";
    public static final String CARD_RADAR = "/users/cardRadar/";
    public static final String BUMP_DATA = "/users/bump";
    public static final String ADD_SHARED_CARD = "users/AddContactCard/";
    public static final String INVITE = "users/inviteContacts/";

    // For common variables
    public static final String STATUS = "status";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAILURE = "failure";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_NUMBER = "contact_number";
    public static final String CONTACT_SELECTED = "contact_selected";
    public static final String CONTACT_IMAGE = "contact_image";
    public static final String CONTACT_USER_ID = "user_id";
    public static final String CONTACT_SUFFIX_NAME = "user_suffix_name";
    public static final String CONTACT_ID = "contact_id";
    public static final String CONTACT_EMAIL_ID = "contact_email_id";
    public static final String CONTACT_EMAIL_NAME = "contact_email_name";
    public static final String CONTACT_EMAIL_EMAIL = "contact_email_email";
    public static final String CONTACT_EMAIL_SELECTED = "contact_email_selected";
    public static final String USER_TYPE = "user_type";
    public static final String NEW_USER = "0";
    public static final String EXIST_USER = "1";
    public static final String CARD_TYPE_AUTO = "auto";
    public static final String CARD_TYPE_MANUAL = "manual";
    public static final String CARD_ID = "card_id";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String BUMP_RESULT = "bump_data";
    // For fragment show
    public static final int FRAGMENT_MY_CARD = 1;
    public static final int FRAGMENT_CARD_HOLDER = 2;
    public static final int FRAGMENT_GROUP = 3;
    public static final int FRAGMENT_NEARBY = 4;
    public static final int FRAGMENT_BUMP = 5;
    public static final int ACTIVITY_ADD_NEW_CARD = 6;
    public static final int ACTIVITY_FREE_TEMPLATES = 7;
    public static final int FRAGMENT_CREATE_GROUP = 8;
    public static final int ACTIVITY_CUSTOM_CAMERA = 9;
    public static final int FRAGMENT_SHARE = 10;
    public static final int FRAGMENT_JOIN_GROUP = 12;
    public static final int ACTIVITY_Edit_CARD = 13;

    public static final int PICK_PERSON_DP_REQUEST_CODE = 101;
    public static final int PICK_COMPANY_DP_REQUEST_CODE = 102;
    public static final int COUNTRY_REQUEST_CODE = 103;

    public static final String APP_GOOGLE_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.mobellotec.cardbiz&hl=en";
    public static final String APP_APPLE_STORE_URL = "http://itunes.apple.com/app/id1062751426";

    public static final int INVITE_SMS = 1;
    public static final int INVITE_EMAIL = 2;
    public static final int INVITE_FACEBOOK = 3;

}
