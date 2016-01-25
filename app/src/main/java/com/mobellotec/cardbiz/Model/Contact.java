package com.mobellotec.cardbiz.Model;

/**
 * Created by MobelloTech on 12-08-2015.
 */
public class Contact {

    private String id;
    private String name;
    private String phone;
    private int isSelected;

    public Contact(){}

    public Contact(String id,String name, String phone,int isSelected) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
