package com.chinashb.www.mobileerp.shipment;

import java.io.Serializable;

/***
 * @date 创建时间 2026/5/2 14:12
 * @author 作者: code-x John
 * @description 外贸集装箱标签信息
 */
public class TradeContainerBean implements Serializable {

    private String Type;
    private long AC_ID;
    private String Container_No;
    private String Container_Type;
    private String Seal_No;
    private long Delivery_ID;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
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

    public String getContainer_Type() {
        return Container_Type;
    }

    public void setContainer_Type(String container_Type) {
        Container_Type = container_Type;
    }

    public String getSeal_No() {
        return Seal_No;
    }

    public void setSeal_No(String seal_No) {
        Seal_No = seal_No;
    }

    public long getDelivery_ID() {
        return Delivery_ID;
    }

    public void setDelivery_ID(long delivery_ID) {
        Delivery_ID = delivery_ID;
    }
}
