package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * codex by john 2026-09-10 13:33 - 物流来料检索条目
 * 字段名对应服务端 SQL 里的中文别名
 */
public class LogisticsIncomeBean {
    @SerializedName("过程状态") private String processStatus;
    @SerializedName("发出日期") private String sendDate;
    @SerializedName("接收时间") private String receiveTime;
    @SerializedName("供应商") private String supplierName;
    @SerializedName("Item_ID") private long itemId;
    @SerializedName("名称") private String itemName;
    @SerializedName("物料编码") private String itemCode;
    @SerializedName("版本") private String version;
    @SerializedName("生产批次") private String productionBatch;
    @SerializedName("数量") private double qty;
    @SerializedName("件数") private double packageQty;
    @SerializedName("检验备注") private String checkRemark;

    public String getProcessStatus() {
        return processStatus;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public long getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getVersion() {
        return version;
    }

    public String getProductionBatch() {
        return productionBatch;
    }

    public double getQty() {
        return qty;
    }

    public double getPackageQty() {
        return packageQty;
    }

    public String getCheckRemark() {
        return checkRemark;
    }
}
