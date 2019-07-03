package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

public class WorkCenter implements Serializable {
    private boolean result;
    /**
     * WC_ID : 6
     * List_No : 1
     */


    private int List_No;

    private int WC_ID;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    private String ErrorInfo;

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String ErrorInfo) {
        this.ErrorInfo = ErrorInfo;
    }


    private String WC_Name;

    public String getWC_Name() {
        return WC_Name;
    }

    public void setWC_Name(String WC_Name) {
        this.WC_Name = WC_Name;
    }


    private int Bu_ID;

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int Bu_ID) {
        this.Bu_ID = Bu_ID;
    }


    private String BuName;

    public String getBuName() {
        return BuName;
    }

    public void setBuName(String BuName) {
        this.BuName = BuName;
    }


    public int getList_No() {
        return List_No;
    }

    public void setList_No(int List_No) {
        this.List_No = List_No;
    }

    public int getWC_ID() {
        return WC_ID;
    }

    public void setWC_ID(int WC_ID) {
        this.WC_ID = WC_ID;
    }
}
