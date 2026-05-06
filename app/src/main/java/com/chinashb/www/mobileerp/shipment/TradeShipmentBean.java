package com.chinashb.www.mobileerp.shipment;

import java.io.Serializable;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description 外贸发运单标签信息
 */
public class TradeShipmentBean implements Serializable {

    private String Type;
    private long Delivery_ID;
    private String TrackNo;
    private int Bu_ID;
    private long CF_ID;
    private String Delivery_Date;
    private String Shiping_Date;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public long getDelivery_ID() {
        return Delivery_ID;
    }

    public void setDelivery_ID(long delivery_ID) {
        Delivery_ID = delivery_ID;
    }

    public String getTrackNo() {
        return TrackNo;
    }

    public void setTrackNo(String trackNo) {
        TrackNo = trackNo;
    }

    public int getBu_ID() {
        return Bu_ID;
    }

    public void setBu_ID(int bu_ID) {
        Bu_ID = bu_ID;
    }

    public long getCF_ID() {
        return CF_ID;
    }

    public void setCF_ID(long CF_ID) {
        this.CF_ID = CF_ID;
    }

    public String getDelivery_Date() {
        return Delivery_Date;
    }

    public void setDelivery_Date(String delivery_Date) {
        Delivery_Date = delivery_Date;
    }

    public String getShiping_Date() {
        return Shiping_Date;
    }

    public void setShiping_Date(String shiping_Date) {
        Shiping_Date = shiping_Date;
    }
}
