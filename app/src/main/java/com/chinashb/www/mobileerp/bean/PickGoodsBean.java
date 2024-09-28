package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 7/25/24 4:11 PM
 * @author 作者: liweifeng
 * @description 拣货任务bean
 */
public class PickGoodsBean implements Parcelable {

    @SerializedName("大区") private String bigArea;
    @SerializedName("存储单元") private String areaUnit;
    @SerializedName("批次号") private String lotNo;
    @SerializedName("Item_ID") private int itemID;
    @SerializedName("规格型号") private String spec;
    @SerializedName("物料名称") private String itemName;
    @SerializedName("数量") private float qty;
    @SerializedName("Sub_IST_ID") private int subIstID;

    protected PickGoodsBean(Parcel in) {
        bigArea = in.readString();
        areaUnit = in.readString();
        lotNo = in.readString();
        itemID = in.readInt();
        spec = in.readString();
        itemName = in.readString();
        qty = in.readFloat();
        subIstID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bigArea);
        dest.writeString(areaUnit);
        dest.writeString(lotNo);
        dest.writeInt(itemID);
        dest.writeString(spec);
        dest.writeString(itemName);
        dest.writeFloat(qty);
        dest.writeInt(subIstID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PickGoodsBean> CREATOR = new Creator<PickGoodsBean>() {
        @Override
        public PickGoodsBean createFromParcel(Parcel in) {
            return new PickGoodsBean(in);
        }

        @Override
        public PickGoodsBean[] newArray(int size) {
            return new PickGoodsBean[size];
        }
    };

    public String getBigArea() {
        return bigArea;
    }

    public PickGoodsBean setBigArea(String bigArea) {
        this.bigArea = bigArea;
        return this;
    }

    public String getAreaUnit() {
        return areaUnit;
    }

    public PickGoodsBean setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
        return this;
    }

    public String getLotNo() {
        return lotNo;
    }

    public PickGoodsBean setLotNo(String lotNo) {
        this.lotNo = lotNo;
        return this;
    }

    public int getItemID() {
        return itemID;
    }

    public PickGoodsBean setItemID(int itemID) {
        this.itemID = itemID;
        return this;
    }

    public String getSpec() {
        return spec;
    }

    public PickGoodsBean setSpec(String spec) {
        this.spec = spec;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public PickGoodsBean setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public float getQty() {
        return qty;
    }

    public PickGoodsBean setQty(float qty) {
        this.qty = qty;
        return this;
    }

    public int getSubIstID() {
        return subIstID;
    }

    public PickGoodsBean setSubIstID(int subIstID) {
        this.subIstID = subIstID;
        return this;
    }
}
