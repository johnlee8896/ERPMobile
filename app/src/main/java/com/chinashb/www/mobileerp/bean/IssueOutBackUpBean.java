package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 1/20/26 10:19 AM
 * @author 作者: liweifeng
 * @description 投料备料bean
 */
public class IssueOutBackUpBean implements Parcelable {

    /**
     * Item_id : 33181
     * IV_ID : 434107
     * Item_Version : A4
     * LotNo : 20260114-4
     * Ist_Name : E9-2-1
     * ScanX : VG/2906730
     * Qty : 2160.0
     * Bu_ID : 1
     * HR_Name : 李伟锋
     * Remark : null
     */

    @SerializedName("Item_id") private long ItemId;
    @SerializedName("item_Name") private String temName;
    @SerializedName("Item_Version") private String ItemVersion;
    @SerializedName("LotNo") private String LotNo;
    @SerializedName("Ist_Name") private String IstName;
    @SerializedName("ScanX") private String ScanX;
    @SerializedName("Qty") private double Qty;
    @SerializedName("Bu_ID") private int BuID;
    @SerializedName("HR_Name") private String HRName;
    @SerializedName("Remark") private String Remark;
    @SerializedName("IV_ID") private long IV_ID;
    @SerializedName("LotID") private long LotID;
    @SerializedName("Ist_ID") private long Ist_ID;
    @SerializedName("Sub_Ist_ID") private long Sub_Ist_ID;
    @SerializedName("SMLI_ID") private long SMLI_ID;
    @SerializedName("SMT_ID") private long SMT_ID;

    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public IssueOutBackUpBean setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    protected IssueOutBackUpBean(Parcel in) {
        ItemId = in.readLong();
        temName = in.readString();
        ItemVersion = in.readString();
        LotNo = in.readString();
        IstName = in.readString();
        ScanX = in.readString();
        Qty = in.readDouble();
        BuID = in.readInt();
        HRName = in.readString();
        Remark = in.readString();
        IV_ID = in.readLong();
        LotID = in.readLong();
        Ist_ID = in.readLong();
        Sub_Ist_ID = in.readLong();
        SMLI_ID = in.readLong();
        SMT_ID = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(ItemId);
        dest.writeString(temName);
        dest.writeString(ItemVersion);
        dest.writeString(LotNo);
        dest.writeString(IstName);
        dest.writeString(ScanX);
        dest.writeDouble(Qty);
        dest.writeInt(BuID);
        dest.writeString(HRName);
        dest.writeString(Remark);
        dest.writeLong(IV_ID);
        dest.writeLong(LotID);
        dest.writeLong(Ist_ID);
        dest.writeLong(Sub_Ist_ID);
        dest.writeLong(SMLI_ID);
        dest.writeLong(SMT_ID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IssueOutBackUpBean> CREATOR = new Creator<IssueOutBackUpBean>() {
        @Override
        public IssueOutBackUpBean createFromParcel(Parcel in) {
            return new IssueOutBackUpBean(in);
        }

        @Override
        public IssueOutBackUpBean[] newArray(int size) {
            return new IssueOutBackUpBean[size];
        }
    };

    public long getItemId() {
        return ItemId;
    }

    public IssueOutBackUpBean setItemId(long itemId) {
        ItemId = itemId;
        return this;
    }

    public String getTemName() {
        return temName;
    }

    public IssueOutBackUpBean setTemName(String temName) {
        this.temName = temName;
        return this;
    }

    public String getItemVersion() {
        return ItemVersion;
    }

    public IssueOutBackUpBean setItemVersion(String itemVersion) {
        ItemVersion = itemVersion;
        return this;
    }

    public String getLotNo() {
        return LotNo;
    }

    public IssueOutBackUpBean setLotNo(String lotNo) {
        LotNo = lotNo;
        return this;
    }

    public String getIstName() {
        return IstName;
    }

    public IssueOutBackUpBean setIstName(String istName) {
        IstName = istName;
        return this;
    }

    public String getScanX() {
        return ScanX;
    }

    public IssueOutBackUpBean setScanX(String scanX) {
        ScanX = scanX;
        return this;
    }

    public double getQty() {
        return Qty;
    }

    public IssueOutBackUpBean setQty(double qty) {
        Qty = qty;
        return this;
    }

    public int getBuID() {
        return BuID;
    }

    public IssueOutBackUpBean setBuID(int buID) {
        BuID = buID;
        return this;
    }

    public String getHRName() {
        return HRName;
    }

    public IssueOutBackUpBean setHRName(String HRName) {
        this.HRName = HRName;
        return this;
    }

    public String getRemark() {
        return Remark;
    }

    public IssueOutBackUpBean setRemark(String remark) {
        Remark = remark;
        return this;
    }

    public long getIV_ID() {
        return IV_ID;
    }

    public IssueOutBackUpBean setIV_ID(long IV_ID) {
        this.IV_ID = IV_ID;
        return this;
    }

    public long getLotID() {
        return LotID;
    }

    public IssueOutBackUpBean setLotID(long lotID) {
        LotID = lotID;
        return this;
    }

    public long getIst_ID() {
        return Ist_ID;
    }

    public IssueOutBackUpBean setIst_ID(long ist_ID) {
        Ist_ID = ist_ID;
        return this;
    }

    public long getSub_Ist_ID() {
        return Sub_Ist_ID;
    }

    public IssueOutBackUpBean setSub_Ist_ID(long sub_Ist_ID) {
        Sub_Ist_ID = sub_Ist_ID;
        return this;
    }

    public long getSMLI_ID() {
        return SMLI_ID;
    }

    public IssueOutBackUpBean setSMLI_ID(long SMLI_ID) {
        this.SMLI_ID = SMLI_ID;
        return this;
    }

    public long getSMT_ID() {
        return SMT_ID;
    }

    public IssueOutBackUpBean setSMT_ID(long SMT_ID) {
        this.SMT_ID = SMT_ID;
        return this;
    }
}
