package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/2/26 9:42 AM
 * @author 作者: liweifeng
 * @description 发货指令中的发货单
 */
public class SDZHDeliveryOrderNumberBean {

    /**
     * DOP_Id : 31
     * Do_Id : 6777
     * OrderNo : CLD0A252A12I1A
     * PS_Id : 21911
     * Product_Id : 18240
     * IsDelete : 0
     */

    @SerializedName("DOP_Id") private int DOPId;
    @SerializedName("Do_Id") private int DoId;
    @SerializedName("OrderNo") private String OrderNo;
    @SerializedName("PS_Id") private int PSId;
    @SerializedName("Product_Id") private int ProductId;
//    @SerializedName("IsDelete") private int IsDelete;
    @SerializedName("IsDelete") private boolean IsDelete;

    public int getDOPId() {
        return DOPId;
    }

    public void setDOPId(int DOPId) {
        this.DOPId = DOPId;
    }

    public int getDoId() {
        return DoId;
    }

    public void setDoId(int DoId) {
        this.DoId = DoId;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public int getPSId() {
        return PSId;
    }

    public void setPSId(int PSId) {
        this.PSId = PSId;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public boolean getIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(boolean IsDelete) {
        this.IsDelete = IsDelete;
    }
}
