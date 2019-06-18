package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

public class s_WCList implements Serializable {
    /**
     * WCL_Type : 部件
     * LID : 2
     * Bu_ID : 1
     * ListName : 电枢线
     */

    private String WCL_Type;
    private int LID;
    private int Bu_ID;
    private String ListName;

    public String getWCL_Type() {
        return WCL_Type;
    }

    public void setWCL_Type(String WCL_Type) {
        this.WCL_Type = WCL_Type;
    }

    public int getLID() {
        return LID;
    }

    public void setLID(int LID) {
        this.LID = LID;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int Bu_ID) {
        this.Bu_ID = Bu_ID;
    }

    public String getListName() {
        return ListName;
    }

    public void setListName(String ListName) {
        this.ListName = ListName;
    }
}
