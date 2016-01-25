package com.mobellotec.cardbiz.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MobelloTech on 25-10-2015.
 */
public class CardList {

    private String status;
    private List<CardInfo> cardInfo = new ArrayList<CardInfo>();

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The cardInfo
     */
    public List<CardInfo> getCardInfo() {
        return cardInfo;
    }

    /**
     *
     * @param cardInfo
     * The cardInfo
     */
    public void setCardInfo(List<CardInfo> cardInfo) {
        this.cardInfo = cardInfo;
    }


    public class CardInfo {

        private String cardID;
        private String type;
        private String filePath;
        private String templateID;
        private String templateName;
        private String imageFront;
        private String imageBack;
        private List<TemplateDetail> templateDetails = new ArrayList<TemplateDetail>();
        private String firstName;
        private String lastName;
        private String userImage;
        private String phoneNo;
        private String email;
        private String userTwitterID;
        private String userFacebookID;
        private String userLinkedInID;
        private String designation;
        private String companyName;
        private String companyPhoneNo;
        private String companyEmail;
        private String companyFax;
        private String companyWebsite;
        private String companyTwitterID;
        private String companyFacebookID;
        private String companyLinkedInID;
        private String companyLogo;
        private String block;
        private String streetName;
        private String city;
        private String country;
        private String postCode;
        private String aboutCompany;
        private String cardFront;
        private String cardBack;

        /**
         *
         * @return
         * The cardID
         */
        public String getCardID() {
            return cardID;
        }

        /**
         *
         * @param cardID
         * The cardID
         */
        public void setCardID(String cardID) {
            this.cardID = cardID;
        }

        /**
         *
         * @return
         * The type
         */
        public String getType() {
            return type;
        }

        /**
         *
         * @param type
         * The type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         *
         * @return
         * The filePath
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         *
         * @param filePath
         * The filePath
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        /**
         *
         * @return
         * The templateID
         */
        public String getTemplateID() {
            return templateID;
        }

        /**
         *
         * @param templateID
         * The templateID
         */
        public void setTemplateID(String templateID) {
            this.templateID = templateID;
        }

        /**
         *
         * @return
         * The templateName
         */
        public String getTemplateName() {
            return templateName;
        }

        /**
         *
         * @param templateName
         * The templateName
         */
        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        /**
         *
         * @return
         * The imageFront
         */
        public String getImageFront() {
            return imageFront;
        }

        /**
         *
         * @param imageFront
         * The imageFront
         */
        public void setImageFront(String imageFront) {
            this.imageFront = imageFront;
        }

        /**
         *
         * @return
         * The imageBack
         */
        public String getImageBack() {
            return imageBack;
        }

        /**
         *
         * @param imageBack
         * The imageBack
         */
        public void setImageBack(String imageBack) {
            this.imageBack = imageBack;
        }

        /**
         *
         * @return
         * The templateDetails
         */
        public List<TemplateDetail> getTemplateDetails() {
            return templateDetails;
        }

        /**
         *
         * @param templateDetails
         * The templateDetails
         */
        public void setTemplateDetails(List<TemplateDetail> templateDetails) {
            this.templateDetails = templateDetails;
        }

        /**
         *
         * @return
         * The firstName
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         *
         * @param firstName
         * The firstName
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         *
         * @return
         * The lastName
         */
        public String getLastName() {
            return lastName;
        }

        /**
         *
         * @param lastName
         * The lastName
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        /**
         *
         * @return
         * The userImage
         */
        public String getUserImage() {
            return userImage;
        }

        /**
         *
         * @param userImage
         * The userImage
         */
        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        /**
         *
         * @return
         * The phoneNo
         */
        public String getPhoneNo() {
            return phoneNo;
        }

        /**
         *
         * @param phoneNo
         * The phoneNo
         */
        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        /**
         *
         * @return
         * The email
         */
        public String getEmail() {
            return email;
        }

        /**
         *
         * @param email
         * The email
         */
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         *
         * @return
         * The userTwitterID
         */
        public String getUserTwitterID() {
            return userTwitterID;
        }

        /**
         *
         * @param userTwitterID
         * The userTwitterID
         */
        public void setUserTwitterID(String userTwitterID) {
            this.userTwitterID = userTwitterID;
        }

        /**
         *
         * @return
         * The userFacebookID
         */
        public String getUserFacebookID() {
            return userFacebookID;
        }

        /**
         *
         * @param userFacebookID
         * The userFacebookID
         */
        public void setUserFacebookID(String userFacebookID) {
            this.userFacebookID = userFacebookID;
        }

        /**
         *
         * @return
         * The userLinkedInID
         */
        public String getUserLinkedInID() {
            return userLinkedInID;
        }

        /**
         *
         * @param userLinkedInID
         * The userLinkedInID
         */
        public void setUserLinkedInID(String userLinkedInID) {
            this.userLinkedInID = userLinkedInID;
        }

        /**
         *
         * @return
         * The designation
         */
        public String getDesignation() {
            return designation;
        }

        /**
         *
         * @param designation
         * The designation
         */
        public void setDesignation(String designation) {
            this.designation = designation;
        }

        /**
         *
         * @return
         * The companyName
         */
        public String getCompanyName() {
            return companyName;
        }

        /**
         *
         * @param companyName
         * The companyName
         */
        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        /**
         *
         * @return
         * The companyPhoneNo
         */
        public String getCompanyPhoneNo() {
            return companyPhoneNo;
        }

        /**
         *
         * @param companyPhoneNo
         * The companyPhoneNo
         */
        public void setCompanyPhoneNo(String companyPhoneNo) {
            this.companyPhoneNo = companyPhoneNo;
        }

        /**
         *
         * @return
         * The companyEmail
         */
        public String getCompanyEmail() {
            return companyEmail;
        }

        /**
         *
         * @param companyEmail
         * The companyEmail
         */
        public void setCompanyEmail(String companyEmail) {
            this.companyEmail = companyEmail;
        }

        /**
         *
         * @return
         * The companyFax
         */
        public String getCompanyFax() {
            return companyFax;
        }

        /**
         *
         * @param companyFax
         * The companyFax
         */
        public void setCompanyFax(String companyFax) {
            this.companyFax = companyFax;
        }

        /**
         *
         * @return
         * The companyWebsite
         */
        public String getCompanyWebsite() {
            return companyWebsite;
        }

        /**
         *
         * @param companyWebsite
         * The companyWebsite
         */
        public void setCompanyWebsite(String companyWebsite) {
            this.companyWebsite = companyWebsite;
        }

        /**
         *
         * @return
         * The companyTwitterID
         */
        public String getCompanyTwitterID() {
            return companyTwitterID;
        }

        /**
         *
         * @param companyTwitterID
         * The companyTwitterID
         */
        public void setCompanyTwitterID(String companyTwitterID) {
            this.companyTwitterID = companyTwitterID;
        }

        /**
         *
         * @return
         * The companyFacebookID
         */
        public String getCompanyFacebookID() {
            return companyFacebookID;
        }

        /**
         *
         * @param companyFacebookID
         * The companyFacebookID
         */
        public void setCompanyFacebookID(String companyFacebookID) {
            this.companyFacebookID = companyFacebookID;
        }

        /**
         *
         * @return
         * The companyLinkedInID
         */
        public String getCompanyLinkedInID() {
            return companyLinkedInID;
        }

        /**
         *
         * @param companyLinkedInID
         * The companyLinkedInID
         */
        public void setCompanyLinkedInID(String companyLinkedInID) {
            this.companyLinkedInID = companyLinkedInID;
        }

        /**
         *
         * @return
         * The companyLogo
         */
        public String getCompanyLogo() {
            return companyLogo;
        }

        /**
         *
         * @param companyLogo
         * The companyLogo
         */
        public void setCompanyLogo(String companyLogo) {
            this.companyLogo = companyLogo;
        }

        /**
         *
         * @return
         * The block
         */
        public String getBlock() {
            return block;
        }

        /**
         *
         * @param block
         * The block
         */
        public void setBlock(String block) {
            this.block = block;
        }

        /**
         *
         * @return
         * The streetName
         */
        public String getStreetName() {
            return streetName;
        }

        /**
         *
         * @param streetName
         * The streetName
         */
        public void setStreetName(String streetName) {
            this.streetName = streetName;
        }

        /**
         *
         * @return
         * The city
         */
        public String getCity() {
            return city;
        }

        /**
         *
         * @param city
         * The city
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         *
         * @return
         * The country
         */
        public String getCountry() {
            return country;
        }

        /**
         *
         * @param country
         * The country
         */
        public void setCountry(String country) {
            this.country = country;
        }

        /**
         *
         * @return
         * The postCode
         */
        public String getPostCode() {
            return postCode;
        }

        /**
         *
         * @param postCode
         * The postCode
         */
        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }

        /**
         *
         * @return
         * The aboutCompany
         */
        public String getAboutCompany() {
            return aboutCompany;
        }

        /**
         *
         * @param aboutCompany
         * The aboutCompany
         */
        public void setAboutCompany(String aboutCompany) {
            this.aboutCompany = aboutCompany;
        }

        /**
         *
         * @return
         * The cardFront
         */
        public String getCardFront() {
            return cardFront;
        }

        /**
         *
         * @param cardFront
         * The cardFront
         */
        public void setCardFront(String cardFront) {
            this.cardFront = cardFront;
        }

        /**
         *
         * @return
         * The cardBack
         */
        public String getCardBack() {
            return cardBack;
        }

        /**
         *
         * @param cardBack
         * The cardBack
         */
        public void setCardBack(String cardBack) {
            this.cardBack = cardBack;
        }

    }
    public class TemplateDetail {

        private String backgrounImage;
        private String companyLogo;
        private List<String> label = new ArrayList<String>();

        /**
         *
         * @return
         * The backgrounImage
         */
        public String getBackgrounImage() {
            return backgrounImage;
        }

        /**
         *
         * @param backgrounImage
         * The backgrounImage
         */
        public void setBackgrounImage(String backgrounImage) {
            this.backgrounImage = backgrounImage;
        }

        /**
         *
         * @return
         * The companyLogo
         */
        public String getCompanyLogo() {
            return companyLogo;
        }

        /**
         *
         * @param companyLogo
         * The companyLogo
         */
        public void setCompanyLogo(String companyLogo) {
            this.companyLogo = companyLogo;
        }

        /**
         *
         * @return
         * The label
         */
        public List<String> getLabel() {
            return label;
        }

        /**
         *
         * @param label
         * The label
         */
        public void setLabel(List<String> label) {
            this.label = label;
        }

    }
}
