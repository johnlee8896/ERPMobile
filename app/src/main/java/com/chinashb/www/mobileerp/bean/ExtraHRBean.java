package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 11/6/24 9:17 AM
 * @author 作者: liweifeng
 * @description 额外领料名单权限在线获取
 */
public class ExtraHRBean {
    @SerializedName("HR_ID") private int hrID;

    public int getHrID() {
        return hrID;
    }

    public ExtraHRBean setHrID(int hrID) {
        this.hrID = hrID;
        return this;
    }
}
