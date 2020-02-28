package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/2/26 9:43 AM
 * @author 作者: liweifeng
 * @description 发货指令中发货单详情，即其中的箱子内容
 */
public class SDZHDeliveryOrderNumberDetailBean {

    /**
     * DOP_Id : 31
     * Program_ID : 2445
     * Program_Name : 532后刮
     * Product_ID : 18240
     * PS_ID : 21911
     * OrderNo : CLD0A252A12I1A
     * Product_Name : 电机
     * Product_Common_Name : DF-532H-ZD1221C
     * Product_PartNo : 28710 2GN0A
     * PS_Version : S1
     * Newest_version : S1
     */

    @SerializedName("DOP_Id") private int DOPId;
    @SerializedName("Program_ID") private int ProgramID;
    @SerializedName("Program_Name") private String ProgramName;
    @SerializedName("Product_ID") private int ProductID;
    @SerializedName("PS_ID") private int PSID;
    @SerializedName("OrderNo") private String OrderNo;
    @SerializedName("Product_Name") private String ProductName;
    @SerializedName("Product_Common_Name") private String ProductCommonName;
    @SerializedName("Product_PartNo") private String ProductPartNo;
    @SerializedName("PS_Version") private String PSVersion;
    @SerializedName("Newest_version") private String NewestVersion;

    public int getDOPId() {
        return DOPId;
    }

    public void setDOPId(int DOPId) {
        this.DOPId = DOPId;
    }

    public int getProgramID() {
        return ProgramID;
    }

    public void setProgramID(int ProgramID) {
        this.ProgramID = ProgramID;
    }

    public String getProgramName() {
        return ProgramName;
    }

    public void setProgramName(String ProgramName) {
        this.ProgramName = ProgramName;
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

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String ProductName) {
        this.ProductName = ProductName;
    }

    public String getProductCommonName() {
        return ProductCommonName;
    }

    public void setProductCommonName(String ProductCommonName) {
        this.ProductCommonName = ProductCommonName;
    }

    public String getProductPartNo() {
        return ProductPartNo;
    }

    public void setProductPartNo(String ProductPartNo) {
        this.ProductPartNo = ProductPartNo;
    }

    public String getPSVersion() {
        return PSVersion;
    }

    public void setPSVersion(String PSVersion) {
        this.PSVersion = PSVersion;
    }

    public String getNewestVersion() {
        return NewestVersion;
    }

    public void setNewestVersion(String NewestVersion) {
        this.NewestVersion = NewestVersion;
    }
}
