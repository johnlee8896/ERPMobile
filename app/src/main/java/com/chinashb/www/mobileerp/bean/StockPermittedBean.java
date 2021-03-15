package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2021/3/2 15:47
 * @author 作者: xxblwf
 * @description 有仓库录入权限的人
 */

public class StockPermittedBean implements Parcelable {
    @SerializedName("FA_ID") private int FA_ID;
    @SerializedName("HR_ID") private int HR_ID;
    @SerializedName("HR_Name") private String HR_Name;
    @SerializedName("Leave") private boolean Leave;
    @SerializedName("Scope_Name") private String Scope_Name;
    @SerializedName("RangeName") private String RangeName;
    @SerializedName("Permit") private boolean Permit;
    @SerializedName("StartUse") private boolean StartUse;

    protected StockPermittedBean(Parcel in) {
        FA_ID = in.readInt();
        HR_ID = in.readInt();
        HR_Name = in.readString();
        Leave = in.readByte() != 0;
        Scope_Name = in.readString();
        RangeName = in.readString();
        Permit = in.readByte() != 0;
        StartUse = in.readByte() != 0;
    }

    public static final Creator<StockPermittedBean> CREATOR = new Creator<StockPermittedBean>() {
        @Override
        public StockPermittedBean createFromParcel(Parcel in) {
            return new StockPermittedBean(in);
        }

        @Override
        public StockPermittedBean[] newArray(int size) {
            return new StockPermittedBean[size];
        }
    };

    public int getFA_ID() {
        return FA_ID;
    }

    public void setFA_ID(int FA_ID) {
        this.FA_ID = FA_ID;
    }

    public int getHR_ID() {
        return HR_ID;
    }

    public void setHR_ID(int HR_ID) {
        this.HR_ID = HR_ID;
    }

    public String getHR_Name() {
        return HR_Name;
    }

    public void setHR_Name(String HR_Name) {
        this.HR_Name = HR_Name;
    }

    public boolean isLeave() {
        return Leave;
    }

    public void setLeave(boolean leave) {
        Leave = leave;
    }

    public String getScope_Name() {
        return Scope_Name;
    }

    public void setScope_Name(String scope_Name) {
        Scope_Name = scope_Name;
    }

    public String getRangeName() {
        return RangeName;
    }

    public void setRangeName(String rangeName) {
        RangeName = rangeName;
    }

    public boolean isPermit() {
        return Permit;
    }

    public void setPermit(boolean permit) {
        Permit = permit;
    }

    public boolean isStartUse() {
        return StartUse;
    }

    public void setStartUse(boolean startUse) {
        StartUse = startUse;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(FA_ID);
        dest.writeInt(HR_ID);
        dest.writeString(HR_Name);
        dest.writeByte((byte) (Leave ? 1 : 0));
        dest.writeString(Scope_Name);
        dest.writeString(RangeName);
        dest.writeByte((byte) (Permit ? 1 : 0));
        dest.writeByte((byte) (StartUse ? 1 : 0));
    }
}
