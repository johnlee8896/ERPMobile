package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2022/5/19 11:07 AM
 * @author 作者: liweifeng
 * @description 成品包装库存
 */
public class ProductPackBean implements Parcelable {

//    {
//        "Pallet_Id":633978,
//            "Product_ID":17523,
//            "PS_ID":21005,
//            "产品通称":"P-6801126-CD03",
//            "客户零件号":"6801126-CD03",
//            "版本":"A00",
//            "批次":"220321/220327",
//            "包装序列号":"03D03/59",
//            "包装日期":"\/Date(1648396800000+0800)\/",
//            "Package_ID":10887,
//            "包装形式":"通用电机",
//            "标准包装数":324,
//            "产品数量":324,
//            "未满数":null,
//            "指定客户":null,
//            "托盘状态":"正常使用",
//            "存储区域":null
//    }

    @SerializedName("LotID") private int Lot_ID;
    @SerializedName("PS_ID") private int PS_ID;
    @SerializedName("产品数量") private int 产品数量;
    @SerializedName("状态") private String 状态;
    @SerializedName("批次") private String 批次;
    @SerializedName("包装日期") private String 包装日期;
    @SerializedName("入库日期") private String 入库日期;
    @SerializedName("备注") private String 标注;
    @SerializedName("存储区域") private String 存储区域;
    @SerializedName("包装序列号") private String 包装序列号;

    public int getLot_ID() {
        return Lot_ID;
    }

    public ProductPackBean setLot_ID(int lot_ID) {
        Lot_ID = lot_ID;
        return this;
    }

    public int getPS_ID() {
        return PS_ID;
    }

    public ProductPackBean setPS_ID(int PS_ID) {
        this.PS_ID = PS_ID;
        return this;
    }

    public int get产品数量() {
        return 产品数量;
    }

    public ProductPackBean set产品数量(int 产品数量) {
        this.产品数量 = 产品数量;
        return this;
    }

    public String get状态() {
        return 状态;
    }

    public ProductPackBean set状态(String 状态) {
        this.状态 = 状态;
        return this;
    }

    public String get批次() {
        return 批次;
    }

    public ProductPackBean set批次(String 批次) {
        this.批次 = 批次;
        return this;
    }

    public String get包装日期() {
        return 包装日期;
    }

    public ProductPackBean set包装日期(String 包装日期) {
        this.包装日期 = 包装日期;
        return this;
    }

    public String get入库日期() {
        return 入库日期;
    }

    public ProductPackBean set入库日期(String 入库日期) {
        this.入库日期 = 入库日期;
        return this;
    }

    public String get标注() {
        return 标注;
    }

    public ProductPackBean set标注(String 标注) {
        this.标注 = 标注;
        return this;
    }

    public String get存储区域() {
        return 存储区域;
    }

    public ProductPackBean set存储区域(String 存储区域) {
        this.存储区域 = 存储区域;
        return this;
    }

    public String get包装序列号() {
        return 包装序列号;
    }

    public ProductPackBean set包装序列号(String 包装序列号) {
        this.包装序列号 = 包装序列号;
        return this;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.Lot_ID);
        dest.writeInt(this.PS_ID);
        dest.writeInt(this.产品数量);
        dest.writeString(this.状态);
        dest.writeString(this.批次);
        dest.writeString(this.包装日期);
        dest.writeString(this.入库日期);
        dest.writeString(this.标注);
        dest.writeString(this.存储区域);
        dest.writeString(this.包装序列号);
    }

    public ProductPackBean() {
    }

    protected ProductPackBean(Parcel in) {
        this.Lot_ID = in.readInt();
        this.PS_ID = in.readInt();
        this.产品数量 = in.readInt();
        this.状态 = in.readString();
        this.批次 = in.readString();
        this.包装日期 = in.readString();
        this.入库日期 = in.readString();
        this.标注 = in.readString();
        this.存储区域 = in.readString();
        this.包装序列号 = in.readString();
    }

    public static final Creator<ProductPackBean> CREATOR = new Creator<ProductPackBean>() {
        @Override
        public ProductPackBean createFromParcel(Parcel source) {
            return new ProductPackBean(source);
        }

        @Override
        public ProductPackBean[] newArray(int size) {
            return new ProductPackBean[size];
        }
    };
}
