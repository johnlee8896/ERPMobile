package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/16 15:18
 * @author 作者: xxblwf
 * @description 收货客户公司Bean
 */

public class ReceiverCompanyBean {
    @SerializedName("CF_ID") private int cfId;
    @SerializedName("Customer") private String customer;
    @SerializedName("Factory") private String factory;
    @SerializedName("CF_Address") private String fgAddress;
    @SerializedName("Country") private String country;

    public int getCfId() {
        return cfId;
    }

    public void setCfId(int cfId) {
        this.cfId = cfId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getFgAddress() {
        return fgAddress;
    }

    public void setFgAddress(String fgAddress) {
        this.fgAddress = fgAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
