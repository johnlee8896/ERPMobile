package com.chinashb.www.mobileerp.bean.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.chinashb.www.mobileerp.bean.BuBean;
import com.chinashb.www.mobileerp.bean.CompanyBean;
import com.chinashb.www.mobileerp.bean.DeliveryTypeBean;
import com.chinashb.www.mobileerp.bean.LogisticsCompanyBean;
import com.chinashb.www.mobileerp.bean.ReceiverCompanyBean;

import java.util.Date;

/***
 * @date 创建时间 2020/1/17 14:48
 * @author 作者: xxblwf
 * @description 物流发货entity
 */

public class LogisticsDeliveryEntity implements Parcelable {
    private String trackNO;
    private Date outDate;
    private Date loadCarTime;
    private CompanyBean sendCompanyBean;
    private BuBean buBean;
    private String sendRemark;
    private ReceiverCompanyBean receiverCompanyBean;
    private String receiverAddress;
    private String receiverRemarkable;
    private int domesticType;
    private int sampleType;
    private int senderGoodsType;
    private DeliveryTypeBean deliveryTypeBean;
    private LogisticsCompanyBean logisticsCompanyBean;
    private String logisticsTrackNO;
    private Date arriveDate;
    private int dayNumber;
    private String driver;
    private String telephone;
    private String carPlateNumber;
    private String logisticsRemark;

    public LogisticsDeliveryEntity(){

    }

    protected LogisticsDeliveryEntity(Parcel in) {
        trackNO = in.readString();
        sendCompanyBean = in.readParcelable(CompanyBean.class.getClassLoader());
        buBean = in.readParcelable(BuBean.class.getClassLoader());
        sendRemark = in.readString();
        receiverCompanyBean = in.readParcelable(ReceiverCompanyBean.class.getClassLoader());
        receiverAddress = in.readString();
        receiverRemarkable = in.readString();
        domesticType = in.readInt();
        sampleType = in.readInt();
        senderGoodsType = in.readInt();
        deliveryTypeBean = in.readParcelable(DeliveryTypeBean.class.getClassLoader());
        logisticsCompanyBean = in.readParcelable(LogisticsCompanyBean.class.getClassLoader());
        logisticsTrackNO = in.readString();
        dayNumber = in.readInt();
        driver = in.readString();
        telephone = in.readString();
        carPlateNumber = in.readString();
        logisticsRemark = in.readString();
    }

    public static final Creator<LogisticsDeliveryEntity> CREATOR = new Creator<LogisticsDeliveryEntity>() {
        @Override
        public LogisticsDeliveryEntity createFromParcel(Parcel in) {
            return new LogisticsDeliveryEntity(in);
        }

        @Override
        public LogisticsDeliveryEntity[] newArray(int size) {
            return new LogisticsDeliveryEntity[size];
        }
    };

    public String getTrackNO() {
        return trackNO;
    }

    public void setTrackNO(String trackNO) {
        this.trackNO = trackNO;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public Date getLoadCarTime() {
        return loadCarTime;
    }

    public void setLoadCarTime(Date loadCarTime) {
        this.loadCarTime = loadCarTime;
    }

    public CompanyBean getSendCompanyBean() {
        return sendCompanyBean;
    }

    public void setSendCompanyBean(CompanyBean sendCompanyBean) {
        this.sendCompanyBean = sendCompanyBean;
    }

    public BuBean getBuBean() {
        return buBean;
    }

    public void setBuBean(BuBean buBean) {
        this.buBean = buBean;
    }

    public String getSendRemark() {
        return sendRemark;
    }

    public void setSendRemark(String sendRemark) {
        this.sendRemark = sendRemark;
    }

    public ReceiverCompanyBean getReceiverCompanyBean() {
        return receiverCompanyBean;
    }

    public void setReceiverCompanyBean(ReceiverCompanyBean receiverCompanyBean) {
        this.receiverCompanyBean = receiverCompanyBean;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverRemarkable() {
        return receiverRemarkable;
    }

    public void setReceiverRemarkable(String receiverRemarkable) {
        this.receiverRemarkable = receiverRemarkable;
    }

    public int getDomesticType() {
        return domesticType;
    }

    public void setDomesticType(int domesticType) {
        this.domesticType = domesticType;
    }

    public int getSampleType() {
        return sampleType;
    }

    public void setSampleType(int sampleType) {
        this.sampleType = sampleType;
    }

    public int getSenderGoodsType() {
        return senderGoodsType;
    }

    public void setSenderGoodsType(int senderGoodsType) {
        this.senderGoodsType = senderGoodsType;
    }

    public DeliveryTypeBean getDeliveryTypeBean() {
        return deliveryTypeBean;
    }

    public void setDeliveryTypeBean(DeliveryTypeBean deliveryTypeBean) {
        this.deliveryTypeBean = deliveryTypeBean;
    }

    public LogisticsCompanyBean getLogisticsCompanyBean() {
        return logisticsCompanyBean;
    }

    public void setLogisticsCompanyBean(LogisticsCompanyBean logisticsCompanyBean) {
        this.logisticsCompanyBean = logisticsCompanyBean;
    }

    public String getLogisticsTrackNO() {
        return logisticsTrackNO;
    }

    public void setLogisticsTrackNO(String logisticsTrackNO) {
        this.logisticsTrackNO = logisticsTrackNO;
    }

    public Date getArriveDate() {
        return arriveDate;
    }

    public void setArriveDate(Date arriveDate) {
        this.arriveDate = arriveDate;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCarPlateNumber() {
        return carPlateNumber;
    }

    public void setCarPlateNumber(String carPlateNumber) {
        this.carPlateNumber = carPlateNumber;
    }

    public String getLogisticsRemark() {
        return logisticsRemark;
    }

    public void setLogisticsRemark(String logisticsRemark) {
        this.logisticsRemark = logisticsRemark;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackNO);
        dest.writeParcelable(sendCompanyBean, flags);
        dest.writeParcelable(buBean, flags);
        dest.writeString(sendRemark);
        dest.writeParcelable(receiverCompanyBean, flags);
        dest.writeString(receiverAddress);
        dest.writeString(receiverRemarkable);
        dest.writeInt(domesticType);
        dest.writeInt(sampleType);
        dest.writeInt(senderGoodsType);
        dest.writeParcelable(deliveryTypeBean, flags);
        dest.writeParcelable(logisticsCompanyBean, flags);
        dest.writeString(logisticsTrackNO);
        dest.writeInt(dayNumber);
        dest.writeString(driver);
        dest.writeString(telephone);
        dest.writeString(carPlateNumber);
        dest.writeString(logisticsRemark);
    }
}
