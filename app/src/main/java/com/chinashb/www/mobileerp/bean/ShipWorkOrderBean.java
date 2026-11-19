package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 成品发货 - 可发货指令 Bean
 * 对应 op_DP_ShipWork_ListOrders 返回的 JSON
 */
public class ShipWorkOrderBean implements Parcelable {

    @SerializedName("DO_ID") private long DO_ID;
    @SerializedName("TrackNo") private String TrackNo;
    @SerializedName("Delivery_Date") private String Delivery_Date;
    @SerializedName("Bu_ID") private int Bu_ID;
    @SerializedName("Company_ID") private int Company_ID;
    @SerializedName("CF_ID") private long CF_ID;
    @SerializedName("Customer_Name") private String Customer_Name;
    @SerializedName("Delivery_ID") private Long Delivery_ID;
    @SerializedName("Work_ID") private Long Work_ID;
    @SerializedName("Work_Status") private String Work_Status;

    public ShipWorkOrderBean() {
    }

    protected ShipWorkOrderBean(Parcel in) {
        DO_ID = in.readLong();
        TrackNo = in.readString();
        Delivery_Date = in.readString();
        Bu_ID = in.readInt();
        Company_ID = in.readInt();
        CF_ID = in.readLong();
        Customer_Name = in.readString();
        if (in.readByte() == 0) {
            Delivery_ID = null;
        } else {
            Delivery_ID = in.readLong();
        }
        if (in.readByte() == 0) {
            Work_ID = null;
        } else {
            Work_ID = in.readLong();
        }
        Work_Status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(DO_ID);
        dest.writeString(TrackNo);
        dest.writeString(Delivery_Date);
        dest.writeInt(Bu_ID);
        dest.writeInt(Company_ID);
        dest.writeLong(CF_ID);
        dest.writeString(Customer_Name);
        if (Delivery_ID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(Delivery_ID);
        }
        if (Work_ID == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(Work_ID);
        }
        dest.writeString(Work_Status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShipWorkOrderBean> CREATOR = new Creator<ShipWorkOrderBean>() {
        @Override
        public ShipWorkOrderBean createFromParcel(Parcel in) {
            return new ShipWorkOrderBean(in);
        }

        @Override
        public ShipWorkOrderBean[] newArray(int size) {
            return new ShipWorkOrderBean[size];
        }
    };

    public long getDO_ID() {
        return DO_ID;
    }

    public void setDO_ID(long DO_ID) {
        this.DO_ID = DO_ID;
    }

    public String getTrackNo() {
        return TrackNo;
    }

    public void setTrackNo(String trackNo) {
        TrackNo = trackNo;
    }

    public String getDelivery_Date() {
        return Delivery_Date;
    }

    public void setDelivery_Date(String delivery_Date) {
        Delivery_Date = delivery_Date;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int bu_ID) {
        Bu_ID = bu_ID;
    }

    public int getCompany_ID() {
        return Company_ID;
    }

    public void setCompany_ID(int company_ID) {
        Company_ID = company_ID;
    }

    public long getCF_ID() {
        return CF_ID;
    }

    public void setCF_ID(long CF_ID) {
        this.CF_ID = CF_ID;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public Long getDelivery_ID() {
        return Delivery_ID;
    }

    public void setDelivery_ID(Long delivery_ID) {
        Delivery_ID = delivery_ID;
    }

    public Long getWork_ID() {
        return Work_ID;
    }

    public void setWork_ID(Long work_ID) {
        Work_ID = work_ID;
    }

    public String getWork_Status() {
        return Work_Status;
    }

    public void setWork_Status(String work_Status) {
        Work_Status = work_Status;
    }
}
