package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 成品发货 - 发货指令明细 Bean
 * 对应 op_DP_ShipWork_ListLines(DO_ID) 返回的 JSON
 */
public class ShipWorkLineBean implements Parcelable {

    @SerializedName("DPI_ID") private long DPI_ID;
    @SerializedName("Product_ID") private long Product_ID;
    @SerializedName("PS_ID") private long PS_ID;
    @SerializedName("DPI_Quantity") private double DPI_Quantity;
    @SerializedName("Shipped_Quantity") private double Shipped_Quantity;
    @SerializedName("Reserved_Quantity") private double Reserved_Quantity;
    @SerializedName("Available_Quantity") private double Available_Quantity;

    public ShipWorkLineBean() {
    }

    protected ShipWorkLineBean(Parcel in) {
        DPI_ID = in.readLong();
        Product_ID = in.readLong();
        PS_ID = in.readLong();
        DPI_Quantity = in.readDouble();
        Shipped_Quantity = in.readDouble();
        Reserved_Quantity = in.readDouble();
        Available_Quantity = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(DPI_ID);
        dest.writeLong(Product_ID);
        dest.writeLong(PS_ID);
        dest.writeDouble(DPI_Quantity);
        dest.writeDouble(Shipped_Quantity);
        dest.writeDouble(Reserved_Quantity);
        dest.writeDouble(Available_Quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShipWorkLineBean> CREATOR = new Creator<ShipWorkLineBean>() {
        @Override
        public ShipWorkLineBean createFromParcel(Parcel in) {
            return new ShipWorkLineBean(in);
        }

        @Override
        public ShipWorkLineBean[] newArray(int size) {
            return new ShipWorkLineBean[size];
        }
    };

    public long getDPI_ID() {
        return DPI_ID;
    }

    public void setDPI_ID(long DPI_ID) {
        this.DPI_ID = DPI_ID;
    }

    public long getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(long product_ID) {
        Product_ID = product_ID;
    }

    public long getPS_ID() {
        return PS_ID;
    }

    public void setPS_ID(long PS_ID) {
        this.PS_ID = PS_ID;
    }

    public double getDPI_Quantity() {
        return DPI_Quantity;
    }

    public void setDPI_Quantity(double DPI_Quantity) {
        this.DPI_Quantity = DPI_Quantity;
    }

    public double getShipped_Quantity() {
        return Shipped_Quantity;
    }

    public void setShipped_Quantity(double shipped_Quantity) {
        Shipped_Quantity = shipped_Quantity;
    }

    public double getReserved_Quantity() {
        return Reserved_Quantity;
    }

    public void setReserved_Quantity(double reserved_Quantity) {
        Reserved_Quantity = reserved_Quantity;
    }

    public double getAvailable_Quantity() {
        return Available_Quantity;
    }

    public void setAvailable_Quantity(double available_Quantity) {
        Available_Quantity = available_Quantity;
    }
}
