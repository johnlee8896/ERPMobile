package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2023/4/12 3:30 PM
 * @author 作者: liweifeng
 * @description 拆解或合并的箱子信息
 */
public class PackDepackBoxBean {

    /**
     * SMT_ID : 967409
     * SML_ID : 1079757
     * No : 2
     * IQty : 4000.0
     * Code : VB/MT/967409/S/3504/IV/132658/P/EPS2GMA-5002 B8/D/20210111/L/20210111-1/N/2/Q/4000
     * Supplier_ID : 3504
     * Item_ID : 1395
     * IV_ID : 132658
     * ManuLot : 20210111-1
     * ManuDate : /Date(1610294400000+0800)/
     * Used : false
     * UsedDate : null
     * IsFull : true
     * LotID : null
     * DII_ID : null
     * DII_Date : null
     * Freezed : false
     * ChildCount : 8
     * ChildLoaded : 8
     * Box_Name : 托盘
     * PackQty : 4000.0
     * PWC_ID : 0
     * MPIWC_ID : 0
     * Code2 : VG/967409
     * Remark : 含8个盒子, No:9-16
     * IsReworked : null
     * Code_Name : C
     * ProInLotID : null
     * ProInDII_ID : null
     * Product_ID : null
     * PS_ID : null
     * Kis_FBDC_Exe : null
     * Kis_FBDR_Exe : null
     * ManuDate_Y : 2021
     * ManuDate_M : 1
     * ManuDate_D : 11
     * Q_Number : null
     */

    @SerializedName("SMT_ID") private int SMTID;
    @SerializedName("SML_ID") private int SMLID;
    @SerializedName("No") private int No;
    @SerializedName("IQty") private double IQty;
    @SerializedName("Code") private String Code;
    @SerializedName("Supplier_ID") private int SupplierID;
    @SerializedName("Item_ID") private int ItemID;
    @SerializedName("IV_ID") private int IVID;
    @SerializedName("ManuLot") private String ManuLot;
    @SerializedName("ManuDate") private String ManuDate;
    @SerializedName("Used") private boolean Used;
    @SerializedName("IsFull") private boolean IsFull;
    @SerializedName("Freezed") private boolean Freezed;
    @SerializedName("ChildCount") private int ChildCount;
    @SerializedName("ChildLoaded") private int ChildLoaded;
    @SerializedName("Box_Name") private String BoxName;
    @SerializedName("PackQty") private double PackQty;
    @SerializedName("Remark") private String Remark;
    @SerializedName("Code_Name") private String CodeName;

    public int getSMTID() {
        return SMTID;
    }

    public PackDepackBoxBean setSMTID(int SMTID) {
        this.SMTID = SMTID;
        return this;
    }

    public int getSMLID() {
        return SMLID;
    }

    public PackDepackBoxBean setSMLID(int SMLID) {
        this.SMLID = SMLID;
        return this;
    }

    public int getNo() {
        return No;
    }

    public PackDepackBoxBean setNo(int no) {
        No = no;
        return this;
    }

    public double getIQty() {
        return IQty;
    }

    public PackDepackBoxBean setIQty(double IQty) {
        this.IQty = IQty;
        return this;
    }

    public String getCode() {
        return Code;
    }

    public PackDepackBoxBean setCode(String code) {
        Code = code;
        return this;
    }

    public int getSupplierID() {
        return SupplierID;
    }

    public PackDepackBoxBean setSupplierID(int supplierID) {
        SupplierID = supplierID;
        return this;
    }

    public int getItemID() {
        return ItemID;
    }

    public PackDepackBoxBean setItemID(int itemID) {
        ItemID = itemID;
        return this;
    }

    public int getIVID() {
        return IVID;
    }

    public PackDepackBoxBean setIVID(int IVID) {
        this.IVID = IVID;
        return this;
    }

    public String getManuLot() {
        return ManuLot;
    }

    public PackDepackBoxBean setManuLot(String manuLot) {
        ManuLot = manuLot;
        return this;
    }

    public String getManuDate() {
        return ManuDate;
    }

    public PackDepackBoxBean setManuDate(String manuDate) {
        ManuDate = manuDate;
        return this;
    }

    public boolean isUsed() {
        return Used;
    }

    public PackDepackBoxBean setUsed(boolean used) {
        Used = used;
        return this;
    }

    public boolean isFull() {
        return IsFull;
    }

    public PackDepackBoxBean setFull(boolean full) {
        IsFull = full;
        return this;
    }

    public boolean isFreezed() {
        return Freezed;
    }

    public PackDepackBoxBean setFreezed(boolean freezed) {
        Freezed = freezed;
        return this;
    }

    public int getChildCount() {
        return ChildCount;
    }

    public PackDepackBoxBean setChildCount(int childCount) {
        ChildCount = childCount;
        return this;
    }

    public int getChildLoaded() {
        return ChildLoaded;
    }

    public PackDepackBoxBean setChildLoaded(int childLoaded) {
        ChildLoaded = childLoaded;
        return this;
    }

    public String getBoxName() {
        return BoxName;
    }

    public PackDepackBoxBean setBoxName(String boxName) {
        BoxName = boxName;
        return this;
    }

    public double getPackQty() {
        return PackQty;
    }

    public PackDepackBoxBean setPackQty(double packQty) {
        PackQty = packQty;
        return this;
    }

    public String getRemark() {
        return Remark;
    }

    public PackDepackBoxBean setRemark(String remark) {
        Remark = remark;
        return this;
    }

    public String getCodeName() {
        return CodeName;
    }

    public PackDepackBoxBean setCodeName(String codeName) {
        CodeName = codeName;
        return this;
    }
}
