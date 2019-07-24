package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/7/23 8:49
 * @author 作者: xxblwf
 * @description 计划列表详情
 */

public class PlanItemDetailBean implements Parcelable {

    /**
     * WC_ID : 2
     * List_No : 2
     * WC_Name : 装配A2线
     * MPIWC_ID : 589556
     * MwName : 生产日期=2019-07-22,生产线=装配A2线,班次=白,班次序号=1,产品=调角电机 JC-GM-2288323 02,计划数=500
     * HtmlMwName : 生产日期=<font color='#00FF00'>2019-07-22</font>,生产线=<font color='#FF0000'>装配A2线</font>,班次=<font color='#00FF00'>白</font>,班次序号=1,产品=<font color='#0000FF'>调角电机 JC-GM-2288323 02</font>,计划数=<font color='#00FF00'>500</font>
     * MPI_Remark : 7/23
     */

    @SerializedName("WC_ID") private int WCID;
    @SerializedName("List_No") private int ListNo;
    @SerializedName("WC_Name") private String WCName;
    @SerializedName("MPIWC_ID") private int MPIWCID;
    @SerializedName("MwName") private String MwName;
    @SerializedName("HtmlMwName") private String HtmlMwName;
    @SerializedName("MPI_Remark") private String MPIRemark;

    protected PlanItemDetailBean(Parcel in) {
        WCID = in.readInt();
        ListNo = in.readInt();
        WCName = in.readString();
        MPIWCID = in.readInt();
        MwName = in.readString();
        HtmlMwName = in.readString();
        MPIRemark = in.readString();
    }

    public static final Creator<PlanItemDetailBean> CREATOR = new Creator<PlanItemDetailBean>() {
        @Override
        public PlanItemDetailBean createFromParcel(Parcel in) {
            return new PlanItemDetailBean(in);
        }

        @Override
        public PlanItemDetailBean[] newArray(int size) {
            return new PlanItemDetailBean[size];
        }
    };

    public int getWCID() {
        return WCID;
    }

    public void setWCID(int WCID) {
        this.WCID = WCID;
    }

    public int getListNo() {
        return ListNo;
    }

    public void setListNo(int ListNo) {
        this.ListNo = ListNo;
    }

    public String getWCName() {
        return WCName;
    }

    public void setWCName(String WCName) {
        this.WCName = WCName;
    }

    public int getMPIWCID() {
        return MPIWCID;
    }

    public void setMPIWCID(int MPIWCID) {
        this.MPIWCID = MPIWCID;
    }

    public String getMwName() {
        return MwName;
    }

    public void setMwName(String MwName) {
        this.MwName = MwName;
    }

    public String getHtmlMwName() {
        return HtmlMwName;
    }

    public void setHtmlMwName(String HtmlMwName) {
        this.HtmlMwName = HtmlMwName;
    }

    public String getMPIRemark() {
        return MPIRemark;
    }

    public void setMPIRemark(String MPIRemark) {
        this.MPIRemark = MPIRemark;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(WCID);
        dest.writeInt(ListNo);
        dest.writeString(WCName);
        dest.writeInt(MPIWCID);
        dest.writeString(MwName);
        dest.writeString(HtmlMwName);
        dest.writeString(MPIRemark);
    }
}
