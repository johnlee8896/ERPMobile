package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/2/27 4:17 PM
 * @author 作者: liweifeng
 * @description 三点照合 包装箱详情
 */
public class SDZHBoxDetailBean {

    /**
     * DOB_Id : 8
     * Do_Id : 6023
     * OrderNo : RD10A252A11E1A1
     * Product_Id : 19337
     * ProductNo : 28710-4DW0BD9
     * BoxQty : 6
     * BoxCode : P287104DW0BD9M10006RECHZ057001001
     * IsDelete : 0
     * IsOK : 1
     */

    @SerializedName("DOB_Id") private int DOBId;
    @SerializedName("Do_Id") private int DoId;
    @SerializedName("OrderNo") private String OrderNo;
    @SerializedName("Product_Id") private int ProductId;
    @SerializedName("ProductNo") private String ProductNo;
    @SerializedName("BoxQty") private int BoxQty;
    @SerializedName("BoxCode") private String BoxCode;
    @SerializedName("IsDelete") private int IsDelete;
    @SerializedName("IsOK") private int IsOK;

    public int getDOBId() {
        return DOBId;
    }

    public void setDOBId(int DOBId) {
        this.DOBId = DOBId;
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

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int ProductId) {
        this.ProductId = ProductId;
    }

    public String getProductNo() {
        return ProductNo;
    }

    public void setProductNo(String ProductNo) {
        this.ProductNo = ProductNo;
    }

    public int getBoxQty() {
        return BoxQty;
    }

    public void setBoxQty(int BoxQty) {
        this.BoxQty = BoxQty;
    }

    public String getBoxCode() {
        return BoxCode;
    }

    public void setBoxCode(String BoxCode) {
        this.BoxCode = BoxCode;
    }

    public int getIsDelete() {
        return IsDelete;
    }

    public void setIsDelete(int IsDelete) {
        this.IsDelete = IsDelete;
    }

    public int getIsOK() {
        return IsOK;
    }

    public void setIsOK(int IsOK) {
        this.IsOK = IsOK;
    }
}
