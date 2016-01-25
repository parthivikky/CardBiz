package com.mobellotec.cardbiz.Model;

/**
 * Created by MobelloTech on 24-10-2015.
 */
public class Register {
    String status;
    String userID;

    public String getErrorMsg() {
        return errorMsg;
    }

    String errorMsg;


    public String getUserID() {
        return userID;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestID() {
        return requestID;
    }

    String requestID;

}
