package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2023/7/12 8:38 AM
 * @author 作者: liweifeng
 * @description
 */
public class BoxMoveRecordBean {

    /**
     * item_id,item.item,item.item_version,supplier_manu_top_box.iqty
     * bu_name : 滁州座椅电机
     * 移出库位 : A07
     * 移出单元 : A07-14-2
     * 移到库位 : A07
     * 移到单元 : A07-3-1
     * 移库人 : 张旭6
     * 移库时间 : /Date(1660407732340+0800)/
     * 移库类型 : 大箱
     * boxcode : VG/1397739
     */

    @SerializedName("bu_name") private String buName;
    @SerializedName("item_id") private int item_id;
    @SerializedName("item") private String itemDrawNO;
    @SerializedName("item_version") private String item_version;
    @SerializedName("iqty") private String iqty;
    @SerializedName("移出库位") private String 移出库位;
    @SerializedName("移出单元") private String 移出单元;
    @SerializedName("移到库位") private String 移到库位;
    @SerializedName("移到单元") private String 移到单元;
    @SerializedName("移库人") private String 移库人;
    @SerializedName("移库时间") private String 移库时间;
    @SerializedName("移库类型") private String 移库类型;
    @SerializedName("boxcode") private String boxcode;

    public String getBuName() {
        return buName;
    }

    public BoxMoveRecordBean setBuName(String buName) {
        this.buName = buName;
        return this;
    }

    public int getItem_id() {
        return item_id;
    }

    public BoxMoveRecordBean setItem_id(int item_id) {
        this.item_id = item_id;
        return this;
    }

    public String getItemDrawNO() {
        return itemDrawNO;
    }

    public BoxMoveRecordBean setItemDrawNO(String itemDrawNO) {
        this.itemDrawNO = itemDrawNO;
        return this;
    }

    public String getItem_version() {
        return item_version;
    }

    public BoxMoveRecordBean setItem_version(String item_version) {
        this.item_version = item_version;
        return this;
    }

    public String getIqty() {
        return iqty;
    }

    public BoxMoveRecordBean setIqty(String iqty) {
        this.iqty = iqty;
        return this;
    }

    public String get移出库位() {
        return 移出库位;
    }

    public BoxMoveRecordBean set移出库位(String 移出库位) {
        this.移出库位 = 移出库位;
        return this;
    }

    public String get移出单元() {
        return 移出单元;
    }

    public BoxMoveRecordBean set移出单元(String 移出单元) {
        this.移出单元 = 移出单元;
        return this;
    }

    public String get移到库位() {
        return 移到库位;
    }

    public BoxMoveRecordBean set移到库位(String 移到库位) {
        this.移到库位 = 移到库位;
        return this;
    }

    public String get移到单元() {
        return 移到单元;
    }

    public BoxMoveRecordBean set移到单元(String 移到单元) {
        this.移到单元 = 移到单元;
        return this;
    }

    public String get移库人() {
        return 移库人;
    }

    public BoxMoveRecordBean set移库人(String 移库人) {
        this.移库人 = 移库人;
        return this;
    }

    public String get移库时间() {
        return 移库时间;
    }

    public BoxMoveRecordBean set移库时间(String 移库时间) {
        this.移库时间 = 移库时间;
        return this;
    }

    public String get移库类型() {
        return 移库类型;
    }

    public BoxMoveRecordBean set移库类型(String 移库类型) {
        this.移库类型 = 移库类型;
        return this;
    }

    public String getBoxcode() {
        return boxcode;
    }

    public BoxMoveRecordBean setBoxcode(String boxcode) {
        this.boxcode = boxcode;
        return this;
    }
}
