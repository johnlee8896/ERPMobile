package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/13 16:00
 * @author 作者: xxblwf
 * @description 公司的bean
 */

public class CompanyBean implements Parcelable {
    @SerializedName("Company_ID") private int companyId;
    @SerializedName("Company_Chinese_Name") private String companyChineseName;
    @SerializedName("Company_English_Name") private String companyEnglishName;

    protected CompanyBean(Parcel in) {
        companyId = in.readInt();
        companyChineseName = in.readString();
        companyEnglishName = in.readString();
    }

    public static final Creator<CompanyBean> CREATOR = new Creator<CompanyBean>() {
        @Override
        public CompanyBean createFromParcel(Parcel in) {
            return new CompanyBean(in);
        }

        @Override
        public CompanyBean[] newArray(int size) {
            return new CompanyBean[size];
        }
    };

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyChineseName() {
        return companyChineseName;
    }

    public void setCompanyChineseName(String companyChineseName) {
        this.companyChineseName = companyChineseName;
    }

    public String getCompanyEnglishName() {
        return companyEnglishName;
    }

    public void setCompanyEnglishName(String companyEnglishName) {
        this.companyEnglishName = companyEnglishName;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(companyId);
        dest.writeString(companyChineseName);
        dest.writeString(companyEnglishName);
    }
}
