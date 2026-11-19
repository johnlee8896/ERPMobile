package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 成品发货 - 执行中发货作业 Bean
 * 对应 op_DP_ShipWork_ListActive 返回的 JSON
 */
public class ShipWorkActiveBean implements Parcelable {

    @SerializedName("Work_ID") private long Work_ID;
    @SerializedName("DO_ID") private long DO_ID;
    @SerializedName("Delivery_ID") private long Delivery_ID;
    @SerializedName("Warehouse_ID") private long Warehouse_ID;
    @SerializedName("Bu_ID") private int Bu_ID;
    @SerializedName("Work_Status") private String Work_Status;
    @SerializedName("Started_At") private String Started_At;
    @SerializedName("Daily_Sequence") private int Daily_Sequence;
    @SerializedName("Last_Action_At") private String Last_Action_At;
    @SerializedName("Posted_At") private String Posted_At;
    @SerializedName("Stage_Name") private String Stage_Name;
    @SerializedName("TrackNo") private String TrackNo;
    @SerializedName("Customer_Name") private String Customer_Name;
    @SerializedName("Elapsed_Minutes") private long Elapsed_Minutes;
    @SerializedName("Locked_Count") private int Locked_Count;
    @SerializedName("Staged_Count") private int Staged_Count;
    @SerializedName("Scanned_Count") private int Scanned_Count;
    @SerializedName("Posted_Count") private int Posted_Count;

    public ShipWorkActiveBean() {
    }

    protected ShipWorkActiveBean(Parcel in) {
        Work_ID = in.readLong();
        DO_ID = in.readLong();
        Delivery_ID = in.readLong();
        Warehouse_ID = in.readLong();
        Bu_ID = in.readInt();
        Work_Status = in.readString();
        Started_At = in.readString();
        Daily_Sequence = in.readInt();
        Last_Action_At = in.readString();
        Posted_At = in.readString();
        Stage_Name = in.readString();
        TrackNo = in.readString();
        Customer_Name = in.readString();
        Elapsed_Minutes = in.readLong();
        Locked_Count = in.readInt();
        Staged_Count = in.readInt();
        Scanned_Count = in.readInt();
        Posted_Count = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Work_ID);
        dest.writeLong(DO_ID);
        dest.writeLong(Delivery_ID);
        dest.writeLong(Warehouse_ID);
        dest.writeInt(Bu_ID);
        dest.writeString(Work_Status);
        dest.writeString(Started_At);
        dest.writeInt(Daily_Sequence);
        dest.writeString(Last_Action_At);
        dest.writeString(Posted_At);
        dest.writeString(Stage_Name);
        dest.writeString(TrackNo);
        dest.writeString(Customer_Name);
        dest.writeLong(Elapsed_Minutes);
        dest.writeInt(Locked_Count);
        dest.writeInt(Staged_Count);
        dest.writeInt(Scanned_Count);
        dest.writeInt(Posted_Count);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShipWorkActiveBean> CREATOR = new Creator<ShipWorkActiveBean>() {
        @Override
        public ShipWorkActiveBean createFromParcel(Parcel in) {
            return new ShipWorkActiveBean(in);
        }

        @Override
        public ShipWorkActiveBean[] newArray(int size) {
            return new ShipWorkActiveBean[size];
        }
    };

    public long getWork_ID() {
        return Work_ID;
    }

    public void setWork_ID(long work_ID) {
        Work_ID = work_ID;
    }

    public long getDO_ID() {
        return DO_ID;
    }

    public void setDO_ID(long DO_ID) {
        this.DO_ID = DO_ID;
    }

    public long getDelivery_ID() {
        return Delivery_ID;
    }

    public void setDelivery_ID(long delivery_ID) {
        Delivery_ID = delivery_ID;
    }

    public long getWarehouse_ID() {
        return Warehouse_ID;
    }

    public void setWarehouse_ID(long warehouse_ID) {
        Warehouse_ID = warehouse_ID;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int bu_ID) {
        Bu_ID = bu_ID;
    }

    public String getWork_Status() {
        return Work_Status;
    }

    public void setWork_Status(String work_Status) {
        Work_Status = work_Status;
    }

    public int getDaily_Sequence() {
        return Daily_Sequence;
    }

    public void setDaily_Sequence(int daily_Sequence) {
        Daily_Sequence = daily_Sequence;
    }

    public String getStarted_At() {
        return Started_At;
    }

    public void setStarted_At(String started_At) {
        Started_At = started_At;
    }

    public String getLast_Action_At() {
        return Last_Action_At;
    }

    public void setLast_Action_At(String last_Action_At) {
        Last_Action_At = last_Action_At;
    }

    public String getPosted_At() {
        return Posted_At;
    }

    public void setPosted_At(String posted_At) {
        Posted_At = posted_At;
    }

    public String getStage_Name() {
        return Stage_Name;
    }

    public void setStage_Name(String stage_Name) {
        Stage_Name = stage_Name;
    }

    public String getTrackNo() {
        return TrackNo;
    }

    public void setTrackNo(String trackNo) {
        TrackNo = trackNo;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public long getElapsed_Minutes() {
        return Elapsed_Minutes;
    }

    public void setElapsed_Minutes(long elapsed_Minutes) {
        Elapsed_Minutes = elapsed_Minutes;
    }

    public int getLocked_Count() {
        return Locked_Count;
    }

    public void setLocked_Count(int locked_Count) {
        Locked_Count = locked_Count;
    }

    public int getStaged_Count() {
        return Staged_Count;
    }

    public void setStaged_Count(int staged_Count) {
        Staged_Count = staged_Count;
    }

    public int getScanned_Count() {
        return Scanned_Count;
    }

    public void setScanned_Count(int scanned_Count) {
        Scanned_Count = scanned_Count;
    }

    public int getPosted_Count() {
        return Posted_Count;
    }

    public void setPosted_Count(int posted_Count) {
        Posted_Count = posted_Count;
    }
}
