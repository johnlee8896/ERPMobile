package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 2026-10-29 john
 * 投料出库辅料列表新bean，对应 getAssistList 新SQL返回字段。
 * 原 AssistItemBean 保留不动。
 */
public class AssistItemNewBean {
    @SerializedName("IA_ID")
    private int IA_ID;
    @SerializedName("Item_ID")
    private int Item_ID;
    @SerializedName("名称")
    private String Item_Name;
    @SerializedName("物料编码")
    private String Item;
    @SerializedName("规格")
    private String Item_Spec2;
    @SerializedName("单位")
    private String Item_Unit;
    @SerializedName("单机用量")
    private float Qty;
    @SerializedName("备注")
    private String Remark;
    @SerializedName("储位")
    private String StorageLocation;
    @SerializedName("批次")
    private String LotNo;
    @SerializedName("入库时间")
    private String InboundTime;
    @SerializedName("库存数量")
    private float InventoryQty;

    public int getIA_ID() {
        return IA_ID;
    }

    public AssistItemNewBean setIA_ID(int IA_ID) {
        this.IA_ID = IA_ID;
        return this;
    }

    public int getItem_ID() {
        return Item_ID;
    }

    public AssistItemNewBean setItem_ID(int item_ID) {
        Item_ID = item_ID;
        return this;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public AssistItemNewBean setItem_Name(String item_Name) {
        Item_Name = item_Name;
        return this;
    }

    public String getItem() {
        return Item;
    }

    public AssistItemNewBean setItem(String item) {
        Item = item;
        return this;
    }

    public String getItem_Spec2() {
        return Item_Spec2;
    }

    public AssistItemNewBean setItem_Spec2(String item_Spec2) {
        Item_Spec2 = item_Spec2;
        return this;
    }

    public String getItem_Unit() {
        return Item_Unit;
    }

    public AssistItemNewBean setItem_Unit(String item_Unit) {
        Item_Unit = item_Unit;
        return this;
    }

    public float getQty() {
        return Qty;
    }

    public AssistItemNewBean setQty(float qty) {
        Qty = qty;
        return this;
    }

    public String getRemark() {
        return Remark;
    }

    public AssistItemNewBean setRemark(String remark) {
        Remark = remark;
        return this;
    }

    public String getStorageLocation() {
        return StorageLocation;
    }

    public AssistItemNewBean setStorageLocation(String storageLocation) {
        StorageLocation = storageLocation;
        return this;
    }

    public String getLotNo() {
        return LotNo;
    }

    public AssistItemNewBean setLotNo(String lotNo) {
        LotNo = lotNo;
        return this;
    }

    public String getInboundTime() {
        return InboundTime;
    }

    public AssistItemNewBean setInboundTime(String inboundTime) {
        InboundTime = inboundTime;
        return this;
    }

    public float getInventoryQty() {
        return InventoryQty;
    }

    public AssistItemNewBean setInventoryQty(float inventoryQty) {
        InventoryQty = inventoryQty;
        return this;
    }
}
