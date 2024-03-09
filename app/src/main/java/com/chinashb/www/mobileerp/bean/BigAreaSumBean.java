package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2024/2/28 6:46 PM
 * @author 作者: liweifeng
 * @description 零件大区查询
 */
public class BigAreaSumBean implements Parcelable {
    @SerializedName("Item_ID") private int itemID;
    @SerializedName("SumQty") private float sumQty;
    @SerializedName("ISL_ID") private int ISL_ID;
    @SerializedName("LayOut_Name") private String layoutName;

    protected BigAreaSumBean(Parcel in) {
        itemID = in.readInt();
        sumQty = in.readFloat();
        ISL_ID = in.readInt();
        layoutName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemID);
        dest.writeFloat(sumQty);
        dest.writeInt(ISL_ID);
        dest.writeString(layoutName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BigAreaSumBean> CREATOR = new Creator<BigAreaSumBean>() {
        @Override
        public BigAreaSumBean createFromParcel(Parcel in) {
            return new BigAreaSumBean(in);
        }

        @Override
        public BigAreaSumBean[] newArray(int size) {
            return new BigAreaSumBean[size];
        }
    };

    public int getItemID() {
        return itemID;
    }

    public BigAreaSumBean setItemID(int itemID) {
        this.itemID = itemID;
        return this;
    }

    public float getSumQty() {
        return sumQty;
    }

    public BigAreaSumBean setSumQty(float sumQty) {
        this.sumQty = sumQty;
        return this;
    }

    public int getISL_ID() {
        return ISL_ID;
    }

    public BigAreaSumBean setISL_ID(int ISL_ID) {
        this.ISL_ID = ISL_ID;
        return this;
    }

    public String getLayoutName() {
        return layoutName;
    }

    public BigAreaSumBean setLayoutName(String layoutName) {
        this.layoutName = layoutName;
        return this;
    }
}
