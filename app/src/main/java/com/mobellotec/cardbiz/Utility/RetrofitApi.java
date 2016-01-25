package com.mobellotec.cardbiz.Utility;

import com.mobellotec.cardbiz.Model.AllSharedContacts;
import com.mobellotec.cardbiz.Model.ApiStatus;
import com.mobellotec.cardbiz.Model.CardRadar;
import com.mobellotec.cardbiz.Model.ContactsResult;
import com.mobellotec.cardbiz.Model.CreateGroup;
import com.mobellotec.cardbiz.Model.GroupInfoResult;
import com.mobellotec.cardbiz.Model.GroupResult;
import com.mobellotec.cardbiz.Model.Login;
import com.mobellotec.cardbiz.Model.Register;
import com.mobellotec.cardbiz.Model.ResendVerifyCode;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by MobelloTech on 24-10-2015.
 */
public class RetrofitApi {

    public static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){
        retrofit = new Retrofit.Builder()
                .baseUrl("http://cardbizapp.com/app/index.php/api/")        // live url
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static ApiInterface getApiInterfaceInstance(){
        return getRetrofitInstance().create(ApiInterface.class);
    }

    public interface ApiInterface {

        @FormUrlEncoded
        @POST("myCard/delete")
        public Call<ApiStatus> deleteCard(
                @Field("cardID") String cardID
        );

        @FormUrlEncoded
        @POST("users/registration")
        Call<Register> register(
                @Field("firstName") String firstName,
                @Field("lastName") String lastName,
                @Field("email") String email,
                @Field("password") String password,
                @Field("phoneNo") String phoneNo,
                @Field("userType") String userType,
                @Field("key") String key,
                @Field("deviceType") String deviceType,
                @Field("deviceID") String deviceID
        );

        @FormUrlEncoded
        @POST("users/forgotPassword")
        Call<ApiStatus> forgotPassword(
                @Field("email") String email,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("users/login")
        Call<Login> login(
                @Field("email") String email,
                @Field("password") String password,
                @Field("userType") String userType,
                @Field("key") String key,
                @Field("deviceType") String deviceType,
                @Field("deviceID") String deviceId
        );

        @FormUrlEncoded
        @POST("users/verifyCode")
        Call<ApiStatus> verifyCode(
                @Field("userID") String userID,
                @Field("requestID") String requestID,
                @Field("code") String code,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("users/resendCode")
        Call<ResendVerifyCode> resendVerifyCode(
                @Field("userID") String userID,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("groups/listAll")
        Call<GroupResult> groupList(
                @Field("userID") String userID,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("groups/info")
        Call<GroupInfoResult> groupInfo(
                @Field("userID") String userID,
                @Field("groupID") String groupID,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("groups/delete")
        Call<ApiStatus> groupDelete(
                @Field("userID") String userID,
                @Field("groupID") String groupID,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("groups/join")
        Call<CreateGroup> joinGroup(
                @Field("userID") String userID,
                @Field("groupName") String groupName,
                @Field("groupCode") String groupCode,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("groups/create")
        Call<CreateGroup> createGroup(
                @Field("creator") String creator,
                @Field("groupName") String groupName,
                @Field("groupCode") String groupCode,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("users/contacts")
        Call<ContactsResult> contacts(
                @Field("userID") String userID,
                @Field("contacts") JSONArray contacts
        );

        @FormUrlEncoded
        @POST("users/nearBy")
        Call<CardRadar> nearBy(
                @Field("userID") String userID,
                @Field("cardID") String cardID,
                @Field("radius") String radius,
                @Field("latLocation") String latLocation,
                @Field("longLocation") String longLocation,
                @Field("visibility") String visibility
        );

        @FormUrlEncoded
        @POST("groups/updateCard")
        Call<ApiStatus>updateCard(
                @Field("userID") String userID,
                @Field("groupID") String groupID,
                @Field("cardID") String cardID,
                @Field("key") String key
        );

        @FormUrlEncoded
        @POST("users/AddContactCard")
        Call<ApiStatus> addSharedCard(
                @Field("userID") String userID,
                @Field("cardID") String cardID
        );

        @FormUrlEncoded
        @POST("users/contactCardList")
        Call<AllSharedContacts> listSharedCard(
                @Field("userID") String userID
        );

        @FormUrlEncoded
        @POST("users/DeleteContactCard")
        Call<ApiStatus> deleteSharedCard(
                @Field("userID") String userID,
                @Field("cardID") String cardID
        );

        @FormUrlEncoded
        @POST("users/bump")
        Call<ApiStatus> sendBumpData(
                @Field("userID") String userID,
                @Field("cardID") String cardID,
                @Field("name") String name,
                @Field("phoneNo") String phoneNo,
                @Field("companyName") String companyName,
                @Field("latPosition") String latPosition,
                @Field("longPosition") String longPosition,
                @Field("time") String time,
                @Field("deviceID") String deviceID,
                @Field("deviceType") String deviceType
        );

        @POST("privacyPolicy")
        Call<ApiStatus> privacyPolicyCall();

        @POST("termsConditions")
        Call<ApiStatus> termsAndConditionsCall();

        @FormUrlEncoded
        @POST("users/inviteContacts")
        Call<ApiStatus> sendInvite(@Field("contacts") JSONArray contacts);

        @FormUrlEncoded
        @POST("logs/create")
        Call<ApiStatus> sendBugReport(
                @Field("userID") String userID,
                @Field("name") String name,
                @Field("log") String log,
                @Field("device") String device
        );
    }
}
