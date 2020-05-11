package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/5/11 10:34
 * @author 作者: xxblwf
 * @description 要货计划中ivid bean
 */

public class PlanGoodsIVIDBean {

    @SerializedName("IV_ID") private long ivID;

    public long getIvID() {
        return ivID;
    }

    public void setIvID(long ivID) {
        this.ivID = ivID;
    }
}
