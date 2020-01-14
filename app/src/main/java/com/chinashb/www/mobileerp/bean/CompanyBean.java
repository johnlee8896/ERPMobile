package com.chinashb.www.mobileerp.bean;

import com.google.gson.annotations.SerializedName;

/***
 * @date 创建时间 2020/1/13 16:00
 * @author 作者: xxblwf
 * @description 公司的bean
 */

public class CompanyBean {
    @SerializedName("Company_ID") private int companyId;
    @SerializedName("Company_Chinese_Name") private String companyChineseName;
    @SerializedName("Company_English_Name") private String companyEnglishName;

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
}
