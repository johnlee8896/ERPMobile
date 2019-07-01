package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/6/28 10:48
 * @author 作者: xxblwf
 * @description 成品的item详情
 */

public class ProductItemBean {

    /**
     * LotID : 2391238
     * 数量 : 467
     * 状态 : 正常使用
     * 批次号 : 180423-01
     * 入库日期 : 2018-04-23T00:00:00
     * 标注 :
     */

    @SerializedName("LotID")
    private int LotID;
    @SerializedName("数量")
    private int 数量;
    @SerializedName("状态")
    private String 状态;
    @SerializedName("批次号")
    private String 批次号;
    @SerializedName("入库日期")
    private String 入库日期;
    @SerializedName("标注")
    private String 标注;

    public int getLotID() {
        return LotID;
    }

    public void setLotID(int LotID) {
        this.LotID = LotID;
    }

    public int get数量() {
        return 数量;
    }

    public void set数量(int 数量) {
        this.数量 = 数量;
    }

    public String get状态() {
        return 状态;
    }

    public void set状态(String 状态) {
        this.状态 = 状态;
    }

    public String get批次号() {
        return 批次号;
    }

    public void set批次号(String 批次号) {
        this.批次号 = 批次号;
    }

    public String get入库日期() {
        return 入库日期;
    }

    public void set入库日期(String 入库日期) {
        this.入库日期 = 入库日期;
    }

    public String get标注() {
        return 标注;
    }

    public void set标注(String 标注) {
        this.标注 = 标注;
    }
}
