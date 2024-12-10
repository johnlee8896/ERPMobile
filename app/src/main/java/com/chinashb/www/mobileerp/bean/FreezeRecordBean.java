package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 11/18/24 6:51 PM
 * @author 作者: liweifeng
 * @description 冻结记录bean
 */
public class FreezeRecordBean {

    /**
     * FUR_ID : 1
     * OP_Time : /Date(1731909681953+0800)/
     * Freeze : true
     * executeSuccess : true
     * failmsg :
     * scan_code : null
     * bu_id : 81
     * HR_ID : 26009
     * HR_Name : null
     * Item_ID : null
     * SMT_ID : 2327077
     * SMLI_ID : 0
     * Remark : 测试一下
     * HR_Name1 : 李伟锋
     */

    @SerializedName("OP_Time") private String OPTime;
    @SerializedName("Freeze") private boolean Freeze;
    @SerializedName("executeSuccess") private boolean executeSuccess;
    @SerializedName("failmsg") private String failmsg;
    @SerializedName("scan_code") private Object scanCode;
    @SerializedName("bu_id") private int buId;
    @SerializedName("HR_ID") private int HRID;
    @SerializedName("HR_Name") private String HRName;
    @SerializedName("Item_ID") private int ItemID;
    @SerializedName("SMT_ID") private int SMTID;
    @SerializedName("SMLI_ID") private int SMLIID;
    @SerializedName("Remark") private String Remark;
    @SerializedName("HR_Name1") private String HRName1;

    public String getOPTime() {
        return OPTime;
    }

    public FreezeRecordBean setOPTime(String OPTime) {
        this.OPTime = OPTime;
        return this;
    }

    public boolean isFreeze() {
        return Freeze;
    }

    public FreezeRecordBean setFreeze(boolean freeze) {
        Freeze = freeze;
        return this;
    }

    public boolean isExecuteSuccess() {
        return executeSuccess;
    }

    public FreezeRecordBean setExecuteSuccess(boolean executeSuccess) {
        this.executeSuccess = executeSuccess;
        return this;
    }

    public String getFailmsg() {
        return failmsg;
    }

    public FreezeRecordBean setFailmsg(String failmsg) {
        this.failmsg = failmsg;
        return this;
    }

    public Object getScanCode() {
        return scanCode;
    }

    public FreezeRecordBean setScanCode(Object scanCode) {
        this.scanCode = scanCode;
        return this;
    }

    public int getBuId() {
        return buId;
    }

    public FreezeRecordBean setBuId(int buId) {
        this.buId = buId;
        return this;
    }

    public int getHRID() {
        return HRID;
    }

    public FreezeRecordBean setHRID(int HRID) {
        this.HRID = HRID;
        return this;
    }

    public String getHRName() {
        return HRName;
    }

    public FreezeRecordBean setHRName(String HRName) {
        this.HRName = HRName;
        return this;
    }

    public int getItemID() {
        return ItemID;
    }

    public FreezeRecordBean setItemID(int itemID) {
        ItemID = itemID;
        return this;
    }

    public int getSMTID() {
        return SMTID;
    }

    public FreezeRecordBean setSMTID(int SMTID) {
        this.SMTID = SMTID;
        return this;
    }

    public int getSMLIID() {
        return SMLIID;
    }

    public FreezeRecordBean setSMLIID(int SMLIID) {
        this.SMLIID = SMLIID;
        return this;
    }

    public String getRemark() {
        return Remark;
    }

    public FreezeRecordBean setRemark(String remark) {
        Remark = remark;
        return this;
    }

    public String getHRName1() {
        return HRName1;
    }

    public FreezeRecordBean setHRName1(String HRName1) {
        this.HRName1 = HRName1;
        return this;
    }
}
