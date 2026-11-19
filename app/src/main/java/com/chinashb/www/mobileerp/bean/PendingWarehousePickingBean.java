package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * codex by john 2026-11-12 - 来料待入库拣货列表 Bean
 */
public class PendingWarehousePickingBean {
    @SerializedName("DIII_ID") private long diiiId;
    @SerializedName("DII_ID") private long diiId;
    @SerializedName("Code") private String code;
    @SerializedName("Bu_ID") private int buId;
    @SerializedName("Company_ID") private int companyId;
    @SerializedName("Supplier_ID") private int supplierId;
    @SerializedName("供应商") private String supplierName;
    @SerializedName("Item_ID") private long itemId;
    @SerializedName("物料编码") private String itemCode;
    @SerializedName("物料名称") private String itemName;
    @SerializedName("规格型号") private String itemSpec;
    @SerializedName("批次") private String lotNo;
    @SerializedName("数量") private double qty;
    @SerializedName("件数") private int packageQty;
    @SerializedName("主单总数量") private double orderTotalQty;
    @SerializedName("主单总件数") private int orderTotalPackageQty;
    @SerializedName("接收日期") private String receiveDate;
    @SerializedName("接收时间") private String receiveTime;
    @SerializedName("临时存放") private String temporaryLocation;
    @SerializedName("储位单元") private String storageLocation;
    @SerializedName("ShortageLevel") private int shortageLevel;
    @SerializedName("ShortageLabel") private String shortageLabel;
    @SerializedName("ShortageColor") private String shortageColor;
    @SerializedName("TodayTheoreticalBalance") private double todayTheoreticalBalance;
    @SerializedName("TomorrowTheoreticalBalance") private double tomorrowTheoreticalBalance;

    public long getDiiiId() {
        return diiiId;
    }

    public long getDiiId() {
        return diiId;
    }

    public String getCode() {
        return code;
    }

    public int getBuId() {
        return buId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public long getItemId() {
        return itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemSpec() {
        return itemSpec;
    }

    public String getLotNo() {
        return lotNo;
    }

    public double getQty() {
        return qty;
    }

    public int getPackageQty() {
        return packageQty;
    }

    public double getOrderTotalQty() {
        return orderTotalQty;
    }

    public int getOrderTotalPackageQty() {
        return orderTotalPackageQty;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public String getTemporaryLocation() {
        return temporaryLocation;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public int getShortageLevel() {
        return shortageLevel;
    }

    public String getShortageLabel() {
        return shortageLabel;
    }

    public String getShortageColor() {
        return shortageColor;
    }

    public double getTodayTheoreticalBalance() {
        return todayTheoreticalBalance;
    }

    public double getTomorrowTheoreticalBalance() {
        return tomorrowTheoreticalBalance;
    }

    public String getDisplayLocation() {
        return isEmpty(storageLocation) ? temporaryLocation : storageLocation;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
