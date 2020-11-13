package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/11/11 15:05
 * @author 作者: xxblwf
 * @description
 */

public class MesDataInfoForSaleOutBean {

    /**
     * Id : 124
     * JobOrderId : 900590
     * CartonNo : HMDB6202011100001
     * Qty : 216
     * Date : 2020-11-10T08:48:56
     * Product_ID : 20534
     * PS_ID : 24815
     * Item_ID : 66594
     * IV_ID : 141609
     * IsDelete : 0
     * IsWarehousing : 1
     */

    @SerializedName("Id") private int Id;
    @SerializedName("JobOrderId") private int JobOrderId;
    @SerializedName("CartonNo") private String CartonNo;
    @SerializedName("Qty") private int Qty;
    @SerializedName("Date") private String Date;
    @SerializedName("Product_ID") private int ProductID;
    @SerializedName("PS_ID") private int PSID;
    @SerializedName("Item_ID") private int ItemID;
    @SerializedName("IV_ID") private int IVID;
    @SerializedName("IsDelete") private int IsDelete;
    @SerializedName("IsWarehousing") private int IsWarehousing;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getJobOrderId() {
        return JobOrderId;
    }

    public void setJobOrderId(int JobOrderId) {
        this.JobOrderId = JobOrderId;
    }

    public String getCartonNo() {
        return CartonNo;
    }

    public void setCartonNo(String CartonNo) {
        this.CartonNo = CartonNo;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int Qty) {
        this.Qty = Qty;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int ProductID) {
        this.ProductID = ProductID;
    }

    public int getPSID() {
        return PSID;
    }

    public void setPSID(int PSID) {
        this.PSID = PSID;
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

    public int getIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(int IsDelete) {
        this.IsDelete = IsDelete;
    }

    public int getIsWarehousing() {
        return IsWarehousing;
    }

    public void setIsWarehousing(int IsWarehousing) {
        this.IsWarehousing = IsWarehousing;
    }
}
