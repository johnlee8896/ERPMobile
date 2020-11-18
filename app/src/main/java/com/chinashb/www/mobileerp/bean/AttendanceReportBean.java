package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2018/7/26 4:36 PM
 * @author 作者: liweifeng
 * @descriptio 考勤报表统计，如30天，主要为
 */
public class AttendanceReportBean {

    /**
     * date : 1530028800000
     * start_time : 1530028800000
     * end_time : 1530028800000
     * status : {"code":3,"message":"旷工"}
     * out : {"code":0,"message":"正常/正常"}
     * address : 明道大厦
     * remark :
     */

    @SerializedName("date") private long date;
    @SerializedName("start_time") private long startTime;
    @SerializedName("end_time") private long endTime;
    @SerializedName("status") private StatusBean status;
    @SerializedName("out") private StatusBean out;
    @SerializedName("address") private String address;
    @SerializedName("remark") private String remark;

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public StatusBean getOut() {
        return out;
    }

    public void setOut(StatusBean out) {
        this.out = out;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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
