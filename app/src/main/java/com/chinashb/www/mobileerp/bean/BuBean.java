package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/16 15:01
 * @author 作者: xxblwf
 * @description 车间bean
 */

public class BuBean implements Parcelable {
    @SerializedName("Bu_ID") private int buId;
    @SerializedName("Bu_Name") private String buName;

    protected BuBean(Parcel in) {
        buId = in.readInt();
        buName = in.readString();
    }

    public static final Creator<BuBean> CREATOR = new Creator<BuBean>() {
        @Override
        public BuBean createFromParcel(Parcel in) {
            return new BuBean(in);
        }

        @Override
        public BuBean[] newArray(int size) {
            return new BuBean[size];
        }
    };

    public int getBuId() {
        return buId;
    }

    public void setBuId(int buId) {
        this.buId = buId;
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
        dest.writeInt(buId);
        dest.writeString(buName);
    }
}
