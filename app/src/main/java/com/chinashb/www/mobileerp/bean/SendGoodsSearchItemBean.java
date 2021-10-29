package com.chinashb.www.mobileerp.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class SendGoodsSearchItemBean implements Parcelable {

    @SerializedName("Item_ID") private int Item_ID;
    @SerializedName("IV_ID") private long IV_ID;
    @SerializedName("Item_Name") private String Item_Name;
    @SerializedName("Item_Spec2") private String Item_Spec2;
    @SerializedName("Item_DrawNo") private String Item_DrawNo;
    @SerializedName("Item_Version") private String Item_Version;
//    @SerializedName("Newest_Version") private String Newest_Version;
    @SerializedName("Item_Unit") private String Item_Unit;

    protected SendGoodsSearchItemBean(Parcel in) {
        Item_ID = in.readInt();
        IV_ID = in.readLong();
        Item_Name = in.readString();
        Item_Spec2 = in.readString();
        Item_DrawNo = in.readString();
        Item_Version = in.readString();
        Item_Unit = in.readString();
    }

    public static final Creator<SendGoodsSearchItemBean> CREATOR = new Creator<SendGoodsSearchItemBean>() {
        @Override
        public SendGoodsSearchItemBean createFromParcel(Parcel in) {
            return new SendGoodsSearchItemBean(in);
        }

        @Override
        public SendGoodsSearchItemBean[] newArray(int size) {
            return new SendGoodsSearchItemBean[size];
        }
    };

    public int getItem_ID() {
        return Item_ID;
    }

    public SendGoodsSearchItemBean setItem_ID(int item_ID) {
        Item_ID = item_ID;
        return this;
    }

    public long getIV_ID() {
        return IV_ID;
    }

    public SendGoodsSearchItemBean setIV_ID(long IV_ID) {
        this.IV_ID = IV_ID;
        return this;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public SendGoodsSearchItemBean setItem_Name(String item_Name) {
        Item_Name = item_Name;
        return this;
    }

    public String getItem_Spec2() {
        return Item_Spec2;
    }

    public SendGoodsSearchItemBean setItem_Spec2(String item_Spec2) {
        Item_Spec2 = item_Spec2;
        return this;
    }

    public String getItem_DrawNo() {
        return Item_DrawNo;
    }

    public SendGoodsSearchItemBean setItem_DrawNo(String item_DrawNo) {
        Item_DrawNo = item_DrawNo;
        return this;
    }

    public String getItem_Version() {
        return Item_Version;
    }

    public SendGoodsSearchItemBean setItem_Version(String item_Version) {
        Item_Version = item_Version;
        return this;
    }

    public String getItem_Unit() {
        return Item_Unit;
    }

    public SendGoodsSearchItemBean setItem_Unit(String item_Unit) {
        Item_Unit = item_Unit;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Item_ID);
        dest.writeLong(IV_ID);
        dest.writeString(Item_Name);
        dest.writeString(Item_Spec2);
        dest.writeString(Item_DrawNo);
        dest.writeString(Item_Version);
        dest.writeString(Item_Unit);
    }
}
