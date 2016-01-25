package com.mobellotec.cardbiz.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class CardRadar {

    private String status;
    private ArrayList<CardRadarList> result = new ArrayList<>();

    public ArrayList<CardRadarList> getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public class CardRadarList{

        private String firstName;
        private String lastName;
        private String userID;
        private String latPosition;
        private String longPosition;
        private String distance;
        private String phoneNo;
        private String cardID;
        private String companyName;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getUserID() {
            return userID;
        }

        public String getLatPosition() {
            return latPosition;
        }

        public String getLongPosition() {
            return longPosition;
        }

        public String getDistance() {
            return distance;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public String getCardID() {
            return cardID;
        }

        public String getCompanyName() {
            return companyName;
        }

    }
}
