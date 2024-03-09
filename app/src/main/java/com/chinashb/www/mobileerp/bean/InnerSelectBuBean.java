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

    @SerializedName("Bu_ID") private int buID;
    @SerializedName("CF_ID") private int cfID;
    @SerializedName("Company_Chinese_Name") private String companyName;
    @SerializedName("Bu_Name") private String buName;

    public int getBuID() {
        return buID;
    }

    public InnerSelectBuBean setBuID(int buID) {
        this.buID = buID;
        return this;
    }

    public int getCfID() {
        return cfID;
    }

    public InnerSelectBuBean setCfID(int cfID) {
        this.cfID = cfID;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public InnerSelectBuBean setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getBuName() {
        return buName;
    }

    public InnerSelectBuBean setBuName(String buName) {
        this.buName = buName;
        return this;
    }

    protected InnerSelectBuBean(Parcel in) {
        buID = in.readInt();
        cfID = in.readInt();
        companyName = in.readString();
        buName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(buID);
        dest.writeInt(cfID);
        dest.writeString(companyName);
        dest.writeString(buName);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
