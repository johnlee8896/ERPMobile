package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 成品发货 - 发货区 Bean
 * 对应 op_DP_ShipWork_ListStageAreas(Warehouse_ID, Bu_ID) 返回的 JSON
 */
public class ShipWorkStageAreaBean implements Parcelable {

    @SerializedName("Stage_Area_ID") private long Stage_Area_ID;
    @SerializedName("Warehouse_ID") private long Warehouse_ID;
    @SerializedName("IST_ID") private long IST_ID;
    @SerializedName("Sub_Ist_ID") private long Sub_Ist_ID;
    @SerializedName("Stage_Name") private String Stage_Name;

    public ShipWorkStageAreaBean() {
    }

    protected ShipWorkStageAreaBean(Parcel in) {
        Stage_Area_ID = in.readLong();
        Warehouse_ID = in.readLong();
        IST_ID = in.readLong();
        Sub_Ist_ID = in.readLong();
        Stage_Name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Stage_Area_ID);
        dest.writeLong(Warehouse_ID);
        dest.writeLong(IST_ID);
        dest.writeLong(Sub_Ist_ID);
        dest.writeString(Stage_Name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShipWorkStageAreaBean> CREATOR = new Creator<ShipWorkStageAreaBean>() {
        @Override
        public ShipWorkStageAreaBean createFromParcel(Parcel in) {
            return new ShipWorkStageAreaBean(in);
        }

        @Override
        public ShipWorkStageAreaBean[] newArray(int size) {
            return new ShipWorkStageAreaBean[size];
        }
    };

    public long getStage_Area_ID() {
        return Stage_Area_ID;
    }

    public void setStage_Area_ID(long stage_Area_ID) {
        Stage_Area_ID = stage_Area_ID;
    }

    public long getWarehouse_ID() {
        return Warehouse_ID;
    }

    public void setWarehouse_ID(long warehouse_ID) {
        Warehouse_ID = warehouse_ID;
    }

    public long getIST_ID() {
        return IST_ID;
    }

    public void setIST_ID(long IST_ID) {
        this.IST_ID = IST_ID;
    }

    public long getSub_Ist_ID() {
        return Sub_Ist_ID;
    }

    public void setSub_Ist_ID(long sub_Ist_ID) {
        Sub_Ist_ID = sub_Ist_ID;
    }

    public String getStage_Name() {
        return Stage_Name;
    }

    public void setStage_Name(String stage_Name) {
        Stage_Name = stage_Name;
    }
}
