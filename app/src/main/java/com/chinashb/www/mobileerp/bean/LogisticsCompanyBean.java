package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/17 10:19
 * @author 作者: xxblwf
 * @description 物流公司Bean
 */

public class LogisticsCompanyBean implements Parcelable {
    @SerializedName("LC_ID") private int lcId;
    @SerializedName("LC_Name") private String lcName;
    @SerializedName("Contact") private String contact;
    @SerializedName("Tel") private String tel;

    protected LogisticsCompanyBean(Parcel in) {
        lcId = in.readInt();
        lcName = in.readString();
        contact = in.readString();
        tel = in.readString();
    }

    public static final Creator<LogisticsCompanyBean> CREATOR = new Creator<LogisticsCompanyBean>() {
        @Override
        public LogisticsCompanyBean createFromParcel(Parcel in) {
            return new LogisticsCompanyBean(in);
        }

        @Override
        public LogisticsCompanyBean[] newArray(int size) {
            return new LogisticsCompanyBean[size];
        }
    };

    public int getLcId() {
        return lcId;
    }

    public void setLcId(int lcId) {
        this.lcId = lcId;
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lcId);
        dest.writeString(lcName);
        dest.writeString(contact);
        dest.writeString(tel);
    }
}
