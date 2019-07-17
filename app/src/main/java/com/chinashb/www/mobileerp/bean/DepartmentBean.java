package com.chinashb.www.mobileerp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2019/7/3 15:05
 * @author 作者: xxblwf
 * @description 部门选择的bean
 */

public class DepartmentBean implements Parcelable {

    /**
     * Department_ID : 10
     * PDN : null
     * Department_Name : 总经理
     */

    @SerializedName("Department_ID") private int DepartmentID;
    @SerializedName("PDN") private String PDN;
    @SerializedName("Department_Name") private String DepartmentName;

    public DepartmentBean(){

    }

    protected DepartmentBean(Parcel in) {
        DepartmentID = in.readInt();
        PDN = in.readString();
        DepartmentName = in.readString();
    }

    public static final Creator<DepartmentBean> CREATOR = new Creator<DepartmentBean>() {
        @Override
        public DepartmentBean createFromParcel(Parcel in) {
            return new DepartmentBean(in);
        }

        @Override
        public DepartmentBean[] newArray(int size) {
            return new DepartmentBean[size];
        }
    };

    public int getDepartmentID() {
        return DepartmentID;
    }

    public void setDepartmentID(int DepartmentID) {
        this.DepartmentID = DepartmentID;
    }

    public String getPDN() {
        return PDN;
    }

    public void setPDN(Object PDN) {
        this.PDN = (String) PDN;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(DepartmentID);
        dest.writeString(PDN);
        dest.writeString(DepartmentName);
    }
}
