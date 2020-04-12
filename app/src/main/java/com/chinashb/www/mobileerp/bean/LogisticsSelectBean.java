package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/4/8 10:17
 * @author 作者: xxblwf
 * @description 物流选择bean
 */

public class LogisticsSelectBean implements Parcelable {

    /**
     * Delivery_ID : 103027
     * Abroad : false
     * Mass : false
     * CF_ID : 68
     * Delivery_NO : deliveryNo
     * Delivery_Date : 2020-04-07T00:00:00
     * delivery : 汽车
     * LC_ID : 6
     * lc_name : 近铁空运
     * AWarehouse : false
     * AW_ID : 8
     * Ac_Title_ID : 0
     * Arrive_Date : 2020-04-08T00:00:00
     * Shiping_Date : 2020-04-08T00:00:00
     * Company_ID : 8
     * Bu_ID : 54
     * DT_ID : 2
     * Contract_No : contractNo
     * Replenish : false
     * TrackNo : C-200407-06-SHB-YQ20200407001
     * LoadTime : 2020-04-07T00:00:00
     * DriverName : 帮不帮爸爸
     * DriverTel : 1111
     * Remark :
     * Des_Address : 2020-03-30T06:34:58.588Z
     * License_Plate_NO : vv122
     * IsBackUp : false
     * IsReturn : false
     * Truck_Left : true
     * cf_chinese_name : 一汽奔腾轿车有限公司
     */

    @SerializedName("Delivery_ID") private int DeliveryID;
    @SerializedName("Abroad") private boolean Abroad;
    @SerializedName("Mass") private boolean Mass;
    @SerializedName("CF_ID") private int CFID;
    @SerializedName("Delivery_NO") private String DeliveryNO;
    @SerializedName("Delivery_Date") private String DeliveryDate;
    @SerializedName("delivery") private String delivery;
    @SerializedName("LC_ID") private int LCID;
    @SerializedName("lc_name") private String lcName;
    @SerializedName("AWarehouse") private boolean AWarehouse;
    @SerializedName("AW_ID") private int AWID;
    @SerializedName("Ac_Title_ID") private int AcTitleID;
    @SerializedName("Arrive_Date") private String ArriveDate;
    @SerializedName("Shiping_Date") private String ShipingDate;
    @SerializedName("Company_ID") private int CompanyID;
    @SerializedName("Bu_ID") private int BuID;
    @SerializedName("DT_ID") private int DTID;
    @SerializedName("Contract_No") private String ContractNo;
    @SerializedName("Replenish") private boolean Replenish;
    @SerializedName("TrackNo") private String TrackNo;
    @SerializedName("LoadTime") private String LoadTime;
    @SerializedName("DriverName") private String DriverName;
    @SerializedName("DriverTel") private String DriverTel;
    @SerializedName("Remark") private String Remark;
    @SerializedName("Des_Address") private String DesAddress;
    @SerializedName("License_Plate_NO") private String LicensePlateNO;
    @SerializedName("IsBackUp") private boolean IsBackUp;
    @SerializedName("IsReturn") private boolean IsReturn;
    @SerializedName("Truck_Left") private boolean TruckLeft;
    @SerializedName("cf_chinese_name") private String cfChineseName;

    protected LogisticsSelectBean(Parcel in) {
        DeliveryID = in.readInt();
        Abroad = in.readByte() != 0;
        Mass = in.readByte() != 0;
        CFID = in.readInt();
        DeliveryNO = in.readString();
        DeliveryDate = in.readString();
        delivery = in.readString();
        LCID = in.readInt();
        lcName = in.readString();
        AWarehouse = in.readByte() != 0;
        AWID = in.readInt();
        AcTitleID = in.readInt();
        ArriveDate = in.readString();
        ShipingDate = in.readString();
        CompanyID = in.readInt();
        BuID = in.readInt();
        DTID = in.readInt();
        ContractNo = in.readString();
        Replenish = in.readByte() != 0;
        TrackNo = in.readString();
        LoadTime = in.readString();
        DriverName = in.readString();
        DriverTel = in.readString();
        Remark = in.readString();
        DesAddress = in.readString();
        LicensePlateNO = in.readString();
        IsBackUp = in.readByte() != 0;
        IsReturn = in.readByte() != 0;
        TruckLeft = in.readByte() != 0;
        cfChineseName = in.readString();
    }

    public static final Creator<LogisticsSelectBean> CREATOR = new Creator<LogisticsSelectBean>() {
        @Override
        public LogisticsSelectBean createFromParcel(Parcel in) {
            return new LogisticsSelectBean(in);
        }

        @Override
        public LogisticsSelectBean[] newArray(int size) {
            return new LogisticsSelectBean[size];
        }
    };

    public int getDeliveryID() {
        return DeliveryID;
    }

    public void setDeliveryID(int DeliveryID) {
        this.DeliveryID = DeliveryID;
    }

    public boolean isAbroad() {
        return Abroad;
    }

    public void setAbroad(boolean Abroad) {
        this.Abroad = Abroad;
    }

    public boolean isMass() {
        return Mass;
    }

    public void setMass(boolean Mass) {
        this.Mass = Mass;
    }

    public int getCFID() {
        return CFID;
    }

    public void setCFID(int CFID) {
        this.CFID = CFID;
    }

    public String getDeliveryNO() {
        return DeliveryNO;
    }

    public void setDeliveryNO(String DeliveryNO) {
        this.DeliveryNO = DeliveryNO;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String DeliveryDate) {
        this.DeliveryDate = DeliveryDate;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public int getLCID() {
        return LCID;
    }

    public void setLCID(int LCID) {
        this.LCID = LCID;
    }

    public String getLcName() {
        return lcName;
    }

    public void setLcName(String lcName) {
        this.lcName = lcName;
    }

    public boolean isAWarehouse() {
        return AWarehouse;
    }

    public void setAWarehouse(boolean AWarehouse) {
        this.AWarehouse = AWarehouse;
    }

    public int getAWID() {
        return AWID;
    }

    public void setAWID(int AWID) {
        this.AWID = AWID;
    }

    public int getAcTitleID() {
        return AcTitleID;
    }

    public void setAcTitleID(int AcTitleID) {
        this.AcTitleID = AcTitleID;
    }

    public String getArriveDate() {
        return ArriveDate;
    }

    public void setArriveDate(String ArriveDate) {
        this.ArriveDate = ArriveDate;
    }

    public String getShipingDate() {
        return ShipingDate;
    }

    public void setShipingDate(String ShipingDate) {
        this.ShipingDate = ShipingDate;
    }

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int CompanyID) {
        this.CompanyID = CompanyID;
    }

    public int getBuID() {
        return BuID;
    }

    public void setBuID(int BuID) {
        this.BuID = BuID;
    }

    public int getDTID() {
        return DTID;
    }

    public void setDTID(int DTID) {
        this.DTID = DTID;
    }

    public String getContractNo() {
        return ContractNo;
    }

    public void setContractNo(String ContractNo) {
        this.ContractNo = ContractNo;
    }

    public boolean isReplenish() {
        return Replenish;
    }

    public void setReplenish(boolean Replenish) {
        this.Replenish = Replenish;
    }

    public String getTrackNo() {
        return TrackNo;
    }

    public void setTrackNo(String TrackNo) {
        this.TrackNo = TrackNo;
    }

    public String getLoadTime() {
        return LoadTime;
    }

    public void setLoadTime(String LoadTime) {
        this.LoadTime = LoadTime;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String DriverName) {
        this.DriverName = DriverName;
    }

    public String getDriverTel() {
        return DriverTel;
    }

    public void setDriverTel(String DriverTel) {
        this.DriverTel = DriverTel;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getDesAddress() {
        return DesAddress;
    }

    public void setDesAddress(String DesAddress) {
        this.DesAddress = DesAddress;
    }

    public String getLicensePlateNO() {
        return LicensePlateNO;
    }

    public void setLicensePlateNO(String LicensePlateNO) {
        this.LicensePlateNO = LicensePlateNO;
    }

    public boolean isIsBackUp() {
        return IsBackUp;
    }

    public void setIsBackUp(boolean IsBackUp) {
        this.IsBackUp = IsBackUp;
    }

    public boolean isIsReturn() {
        return IsReturn;
    }

    public void setIsReturn(boolean IsReturn) {
        this.IsReturn = IsReturn;
    }

    public boolean isTruckLeft() {
        return TruckLeft;
    }

    public void setTruckLeft(boolean TruckLeft) {
        this.TruckLeft = TruckLeft;
    }

    public String getCfChineseName() {
        return cfChineseName;
    }

    public void setCfChineseName(String cfChineseName) {
        this.cfChineseName = cfChineseName;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(DeliveryID);
        dest.writeByte((byte) (Abroad ? 1 : 0));
        dest.writeByte((byte) (Mass ? 1 : 0));
        dest.writeInt(CFID);
        dest.writeString(DeliveryNO);
        dest.writeString(DeliveryDate);
        dest.writeString(delivery);
        dest.writeInt(LCID);
        dest.writeString(lcName);
        dest.writeByte((byte) (AWarehouse ? 1 : 0));
        dest.writeInt(AWID);
        dest.writeInt(AcTitleID);
        dest.writeString(ArriveDate);
        dest.writeString(ShipingDate);
        dest.writeInt(CompanyID);
        dest.writeInt(BuID);
        dest.writeInt(DTID);
        dest.writeString(ContractNo);
        dest.writeByte((byte) (Replenish ? 1 : 0));
        dest.writeString(TrackNo);
        dest.writeString(LoadTime);
        dest.writeString(DriverName);
        dest.writeString(DriverTel);
        dest.writeString(Remark);
        dest.writeString(DesAddress);
        dest.writeString(LicensePlateNO);
        dest.writeByte((byte) (IsBackUp ? 1 : 0));
        dest.writeByte((byte) (IsReturn ? 1 : 0));
        dest.writeByte((byte) (TruckLeft ? 1 : 0));
        dest.writeString(cfChineseName);
    }
}
