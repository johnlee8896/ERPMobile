package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/***
 * @date 创建时间 2019/7/3 15:05
 * @author 作者: xxblwf
 * @description 部门选择的bean
 */

public class DepartmentBean implements Serializable {

    /**
     * Department_ID : 10
     * PDN : null
     * Department_Name : 总经理
     */

    @SerializedName("Department_ID") private int DepartmentID;
    @SerializedName("PDN") private String PDN;
    @SerializedName("Department_Name") private String DepartmentName;

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
}
