package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/6/26 13:53
 * @author 作者: xxblwf
 * @description 车间的bean
 */

public class BUItemBean implements Parcelable {

    /**
     * Company_ID : 1
     * Company_Chinese_Name : 上海胜华波汽车电器有限公司
     * Brief : 有限公司
     * bu_id : 1
     * bu_name : 座椅电机
     */

    @SerializedName("Company_ID")
    private int CompanyID;
    @SerializedName("Company_Chinese_Name")
    private String CompanyChineseName;
    @SerializedName("Brief")
    private String Brief;
    @SerializedName("bu_id")
    private int BUId;
    @SerializedName("bu_name")
    private String BUName;

    protected BUItemBean(Parcel in) {
        CompanyID = in.readInt();
        CompanyChineseName = in.readString();
        Brief = in.readString();
        BUId = in.readInt();
        BUName = in.readString();
    }

    public static final Creator<BUItemBean> CREATOR = new Creator<BUItemBean>() {
        @Override
        public BUItemBean createFromParcel(Parcel in) {
            return new BUItemBean(in);
        }

        @Override
        public BUItemBean[] newArray(int size) {
            return new BUItemBean[size];
        }
    };

    public int getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(int CompanyID) {
        this.CompanyID = CompanyID;
    }

    public String getCompanyChineseName() {
        return CompanyChineseName;
    }

    public void setCompanyChineseName(String CompanyChineseName) {
        this.CompanyChineseName = CompanyChineseName;
    }

    public String getBrief() {
        return Brief;
    }

    public void setBrief(String Brief) {
        this.Brief = Brief;
    }

    public int getBUId() {
        return BUId;
    }

    public void setBUId(int BUId) {
        this.BUId = BUId;
    }

    public String getBUName() {
        return BUName;
    }

    public void setBUName(String BUName) {
        this.BUName = BUName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(CompanyID);
        dest.writeString(CompanyChineseName);
        dest.writeString(Brief);
        dest.writeInt(BUId);
        dest.writeString(BUName);
    }
}
