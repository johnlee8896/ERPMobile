package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2023/8/12 10:49 AM
 * @author 作者: liweifeng
 * @description
 */
public class ScanIstItemProductBean {

    /**
     * Product_Chinese_Name : 电机总成
     * Product_PartNo : 3257162
     * IST_ID : 9604
     * Sub_IST_ID : 40464
     * 存储区域 : B14
     * 单元 : B14-15
     * LotID : 6477236
     * 批次号 : 230801/230801
     * 库存 : 768.0
     * 在托盘 : true
     * Status_ID : 101
     * 状态 : 正常使用
     */

    @SerializedName("Product_Chinese_Name") private String ProductChineseName;
    @SerializedName("Product_PartNo") private String ProductPartNo;
    @SerializedName("IST_ID") private int ISTID;
    @SerializedName("Sub_IST_ID") private int SubISTID;
    @SerializedName("存储区域") private String 存储区域;
    @SerializedName("单元") private String 单元;
    @SerializedName("LotID") private int LotID;
    @SerializedName("批次号") private String 批次号;
    @SerializedName("库存") private double 库存;
    @SerializedName("在托盘") private boolean 在托盘;
    @SerializedName("Status_ID") private int StatusID;
    @SerializedName("状态") private String 状态;

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

    public int getISTID() {
        return ISTID;
    }

    public void setISTID(int ISTID) {
        this.ISTID = ISTID;
    }

    public int getSubISTID() {
        return SubISTID;
    }

    public void setSubISTID(int SubISTID) {
        this.SubISTID = SubISTID;
    }

    public String get存储区域() {
        return 存储区域;
    }

    public void set存储区域(String 存储区域) {
        this.存储区域 = 存储区域;
    }

    public String get单元() {
        return 单元;
    }

    public void set单元(String 单元) {
        this.单元 = 单元;
    }

    public int getLotID() {
        return LotID;
    }

    public void setLotID(int LotID) {
        this.LotID = LotID;
    }

    public String get批次号() {
        return 批次号;
    }

    public void set批次号(String 批次号) {
        this.批次号 = 批次号;
    }

    public double get库存() {
        return 库存;
    }

    public void set库存(double 库存) {
        this.库存 = 库存;
    }

    public boolean is在托盘() {
        return 在托盘;
    }

    public void set在托盘(boolean 在托盘) {
        this.在托盘 = 在托盘;
    }

    public int getStatusID() {
        return StatusID;
    }

    public void setStatusID(int StatusID) {
        this.StatusID = StatusID;
    }

    public String get状态() {
        return 状态;
    }

    public void set状态(String 状态) {
        this.状态 = 状态;
    }
}
