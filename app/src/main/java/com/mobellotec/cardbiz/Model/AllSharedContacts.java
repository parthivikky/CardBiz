package com.mobellotec.cardbiz.Model;

import java.util.ArrayList;

/**
 * Created by Parthi on 20-Nov-15.
 */
public class AllSharedContacts {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GroupInfo> getResult() {
        return result;
    }

    public void setResult(ArrayList<GroupInfo> result) {
        this.result = result;
    }

    private String status;
    private ArrayList<GroupInfo> result = new ArrayList<>();
}
