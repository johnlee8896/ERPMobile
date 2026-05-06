package com.chinashb.www.mobileerp.shipment;

import java.io.Serializable;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description 外贸托盘标签信息
 */
public class TradePalletBean implements Serializable {

    private String Type;
    private long Pallet_ID;
    private String Pallet_SerialNo;
    private boolean Checked;
    private boolean LoadForDelivery;
    private boolean Deliveryed;
    private long AC_ID;
    private String Container_No;
    private long Delivery_ID;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public long getPallet_ID() {
        return Pallet_ID;
    }

    public void setPallet_ID(long pallet_ID) {
        Pallet_ID = pallet_ID;
    }

    public String getPallet_SerialNo() {
        return Pallet_SerialNo;
    }

    public void setPallet_SerialNo(String pallet_SerialNo) {
        Pallet_SerialNo = pallet_SerialNo;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public boolean isLoadForDelivery() {
        return LoadForDelivery;
    }

    public void setLoadForDelivery(boolean loadForDelivery) {
        LoadForDelivery = loadForDelivery;
    }

    public boolean isDeliveryed() {
        return Deliveryed;
    }

    public void setDeliveryed(boolean deliveryed) {
        Deliveryed = deliveryed;
    }

    public long getAC_ID() {
        return AC_ID;
    }

    public void setAC_ID(long AC_ID) {
        this.AC_ID = AC_ID;
    }

    public String getContainer_No() {
        return Container_No;
    }

    public void setContainer_No(String container_No) {
        Container_No = container_No;
    }

    public long getDelivery_ID() {
        return Delivery_ID;
    }

    public void setDelivery_ID(long delivery_ID) {
        Delivery_ID = delivery_ID;
    }
}
