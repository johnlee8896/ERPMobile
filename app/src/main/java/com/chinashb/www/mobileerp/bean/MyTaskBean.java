package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2021/1/12 16:31
 * @author 作者: xxblwf
 * @description 自己的任务，创建 bean
 */

public class MyTaskBean implements Parcelable {

    /**
     * TID : 27948
     * LastAccess : 2020-09-09T08:13:53.507
     */

    @SerializedName("TID") private int TID;
    @SerializedName("LastAccess") private String LastAccess;

    protected MyTaskBean(Parcel in) {
        TID = in.readInt();
        LastAccess = in.readString();
    }

    public static final Creator<MyTaskBean> CREATOR = new Creator<MyTaskBean>() {
        @Override
        public MyTaskBean createFromParcel(Parcel in) {
            return new MyTaskBean(in);
        }

        @Override
        public MyTaskBean[] newArray(int size) {
            return new MyTaskBean[size];
        }
    };

    public int getTID() {
        return TID;
    }

    public void setTID(int TID) {
        this.TID = TID;
    }

    public String getLastAccess() {
        return LastAccess;
    }

    public void setLastAccess(String LastAccess) {
        this.LastAccess = LastAccess;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(TID);
        dest.writeString(LastAccess);
    }
}
