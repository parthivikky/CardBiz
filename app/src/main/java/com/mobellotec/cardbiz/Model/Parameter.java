package com.mobellotec.cardbiz.Model;

/**
 * Created by MobelloTech on 13-07-2015.
 */
public class Parameter {

    String key,value;

    public Parameter(String key,String value){
        this.key = key;
        this.value = value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }
}
