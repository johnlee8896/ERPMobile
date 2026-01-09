package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 7/16/25 6:48 PM
 * @author 作者: liweifeng
 * @description 成品返工出库
 */
public class WCProductBean {

    /**
     * WC_Id : 639
     * 生产线 : 包装组
     * 车间 : 滁州座椅电机
     */

    @SerializedName("WC_Id") private int WCId;
    @SerializedName("生产线") private String 生产线;
    @SerializedName("车间") private String 车间;

    public int getWCId() {
        return WCId;
    }

    public void setWCId(int WCId) {
        this.WCId = WCId;
    }

    public String get生产线() {
        return 生产线;
    }

    public void set生产线(String 生产线) {
        this.生产线 = 生产线;
    }

    public String get车间() {
        return 车间;
    }

    public void set车间(String 车间) {
        this.车间 = 车间;
    }
}
