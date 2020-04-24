package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/3 12:16
 * @author 作者: xxblwf
 * @description 发货指令
 */

public class DeliveryOrderBean implements Parcelable {
    //// TODO: 2020/1/3 可排查下sql返回的0或1字段int与boolean的关系 ，有些返回0或1的默认竟为boolean

    /**
     * DO_ID : 6123
     * TrackNo : 20191231
     * SpecificTime : false
     * Delivery_Date : 2020-01-03T00:00:00
     * Arrive_Date : 2020-01-06T00:00:00
     * CF_ID:5 //这是增加的
     * CF_Chinese_Name : 重庆长安汽车股份有限公司北京长安汽车公司
     * Des_Info :
     * Special :
     * Done : false
     * Part_Done : false
     */

    @SerializedName("DO_ID") private int DOID;
    @SerializedName("TrackNo") private String TrackNo;
    @SerializedName("SpecificTime") private boolean SpecificTime;
    @SerializedName("Delivery_Date") private String DeliveryDate;
    @SerializedName("Arrive_Date") private String ArriveDate;
    @SerializedName("CF_ID") private int CFID;
    @SerializedName("CF_Chinese_Name") private String CFChineseName;
    @SerializedName("Des_Info") private String DesInfo;
    @SerializedName("Special") private String Special;
    @SerializedName("Done") private boolean Done;
    @SerializedName("Part_Done") private boolean PartDone;

    protected DeliveryOrderBean(Parcel in) {
        DOID = in.readInt();
        TrackNo = in.readString();
        SpecificTime = in.readByte() != 0;
        DeliveryDate = in.readString();
        ArriveDate = in.readString();
        CFID = in.readInt();
        CFChineseName = in.readString();
        DesInfo = in.readString();
        Special = in.readString();
        Done = in.readByte() != 0;
        PartDone = in.readByte() != 0;
    }

    public static final Creator<DeliveryOrderBean> CREATOR = new Creator<DeliveryOrderBean>() {
        @Override
        public DeliveryOrderBean createFromParcel(Parcel in) {
            return new DeliveryOrderBean(in);
        }

        @Override
        public DeliveryOrderBean[] newArray(int size) {
            return new DeliveryOrderBean[size];
        }
    };

    public int getCFID() {
        return CFID;
    }

    public void setCFID(int CFID) {
        this.CFID = CFID;
    }

    public int getDOID() {
        return DOID;
    }

    public void setDOID(int DOID) {
        this.DOID = DOID;
    }

    public String getTrackNo() {
        return TrackNo;
    }

    public void setTrackNo(String TrackNo) {
        this.TrackNo = TrackNo;
    }

    public boolean isSpecificTime() {
        return SpecificTime;
    }

    public void setSpecificTime(boolean SpecificTime) {
        this.SpecificTime = SpecificTime;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String DeliveryDate) {
        this.DeliveryDate = DeliveryDate;
    }

    public String getArriveDate() {
        return ArriveDate;
    }

    public void setArriveDate(String ArriveDate) {
        this.ArriveDate = ArriveDate;
    }

    public String getCFChineseName() {
        return CFChineseName;
    }

    public void setCFChineseName(String CFChineseName) {
        this.CFChineseName = CFChineseName;
    }

    public String getDesInfo() {
        return DesInfo;
    }

    public void setDesInfo(String DesInfo) {
        this.DesInfo = DesInfo;
    }

    public String getSpecial() {
        return Special;
    }

    public void setSpecial(String Special) {
        this.Special = Special;
    }

    public boolean isDone() {
        return Done;
    }

    public void setDone(boolean Done) {
        this.Done = Done;
    }

    public boolean isPartDone() {
        return PartDone;
    }

    public void setPartDone(boolean PartDone) {
        this.PartDone = PartDone;
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(DOID);
        dest.writeString(TrackNo);
        dest.writeByte((byte) (SpecificTime ? 1 : 0));
        dest.writeString(DeliveryDate);
        dest.writeString(ArriveDate);
        dest.writeInt(CFID);
        dest.writeString(CFChineseName);
        dest.writeString(DesInfo);
        dest.writeString(Special);
        dest.writeByte((byte) (Done ? 1 : 0));
        dest.writeByte((byte) (PartDone ? 1 : 0));
    }
}
