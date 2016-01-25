package com.mobellotec.cardbiz.Model;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class GroupInfo {
    public String getContactNumber() {
        return phoneNo;
    }

    public void setContactNumber(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private String name;
    private String userID;
    private String phoneNo;
    private String cardID;
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }




    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }
}
