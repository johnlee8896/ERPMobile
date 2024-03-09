package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2024/2/27 4:41 PM
 * @author 作者: liweifeng
 * @description 大区
 */
public class SelectStorageAreaBean implements Parcelable {
    @SerializedName("ISL_ID") private int ISL_ID;
    @SerializedName("LayOut_Code") private String layoutCode;
    @SerializedName("LayOut_Name") private String layoutName;
    @SerializedName("LayOut_Description") private String layoutDescription;
    @SerializedName("Bu_ID") private int buID;
    @SerializedName("Ac_Type") private int acType;
    @SerializedName("Manager_HR_ID") private int managerHRID;
    @SerializedName("Manager_HR_Name") private String managerHRName;

    protected SelectStorageAreaBean(Parcel in) {
        ISL_ID = in.readInt();
        layoutCode = in.readString();
        layoutName = in.readString();
        layoutDescription = in.readString();
        buID = in.readInt();
        acType = in.readInt();
        managerHRID = in.readInt();
        managerHRName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ISL_ID);
        dest.writeString(layoutCode);
        dest.writeString(layoutName);
        dest.writeString(layoutDescription);
        dest.writeInt(buID);
        dest.writeInt(acType);
        dest.writeInt(managerHRID);
        dest.writeString(managerHRName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SelectStorageAreaBean> CREATOR = new Creator<SelectStorageAreaBean>() {
        @Override
        public SelectStorageAreaBean createFromParcel(Parcel in) {
            return new SelectStorageAreaBean(in);
        }

        @Override
        public SelectStorageAreaBean[] newArray(int size) {
            return new SelectStorageAreaBean[size];
        }
    };

    public int getISL_ID() {
        return ISL_ID;
    }

    public SelectStorageAreaBean setISL_ID(int ISL_ID) {
        this.ISL_ID = ISL_ID;
        return this;
    }

    public String getLayoutCode() {
        return layoutCode;
    }

    public SelectStorageAreaBean setLayoutCode(String layoutCode) {
        this.layoutCode = layoutCode;
        return this;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public SelectStorageAreaBean setLayoutName(String layoutName) {
        this.layoutName = layoutName;
        return this;
    }

    public String getLayoutDescription() {
        return layoutDescription;
    }

    public SelectStorageAreaBean setLayoutDescription(String layoutDescription) {
        this.layoutDescription = layoutDescription;
        return this;
    }

    public int getBuID() {
        return buID;
    }

    public SelectStorageAreaBean setBuID(int buID) {
        this.buID = buID;
        return this;
    }

    public int getAcType() {
        return acType;
    }

    public SelectStorageAreaBean setAcType(int acType) {
        this.acType = acType;
        return this;
    }

    public int getManagerHRID() {
        return managerHRID;
    }

    public SelectStorageAreaBean setManagerHRID(int managerHRID) {
        this.managerHRID = managerHRID;
        return this;
    }

    public String getManagerHRName() {
        return managerHRName;
    }

    public SelectStorageAreaBean setManagerHRName(String managerHRName) {
        this.managerHRName = managerHRName;
        return this;
    }
}
