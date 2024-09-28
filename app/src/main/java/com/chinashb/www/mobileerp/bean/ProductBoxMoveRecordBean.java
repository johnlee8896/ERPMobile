package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 8/19/24 5:18 PM
 * @author 作者: liweifeng
 * @description 成品移库记录j查询
 */
public class ProductBoxMoveRecordBean implements Parcelable {
    @SerializedName("区域") private String toAreaName;
    @SerializedName("HR_Name") private String 移库人;
    @SerializedName("Insert_Time") private String 移库时间;
    @SerializedName("BoxID") private int boxID;
    @SerializedName("Bu_ID") private int buID;

    protected ProductBoxMoveRecordBean(Parcel in) {
        toAreaName = in.readString();
        移库人 = in.readString();
        移库时间 = in.readString();
        boxID = in.readInt();
        buID = in.readInt();
    }

    public static final Creator<ProductBoxMoveRecordBean> CREATOR = new Creator<ProductBoxMoveRecordBean>() {
        @Override
        public ProductBoxMoveRecordBean createFromParcel(Parcel in) {
            return new ProductBoxMoveRecordBean(in);
        }

        @Override
        public ProductBoxMoveRecordBean[] newArray(int size) {
            return new ProductBoxMoveRecordBean[size];
        }
    };

    public String getToAreaName() {
        return toAreaName;
    }

    public ProductBoxMoveRecordBean setToAreaName(String toAreaName) {
        this.toAreaName = toAreaName;
        return this;
    }

    public String get移库人() {
        return 移库人;
    }

    public ProductBoxMoveRecordBean set移库人(String 移库人) {
        this.移库人 = 移库人;
        return this;
    }

    public String get移库时间() {
        return 移库时间;
    }

    public ProductBoxMoveRecordBean set移库时间(String 移库时间) {
        this.移库时间 = 移库时间;
        return this;
    }

    public int getBoxID() {
        return boxID;
    }

    public ProductBoxMoveRecordBean setBoxID(int boxID) {
        this.boxID = boxID;
        return this;
    }

    public int getBuID() {
        return buID;
    }

    public ProductBoxMoveRecordBean setBuID(int buID) {
        this.buID = buID;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toAreaName);
        dest.writeString(移库人);
        dest.writeString(移库时间);
        dest.writeInt(boxID);
        dest.writeInt(buID);
    }
}
