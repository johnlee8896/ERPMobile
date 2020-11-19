package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2018/7/11 5:29 PM
 * @author 作者: liweifeng
 * @description 根据日期获取当天考勤详情
 */
public class AttendanceDetailBean {
    /**
     * date : 1531292881000
     * type : {"code":0,"message":"上班"}
     * out : {"code":0,"message":"正常"}
     * status : {"code":1,"message":"迟到"}
     * day_span : {"code":0,"message":"正常"}
     * address : 明道大厦
     * remark : 打卡
     */

//    @SerializedName("date") private long date;
            //这里因是VB的关键字，故无效
    @SerializedName("currentDate") private long date;
    @SerializedName("type") private StatusBean type;
    @SerializedName("out") private StatusBean out;
    @SerializedName("status") private StatusBean status;
    @SerializedName("day_span") private StatusBean daySpan;
    @SerializedName("address") private String address;
    @SerializedName("remark") private String remark;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public StatusBean getType() {
        return type;
    }

    public void setType(StatusBean type) {
        this.type = type;
    }

    public StatusBean getOut() {
        return out;
    }

    public void setOut(StatusBean out) {
        this.out = out;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public StatusBean getDaySpan() {
        return daySpan;
    }

    public void setDaySpan(StatusBean daySpan) {
        this.daySpan = daySpan;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
