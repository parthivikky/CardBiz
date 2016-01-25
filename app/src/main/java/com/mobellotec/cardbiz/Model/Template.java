package com.mobellotec.cardbiz.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sai Sheshan on 26-Aug-15.
 */
public class Template implements Parcelable{

    private String strFilePath;
    private String strTemplateId;
    private String strFrontImage;
    private String strBackImage;
    private String strPrice;
    private String strName;
    private String strFrontModel;
    private String strBackModel;
    private String strFrontDetails;
    private String strBackDetails;

    public String getFrontDetails() {
        return strFrontDetails;
    }

    public void setFrontDetails(String strFrontDetails) {
        this.strFrontDetails = strFrontDetails;
    }

    public String getBackDetails() {
        return strBackDetails;
    }

    public void setBackDetails(String strBackDetails) {
        this.strBackDetails = strBackDetails;
    }

    public String getFrontModel() {
        return strFrontModel;
    }

    public void setFrontModel(String strFrontModel) {
        this.strFrontModel = strFrontModel;
    }

    public String getBackModel() {
        return strBackModel;
    }

    public void setBackModel(String strBackModel) {
        this.strBackModel = strBackModel;
    }



    public String getFilePath() {
        return strFilePath;
    }

    public void setFilePath(String strFilePath) {
        this.strFilePath = strFilePath;
    }

    public String getTemplateId() {
        return strTemplateId;
    }

    public void setTemplateId(String strTemplateId) {
        this.strTemplateId = strTemplateId;
    }

    public String getFrontImage() {
        return strFrontImage;
    }

    public void setFrontImage(String strFrontImage) {
        this.strFrontImage = strFrontImage;
    }

    public String getBackImage() {
        return strBackImage;
    }

    public void setBackImage(String strBackImage) {
        this.strBackImage = strBackImage;
    }

    public String getPrice() {
        return strPrice;
    }

    public void setPrice(String strPrice) {
        this.strPrice = strPrice;
    }

    public String getName() {
        return strName;
    }

    public void setName(String strName) {
        this.strName = strName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.strFilePath, this.strTemplateId, this.strFrontImage, this.strBackImage,
                this.strPrice, this.strName, this.strFrontModel, this.strBackModel, this.strFrontDetails,
                this.strBackDetails});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Template createFromParcel(Parcel in) {
            return new Template(in);
        }

        @Override
        public Template[] newArray(int size) {
            return new Template[size];
        }
    };

    public Template(Parcel in) {
        String[] data = new String[10];
        in.readStringArray(data);
        this.strFilePath = data[0];
        this.strTemplateId = data[1];
        this.strFrontImage = data[2];
        this.strBackImage = data[3];
        this.strPrice = data[4];
        this.strName = data[5];
        this.strFrontModel = data[6];
        this.strBackModel = data[7];
        this.strFrontDetails = data[8];
        this.strBackDetails = data[9];
    }

    public Template(){

    }
}