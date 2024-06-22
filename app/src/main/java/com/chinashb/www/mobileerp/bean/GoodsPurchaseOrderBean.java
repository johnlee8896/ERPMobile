package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2024/4/15 3:45 PM
 * @author 作者: liweifeng
 * @description
 */
public class GoodsPurchaseOrderBean implements Parcelable {

    @SerializedName("POI_ID") private int poiID;
    @SerializedName("PO_No") private String PONO;
    @SerializedName("POI_Due_Date") private String poiDueDate;
    @SerializedName("POI_Quantity") private float poiQuantity;
    @SerializedName("UnReachQuantity") private float unReachQuantity;
    @SerializedName("LeftQuantity") private float leftQuantity;

    protected GoodsPurchaseOrderBean(Parcel in) {
        poiID = in.readInt();
        PONO = in.readString();
        poiDueDate = in.readString();
        poiQuantity = in.readFloat();
        unReachQuantity = in.readFloat();
        leftQuantity = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(poiID);
        dest.writeString(PONO);
        dest.writeString(poiDueDate);
        dest.writeFloat(poiQuantity);
        dest.writeFloat(unReachQuantity);
        dest.writeFloat(leftQuantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GoodsPurchaseOrderBean> CREATOR = new Creator<GoodsPurchaseOrderBean>() {
        @Override
        public GoodsPurchaseOrderBean createFromParcel(Parcel in) {
            return new GoodsPurchaseOrderBean(in);
        }

        @Override
        public GoodsPurchaseOrderBean[] newArray(int size) {
            return new GoodsPurchaseOrderBean[size];
        }
    };

    public int getPoiID() {
        return poiID;
    }

    public GoodsPurchaseOrderBean setPoiID(int poiID) {
        this.poiID = poiID;
        return this;
    }

    public String getPONO() {
        return PONO;
    }

    public GoodsPurchaseOrderBean setPONO(String PONO) {
        this.PONO = PONO;
        return this;
    }

    public String getPoiDueDate() {
        return poiDueDate;
    }

    public GoodsPurchaseOrderBean setPoiDueDate(String poiDueDate) {
        this.poiDueDate = poiDueDate;
        return this;
    }

    public float getPoiQuantity() {
        return poiQuantity;
    }

    public GoodsPurchaseOrderBean setPoiQuantity(float poiQuantity) {
        this.poiQuantity = poiQuantity;
        return this;
    }

    public float getUnReachQuantity() {
        return unReachQuantity;
    }

    public GoodsPurchaseOrderBean setUnReachQuantity(float unReachQuantity) {
        this.unReachQuantity = unReachQuantity;
        return this;
    }

    public float getLeftQuantity() {
        return leftQuantity;
    }

    public GoodsPurchaseOrderBean setLeftQuantity(float leftQuantity) {
        this.leftQuantity = leftQuantity;
        return this;
    }
}
