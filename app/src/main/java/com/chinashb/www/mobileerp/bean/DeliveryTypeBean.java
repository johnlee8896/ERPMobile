package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/17 12:06
 * @author 作者: xxblwf
 * @description 物流运输方式bean
 */

public class DeliveryTypeBean implements Parcelable {
    @SerializedName("DT_ID") private int dtId;
    @SerializedName("Delivery") private String delivery;

    public DeliveryTypeBean(){

    }

    protected DeliveryTypeBean(Parcel in) {
        dtId = in.readInt();
        delivery = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dtId);
        dest.writeString(delivery);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeliveryTypeBean> CREATOR = new Creator<DeliveryTypeBean>() {
        @Override
        public DeliveryTypeBean createFromParcel(Parcel in) {
            return new DeliveryTypeBean(in);
        }

        @Override
        public DeliveryTypeBean[] newArray(int size) {
            return new DeliveryTypeBean[size];
        }
    };

    public int getDtId() {
        return dtId;
    }

    public void setDtId(int dtId) {
        this.dtId = dtId;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }
}
