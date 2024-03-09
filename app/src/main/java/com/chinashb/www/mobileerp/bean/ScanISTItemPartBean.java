package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2023/7/4 6:44 PM
 * @author 作者: liweifeng
 * @description
 */
public class ScanISTItemPartBean {

    /**
     * ASI_ID : 708265
     * IST_ID : 6748
     * Sub_Ist_ID : 30549
     * IV_ID : 104628
     * 物料版本 : S1
     * 单元 : A06.A06-1-1
     * LotID : 6241776
     * 入库日期 : /Date(1686240000000+0800)/
     * 入库批次 : 230609-02
     * 生产日期 : 2023/6/9
     * 生产批次 : 20230609-20230609-1
     * 状态 : 正常使用
     * 库存 : 37000.0
     * 在库日期 : 25
     * 质保天数 : 0
     */

    // TODO: 2023/7/5 如果要parcable 序列化需要重新生成 ，因新增了item_id,item_name,item_spec
    @SerializedName("ASI_ID") private int ASIID;
    @SerializedName("Item_ID") private int Item_ID;
    @SerializedName("IST_ID") private int ISTID;
    @SerializedName("Sub_Ist_ID") private int SubIstID;
    @SerializedName("IV_ID") private int IVID;
    @SerializedName("物料版本") private String 物料版本;
    @SerializedName("Item_Name") private String Item_Name;
    @SerializedName("Item_Spec2") private String Item_Spec2;
    @SerializedName("单元") private String 单元;
    @SerializedName("LotID") private int LotID;
    @SerializedName("入库日期") private String 入库日期;
    @SerializedName("入库批次") private String 入库批次;
    @SerializedName("生产日期") private String 生产日期;
    @SerializedName("生产批次") private String 生产批次;
    @SerializedName("状态") private String 状态;
    @SerializedName("库存") private double 库存;
    @SerializedName("在库日期") private int 在库日期;
    @SerializedName("质保天数") private int 质保天数;

    public int getASIID() {
        return ASIID;
    }

    public void setASIID(int ASIID) {
        this.ASIID = ASIID;
    }

    public int getISTID() {
        return ISTID;
    }

    public void setISTID(int ISTID) {
        this.ISTID = ISTID;
    }

    public int getSubIstID() {
        return SubIstID;
    }

    public void setSubIstID(int SubIstID) {
        this.SubIstID = SubIstID;
    }

    public int getIVID() {
        return IVID;
    }

    public void setIVID(int IVID) {
        this.IVID = IVID;
    }

    public String get物料版本() {
        return 物料版本;
    }

    public void set物料版本(String 物料版本) {
        this.物料版本 = 物料版本;
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

    public String get入库日期() {
        return 入库日期;
    }

    public void set入库日期(String 入库日期) {
        this.入库日期 = 入库日期;
    }

    public String get入库批次() {
        return 入库批次;
    }

    public void set入库批次(String 入库批次) {
        this.入库批次 = 入库批次;
    }

    public String get生产日期() {
        return 生产日期;
    }

    public void set生产日期(String 生产日期) {
        this.生产日期 = 生产日期;
    }

    public String get生产批次() {
        return 生产批次;
    }

    public void set生产批次(String 生产批次) {
        this.生产批次 = 生产批次;
    }

    public String get状态() {
        return 状态;
    }

    public void set状态(String 状态) {
        this.状态 = 状态;
    }

    public double get库存() {
        return 库存;
    }

    public void set库存(double 库存) {
        this.库存 = 库存;
    }

    public int get在库日期() {
        return 在库日期;
    }

    public void set在库日期(int 在库日期) {
        this.在库日期 = 在库日期;
    }

    public int get质保天数() {
        return 质保天数;
    }

    public void set质保天数(int 质保天数) {
        this.质保天数 = 质保天数;
    }

    public int getItem_ID() {
        return Item_ID;
    }

    public ScanISTItemPartBean setItem_ID(int item_ID) {
        Item_ID = item_ID;
        return this;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public ScanISTItemPartBean setItem_Name(String item_Name) {
        Item_Name = item_Name;
        return this;
    }

    public String getItem_Spec2() {
        return Item_Spec2;
    }

    public ScanISTItemPartBean setItem_Spec2(String item_Spec2) {
        Item_Spec2 = item_Spec2;
        return this;
    }
}
