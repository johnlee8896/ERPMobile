package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2023/12/27 3:24 PM
 * @author 作者: liweifeng
 * @description
 */
public class BoxLoadedQtyBean implements Parcelable {

    @SerializedName("box_loaded_qty") private String boxLoadedQty;

    protected BoxLoadedQtyBean(Parcel in) {
        boxLoadedQty = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(boxLoadedQty);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BoxLoadedQtyBean> CREATOR = new Creator<BoxLoadedQtyBean>() {
        @Override
        public BoxLoadedQtyBean createFromParcel(Parcel in) {
            return new BoxLoadedQtyBean(in);
        }

        @Override
        public BoxLoadedQtyBean[] newArray(int size) {
            return new BoxLoadedQtyBean[size];
        }
    };

    public String getBoxLoadedQty() {
        return boxLoadedQty;
    }

    public BoxLoadedQtyBean setBoxLoadedQty(String boxLoadedQty) {
        this.boxLoadedQty = boxLoadedQty;
        return this;
    }
}
