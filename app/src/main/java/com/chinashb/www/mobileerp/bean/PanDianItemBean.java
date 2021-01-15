package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/12/28 16:08
 * @author 作者: xxblwf
 * @description 盘点所选的Item Bean
 */

public class PanDianItemBean implements Parcelable {
    @SerializedName("Item_ID") private int Item_ID;
    @SerializedName("IV_ID") private int IV_ID;
    @SerializedName("Item_Name") private String Item_Name;
    @SerializedName("Version") private String Version;
    @SerializedName("Item_Unit") private String Item_Unit;
    @SerializedName("Item_Unit_Exchange") private int Item_Unit_Exchange;

    protected PanDianItemBean(Parcel in) {
        Item_ID = in.readInt();
        IV_ID = in.readInt();
        Item_Name = in.readString();
        Version = in.readString();
        Item_Unit = in.readString();
        Item_Unit_Exchange = in.readInt();
    }

    public static final Creator<PanDianItemBean> CREATOR = new Creator<PanDianItemBean>() {
        @Override
        public PanDianItemBean createFromParcel(Parcel in) {
            return new PanDianItemBean(in);
        }

        @Override
        public PanDianItemBean[] newArray(int size) {
            return new PanDianItemBean[size];
        }
    };

    public int getItem_ID() {
        return Item_ID;
    }

    public void setItem_ID(int item_ID) {
        Item_ID = item_ID;
    }

    public int getIV_ID() {
        return IV_ID;
    }

    public void setIV_ID(int IV_ID) {
        this.IV_ID = IV_ID;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getItem_Unit() {
        return Item_Unit;
    }

    public void setItem_Unit(String item_Unit) {
        Item_Unit = item_Unit;
    }

    public int getItem_Unit_Exchange() {
        return Item_Unit_Exchange;
    }

    public void setItem_Unit_Exchange(int item_Unit_Exchange) {
        Item_Unit_Exchange = item_Unit_Exchange;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Item_ID);
        dest.writeInt(IV_ID);
        dest.writeString(Item_Name);
        dest.writeString(Version);
        dest.writeString(Item_Unit);
        dest.writeInt(Item_Unit_Exchange);
    }
}
