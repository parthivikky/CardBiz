package com.mobellotec.cardbiz.Model;

/**
 * Created by Sai Sheshan on 27-Aug-15.
 */
public class Group {

    String groupName;
    String groupID;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(String membersCount) {
        this.membersCount = membersCount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getIs_creator() {
        return is_creator;
    }

    public void setIs_creator(String is_creator) {
        this.is_creator = is_creator;
    }

    String membersCount;
    String is_creator;


}
