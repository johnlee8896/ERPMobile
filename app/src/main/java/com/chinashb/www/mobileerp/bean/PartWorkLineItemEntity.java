package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/10 13:34
 * @author 作者: xxblwf
 * @description 生产线领料中产线对应的item
 */

public class PartWorkLineItemEntity {
    @SerializedName("Item_ID") private long itemId;
    @SerializedName("IV_ID") private long ivId;
    @SerializedName("Transferred_Item_Name") private String itemName;
    @SerializedName("Item_Version") private String itemVersion;

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getIvId() {
        return ivId;
    }

    public void setIvId(long ivId) {
        this.ivId = ivId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemVersion() {
        return itemVersion;
    }

    public void setItemVersion(String itemVersion) {
        this.itemVersion = itemVersion;
    }
}
