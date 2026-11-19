package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 成品发货 - 作业托盘 Bean
 * 对应 op_DP_ShipWork_ListPallets(Work_ID) 返回的 JSON
 */
public class ShipWorkPalletBean implements Parcelable {

    @SerializedName("Work_Pallet_ID") private long Work_Pallet_ID;
    @SerializedName("Work_ID") private long Work_ID;
    @SerializedName("Pallet_ID") private long Pallet_ID;
    @SerializedName("Box_ID") private long Box_ID;
    @SerializedName("Code_No_Box") private String Code_No_Box;
    @SerializedName("FIFO_Op_Date") private String FIFO_Op_Date;
    @SerializedName("Original_IST_ID") private long Original_IST_ID;
    @SerializedName("Original_Sub_Ist_ID") private long Original_Sub_Ist_ID;
    @SerializedName("Pallet_Status") private String Pallet_Status;
    @SerializedName("OQC_Result") private String OQC_Result;
    @SerializedName("OQC_Remark") private String OQC_Remark;
    @SerializedName("Pick_Scan_At") private String Pick_Scan_At;
    @SerializedName("Pick_Scan_By") private String Pick_Scan_By;
    @SerializedName("Pick_Scan_Name") private String Pick_Scan_Name;
    @SerializedName("Stage_Move_At") private String Stage_Move_At;
    @SerializedName("OQC_At") private String OQC_At;
    @SerializedName("Ship_Scan_At") private String Ship_Scan_At;
    @SerializedName("DPI_ID") private long DPI_ID;
    @SerializedName("Product_ID") private long Product_ID;
    @SerializedName("PS_ID") private long PS_ID;
    @SerializedName("Lot_ID") private long Lot_ID;
    @SerializedName("Quantity") private double Quantity;
    @SerializedName("Original_Location") private String Original_Location;

    public ShipWorkPalletBean() {
    }

    protected ShipWorkPalletBean(Parcel in) {
        Work_Pallet_ID = in.readLong();
        Work_ID = in.readLong();
        Pallet_ID = in.readLong();
        Box_ID = in.readLong();
        Code_No_Box = in.readString();
        FIFO_Op_Date = in.readString();
        Original_IST_ID = in.readLong();
        Original_Sub_Ist_ID = in.readLong();
        Pallet_Status = in.readString();
        OQC_Result = in.readString();
        OQC_Remark = in.readString();
        Pick_Scan_At = in.readString();
        Pick_Scan_By = in.readString();
        Pick_Scan_Name = in.readString();
        Stage_Move_At = in.readString();
        OQC_At = in.readString();
        Ship_Scan_At = in.readString();
        DPI_ID = in.readLong();
        Product_ID = in.readLong();
        PS_ID = in.readLong();
        Lot_ID = in.readLong();
        Quantity = in.readDouble();
        Original_Location = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(Work_Pallet_ID);
        dest.writeLong(Work_ID);
        dest.writeLong(Pallet_ID);
        dest.writeLong(Box_ID);
        dest.writeString(Code_No_Box);
        dest.writeString(FIFO_Op_Date);
        dest.writeLong(Original_IST_ID);
        dest.writeLong(Original_Sub_Ist_ID);
        dest.writeString(Pallet_Status);
        dest.writeString(OQC_Result);
        dest.writeString(OQC_Remark);
        dest.writeString(Pick_Scan_At);
        dest.writeString(Pick_Scan_By);
        dest.writeString(Pick_Scan_Name);
        dest.writeString(Stage_Move_At);
        dest.writeString(OQC_At);
        dest.writeString(Ship_Scan_At);
        dest.writeLong(DPI_ID);
        dest.writeLong(Product_ID);
        dest.writeLong(PS_ID);
        dest.writeLong(Lot_ID);
        dest.writeDouble(Quantity);
        dest.writeString(Original_Location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShipWorkPalletBean> CREATOR = new Creator<ShipWorkPalletBean>() {
        @Override
        public ShipWorkPalletBean createFromParcel(Parcel in) {
            return new ShipWorkPalletBean(in);
        }

        @Override
        public ShipWorkPalletBean[] newArray(int size) {
            return new ShipWorkPalletBean[size];
        }
    };

    public long getWork_Pallet_ID() {
        return Work_Pallet_ID;
    }

    public void setWork_Pallet_ID(long work_Pallet_ID) {
        Work_Pallet_ID = work_Pallet_ID;
    }

    public long getWork_ID() {
        return Work_ID;
    }

    public void setWork_ID(long work_ID) {
        Work_ID = work_ID;
    }

    public long getPallet_ID() {
        return Pallet_ID;
    }

    public void setPallet_ID(long pallet_ID) {
        Pallet_ID = pallet_ID;
    }

    public long getBox_ID() {
        return Box_ID;
    }

    public void setBox_ID(long box_ID) {
        Box_ID = box_ID;
    }

    public String getCode_No_Box() {
        return Code_No_Box;
    }

    public void setCode_No_Box(String code_No_Box) {
        Code_No_Box = code_No_Box;
    }

    public String getFIFO_Op_Date() {
        return FIFO_Op_Date;
    }

    public void setFIFO_Op_Date(String FIFO_Op_Date) {
        this.FIFO_Op_Date = FIFO_Op_Date;
    }

    public long getOriginal_IST_ID() {
        return Original_IST_ID;
    }

    public void setOriginal_IST_ID(long original_IST_ID) {
        Original_IST_ID = original_IST_ID;
    }

    public long getOriginal_Sub_Ist_ID() {
        return Original_Sub_Ist_ID;
    }

    public void setOriginal_Sub_Ist_ID(long original_Sub_Ist_ID) {
        Original_Sub_Ist_ID = original_Sub_Ist_ID;
    }

    public String getPallet_Status() {
        return Pallet_Status;
    }

    public void setPallet_Status(String pallet_Status) {
        Pallet_Status = pallet_Status;
    }

    public String getOQC_Result() {
        return OQC_Result;
    }

    public void setOQC_Result(String OQC_Result) {
        this.OQC_Result = OQC_Result;
    }

    public String getOQC_Remark() {
        return OQC_Remark;
    }

    public void setOQC_Remark(String OQC_Remark) {
        this.OQC_Remark = OQC_Remark;
    }

    public String getPick_Scan_At() {
        return Pick_Scan_At;
    }

    public void setPick_Scan_At(String pick_Scan_At) {
        Pick_Scan_At = pick_Scan_At;
    }

    public String getPick_Scan_By() {
        return Pick_Scan_By;
    }

    public void setPick_Scan_By(String pick_Scan_By) {
        Pick_Scan_By = pick_Scan_By;
    }

    public String getPick_Scan_Name() {
        return Pick_Scan_Name;
    }

    public void setPick_Scan_Name(String pick_Scan_Name) {
        Pick_Scan_Name = pick_Scan_Name;
    }

    public String getStage_Move_At() {
        return Stage_Move_At;
    }

    public void setStage_Move_At(String stage_Move_At) {
        Stage_Move_At = stage_Move_At;
    }

    public String getOQC_At() {
        return OQC_At;
    }

    public void setOQC_At(String OQC_At) {
        this.OQC_At = OQC_At;
    }

    public String getShip_Scan_At() {
        return Ship_Scan_At;
    }

    public void setShip_Scan_At(String ship_Scan_At) {
        Ship_Scan_At = ship_Scan_At;
    }

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

    public long getLot_ID() {
        return Lot_ID;
    }

    public void setLot_ID(long lot_ID) {
        Lot_ID = lot_ID;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public String getOriginal_Location() {
        return Original_Location;
    }

    public void setOriginal_Location(String original_Location) {
        Original_Location = original_Location;
    }
}
