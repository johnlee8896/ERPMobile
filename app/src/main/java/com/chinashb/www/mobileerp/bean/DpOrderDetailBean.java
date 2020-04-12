package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/4/7 15:44
 * @author 作者: xxblwf
 * @description 发货指令item详情
 */

public class DpOrderDetailBean {

    /**
     * Program_Name : CHB021前&后刮
     * Product_ID : 11919
     * PS_ID : 16361
     * Abb : 前刮水器连杆电机总成
     * Product_PartNo : 5205110XKZ16A
     * Product_Version : A1
     * DPI_Quantity : 2496
     */

    @SerializedName("Program_Name") private String ProgramName;
    @SerializedName("Product_ID") private int ProductID;
    @SerializedName("PS_ID") private int PSID;
    @SerializedName("Abb") private String ProductChineseName;
    @SerializedName("Product_PartNo") private String ProductPartNo;
    @SerializedName("Product_Version") private String ProductVersion;
    @SerializedName("DPI_Quantity") private String DPIQuantity;

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

    public String getProductChineseName() {
        return ProductChineseName;
    }

    public void setProductChineseName(String ProductChineseName) {
        this.ProductChineseName = ProductChineseName;
    }

    public String getProductPartNo() {
        return ProductPartNo;
    }

    public void setProductPartNo(String ProductPartNo) {
        this.ProductPartNo = ProductPartNo;
    }

    public String getProductVersion() {
        return ProductVersion;
    }

    public void setProductVersion(String ProductVersion) {
        this.ProductVersion = ProductVersion;
    }

    public String getDPIQuantity() {
        return DPIQuantity;
    }

    public void setDPIQuantity(String DPIQuantity) {
        this.DPIQuantity = DPIQuantity;
    }
}
