package com.mobellotec.cardbiz.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MobelloTech on 25-10-2015.
 */
public class GroupInfoResult {

    private String status;

    public InfoResult getResult() {
        return result;
    }

    public void setResult(InfoResult result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private InfoResult result;

    public class InfoResult{
        String groupName;
        String groupID;
        String groupCode;
        List<GroupInfo> members = new ArrayList<>();

        public String getGroupCode() {
            return groupCode;
        }

        public void setGroupCode(String groupCode) {
            this.groupCode = groupCode;
        }


        public List<GroupInfo> getMembers() {
            return members;
        }

        public void setMembers(List<GroupInfo> members) {
            this.members = members;
        }

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
}
