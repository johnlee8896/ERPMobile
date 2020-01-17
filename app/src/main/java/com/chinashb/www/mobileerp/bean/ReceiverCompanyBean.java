package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/16 15:18
 * @author 作者: xxblwf
 * @description 收货客户公司Bean
 */

public class ReceiverCompanyBean implements Parcelable {
    @SerializedName("CF_ID") private int cfId;
    @SerializedName("Customer") private String customer;
    @SerializedName("Factory") private String factory;
    @SerializedName("CF_Address") private String fgAddress;
    @SerializedName("Country") private String country;

    protected ReceiverCompanyBean(Parcel in) {
        cfId = in.readInt();
        customer = in.readString();
        factory = in.readString();
        fgAddress = in.readString();
        country = in.readString();
    }

    public static final Creator<ReceiverCompanyBean> CREATOR = new Creator<ReceiverCompanyBean>() {
        @Override
        public ReceiverCompanyBean createFromParcel(Parcel in) {
            return new ReceiverCompanyBean(in);
        }

        @Override
        public ReceiverCompanyBean[] newArray(int size) {
            return new ReceiverCompanyBean[size];
        }
    };

    public int getCfId() {
        return cfId;
    }

    public void setCfId(int cfId) {
        this.cfId = cfId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getFgAddress() {
        return fgAddress;
    }

    public void setFgAddress(String fgAddress) {
        this.fgAddress = fgAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cfId);
        dest.writeString(customer);
        dest.writeString(factory);
        dest.writeString(fgAddress);
        dest.writeString(country);
    }
}
