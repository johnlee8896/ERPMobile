package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/4/14 14:41
 * @author 作者: xxblwf
 * @description 内部调拨 选择出库车间bean
 */

public class InnerSelectBuBean implements Parcelable{

    @SerializedName("CF_ID") private int cfID;
    @SerializedName("Company_Chinese_Name") private String companyName;
    @SerializedName("Bu_Name") private String buName;

    protected InnerSelectBuBean(Parcel in) {
        cfID = in.readInt();
        companyName = in.readString();
        buName = in.readString();
    }

    public static final Creator<InnerSelectBuBean> CREATOR = new Creator<InnerSelectBuBean>() {
        @Override
        public InnerSelectBuBean createFromParcel(Parcel in) {
            return new InnerSelectBuBean(in);
        }

        @Override
        public InnerSelectBuBean[] newArray(int size) {
            return new InnerSelectBuBean[size];
        }
    };

    public int getCfID() {
        return cfID;
    }

    public void setCfID(int cfID) {
        this.cfID = cfID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cfID);
        dest.writeString(companyName);
        dest.writeString(buName);
    }
}
