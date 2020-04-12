package com.chinashb.www.mobileerp.bean.entity;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/12/31 9:17
 * @author 作者: xxblwf
 * @description
 */

public class MESInnerDataEntity {

    /**
     * PS_ID : 1829
     * Product_ID : 1936
     * Product_Chinese_Name : 抬高电机(改短版)
     * Product_DrawNo :
     * Product_Version : 14
     * Item_ID : 10901
     * IV_ID : 11219
     * Qty : 2
     * Item_Unit : 个
     */

    //// TODO: 2020/4/7 加上boxID,Date
    @SerializedName("BoxID") private int BoxId;
    @SerializedName("Date") private String dateString;
    @SerializedName("PS_ID") private int PSID;
    @SerializedName("Product_ID") private int ProductID;
    @SerializedName("Product_Chinese_Name") private String ProductChineseName;
    @SerializedName("Product_DrawNo") private String ProductDrawNo;
    @SerializedName("Product_Version") private String ProductVersion;
    @SerializedName("Item_ID") private int ItemID;
    @SerializedName("IV_ID") private int IVID;
    @SerializedName("Qty") private int Qty;
    @SerializedName("Item_Unit") private String ItemUnit;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public int getBoxId() {
        return BoxId;
    }

    public void setBoxId(int boxId) {
        BoxId = boxId;
    }

    public int getPSID() {
        return PSID;
    }

    public void setPSID(int PSID) {
        this.PSID = PSID;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int ProductID) {
        this.ProductID = ProductID;
    }

    public String getProductChineseName() {
        return ProductChineseName;
    }

    public void setProductChineseName(String ProductChineseName) {
        this.ProductChineseName = ProductChineseName;
    }

    public String getProductDrawNo() {
        return ProductDrawNo;
    }

    public void setProductDrawNo(String ProductDrawNo) {
        this.ProductDrawNo = ProductDrawNo;
    }

    public String getProductVersion() {
        return ProductVersion;
    }

    public void setProductVersion(String ProductVersion) {
        this.ProductVersion = ProductVersion;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int ItemID) {
        this.ItemID = ItemID;
    }

    public int getIVID() {
        return IVID;
    }

    public void setIVID(int IVID) {
        this.IVID = IVID;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int Qty) {
        this.Qty = Qty;
    }

    public String getItemUnit() {
        return ItemUnit;
    }

    public void setItemUnit(String ItemUnit) {
        this.ItemUnit = ItemUnit;
    }
}
