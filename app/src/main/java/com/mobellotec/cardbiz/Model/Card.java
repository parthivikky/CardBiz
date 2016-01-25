package com.mobellotec.cardbiz.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MobelloTech on 28-07-2015.
 */
public class Card implements Parcelable {
    String cardId;
    String firstName;
    String lastName;
    String email;
    String mobile;
    String personFacebook;
    String personTwitter;
    String personLinkenin;
    String companyName;
    String role;
    String landline;
    String officeMail;
    String fax;
    String website;
    String aboutCompany;
    String companyFacebook;
    String companyTwitter;
    String companyLinkedin;
    String block;
    String street;
    String city;
    String country;
    String postalCode;
    String templateId;
    String personImage;
    String companyImage;
    String templateName;
    String cardType;
    String templateFront;
    String templateBack;
    String tempFrontDetails;
    String tempBackDetails;
    String filePath;
    String tempModelFront;
    String tempModelBack;

    public String getTempModelFront() {
        return tempModelFront;
    }

    public void setTempModelFront(String tempModelFront) {
        this.tempModelFront = tempModelFront;
    }

    public String getTempModelBack() {
        return tempModelBack;
    }

    public void setTempModelBack(String tempModelBack) {
        this.tempModelBack = tempModelBack;
    }

    public Card() {

    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    Bitmap personImageBitmap, companyLogoBitmap;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getAboutCompany() {
        return aboutCompany;
    }

    public void setAboutCompany(String aboutCompany) {
        this.aboutCompany = aboutCompany;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompanyFacebook() {
        return companyFacebook;
    }

    public void setCompanyFacebook(String companyFacebook) {
        this.companyFacebook = companyFacebook;
    }

    public String getCompanyLinkedin() {
        return companyLinkedin;
    }

    public void setCompanyLinkedin(String companyLinkedin) {
        this.companyLinkedin = companyLinkedin;
    }

    public Bitmap getCompanyLogoBitmap() {
        return companyLogoBitmap;
    }

    public void setCompanyLogoBitmap(Bitmap companyLogoBitmap) {
        this.companyLogoBitmap = companyLogoBitmap;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyTwitter() {
        return companyTwitter;
    }

    public void setCompanyTwitter(String companyTwitter) {
        this.companyTwitter = companyTwitter;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOfficeMail() {
        return officeMail;
    }

    public void setOfficeMail(String officeMail) {
        this.officeMail = officeMail;
    }

    public String getPersonFacebook() {
        return personFacebook;
    }

    public void setPersonFacebook(String personFacebook) {
        this.personFacebook = personFacebook;
    }

    public Bitmap getPersonImageBitmap() {
        return personImageBitmap;
    }

    public void setPersonImageBitmap(Bitmap personImageBitmap) {
        this.personImageBitmap = personImageBitmap;
    }

    public String getPersonLinkenin() {
        return personLinkenin;
    }

    public void setPersonLinkenin(String personLinkenin) {
        this.personLinkenin = personLinkenin;
    }


    public String getPersonTwitter() {
        return personTwitter;
    }

    public void setPersonTwitter(String personTwitter) {
        this.personTwitter = personTwitter;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPersonImage() {
        return personImage;
    }

    public void setPersonImage(String personImage) {
        this.personImage = personImage;
    }

    public String getCompanyImage() {
        return companyImage;
    }

    public void setCompanyImage(String companyImage) {
        this.companyImage = companyImage;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateFront() {
        String[] tempFront = templateFront.split("\\^");
        return tempFront[0];
    }

    public void setTemplateFront(String templateFront) {
        this.templateFront = templateFront;
    }

    public String getTemplateBack() {
        String[] tempBack = templateBack.split("\\^");
        return tempBack[0];
    }

    public void setTemplateBack(String templateBack) {
        this.templateBack = templateBack;
    }

    public String getTempFrontDetails() {
        return tempFrontDetails;
    }

    public void setTempFrontDetails(String tempFrontDetails) {
        this.tempFrontDetails = tempFrontDetails;
    }

    public String getTempBackDetails() {
        return tempBackDetails;
    }

    public void setTempBackDetails(String tempBackDetails) {
        this.tempBackDetails = tempBackDetails;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.cardId, this.firstName, this.lastName, this.email, this.mobile, this.personFacebook,
                this.personTwitter, this.personLinkenin, this.companyName, this.role, this.landline, this.officeMail,
                this.fax, this.website, this.aboutCompany, this.companyFacebook, this.companyTwitter, this.companyLinkedin,
                this.block, this.street, this.city, this.country, this.postalCode, this.templateId, this.personImage,
                this.companyImage, this.templateName, this.cardType, this.templateFront, this.templateBack, this.tempFrontDetails,
                this.tempBackDetails, this.filePath,this.tempModelFront,this.tempModelBack});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    public Card(Parcel in) {
        String[] data = new String[35];
        in.readStringArray(data);
        this.cardId = data[0];
        this.firstName = data[1];
        this.lastName = data[2];
        this.email = data[3];
        this.mobile = data[4];
        this.personFacebook = data[5];
        this.personTwitter = data[6];
        this.personLinkenin = data[7];
        this.companyName = data[8];
        this.role = data[9];
        this.landline = data[10];
        this.officeMail = data[11];
        this.fax = data[12];
        this.website = data[13];
        this.aboutCompany = data[14];
        this.companyFacebook = data[15];
        this.companyTwitter = data[16];
        this.companyLinkedin = data[17];
        this.block = data[18];
        this.street = data[19];
        this.city = data[20];
        this.country = data[21];
        this.postalCode = data[22];
        this.templateId = data[23];
        this.personImage = data[24];
        this.companyImage = data[25];
        this.templateName = data[26];
        this.cardType = data[27];
        this.templateFront = data[28];
        this.templateBack = data[29];
        this.tempFrontDetails = data[30];
        this.tempBackDetails = data[31];
        this.filePath = data[32];
        this.tempModelFront = data[33];
        this.tempModelBack = data[34];
    }


}
