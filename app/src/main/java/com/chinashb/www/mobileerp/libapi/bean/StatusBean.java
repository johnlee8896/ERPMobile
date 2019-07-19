package com.chinashb.www.mobileerp.libapi.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2018/3/22 15:54
 * @author 作者: yulong
 * @description 通用的状态bean
 */
public class StatusBean implements Parcelable {
    public static final Creator<StatusBean> CREATOR = new Creator<StatusBean>() {
        @Override
        public StatusBean createFromParcel(Parcel source) {
            return new StatusBean(source);
        }

        @Override
        public StatusBean[] newArray(int size) {
            return new StatusBean[size];
        }
    };
    @SerializedName("code") private int code;
    @SerializedName("message") private String message;
    public StatusBean() {
    }

    protected StatusBean(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
    }

    @Override
    public String toString() {
        return "StatusBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
    }

}
