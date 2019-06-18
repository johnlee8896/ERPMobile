package com.chinashb.www.mobileerp.basicobject;

import java.io.Serializable;

public class Ist_Place implements Serializable {
    private boolean result;
    private String ErrorInfo;
    private long Ist_ID;
    private long Sub_Ist_ID;
    private String IstName;
    private int Bu_ID;
    private String BuName;

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorInfo() {
        return ErrorInfo;
    }

    public void setErrorInfo(String ErrorInfo) {
        this.ErrorInfo = ErrorInfo;
    }

    public long getIst_ID() {
        return Ist_ID;
    }

    public void setIst_ID(long Ist_ID) {
        this.Ist_ID = Ist_ID;
    }

    public long getSub_Ist_ID() {
        return Sub_Ist_ID;
    }

    public void setSub_Ist_ID(long Sub_Ist_ID) {
        this.Sub_Ist_ID = Sub_Ist_ID;
    }

    public String getIstName() {
        return IstName;
    }

    public void setIstName(String IstName) {
        this.IstName = IstName;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int Bu_ID) {
        this.Bu_ID = Bu_ID;
    }

    public String getBuName() {
        return BuName;
    }

    public void setBuName(String BuName) {
        this.BuName = BuName;
    }


}
