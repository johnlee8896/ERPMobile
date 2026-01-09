package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 1/7/26 11:03 AM
 * @author 作者: liweifeng
 * @description 零部件及马来的采购订单bean，因是从金蝶生成的
 */
public class StockInPurchaseOrderBean implements Parcelable {

    /**
     * POI_ID : 1338971
     * PO_ID : 226056
     * PO_No : D0901202512270001
     * 下单日期 : /Date(1766764800000+0800)/
     * KisCode : 4.01.026.0001
     * Item_ID : 48327
     * 物料编码 : P-SE00600-8001
     * 物料 : 托盘
     * 规格 : 1115X870X620
     * 版本 : A1
     * 单位 : 个
     * 采购数量 : 50.0
     * 已关联数量 : 0.0
     * 未关联数量 : 50.0
     * 金蝶采购单号 : CGDD226204
     */

    @SerializedName("POI_ID") private int POIID;
    @SerializedName("PO_ID") private int POID;
    @SerializedName("PO_No") private String PONo;
    @SerializedName("下单日期") private String orderDate;
    @SerializedName("KisCode") private String KisCode;
    @SerializedName("Item_ID") private int ItemID;
    @SerializedName("物料编码") private String itemCode;
    @SerializedName("物料") private String itemName;
    @SerializedName("规格") private String spec;
    @SerializedName("版本") private String version;
    @SerializedName("单位") private String unit;
    @SerializedName("采购数量") private double purchaseQty;
    @SerializedName("已关联数量") private double linkedQty;
    @SerializedName("未关联数量") private double unLinkedQty;
    @SerializedName("金蝶采购单号") private String kisOrderNO;

    protected StockInPurchaseOrderBean(Parcel in) {
        POIID = in.readInt();
        POID = in.readInt();
        PONo = in.readString();
        orderDate = in.readString();
        KisCode = in.readString();
        ItemID = in.readInt();
        itemCode = in.readString();
        itemName = in.readString();
        spec = in.readString();
        version = in.readString();
        unit = in.readString();
        purchaseQty = in.readDouble();
        linkedQty = in.readDouble();
        unLinkedQty = in.readDouble();
        kisOrderNO = in.readString();
    }

    public static final Creator<StockInPurchaseOrderBean> CREATOR = new Creator<StockInPurchaseOrderBean>() {
        @Override
        public StockInPurchaseOrderBean createFromParcel(Parcel in) {
            return new StockInPurchaseOrderBean(in);
        }

        @Override
        public StockInPurchaseOrderBean[] newArray(int size) {
            return new StockInPurchaseOrderBean[size];
        }
    };

    public int getPOIID() {
        return POIID;
    }

    public StockInPurchaseOrderBean setPOIID(int POIID) {
        this.POIID = POIID;
        return this;
    }

    public int getPOID() {
        return POID;
    }

    public StockInPurchaseOrderBean setPOID(int POID) {
        this.POID = POID;
        return this;
    }

    public String getPONo() {
        return PONo;
    }

    public StockInPurchaseOrderBean setPONo(String PONo) {
        this.PONo = PONo;
        return this;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public StockInPurchaseOrderBean setOrderDate(String orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public String getKisCode() {
        return KisCode;
    }

    public StockInPurchaseOrderBean setKisCode(String kisCode) {
        KisCode = kisCode;
        return this;
    }

    public int getItemID() {
        return ItemID;
    }

    public StockInPurchaseOrderBean setItemID(int itemID) {
        ItemID = itemID;
        return this;
    }

    public String getItemCode() {
        return itemCode;
    }

    public StockInPurchaseOrderBean setItemCode(String itemCode) {
        this.itemCode = itemCode;
        return this;
    }

    public String getItemName() {
        return itemName;
    }

    public StockInPurchaseOrderBean setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public String getSpec() {
        return spec;
    }

    public StockInPurchaseOrderBean setSpec(String spec) {
        this.spec = spec;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public StockInPurchaseOrderBean setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public StockInPurchaseOrderBean setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public double getPurchaseQty() {
        return purchaseQty;
    }

    public StockInPurchaseOrderBean setPurchaseQty(double purchaseQty) {
        this.purchaseQty = purchaseQty;
        return this;
    }

    public double getLinkedQty() {
        return linkedQty;
    }

    public StockInPurchaseOrderBean setLinkedQty(double linkedQty) {
        this.linkedQty = linkedQty;
        return this;
    }

    public double getUnLinkedQty() {
        return unLinkedQty;
    }

    public StockInPurchaseOrderBean setUnLinkedQty(double unLinkedQty) {
        this.unLinkedQty = unLinkedQty;
        return this;
    }

    public String getKisOrderNO() {
        return kisOrderNO;
    }

    public StockInPurchaseOrderBean setKisOrderNO(String kisOrderNO) {
        this.kisOrderNO = kisOrderNO;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(POIID);
        dest.writeInt(POID);
        dest.writeString(PONo);
        dest.writeString(orderDate);
        dest.writeString(KisCode);
        dest.writeInt(ItemID);
        dest.writeString(itemCode);
        dest.writeString(itemName);
        dest.writeString(spec);
        dest.writeString(version);
        dest.writeString(unit);
        dest.writeDouble(purchaseQty);
        dest.writeDouble(linkedQty);
        dest.writeDouble(unLinkedQty);
        dest.writeString(kisOrderNO);
    }
}
