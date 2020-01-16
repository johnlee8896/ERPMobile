package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/16 15:01
 * @author 作者: xxblwf
 * @description 车间bean
 */

public class BuBean {
    @SerializedName("Bu_ID") private int buId;
    @SerializedName("Bu_Name") private String buName;

    public int getBuId() {
        return buId;
    }

    public void setBuId(int buId) {
        this.buId = buId;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }
}
