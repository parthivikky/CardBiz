package com.mobellotec.cardbiz.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MobelloTech on 25-10-2015.
 */
public class GroupResult {
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Group> getResult() {
        return result;
    }

    public void setResult(List<Group> result) {
        this.result = result;
    }

    String status;
    List<Group> result = new ArrayList<>();
}
