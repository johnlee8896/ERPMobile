package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2018/7/10 1:44 PM
 * @author 作者: liweifeng
 * @description 系统时间
 */
public class SystemDateBean {

    /**
     * current_time : 1531200292820
     * expired : true
     */

    @SerializedName("current_time") private long currentTime;
    @SerializedName("expired") private boolean expired;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
