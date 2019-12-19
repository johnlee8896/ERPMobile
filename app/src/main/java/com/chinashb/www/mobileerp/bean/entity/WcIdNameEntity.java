package com.chinashb.www.mobileerp.bean.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/12/18 16:38
 * @author 作者: xxblwf
 * @description 产线(工作中心)
 */

public class WcIdNameEntity implements Parcelable {
    @SerializedName("WC_Id") private int wcId;
    @SerializedName("WC_Name") private String wcName;

    protected WcIdNameEntity(Parcel in) {
        wcId = in.readInt();
        wcName = in.readString();
    }

    public static final Creator<WcIdNameEntity> CREATOR = new Creator<WcIdNameEntity>() {
        @Override
        public WcIdNameEntity createFromParcel(Parcel in) {
            return new WcIdNameEntity(in);
        }

        @Override
        public WcIdNameEntity[] newArray(int size) {
            return new WcIdNameEntity[size];
        }
    };

    public int getWcId() {
        return wcId;
    }

    public void setWcId(int wcId) {
        this.wcId = wcId;
    }

    public String getWcName() {
        return wcName;
    }

    public void setWcName(String wcName) {
        this.wcName = wcName;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(wcId);
        dest.writeString(wcName);
    }
}
