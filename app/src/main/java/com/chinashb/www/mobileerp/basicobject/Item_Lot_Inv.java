package com.chinashb.www.mobileerp.basicobject;

public class Item_Lot_Inv {

    /**
     * IV_ID : 25322
     * ManuLotNo : 20180413-01
     * LotStatus_ID : 101
     * Item_Version : B3
     * LotID : 2377825
     * LotNo : 180413-01
     * InvQty : 3200.0
     * ManuDate : null
     * Sub_Ist_ID : 0
     * LotDate : 2018-04-13T00:00:00
     * IST_ID : 1298
     * LotStatus : 正常使用
     * IstName : 9#A.
     */
    private int IV_ID;
    private String ManuLotNo;
    private int LotStatus_ID;
    private String Item_Version;
    private int LotID;
    private String LotNo;
    private double InvQty;
    private String ManuDate;
    private int Sub_Ist_ID;
    private String LotDate;
    private int IST_ID;
    private String LotStatus;
    private String IstName;
    
    private String LotDescription;

    public void setIV_ID(int IV_ID) {
        this.IV_ID = IV_ID;
    }

    public void setManuLotNo(String ManuLotNo) {
        this.ManuLotNo = ManuLotNo;
    }

    public void setLotStatus_ID(int LotStatus_ID) {
        this.LotStatus_ID = LotStatus_ID;
    }

    public void setItem_Version(String Item_Version) {
        this.Item_Version = Item_Version;
    }

    public void setLotID(int LotID) {
        this.LotID = LotID;
    }

    public void setLotNo(String LotNo) {
        this.LotNo = LotNo;
    }

    public void setInvQty(double InvQty) {
        this.InvQty = InvQty;
    }

    public void setManuDate(String ManuDate) {
        this.ManuDate = ManuDate;
    }

    public void setSub_Ist_ID(int Sub_Ist_ID) {
        this.Sub_Ist_ID = Sub_Ist_ID;
    }

    public void setLotDate(String LotDate) {
        this.LotDate = LotDate;
    }

    public void setIST_ID(int IST_ID) {
        this.IST_ID = IST_ID;
    }

    public void setLotStatus(String LotStatus) {
        this.LotStatus = LotStatus;
    }

    public void setIstName(String IstName) {
        this.IstName = IstName;
    }

    public int getIV_ID() {
        return IV_ID;
    }

    public String getManuLotNo() {
        return ManuLotNo;
    }

    public int getLotStatus_ID() {
        return LotStatus_ID;
    }

    public String getItem_Version() {
        return Item_Version;
    }

    public int getLotID() {
        return LotID;
    }

    public String getLotNo() {
        return LotNo;
    }

    public double getInvQty() {
        return InvQty;
    }

    public String getManuDate() {
        return ManuDate;
    }

    public int getSub_Ist_ID() {
        return Sub_Ist_ID;
    }

    public String getLotDate() {
        return LotDate;
    }

    public int getIST_ID() {
        return IST_ID;
    }

    public String getLotStatus() {
        return LotStatus;
    }

    public String getIstName() {
        return IstName;
    }

    public String getLotDescription() {
        if(LotDescription==null){return "";}
        return LotDescription;
    }
    public void setLotDescription(String LotDescription) {
        this.LotDescription = LotDescription;
    }

}
