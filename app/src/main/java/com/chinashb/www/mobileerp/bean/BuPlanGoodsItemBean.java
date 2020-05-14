package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/5/12 14:16
 * @author 作者: xxblwf
 * @description 滁州车间要货计划 bean
 */

public class BuPlanGoodsItemBean {

    /**
     * No : 1
     * Item_ID : 3267
     * IV_ID : 3337
     * item_spec : PYG-CA7220-1212-1 衬套
     * item_name_code : PYG-CA7220-1212-1
     * item_name : 衬套
     * spec :
     * version :
     */

    @SerializedName("No") private int No;
    @SerializedName("Item_ID") private int ItemID;
    @SerializedName("IV_ID") private int IVID;
    @SerializedName("item_spec") private String itemSpec;
    @SerializedName("item_name_code") private String itemNameCode;
    @SerializedName("item_name") private String itemName;
    @SerializedName("spec") private String spec;
    @SerializedName("version") private String version;

    public int getNo() {
        return No;
    }

    public void setNo(int No) {
        this.No = No;
    }

    public int getItemID() {
        return ItemID;
    }

    public void setItemID(int ItemID) {
        this.ItemID = ItemID;
    }

    public int getIVID() {
        return IVID;
    }

    public void setIVID(int IVID) {
        this.IVID = IVID;
    }

    public String getItemSpec() {
        return itemSpec;
    }

    public void setItemSpec(String itemSpec) {
        this.itemSpec = itemSpec;
    }

    public String getItemNameCode() {
        return itemNameCode;
    }

    public void setItemNameCode(String itemNameCode) {
        this.itemNameCode = itemNameCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}