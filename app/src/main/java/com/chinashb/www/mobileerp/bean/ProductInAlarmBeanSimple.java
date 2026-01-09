package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 6/3/25 2:14 PM
 * @author 作者: liweifeng
 * @description 简易的成品入库alarm
 */
public class ProductInAlarmBeanSimple {

    /**
     * DO_ID : 180161
     * DPI_ID : 372817
     * CPO_ID : 0
     * 销售公司 : 滁州座椅
     * BU_ID : 81
     * 余数 : 324.0
     * 排版 : false
     * 库存够发货 : false
     */

    @SerializedName("DO_ID") private int DOID;
    @SerializedName("DPI_ID") private int DPIID;
    @SerializedName("CPO_ID") private int CPOID;
    @SerializedName("销售公司") private String 销售公司;
    @SerializedName("BU_ID") private int BUID;
    @SerializedName("余数") private double 余数;
    @SerializedName("排版") private boolean 排版;
    @SerializedName("库存够发货") private boolean 库存够发货;

    public int getDOID() {
        return DOID;
    }

    public void setDOID(int DOID) {
        this.DOID = DOID;
    }

    public int getDPIID() {
        return DPIID;
    }

    public void setDPIID(int DPIID) {
        this.DPIID = DPIID;
    }

    public int getCPOID() {
        return CPOID;
    }

    public void setCPOID(int CPOID) {
        this.CPOID = CPOID;
    }

    public String get销售公司() {
        return 销售公司;
    }

    public void set销售公司(String 销售公司) {
        this.销售公司 = 销售公司;
    }

    public int getBUID() {
        return BUID;
    }

    public void setBUID(int BUID) {
        this.BUID = BUID;
    }

    public double get余数() {
        return 余数;
    }

    public void set余数(double 余数) {
        this.余数 = 余数;
    }

    public boolean is排版() {
        return 排版;
    }

    public void set排版(boolean 排版) {
        this.排版 = 排版;
    }

    public boolean is库存够发货() {
        return 库存够发货;
    }

    public void set库存够发货(boolean 库存够发货) {
        this.库存够发货 = 库存够发货;
    }
}
