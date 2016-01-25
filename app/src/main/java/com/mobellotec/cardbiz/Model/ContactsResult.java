package com.mobellotec.cardbiz.Model;

import java.util.List;

/**
 * Created by MobelloTech on 26-10-2015.
 */
public class ContactsResult {

    String status;

    public List<ContactsList> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactsList> contacts) {
        this.contacts = contacts;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    List<ContactsList> contacts;


}
